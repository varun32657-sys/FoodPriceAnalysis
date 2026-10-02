package com.foodprice.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SearchFrequency {

    private Map<String, Integer> frequencyMap;
    private static final String FILE_PATH = "users/search_history.txt";

    public SearchFrequency() {
        frequencyMap = new TreeMap<>();
        loadFromFile();
    }

    public void recordSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;
        String key = query.toLowerCase().trim();
        frequencyMap.merge(key, 1, Integer::sum);
        saveToFile();
    }

    public int getFrequency(String query) {
        return frequencyMap.getOrDefault(query.toLowerCase().trim(), 0);
    }

    // Get top N most searched
    public List<Map.Entry<String, Integer>> getTopSearches(int n) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(frequencyMap.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());
        return entries.subList(0, Math.min(n, entries.size()));
    }

    // Get all search history
    public Map<String, Integer> getAllFrequencies() {
        return Collections.unmodifiableMap(frequencyMap);
    }

    private void saveToFile() {
        try {
            new File("users").mkdirs();
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                    new FileOutputStream(FILE_PATH), StandardCharsets.UTF_8));
            for (Map.Entry<String, Integer> e : frequencyMap.entrySet()) {
                writer.println(e.getKey() + "=" + e.getValue());
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Could not save search history: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) return;
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    try {
                        frequencyMap.put(parts[0], Integer.parseInt(parts[1]));
                    } catch (Exception ignored) {}
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Could not load search history: " + e.getMessage());
        }
    }
}