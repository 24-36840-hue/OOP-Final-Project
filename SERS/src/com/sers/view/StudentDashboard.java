package com.sers.view;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {
    public StudentDashboard() {
        setTitle("Student Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 0, 584, 338);
        tabbedPane.addTab("Profile", createProfilePanel());
        tabbedPane.addTab("Enroll Courses", createEnrollmentPanel());
        tabbedPane.addTab("View Status", createStatusPanel());
        tabbedPane.addTab("Billing", createBillingPanel());

        getContentPane().add(tabbedPane);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(495, 338, 89, 23);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                dispose();  // Close current dashboard
            }
        });
        getContentPane().add(logoutButton);
    }

    private JPanel createProfilePanel() {
        // JTextFields for updating profile, save button
        JPanel panel = new JPanel();
        panel.setLayout(null);
        return panel;
    }

    private JPanel createEnrollmentPanel() {
        // JList or JTable for courses, enroll button
        return new JPanel();
    }

    private JPanel createStatusPanel() {
        // Display enrollment status
        return new JPanel();
    }

    private JPanel createBillingPanel() {
        // Show fees
        return new JPanel();
    }
}