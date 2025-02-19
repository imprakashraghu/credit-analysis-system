import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class FrequencyCount {

    private String COMBINE_CSV_FILE_PATH = "/Users/prakashraghu/scraping/combineCSV.csv";

    public FrequencyCount() {}

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
     * Used to get frequency count of top words from files
     * @param patternToSearch
     * @return
     * @throws IOException
     */
    public JSONArray performFrequencyCount(String patternToSearch) throws IOException {
        JSONArray jsonArray = new JSONArray();
        try {
            // Read the concatenated CSV file.
            BufferedReader reader = new BufferedReader(new FileReader(this.COMBINE_CSV_FILE_PATH));

            // Word frequencies are stored in a hash map.
            Map<String, Integer> wordFreq = new HashMap<>();

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
                        wordFreq.put(word.toLowerCase(), wordFreq.getOrDefault(word.toLowerCase(), 0) + 1);
                    }
                }
            }

            reader.close(); // When all the lines have been read, close the reader.

            // Convert map items to lists in order to arrange them.
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordFreq.entrySet());

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

    // Using the Quicksort method, order the elements by frequency
    private static void quicksort(List<Map.Entry<String, Integer>> entries, int low, int high) {
        if (low < high) {
            // Divide the array into parts.
            int pi = partition(entries, low, high);

            // Sort elements recursively both before and after partitioning
            quicksort(entries, low, pi - 1);
            quicksort(entries, pi + 1, high);
        }
    }

    // Quicksort partitioning technique
    private static int partition(List<Map.Entry<String, Integer>> entries, int low, int high) {
        int pivot = entries.get(high).getValue();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (entries.get(j).getValue() >= pivot) {
                i++;
                Collections.swap(entries, i, j); // Swap entries
            }
        }

        Collections.swap(entries, i + 1, high); // Last swap to align pivot in proper location
        return i + 1; // Give the partition index back.
    }
}
