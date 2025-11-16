package com.sers.view;

import com.sers.dao.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Registration extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;

    public Registration() {
        setTitle("SERS Student Registration");
        setSize(400, 250);  // Adjusted size for fewer components
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));  // Reduced to 5 rows

        // Add components (removed role selection)
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        // Note: Role is automatically set to "student"
        add(new JLabel("Note: Registration is for students only."));
        add(new JLabel(""));  // Empty label for layout

        registerButton = new JButton("Register as Student");
        add(registerButton);

        backButton = new JButton("Back to Login");
        add(backButton);

        // Register button action (hardcoded role to "student")
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String role = "student";  // Hardcoded to student

                // Basic validation
                if (username.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields and ensure passwords match.");
                    return;
                }
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(null, "Password must be at least 6 characters.");
                    return;
                }

                // Check if username exists
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE username=?")) {
                    checkStmt.setString(1, username);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Username already exists. Choose another.");
                        return;
                    }

                    // Insert new user
                    PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, password);  // In production, hash this
                    insertStmt.setString(3, role);
                    insertStmt.executeUpdate();

                    // Create basic student profile
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        PreparedStatement studentStmt = conn.prepareStatement("INSERT INTO students (user_id, name, year_level, classification) VALUES (?, 'New Student', 'freshmen', 'regular')");
                        studentStmt.setInt(1, userId);
                        studentStmt.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(null, "Registration successful! Please log in as a student.");
                    new Login().setVisible(true);
                    dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Registration failed. Try again.");
                }
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }
}