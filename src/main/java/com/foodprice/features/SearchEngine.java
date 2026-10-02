package com.foodprice.features;

import com.foodprice.model.Product;
import java.util.*;
import java.util.stream.Collectors;

public class SearchEngine {

    private List<Product> allProducts;
    private Map<String, List<Product>> invertedIndex;

    public SearchEngine(List<Product> products) {
        this.allProducts = products;
        this.invertedIndex = new HashMap<>();
        buildIndex();
    }

    private void buildIndex() {
        for (Product p : allProducts) {
            if (p == null || p.getName() == null) continue;
            String[] words = (p.getName() + " " + p.getCategory()).toLowerCase().split("\\s+");
            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                if (word.length() > 1)
                    invertedIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(p);
            }
        }
    }

    public List<Product> search(String query) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>();
        String q = query.toLowerCase().trim();
        String[] words = q.split("\\s+");

        Map<Product, Double> scoreMap = new HashMap<>();

        for (Product p : allProducts) {
            if (p == null || p.getName() == null) continue;
            double score = 0;
            String name = p.getName().toLowerCase();
            String cat  = p.getCategory() != null ? p.getCategory().toLowerCase() : "";

            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                if (word.isEmpty()) continue;

                // Exact category match — highest priority
                if (cat.equals(word) || cat.equals(q)) score += 20;
                // Category contains word
                else if (cat.contains(word)) score += 10;
                // Exact name match
                if (name.equals(word)) score += 15;
                // Name starts with word
                else if (name.startsWith(word)) score += 8;
                // Name contains word
                else if (name.contains(word)) score += 4;
                // Inverted index match
                List<Product> idx = invertedIndex.getOrDefault(word, new ArrayList<>());
                if (idx.contains(p)) score += 2;
            }

            if (score > 0) scoreMap.put(p, score);
        }

        return scoreMap.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    public List<Product> searchByCategory(String category) {
        return allProducts.stream()
            .filter(p -> p != null && p.getCategory() != null &&
                p.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    public List<Product> searchByStore(String store) {
        return allProducts.stream()
            .filter(p -> p != null && p.getStore() != null &&
                p.getStore().equalsIgnoreCase(store))
            .collect(Collectors.toList());
    }

    public Product getCheapest(List<Product> products) {
        return products.stream()
            .filter(p -> p != null)
            .min(Comparator.comparingDouble(Product::getPrice))
            .orElse(null);
    }

    public Map<String, Product> getCheapestPerStore(List<Product> products) {
        Map<String, Product> map = new HashMap<>();
        for (Product p : products) {
            if (p == null) continue;
            map.merge(p.getStore(), p,
                (ex, nw) -> nw.getPrice() < ex.getPrice() ? nw : ex);
        }
        return map;
    }

    public List<String> getAllCategories() {
        return allProducts.stream()
            .filter(p -> p != null && p.getCategory() != null)
            .map(Product::getCategory)
            .distinct().sorted().collect(Collectors.toList());
    }

    public List<String> getAllStores() {
        return allProducts.stream()
            .filter(p -> p != null && p.getStore() != null)
            .map(Product::getStore)
            .distinct().sorted().collect(Collectors.toList());
    }

    public List<Product> getAllProducts() { return allProducts; }

    public List<Product> filterByPriceRange(List<Product> products, double min, double max) {
        return products.stream()
            .filter(p -> p != null && p.getPrice() >= min && p.getPrice() <= max)
            .collect(Collectors.toList());
    }

    public List<Product> sortByPrice(List<Product> products, boolean asc) {
        return products.stream().filter(p -> p != null)
            .sorted(asc
                ? Comparator.comparingDouble(Product::getPrice)
                : Comparator.comparingDouble(Product::getPrice).reversed())
            .collect(Collectors.toList());
    }

    public List<Product> sortByName(List<Product> products) {
        return products.stream().filter(p -> p != null)
            .sorted(Comparator.comparing(Product::getName))
            .collect(Collectors.toList());
    }
}
