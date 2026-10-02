package com.foodprice.features;

import com.foodprice.model.Product;
import java.util.*;

public class SpellChecker {

    private Set<String> vocabulary;

    public SpellChecker(List<Product> products) {
        vocabulary = new HashSet<>();
        buildVocabulary(products);
    }

    private void buildVocabulary(List<Product> products) {
        for (Product p : products) {
            String[] words = (p.getName() + " " + p.getCategory())
                .toLowerCase().split("\\s+");
            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                if (word.length() > 1) vocabulary.add(word);
            }
        }
    }

    // Edit distance algorithm
    public int editDistance(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i-1) == b.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i-1][j-1],
                               Math.min(dp[i-1][j], dp[i][j-1]));
                }
            }
        }
        return dp[m][n];
    }

    public boolean isCorrect(String word) {
        return vocabulary.contains(word.toLowerCase());
    }

    // Get suggestions for misspelled word
    public List<String> getSuggestions(String word, int maxSuggestions) {
        word = word.toLowerCase();
        PriorityQueue<String[]> pq = new PriorityQueue<>(
            Comparator.comparingInt(a -> Integer.parseInt(a[1]))
        );
        for (String v : vocabulary) {
            int dist = editDistance(word, v);
            if (dist <= 3) {
                pq.offer(new String[]{v, String.valueOf(dist)});
            }
        }
        List<String> suggestions = new ArrayList<>();
        int count = 0;
        while (!pq.isEmpty() && count < maxSuggestions) {
            suggestions.add(pq.poll()[0]);
            count++;
        }
        return suggestions;
    }

    // Check full query and return corrected version
    public String checkQuery(String query) {
        String[] words = query.toLowerCase().split("\\s+");
        StringBuilder corrected = new StringBuilder();
        for (String word : words) {
            String clean = word.replaceAll("[^a-zA-Z0-9]", "");
            if (clean.isEmpty()) continue;
            if (isCorrect(clean)) {
                corrected.append(clean).append(" ");
            } else {
                List<String> suggestions = getSuggestions(clean, 1);
                if (!suggestions.isEmpty()) {
                    corrected.append(suggestions.get(0)).append(" ");
                } else {
                    corrected.append(clean).append(" ");
                }
            }
        }
        return corrected.toString().trim();
    }
}