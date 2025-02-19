import java.util.*;

/**
 * Basic Tree Node class with all its required elements
 * String word -> the word that needs to stored as value
 * int frequency -> the occurrence of the word
 * int height -> height of the node of the tree (level)
 * Node left -> left child of the current node
 * Node right -> right child of the current node
 */
class Node {
    String word;
    int frequency;
    int height;
    Node left;
    Node right;

    // public constructor to instantiate
    public Node(String word) {
        this.word = word;
        this.frequency = 1;
        this.height = 1;
    }
}

/**
 * Data Structure (AVL Tree)
 * Basic implementation of AVL Tree and other required methods based on the usage
 * insert -> Used to insert a node to the AVL tree, and it makes sures balance of the tree as well
 * leftRotate -> Used to perform left rotation when needed during removal or any operation on the tree
 * rightRotate -> Used to perform right rotation when needed during insertion or any operation on the tree
 * balanceFactor -> Used to calculate the balance factor of the node
 * searchPrefix -> Search for the node with a given prefix provided
 * collectWords -> Collect words from the subtree that match with the given prefix
 * autocomplete -> Returns a list of words with their frequencies that match a provided prefix
 * autocompleteWithRanking -> Returns a list of Top N words that match given prefix, ranked by frequency
 */
public class AVLTree {
    Node root;

    int height(Node node) {
        if (node == null) return 0;
        return node.height;
    }

    int balanceFactor(Node node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    Node rightRotate(Node node) {
        Node x = node.left;
        Node T2 = x.right;
        x.right = node;
        node.left = T2;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return x;
    }

    Node leftRotate(Node x) {
        Node node = x.right;
        Node T2 = node.left;
        node.left = x;
        x.right = T2;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        return node;
    }

    Node insert(Node node, String word) {
        if (node == null) return new Node(word);
        if (word.compareTo(node.word) < 0)
            node.left = insert(node.left, word);
        else if (word.compareTo(node.word) > 0)
            node.right = insert(node.right, word);
        else {
            node.frequency++;
            return node;
        }
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        int balance = balanceFactor(node);
        if (balance > 1 && word.compareTo(node.left.word) < 0)
            return rightRotate(node);
        if (balance < -1 && word.compareTo(node.right.word) > 0)
            return leftRotate(node);
        if (balance > 1 && word.compareTo(node.left.word) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && word.compareTo(node.right.word) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    Node searchPrefix(Node node, String prefix) {
        if (node == null) return null;
        if (node.word.startsWith(prefix)) return node;
        if (prefix.compareTo(node.word) < 0) return searchPrefix(node.left, prefix);
        return searchPrefix(node.right, prefix);
    }

    void collectWords(Node node, String prefix, List<Map.Entry<String, Integer>> words) {
        if (node == null) return;
        if (node.word.startsWith(prefix)) words.add(new AbstractMap.SimpleEntry<>(node.word, node.frequency));
        collectWords(node.left, prefix, words);
        collectWords(node.right, prefix, words);
    }

    List<Map.Entry<String, Integer>> autocomplete(String prefix) {
        List<Map.Entry<String, Integer>> words = new ArrayList<>();
        if (prefix == null) {
            System.out.println("Prefix cannot be null");
            return words;
        }
        Node matchedNode = searchPrefix(root, prefix);
        if (matchedNode != null) collectWords(matchedNode, prefix, words);
        return words;
    }

    List<String> autocompleteWithRanking(String prefix, int topN) {
        List<String> topSuggestions = new ArrayList<>();
        try {
            if (prefix == null) {
                System.out.println("Prefix cannot be null");
                return topSuggestions;
            }
            if (topN < 0) {
                System.out.println("topN cannot be negative");
                return topSuggestions;
            }
            List<Map.Entry<String, Integer>> words;
            try {
                words = autocomplete(prefix); // gets a list of words that matches the prefix provided
            } catch (Exception e) {
                System.out.println("Error retrieving autocomplete suggestions - "+e.getMessage());
                return topSuggestions;
            }
            if (words == null) {
                System.out.println("Autocomplete suggestions cannot be null");
                return topSuggestions;
            }
            PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
            for (Map.Entry<String, Integer> entry : words) {
                maxHeap.offer(entry);
                if (maxHeap.size() > topN) maxHeap.poll();
            }
            while (!maxHeap.isEmpty()) topSuggestions.add(maxHeap.poll().getKey());
//        Collections.reverse(topSuggestions); // highest frequency first - list in reverse order
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return topSuggestions;
    }
}