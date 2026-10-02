package com.foodprice.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.foodprice.features.FavoritesManager;
import com.foodprice.features.InvertedIndex;
import com.foodprice.features.PageRanking;
import com.foodprice.features.SearchEngine;
import com.foodprice.features.SearchFrequency;
import com.foodprice.features.SpellChecker;
import com.foodprice.features.WordCompletion;
import com.foodprice.loader.CSVLoader;
import com.foodprice.model.Product;

public class MainFrame extends JFrame {

    private List<Product> allProducts;
    private SearchEngine searchEngine;
    private SpellChecker spellChecker;
    private WordCompletion wordCompletion;
    private PageRanking pageRanking;
    private SearchFrequency searchFrequency;
    private InvertedIndex invertedIndex;
    private FavoritesManager favoritesManager;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String currentUser = null;

    public MainFrame() {
        setTitle("Food Price Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));

        loadData();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new HomePage(this), "HOME");
        mainPanel.add(new SearchPage(this), "SEARCH");
        mainPanel.add(new RegisterPage(this), "REGISTER");

        add(mainPanel);
        showPage("HOME");
        setVisible(true);
    }

    private void loadData() {
        allProducts = CSVLoader.loadAll("data");
        searchEngine = new SearchEngine(allProducts);
        spellChecker = new SpellChecker(allProducts);
        wordCompletion = new WordCompletion(allProducts);
        pageRanking = new PageRanking();
        searchFrequency = new SearchFrequency();
        invertedIndex = new InvertedIndex(allProducts);
        favoritesManager = new FavoritesManager();
    }

    public void setLoggedInUser(String name) {
        this.currentUser = name;
        HomePage hp = (HomePage) getPanelByName("HOME");
        if (hp != null) hp.updateLoginStatus(name);
    }

    // CRITICAL FIX: Changed from private to public so HomePage can access it
    public JPanel getPanelByName(String name) {
        for (Component c : mainPanel.getComponents()) {
            if (c instanceof SearchPage && name.equals("SEARCH")) return (JPanel) c;
            if (c instanceof HomePage && name.equals("HOME")) return (JPanel) c;
            if (c instanceof RegisterPage && name.equals("REGISTER")) return (JPanel) c;
        }
        return null;
    }

    public void showPage(String name) { cardLayout.show(mainPanel, name); }
    public List<Product> getAllProducts() { return allProducts; }
    public SearchEngine getSearchEngine() { return searchEngine; }
    public SpellChecker getSpellChecker() { return spellChecker; }
    public WordCompletion getWordCompletion() { return wordCompletion; }
    public PageRanking getPageRanking() { return pageRanking; }
    public SearchFrequency getSearchFrequency() { return searchFrequency; }
    public InvertedIndex getInvertedIndex() { return invertedIndex; }
    public FavoritesManager getFavoritesManager() { return favoritesManager; }
}