import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RBCScraper {

    private static String RBC_CSV_FILE_PATH = "/Users/prakashraghu/scraping/rbc.csv";

    public static JSONArray scrapSite() {
        JSONArray resultJsonArr = new JSONArray();
        try {
            String chromeDriverPath = "/Users/prakashraghu/chromedriver";
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);

            // Initializing the Chrome web driver
            WebDriver driver = new ChromeDriver();

            // Initializing the wait method to wait for any loading of DOM for a duration of 10 seconds
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Opening the website of a bank
            driver.get("https://www.rbcroyalbank.com/credit-cards/index.html");
            System.out.println("Crawling: https://www.rbcroyalbank.com/credit-cards");
            // Maximizing the Chrome window
            driver.manage().window().minimize();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("onetrust-accept-btn-handler")));
            // Closing the cookie consent popup by clicking the accept button
            WebElement cookiePopup = driver.findElement(By.id("onetrust-accept-btn-handler"));
            cookiePopup.click();

            // Getting all the category DOM elements into a list
            List<WebElement> categoryElements = driver.findElements(By.xpath("//a[@class=\"standalone-link pad-l-0\"]"));
            ArrayList<String> categories = new ArrayList<>();

            /**
             * Using the extracted category web elements, each webpage of the category of credit card is then opened
             * and extracted information such as name of the card, image and annual fee and store it into a hashmap
             * example: Category -> [CreditCardModel]
             */
            HashMap<String, ArrayList<CreditCardModel>> creditCardInformation = new HashMap<>();
            int i = 0;
            for (WebElement element : categoryElements) {
                List<WebElement> categoryAnchors = driver.findElements(By.xpath("//a[@class=\"standalone-link pad-l-0\"]"));
                String str = categoryAnchors.get(i).getText();
                String link = categoryAnchors.get(i).getAttribute("href");
                System.out.println("Crawling: "+link);
                if (!str.isEmpty()) {
                    // Here for a page unstructured reason the category of business is avoided
                    if (!str.equals("Business")) {
                        categories.add(str);
                        Thread.sleep(3000);
                        categoryAnchors.get(i).click();
                        // Here the driver waits for the navigated webpage to be loaded
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
                        List<WebElement> cards = driver.findElements(By.xpath("//div[@class=\"grid-wpr w-full sub-section mar-b\"]"));
                        ArrayList<CreditCardModel> tempCardTitles = new ArrayList<>();
                        int cardIndex = 0;
                        for (WebElement card : cards) {
                            // getting all the descriptions for each credit card
                            ArrayList<String> descriptives = new ArrayList<>();
                            List<WebElement> descriptionList = card.findElements(By.xpath("//ul[@class=\"check-list\"]"));
                            List<WebElement> desriptionListItems = descriptionList.get(cardIndex).findElements(By.tagName("li"));
                            for (WebElement descriptionItem: desriptionListItems) {
                                descriptives.add(descriptionItem.getText());
                            }
                            cardIndex++;
                            // getting other necessary information
                            tempCardTitles.add(new CreditCardModel(str ,card.findElement(By.tagName("h3")).getText(),
                                    card.findElement(By.tagName("img")).getAttribute("src"),
                                    card.findElement(By.xpath("//div[@class=\"grid-one-third\"]//ul//li//span[contains(@class, " +
                                            "'text-script') or contains(@class, 'text-script ng-binding')]")).getText(), card.findElement(By.xpath("//div[@class=\"grid-one-third\"]//ul//li[2]//span[contains(@class, " +
                                    "'text-script') or contains(@class, 'text-script ng-binding')]")).getText(), descriptives, link));
                        }
                        // Here Category information and its corresponding array of CreditCardModel details are inserted into the hashmap
                        creditCardInformation.put(str, tempCardTitles);
                    }

                    if (!str.equals("Business")) {
                        driver.navigate().back();
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
                    }
                }
                i++;
            }

            // Each credit card details from the  hash map are been written to csv with the category as grouped by
            for (Map.Entry<String, ArrayList<CreditCardModel>> entry : creditCardInformation.entrySet()) {
                String key = entry.getKey();
                ArrayList<CreditCardModel> values = entry.getValue();

                for (CreditCardModel creditCard : values) {
                    String[] creditCardContent = new String[5];

                    JSONObject creditJsonObj = new JSONObject();
                    creditJsonObj.put("category", key);
                    creditJsonObj.put("title", creditCard.getTitle());
                    creditJsonObj.put("image_url", creditCard.getImage());
                    creditJsonObj.put("annual_fee", creditCard.getAnnualFee());
                    creditJsonObj.put("purchase_fee", creditCard.getPurchaseFee());
                    creditJsonObj.put("link", creditCard.getLink());

                    resultJsonArr.put(creditJsonObj);
                }
            }

            /**
             * Here the OpenCSV library is used in order to create and write all the information
             * available in a formatted way and save it.
             * All the data that has been written to the file, as by using a string array.
             */

            List<String[]> csvData = new ArrayList<>();

            // The spacer string array which is empty is created and used wherever there is a space needed
            String[] spacer = {};

