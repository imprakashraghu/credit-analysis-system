import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.*;

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
