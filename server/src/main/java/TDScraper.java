import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TDScraper {

    private static String TD_CSV_FILE_PATH = "/Users/prakashraghu/scraping/td.csv";

    public static JSONArray scrapSite() {
        JSONArray responseJson = new JSONArray();
        // Initialize WebDriver

        WebDriver driver = new ChromeDriver();

        // List to store card details
        List<String[]> cardDetailsList = new ArrayList<>();

        // Add header row
        cardDetailsList.add(new String[]{"Category","Card Name", "Image", "Annual Fee", "Purchase Rate", "Link"});

        try {
            // Open the page
            driver.get("https://www.td.com/ca/en/personal-banking/products/credit-cards");

            driver.manage().window().minimize();

            // Get the page source
            String pageSource = driver.getPageSource();

            // Parse the page with JSoup
            Document doc = Jsoup.parse(pageSource);

            // Select the credit card elements
            Elements cardElements = doc.select(".cmp-compare-product__container .compareProduct");

            // Iterate over each card element and extract details
            for (Element cardElement : cardElements) {
                JSONObject item = new JSONObject();
                String cardName = cardElement.attr("data-cardname").trim().replace("&nbsp;","");
                String cardImage = cardElement.attr("data-cardimg");
                String cardDescriptionUrl = cardElement.attr("data-carddescriptionurl");
                String specialOffer = cardElement.attr("data-cardspecialoffer");
                String annualFee = cardElement.select(".cmp-compare-product__card--info-content-text strong p").first().text();
                String interestPurchases = cardElement.select(".cmp-compare-product__card--info-content-text strong p").get(1).text();
                String interestCashAdvances = cardElement.select(".cmp-compare-product__card--info-content-text strong p").get(2).text();
                String detailsLink = cardElement.select(".td-link-action a").attr("href");
                String applyLink = cardElement.select(".cmp-compare-product__card--cta-button").attr("href");
                item.put("category", "Featured");
                item.put("title", cardName);
                item.put("image_url", "https://www.td.com"+cardImage);
                item.put("annual_fee", annualFee);
                item.put("purchase_fee", interestPurchases);
                item.put("link", detailsLink);
                responseJson.put(item);
                // Add details to the list
                cardDetailsList.add(new String[]{"Featured", cardName, "https://www.td.com"+cardImage, annualFee, interestPurchases, "https://www.td.com"+
                        detailsLink});
            }

            // Write to CSV
            writeCsv(TD_CSV_FILE_PATH, cardDetailsList);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // Close the browser
            driver.quit();
        }
        return responseJson;
    }

    /**
     * Used to write data to the csv file
     * @param fileName
     * @param data
     */
    private static void writeCsv(String fileName, List<String[]> data) {
        try (FileWriter csvWriter = new FileWriter(fileName)) {
            for (String[] rowData : data) {
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }
            csvWriter.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
