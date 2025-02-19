import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.checkerframework.checker.units.qual.C;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
