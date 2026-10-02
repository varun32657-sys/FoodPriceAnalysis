package com.foodprice.features;

import com.foodprice.model.Product;
import java.util.*;

public class WordCompletion {

    // Trie Node
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
        String fullWord = "";
        int frequency = 0;
    }

    private TrieNode root;

    public WordCompletion(List<Product> products) {
        root = new TrieNode();
        buildTrie(products);
    }

    private void buildTrie(List<Product> products) {
        for (Product p : products) {
            String[] words = (p.getName() + " " + p.getCategory())
                .toLowerCase().split("\\s+");
            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                if (word.length() > 1) insert(word);
            }
            // Also insert full product name
            insert(p.getName().toLowerCase());
        }
    }

    private void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEnd = true;
        node.fullWord = word;
        node.frequency++;
    }

    // Get suggestions for prefix
    public List<String> getSuggestions(String prefix, int maxResults) {
        prefix = prefix.toLowerCase();
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) return new ArrayList<>();
            node = node.children.get(c);
        }
        List<String[]> results = new ArrayList<>();
        dfs(node, results);
        results.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));
        List<String> suggestions = new ArrayList<>();
        for (int i = 0; i < Math.min(maxResults, results.size()); i++) {
            suggestions.add(results.get(i)[0]);
        }
        return suggestions;
    }

    private void dfs(TrieNode node, List<String[]> results) {
        if (node.isEnd) {
            results.add(new String[]{node.fullWord, String.valueOf(node.frequency)});
        }
        for (TrieNode child : node.children.values()) {
            dfs(child, results);
        }
    }
}