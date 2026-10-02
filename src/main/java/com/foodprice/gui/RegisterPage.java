package com.foodprice.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class RegisterPage extends JPanel {

    private final MainFrame frame;
    private JTextField nameField, emailField;
    private JPasswordField passwordField, confirmField;
    private JLabel messageLabel;

    // Consistency Palette
    private static final Color BG      = new Color(15, 23, 42);
    private static final Color CARD    = new Color(30, 41, 59);
    private static final Color BORDER  = new Color(71, 85, 105);
    private static final Color ACCENT  = new Color(34, 197, 94); // Green
    private static final Color WHITE   = Color.WHITE;
    private static final Color RED     = new Color(239, 68, 68);
    private static final Color GREEN   = new Color(34, 197, 94);
    private static final Color TEXT_HINT = new Color(203, 213, 225); 

    public RegisterPage(MainFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        add(buildNav(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        form.setPreferredSize(new Dimension(460, 620));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 0);

        g.gridy = 0;
        JLabel logo = new JLabel("🛒 FoodPriceAnalyzer");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(ACCENT);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(logo, g);

        g.gridy = 1;
        JLabel title = new JLabel("Create Your Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(title, g);

        g.gridy = 2;
        JLabel sub = new JLabel("Track grocery deals across 5 stores");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_HINT);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(sub, g);

        g.gridy = 3; form.add(fieldLabel("Full Name"), g);
        g.gridy = 4; nameField = darkField(); form.add(nameField, g);
        
        g.gridy = 5; form.add(fieldLabel("Email Address"), g);
        g.gridy = 6; emailField = darkField(); form.add(emailField, g);
        
        g.gridy = 7; form.add(fieldLabel("Password"), g);
        g.gridy = 8; passwordField = darkPassField(); form.add(passwordField, g);
        
        g.gridy = 9; form.add(fieldLabel("Confirm Password"), g);
        g.gridy = 10; confirmField = darkPassField(); form.add(confirmField, g);

        g.gridy = 11;
        JLabel passHint = new JLabel("ⓘ Password must be at least 6 characters long");
        passHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        passHint.setForeground(TEXT_HINT); 
        form.add(passHint, g);

        g.gridy = 12;
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(messageLabel, g);

        g.gridy = 13;
        JButton regBtn = new JButton("Create Account");
        regBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        regBtn.setBackground(ACCENT);
        regBtn.setForeground(WHITE); // SET TO WHITE
        regBtn.setFocusPainted(false);
        regBtn.setBorderPainted(false);
        regBtn.setPreferredSize(new Dimension(0, 46));
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> register());
        form.add(regBtn, g);

        g.gridy = 14;
        JLabel loginLnk = new JLabel(
            "<html><center style='color:#ffffff'>Already have an account? " +
            "<u>Login here</u></center></html>"); // SET TO WHITE AND UNDERLINED
        loginLnk.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginLnk.setHorizontalAlignment(SwingConstants.CENTER);
        loginLnk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLnk.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showLoginDialog(); }
        });
        form.add(loginLnk, g);

        center.add(form);
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(15, 23, 42));
        nav.setPreferredSize(new Dimension(0, 70));
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            BorderFactory.createEmptyBorder(0, 25, 0, 25)
        ));

        JLabel logo = new JLabel("🛒 FoodPriceAnalyzer.ca");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(WHITE);

        JButton homeBtn = new JButton("← Home");
        homeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        homeBtn.setForeground(WHITE);
        homeBtn.setBackground(new Color(51, 65, 85));
        homeBtn.setFocusPainted(false);
        homeBtn.setBorderPainted(false);
        homeBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        homeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> frame.showPage("HOME"));

        nav.add(logo, BorderLayout.WEST);
        nav.add(homeBtn, BorderLayout.EAST);
        return nav;
    }

    private void register() {
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String pass    = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showMsg("All fields are required.", RED); return;
        }
        if (!Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", email)) {
            showMsg("Please enter a valid email address.", RED); return;
        }
        if (pass.length() < 6) {
            showMsg("Password must be at least 6 characters.", RED); return;
        }
        if (!pass.equals(confirm)) {
            showMsg("Passwords do not match.", RED); return;
        }
        if (emailExists(email)) {
            showMsg("This email is already registered.", RED); return;
        }

        saveUser(name, email, pass);
        showMsg("✅ Account created! Welcome, " + name + "!", GREEN);
        
        nameField.setText(""); emailField.setText("");
        passwordField.setText(""); confirmField.setText("");
    }

    private void showLoginDialog() {
        JDialog d = new JDialog();
        d.setTitle("Login");
        d.setSize(400, 350);
        d.setLocationRelativeTo(this);
        d.setModal(true);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CARD);
        p.setBorder(new EmptyBorder(25, 35, 25, 35));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(7, 0, 7, 0);

        g.gridy = 0;
        JLabel t = new JLabel("Welcome Back!");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(WHITE);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(t, g);

        g.gridy = 1; p.add(fieldLabel("Email"), g);
        g.gridy = 2;
        JTextField loginEmail = darkField();
        p.add(loginEmail, g);

        g.gridy = 3; p.add(fieldLabel("Password"), g);
        g.gridy = 4;
        JPasswordField loginPass = darkPassField();
        p.add(loginPass, g);

        g.gridy = 5;
        JLabel msg = new JLabel(" ");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(msg, g);

        g.gridy = 6;
        JButton btn = new JButton("Login");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(ACCENT);
        btn.setForeground(WHITE); // SET TO WHITE
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(0, 42));
        
        // Inside RegisterPage.java, inside showLoginDialog()'s ActionEvent
ActionListener loginAction = e -> {
    String email = loginEmail.getText().trim();
    String pass = new String(loginPass.getPassword());
    
    if (validateLogin(email, pass)) {
        d.dispose();
        // Get the user's name from the file (or just use the email for now)
        // For this example, we'll assume we pass the successful name
        frame.setLoggedInUser(email); 
        JOptionPane.showMessageDialog(this, "Welcome back!", "Login", JOptionPane.INFORMATION_MESSAGE);
        frame.showPage("HOME");
    } else {
        msg.setForeground(RED);
        msg.setText("Invalid email or password.");
    }
};

        btn.addActionListener(loginAction);
        loginPass.addActionListener(loginAction); 
        p.add(btn, g);

        d.add(p);
        d.setVisible(true);
    }

    private boolean validateLogin(String email, String password) {
        File f = new File("users/users.txt");
        if (!f.exists()) return false;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] pts = line.split("\\|");
                if (pts.length >= 3 && pts[1].equals(email) && pts[2].equals(password)) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean emailExists(String email) {
        File f = new File("users/users.txt");
        if (!f.exists()) return false;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] pts = line.split("\\|");
                if (pts.length >= 2 && pts[1].equals(email)) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private void saveUser(String name, String email, String password) {
        try {
            new File("users").mkdirs();
            try (PrintWriter w = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream("users/users.txt", true), StandardCharsets.UTF_8))) {
                w.println(name + "|" + email + "|" + password);
            }
        } catch (Exception ignored) {}
    }

    private void showMsg(String msg, Color c) {
        messageLabel.setText(msg);
        messageLabel.setForeground(c);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(WHITE); 
        return l;
    }

    private JTextField darkField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(15, 23, 42));
        f.setForeground(WHITE);
        f.setCaretColor(WHITE);
        f.setPreferredSize(new Dimension(0, 42));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        return f;
    }

    private JPasswordField darkPassField() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(15, 23, 42));
        f.setForeground(WHITE);
        f.setCaretColor(WHITE);
        f.setPreferredSize(new Dimension(0, 42));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        return f;
    }
}