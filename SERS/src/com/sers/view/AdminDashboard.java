package com.sers.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.sers.dao.DBConnection;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        // Tabs for different functions
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 0, 584, 338);
        tabbedPane.addTab("Manage Students", createStudentPanel());
        tabbedPane.addTab("Manage Courses", createCoursePanel());
        tabbedPane.addTab("Enrollment Approvals", createEnrollmentPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        getContentPane().add(tabbedPane);
        
     // Add Logout button at the bottom
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(480, 338, 104, 23);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                dispose();  // Close current dashboard
            }
        });
        getContentPane().add(logoutButton);
    }

 // ... existing imports and class structure ...

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Form panel for inputs
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 5, 5));  // 10 rows for fields
        
        // Input fields
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> sexCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField addressField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField guardianField = new JTextField();
        JTextField guardianContactField = new JTextField();
        JComboBox<String> yearLevelCombo = new JComboBox<>(new String[]{"freshmen", "sophomore", "junior", "senior"});
        JComboBox<String> classificationCombo = new JComboBox<>(new String[]{"regular", "irregular"});
        
        // Add labels and fields
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageField);
        formPanel.add(new JLabel("Sex:"));
        formPanel.add(sexCombo);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Guardian:"));
        formPanel.add(guardianField);
        formPanel.add(new JLabel("Guardian Contact:"));
        formPanel.add(guardianContactField);
        formPanel.add(new JLabel("Year Level:"));
        formPanel.add(yearLevelCombo);
        formPanel.add(new JLabel("Classification:"));
        formPanel.add(classificationCombo);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Add button with action
        JButton addStudentBtn = new JButton("Add Student");
        addStudentBtn.addActionListener(e -> {
            // Get input values
            String name = nameField.getText().trim();
            String ageText = ageField.getText().trim();
            String sex = (String) sexCombo.getSelectedItem();
            String address = addressField.getText().trim();
            String contact = contactField.getText().trim();
            String guardian = guardianField.getText().trim();
            String guardianContact = guardianContactField.getText().trim();
            String yearLevel = (String) yearLevelCombo.getSelectedItem();
            String classification = (String) classificationCombo.getSelectedItem();
            
            // Basic validation
            if (name.isEmpty() || ageText.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill in Name, Age, and Address.");
                return;
            }
            int age;
            try {
                age = Integer.parseInt(ageText);
                if (age < 16 || age > 100) {
                    JOptionPane.showMessageDialog(panel, "Age must be between 16 and 100.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Age must be a valid number.");
                return;
            }
            
            // Generate a unique username (e.g., based on name)
            String username = name.replaceAll("\\s+", "").toLowerCase() + age;  // Simple generation
            String password = "default123";  // Default password; admin can change later
            
            try (Connection conn = DBConnection.getConnection()) {
                // Check if username exists
                PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE username=?");
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(panel, "Generated username already exists. Try different details.");
                    return;
                }
                
                // Insert into users
                PreparedStatement userStmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'student')", PreparedStatement.RETURN_GENERATED_KEYS);
                userStmt.setString(1, username);
                userStmt.setString(2, password);  // Hash in production
                userStmt.executeUpdate();
                
                // Get generated user_id
                ResultSet keys = userStmt.getGeneratedKeys();
                if (keys.next()) {
                    int userId = keys.getInt(1);
                    
                    // Insert into students
                    PreparedStatement studentStmt = conn.prepareStatement("INSERT INTO students (user_id, name, age, sex, address, contact, guardian, guardian_contact, year_level, classification) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    studentStmt.setInt(1, userId);
                    studentStmt.setString(2, name);
                    studentStmt.setInt(3, age);
                    studentStmt.setString(4, sex);
                    studentStmt.setString(5, address);
                    studentStmt.setString(6, contact);
                    studentStmt.setString(7, guardian);
                    studentStmt.setString(8, guardianContact);
                    studentStmt.setString(9, yearLevel);
                    studentStmt.setString(10, classification);
                    studentStmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(panel, "Student added successfully! Username: " + username + ", Password: " + password);
                    
                    // Clear fields
                    nameField.setText("");
                    ageField.setText("");
                    addressField.setText("");
                    contactField.setText("");
                    guardianField.setText("");
                    guardianContactField.setText("");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error adding student. Check database connection.");
            }
        });
        
        panel.add(addStudentBtn, BorderLayout.SOUTH);
        return panel;
    }

    
    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Form panel for adding/updating courses
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));  // 8 rows for fields
        
        // Input fields
        JTextField codeField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField creditsField = new JTextField();
        JTextField prerequisitesField = new JTextField();
        JTextField semesterField = new JTextField();
        JTextField programField = new JTextField();
        
        // Add labels and fields
        formPanel.add(new JLabel("Course Code:"));
        formPanel.add(codeField);
        formPanel.add(new JLabel("Course Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Credits:"));
        formPanel.add(creditsField);
        formPanel.add(new JLabel("Prerequisites:"));
        formPanel.add(prerequisitesField);
        formPanel.add(new JLabel("Semester:"));
        formPanel.add(semesterField);
        formPanel.add(new JLabel("Program:"));
        formPanel.add(programField);
        
        // Buttons for actions
        JButton addCourseBtn = new JButton("Add Course");
        JButton updateCourseBtn = new JButton("Update Selected Course");
        JButton deleteCourseBtn = new JButton("Delete Selected Course");
        JButton refreshBtn = new JButton("Refresh List");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addCourseBtn);
        buttonPanel.add(updateCourseBtn);
        buttonPanel.add(deleteCourseBtn);
        buttonPanel.add(refreshBtn);
        
        // Table to display courses
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Code", "Name", "Credits", "Prerequisites", "Semester", "Program"}, 0);
        JTable courseTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(courseTable);
        
        // Load courses into table
        refreshTable(tableModel);
        
        // Layout: Form on top, table in center, buttons at bottom
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        
        // Add Course Button Action
        addCourseBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String creditsText = creditsField.getText().trim();
            String prerequisites = prerequisitesField.getText().trim();
            String semester = semesterField.getText().trim();
            String program = programField.getText().trim();
            
            // Validation
            if (code.isEmpty() || name.isEmpty() || creditsText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill in Code, Name, and Credits.");
                return;
            }
            int credits;
            try {
                credits = Integer.parseInt(creditsText);
                if (credits <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Credits must be a positive number.");
                return;
            }
            
            try (Connection conn = DBConnection.getConnection()) {
                // Check unique code
                PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM courses WHERE code=?");
                checkStmt.setString(1, code);
                if (checkStmt.executeQuery().next()) {
                    JOptionPane.showMessageDialog(panel, "Course code already exists.");
                    return;
                }
                
                // Insert
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO courses (code, name, credits, prerequisites, semester, program) VALUES (?, ?, ?, ?, ?, ?)");
                stmt.setString(1, code);
                stmt.setString(2, name);
                stmt.setInt(3, credits);
                stmt.setString(4, prerequisites);
                stmt.setString(5, semester);
                stmt.setString(6, program);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(panel, "Course added successfully!");
                clearForm(codeField, nameField, creditsField, prerequisitesField, semesterField, programField);
                refreshTable(tableModel);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error adding course.");
            }
        });
        
        // Update Course Button Action
        updateCourseBtn.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Select a course to update.");
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            // Similar to add, but update query: UPDATE courses SET ... WHERE id=?
            // (Implement similarly with validation)
            JOptionPane.showMessageDialog(panel, "Update functionality: Implement as needed.");  // Placeholder
        });
        
        // Delete Course Button Action
        deleteCourseBtn.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Select a course to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(panel, "Delete this course?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM courses WHERE id=?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(panel, "Course deleted.");
                    refreshTable(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error deleting course.");
                }
            }
        });
        
        // Refresh Button Action
        refreshBtn.addActionListener(e -> refreshTable(tableModel));
        
        return panel;
    }

    // Helper method to refresh table
    private void refreshTable(DefaultTableModel model) {
        model.setRowCount(0);  // Clear table
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM courses")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("code"), rs.getString("name"), rs.getInt("credits"), rs.getString("prerequisites"), rs.getString("semester"), rs.getString("program")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Helper method to clear form
    private void clearForm(JTextField... fields) {
        for (JTextField field : fields) field.setText("");
    }

    // ... existing imports (add if needed: import javax.swing.table.DefaultTableModel; for JTable)

private JPanel createEnrollmentPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    
    // Table to display enrollments
    DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Student Name", "Course Name", "Term", "Status", "Remarks"}, 0);
    JTable enrollmentTable = new JTable(tableModel);
    JScrollPane tableScroll = new JScrollPane(enrollmentTable);
    
    // Load pending enrollments
    refreshEnrollmentTable(tableModel);
    
    // Remarks text area
    JTextArea remarksArea = new JTextArea(3, 20);
    JScrollPane remarksScroll = new JScrollPane(remarksArea);
    remarksArea.setText("Add remarks here...");
    
    // Buttons
    JButton approveBtn = new JButton("Approve Selected");
    JButton rejectBtn = new JButton("Reject Selected");
    JButton refreshBtn = new JButton("Refresh List");
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(approveBtn);
    buttonPanel.add(rejectBtn);
    buttonPanel.add(refreshBtn);
    
    // Layout: Table in center, remarks and buttons at bottom
    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.add(new JLabel("Remarks:"), BorderLayout.NORTH);
    bottomPanel.add(remarksScroll, BorderLayout.CENTER);
    bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    panel.add(tableScroll, BorderLayout.CENTER);
    panel.add(bottomPanel, BorderLayout.SOUTH);
    
    // Approve Button Action
    approveBtn.addActionListener(e -> {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(panel, "Select an enrollment to approve.");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String remarks = remarksArea.getText().trim();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE enrollments SET status='accepted', remarks=? WHERE id=?")) {
            stmt.setString(1, remarks);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(panel, "Enrollment approved.");
            refreshEnrollmentTable(tableModel);
            remarksArea.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error approving enrollment.");
        }
    });
    
    // Reject Button Action
    rejectBtn.addActionListener(e -> {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(panel, "Select an enrollment to reject.");
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String remarks = remarksArea.getText().trim();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE enrollments SET status='rejected', remarks=? WHERE id=?")) {
            stmt.setString(1, remarks);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(panel, "Enrollment rejected.");
            refreshEnrollmentTable(tableModel);
            remarksArea.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error rejecting enrollment.");
        }
    });
    
    // Refresh Button Action
    refreshBtn.addActionListener(e -> refreshEnrollmentTable(tableModel));
    
    return panel;
}

