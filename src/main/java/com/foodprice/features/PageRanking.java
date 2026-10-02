package com.foodprice.features;

import com.foodprice.model.Product;
import java.util.*;

public class PageRanking {

    public List<Product> rank(List<Product> products, String query) {
        if (products == null || products.isEmpty()) return new ArrayList<>();

        String queryLower = query.toLowerCase();
        String[] queryWords = queryLower.split("\\s+");

        Map<Product, Double> scoreMap = new LinkedHashMap<>();
        for (Product p : products) {
            if (p == null) continue;
            double score = calculateScore(p, queryWords);
            scoreMap.put(p, score);
        }

        List<Map.Entry<Product, Double>> entries = new ArrayList<>(scoreMap.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Product> ranked = new ArrayList<>();
        for (Map.Entry<Product, Double> e : entries) {
            ranked.add(e.getKey());
        }
        return ranked;
    }

    private double calculateScore(Product p, String[] queryWords) {
        if (p == null || p.getName() == null) return 0;
        double score = 0;
        String nameLower     = p.getName().toLowerCase();
        String categoryLower = p.getCategory() != null ? p.getCategory().toLowerCase() : "";

        for (String word : queryWords) {
            if (word == null || word.isEmpty()) continue;
            if (nameLower.equals(word))          score += 10;
            else if (nameLower.contains(word))   score += 5;
            if (categoryLower.contains(word))    score += 3;
            int idx = 0;
            while ((idx = nameLower.indexOf(word, idx)) != -1) {
                score += 2;
                idx += word.length();
            }
        }
        if (p.getPrice() < 5) score += 1;
        return score;
    }

    public List<Product> getTopCheapest(List<Product> products, int n) {
        if (products == null || products.isEmpty()) return new ArrayList<>();
        PriorityQueue<Product> minHeap = new PriorityQueue<>(
            Comparator.comparingDouble(Product::getPrice));
        for (Product p : products) {
            if (p != null) minHeap.add(p);
        }
        List<Product> result = new ArrayList<>();
        int count = 0;
        while (!minHeap.isEmpty() && count < n) {
            result.add(minHeap.poll());
            count++;
        }
        return result;
    }
}