//            int maxLengthOfDescriptives = getMaxLengthOfDescription(creditCardInformation);
            String[] header = new String[6];
            header[0] = "Category";
            header[1] = "Name";
            header[2] = "ImageUrl";
            header[3] = "AnnualFee";
            header[4] = "PurchaseRate";
            header[5] = "Link";
            csvData.add(header);

            // Each credit card details from the  hash map are been written to csv with the category as grouped by
            for (Map.Entry<String, ArrayList<CreditCardModel>> entry : creditCardInformation.entrySet()) {
                String key = entry.getKey();
                ArrayList<CreditCardModel> values = entry.getValue();

                for (CreditCardModel creditCard : values) {
                    String[] creditCardContent = new String[6];
                    creditCardContent[0] = key;
                    creditCardContent[1] = creditCard.getTitle();
                    creditCardContent[2] = creditCard.getImage();
                    creditCardContent[3] = creditCard.getAnnualFee();
                    creditCardContent[4] = creditCard.getPurchaseFee();
                    creditCardContent[5] = creditCard.getLink();

                    csvData.add(creditCardContent);
                }

            }

            // write all the generated or stored data into the csv
            writeToCSVFile(csvData, RBC_CSV_FILE_PATH);

            // Finally the web driver is quited
            driver.quit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return resultJsonArr;
    }

    /**
     * Used to get the maximum length of the descriptive array from the existing hash map
     * to set the headers of the csv to hold the maximum information
     * @param hashMap
     */
    public static int getMaxLengthOfDescription(HashMap<String, ArrayList<CreditCardModel>> hashMap) {
        int maxLength = 0;
        for (Map.Entry<String, ArrayList<CreditCardModel>> entry : hashMap.entrySet()) {
            ArrayList<CreditCardModel> values = entry.getValue();
            for (CreditCardModel creditCardModel: values) {
                maxLength = Math.max(maxLength, creditCardModel.getDescription().size());
            }
        }
        return maxLength;
    }

    /**
     * CSVWriter is used to write all the string array list to a file in the system
     * @param csvData list of string array that had to be written to the csv
     * @param fileLocation string location to the file path that the csv should exist
     */
    public static void writeToCSVFile(List<String[]> csvData, String fileLocation) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileLocation))) {
            writer.writeAll(csvData);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

/**
 * CreditCardModel is a wrapper class which is used to store a credit card information
 * in a structured format and use it in any list or data structure.
 * The necessary getters and setters are used if needed.
 */
class CreditCardModel {
    private String category;
    private String title;
    private String image;
    private String annualFee;
    private String purchaseFee;
    private ArrayList<String> description;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    private String link;

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    // constructor
    public CreditCardModel(String category, String title, String image, String annualFee, String purchaseFee, ArrayList<String> descriptions, String link) {
        this.category = category;
        this.title = title;
        this.image = image;
        this.annualFee = annualFee;
        this.purchaseFee = purchaseFee;
        this.description = descriptions;
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAnnualFee(String fee) {
        this.annualFee = fee;
    }

    public String getTitle() {
        return this.title;
    }

    public String getImage() {
        return this.image;
    }

    public String getAnnualFee() {
        return this.annualFee;
    }

    public double getAnnualFeeInDouble() {
        return Double.valueOf(this.annualFee);
    }

    public double getPurchaseRateInDouble() {
        return Double.valueOf(this.purchaseFee);
    }

    public String getPurchaseFee() {
        return purchaseFee;
    }

    public void setPurchaseFee(String purchaseFee) {
        this.purchaseFee = purchaseFee;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}