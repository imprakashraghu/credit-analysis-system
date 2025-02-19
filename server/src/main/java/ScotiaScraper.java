import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScotiaScraper {
    public static WebElement Creditcard;
    private static String SCOTIA_CSV_FILE_PATH = "/Users/prakashraghu/scraping/scotia.csv";

    public static JSONArray scrapSite3() {
        JSONArray responseJson = new JSONArray();
        // Initialize WebDriver

        WebDriver driver = new ChromeDriver();

        // List to store card details
        List<String[]> cardDetailsList = new ArrayList<>();

        // Add header row
        cardDetailsList.add(new String[]{"Card Name", "Image", "Annual Fee", "Purchase Rate"});

        try {
            JSONObject[] jsonObjects = new JSONObject[6];
            // Open the page
            driver.get("https://www.scotiabank.com/ca/en/personal.html");

            driver.manage().window().minimize();

            // Get the page source
            String pageSource = driver.getPageSource();

            // Parse the page with JSoup
            Document doc = Jsoup.parse(pageSource);

            // Select the credit card elements
            String category1 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(1) > div > div > div > div.callout.dark-grey.dark-bg").text();
            String cardName1 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(1) > div > div > div > div.card-content > div:nth-child(1) > h3").text();
            String cardImage1 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(1) > div > div > div > div:nth-child(2) > div > img").attr("src");
            String aFee1 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(1) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(4) > b").text();
            String pFee1 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(1) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(5) > b:nth-child(2)").text();
            String link1 = doc.select("#PassportVisaInfinite-Tile-Eng").attr("href");
            jsonObjects[0] = new JSONObject();
            jsonObjects[0].put("category", category1);
            jsonObjects[0].put("title", cardName1);
            jsonObjects[0].put("image_url", "https://www.scotiabank.com/"+cardImage1);
            jsonObjects[0].put("annual_fee", aFee1);
            jsonObjects[0].put("purchase_fee", pFee1);
            jsonObjects[0].put("link", link1);
            responseJson.put(jsonObjects[0]);
            String[] card1 = new String[]{category1, cardName1, "https://www.scotiabank.com/"+cardImage1, aFee1, pFee1, link1};

            String category2 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(3) > div > div > div > div.callout.dark-grey.dark-bg").text();
            String cardName2 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(3) > div > div > div > div.card-content > div:nth-child(1) > h3 > span").text();
            String cardImage2 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(3) > div > div > div > div:nth-child(2) > div > img").attr("src");
            String aFee2 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(3) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(4) > b:nth-child(2)").text();
            String pFee2 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(3) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(4) > b:nth-child(4)").text();
            String link2 = doc.select("#ScotiaMomentumVisaInfiniteCard-Tile-Eng").attr("href");
            jsonObjects[1] = new JSONObject();
            jsonObjects[1].put("category", category2);
            jsonObjects[1].put("title", cardName2);
            jsonObjects[1].put("image_url", "https://www.scotiabank.com/"+cardImage2);
            jsonObjects[1].put("annual_fee", aFee2);
            jsonObjects[1].put("purchase_fee", pFee2);
            jsonObjects[1].put("link", link2);
            responseJson.put(jsonObjects[1]);
            String[] card2 = new String[]{category2, cardName2, "https://www.scotiabank.com/"+cardImage2, aFee2, pFee2, link2};

            String category3 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(5) > div > div > div > div.callout.dark-grey.dark-bg").text();
            String cardName3 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(5) > div > div > div > div.card-content > div:nth-child(1) > h3 > span").text();
            String cardImage3 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(5) > div > div > div > div:nth-child(2) > div > img").attr("src");
            String aFee3 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(5) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(4) > b:nth-child(1)").text();
            String pFee3 = doc.select("#credit-cards-panel > div > div:nth-child(3) > div > div > div:nth-child(5) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(4) > b:nth-child(3)").text();
            String link3 = doc.select("#AmericanExpressGold-Tile-Eng").attr("href");
            jsonObjects[2] = new JSONObject();
            jsonObjects[2].put("category", category3);
            jsonObjects[2].put("title", cardName3);
            jsonObjects[2].put("image_url", "https://www.scotiabank.com/"+cardImage3);
            jsonObjects[2].put("annual_fee", aFee3);
            jsonObjects[2].put("purchase_fee", pFee3);
            jsonObjects[2].put("link", link3);
            responseJson.put(jsonObjects[2]);
            String[] card3 = new String[]{category3, cardName3, "https://www.scotiabank.com/"+cardImage3, aFee3, pFee3, link3};

            String category4 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(1) > div > div > div > div.callout.dark-grey.dark-bg").text();
            String cardName4 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(1) > div > div > div > div.card-content > div:nth-child(1) > h3:nth-child(1) > span > span").text();
            String cardImage4 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(1) > div > div > div > div:nth-child(2) > div > img").attr("src");
            String aFee4 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(1) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(5) > b:nth-child(2)").text();
            String pFee4 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(1) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(5) > b:nth-child(4)").text();
            String link4 = doc.select("#ScotiaSceneVisaCard-Tile-Eng").attr("href");
            jsonObjects[3] = new JSONObject();
            jsonObjects[3].put("category", category4);
            jsonObjects[3].put("title", cardName4);
            jsonObjects[3].put("image_url", "https://www.scotiabank.com/"+cardImage4);
            jsonObjects[3].put("annual_fee", aFee4);
            jsonObjects[3].put("purchase_fee", pFee4);
            jsonObjects[3].put("link", link4);
            responseJson.put(jsonObjects[3]);
            String[] card4 = new String[]{category4, cardName4, "https://www.scotiabank.com/"+cardImage4, aFee4, pFee4, link4};

            String category5 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(3) > div > div > div > div.callout.dark-grey.dark-bg").text();
            String cardName5 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(3) > div > div > div > div.card-content > div:nth-child(1) > h3 > b > span").text();
            String cardImage5 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(3) > div > div > div > div:nth-child(2) > div > img").attr("src");
            String aFee5 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(3) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(6) > b").text();
            String pFee5 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(3) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(6) > span > b:nth-child(1)").text();
            String link5 = doc.select("#ValueVisaCard-Tile-Eng").attr("href");
            jsonObjects[4] = new JSONObject();
            jsonObjects[4].put("category", category5);
            jsonObjects[4].put("title", cardName5);
            jsonObjects[4].put("image_url", "https://www.scotiabank.com/"+cardImage5);
            jsonObjects[4].put("annual_fee", aFee5);
            jsonObjects[4].put("purchase_fee", pFee5);
            jsonObjects[4].put("link", link5);
            responseJson.put(jsonObjects[4]);
            String[] card5 = new String[]{category5, cardName5, "https://www.scotiabank.com/"+cardImage5, aFee5, pFee5, link5};

            String category6 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(5) > div > div > div > div.callout.dark-grey.dark-bg").text();
            String cardName6 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(5) > div > div > div > div.card-content > div:nth-child(1) > h3 > span").text();
            String cardImage6 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(5) > div > div > div > div:nth-child(2) > div > img").attr("src");
            String aFee6 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(5) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(4) > b:nth-child(1)").text();
            String pFee6 = doc.select("#credit-cards-panel > div > div:nth-child(5) > div > div > div:nth-child(5) > div > div > div > div.card-content > div:nth-child(1) > p:nth-child(4) > b:nth-child(2)").text();
            String link6 = doc.select("#ScotiaMomentumVisaCard-Tile-Eng").attr("href");
            jsonObjects[5] = new JSONObject();
            jsonObjects[5].put("category", category6);
            jsonObjects[5].put("title", cardName6);
            jsonObjects[5].put("image_url", "https://www.scotiabank.com/"+cardImage6);
            jsonObjects[5].put("annual_fee", aFee6);
            jsonObjects[5].put("purchase_fee", pFee6);
            jsonObjects[5].put("link", link6);
            responseJson.put(jsonObjects[5]);
            String[] card6 = new String[]{category6, cardName6, "https://www.scotiabank.com/"+cardImage6, aFee6, pFee6, link6};

            FileWriter fileWriter = new FileWriter(SCOTIA_CSV_FILE_PATH);
            //Initialise CSV writer to facilitate writin to the CSV file.
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            String[] headings = {"Category","Card Name","Image","Annual Fee","Purchase Fee", "Link"};

            // Write to CSV
            csvWriter.writeNext(headings);
            csvWriter.writeNext(card1);
            csvWriter.writeNext(card2);
            csvWriter.writeNext(card3);
            csvWriter.writeNext(card4);
            csvWriter.writeNext(card5);
            csvWriter.writeNext(card6);
            csvWriter.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // Close the browser
            driver.quit();
        }
        return responseJson;
    }

}
