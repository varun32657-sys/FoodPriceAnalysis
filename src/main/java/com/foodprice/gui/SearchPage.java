package com.foodprice.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.foodprice.features.WordFrequencyScanner; // Required for Task 3
import com.foodprice.model.Product;

public class SearchPage extends JPanel {

    private final MainFrame frame;
    private JTextField searchField;
    private JPanel resultsPanel;
    private JLabel resultCountLabel;
    private JLabel boyerMooreLabel; // Added for Boyer-Moore Frequency
    private JLabel spellLabel;
    private JComboBox<String> sortBox;
    private JComboBox<String> storeFilter;
    private JComboBox<String> categoryFilter;
    private String currentQuery = "";
    private JPopupMenu suggestionsPopup;
    
    // Initialize the Boyer-Moore Scanner
    private WordFrequencyScanner boyerScanner = new WordFrequencyScanner();

    // 🔥 PREMIUM PALETTE (Same as HomePage)
    private static final Color BG            = new Color(11, 15, 25);    
    private static final Color BG_CARD       = new Color(23, 32, 51);    
    private static final Color BG_CARD_HOVER = new Color(33, 46, 73);    
    private static final Color ACCENT        = new Color(34, 197, 94);  // The Green 500
    private static final Color ACCENT_HOVER  = new Color(22, 163, 74);  
    private static final Color TEXT_LIGHT    = new Color(248, 250, 252); 
    private static final Color TEXT_MUTED    = new Color(148, 163, 184); 
    private static final Color BORDER        = new Color(45, 55, 72);    

