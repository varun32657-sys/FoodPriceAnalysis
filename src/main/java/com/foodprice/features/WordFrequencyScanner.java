package com.foodprice.features;

import java.util.HashMap;
import java.util.Map;

public class WordFrequencyScanner {

    /**
     * Preprocesses the search term to create the Bad Character Table.
     * This table tells the algorithm how many characters to skip.
     */
    private Map<Character, Integer> buildBadCharTable(String pattern) {
        Map<Character, Integer> table = new HashMap<>();
        int m = pattern.length();
        for (int i = 0; i < m; i++) {
            table.put(pattern.charAt(i), i);
        }
        return table;
    }

    /**
     * Boyer-Moore algorithm to count occurrences of a word in a text string.
     * This fulfills the requirement for Task 3.
     */
    public int countInText(String text, String pattern) {
        if (pattern == null || pattern.isEmpty() || text == null || text.isEmpty()) {
            return 0;
        }

        String t = text.toLowerCase();
        String p = pattern.toLowerCase();
        int n = t.length();
        int m = p.length();
        int count = 0;

        Map<Character, Integer> badCharTable = buildBadCharTable(p);

        int s = 0; // Shift of the pattern relative to text
        while (s <= (n - m)) {
            int j = m - 1;

            // Start matching from the end of the pattern (Right-to-Left)
            while (j >= 0 && p.charAt(j) == t.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                // Match found!
                count++;
                // Shift to find the next possible match
                s += (s + m < n) ? m - badCharTable.getOrDefault(t.charAt(s + m), -1) : 1;
            } else {
                // Mismatch: Shift based on the last occurrence of the bad character
                s += Math.max(1, j - badCharTable.getOrDefault(t.charAt(s + j), -1));
            }
        }
        return count;
    }
}