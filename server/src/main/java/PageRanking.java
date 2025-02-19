import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageRanking {

    private String COMBINE_CSV_FILE_PATH = "/Users/prakashraghu/scraping/combineCSV.csv";

    public PageRanking() {}
    // A list to keep track of every item
    private List<Product> products = new ArrayList<>();

    /**
     * Used to rank pages in descending order based on the word occurrences in that file or url
     * @param query
     * @return
     */
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
