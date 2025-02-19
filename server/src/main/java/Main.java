import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
