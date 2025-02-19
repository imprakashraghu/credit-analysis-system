import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

    // Class for Edit Distance calculation
    public static class EditDistance {
        public static int levenshteinDistance(String s11, String s22) {
            int[] previousRow = new int[s22.length() + 1];
            for (int i = 0; i <= s22.length(); i++) {
                previousRow[i] = i;
            }

            for (int i = 0; i < s11.length(); i++) {
                int[] currentRow = new int[s22.length() + 1];
                currentRow[0] = i + 1;

                for (int j = 0; j < s22.length(); j++) {
                    int insertions = previousRow[j + 1] + 1;
                    int deletions = currentRow[j] + 1;
                    int substitutions = previousRow[j] + (s11.charAt(i) == s22.charAt(j) ? 0 : 1);
                    currentRow[j + 1] = Math.min(Math.min(insertions, deletions), substitutions);
                }
                previousRow = currentRow;
            }
            return previousRow[s22.length()];
        }
    }

    // Method to sort suggestions using merge sort
    public static void mergeSort(List<Map.Entry<String, Integer>> list, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(list, left, mid);
            mergeSort(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }

    private static void merge(List<Map.Entry<String, Integer>> list, int left, int mid, int right) {
        List<Map.Entry<String, Integer>> leftList = new ArrayList<>(list.subList(left, mid + 1));
        List<Map.Entry<String, Integer>> rightList = new ArrayList<>(list.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;
        while (i < leftList.size() && j < rightList.size()) {
            if (leftList.get(i).getValue() <= rightList.get(j).getValue()) {
                list.set(k++, leftList.get(i++));
            } else {
                list.set(k++, rightList.get(j++));
            }
        }

        while (i < leftList.size()) {
            list.set(k++, leftList.get(i++));
        }

        while (j < rightList.size()) {
            list.set(k++, rightList.get(j++));
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
