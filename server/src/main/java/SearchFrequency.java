
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

class FrequencyNode {
    String wrd;
    int frqncy = 1; // default frequency is 1
    int ht = 1; // default height is 1
    FrequencyNode lft, rit;

    FrequencyNode(String wrd) {
        this.wrd = wrd;
        this.lft = null;
        this.rit = null;
    }
}

public class SearchFrequency {

    private String SEARCH_COUNT_CSV_FILE_PATH = "/Users/prakashraghu/searchCount.csv";

    public SearchFrequency() {
        loadFromCSV(SEARCH_COUNT_CSV_FILE_PATH);
    }

    private FrequencyNode rut;

    // gives height of the FrequencyTree
    private int higt(FrequencyNode n) {
        return n == null ? 0 : n.ht;
    }

    private int top(int vl1, int vl2) {
        return vl1 > vl2 ? vl1 : vl2;
    }

    // gives level/balance factor for n
    private int retrieveLevel(FrequencyNode n) {
        return n == null ? 0 : higt(n.lft) - higt(n.rit);
    }

    // rotate the subtree right Rooted with n
    private FrequencyNode rightChildMove(FrequencyNode n) {
        FrequencyNode vp = n.lft;
        FrequencyNode pv = vp.rit;

        // do rotate
        vp.rit = n;
        n.lft = pv;

        // height update
        n.ht = top(higt(n.lft), higt(n.rit)) + 1;
        vp.ht = top(higt(vp.lft), higt(vp.rit)) + 1;

        // returns new root
        return vp;
    }

    // rotate the subtree left Rooted with n
    private FrequencyNode leftChildMove(FrequencyNode n) {
        FrequencyNode vp = n.rit;
        FrequencyNode pv = vp.lft;

        // do rotate
        vp.lft = n;
        n.rit = pv;

        // height update
        n.ht = top(higt(n.lft), higt(n.rit)) + 1;
        vp.ht = top(higt(vp.lft), higt(vp.rit)) + 1;

        // returns new root
        return vp;
    }

    // calls private method putElement
    public void put(String wrd) {
        rut = putElement(rut, wrd);
        saveToCSV(); // Automatically save after insertion
    }

    // put a word in FrequencyTree
    private FrequencyNode putElement(FrequencyNode n, String wrd) {
        // does BST insertion
        if (n == null) {
            return new FrequencyNode(wrd);
        }

        // compares word with current node word
        if (wrd.compareTo(n.wrd) < 0) {
            n.lft = putElement(n.lft, wrd);
        } else if (wrd.compareTo(n.wrd) > 0) {
            n.rit = putElement(n.rit, wrd);
        } else {
            n.frqncy++;
            return n;
        }

        // modifies height of this ancestor node
        n.ht = 1 + top(higt(n.lft), higt(n.rit));

        // take balance factor for this ancestor node to find if this node is unbalanced or not
        int lvl = retrieveLevel(n);

        // 4 cases if this node becomes unbalanced

        // case for left-left
        if (lvl > 1 && wrd.compareTo(n.lft.wrd) < 0) {
            return rightChildMove(n);
        }

        // case for right-right
        if (lvl < -1 && wrd.compareTo(n.rit.wrd) > 0) {
            return leftChildMove(n);
        }

        // case for left-right
        if (lvl > 1 && wrd.compareTo(n.lft.wrd) > 0) {
            n.lft = leftChildMove(n.lft);
            return rightChildMove(n);
        }

        // case for right-left
        if (lvl < -1 && wrd.compareTo(n.rit.wrd) < 0) {
            n.rit = rightChildMove(n.rit);
            return leftChildMove(n);
        }

        // node which is not modified returned
        return n;
    }

    public static boolean queryvldty(String qr) {
        // if word is null or empty after removing trailing spaces or length after
        // split greater than 1 returns false
        if (qr == null || qr.trim().isEmpty() || qr.trim().split("\\s+").length > 1) {
            return false;
        }
        return true;
    }
    // calls checkRestore
    public void checkRestore(String wrd) {

        rut = checkRestore(rut, wrd.trim().toLowerCase());
        saveToCSV(); // Automatically save after insertion
    }