    public SearchPage(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42)); // Slate 900
        buildUI();
    }

    private void buildUI() {
        add(buildTopHeaderArea(), BorderLayout.NORTH);
        
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(15, 23, 42));
        resultsPanel.setBorder(new EmptyBorder(15, 30, 30, 30));

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getViewport().setBackground(new Color(15, 23, 42));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = BORDER;
                this.trackColor = new Color(15, 23, 42);
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton jb = new JButton(); jb.setPreferredSize(new Dimension(0,0)); return jb;
            }
        });

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildTopHeaderArea() {
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(BG_CARD);
        topContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(15, 23, 42));
        nav.setPreferredSize(new Dimension(0, 75));
        nav.setBorder(new EmptyBorder(0, 20, 0, 20));

        // LOGO AREA
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setOpaque(false);
        
        JLabel logoIcon = new JLabel("🛒 ");
        logoIcon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24)); 
        
        JLabel logoTxt = new JLabel("FoodPriceAnalyzer");
        logoTxt.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        logoTxt.setForeground(Color.WHITE);
        
        logoPanel.add(logoIcon); 
        logoPanel.add(logoTxt); 

        // SEARCH AND BUTTONS AREA
        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        navRight.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        searchField.setBackground(BG_CARD);
        searchField.setForeground(TEXT_LIGHT);
        searchField.setCaretColor(ACCENT);
        searchField.setPreferredSize(new Dimension(240, 42)); 
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(4, 15, 4, 15)
        ));

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) return;
                String text = searchField.getText().trim();
                if (text.length() >= 2) showSuggestions(text);
                else hideSuggestions();
            }
        });

        JButton sb = new JButton("🔍");
        sb.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16)); 
        sb.setPreferredSize(new Dimension(50, 42));
        sb.setBackground(ACCENT);
        sb.setForeground(new Color(15, 23, 42));
        sb.setFocusPainted(false);
        sb.setBorder(BorderFactory.createLineBorder(ACCENT, 1, true));
        sb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sb.addActionListener(e -> { hideSuggestions(); search(searchField.getText().trim()); });

        JButton homeBtn = createModernButton("Home", ACCENT, true, e -> frame.showPage("HOME"));

        navRight.add(searchField); 
        navRight.add(sb); 
        navRight.add(Box.createHorizontalStrut(10));
        navRight.add(homeBtn);
        
        nav.add(logoPanel, BorderLayout.WEST);
        nav.add(navRight, BorderLayout.EAST);

        // FILTER RIBBON
        JPanel filterContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        filterContent.setBackground(BG_CARD);

        resultCountLabel = new JLabel("Start searching to find deals...");
        resultCountLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        resultCountLabel.setForeground(TEXT_LIGHT);

        boyerMooreLabel = new JLabel("");
        boyerMooreLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        boyerMooreLabel.setForeground(ACCENT);

        spellLabel = new JLabel("");
        spellLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 13));
        spellLabel.setForeground(new Color(96, 165, 250));
        spellLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        sortBox = new JComboBox<>(new String[]{"Relevance", "Price: Low → High", "Price: High → Low", "Name A-Z"});
        storeFilter = new JComboBox<>();
        storeFilter.addItem("All Stores");
        if(frame.getSearchEngine() != null) frame.getSearchEngine().getAllStores().forEach(storeFilter::addItem);

        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("All Categories");
        if(frame.getSearchEngine() != null) frame.getSearchEngine().getAllCategories().forEach(categoryFilter::addItem);

        styleCombo(sortBox); styleCombo(storeFilter); styleCombo(categoryFilter);

        sortBox.addActionListener(e -> refreshResults());
        storeFilter.addActionListener(e -> refreshResults());
        categoryFilter.addActionListener(e -> refreshResults());

        filterContent.add(resultCountLabel);
        filterContent.add(boyerMooreLabel); 
        filterContent.add(spellLabel);
        filterContent.add(Box.createHorizontalStrut(20));
        filterContent.add(filterLabel("Sort:")); filterContent.add(sortBox);
        filterContent.add(filterLabel("Store:")); filterContent.add(storeFilter);
        filterContent.add(filterLabel("Category:")); filterContent.add(categoryFilter);

        JScrollPane filterScroll = new JScrollPane(filterContent);
        filterScroll.setBorder(null);
        filterScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        filterScroll.setPreferredSize(new Dimension(0, 55));
        
        topContainer.add(nav, BorderLayout.NORTH);
        topContainer.add(filterScroll, BorderLayout.SOUTH);
        return topContainer;
    }

    private JButton createModernButton(String text, Color bg, boolean isPrimary, ActionListener al) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? (isPrimary ? ACCENT_HOVER : BORDER) : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30)); 
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btn.setForeground(isPrimary ? new Color(15, 23, 42) : TEXT_LIGHT);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.addActionListener(al);
        return btn;
    }

    private void showSuggestions(String text) {
        if (frame.getWordCompletion() == null) return;
        List<String> suggestions = frame.getWordCompletion().getSuggestions(text, 8);
        hideSuggestions();
        if (suggestions.isEmpty()) return;

        suggestionsPopup = new JPopupMenu();
        suggestionsPopup.setLayout(new BorderLayout());
        suggestionsPopup.setBackground(BG_CARD);
        suggestionsPopup.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        suggestionsPopup.setFocusable(false); 

        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(BG_CARD);

        for (String s : suggestions) {
            JPanel row = new JPanel(new BorderLayout(15, 0));
            row.setBackground(BG_CARD);
            Dimension rowSize = new Dimension(searchField.getWidth(), 38);
            row.setPreferredSize(rowSize);
            row.setMaximumSize(rowSize);
            row.setBorder(new EmptyBorder(5, 15, 5, 15));
            row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel lbl = new JLabel("🔍 " + s);
            lbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            lbl.setForeground(TEXT_LIGHT);

            final int freq = (frame.getSearchFrequency() != null) ? frame.getSearchFrequency().getFrequency(s) : 0;
            JLabel freqLbl = new JLabel(freq > 0 ? "🔥 " + freq : "✨ New");
            freqLbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            freqLbl.setForeground(freq > 0 ? ACCENT : TEXT_MUTED);

            row.add(lbl, BorderLayout.WEST);
            row.add(freqLbl, BorderLayout.EAST);

            row.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { row.setBackground(BG_CARD_HOVER); }
                @Override public void mouseExited(MouseEvent e) { row.setBackground(BG_CARD); }
                @Override public void mousePressed(MouseEvent e) {
                    searchField.setText(s);
                    hideSuggestions();
                    search(s);
                }
            });
            listContainer.add(row);
        }
        suggestionsPopup.add(listContainer);
        suggestionsPopup.pack();
        suggestionsPopup.show(searchField, 0, searchField.getHeight());
        searchField.requestFocusInWindow(); 
    }

    private void hideSuggestions() {
        if (suggestionsPopup != null && suggestionsPopup.isVisible()) {
            suggestionsPopup.setVisible(false);
        }
    }

    private void styleCombo(JComboBox<String> box) {
        box.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        box.setBackground(BG_CARD_HOVER);
        box.setForeground(TEXT_LIGHT);
        box.setPreferredSize(new Dimension(160, 32));
        box.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    private JLabel filterLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public void search(String query) {
        if (query == null || query.trim().isEmpty()) return;
        currentQuery = query.trim();
        searchField.setText(currentQuery);
        if(frame.getSearchFrequency() != null) frame.getSearchFrequency().recordSearch(currentQuery);

        if(frame.getSpellChecker() != null) {
            String corrected = frame.getSpellChecker().checkQuery(currentQuery);
            if (!corrected.equalsIgnoreCase(currentQuery)) {
                spellLabel.setText("✏ Did you mean: \"" + corrected + "\"?");
                final String c = corrected;
                for (MouseListener ml : spellLabel.getMouseListeners()) spellLabel.removeMouseListener(ml);
                spellLabel.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { search(c); }
                });
            } else {
                spellLabel.setText("");
            }
        }
        refreshResults();
    }

    /**
     * Specialized method to display only the user's Watchlist items.
     * Fulfills the "My Watchlist" feature button action.
     */
    public void displayWatchlist(List<Product> favorites) {
        this.currentQuery = ""; 
        searchField.setText("");
        resultsPanel.removeAll();
        resultCountLabel.setText("Showing " + favorites.size() + " items in your Watchlist ❤️");
        if (boyerMooreLabel != null) boyerMooreLabel.setText(""); 

        if (favorites.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setOpaque(false);
            JLabel none = new JLabel("Your watchlist is empty! Heart some items to see them here.");
            none.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
            none.setForeground(TEXT_MUTED);
            empty.add(none);
            resultsPanel.add(empty);
        } else {
            for (Product p : favorites) {
                // Pass 'frame' to fix constructor error
                resultsPanel.add(new ProductCard(p, false, "", frame));
                resultsPanel.add(Box.createVerticalStrut(6));
            }
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void refreshResults() {
        if (currentQuery.isEmpty() || frame.getSearchEngine() == null) return;
        
        // Boyer-Moore Frequency Calculation for Task 3
        int dbFreq = 0;
        for (Product p : frame.getAllProducts()) {
            String content = p.getName() + " " + p.getCategory();
            dbFreq += boyerScanner.countInText(content, currentQuery);
        }
        boyerMooreLabel.setText(" 📊 DB Frequency: " + dbFreq + " (Boyer-Moore)");

        List<Product> results = frame.getSearchEngine().search(currentQuery);

        String store = (String) storeFilter.getSelectedItem();
        if (store != null && !store.equals("All Stores"))
            results = results.stream().filter(p -> p.getStore().equalsIgnoreCase(store)).collect(Collectors.toList());

        String cat = (String) categoryFilter.getSelectedItem();
        if (cat != null && !cat.equals("All Categories"))
            results = results.stream().filter(p -> p.getCategory().equalsIgnoreCase(cat)).collect(Collectors.toList());

        String sort = (String) sortBox.getSelectedItem();
        if (sort != null) {
            if (sort.contains("Low")) results = frame.getSearchEngine().sortByPrice(results, true);
            else if (sort.contains("High")) results = frame.getSearchEngine().sortByPrice(results, false);
            else if (sort.contains("A-Z")) results = frame.getSearchEngine().sortByName(results);
            else results = frame.getPageRanking().rank(results, currentQuery);
        }

        resultCountLabel.setText("Found " + results.size() + " results for \"" + currentQuery + "\"");
        Product cheapest = frame.getSearchEngine().getCheapest(results);
        Map<String, Product> cheapestPerStore = frame.getSearchEngine().getCheapestPerStore(results);

        resultsPanel.removeAll();
        if (results.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setOpaque(false);
            JLabel none = new JLabel("😕 No products found for \"" + currentQuery + "\"");
            none.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
            none.setForeground(TEXT_MUTED);
            empty.add(none);
            resultsPanel.add(empty);
        } else {
            if (cheapestPerStore.size() > 1) {
                resultsPanel.add(buildComparisonBar(cheapestPerStore, cheapest));
                resultsPanel.add(Box.createVerticalStrut(10));
            }
            for (Product p : results) {
                boolean isCheapest = cheapest != null && p == cheapest;
                // FIX: Pass 'frame' as the 4th argument
                resultsPanel.add(new ProductCard(p, isCheapest, currentQuery, frame)); 
                resultsPanel.add(Box.createVerticalStrut(6));
            }
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel buildComparisonBar(Map<String, Product> storeMap, Product overall) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(20, 40, 30));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JLabel title = new JLabel("💰 Price Comparison Across All Stores");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        title.setForeground(ACCENT);
        panel.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cards.setOpaque(false);

        List<Map.Entry<String, Product>> sorted = new ArrayList<>(storeMap.entrySet());
        sorted.sort(Comparator.comparingDouble(e -> e.getValue().getPrice()));

        for (Map.Entry<String, Product> e : sorted) {
            boolean best = overall != null && Math.abs(e.getValue().getPrice() - overall.getPrice()) < 0.001;
            cards.add(storePriceCard(e.getKey(), e.getValue(), best));
        }
        panel.add(cards, BorderLayout.CENTER);
        return panel;
    }

    private JPanel storePriceCard(String store, Product p, boolean best) {
        JPanel c = new JPanel(new GridLayout(3, 1, 0, 2));
        c.setBackground(best ? new Color(30, 40, 10) : BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(best ? ACCENT : BORDER, best ? 2 : 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        c.setPreferredSize(new Dimension(155, 75));

        JLabel s = new JLabel(store);
        s.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        s.setForeground(TEXT_LIGHT);

        JLabel pr = new JLabel("$" + String.format("%.2f", p.getPrice()));
        pr.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        pr.setForeground(best ? ACCENT : new Color(96, 165, 250));

        JLabel tag = new JLabel(best ? "⭐ CHEAPEST!" : (p.getUnit() != null && p.getUnit().equals("N/A") ? "" : p.getUnit()));
        tag.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        tag.setForeground(best ? ACCENT : TEXT_MUTED);

        c.add(s); c.add(pr); c.add(tag);
        return c;
    }
}