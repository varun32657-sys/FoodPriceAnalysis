package com.foodprice.features;

import com.foodprice.model.Product;
import java.util.*;

public class InvertedIndex {

    private Map<String, List<Product>> index;

    public InvertedIndex(List<Product> products) {
        index = new HashMap<>();
        build(products);
    }

    private void build(List<Product> products) {
        for (Product p : products) {
            String[] words = (p.getName() + " " + p.getCategory() + " " + p.getStore())
                .toLowerCase().split("\\s+");
            Set<String> seen = new HashSet<>();
            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                if (word.length() > 1 && !seen.contains(word)) {
                    index.computeIfAbsent(word, k -> new ArrayList<>()).add(p);
                    seen.add(word);
                }
            }
        }
    }

    public List<Product> search(String query) {
        String[] words = query.toLowerCase().split("\\s+");
        if (words.length == 0) return new ArrayList<>();

        Set<Product> result = null;
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z0-9]", "");
            List<Product> matches = index.getOrDefault(word, new ArrayList<>());
            Set<Product> matchSet = new HashSet<>(matches);
            if (result == null) {
                result = matchSet;
            } else {
                result.retainAll(matchSet);
            }
        }
        return result == null ? new ArrayList<>() : new ArrayList<>(result);
    }

    public List<Product> searchAny(String query) {
        String[] words = query.toLowerCase().split("\\s+");
        Set<Product> result = new HashSet<>();
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z0-9]", "");
            List<Product> matches = index.getOrDefault(word, new ArrayList<>());
            result.addAll(matches);
        }
        return new ArrayList<>(result);
    }

    public Map<String, List<Product>> getIndex() {
        return Collections.unmodifiableMap(index);
    }

    public int getIndexSize() {
        return index.size();
    }
}