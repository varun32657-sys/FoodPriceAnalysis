package com.foodprice.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.foodprice.model.Product;

public class ProductCard extends JPanel {

    private static final ExecutorService IMG_POOL = Executors.newFixedThreadPool(6);
    
    // Modern Color Palette
    private static final Color BG_CARD      = new Color(30, 41, 59);   
    private static final Color BG_HOVER     = new Color(51, 65, 85);   
    private static final Color BG_CHEAP     = new Color(20, 83, 45, 40); 
    private static final Color ACCENT       = new Color(34, 197, 94);  
    private static final Color TEXT_MAIN    = new Color(248, 250, 252); 
    private static final Color TEXT_MUTED   = new Color(148, 163, 184); 
    private static final Color GOLD         = new Color(234, 179, 8);   
    private static final Color BORDER_COLOR = new Color(71, 85, 105);  

    public ProductCard(Product product, boolean isCheapest, String query, MainFrame frame) {
        setLayout(new BorderLayout(20, 0));
        setBackground(isCheapest ? BG_CHEAP : BG_CARD);
        setOpaque(false); 
        
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 

        // --- LEFT: Image Container ---
        JPanel imgContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 23, 42)); 
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.dispose();
            }
        };
        imgContainer.setOpaque(false);
        imgContainer.setPreferredSize(new Dimension(85, 85));

        JLabel imgLbl = new JLabel(getEmoji(product.getCategory()), SwingConstants.CENTER);
        imgLbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
        
        loadProductImage(product.getImage(), imgLbl);
        imgContainer.add(imgLbl, BorderLayout.CENTER);

        // --- CENTER: Product Info ---
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel name = new JLabel("<html><body style='width: 400px;'>" + product.getName() + "</body></html>");
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(TEXT_MAIN);

        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        metaRow.setOpaque(false);
        
        JLabel storeTag = new JLabel(product.getStore().toUpperCase() + " • ");
        storeTag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        storeTag.setForeground(ACCENT);
        
        JLabel catTag = new JLabel(product.getCategory());
        catTag.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        catTag.setForeground(TEXT_MUTED);
        
        metaRow.add(storeTag);
        metaRow.add(catTag);

        JLabel unit = new JLabel(product.getUnit() != null && !product.getUnit().equals("N/A") ? product.getUnit() : "Standard Size");
        unit.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        unit.setForeground(TEXT_MUTED);

        info.add(Box.createVerticalGlue());
        info.add(name);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(metaRow);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(unit);
        info.add(Box.createVerticalGlue());

        // --- RIGHT: Pricing & Actions ---
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(220, 100)); // Ensure fixed width for visibility

        // Price Container
        JPanel pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
        pricePanel.setOpaque(false);

        JLabel price = new JLabel("$" + String.format("%.2f", product.getPrice()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 24));
        price.setForeground(isCheapest ? ACCENT : Color.WHITE);
        price.setAlignmentX(Component.RIGHT_ALIGNMENT);
        pricePanel.add(price);
        
        if (isCheapest) {
            JLabel badge = new JLabel(" BEST DEAL ");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            badge.setForeground(new Color(15, 23, 42));
            badge.setBackground(GOLD);
            badge.setOpaque(true);
            badge.setAlignmentX(Component.RIGHT_ALIGNMENT);
            pricePanel.add(badge);
        }

        // Action Buttons Container (Heart & Link)
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actions.setOpaque(false);

        // Heart Toggle (Watchlist)
        JButton favBtn = new JButton(frame.getFavoritesManager().isFavorite(product.getName()) ? "❤️" : "🤍");
        favBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        favBtn.setContentAreaFilled(false);
        favBtn.setBorderPainted(false);
        favBtn.setFocusPainted(false);
        favBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        favBtn.addActionListener(e -> {
            frame.getFavoritesManager().toggleFavorite(product.getName());
            favBtn.setText(frame.getFavoritesManager().isFavorite(product.getName()) ? "❤️" : "🤍");
        });

        // External Link
        JButton linkBtn = new JButton("🔗");
        linkBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        linkBtn.setContentAreaFilled(false);
        linkBtn.setBorderPainted(false);
        linkBtn.setFocusPainted(false);
        linkBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkBtn.addActionListener(e -> openLink(product.getLink()));

        actions.add(favBtn);
        actions.add(linkBtn);

        right.add(pricePanel);
        right.add(actions);

        add(imgContainer, BorderLayout.WEST);
        add(info, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        // --- INTERACTIVITY ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(BG_HOVER);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(isCheapest ? BG_CHEAP : BG_CARD);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(1));
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
        g2.dispose();
    }

    private void loadProductImage(String url, JLabel label) {
        if (url == null || !url.startsWith("http")) return;
        IMG_POOL.submit(() -> {
            try {
                java.net.URL u = new java.net.URL(url);
                Image img = javax.imageio.ImageIO.read(u);
                if (img != null) {
                    Image scaled = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> {
                        label.setIcon(new ImageIcon(scaled));
                        label.setText("");
                    });
                }
            } catch (Exception ignored) {}
        });
    }

    private void openLink(String url) {
        if (url == null || url.isEmpty() || url.equals("No Link")) return;
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not open browser.");
        }
    }

    private String getEmoji(String cat) {
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