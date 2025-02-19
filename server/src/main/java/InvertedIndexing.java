import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

    /**
     * Indexing the search based on the frequency of words that occured in that particular page
     * @param query
     * @return (list of urls and its respective word occurrences)
     */
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
