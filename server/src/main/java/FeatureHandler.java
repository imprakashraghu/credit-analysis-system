import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.json.Json;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Helper method used to read each csv file
     * @param filePath
     * @param allRows
     * @throws IOException
     */
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
