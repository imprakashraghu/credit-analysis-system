import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Node {
    String word;
    int frequency;
    int height;
    Node left;
    Node right;

    // public constructor to instantiate
    public Node(String word) {
        this.word = word;
        this.frequency = 1;
        this.height = 1;
    }
}

public class AVLTree {
    Node root;

    int height(Node node) {
        if (node == null) return 0;
        return node.height;
    }

    int balanceFactor(Node node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    Node rightRotate(Node node) {
        Node x = node.left;
        Node T2 = x.right;
        x.right = node;
        node.left = T2;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return x;
    }

    Node leftRotate(Node x) {
        Node node = x.right;
        Node T2 = node.left;
        node.left = x;
        x.right = T2;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        return node;
    }

    Node insert(Node node, String word) {
        if (node == null) return new Node(word);
        if (word.compareTo(node.word) < 0)
            node.left = insert(node.left, word);
        else if (word.compareTo(node.word) > 0)
            node.right = insert(node.right, word);
        else {
            node.frequency++;
            return node;
        }
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        int balance = balanceFactor(node);
        if (balance > 1 && word.compareTo(node.left.word) < 0)
            return rightRotate(node);
        if (balance < -1 && word.compareTo(node.right.word) > 0)
            return leftRotate(node);
        if (balance > 1 && word.compareTo(node.left.word) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && word.compareTo(node.right.word) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    Node searchPrefix(Node node, String prefix) {
        if (node == null) return null;
        if (node.word.startsWith(prefix)) return node;
        if (prefix.compareTo(node.word) < 0) return searchPrefix(node.left, prefix);
        return searchPrefix(node.right, prefix);
    }

    void collectWords(Node node, String prefix, List<Map.Entry<String, Integer>> words) {
        if (node == null) return;
        if (node.word.startsWith(prefix)) words.add(new AbstractMap.SimpleEntry<>(node.word, node.frequency));
        collectWords(node.left, prefix, words);
        collectWords(node.right, prefix, words);
    }

    List<Map.Entry<String, Integer>> autocomplete(String prefix) {
        List<Map.Entry<String, Integer>> words = new ArrayList<>();
        if (prefix == null) {
            System.out.println("Prefix cannot be null");
            return words;
        }
        Node matchedNode = searchPrefix(root, prefix);
        if (matchedNode != null) collectWords(matchedNode, prefix, words);
        return words;
    }

    List<String> autocompleteWithRanking(String prefix, int topN) {
        List<String> topSuggestions = new ArrayList<>();
        try {
            if (prefix == null) {
                System.out.println("Prefix cannot be null");
                return topSuggestions;
            }
            if (topN < 0) {
                System.out.println("topN cannot be negative");
                return topSuggestions;
            }
            List<Map.Entry<String, Integer>> words;
            try {
                words = autocomplete(prefix); // gets a list of words that matches the prefix provided
            } catch (Exception e) {
                System.out.println("Error retrieving autocomplete suggestions - "+e.getMessage());
                return topSuggestions;
            }
            if (words == null) {
                System.out.println("Autocomplete suggestions cannot be null");
                return topSuggestions;
            }
            PriorityQueue<Map.Entry<String, Integer>> maxHeapDSForAutoCompleteFeature = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
            for (Map.Entry<String, Integer> entry : words) {
                maxHeapDSForAutoCompleteFeature.offer(entry);
                if (maxHeapDSForAutoCompleteFeature.size() > topN) maxHeapDSForAutoCompleteFeature.poll();
            }
            while (!maxHeapDSForAutoCompleteFeature.isEmpty()) topSuggestions.add(maxHeapDSForAutoCompleteFeature.poll().getKey());
//        Collections.reverse(topSuggestions); // highest frequency first - list in reverse order
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return topSuggestions;
    }
}

public class BestDealFinder implements HttpHandler {

    private String COMBINE_CSV_PATH = "/Users/prakashraghu/scraping/combineCSV.csv";

    public BestDealFinder() {}

    public static List[] findBestDeals(String filePath, int top, String categoryFilter) {
        List[] result = new List[2];

        if (filePath == null) {
            System.out.println("File Path cannot be empty");
            return result;
        }

        if (top < 0) {
            System.out.println("Invalid top integer");
            return result;
        }

        if (categoryFilter == null) {
            System.out.println("Category filter cannot be empty");
            return result;
        }

        List<CreditCardModel> creditCards = new ArrayList<>();
        HashSet<String> categoriesAvailable = new HashSet<>();
        NumberFormat format = NumberFormat.getInstance(Locale.US);

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] values;
            csvReader.readNext(); // Skip header

            while ((values = csvReader.readNext()) != null) {
                String category = values[0];
                String name = values[1];
                String imageUrl = values[2];
                double annualFee = values[3].contains("US") ? format.parse(values[3].replace("US","")
                        .replace("$","").trim()).doubleValue() : format.parse((values[3]
                        .split(" ").length>1?values[3].split(" ")[values[3].split(" ").length-1]
                        :values[3]).replace("$", "")).doubleValue();
                double purchaseRate = format.parse((values[4].split(" ").length>1?values[4]
                        .split(" ")[values[4].split(" ").length-1]
                        :values[4]).replace("%", "")).doubleValue();
                String link = values[5];
                ArrayList<String> descriptions = new ArrayList<>();
                CreditCardModel card = new CreditCardModel(category, name, imageUrl, String.valueOf(annualFee),
                        String.valueOf(purchaseRate), descriptions, link);
                creditCards.add(card);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return result;
        }

        // Sort by annual fee and purchase rate
        creditCards.sort(Comparator.comparingDouble(CreditCardModel::getPurchaseRateInDouble)
                .thenComparingDouble(CreditCardModel::getAnnualFeeInDouble));

        // Return top deals
        List<CreditCardModel> deals = creditCards.subList(0, Math.min(top, creditCards.size()));

        for (CreditCardModel itemDeal: deals) {
            categoriesAvailable.add(itemDeal.getCategory());
        }
        result[1] = List.of(categoriesAvailable.toArray());

        List<CreditCardModel> filteredDeals = new ArrayList<>();
        if (!categoryFilter.equals("all")) {
            for (CreditCardModel itemDeal: deals) {
                if (itemDeal.getCategory().equals(categoryFilter)) {
                    filteredDeals.add(itemDeal);
                }
            }
            result[0] = filteredDeals;
        } else {
            result[0] = deals;
        }

        return result;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Handles CORS-Policy Issues
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        if ("POST".equals(exchange.getRequestMethod())) {
            JSONObject result = getResultFromRequest(exchange);

            String response = result.toString();

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");

            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    private JSONObject getResultFromRequest(HttpExchange exchange) throws IOException {
        JSONObject responseJSON = new JSONObject();
        InputStream is = exchange.getRequestBody();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        JSONObject json = new JSONObject(sb.toString());
        int topResults = json.getInt("top");
        String category = json.getString("category");

        JSONArray result = new JSONArray();

        List[] response = findBestDeals(this.COMBINE_CSV_PATH, topResults, category);

        List<CreditCardModel> deals = response[0];
        List<String> categories = response[1];

        for (CreditCardModel creditCard: deals) {
            JSONObject item = new JSONObject();
            item.put("category", creditCard.getCategory());
            item.put("title", creditCard.getTitle());
            item.put("image_url", creditCard.getImage());
            item.put("annual_fee", creditCard.getAnnualFee());
            item.put("purchase_fee", creditCard.getPurchaseFee());
            item.put("link", creditCard.getLink());
            result.put(item);
        }

        responseJSON.put("best_deals", result);
        responseJSON.put("categories", categories);

        return responseJSON;

    }
}


public class CIBCScraper {

    private static String CSV_FILE_PATH = "/Users/prakashraghu/scraping/cibc.csv";

    //creates the csv file and returns the csv writer obj
    public static CSVWriter filerd() throws IOException {
        // New csv file crddata.csv created in the same folder of this file
        File file = new File(CSV_FILE_PATH);

        FileWriter optionFileWriterObj = new FileWriter(file);
        CSVWriter CSVWriterObjzForCIBCSCraper = new CSVWriter(optionFileWriterObj);

        String[] hdr = { "Category", "Card Name", "Image", "AnnualFee", "PurchaseRate","Link"};
        CSVWriterObjzForCIBCSCraper.writeNext(hdr);
        return CSVWriterObjzForCIBCSCraper;
    }

    //executes the script mentioned in the act variable
    public static void click(WebDriver driverForExtration, WebElement e, String act) {
        JavascriptExecutor scrollableClickableObj = (JavascriptExecutor) driverForExtration;
        scrollableClickableObj.executeScript(act, e);
    }

    //executes the script mentioned in the act variable, using it to scroll the webpage
    public static void scroled(WebDriver driverForExtration, String act) {
        JavascriptExecutor scrol = (JavascriptExecutor) driverForExtration;
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
    
    public static void gettingCategoriesWiseInformationFromCIBC(WebDriver driverForExtration, String crdn, int ctgy, JSONArray result, CSVWriter cr) {

        HashMap<Integer, String> categoryForCIBCScraper = new HashMap<Integer, String>();
        categoryForCIBCScraper.put(1, "#buttonid-1649102992955");
        categoryForCIBCScraper.put(2, "#buttonid-1649344174740");
        categoryForCIBCScraper.put(3, "#buttonid-1649344331322");
        categoryForCIBCScraper.put(4, "#buttonid-1649344397257");
        categoryForCIBCScraper.put(5, "#buttonid-1649344494907");
        categoryForCIBCScraper.put(6, "#buttonid-1649344612028");
        categoryForCIBCScraper.put(7, "#buttonid-1649344656037");

        String crdpicked = null;
        if (categoryForCIBCScraper.containsKey(ctgy)) {
            crdpicked = categoryForCIBCScraper.get(ctgy);
        }
        WebDriverWait stop = new WebDriverWait(driverForExtration, Duration.ofMillis(2000));
        //waiting for the visibility of the crdpicked element
        stop.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(crdpicked)));
        //waiting implicitly 3 seconds just for the page to load
        driverForExtration.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        //storing the crdpicked ele and clicking it
        WebElement cback = driverForExtration.findElement(By.cssSelector(crdpicked));
        String link = cback.getAttribute("href");
        System.out.println("Crawling: "+link);
        click(driverForExtration, cback, "arguments[0].click()");

        //getting count of credit cards in the selected category but not using it
        int cnt = Integer.parseInt(driverForExtration
                .findElement(By.xpath("//*[@id=\"product-filter-results\"]/div[2]/div[2]/div[1]/h2/strong")).getText());
        WebElement elementForCIBCFeature = driverForExtration.findElement(By.cssSelector(".results-list.versiontwo"));
        click(driverForExtration, elementForCIBCFeature, "arguments[0].click()");
        scroled(driverForExtration, "window.scrollBy(0,3000)");
        List<WebElement> elmnts = elementForCIBCFeature.findElements(By.xpath("./child::*"));

        //Category 3 which is costco cards doen't have same structure as other category web pages
        if(ctgy == 3) {
            try {
                List<WebElement> lt = elementForCIBCFeature.findElements(By.cssSelector(".full-bleed.no-margin-sides.no-margin-top.no-margin-bottom.opacity-100.result.stacked"));

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
                    for (WebElement cardRateLimitCrlt : prdbnftselmnts) {
                        String insd2 = cardRateLimitCrlt.getText();
                        prdbnftsmp.put(bn, insd2);
                        bn += 1;
                    }
                    WebElement crdrate = elmnt.findElement(By.cssSelector(".column.large-4.medium-4.small-12.product-rates > div > div"));
                    List<WebElement> cardRateLimitCrlts = crdrate.findElements(By.xpath("./child::*"));
                    HashMap<Integer, String> crdratemp = new HashMap<Integer, String>();
                    int lp = 0;
                    //looping through product rates
                    for (WebElement cardRateLimitCrlt : cardRateLimitCrlts) {
                        String insd2 = cardRateLimitCrlt.findElement(By.cssSelector(".product-rate-value")).getText().trim().replaceAll("\n ", "");
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
        for (WebElement elementForCIBCIteratio : elmnts) {
            JSONObject jsonObject = new JSONObject();
            if(ctgy == 3 ) return;
            String CardForCIBCIetartionScraper = elementForCIBCIteratio.findElement(By.cssSelector(".product-image")).getAttribute("alt");
            WebElement crdimgele = elementForCIBCIteratio.findElement(By.cssSelector(".product-image"));
            String crdimg = crdimgele.getAttribute("src");
            String crdimgdtsrc = crdimgele.getAttribute("data-src");
            String crdhdline = elementForCIBCIteratio.findElement(By.cssSelector(".product-headline")).getText();
            String crdvalp = elementForCIBCIteratio.findElement(By.cssSelector(".product-valueprop")).getText();
            WebElement prdbnfts = elementForCIBCIteratio.findElement(By.cssSelector(".product-benefits ul"));
            List<WebElement> prdbnftselmnts = prdbnfts.findElements(By.xpath("./child::*"));
            HashMap<Integer, String> prdbnftsmp = new HashMap<Integer, String>();
            int bn = 0;
            //looping through product benefits
            for (WebElement cardRateLimitCrlt : prdbnftselmnts) {
                String insd2 = cardRateLimitCrlt.getText();
                prdbnftsmp.put(bn, insd2);
                bn += 1;
            }
            WebElement crdrate = elementForCIBCIteratio.findElement(By.cssSelector(".product-rates div"));
            List<WebElement> cardRateLimitCrlts = crdrate.findElements(By.xpath("./child::*"));
            HashMap<Integer, String> crdratemp = new HashMap<Integer, String>();
            int lp = 0;
            for (WebElement cardRateLimitCrlt : cardRateLimitCrlts) {
                String insd2 = cardRateLimitCrlt.findElement(By.cssSelector(".product-rate-value")).getText().trim().replaceAll("\n ", "");
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
            jsonObject.put("title", CardForCIBCIetartionScraper);
            jsonObject.put("image_url", crdimg);
            jsonObject.put("annual_fee", extractDollars(crdratemp.get(0)));
            jsonObject.put("purchase_fee", extractPercentage(crdratemp.get(1)));
            jsonObject.put("link", link);
//            jsonObject.put("description_1", prdbnftsmp.get(0));
            result.put(jsonObject);
            String[] d = { crdn, CardForCIBCIetartionScraper, crdimg, extractDollars(crdratemp.get(0)), extractPercentage(crdratemp.get(1)), link };
            cr.writeNext(d);
        }

        //Filling up the form in Travel Category section
        if (ctgy == 1) {
            WebElement crd = driverForExtration.findElement(By.cssSelector("a[href*='aventura-visa-infinite-card']"));
            click(driverForExtration, crd, "arguments[0].click()");
            driverForExtration.findElement(By.id("inputCCRCSlider1")).sendKeys("1000");
            driverForExtration.findElement(By.id("inputCCRCSlider2")).sendKeys("4000");
        }
    }

    public static JSONArray scrapSite() throws IOException {
        //setting the webdriver property
        System.setProperty("webdriver.chrome.driver", "/Users/venkat/chromedriver");

        WebDriver driverForCIBCFinalPRoject = new ChromeDriver();

        //cibc homepage url
        String home = "https://www.cibc.com/en/personal-banking/credit-cards.html";
        System.out.println("Crawling: "+home);

        //maximizing the window of browser
        driverForCIBCFinalPRoject.manage().window().minimize();

        //navigating to home
        driverForCIBCFinalPRoject.get(home);

        //waiting for 20000 ms, every 500 ms element is checked for visibility if present waiting stops
        WebDriverWait stop = new WebDriverWait(driverForCIBCFinalPRoject, Duration.ofSeconds(10));

        stop.until(ExpectedConditions.visibilityOfElementLocated(By.id("onetrust-accept-btn-handler")));

        driverForCIBCFinalPRoject.findElement(By.cssSelector("#onetrust-accept-btn-handler")).click();

        CSVWriter CSVWriterObjForCategoryExtraction = null;
        //filerd function executed which return csv file writer obj
        try {
            CSVWriterObjForCategoryExtraction = filerd();
        } catch (IOException ei) {
            System.out.println(ei);
            ei.printStackTrace();
        }
        JSONArray result = new JSONArray();
        //navigating to each credit card category webpage to scrape the details on the page and load it to the csv file
        gettingCategoriesWiseInformationFromCIBC(driverForCIBCFinalPRoject, "Travel", 1, result, CSVWriterObjForCategoryExtraction);
        driverForCIBCFinalPRoject.navigate().to(home);
        gettingCategoriesWiseInformationFromCIBC(driverForCIBCFinalPRoject, "Cashback", 2, result, CSVWriterObjForCategoryExtraction);
        driverForCIBCFinalPRoject.navigate().to(home);
        gettingCategoriesWiseInformationFromCIBC(driverForCIBCFinalPRoject, "Costco", 3, result, CSVWriterObjForCategoryExtraction);
        driverForCIBCFinalPRoject.navigate().to(home);
        gettingCategoriesWiseInformationFromCIBC(driverForCIBCFinalPRoject, "No Annual Fee", 4, result, CSVWriterObjForCategoryExtraction);
        driverForCIBCFinalPRoject.navigate().to(home);
        gettingCategoriesWiseInformationFromCIBC(driverForCIBCFinalPRoject, "Low Interest", 5, result, CSVWriterObjForCategoryExtraction);
        driverForCIBCFinalPRoject.navigate().to(home);
        gettingCategoriesWiseInformationFromCIBC(driverForCIBCFinalPRoject, "Student Credit", 6, result, CSVWriterObjForCategoryExtraction);
        driverForCIBCFinalPRoject.navigate().to(home);
        gettingCategoriesWiseInformationFromCIBC(driverForCIBCFinalPRoject, "Business", 7, result, CSVWriterObjForCategoryExtraction);

        CSVWriterObjForCategoryExtraction.close();

        driverForCIBCFinalPRoject.navigate().to(home);

        driverForCIBCFinalPRoject.quit();

        return result;
    }
}

public class DataValidation {

    // Task 1: Email Validation
    public static boolean validateEmails(String email) {
        // Regex for validating email addresses
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        // Lists to store valid and invalid emails
        boolean isValid = false;

        if (email.isEmpty()) {
            System.out.println("Email cannot be empty");
            return isValid;
        }

        Matcher matcher = emailPattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;
    }

    // Phone Number Validation
    public static boolean validatePhoneNumbers(String phoneNumber) {
        String phoneRegex = "(?:\\+\\d{1,3}\\s*)?(?:\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4})";
        Pattern phonePattern = Pattern.compile(phoneRegex);
        boolean isValid = false;

        if (phoneNumber.isEmpty()) {
            System.out.println("Phone number cannot be empty");
            return isValid;
        }

        Matcher matcher = phonePattern.matcher(phoneNumber);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;
    }
}


public class FeatureHandler implements HttpHandler {
    private final String featureName;
    private final String COMBINE_CSV_FILE_PATH = "/Users/prakashraghu/scraping/combineCSV.csv";

    public FeatureHandler(String name) {
        this.featureName = name;
    }

    public boolean isValidURL(String txt, boolean web) {
        String urlRegex = "https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/[^\\\\s]*)?";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcherForA04HP = pattern.matcher(txt); // checks for valid url
        if (web) return matcherForA04HP.find();
        else return matcherForA04HP.find() || txt.contains("http") || txt.contains("//") || txt.contains(":") || txt.contains(".");
    }

    public boolean isValidNumber(String txt) {
        String numberRegex = "\\d+";
        Pattern pattern = Pattern.compile(numberRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcherForA04HP = pattern.matcher(txt); // checks for valid number
        return matcherForA04HP.find();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS - Policy issue handler
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        if ("POST".equals(exchange.getRequestMethod())) {
            JSONObject result = new JSONObject();
            try {
                result = getResultFromRequest(exchange);
            } catch (CsvValidationException e) {
                System.out.println(e.getMessage());
            }

            String response = result.toString();

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");

            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    private JSONObject getResultFromRequest(HttpExchange exchange) throws IOException, CsvValidationException {
        JSONObject responseJSON = new JSONObject();
        InputStream is = exchange.getRequestBody();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        JSONObject json = new JSONObject(sb.toString());

        // word completion feature is captured and handled here
        if (Objects.equals(this.featureName, "wordCompletion")) {
            String word = json.getString("query");
            int resultsLimit = json.getInt("limit");

            if (word == null || word.trim().isEmpty() || isValidURL(word, false) || isValidNumber(word)) {
                System.out.println("Word cannot be empty or Invalid word");
                responseJSON.put("result", "invalid");
                return responseJSON;
            }

            if (resultsLimit < 0) {
                resultsLimit = 5;
            }

            // the word completion algorithm is executed to get the required word suggestions in order to get the results of suggestions
            WordCompletion wordCompletion = new WordCompletion(this.COMBINE_CSV_FILE_PATH);

            List<String> suggestions = wordCompletion.getWordSuggestions(word, resultsLimit);

            ListIterator<String> iterator = suggestions.listIterator();
            while (iterator.hasNext()) {
                String suggestion = iterator.next();
                if (suggestion.contains(",")) {
                    iterator.remove();
                } else if (suggestion.contains(".")) {
                    suggestion = suggestion.replace(".","");
                    iterator.set(suggestion);
                }

            }

            responseJSON.put("suggestions", new JSONArray(suggestions));
        }
        // frequency count feature is handled here
        else if (Objects.equals(this.featureName, "frequencyCount")) {
            FrequencyCount frequencyCount = new FrequencyCount();
            responseJSON.put("frequencies", frequencyCount.performFrequencyCount(""));
        }
        // spell check is handled here
        else if (Objects.equals(this.featureName, "spellCheck")) {
            String word = json.getString("query");
            if (word == null || word.trim().isEmpty() || isValidURL(word, false) || isValidNumber(word)) {
                System.out.println("Word cannot be empty or Invalid word");
                responseJSON.put("result", "invalid");
                return responseJSON;
            }
            SpellCheck spellCheck = new SpellCheck();
            String spellings = spellCheck.checkSpelling(word);
            responseJSON.put("spelling", spellings);
        }
        // search frequency is handled here
        else if (Objects.equals(this.featureName, "searchFrequency")) {
            String word = json.getString("query");
            boolean isTop = json.getBoolean("isTop");
            SearchFrequency searchFrequency = new SearchFrequency();
            if (isTop) {
                JSONObject topResults = searchFrequency.topQueries(4);
                responseJSON.put("top_items", topResults);
            } else {
                if (word == null || word.trim().isEmpty() || isValidNumber(word) || isValidURL(word, false)) {
                    System.out.println("Word cannot be empty or Invalid word");
                    responseJSON.put("result", "invalid");
                    return responseJSON;
                }
                searchFrequency.checkRestore(word);
                responseJSON.put("frequency", searchFrequency.find(word));
                responseJSON.put("word", word);
            }
        }
        // page ranking is handled here
        else if (Objects.equals(this.featureName, "pageRanking")) {
            String word = json.getString("query");
            if (word == null || word.trim().isEmpty() || isValidURL(word, false) || isValidNumber(word)) {
                System.out.println("Word cannot be empty or Invalid word");
                responseJSON.put("result", "invalid");
                return responseJSON;
            }
            PageRanking pageRanking = new PageRanking();
            JSONArray temp = pageRanking.rank(word);
            if (temp.isEmpty()) {
                responseJSON.put("ranking", "not-found");
            } else {
                responseJSON.put("ranking", temp);
            }
        } else if (Objects.equals(this.featureName, "combine")) { // combination of all csv files
            combineCSVFiles();
            responseJSON.put("result", "completed");
        } else if (Objects.equals(this.featureName, "dataExtraction")) { // data extraction handled here
            String message = json.getString("query");
            if (message == null || message.trim().isEmpty()) {
                System.out.println("Message cannot be empty or Invalid message");
                responseJSON.put("result", "invalid");
                return responseJSON;
            }
            List<String> emails = PatternFinder.extractEmails(message);
            JSONArray items = new JSONArray();
            for (String email: emails) {
                items.put(email);
            }
            List<String> phoneNumbers = PatternFinder.extractPhoneNumbers(message);
            JSONArray mobiles = new JSONArray();
            for (String phone: phoneNumbers) {
                mobiles.put(phone);
            }
            List<String> urls = PatternFinder.extractURLsForA4(message);
            JSONArray urlArr = new JSONArray();
            for (String url: urls) {
                urlArr.put(url);
            }
            responseJSON.put("email", items);
            responseJSON.put("phone", mobiles);
            responseJSON.put("url", urlArr);
        } else if (Objects.equals(this.featureName, "emailValidate")) { // email validation is handled here
            String email = json.getString("query");
            if (email == null || email.trim().isEmpty()) {
                System.out.println("Email cannot be empty or Invalid email");
                responseJSON.put("result", "invalid");
                return responseJSON;
            }
            boolean isValid = DataValidation.validateEmails(email);
            responseJSON.put("isvalid", isValid);
        } else if (Objects.equals(this.featureName, "phoneValidate")) { // phone validation is handled here
            String phone = json.getString("query");
            if (phone == null || phone.trim().isEmpty() || phone.trim().split("\\s+").length > 1) {
                System.out.println("Phone cannot be empty or Invalid phone");
                responseJSON.put("result", "invalid");
                return responseJSON;
            }
            boolean isValid = DataValidation.validatePhoneNumbers(phone);
            responseJSON.put("isvalid", isValid);
        } else if (Objects.equals(this.featureName, "invertedIndexing")) { // inverted indexing is handled here
            String query = json.getString("query");
            if (query == null || query.trim().isEmpty() || isValidURL(query, false) || isValidNumber(query)) {
                System.out.println("Query cannot be empty or Invalid query");
                responseJSON.put("result", "invalid");
                return responseJSON;
            }
            InvertedIndexing invertedIndexing = new InvertedIndexing();
            Map<String, Integer> result = invertedIndexing.mainProcess(query);
            if (result.isEmpty()) {
                responseJSON.put("result", "not-found");
            } else {
                JSONArray resultObj = new JSONArray();
                for (Map.Entry<String, Integer> entry : result.entrySet()) {
                    JSONObject item = new JSONObject();
                    item.put("link", entry.getKey());
                    item.put("frequency", entry.getValue());
                    resultObj.put(item);
                }
                responseJSON.put("result", resultObj);
            }
        } // getting card information is handled here
        else if (Objects.equals(this.featureName, "getCard")) {
            String query = json.getString("query");
            if (query == null || query.trim().isEmpty() || isValidURL(query, false) || isValidNumber(query)) {
                System.out.println("Query cannot be empty or Invalid query");
                responseJSON.put("result", "invalid");
                return responseJSON;
            }
            WordCompletion wordCompletion = new WordCompletion(this.COMBINE_CSV_FILE_PATH);
            CreditCardModel result = wordCompletion.getCardByName(query);
            if (result == null) {
                responseJSON.put("result", "not-found");
            } else {
                JSONObject cardItem = new JSONObject();
                cardItem.put("category", result.getCategory());
                cardItem.put("title", result.getTitle());
                cardItem.put("image_url", result.getImage());
                cardItem.put("annual_fee", result.getAnnualFee());
                cardItem.put("purchase_fee", result.getPurchaseFee());
                cardItem.put("link", result.getLink());
                responseJSON.put("result", cardItem);
            }
        }
        else if (Objects.equals(this.featureName, "webCrawler")) { // web crawler is handled here

            WebCrawler webCrawler = new WebCrawler();
            String query = json.getString("url");

            if (query == null || query.trim().isEmpty() || !isValidURL(query, true) || isValidNumber(query)) {
                System.out.println("URL cannot be empty or Invalid url");
                responseJSON.put("result", "invalid");
                return responseJSON;
            }
            JSONArray temp = webCrawler.startCrawling(query);
            if (temp.isEmpty()) {
                responseJSON.put("result", "not-found");
            } else {
                responseJSON.put("result", temp);
            }
        }

        return responseJSON;

    }

    /**
     * Used to combine csv list of files to one single file
     */
    public void combineCSVFiles() {
        int ctrl = 0;
        // handles any number of csv files by adding , with file names
        List<String[]> allRows = new ArrayList<>();
        allRows.add(new String[]{"Category","Card Name","Image","Annual Fee","Purchase Fee","Link"});
        String[] files = { "/Users/prakashraghu/scraping/rbc.csv", "/Users/prakashraghu/scraping/cibc.csv", "/Users/prakashraghu/scraping/td.csv", "/Users/prakashraghu/scraping/nbc.csv", "/Users/prakashraghu/scraping/scotia.csv" };
        while (ctrl <= 4) {
            try {
                readAndCombineCSVFile(files[ctrl], allRows);
                ctrl++;
            } catch (IOException exp) {
                System.out.println(exp.getMessage());
            }
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter(this.COMBINE_CSV_FILE_PATH))) {
            writer.writeAll(allRows);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void readAndCombineCSVFile(String filePath, List<String[]> allRows) throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            int i = 0;
            while ((nextLine = reader.readNext()) != null) {
                if (i!=0 && !Arrays.toString(nextLine).isEmpty()) {
                    allRows.add(nextLine);
                }
                i++;
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}


public class FrequencyCount {

    private String COMBINE_CSV_FILE_PATH = "/Users/prakashraghu/scraping/combineCSV.csv";

    public FrequencyCount() {}

    public boolean isNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        // Remove common currency symbols and whitespace
        str = str.replaceAll("[\\s,$€£¥₹]", "");

        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public JSONArray performFrequencyCount(String patternToSearch) throws IOException {
        JSONArray jsonArray = new JSONArray();
        try {
            // Read the concatenated CSV file.
            BufferedReader reader = new BufferedReader(new FileReader(this.COMBINE_CSV_FILE_PATH));

            // Word frequencies are stored in a hash map.
            Map<String, Integer> wordFrequencyObjMapForFC = new HashMap<>();

            // To separate words by non-word characters, use a regex pattern.
            Pattern wordSplitPattern = Pattern.compile("\\W+");

            String line;
            while ((line = reader.readLine()) != null) {
                // Divide the line using the regex pattern into words.
                String[] words = wordSplitPattern.split(line);

                // Determine how often each word occurs.
                for (String word : words) {
                    if (!word.isEmpty() && !word.contains("https") && !isNumber(word) && !word.contains("_")) {
                        // Update the word's frequency in the map and convert it to lowercase.
                        wordFrequencyObjMapForFC.put(word.toLowerCase(), wordFrequencyObjMapForFC.getOrDefault(word.toLowerCase(), 0) + 1);
                    }
                }
            }

            reader.close(); // When all the lines have been read, close the reader.

            // Convert map items to lists in order to arrange them.
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordFrequencyObjMapForFC.entrySet());

            // Use quicksort to sort words by frequency in decreasing order.
            quicksort(entries, 0, entries.size() - 1);

            // Show the top N most common words (for example, the top 10).
            int N = 20;
            if (patternToSearch.isEmpty()) {
                for (int i = 0; i < N && i < entries.size(); i++) {
                    JSONObject tmp = new JSONObject();
                    Map.Entry<String, Integer> entry = entries.get(i);
                    tmp.put("word",entry.getKey());
                    tmp.put("freq", entry.getValue());
                    jsonArray.put(tmp);
                }
            } else {
                boolean foundSomething = false;
                for (int j=0; j<entries.size(); j++) {
                    JSONObject tmp = new JSONObject();
                    Map.Entry<String, Integer> entry = entries.get(j);
                    if (entry.getKey().equals(patternToSearch)) {
                        tmp.put("result", entry.getValue());
                        foundSomething = true;
                    }
                    jsonArray.put(tmp);
                }
                if (!foundSomething) {
                    JSONObject tmp = new JSONObject();
                    tmp.put("result", 0);
                    jsonArray.put(tmp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return jsonArray;
    }
}

public class InvertedIndexing {

    public InvertedIndexing() {}

    private List<Product> products = new ArrayList<>(); // Make a list where Product objects will be kept.
    private Trie invertedIndex = new Trie(); // Construct an inverted index trie.

    public Map<String, Integer> mainProcess(String searchQuery) {
        String csvFile = "/Users/prakashraghu/scraping/combineCSV.csv"; // File path for the CSV
        readProductData(csvFile); // Use this function to call the CSV file and read product info.
        return searchProducts(searchQuery);
    }

    public void readProductData(String csvFile) { // How to get product information from a CSV file
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) { // Construct a CSVReader instance.
            String[] line; // Create an array to hold every line in the CSV file.
            reader.readNext(); // Ignore the row heading

            while ((line = reader.readNext()) != null) { // Go over every line in the CSV file.
                if (line.length >= 6) { // Verify that the line contains the anticipated number of columns.
                    String category = line[0].trim(); // Obtain the category and remove any extra space.
                    String name = line[1].trim(); // Obtain the name and remove any spaces.
                    String imageUrl = line[2].trim(); // Obtain the image's URL and remove any spaces.
                    String annualFee = line[3].trim(); // Obtain the yearly charge and reduce whitespace
                    String purchaseRate = line[4].trim(); // Determine the buy rate and remove any extra space.
                    String link = line[5].trim(); // Obtain the URL and remove any extra space.

                    Product product = new Product(category, name, imageUrl, annualFee, purchaseRate, link); // Generate a fresh Product entity
                    products.add(product); // Include the item in the list of goods
                    insertProductIntoTrie(product); // Add product details to the query
                } else { // Should the line include fewer columns than anticipated
                    System.out.println("Invalid data format: " + Arrays.toString(line)); // Generate an error message
                }
            }
        } catch (IOException | CsvValidationException e) { // Recover from any IO errors.
            System.out.println(e.getMessage());
        }
    }

    private void insertProductIntoTrie(Product product) { // Technique for adding product details to the trie
        String[] attributes = { // Generate a variety of product characteristics
                product.getName(), // Find the name of the product.
                product.getCategory(), // Find the category of the product.
                product.getAnnualFee(), // Find the annual fee of the product.
                product.getPurchaseRate() // Find the purchase rate of the product.
        };

        for (String attribute : attributes) { // Repeat for every attribute.
            String[] tokens = tokenize(attribute); // Make the attribute tokenized
            for (String token : tokens) { // Repeat for every token.
                invertedIndex.insert(token, product.getLink()); // Add the token and the product URL to the trie.
            }
        }
    }

    public Map<String, Integer> searchProducts(String query) { // How to look for items
        List<String> searchKeywords = Arrays.asList(query.toLowerCase().split("\\s+")); // Divide the search term list into keywords.
        Map<String, Integer> frequencyMap = new HashMap<>(); // Make a map to keep track of search results.

        try {
            for (String keyword : searchKeywords) { // Repeat for every keyword.
                List<String> matchedLinks = invertedIndex.search(keyword); // Look up the term in the search from a trie data structure
                for (String link : matchedLinks) { // Repeat for every matching link.
                    frequencyMap.put(link, frequencyMap.getOrDefault(link, 0) + 1); // Increase the link's frequency count.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return frequencyMap; // Provide the search results back.
    }

    private String[] tokenize(String text) { // Text tokenization technique
        return text.toLowerCase().split("\\s+|(?=[^a-zA-Z0-9])|(?<=[^a-zA-Z0-9])"); // Divide text into tokens that can handle decimals and numbers.
    }

    class Product { // Using a product class to symbolize a product
        private String category; // Product category
        private String name; // Product name
        private String imageUrl; // Product image URL
        private String annualFee; // Product annual fee
        private String purchaseRate; // Product purchase rate
        private String link; // Product link

        public Product(String category, String name, String imageUrl, String annualFee, String purchaseRate, String link) { // Constructor
            this.category = category; // Create the category.
            this.name = name; // Create the name
            this.imageUrl = imageUrl; // Create the image URL
            this.annualFee = annualFee; // Create the annual fee
            this.purchaseRate = purchaseRate; // Create the purchase rate
            this.link = link; // Create the link
        }

        public String getCategory() { return category; } // Getter for category
        public String getName() { return name; } // Getter for name
        public String getImageUrl() { return imageUrl; } // Getter for image URL
        public String getAnnualFee() { return annualFee; } // Getter for annual fee
        public String getPurchaseRate() { return purchaseRate; } // Getter for purchase rate
        public String getLink() { return link; } // Getter for link

        @Override
        public String toString() { return "Link: " + link; } // Replace the toString function
    }

    class TrieNode { // TrieNode class to symbolize a trie node
        Map<Character, TrieNode> children = new HashMap<>(); // Map to hold offspring nodes
        List<String> links = new ArrayList<>(); // List to hold the connections connected to this node
    }

    class Trie { // Trie class for the trie's representation
        private final TrieNode root = new TrieNode(); // Node at the root of the tree

        public void insert(String word, String link) { // Technique for adding a word to the trial
            TrieNode node = root; // Begin with the root node.
            for (char c : word.toCharArray()) { // Go through every character in the word once.
                node = node.children.computeIfAbsent(c, k -> new TrieNode()); // Obtain or make the kid node
            }
            node.links.add(link); // Include the node's link.
        }

        public List<String> search(String word) { // How to look up a term in the dictionary
            TrieNode node = root; // Begin with the root node.
            for (char c : word.toCharArray()) { // Go through every character in the word once.
                node = node.children.get(c); // Traverse to the child node
                if (node == null) { // Should the node not be located
                    return Collections.emptyList(); // Give back a null list.
                }
            }
            return node.links; // Provide the node's related connections back.
        }
    }
}


public class Main {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/getSuggestions", new FeatureHandler("wordCompletion"));
        server.createContext("/searchFrequency", new FeatureHandler("searchFrequency"));
        server.createContext("/frequencyCount", new FeatureHandler("frequencyCount"));
        server.createContext("/spellCheck", new FeatureHandler("spellCheck"));
        server.createContext("/rankPage", new FeatureHandler("pageRanking"));
        server.createContext("/invertedIndexing", new FeatureHandler("invertedIndexing"));
        server.createContext("/scrap/rbc", new ScrapHandler("rbc"));
        server.createContext("/scrap/cibc", new ScrapHandler("cibc"));
        server.createContext("/scrap/nbc", new ScrapHandler("nbc"));
        server.createContext("/scrap/scotia", new ScrapHandler("scotia"));
        server.createContext("/scrap/td", new ScrapHandler("td"));
        server.createContext("/bestDeals", new BestDealFinder());
        server.createContext("/combineCSV", new FeatureHandler("combine"));
        server.createContext("/dataExtraction", new FeatureHandler("dataExtraction"));
        server.createContext("/emailValidate", new FeatureHandler("emailValidate"));
        server.createContext("/phoneValidate", new FeatureHandler("phoneValidate"));
        server.createContext("/getCard", new FeatureHandler("getCard"));
        server.createContext("/webCrawling", new FeatureHandler("webCrawler"));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8000");
    }
}


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

public class PageRanking {

    private String COMBINE_CSV_FILE_PATH = "/Users/prakashraghu/scraping/combineCSV.csv";

    public PageRanking() {}
    // A list to keep track of every item
    private List<Product> products = new ArrayList<>();

    public JSONArray rank(String query) {
        JSONArray result = new JSONArray();
        String csvFile = this.COMBINE_CSV_FILE_PATH;
        readProductData(csvFile);

        // Examine your search phrase for any special characters.
        if (query.matches(".*[%$#@%&();].*")) {
            System.out.println("Please enter values without special characters like \nEX: 10, travel");
        }

        // Look for goods that fit the criteria.
        List<Product> rankedProducts = searchProducts(query); // Obtain product rankings according to query

        // Sort products in descending order based on frequency
        rankedProducts.sort((a, b) -> {
            // To ensure sorting stability, sort first by link and then by frequency downwards.
            if (b.getFrequency() != a.getFrequency()) {
                return b.getFrequency() - a.getFrequency(); // Examine frequency differences
            } else {
                return a.getLink().compareTo(b.getLink()); // If the frequencies are same, compare the links.
            }
        });

        // Show the results of your search
        if (rankedProducts.isEmpty()) {
            System.out.println("No results found."); // No products found
        } else {
            // Show links that are sorted according to frequency
            int rank = 1; // Initial rank
            int currentFrequency = rankedProducts.getFirst().getFrequency(); // Highest frequency
            for (Product product : rankedProducts) {
                if (product.getFrequency() < currentFrequency) {
                    rank++; // If frequency drops, increase rank
                    currentFrequency = product.getFrequency(); // Update current frequency
                }
                JSONObject tempItem = new JSONObject();
                tempItem.put("rank", rank);
                tempItem.put("link", product.getLink());
                tempItem.put("frequency", product.getFrequency());
                result.put(tempItem);
            }
        }
        return result;
    }

    // How to get product information from a CSV file
    public void readProductData(String csvFile) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] line;
            // Skip header row
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                if (line.length >= 6) {
                    // Read the CSV line's data fields and cut them.
                    String category = line[0].trim();
                    String name = line[1].trim();
                    String imageUrl = line[2].trim(); // Modify index according to CSV structure
                    String annualFee = line[3].trim();
                    String purchaseRate = line[4].trim();
                    String link = line[5].trim();

                    // Add a fresh Product object to the collection.
                    products.add(new Product(category, name, imageUrl, annualFee, purchaseRate, link));
                } else {
                    System.out.println("Invalid data format: " + Arrays.toString(line)); // Display the message "Invalid data format."
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // How to use the query to search for items
    public List<Product> searchProducts(String query) {
        // Divide the query into keywords, then lowercase them.
        List<String> searchKeywords = Arrays.asList(query.toLowerCase().split("\\s+"));
        Map<String, Product> productMap = new HashMap<>(); // Product mapping to storage frequencies

        try {
            // Calculate keyword frequency by iterating over all products.
            for (Product product : products) {
                int frequency = calculateKeywordFrequency(product, searchKeywords); // Determine the frequency for every product.
                if (frequency > 0) {
                    // If the product is already on the map, update the frequency.
                    if (productMap.containsKey(product.getLink())) {
                        Product existingProduct = productMap.get(product.getLink());
                        existingProduct.setFrequency(existingProduct.getFrequency() + frequency); // Increment frequency
                    } else {
                        // Include a new product with the determined frequency on the map.
                        product.setFrequency(frequency);
                        productMap.put(product.getLink(), product); // Add product to map
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        // Make a list of items that are bundled.
        return new ArrayList<>(productMap.values()); // Return list of products
    }

    // How to determine how frequently a keyword appears in product characteristics
    public int calculateKeywordFrequency(Product product, List<String> searchKeywords) {
        int frequency = 0; // Initialize frequency to 0

        // Determine how often each keyword appears in the product characteristics.
        for (String keyword : searchKeywords) {
            frequency += countOccurrences(product.getName().toLowerCase(), keyword); // Count in name
            frequency += countOccurrences(product.getCategory().toLowerCase(), keyword); // Count in category
            frequency += countOccurrences(product.getAnnualFee().toLowerCase(), keyword); // Count in annual fee
            frequency += countOccurrences(product.getPurchaseRate().toLowerCase(), keyword); // Count in purchase rate
        }

        return frequency; // Return total frequency
    }

    // A helpful way for counting a keyword's occurrences in a text
    private  int countOccurrences(String text, String keyword) {
        int count = 0; // Initialize count to 0
        Pattern pattern; // Pattern to match keywords
        if (isNumericWithSpecialChars(keyword)) {
            // Enhanced pattern to match optional decimal numbers to numerical values, followed by optional special characters
            pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "(\\.\\d+)?[%$]?\\b");
        } else {
            pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b"); // Pattern for regular keywords
        }
        Matcher matcher = pattern.matcher(text); // Matcher to find occurrences
        while (matcher.find()) {
            count++; // Increment count for each match
        }
        return count; // Return total count
    }

    // Helper function to determine whether a string contains optional special characters and is numeric
    private  boolean isNumericWithSpecialChars(String str) {
        // Determine if the string contains the optional special characters $ or %.
        return str.matches("\\d+(\\.\\d+)?[%$]?");
    }

    // Product class to indicate every single product
    class Product {
        private String category; // Product category
        private String name; // Product name
        private String imageUrl; // Image URL
        private String annualFee; // Annual fee
        private String purchaseRate; // Purchase rate
        private String link; // Product link
        private int frequency; // Frequency of keyword occurrence

        // Initialize product properties using the constructor
        public Product(String category, String name, String imageUrl, String annualFee, String purchaseRate, String link) {
            this.category = category;
            this.name = name;
            this.imageUrl = imageUrl;
            this.annualFee = annualFee;
            this.purchaseRate = purchaseRate;
            this.link = link;
            this.frequency = 0; // Initialize frequency to 0
        }

        // Getters and setters for attributes on the product
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getAnnualFee() {
            return annualFee;
        }

        public void setAnnualFee(String annualFee) {
            this.annualFee = annualFee;
        }

        public String getPurchaseRate() {
            return purchaseRate;
        }

        public void setPurchaseRate(String purchaseRate) {
            this.purchaseRate = purchaseRate;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        // To display product details, use the override toString method.
        @Override
        public String toString() {
            return "Frequency: " + frequency + "\n"; // Return frequency for now
        }
    }
}

public class PatternFinder {

    // Regular expression to find URLs
    private static final
    String urlRegexForA4HP = "https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/[^\\\\s]*)?";
    private static final Pattern patternForHPA4 = Pattern.compile(urlRegexForA4HP, Pattern.CASE_INSENSITIVE);

    // Task6: Taking Email Addresses Out of Text
    public static List<String> extractEmails(String text) {
        // Create a list at the beginning to hold the retrieved email addresses.
        List<String> emails = new ArrayList<>();
        if (text == null) {
            System.out.println("Text input cannot be empty");
            return emails;
        }
        // Establish the regular expression for email addresses.
        String emailRegex = "[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}";
        // Construct a Pattern object by assembling the regular expression.
        Pattern emailPattern = Pattern.compile(emailRegex);
        // Construct a Matcher object and use the constructed pattern to search the text.
        Matcher matcher = emailPattern.matcher(text);

        // Extract email addresses from the text and gather them.
        while (matcher.find()) {
            // Compile a list for every matching email address.
            emails.add(matcher.group());
        }

        return emails;
    }

    /**
     * Used to extract url(s) from the given text
     * using regular expressions
     * @param txtStrForA4
     * @return
     */
    public static List<String> extractURLsForA4(String txtStrForA4) {
        List<String> URLsForA04 = new ArrayList<>();
        if (txtStrForA4.isEmpty()) {
            System.out.println("Text input cannot be empty");
            return URLsForA04;
        }
        Matcher matcherForA04HP = patternForHPA4.matcher(txtStrForA4);

        while (matcherForA04HP.find()) {
            URLsForA04.add(matcherForA04HP.group());
        }

        return URLsForA04;
    }

    /**
     * extracting content from csv file and the performing the
     * url finding and returning a list of strings
     * @param filepathForA4
     * @return
     */
    public static List<String> extractURLsFromCSV(String filepathForA4) {
        List<String> allUrls = new ArrayList<>();
        if (filepathForA4.isEmpty()) {
            System.out.println("File Path input cannot be empty");
            return allUrls;
        }
        try (BufferedReader readerFromBuffer04 = new BufferedReader(new FileReader(filepathForA4))) {
            String lineForA04HP;
            while ((lineForA04HP = readerFromBuffer04.readLine()) != null) {
                String[] wrdsHPForA04 = lineForA04HP.split(",");
                for(String wrd: wrdsHPForA04) {
                    allUrls.addAll(extractURLsForA4(wrd));
                }
            }
        } catch (IOException eerrExcepA4) {
            System.out.println(eerrExcepA4.getMessage());
        }

        return allUrls;
    }

    // Find Dates in Text
    public static List<String> findDates(List<String> texts) {
        String dateRegex = "\\b\\d{4}-\\d{2}-\\d{2}\\b";
        Pattern datePattern = Pattern.compile(dateRegex);
        List<String> foundDates = new ArrayList<>();

        for (String text : texts) {
            Matcher matcher = datePattern.matcher(text);
            while (matcher.find()) {
                foundDates.add(matcher.group());
            }
        }

        return foundDates;
    }

    public static List<String> extractPhoneNumbers(String text) {
        List<String> phoneNumbers = new ArrayList<>();
        if (text.isEmpty()) {
            System.out.println("Phone Number input cannot be empty");
            return phoneNumbers;
        }
        // Regular expression to find phone numbers
        String phoneRegex = "(?:\\+\\d{1,3}\\s*)?(?:\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4})";

        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            phoneNumbers.add(matcher.group());
        }

        return phoneNumbers;
    }
}

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


public class ScrapHandler implements HttpHandler {

    private String siteName;

    public ScrapHandler(String website) {
        this.siteName = website;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equals(exchange.getRequestMethod())) {
            JSONArray result = new JSONArray();
            try {
                result = getStrings(exchange);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InterruptedException e) {
                System.out.println(e.getMessage());
            }

            JSONObject responseJSON = new JSONObject();
            responseJSON.put("result", result);

            String response = responseJSON.toString();

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");

            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private JSONArray getStrings(HttpExchange exchange) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        InputStream is = exchange.getRequestBody();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        if (Objects.equals(this.siteName, "rbc")) { // handles rbc bank site scraping
            return RBCScraper.scrapSite();
        } else if (Objects.equals(this.siteName, "cibc")) { // handles cibc bank site scraping
            return CIBCScraper.scrapSite();
        } else if (Objects.equals(this.siteName, "nbc")) { // handles national bank site scraping
            return NBCScraper.scrapSite();
        } else if (Objects.equals(this.siteName, "scotia")) { // handles scotia bank site scraping
            return ScotiaScraper.scrapSite3();
        } else if (Objects.equals(this.siteName, "td")) { // handles td bank site scraping
            return TDScraper.scrapSite();
        } else {
            return null;
        }
    }

}


import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

class FrequencyNode {
    String wrd;
    int frqncy = 1; // default frequency is 1
    int ht = 1; // default height is 1
    FrequencyNode lft, rit;

    FrequencyNode(String wrd) {
        this.wrd = wrd;
        this.lft = null;
        this.rit = null;
    }
}

public class SearchFrequency {

    private String SEARCH_COUNT_CSV_FILE_PATH = "/Users/prakashraghu/searchCount.csv";

    public SearchFrequency() {
        loadFromCSV(SEARCH_COUNT_CSV_FILE_PATH);
    }

    private FrequencyNode rut;

    // gives height of the FrequencyTree
    private int higt(FrequencyNode n) {
        return n == null ? 0 : n.ht;
    }

    private int top(int vl1, int vl2) {
        return vl1 > vl2 ? vl1 : vl2;
    }

    // gives level/balance factor for n
    private int retrieveLevel(FrequencyNode n) {
        return n == null ? 0 : higt(n.lft) - higt(n.rit);
    }

    // rotate the subtree right Rooted with n
    private FrequencyNode rightChildMove(FrequencyNode n) {
        FrequencyNode vp = n.lft;
        FrequencyNode pv = vp.rit;

        // do rotate
        vp.rit = n;
        n.lft = pv;

        // height update
        n.ht = top(higt(n.lft), higt(n.rit)) + 1;
        vp.ht = top(higt(vp.lft), higt(vp.rit)) + 1;

        // returns new root
        return vp;
    }

    // rotate the subtree left Rooted with n
    private FrequencyNode leftChildMove(FrequencyNode n) {
        FrequencyNode vp = n.rit;
        FrequencyNode pv = vp.lft;

        // do rotate
        vp.lft = n;
        n.rit = pv;

        // height update
        n.ht = top(higt(n.lft), higt(n.rit)) + 1;
        vp.ht = top(higt(vp.lft), higt(vp.rit)) + 1;

        // returns new root
        return vp;
    }

    // calls private method putElement
    public void put(String wrd) {
        rut = putElement(rut, wrd);
        saveToCSV(); // Automatically save after insertion
    }

    // put a word in FrequencyTree
    private FrequencyNode putElement(FrequencyNode n, String wrd) {
        // does BST insertion
        if (n == null) {
            return new FrequencyNode(wrd);
        }

        // compares word with current node word
        if (wrd.compareTo(n.wrd) < 0) {
            n.lft = putElement(n.lft, wrd);
        } else if (wrd.compareTo(n.wrd) > 0) {
            n.rit = putElement(n.rit, wrd);
        } else {
            n.frqncy++;
            return n;
        }

        // modifies height of this ancestor node
        n.ht = 1 + top(higt(n.lft), higt(n.rit));

        // take balance factor for this ancestor node to find if this node is unbalanced or not
        int lvl = retrieveLevel(n);

        // 4 cases if this node becomes unbalanced

        // case for left-left
        if (lvl > 1 && wrd.compareTo(n.lft.wrd) < 0) {
            return rightChildMove(n);
        }

        // case for right-right
        if (lvl < -1 && wrd.compareTo(n.rit.wrd) > 0) {
            return leftChildMove(n);
        }

        // case for left-right
        if (lvl > 1 && wrd.compareTo(n.lft.wrd) > 0) {
            n.lft = leftChildMove(n.lft);
            return rightChildMove(n);
        }

        // case for right-left
        if (lvl < -1 && wrd.compareTo(n.rit.wrd) < 0) {
            n.rit = rightChildMove(n.rit);
            return leftChildMove(n);
        }

        // node which is not modified returned
        return n;
    }

    public static boolean queryvldty(String qr) {
        // if word is null or empty after removing trailing spaces or length after
        // split greater than 1 returns false
        if (qr == null || qr.trim().isEmpty() || qr.trim().split("\\s+").length > 1) {
            return false;
        }
        return true;
    }
    // calls checkRestore
    public void checkRestore(String wrd) {

        rut = checkRestore(rut, wrd.trim().toLowerCase());
        saveToCSV(); // Automatically save after insertion
    }

    // checks if word is already present in the tree, if it's present increment its frequency, else add as new
    private FrequencyNode checkRestore(FrequencyNode n, String wrd) {
        if (n == null) {
            return new FrequencyNode(wrd);
        }

        // compares word with current node word
        int cmp = wrd.compareTo(n.wrd);
        if (cmp < 0) {
            n.lft = checkRestore(n.lft, wrd);
        } else if (cmp > 0) {
            n.rit = checkRestore(n.rit, wrd);
        } else {
            n.frqncy++; // if word already present, modify its count
            return n;
        }

        // modifies height of this ancestor node
        n.ht = 1 + top(higt(n.lft), higt(n.rit));

        // take balance factor for this ancestor node to find if this node is unbalanced or not
        int lvl = retrieveLevel(n);

        // 4 cases if this node becomes unbalanced

        // case for left-left
        if (lvl > 1 && wrd.compareTo(n.lft.wrd) < 0) {
            return rightChildMove(n);
        }

        // case for right-right
        if (lvl < -1 && wrd.compareTo(n.rit.wrd) > 0) {
            return leftChildMove(n);
        }

        // case for left-right
        if (lvl > 1 && wrd.compareTo(n.lft.wrd) > 0) {
            n.lft = leftChildMove(n.lft);
            return rightChildMove(n);
        }

        // case for right-left
        if (lvl < -1 && wrd.compareTo(n.rit.wrd) < 0) {
            n.rit = rightChildMove(n.rit);
            return leftChildMove(n);
        }

        // node which is not modified returned
        return n;
    }

    // calls find method
    public int find(String wrd) {
        return find(rut, wrd);
    }

    // looks for a word in FrequencyTree
    private int find(FrequencyNode n, String wrd) {
        if (n == null) {
            return 0; // word not found
        }

        int cmp = wrd.compareTo(n.wrd);
        if (cmp < 0) {
            return find(n.lft, wrd);
        } else if (cmp > 0) {
            return find(n.rit, wrd);
        } else {
            return n.frqncy; // word found, return its count
        }
    }

    public void proper() {
        // calls private method properEle
        properEle(rut);
    }

    // does in-order traversal
    private void properEle(FrequencyNode n) {
        if (n != null) {
            // recursively traverses the left subtree
            properEle(n.lft);
            System.out.println(n.wrd + ": " + n.frqncy);
            // recursively traverses the right subtree
            properEle(n.rit);
        }
    }

    // returns root
    public FrequencyNode getRut() {
        return this.rut;
    }

    // Save the tree to a CSV file
    public void saveToCSV() {
        String filename = "/Users/prakashraghu/searchCount.csv";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            saveToCSV(rut, writer);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Helper method for saveToCSV
    private void saveToCSV(FrequencyNode node, PrintWriter writer) {
        if (node != null) {
            saveToCSV(node.lft, writer);
            writer.println(node.wrd + "," + node.frqncy);
            saveToCSV(node.rit, writer);
        }
    }

    // Load the tree from a CSV file
    public void loadFromCSV(String filename) {
        try (Scanner scanner = new Scanner(new java.io.File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String word = parts[0];
                    int frequency = Integer.parseInt(parts[1]);
                    for (int i = 0; i < frequency; i++) {
                        checkRestore(word);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
    }

    // Display top N queries based on frequency
    public JSONObject topQueries(int n) {
        JSONObject result = new JSONObject();
        PriorityQueue<FrequencyNode> pq = new PriorityQueue<>(n, Comparator.comparingInt(node -> -node.frqncy));
        topQueries(rut, pq, n);
        while (!pq.isEmpty()) {
            FrequencyNode node = pq.poll();
            result.put(node.wrd, node.frqncy);
        }
        return result;
    }

    // Helper method for topQueries
    // depth first search
    private void topQueries(FrequencyNode node, PriorityQueue<FrequencyNode> pq, int n) {
        if (node != null) {
            topQueries(node.lft, pq, n);
            if (pq.size() < n) {
                pq.add(node);
            } else if (node.frqncy > pq.peek().frqncy) {
                pq.poll();
                pq.add(node);
            }
            topQueries(node.rit, pq, n);
        }
    }
}


public class SpellCheck {

    private String COMBINE_CSV_FILE_PATH = "/Users/prakashraghu/scraping/combineCSV.csv";

    public SpellCheck() {}

    public String checkSpelling(String wordToCheck) throws IOException, CsvValidationException {
        Set<String> vocabulary = VocabularyLoader.loadVocabulary(COMBINE_CSV_FILE_PATH);

        CuckooHashTable cuckooHashTable = new CuckooHashTable(vocabulary.size() * 2);
        for (String word : vocabulary) {
            cuckooHashTable.insert(word);
        }

        List<String> suggestions = suggestAlternatives(wordToCheck, vocabulary);
        if (suggestions.isEmpty()) return null;
        return suggestions.getFirst();
    }

    // Class to load vocabulary from text files
    public static class VocabularyLoader {
        public static Set<String> loadVocabulary(String filePath) throws IOException, CsvValidationException {
            Set<String> vocabulary = new HashSet<>();
            CSVReader reader = new CSVReader(new FileReader(filePath));
            String[] linee;
            while ((linee = reader.readNext()) != null) {
                for (String word : linee[0].split(" ")) {
                    vocabulary.add(word.toLowerCase().replace("\"",""));
                }
                for (String word : linee[1].split(" ")) {
                    vocabulary.add(word.toLowerCase().replace("\"",""));
                }
            }
            return vocabulary;
        }
    }

    // Class for Cuckoo Hash Table implementation
    public static class CuckooHashTable {
        private String[] tablee1;
        private String[] tablee2;
        private int size;
        private final int maxxLoop = 50; // Prevent infinite loop

        public CuckooHashTable(int size) {
            this.size = size;
            tablee1 = new String[size];
            tablee2 = new String[size];
        }

        private int hashh1(String keyy) {
            return Math.abs(keyy.hashCode()) % size;
        }

        private int hash2(String keyy) {
            return (Math.abs(keyy.hashCode() / size)) % size;
        }

        public void insert(String keyy) {
            int loopCount = 0;
            while (loopCount < maxxLoop) {
                int poss1 = hashh1(keyy);
                if (tablee1[poss1] == null) {
                    tablee1[poss1] = keyy;
                    return;
                } else {
                    String temp = tablee1[poss1];
                    tablee1[poss1] = keyy;
                    keyy = temp;
                }
                int poss2 = hash2(keyy);
                if (tablee2[poss2] == null) {
                    tablee2[poss2] = keyy;
                    return;
                } else {
                    String temp = tablee2[poss2];
                    tablee2[poss2] = keyy;
                    keyy = temp;
                }
                loopCount++;
            }
            // Resize and rehash if insertion fails repeatedly (not implemented here for simplicity)
        }

        public boolean lookup(String keyy) {
            int poss1 = hashh1(keyy);
            int poss2 = hash2(keyy);
            return keyy.equals(tablee1[poss1]) || keyy.equals(tablee2[poss2]);
        }
    }

    // Method to suggest alternatives for a misspelled word
    public static List<String> suggestAlternatives(String misspelledWord, Set<String> vocabulary) {
        List<String> topSuggestions = new ArrayList<>();
        try {
            List<Map.Entry<String, Integer>> suggestions = new ArrayList<>();
            if (misspelledWord.isEmpty()) {
                System.out.println("Word cannot be empty");
                return topSuggestions;
            }
            if (vocabulary.isEmpty()) {
                System.out.println("Vocabulary cannot be empty");
                return topSuggestions;
            }
            for (String word : vocabulary) {
                int distance = EditDistance.levenshteinDistance(misspelledWord, word);
                suggestions.add(new AbstractMap.SimpleEntry<>(word, distance));
            }
            mergeSort(suggestions, 0, suggestions.size() - 1);

            for (int i = 0; i < 1; i++) {
                if (suggestions.getFirst().getValue() <= 2) {
                    topSuggestions.add(suggestions.get(i).getKey());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return topSuggestions;
    }
}

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

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class WebCrawler {

    private Queue<String> queue;
    private Set<String> visitedUrls;

    public WebCrawler() {
        queue = new LinkedList<>();
        visitedUrls = new HashSet<>();
    }

    /**
     * Used to crawl through websites based on the provided site using jsoup
     * @param startUrl
     * @return
     */
    public JSONArray startCrawling(String startUrl) {
        JSONArray result = new JSONArray();
        if (startUrl.isEmpty()) {
            System.out.println("URL cannot be empty");
            return result;
        }
        queue.add(startUrl);
        visitedUrls.add(startUrl);

        while (!queue.isEmpty() && visitedUrls.size()<=30) {
            String url = queue.poll();
            System.out.println("Crawling: " + url);
            try {
                Document document = Jsoup.connect(url).get();
                Elements links = document.select("a[href]");
                for (Element link : links) {
                    String absUrl = link.absUrl("href");
                    if (!visitedUrls.contains(absUrl) && absUrl.startsWith("http")) {
                        queue.add(absUrl);
                        System.out.println("Crawling: "+absUrl);
                        visitedUrls.add(absUrl);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error fetching the URL: " + url);
                System.err.println(e.getMessage());
            }
        }

        System.out.println("Crawling completed. Visited URLs:");
        for (String visitedUrl : visitedUrls) {
            result.put(visitedUrl);
        }
        return result;
    }

}


public class WordCompletion {

    private AVLTree avlTree;
    private String filePath;

    /**
     * constructor which requires the data set file
     * which is used to load the necessary vocabulary
     * @param filePath
     */
    public WordCompletion(String filePath) {
        this.avlTree = loadVocabulary(filePath);
        this.filePath = filePath;
    }

    /**
     * Used to check whether the given string is a url or not
     * @param str
     * @return true or false
     */
    public boolean isValidUrl(String str) {
        return str.contains("https://");
    }

    /**
     * Used to check if the given string is a number or not
     * it also handles numbers that has prefix of currency symbols
     * @param str
     * @return true or false
     */
    public boolean isNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        // Remove common currency symbols and whitespace
        str = str.replaceAll("[\\s,$€£¥₹]", "");

        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Used to extract all possible vocabulary from the input file (csv) as a file path
     * and then loaded into a data structure (AVL Tree) to keep track of the frequency and its words
     * respectively. here all the words that are loaded are made to lower case before entering into the tree.
     * @param filePath
     * @return AVLTree
     */
    public AVLTree loadVocabulary(String filePath)  {
        AVLTree avlTree = new AVLTree();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                String[] values = nextLine.split(",");

                for (String word : values) {
                    // avoids url and numbers
                    if (!isNumber(word) && !isValidUrl(word) && word.length() >= 3) {
                        avlTree.root = avlTree.insert(avlTree.root, word.toLowerCase().replace("\"",""));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return avlTree;
    }

    /**
     * Used to get the list of suggestions from the AVL Tree based on the given word
     * @param word
     * @param resultLimit
     * @return
     */
    public List<String> getWordSuggestions(String word, int resultLimit) {
        if (word.isEmpty()) {
            System.out.println("Word cannot be empty");
            return new ArrayList<>();
        }
        if (resultLimit < 0) {
            resultLimit = 5;
        }
        // the suggestion algorithm runs and provides a list of strings that matches closely with the given word
        return avlTree.autocompleteWithRanking(word, resultLimit);
    }

    public CreditCardModel getCardByName(String word) {
        CreditCardModel result = null;
        try (CSVReader reader = new CSVReader(new FileReader(this.filePath))) {
            String[] line;
            // Skip header row
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                if (line.length >= 6) {
                    // Read the CSV line's data fields and cut them.
                    String category = line[0].trim();
                    String name = line[1].trim();
                    String imageUrl = line[2].trim(); // Modify index according to CSV structure
                    String annualFee = line[3].trim();
                    String purchaseRate = line[4].trim();
                    String link = line[5].trim();

                    if (name.replaceAll("\\.","").equalsIgnoreCase(word.replaceAll("\\.",""))) {
                        result = new CreditCardModel(category, name, imageUrl, annualFee, purchaseRate, new ArrayList<>(), link);
                    }

                } else {
                    System.out.println("Invalid data format: " + Arrays.toString(line)); // Display the message "Invalid data format."
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

}
