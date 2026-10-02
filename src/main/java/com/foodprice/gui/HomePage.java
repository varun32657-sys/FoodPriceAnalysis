package com.foodprice.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.foodprice.model.Product;

public class HomePage extends JPanel {

    private MainFrame frame;
    private JTextField searchField;
    private JPopupMenu suggestionsPopup;
    private JPanel navButtons; // Container for dynamic login/logout buttons
    private boolean suppressSuggestions = false;

    // Enhanced Modern Color Palette
    private static final Color BG           = new Color(15, 23, 42);   // Slate 900
    private static final Color BG_CARD      = new Color(30, 41, 59);   // Slate 800
    private static final Color BG_CARD_HOVER= new Color(51, 65, 85);   // Slate 700
    private static final Color ACCENT       = new Color(34, 197, 94);  // Green 500
    private static final Color ACCENT_HOVER = new Color(22, 163, 74);  // Green 600
    private static final Color TEXT_LIGHT   = new Color(248, 250, 252); // Slate 50
    private static final Color TEXT_MUTED   = new Color(148, 163, 184); // Slate 400
    private static final Color BORDER       = new Color(71, 85, 105);  // Slate 600
    private static final Color LOGOUT_RED   = new Color(239, 68, 68);  // Red 500

    public HomePage(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        add(buildNavBar(), BorderLayout.NORTH);
        
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.add(buildHero());
        body.add(buildStatsBar());
        body.add(buildTrendingSearches());
        body.add(buildCategorySection());
        body.add(buildFeaturedProducts());
        
        body.add(Box.createRigidArea(new Dimension(0, 40)));

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(15, 23, 42));
        nav.setPreferredSize(new Dimension(0, 70));
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            BorderFactory.createEmptyBorder(0, 30, 0, 30)
        ));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        logoPanel.setOpaque(false);
        JLabel logoIcon = new JLabel("🛒");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        JLabel logoText = new JLabel("FoodPrice");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoText.setForeground(TEXT_LIGHT);
        JLabel logoAccent = new JLabel("Analyzer");
        logoAccent.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoAccent.setForeground(ACCENT);
        
        logoPanel.add(logoIcon);
        logoPanel.add(logoText);
        logoPanel.add(logoAccent);

        navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        navButtons.setOpaque(false);
        
        // Initial state
        resetNavButtons();

        nav.add(logoPanel, BorderLayout.WEST);
        nav.add(navButtons, BorderLayout.EAST);
        return nav;
    }

    /**
     * Updates the navbar when a user successfully logs in.
     */
    public void updateLoginStatus(String userName) {
        navButtons.removeAll();
        navButtons.add(createModernButton("Home", ACCENT, true, e -> frame.showPage("HOME")));
        
        // NEW: Watchlist Button for logged-in users [cite: 131, 137]
        navButtons.add(createModernButton("❤️ My Watchlist", BG_CARD_HOVER, false, e -> showWatchlist()));
        
        // Greeting button (non-clickable info style)
        JButton userGreeting = createModernButton("Hi, " + userName, BG_CARD_HOVER, false, null);
        navButtons.add(userGreeting);
        
        // Logout Button
        navButtons.add(createModernButton("Logout", LOGOUT_RED, false, e -> {
            frame.setLoggedInUser(null); 
            resetNavButtons();
            JOptionPane.showMessageDialog(this, "You have been logged out.");
        }));
        
        navButtons.revalidate();
        navButtons.repaint();
    }

    /**
     * Resets navbar to show Login/Register options.
     */
    public void resetNavButtons() {
        navButtons.removeAll();
        navButtons.add(createModernButton("Home", ACCENT, true, e -> frame.showPage("HOME")));
        navButtons.add(createModernButton("Register / Login", BG_CARD_HOVER, false, e -> frame.showPage("REGISTER")));
        navButtons.revalidate();
        navButtons.repaint();
    }

    /**
     * Switches to the Search Page and displays favorited items.
     */
    private void showWatchlist() {
    // 1. Get the list of favorites from the manager
    List<Product> favorites = frame.getFavoritesManager().getFavoriteProducts(frame.getAllProducts());
    
    // 2. Get the SearchPage panel
    SearchPage sp = (SearchPage) frame.getPanelByName("SEARCH");
    
    if (sp != null) {
        // 3. Tell the SearchPage to display ONLY these products
        sp.displayWatchlist(favorites);
        
        // 4. Switch the screen
        frame.showPage("SEARCH");
    }
}

    private JPanel buildHero() {
        JPanel hero = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(15, 23, 42),
                    getWidth(), getHeight(), new Color(20, 83, 45));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        hero.setPreferredSize(new Dimension(0, 360));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));
        
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 0, 8, 0);

        g.gridy = 0;
        JLabel badge = new JLabel("✨COMP 8457 PROJECT Canada's #1 Grocery Price Comparison Engine");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setForeground(ACCENT);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        hero.add(badge, g);

        g.gridy = 1;
        JLabel h1 = new JLabel("<html><center><span style='font-size:36px; font-weight:bold; color:white;'>Beat Inflation. Find the Best Deals.</span><br><span style='font-size:28px; color:#e2e8f0;'>Across 5 Major Canadian Supermarkets</span></center></html>");
        h1.setHorizontalAlignment(SwingConstants.CENTER);
        hero.add(h1, g);

        g.gridy = 2;
        g.insets = new Insets(30, 0, 0, 0);
        hero.add(buildSearchBox(), g);

        return hero;
    }

    private JPanel buildSearchBox() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);

        JPanel searchPill = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 50, 50));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 50, 50));
                g2.dispose();
            }
        };
        searchPill.setOpaque(false);
        searchPill.setPreferredSize(new Dimension(650, 55));
        searchPill.setBorder(new EmptyBorder(5, 20, 5, 5));

        searchField = new JTextField();
        searchField.setOpaque(false);
        searchField.setBorder(null);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(ACCENT);
        
        JLabel searchIcon = new JLabel("🔍 ");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        searchIcon.setForeground(TEXT_MUTED);

        JButton searchBtn = createModernButton("Search", ACCENT, true, e -> doSearch(searchField.getText().trim()));
        searchBtn.setPreferredSize(new Dimension(130, 45));

        searchPill.add(searchIcon, BorderLayout.WEST);
        searchPill.add(searchField, BorderLayout.CENTER);
        searchPill.add(searchBtn, BorderLayout.EAST);

        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    hideSuggestions();
                    doSearch(searchField.getText().trim());
                } else {
                    String text = searchField.getText().trim();
                    if (text.length() >= 2) showSuggestions(text);
                    else hideSuggestions();
                }
            }
        });

        wrapper.add(searchPill);
        return wrapper;
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
            Dimension rowSize = new Dimension(650, 40);
            row.setPreferredSize(rowSize);
            row.setMaximumSize(rowSize);
            row.setBorder(new EmptyBorder(5, 15, 5, 15));
            row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel lbl = new JLabel("🔍 " + s);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lbl.setForeground(TEXT_LIGHT);

            final int freq = (frame.getSearchFrequency() != null) ? frame.getSearchFrequency().getFrequency(s) : 0;
            JLabel freqLbl = new JLabel(freq > 0 ? "🔥 " + freq : "✨ New");
            freqLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            freqLbl.setForeground(freq > 0 ? ACCENT : TEXT_MUTED);

            row.add(lbl, BorderLayout.WEST);
            row.add(freqLbl, BorderLayout.EAST);

            row.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { row.setBackground(BG_CARD_HOVER); }
                @Override public void mouseExited(MouseEvent e) { row.setBackground(BG_CARD); }
                @Override public void mousePressed(MouseEvent e) {
                    searchField.setText(s);
                    hideSuggestions();
                    doSearch(s);
                }
            });
            listContainer.add(row);
        }

        suggestionsPopup.add(listContainer);
        suggestionsPopup.pack();
        suggestionsPopup.show(searchField, 0, searchField.getHeight() + 10);
        searchField.requestFocusInWindow();
    }

    private void hideSuggestions() {
        if (suggestionsPopup != null && suggestionsPopup.isVisible()) {
            suggestionsPopup.setVisible(false);
        }
    }

    private void doSearch(String query) {
        if (query.isEmpty()) return;
        frame.getSearchFrequency().recordSearch(query);
        SearchPage sp = (SearchPage) frame.getPanelByName("SEARCH");
        if (sp != null) sp.search(query);
        frame.showPage("SEARCH");
    }

    private JPanel buildStatsBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 20));
        bar.setBackground(new Color(20, 83, 45));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, ACCENT));
        
        List<Product> products = frame.getAllProducts();
        long total  = products.size();
        long stores = products.stream().filter(p -> p!=null).map(Product::getStore).distinct().count();
        long cats   = products.stream().filter(p -> p!=null).map(Product::getCategory).distinct().count();

        bar.add(statItem("📦", String.valueOf(total), "Total Products"));
        bar.add(statItem("🏪", String.valueOf(stores), "Retail Chains"));
        bar.add(statItem("🗂", String.valueOf(cats), "Categories"));
        bar.add(statItem("⚡", "Sub-10ms", "Search Speeds"));

        return bar;
    }

    private JPanel statItem(String icon, String val, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        p.setOpaque(false);
        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        JLabel txt = new JLabel("<html><b style='font-size:18px;color:white;'>" + val + "</b><br><span style='font-size:12px;color:#86efac;'>" + label + "</span></html>");
        p.add(ic); p.add(txt);
        return p;
    }

    private JPanel buildTrendingSearches() {
        List<Map.Entry<String, Integer>> top = frame.getSearchFrequency().getTopSearches(6);
        JPanel section = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        section.setBackground(BG);
        section.setBorder(new EmptyBorder(15, 0, 10, 0));

        JLabel title = new JLabel("🔥 Trending Now: ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(249, 115, 22));
        section.add(title);

        for (Map.Entry<String, Integer> e : top) {
            JButton chip = new JButton(e.getKey());
            chip.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            chip.setBackground(BG_CARD);
            chip.setForeground(TEXT_LIGHT);
            chip.setFocusPainted(false);
            chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 16, 6, 16)
            ));
            
            chip.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent ev) { chip.setBackground(BG_CARD_HOVER); }
                @Override public void mouseExited(MouseEvent ev) { chip.setBackground(BG_CARD); }
            });
            chip.addActionListener(ev -> { searchField.setText(e.getKey()); doSearch(e.getKey()); });
            section.add(chip);
        }
        return section;
    }

    private JPanel buildCategorySection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BG);
        section.setBorder(new EmptyBorder(10, 40, 20, 40));

        section.add(sectionTitle("Browse by Category", "Find exactly what you need"), BorderLayout.NORTH);

        String[][] cats = {
            {"🥛","Milk"},{"🥚","Eggs"},{"🍗","Chicken"},{"🍞","Bread"},{"🍦","Ice Cream"},
            {"🍪","Cookies"},{"🍫","Chocolates"},{"🍚","Rice"},{"🥤","Cool Drinks"},{"🥔","Chips"}
        };

        JPanel grid = new JPanel(new GridLayout(1, 10, 15, 0));
        grid.setBackground(BG);
        grid.setBorder(new EmptyBorder(15, 0, 0, 0));
        for (String[] c : cats) grid.add(createInteractiveCard(c[0], c[1]));
        section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JPanel createInteractiveCard(String emoji, String name) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(BG_CARD);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        wrapper.setBorder(new EmptyBorder(5, 0, 5, 0));
        card.setBorder(new EmptyBorder(15, 5, 15, 5));

        JLabel eLabel = new JLabel(emoji, SwingConstants.CENTER);
        eLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        JLabel nLabel = new JLabel(name, SwingConstants.CENTER);
        nLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nLabel.setForeground(TEXT_LIGHT);

        card.add(eLabel, BorderLayout.CENTER);
        card.add(nLabel, BorderLayout.SOUTH);
        wrapper.add(card, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(BG_CARD_HOVER);
                wrapper.setBorder(new EmptyBorder(0, 0, 10, 0));
                wrapper.revalidate();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(BG_CARD);
                wrapper.setBorder(new EmptyBorder(5, 0, 5, 0));
                wrapper.revalidate();
            }
            @Override public void mouseClicked(MouseEvent e) { 
                searchField.setText(name); 
                doSearch(name); 
            }
        });
        return wrapper;
    }

 private JPanel buildFeaturedProducts() {
    JPanel section = new JPanel(new BorderLayout());
    section.setBackground(BG);
    section.setBorder(new EmptyBorder(20, 40, 40, 40));

    section.add(sectionTitle("⭐ Best Deals Right Now", "Cheapest items across all our datasets"), BorderLayout.NORTH);

    JPanel grid = new JPanel(new GridLayout(2, 4, 20, 20));
    grid.setBackground(BG);
    grid.setBorder(new EmptyBorder(20, 0, 0, 0));

    String[] featuredCats = {"Milk","Eggs","Bread","Chicken","Ice Cream","Chips","Rice","Chocolates"};
    
    for (String cat : featuredCats) {
        List<Product> catProds = frame.getSearchEngine().searchByCategory(cat);
        if (catProds != null && !catProds.isEmpty()) {
            Product selectedProduct = null;

            if (cat.equalsIgnoreCase("Milk")) {
                // Filter: Must contain '%' AND 'milk' (lowercase), Exclude 'hershey' and 'chocolate'
                selectedProduct = catProds.stream()
                    .filter(p -> p.getName().contains("%") && p.getName().toLowerCase().contains("milk"))
                    .filter(p -> !p.getName().toLowerCase().contains("Kosher"))
                    .filter(p -> !p.getName().toLowerCase().contains("chocolate"))
                    .min(java.util.Comparator.comparingDouble(Product::getPrice))
                    .orElse(frame.getSearchEngine().getCheapest(catProds));

            } else if (cat.equalsIgnoreCase("Eggs")) {
                // Filter: Must contain 'white' or 'large', Exclude 'cadbury' and 'chocolate'
                selectedProduct = catProds.stream()
                    .filter(p -> p.getName().toLowerCase().contains("white") || p.getName().toLowerCase().contains("large"))
                    .filter(p -> !p.getName().toLowerCase().contains("cadbury"))
                    .filter(p -> !p.getName().toLowerCase().contains("chocolate"))
                    .min(java.util.Comparator.comparingDouble(Product::getPrice))
                    .orElse(frame.getSearchEngine().getCheapest(catProds));

            } else {
                // Default logic for other categories
                selectedProduct = frame.getSearchEngine().getCheapest(catProds);
            }

            if (selectedProduct != null) {
                grid.add(featuredCard(selectedProduct, cat));
            }
        }
    }
    section.add(grid, BorderLayout.CENTER);
    return section;
}

    private JPanel featuredCard(Product p, String category) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel imgLabel = new JLabel(getCatEmoji(category), SwingConstants.CENTER);
        imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        imgLabel.setPreferredSize(new Dimension(0, 70));

        String url = p.getImage();
        if (url != null && url.startsWith("http") && !url.contains("icon")) {
            new Thread(() -> {
                try {
                    Image img = javax.imageio.ImageIO.read(new java.net.URL(url));
                    if (img != null) {
                        Image scaled = img.getScaledInstance(65, 65, Image.SCALE_SMOOTH);
                        SwingUtilities.invokeLater(() -> {
                            imgLabel.setIcon(new ImageIcon(scaled));
                            imgLabel.setText("");
                        });
                    }
                } catch (Exception ignored) {}
            }).start();
        }

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel catBadge = new JLabel(category.toUpperCase());
        catBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        catBadge.setForeground(ACCENT);
        
        JLabel nameLabel = new JLabel("<html><div style='width:150px;'>" + p.getName() + "</div></html>");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_LIGHT);
        
        JLabel storeLabel = new JLabel("📍 " + p.getStore());
        storeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        storeLabel.setForeground(TEXT_MUTED);

        JPanel priceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        priceRow.setOpaque(false);
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", p.getPrice()) + " ");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        priceLabel.setForeground(new Color(234, 179, 8));
        
        priceRow.add(priceLabel);

        info.add(catBadge);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(nameLabel);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(storeLabel);
        info.add(Box.createRigidArea(new Dimension(0, 8)));
        info.add(priceRow);

        card.add(imgLabel, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        wrapper.add(card, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(BG_CARD_HOVER);
                wrapper.setBorder(new EmptyBorder(0, 0, 10, 0));
                wrapper.revalidate();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(BG_CARD);
                wrapper.setBorder(new EmptyBorder(5, 0, 5, 0));
                wrapper.revalidate();
            }
            @Override public void mouseClicked(MouseEvent e) { 
                searchField.setText(category);
                doSearch(category); 
            }
        });

        return wrapper;
    }

    private JPanel sectionTitle(String mainText, String subText) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel main = new JLabel(mainText);
        main.setFont(new Font("Segoe UI", Font.BOLD, 22));
        main.setForeground(TEXT_LIGHT);
        
        JLabel sub = new JLabel(subText);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(TEXT_MUTED);
        
        p.add(main, BorderLayout.NORTH);
        p.add(sub, BorderLayout.SOUTH);
        return p;
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(isPrimary ? new Color(15, 23, 42) : TEXT_LIGHT);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        if (al != null) btn.addActionListener(al);
        return btn;
    }

    private String getCatEmoji(String cat) {
        if (cat == null) return "🛒";
        switch (cat.toLowerCase()) {
            case "milk": return "🥛"; case "eggs": return "🥚";
            case "chicken": return "🍗"; case "bread": return "🍞";
            case "ice cream": return "🍦"; case "cookies": return "🍪";
            case "chocolates": return "🍫"; case "rice": return "🍚";
            case "cool drinks": return "🥤"; case "chips": return "🥔";
            default: return "🛒";
        }
    }
}