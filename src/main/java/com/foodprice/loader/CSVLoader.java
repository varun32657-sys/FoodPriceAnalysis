package com.foodprice.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.foodprice.model.Product;

public class CSVLoader {

    public static List<Product> loadAll(String dataFolder) {
        List<Product> products = new ArrayList<>();
        File folder = new File(dataFolder);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (files == null) {
            System.out.println("No CSV files found in: " + dataFolder);
            return products;
        }

        for (File file : files) {
            System.out.println("Loading: " + file.getName());
            try {
                List<Product> loaded = loadFile(file);
                products.addAll(loaded);
                System.out.println("  -> Loaded " + loaded.size() + " products");
            } catch (Exception e) {
                System.out.println("  -> Error loading " + file.getName() + ": " + e.getMessage());
            }
        }

        System.out.println("Total products loaded: " + products.size());
        return products;
    }

    private static List<Product> loadFile(File file) throws Exception {
        List<Product> products = new ArrayList<>();

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
        );

        String headerLine = reader.readLine();
        if (headerLine == null) { reader.close(); return products; }

        // Remove BOM if present
        if (headerLine.startsWith("\uFEFF")) {
            headerLine = headerLine.substring(1);
        }

        headerLine = headerLine.toLowerCase();

        // Detect format
        boolean isNewFormat = headerLine.contains("date_scraped");

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            try {
                Product p = isNewFormat
                    ? parseNewFormat(line)
                    : parseOldFormat(line);
                if (p != null) products.add(p);
            } catch (Exception ignored) {}
        }

        reader.close();
        return products;
    }

    // Format: name,price,store,image,category,link,unit,date_scraped
    private static Product parseNewFormat(String line) {
        String[] parts = splitCSV(line);
        if (parts.length < 6) return null;

        String name     = clean(parts[0]);
        String priceStr = clean(parts[1]).replaceAll("[^0-9.]", "");
        String store    = clean(parts[2]);
        String image    = clean(parts[3]);
        String category = clean(parts[4]);
        String link     = clean(parts[5]);
        String unit     = parts.length > 6 ? clean(parts[6]) : "N/A";
        String date     = parts.length > 7 ? clean(parts[7]) : "N/A";

        if (name.isEmpty() || priceStr.isEmpty()) return null;
        double price;
        try { price = Double.parseDouble(priceStr); } catch (Exception e) { return null; }
        if (price <= 0) return null;

        return new Product(name, price, store, image, category, link, unit, date);
    }

    // Format: #,Category,Product Name,Price,Image URL,Product Link,Page
    private static Product parseOldFormat(String line) {
        String[] parts = splitCSV(line);
        if (parts.length < 6) return null;

        // Skip header-like lines
        String first = clean(parts[0]);
        if (first.equals("#") || first.equalsIgnoreCase("number")) return null;

        String category = clean(parts[1]).replaceAll("[^a-zA-Z ]", "").trim();
        String name     = clean(parts[2]);
        String priceStr = clean(parts[3]).replaceAll("[^0-9.]", "");
        String image    = clean(parts[4]);
        String link     = clean(parts[5]);

        // Detect store from link
        String store = "Unknown";
        String lowerLink = link.toLowerCase();
        if (lowerLink.contains("freshco"))  store = "FreshCo";
        else if (lowerLink.contains("loblaws")) store = "Loblaws";
        else if (lowerLink.contains("sobeys"))  store = "Sobeys";
        
        else if (lowerLink.contains("foodbasics")) store = "Food Basics";
        else if (lowerLink.contains("nofrills")) store = "No Frills";
        else if (lowerLink.contains("superstore")) store = "Real Canadian Superstore";

        if (name.isEmpty() || priceStr.isEmpty()) return null;
        // Skip lines like "Add X to cart"
        if (name.toLowerCase().startsWith("add ") && name.toLowerCase().contains("to cart")) {
            // Extract real name by removing "Add " prefix and " to cart" suffix
            name = name.replaceAll("(?i)^add\\s+", "").replaceAll("(?i)\\s+to cart$", "").trim();
        }

        double price;
        try { price = Double.parseDouble(priceStr); } catch (Exception e) { return null; }
        if (price <= 0) return null;

        return new Product(name, price, store, image, category, link, "N/A", "N/A");
    }

    // Smart CSV splitter - handles quoted fields
    public static String[] splitCSV(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    private static String clean(String s) {
        if (s == null) return "";
        return s.trim().replace("\uFEFF", "");
    }
}