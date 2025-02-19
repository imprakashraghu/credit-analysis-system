import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CIBCScraper {

    private static String CSV_FILE_PATH = "/Users/prakashraghu/scraping/cibc.csv";

    //creates the csv file and returns the csv writer obj
    public static CSVWriter filerd() throws IOException {
        // New csv file crddata.csv created in the same folder of this file
        File file = new File(CSV_FILE_PATH);

        FileWriter optfile = new FileWriter(file);
        CSVWriter wrtr = new CSVWriter(optfile);

        String[] hdr = { "Category", "Card Name", "Image", "AnnualFee", "PurchaseRate","Link"};
        wrtr.writeNext(hdr);
        return wrtr;
    }

    //executes the script mentioned in the act variable
    public static void click(WebDriver newDriver1, WebElement e, String act) {
        JavascriptExecutor scrol = (JavascriptExecutor) newDriver1;
        scrol.executeScript(act, e);
    }

    //executes the script mentioned in the act variable, using it to scroll the webpage
    public static void scroled(WebDriver newDriver1, String act) {
        JavascriptExecutor scrol = (JavascriptExecutor) newDriver1;
        scrol.executeScript(act);

    }

    public static String extractPercentage(String input) {
        // Use regex to find numbers followed by an optional percentage sign
        String regex = "\\d+\\.\\d+%?";
        // Use regex to replace everything except the pattern with empty string
        return input.replaceAll(".*?(" + regex + ").*", "$1");
    }

    public static String extractDollars(String input) {
        return input;
    }

    /*
     * Takes category name, category number, csv writer object.
     * Depending on the category number loops through all the credit cards within the category
     * and writes it to csv writer object.
     * */
    public static void crds(WebDriver newDriver1, String crdn, int ctgy, JSONArray result, CSVWriter cr) {

        HashMap<Integer, String> ctgry = new HashMap<Integer, String>();
        ctgry.put(1, "#buttonid-1649102992955");
        ctgry.put(2, "#buttonid-1649344174740");
        ctgry.put(3, "#buttonid-1649344331322");
        ctgry.put(4, "#buttonid-1649344397257");
        ctgry.put(5, "#buttonid-1649344494907");
        ctgry.put(6, "#buttonid-1649344612028");
        ctgry.put(7, "#buttonid-1649344656037");

        String crdpicked = null;
        if (ctgry.containsKey(ctgy)) {
            crdpicked = ctgry.get(ctgy);
        }
        WebDriverWait stop = new WebDriverWait(newDriver1, Duration.ofMillis(2000));
        //waiting for the visibility of the crdpicked element
        stop.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(crdpicked)));
        //waiting implicitly 3 seconds just for the page to load
        newDriver1.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        //storing the crdpicked ele and clicking it
        WebElement cback = newDriver1.findElement(By.cssSelector(crdpicked));
        String link = cback.getAttribute("href");
        System.out.println("Crawling: "+link);
        click(newDriver1, cback, "arguments[0].click()");

        //getting count of credit cards in the selected category but not using it
        int cnt = Integer.parseInt(newDriver1
                .findElement(By.xpath("//*[@id=\"product-filter-results\"]/div[2]/div[2]/div[1]/h2/strong")).getText());
        WebElement elmt = newDriver1.findElement(By.cssSelector(".results-list.versiontwo"));
        click(newDriver1, elmt, "arguments[0].click()");
        scroled(newDriver1, "window.scrollBy(0,3000)");
        List<WebElement> elmnts = elmt.findElements(By.xpath("./child::*"));

        //Category 3 which is costco cards doen't have same structure as other category web pages
        if(ctgy == 3) {
            try {
                List<WebElement> lt = elmt.findElements(By.cssSelector(".full-bleed.no-margin-sides.no-margin-top.no-margin-bottom.opacity-100.result.stacked"));

                for (WebElement elmnt : lt) {
                    JSONObject jsonObject = new JSONObject();
                    //				String crd = elmnt.findElement(By.cssSelector(".product-title b")).getText();
                    //Getting card name, image, headline details, benefits, price details for the card
                    String crd = elmnt.findElement(By.cssSelector(".product-image")).getAttribute("alt");
                    WebElement crdimgele = elmnt.findElement(By.cssSelector(".product-image"));
                    String crdimg = crdimgele.getAttribute("src");
                    String crdimgdtsrc = crdimgele.getAttribute("data-src");
                    String crdhdline = elmnt.findElement(By.cssSelector(".product-headline")).getText();
                    String crdvalp = elmnt.findElement(By.cssSelector(".product-valueprop")).getText();
                    WebElement prdbnfts = elmnt.findElement(By.cssSelector(".product-benefits ul"));
                    List<WebElement> prdbnftselmnts = prdbnfts.findElements(By.xpath("./child::*"));
                    HashMap<Integer, String> prdbnftsmp = new HashMap<Integer, String>();
                    int bn = 0;
                    //looping through product benefits
                    for (WebElement crdrateelmt : prdbnftselmnts) {
                        String insd2 = crdrateelmt.getText();
                        prdbnftsmp.put(bn, insd2);
                        bn += 1;
                    }
                    WebElement crdrate = elmnt.findElement(By.cssSelector(".column.large-4.medium-4.small-12.product-rates > div > div"));
                    List<WebElement> crdrateelmts = crdrate.findElements(By.xpath("./child::*"));
                    HashMap<Integer, String> crdratemp = new HashMap<Integer, String>();
                    int lp = 0;
                    //looping through product rates
                    for (WebElement crdrateelmt : crdrateelmts) {
                        String insd2 = crdrateelmt.findElement(By.cssSelector(".product-rate-value")).getText().trim().replaceAll("\n ", "");
                        crdratemp.put(lp, insd2);
                        lp += 1;
                    }
                    if (crdimgdtsrc != null) {
                        crdimgdtsrc = "https://www.cibc.com" + crdimgdtsrc;
                        crdimg = crdimgdtsrc;
                    }
                    crdvalp = crdvalp.replaceAll("\\d+$", "");
                    crdvalp = crdvalp.replaceAll("\\d+,?$", "");
                    String hdln1 = prdbnftsmp.get(0).replaceAll("\\d+$", "");
                    hdln1 = hdln1.replaceAll("\\d+,?$", "");
                    hdln1 = hdln1.replaceAll("\\,?$", "");
                    String hdln2 = prdbnftsmp.get(1).replaceAll("\\d+$", "");
                    hdln2 = hdln2.replaceAll("\\d+,?$", "");
                    hdln2 = hdln2.replaceAll("\\,?$", "");
                    //.?\d?,?\d+$

                    // Creation and insertion of the json object from the data extracted from the site using the driver

                    jsonObject.put("category", crdn);
                    jsonObject.put("title", crd);
                    jsonObject.put("image_url", crdimg);
                    jsonObject.put("annual_fee", extractDollars(crdratemp.get(0)));
                    jsonObject.put("purchase_fee", extractPercentage(crdratemp.get(1)));
                    jsonObject.put("link", link);
                    result.put(jsonObject);
                    String[] d = { crdn, crd, crdimg, extractDollars(crdratemp.get(0)), extractPercentage(crdratemp.get(1)), link };
//                  writing card details to file
                    cr.writeNext(d);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        //Common loop for other category cards
        for (WebElement elmnt : elmnts) {
            JSONObject jsonObject = new JSONObject();
            if(ctgy == 3 ) return;
            //Getting card name, image, headline details, benefits, price details for the card
            String crd = elmnt.findElement(By.cssSelector(".product-image")).getAttribute("alt");
            WebElement crdimgele = elmnt.findElement(By.cssSelector(".product-image"));
            String crdimg = crdimgele.getAttribute("src");
            String crdimgdtsrc = crdimgele.getAttribute("data-src");
            String crdhdline = elmnt.findElement(By.cssSelector(".product-headline")).getText();
            String crdvalp = elmnt.findElement(By.cssSelector(".product-valueprop")).getText();
            WebElement prdbnfts = elmnt.findElement(By.cssSelector(".product-benefits ul"));
            List<WebElement> prdbnftselmnts = prdbnfts.findElements(By.xpath("./child::*"));
            HashMap<Integer, String> prdbnftsmp = new HashMap<Integer, String>();
            int bn = 0;
            //looping through product benefits
            for (WebElement crdrateelmt : prdbnftselmnts) {
                String insd2 = crdrateelmt.getText();
                prdbnftsmp.put(bn, insd2);
                bn += 1;
            }
            WebElement crdrate = elmnt.findElement(By.cssSelector(".product-rates div"));
            List<WebElement> crdrateelmts = crdrate.findElements(By.xpath("./child::*"));
            HashMap<Integer, String> crdratemp = new HashMap<Integer, String>();
            int lp = 0;
            //looping through product rates
            for (WebElement crdrateelmt : crdrateelmts) {
                String insd2 = crdrateelmt.findElement(By.cssSelector(".product-rate-value")).getText().trim().replaceAll("\n ", "");
                crdratemp.put(lp, insd2);
                lp += 1;
            }
            if(crdimgdtsrc != null) {
                crdimgdtsrc = "https://www.cibc.com" + crdimgdtsrc;
                crdimg = crdimgdtsrc;
            }
            crdvalp = crdvalp.replaceAll("\\d+$", "");
            crdvalp = crdvalp.replaceAll("\\d+,?$", "");
            String hdln1 = prdbnftsmp.get(0).replaceAll("\\d+$", "");
            hdln1 = hdln1.replaceAll("\\d+,?$", "");
            hdln1 = hdln1.replaceAll("\\,?$", "");
            String hdln2 = prdbnftsmp.get(1).replaceAll("\\d+$", "");
            hdln2 = hdln2.replaceAll("\\d+,?$", "");
            hdln2 = hdln2.replaceAll("\\,?$", "");
            //.?\d?,?\d+$


            jsonObject.put("category", crdn);
            jsonObject.put("title", crd);
            jsonObject.put("image_url", crdimg);
            jsonObject.put("annual_fee", extractDollars(crdratemp.get(0)));
            jsonObject.put("purchase_fee", extractPercentage(crdratemp.get(1)));
            jsonObject.put("link", link);
//            jsonObject.put("description_1", prdbnftsmp.get(0));
            result.put(jsonObject);
            String[] d = { crdn, crd, crdimg, extractDollars(crdratemp.get(0)), extractPercentage(crdratemp.get(1)), link };
            cr.writeNext(d);
        }

        //Filling up the form in Travel Category section
        if (ctgy == 1) {
            WebElement crd = newDriver1.findElement(By.cssSelector("a[href*='aventura-visa-infinite-card']"));
            click(newDriver1, crd, "arguments[0].click()");
            newDriver1.findElement(By.id("inputCCRCSlider1")).sendKeys("1000");
            newDriver1.findElement(By.id("inputCCRCSlider2")).sendKeys("4000");
        }
    }

    public static JSONArray scrapSite() throws IOException {
        //setting the webdriver property
        System.setProperty("webdriver.chrome.driver", "/Users/venkat/chromedriver");

        WebDriver newDriver1 = new ChromeDriver();

        //cibc homepage url
        String home = "https://www.cibc.com/en/personal-banking/credit-cards.html";
        System.out.println("Crawling: "+home);

        //maximizing the window of browser
        newDriver1.manage().window().minimize();

        //navigating to home
        newDriver1.get(home);

        //waiting for 20000 ms, every 500 ms element is checked for visibility if present waiting stops
        WebDriverWait stop = new WebDriverWait(newDriver1, Duration.ofSeconds(10));

        stop.until(ExpectedConditions.visibilityOfElementLocated(By.id("onetrust-accept-btn-handler")));

        newDriver1.findElement(By.cssSelector("#onetrust-accept-btn-handler")).click();

        CSVWriter cr = null;
        //filerd function executed which return csv file writer obj
        try {
            cr = filerd();
        } catch (IOException ei) {
            System.out.println(ei);
            ei.printStackTrace();
        }
        JSONArray result = new JSONArray();
        //navigating to each credit card category webpage to scrape the details on the page and load it to the csv file
        crds(newDriver1, "Travel", 1, result, cr);
        newDriver1.navigate().to(home);
        crds(newDriver1, "Cashback", 2, result, cr);
        newDriver1.navigate().to(home);
        crds(newDriver1, "Costco", 3, result, cr);
        newDriver1.navigate().to(home);
        crds(newDriver1, "No Annual Fee", 4, result, cr);
        newDriver1.navigate().to(home);
        crds(newDriver1, "Low Interest", 5, result, cr);
        newDriver1.navigate().to(home);
        crds(newDriver1, "Student Credit", 6, result, cr);
        newDriver1.navigate().to(home);
        crds(newDriver1, "Business", 7, result, cr);

        cr.close();

        newDriver1.navigate().to(home);

        newDriver1.quit();

        return result;
    }
}
