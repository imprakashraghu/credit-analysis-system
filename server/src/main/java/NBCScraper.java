import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBCScraper {

    private static String NBC_FILE_PATH = "/Users/prakashraghu/scraping/nbc.csv";

    public static JSONArray scrapSite() {
        JSONArray result = new JSONArray();
        try {
            String chromeDriverPath = "/Users/prakashraghu/chromedriver";
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);

            // Initializing the Chrome web driver
            WebDriver driver = new ChromeDriver();

            // Initializing the wait method to wait for any loading of DOM for a duration of 10 seconds
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Opening the website of a bank
            driver.get("https://www.nbc.ca/personal/mastercard-credit-cards-alt.html");
            // Maximizing the Chrome window
            driver.manage().window().minimize();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("didomi-notice-agree-button")));
            // Closing the cookie consent popup by clicking the accept button
            WebElement cookiePopup = driver.findElement(By.id("didomi-notice-agree-button"));
            cookiePopup.click();

            List<WebElement> categoryElements = driver.findElements(By.xpath("//div[@class=\"textandimage parbase aem-GridColumn--bootstrapsm--4 aem-GridColumn--bootstrapxs--none aem-GridColumn--offset--bootstrapxs--0 aem-GridColumn aem-GridColumn--bootstrapxs--6 aem-GridColumn--default--2\"]//div//div//div//p//b//a"));
            String category1 = categoryElements.get(0).getText();
            String link1 = categoryElements.get(0).getAttribute("href");
            String link2 = categoryElements.get(1).getAttribute("href");
            categoryElements.get(0).click();
            // travel - card 1
            WebElement card1 = driver.findElement(By.xpath("//div[@class=\"responsivegrid background_color_secondary_white padding_20 round_corner absolute-element-container aem-GridColumn--bootstrapxs--none aem-GridColumn--bootstrapsm--6 aem-GridColumn--default--none aem-GridColumn--offset--bootstrapxs--0 aem-GridColumn--bootstrapsm--none aem-GridColumn aem-GridColumn--offset--bootstrapsm--0 aem-GridColumn--offset--default--0 aem-GridColumn--default--4 aem-GridColumn--bootstrapxs--12\"]//div"));

            String cardName1 = driver.findElement(By.id("text-4556e5966c")).getText();
            String cardImg1 = card1.findElement(By.xpath("//div[@class=\"textandimage parbase aem-GridColumn--bootstrapxs--none aem-GridColumn--bootstrapsm--6 aem-GridColumn--offset--bootstrapxs--0 aem-GridColumn--bootstrapsm--none aem-GridColumn aem-GridColumn--offset--bootstrapsm--0 aem-GridColumn--default--4 aem-GridColumn--bootstrapxs--12\"]//div//div[@class=\"row text-image-row\"]//div//p//span//span//img")).getAttribute("src");
            String cardAFee1 = card1.findElement(By.xpath("//div[@class=\"responsivegrid aem-GridColumn--bootstrapxs--none aem-GridColumn--bootstrapsm--6 aem-GridColumn--offset--bootstrapxs--0 aem-GridColumn--bootstrapsm--none aem-GridColumn aem-GridColumn--offset--bootstrapsm--0 aem-GridColumn--default--4 aem-GridColumn--bootstrapxs--12\"]//div[@class=\"aem-Grid aem-Grid--4 aem-Grid--bootstrapxs--12 aem-Grid--default--4 aem-Grid--bootstrapsm--6 \"]//div[@class=\"text fontfamily-SemiBold text_right aem-GridColumn--default--none aem-GridColumn--bootstrapsm--none aem-GridColumn--bootstrapsm--2 aem-GridColumn aem-GridColumn--offset--bootstrapsm--0 aem-GridColumn--offset--default--0 aem-GridColumn--default--4 aem-GridColumn--bootstrapxs--4\"]//div[@id=\"text-3687c07a1e\"]//p//span//span")).getText();
            String cardPFee1 = card1.findElement(By.xpath("//*[@id=\"text-6782561c30\"]/p/span/span")).getText();
            List<String> cardDescription1 = new ArrayList<>();
            JSONObject obj1 = new JSONObject();
            obj1.put("category", category1);
            obj1.put("title", cardName1);
            obj1.put("image_url", cardImg1);
            obj1.put("annual_fee", cardAFee1);
            obj1.put("purchase_fee", cardPFee1);
            obj1.put("link", link1);

            // travel - card 2
            WebElement card2 = driver.findElement(By.xpath("//div[@class=\"responsivegrid background_color_secondary_white padding_20 round_corner absolute-element-container aem-GridColumn--bootstrapxs--none aem-GridColumn--bootstrapsm--6 aem-GridColumn--default--none aem-GridColumn--offset--bootstrapxs--0 aem-GridColumn--bootstrapsm--none aem-GridColumn aem-GridColumn--offset--bootstrapsm--0 aem-GridColumn--offset--default--0 aem-GridColumn--default--4 aem-GridColumn--bootstrapxs--12\"]//div"));

            String cardName2 = driver.findElement(By.id("text-e2826be145")).getText();
            String cardImg2 = card2.findElement(By.xpath("//div[@class=\"textandimage parbase aem-GridColumn--bootstrapxs--none aem-GridColumn--bootstrapsm--6 aem-GridColumn--offset--bootstrapxs--0 aem-GridColumn--bootstrapsm--none aem-GridColumn aem-GridColumn--offset--bootstrapsm--0 aem-GridColumn--default--4 aem-GridColumn--bootstrapxs--12\"]//div//div[@class=\"row text-image-row\"]//div//p//span//span//img")).getAttribute("src");
            String cardAFee2 = card2.findElement(By.xpath("//div[@class=\"responsivegrid aem-GridColumn--bootstrapxs--none aem-GridColumn--bootstrapsm--6 aem-GridColumn--offset--bootstrapxs--0 aem-GridColumn--bootstrapsm--none aem-GridColumn aem-GridColumn--offset--bootstrapsm--0 aem-GridColumn--default--4 aem-GridColumn--bootstrapxs--12\"]//div[@class=\"aem-Grid aem-Grid--4 aem-Grid--bootstrapxs--12 aem-Grid--default--4 aem-Grid--bootstrapsm--6 \"]//div[@class=\"text fontfamily-SemiBold text_right aem-GridColumn--default--none aem-GridColumn--bootstrapsm--none aem-GridColumn--bootstrapsm--2 aem-GridColumn aem-GridColumn--offset--bootstrapsm--0 aem-GridColumn--offset--default--0 aem-GridColumn--default--4 aem-GridColumn--bootstrapxs--4\"]//div[@id=\"text-dd84e9e2f0\"]//p//span//span")).getText();
            String cardPFee2 = card2.findElement(By.xpath("//*[@id=\"text-6628b8050a\"]/p/span/span")).getText();

            List<String> cardDescription2 = new ArrayList<>();
            JSONObject obj2 = new JSONObject();
            obj2.put("category", category1);
            obj2.put("title", cardName2);
            obj2.put("image_url", cardImg2);
            obj2.put("annual_fee", cardAFee2);
            obj2.put("purchase_fee", cardPFee2);
            obj2.put("link", link2);

            result.put(obj1);
            result.put(obj2);

            // Write the extracted information to a CSV file
            try (CSVWriter writer = new CSVWriter(new FileWriter(NBC_FILE_PATH))) {
                writer.writeNext(new String[]{"Category","Card Name","Image","Annual Fee","Purchase Fee","Link"});
                writer.writeNext(new String[]{category1, cardName1, cardImg1, cardAFee1, cardPFee1, link1});
                writer.writeNext(new String[]{category1, cardName2, cardImg2, cardAFee2, cardPFee2, link2});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            // Finally the web driver is quited
            driver.quit();

        } catch (StaleElementReferenceException e) {
            // Any exceptions that deals with stale element where one element is trying to access by web driver by does not exist in the DOM
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    private static String downloadImage(String imageUrl, String fileName) {
        return "";
//        try {
//            // Download image from URL and save to local file system
//            URL url = new URL(imageUrl);
//            InputStream in = url.openStream();
//            Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
//            in.close();
//            return Paths.get(fileName).toAbsolutePath().toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        }
    }
}
