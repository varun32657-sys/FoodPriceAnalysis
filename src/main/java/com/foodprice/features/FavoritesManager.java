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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.foodprice.model.Product;

public class FavoritesManager {
    private final Set<String> favoriteIds;
    private static final String FILE_PATH = "users/favorites.txt";

    public FavoritesManager() {
        this.favoriteIds = new HashSet<>();
        loadFromFile();
    }

    /**
     * Toggles the favorite status of a product.
     * If it exists, it is removed; if not, it is added.
     */
    public void toggleFavorite(String productName) {
        if (favoriteIds.contains(productName)) {
            favoriteIds.remove(productName);
            System.out.println("Removed from favorites: " + productName);
        } else {
            favoriteIds.add(productName);
            System.out.println("Added to favorites: " + productName);
        }
        saveToFile();
    }

    /**
     * Checks if a specific product name is in the favorites list.
     */
    public boolean isFavorite(String productName) {
        return favoriteIds.contains(productName);
    }

    /**
     * Filters a list of all products to return only those marked as favorites.
     */
    public List<Product> getFavoriteProducts(List<Product> allProducts) {
        List<Product> favorites = new ArrayList<>();
        for (Product p : allProducts) {
            // Checks if the product name is stored in our favoriteIds Set
            if (favoriteIds.contains(p.getName())) {
                favorites.add(p);
            }
        }
        return favorites;
    }

    /**
     * Persists the favorite product names to a text file.
     */
    private void saveToFile() {
        try {
            File dir = new File("users");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(FILE_PATH), StandardCharsets.UTF_8))) {
                for (String name : favoriteIds) {
                    writer.println(name);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not save favorites: " + e.getMessage());
        }
    }

    /**
     * Loads the favorite product names from the text file on startup.
     */
    private void loadFromFile() {
        File f = new File(FILE_PATH);
        if (!f.exists()) return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    favoriteIds.add(trimmed);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load favorites: " + e.getMessage());
        }
    }

    /**
     * Helper to get the total count of favorites.
     */
    public int getFavoriteCount() {
        return favoriteIds.size();
    }
}