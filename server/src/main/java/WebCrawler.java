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