import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class UnifiedApp1028 extends JFrame {
    private static final String ATTENDANCE_URL = "jdbc:mysql://localhost:3310/attendanceDB?useSSL=false&serverTimezone=UTC"; // Attendance DB
    private static final String DETAILS_URL = "jdbc:mysql://localhost:3310/studentDetailsDB?useSSL=false&serverTimezone=UTC"; // Student Details DB
    private static final String USER = "root";
    private static final String PASSWORD = "20214637"; // Update password

    // GPA Calculator variables
    private JComboBox<String>[] creditBoxes;
    private JComboBox<String>[] gradeBoxes;
    private JLabel resultLabelSem1;
    private JLabel resultLabelSem2;
    private static final double[] GRADE_POINTS = {10.0, 9.5, 9.0, 8.5, 8.0, 7.0};
    private static final String[] GRADES = {"O", "A+", "A", "B+", "B", "C"};

    // Attendance Management variables
    private JTextField studentNameField;
    private JTextField studentIdField;
    private JTextArea attendanceArea;

    // PDF Viewer variables
    private DefaultListModel<String> listModel;
    private JList<String> pdfList;
    private ArrayList<File> pdfFiles = new ArrayList<>();

    // Student Details Management variables
    private JTextField detailStudentNameField;
    private JTextField detailStudentIdField;
    private JTextField detailStudentAgeField;
    private JTextField detailStudentGpaField;
    private JTextField detailStudentContactField;
    private JTextField detailStudentDepartmentField;
    private JTextArea detailStudentAddressField;
    private JTextArea detailsArea;

    public UnifiedApp1028() {
        // Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found.");
            return;
        }

        // Set up the frame
        setTitle("Unified Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // GPA Calculator Tab
        tabbedPane.addTab("GPA Calculator", createGPATab());

        // Student Attendance Management Tab
        tabbedPane.addTab("Attendance", createAttendanceTab());

        // PDF Viewer Tab
        tabbedPane.addTab("PDF Viewer", createPDFViewerTab());

        // Student Details Tab
        tabbedPane.addTab("Student Details", createStudentDetailsTab());

        add(tabbedPane);
        setVisible(true);
    }

    // GPA Calculator Tab
    private JPanel createGPATab() {
        JPanel panel = new JPanel(new GridLayout(0, 3, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize the creditBoxes and gradeBoxes to fit all subjects (both semesters)
        int totalSubjects = 6 + 7; // Semester 1 has 6 subjects, Semester 2 has 7 subjects
        creditBoxes = new JComboBox[totalSubjects];
        gradeBoxes = new JComboBox[totalSubjects];

        // Semester 1 header
        panel.add(new JLabel("Semester 1", SwingConstants.CENTER));
        panel.add(new JLabel("Credits", SwingConstants.CENTER));
        panel.add(new JLabel("Grades", SwingConstants.CENTER));

        String[] semester1Subjects = {"Semiconductor", "PPS", "EEE", "MATHS", "EGD", "English"};
        addSubjects(panel, semester1Subjects, 0); // Add Semester 1 subjects starting from index 0

        // Semester 2 header
        panel.add(new JLabel("Semester 2", SwingConstants.CENTER));
        panel.add(new JLabel("Credits", SwingConstants.CENTER));
        panel.add(new JLabel("Grades", SwingConstants.CENTER));

        String[] semester2Subjects = {"OODP", "Biology", "BCMW", "POE", "COMPLEX MATHS", "Chemistry", "German"};
        addSubjects(panel, semester2Subjects, 6); // Add Semester 2 subjects starting from index 6

        JButton calculateButton = new JButton("Calculate GPA");
        panel.add(calculateButton);
        panel.add(new JLabel("Semester 1 GPA: ", SwingConstants.CENTER)); // Placeholder for result
        resultLabelSem1 = new JLabel("", SwingConstants.CENTER);
        panel.add(resultLabelSem1);

        panel.add(new JLabel("Semester 2 GPA: ", SwingConstants.CENTER)); // Placeholder for result
        resultLabelSem2 = new JLabel("", SwingConstants.CENTER);
        panel.add(resultLabelSem2);

        calculateButton.addActionListener(e -> calculateGPA());

        return panel;
    }

    // Updated addSubjects method with the starting index parameter
    private void addSubjects(JPanel panel, String[] subjects, int startIndex) {
        int length = subjects.length;

        for (int i = 0; i < length; i++) {
            panel.add(new JLabel(subjects[i])); // Subject name
            creditBoxes[startIndex + i] = new JComboBox<>(new String[]{"0", "1", "2", "3", "4", "5"});
            panel.add(creditBoxes[startIndex + i]);
            gradeBoxes[startIndex + i] = new JComboBox<>(GRADES);
            panel.add(gradeBoxes[startIndex + i]);

            // Add action listeners to update GPA on change
            creditBoxes[startIndex + i].addActionListener(e -> calculateGPA());
            gradeBoxes[startIndex + i].addActionListener(e -> calculateGPA());
        }
    }

    // GPA Calculation Method
    private void calculateGPA() {
        double totalPointsSem1 = 0.0;
        double totalPointsSem2 = 0.0;
        int totalCreditsSem1 = 0;
        int totalCreditsSem2 = 0;

        // Calculate GPA for Semester 1 (first 6 subjects)
        for (int i = 0; i < 6; i++) {
            int credits = Integer.parseInt((String) creditBoxes[i].getSelectedItem());
            int gradeIndex = gradeBoxes[i].getSelectedIndex();

            if (credits > 0) {
                totalPointsSem1 += GRADE_POINTS[gradeIndex] * credits;
                totalCreditsSem1 += credits;
            }
        }

        // Calculate GPA for Semester 2 (remaining subjects)
        for (int i = 6; i < creditBoxes.length; i++) {
            int credits = Integer.parseInt((String) creditBoxes[i].getSelectedItem());
            int gradeIndex = gradeBoxes[i].getSelectedIndex();

            if (credits > 0) {
                totalPointsSem2 += GRADE_POINTS[gradeIndex] * credits;
                totalCreditsSem2 += credits;
            }
        }

        // Ensure that we calculate GPA only if there are credits
        double gpaSem1 = (totalCreditsSem1 > 0) ? totalPointsSem1 / totalCreditsSem1 : 0.0;
        double gpaSem2 = (totalCreditsSem2 > 0) ? totalPointsSem2 / totalCreditsSem2 : 0.0;

        // Update the result labels with formatted GPA
        resultLabelSem1.setText(String.format("%.2f", gpaSem1));
        resultLabelSem2.setText(String.format("%.2f", gpaSem2));
    }

    // Student Attendance Management Tab
    private JPanel createAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Student Name: "));
        studentNameField = new JTextField();
        inputPanel.add(studentNameField);

        inputPanel.add(new JLabel("Student ID: "));
        studentIdField = new JTextField();
        inputPanel.add(studentIdField);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Student");
        JButton markButton = new JButton("Mark Attendance");
        JButton viewButton = new JButton("View Attendance");

        buttonPanel.add(addButton);
        buttonPanel.add(markButton);
        buttonPanel.add(viewButton);

        attendanceArea = new JTextArea();
        attendanceArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(attendanceArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addStudent());
        markButton.addActionListener(e -> markAttendance());
        viewButton.addActionListener(e -> viewAttendance());

        return panel;
    }

    private void addStudent() {
        String name = studentNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student name.");
            return;
        }

        String query = "INSERT INTO attendance (name, attendance_count) VALUES (?, 0)";
        try (Connection conn = DriverManager.getConnection(ATTENDANCE_URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added successfully.");
            studentNameField.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage());
        }
    }

    private void markAttendance() {
        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student ID.");
            return;
        }

        String query = "UPDATE attendance SET attendance_count = attendance_count + 1 WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(ATTENDANCE_URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, studentId);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Attendance marked for student ID: " + studentId);
            } else {
                JOptionPane.showMessageDialog(this, "Student ID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error marking attendance: " + e.getMessage());
        }
    }

    private void viewAttendance() {
        String query = "SELECT name, attendance_count FROM attendance";
        StringBuilder attendanceRecords = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(ATTENDANCE_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("name");
                int count = rs.getInt("attendance_count");
                attendanceRecords.append(name).append(": ").append(count).append("\n");
            }
            attendanceArea.setText(attendanceRecords.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving attendance: " + e.getMessage());
        }
    }

    // PDF Viewer Tab
    private JPanel createPDFViewerTab() {
        JPanel panel = new JPanel(new BorderLayout());

        listModel = new DefaultListModel<>();
        pdfList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(pdfList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Load PDF");
        JButton viewButton = new JButton("View PDF");

        loadButton.addActionListener(e -> loadPDF());
        viewButton.addActionListener(e -> viewPDF());

        buttonPanel.add(loadButton);
        buttonPanel.add(viewButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            pdfFiles.add(selectedFile);
            listModel.addElement(selectedFile.getAbsolutePath());
        }
    }

    private void viewPDF() {
        String selectedPath = pdfList.getSelectedValue();
        if (selectedPath != null) {
            File file = new File(selectedPath);
            if (file.exists() && file.isFile()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No PDF selected", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Student Details Management Tab
    private JPanel createStudentDetailsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.add(new JLabel("Name:"));
        detailStudentNameField = new JTextField();
        inputPanel.add(detailStudentNameField);

        inputPanel.add(new JLabel("ID:"));
        detailStudentIdField = new JTextField();
        inputPanel.add(detailStudentIdField);

        inputPanel.add(new JLabel("Age:"));
        detailStudentAgeField = new JTextField();
        inputPanel.add(detailStudentAgeField);

        inputPanel.add(new JLabel("GPA:"));
        detailStudentGpaField = new JTextField();
        inputPanel.add(detailStudentGpaField);

        inputPanel.add(new JLabel("Contact:"));
        detailStudentContactField = new JTextField();
        inputPanel.add(detailStudentContactField);

        inputPanel.add(new JLabel("Department:"));
        detailStudentDepartmentField = new JTextField();
        inputPanel.add(detailStudentDepartmentField);

        inputPanel.add(new JLabel("Address:"));
        detailStudentAddressField = new JTextArea();
        inputPanel.add(new JScrollPane(detailStudentAddressField));

        JButton saveButton = new JButton("Save Details");
        JButton viewDetailsButton = new JButton("View Details");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(viewDetailsButton);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(detailsArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveStudentDetails());
        viewDetailsButton.addActionListener(e -> viewStudentDetails());

        return panel;
    }

    private void saveStudentDetails() {
        String name = detailStudentNameField.getText();
        String id = detailStudentIdField.getText();
        String age = detailStudentAgeField.getText();
        String gpa = detailStudentGpaField.getText();
        String contact = detailStudentContactField.getText();
        String department = detailStudentDepartmentField.getText();
        String address = detailStudentAddressField.getText();

        String query = "INSERT INTO studentDetails (name, id, age, gpa, contact, department, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DETAILS_URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, id);
            ps.setString(3, age);
            ps.setString(4, gpa);
            ps.setString(5, contact);
            ps.setString(6, department);
            ps.setString(7, address);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student details saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving student details: " + e.getMessage());
        }
    }

    private void viewStudentDetails() {
        StringBuilder details = new StringBuilder();
        String query = "SELECT * FROM studentDetails";
        try (Connection conn = DriverManager.getConnection(DETAILS_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                details.append("Name: ").append(rs.getString("name"))
                        .append(", ID: ").append(rs.getString("id"))
                        .append(", Age: ").append(rs.getString("age"))
                        .append(", GPA: ").append(rs.getString("gpa"))
                        .append(", Contact: ").append(rs.getString("contact"))
                        .append(", Department: ").append(rs.getString("department"))
                        .append(", Address: ").append(rs.getString("address"))
                        .append("\n");
            }
            detailsArea.setText(details.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving student details: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UnifiedApp1028::new);
    }
}
