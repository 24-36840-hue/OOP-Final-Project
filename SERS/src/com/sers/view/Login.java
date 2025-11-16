package com.sers.view;

import javax.swing.*;

import com.sers.dao.DBConnection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public Login() {
        setTitle("SERS Login");
        setSize(496, 446);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JLabel label = new JLabel("Username:");
        label.setBounds(28, 20, 101, 33);
        getContentPane().add(label);
        usernameField = new JTextField();
        usernameField.setBounds(139, 11, 255, 51);
        getContentPane().add(usernameField);

        JLabel label_1 = new JLabel("Password:");
        label_1.setBounds(28, 117, 84, 33);
        getContentPane().add(label_1);
        passwordField = new JPasswordField();
        passwordField.setBounds(139, 108, 255, 51);
        getContentPane().add(passwordField);
        
        registerButton = new JButton("Register New User");
        registerButton.setBounds(168, 261, 201, 51);
        getContentPane().add(registerButton);

        loginButton = new JButton("Login");
        loginButton.setBounds(168, 183, 201, 51);
        getContentPane().add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username=? AND password=?")) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String role = rs.getString("role");
                        if ("admin".equals(role)) {
                            new AdminDashboard().setVisible(true);
                        } else {
                            new StudentDashboard().setVisible(true);
                        }
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid credentials");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
      
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Registration().setVisible(true);
                dispose();  // Close login window
            }
        });
    }

    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}