// Helper method to refresh enrollment table (focus on pending)
private void refreshEnrollmentTable(DefaultTableModel model) {
    model.setRowCount(0);  // Clear table
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(
             "SELECT e.id, s.name AS student_name, c.name AS course_name, e.term, e.status, e.remarks " +
             "FROM enrollments e " +
             "JOIN students s ON e.student_id = s.id " +
             "JOIN courses c ON e.course_id = c.id " +
             "WHERE e.status = 'pending'")) {  // Only pending for approvals
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("student_name"),
                rs.getString("course_name"),
                rs.getString("term"),
                rs.getString("status"),
                rs.getString("remarks")
            });
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}


//... existing imports (add if needed: import javax.swing.table.DefaultTableModel; import java.io.FileWriter; for CSV export)

private JPanel createReportsPanel() {
 JPanel panel = new JPanel(new BorderLayout());
 
 // Buttons for report types
 JButton enrollmentSummaryBtn = new JButton("Enrollment Summary by Term");
 JButton studentDemographicsBtn = new JButton("Student Demographics");
 JButton exportBtn = new JButton("Export to CSV");
 
 JPanel buttonPanel = new JPanel();
 buttonPanel.add(enrollmentSummaryBtn);
 buttonPanel.add(studentDemographicsBtn);
 buttonPanel.add(exportBtn);
 
 // Table to display report data
 DefaultTableModel tableModel = new DefaultTableModel();
 JTable reportTable = new JTable(tableModel);
 JScrollPane tableScroll = new JScrollPane(reportTable);
 
 panel.add(buttonPanel, BorderLayout.NORTH);
 panel.add(tableScroll, BorderLayout.CENTER);
 
 // Enrollment Summary Button Action
 enrollmentSummaryBtn.addActionListener(e -> {
     tableModel.setColumnIdentifiers(new String[]{"Term", "Total Enrollments", "Accepted", "Rejected", "Pending"});
     tableModel.setRowCount(0);
     try (Connection conn = DBConnection.getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(
              "SELECT term, COUNT(*) AS total, " +
              "SUM(CASE WHEN status='accepted' THEN 1 ELSE 0 END) AS accepted, " +
              "SUM(CASE WHEN status='rejected' THEN 1 ELSE 0 END) AS rejected, " +
              "SUM(CASE WHEN status='pending' THEN 1 ELSE 0 END) AS pending " +
              "FROM enrollments GROUP BY term")) {
         while (rs.next()) {
             tableModel.addRow(new Object[]{
                 rs.getString("term"),
                 rs.getInt("total"),
                 rs.getInt("accepted"),
                 rs.getInt("rejected"),
                 rs.getInt("pending")
             });
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
     }
 });
 
 // Student Demographics Button Action
 studentDemographicsBtn.addActionListener(e -> {
     tableModel.setColumnIdentifiers(new String[]{"Year Level", "Regular", "Irregular", "Total"});
     tableModel.setRowCount(0);
     try (Connection conn = DBConnection.getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(
              "SELECT year_level, " +
              "SUM(CASE WHEN classification='regular' THEN 1 ELSE 0 END) AS regular, " +
              "SUM(CASE WHEN classification='irregular' THEN 1 ELSE 0 END) AS irregular, " +
              "COUNT(*) AS total FROM students GROUP BY year_level")) {
         while (rs.next()) {
             tableModel.addRow(new Object[]{
                 rs.getString("year_level"),
                 rs.getInt("regular"),
                 rs.getInt("irregular"),
                 rs.getInt("total")
             });
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
     }
 });
 
 // Export to CSV Button Action
 exportBtn.addActionListener(e -> {
     if (tableModel.getRowCount() == 0) {
         JOptionPane.showMessageDialog(panel, "No data to export. Generate a report first.");
         return;
     }
     try {
         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setSelectedFile(new File("report.csv"));
         if (fileChooser.showSaveDialog(panel) == JFileChooser.APPROVE_OPTION) {
             File file = fileChooser.getSelectedFile();
             try (FileWriter writer = new FileWriter(file)) {
                 // Write headers
                 for (int i = 0; i < tableModel.getColumnCount(); i++) {
                     writer.write(tableModel.getColumnName(i) + (i < tableModel.getColumnCount() - 1 ? "," : "\n"));
                 }
                 // Write data
                 for (int i = 0; i < tableModel.getRowCount(); i++) {
                     for (int j = 0; j < tableModel.getColumnCount(); j++) {
                         writer.write(tableModel.getValueAt(i, j).toString() + (j < tableModel.getColumnCount() - 1 ? "," : "\n"));
                     }
                 }
                 JOptionPane.showMessageDialog(panel, "Report exported to " + file.getAbsolutePath());
             }
         }
     } catch (IOException ex) {
         ex.printStackTrace();
         JOptionPane.showMessageDialog(panel, "Error exporting report.");
     }
 });
 
 return panel;
}

    
}
