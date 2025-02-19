import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