    // checks if word is already present in the tree, if it's present increment its frequency, else add as new
    private FrequencyNode checkRestore(FrequencyNode n, String wrd) {
        if (n == null) {
            return new FrequencyNode(wrd);
        }

        // compares word with current node word
        int cmp = wrd.compareTo(n.wrd);
        if (cmp < 0) {
            n.lft = checkRestore(n.lft, wrd);
        } else if (cmp > 0) {
            n.rit = checkRestore(n.rit, wrd);
        } else {
            n.frqncy++; // if word already present, modify its count
            return n;
        }

        // modifies height of this ancestor node
        n.ht = 1 + top(higt(n.lft), higt(n.rit));

        // take balance factor for this ancestor node to find if this node is unbalanced or not
        int lvl = retrieveLevel(n);

        // 4 cases if this node becomes unbalanced

        // case for left-left
        if (lvl > 1 && wrd.compareTo(n.lft.wrd) < 0) {
            return rightChildMove(n);
        }

        // case for right-right
        if (lvl < -1 && wrd.compareTo(n.rit.wrd) > 0) {
            return leftChildMove(n);
        }

        // case for left-right
        if (lvl > 1 && wrd.compareTo(n.lft.wrd) > 0) {
            n.lft = leftChildMove(n.lft);
            return rightChildMove(n);
        }

        // case for right-left
        if (lvl < -1 && wrd.compareTo(n.rit.wrd) < 0) {
            n.rit = rightChildMove(n.rit);
            return leftChildMove(n);
        }

        // node which is not modified returned
        return n;
    }

    // calls find method
    public int find(String wrd) {
        return find(rut, wrd);
    }

    // looks for a word in FrequencyTree
    private int find(FrequencyNode n, String wrd) {
        if (n == null) {
            return 0; // word not found
        }

        int cmp = wrd.compareTo(n.wrd);
        if (cmp < 0) {
            return find(n.lft, wrd);
        } else if (cmp > 0) {
            return find(n.rit, wrd);
        } else {
            return n.frqncy; // word found, return its count
        }
    }

    public void proper() {
        // calls private method properEle
        properEle(rut);
    }

    // does in-order traversal
    private void properEle(FrequencyNode n) {
        if (n != null) {
            // recursively traverses the left subtree
            properEle(n.lft);
            System.out.println(n.wrd + ": " + n.frqncy);
            // recursively traverses the right subtree
            properEle(n.rit);
        }
    }

    // returns root
    public FrequencyNode getRut() {
        return this.rut;
    }

    // Save the tree to a CSV file
    public void saveToCSV() {
        String filename = "/Users/prakashraghu/searchCount.csv";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            saveToCSV(rut, writer);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Helper method for saveToCSV
    private void saveToCSV(FrequencyNode node, PrintWriter writer) {
        if (node != null) {
            saveToCSV(node.lft, writer);
            writer.println(node.wrd + "," + node.frqncy);
            saveToCSV(node.rit, writer);
        }
    }

    // Load the tree from a CSV file
    public void loadFromCSV(String filename) {
        try (Scanner scanner = new Scanner(new java.io.File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String word = parts[0];
                    int frequency = Integer.parseInt(parts[1]);
                    for (int i = 0; i < frequency; i++) {
                        checkRestore(word);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        }
    }

    // Display top N queries based on frequency
    public JSONObject topQueries(int n) {
        JSONObject result = new JSONObject();
        PriorityQueue<FrequencyNode> pq = new PriorityQueue<>(n, Comparator.comparingInt(node -> -node.frqncy));
        topQueries(rut, pq, n);
        while (!pq.isEmpty()) {
            FrequencyNode node = pq.poll();
            result.put(node.wrd, node.frqncy);
        }
        return result;
    }

    // Helper method for topQueries
    // depth first search
    private void topQueries(FrequencyNode node, PriorityQueue<FrequencyNode> pq, int n) {
        if (node != null) {
            topQueries(node.lft, pq, n);
            if (pq.size() < n) {
                pq.add(node);
            } else if (node.frqncy > pq.peek().frqncy) {
                pq.poll();
                pq.add(node);
            }
            topQueries(node.rit, pq, n);
        }
    }
}
