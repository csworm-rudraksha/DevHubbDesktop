import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import javax.swing.*;

public class Login extends JFrame {
    private JPanel loginPanel, signupPanel;
    private JTextField emailField, nameField, locationField, bioField, linkedinField, xField, leetcodeField, githubField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton, switchToSignupButton, switchToLoginButton;

    public Login() {
        // Frame setup
        setTitle("Login / Sign Up");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create Login and Signup Panels
        loginPanel = createLoginPanel();
        signupPanel = createSignupPanel();

        // Set initial panel to login panel
        setLayout(new CardLayout());
        add(loginPanel, "Login");
        add(signupPanel, "Sign Up");
    }
    private static boolean authenticateUser(String email, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBConnection.getConnection(); // Replace with your DBConnection class method
            String query = "SELECT password_hash FROM users WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password_hash");
                return storedPasswordHash.equals(password); // Use proper hashing for production
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 10, 0, 10)); // Add padding

        // Title Label
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align within the panel
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Email Field
        emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Set a preferred height

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Set a preferred height

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setOpaque(true); // Make button opaque to show background color
        loginButton.setContentAreaFilled(true); // Ensure content area is filled with background color
        loginButton.setBackground(new Color(30, 144, 255)); // Dodger Blue background
        loginButton.setForeground(Color.WHITE); // White text for visibility
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align the button
        loginButton.setMaximumSize(new Dimension(150, 40));
        loginButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true)); // Rounded border with white color
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticateUser(email, password)) {
                    JOptionPane.showMessageDialog(panel, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                    panel.dispose(); // Close login window
                    new Dashboard().setVisible(true); // Open dashboard window
                } else {
                    JOptionPane.showMessageDialog(panel, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Switch to Signup Button
        switchToSignupButton = new JButton("Don't have an account? Sign Up here");
        switchToSignupButton.setForeground(Color.RED);
        switchToSignupButton.setFont(new Font("Arial", Font.PLAIN, 12));
        switchToSignupButton.setFocusPainted(false);
        switchToSignupButton.setContentAreaFilled(false); // Make it look like a link
        switchToSignupButton.setBorderPainted(false);
        switchToSignupButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align
        switchToSignupButton.addActionListener(e -> showSignupForm());

        // Add Components to Panel
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20)); // Add spacing
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(15)); // Add spacing
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20)); // Add spacing
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(15)); // Add spacing
        panel.add(switchToSignupButton);

        return panel;
    }


    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder("Name"));

        emailField = new JTextField();
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        locationField = new JTextField();
        locationField.setBorder(BorderFactory.createTitledBorder("Location"));

        bioField = new JTextField();
        bioField.setBorder(BorderFactory.createTitledBorder("Bio"));

        linkedinField = new JTextField();
        linkedinField.setBorder(BorderFactory.createTitledBorder("LinkedIn Username"));

        xField = new JTextField();
        xField.setBorder(BorderFactory.createTitledBorder("X Username"));

        leetcodeField = new JTextField();
        leetcodeField.setBorder(BorderFactory.createTitledBorder("LeetCode Username"));

        githubField = new JTextField();
        githubField.setBorder(BorderFactory.createTitledBorder("GitHub Username"));

        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(55, 61, 72));
        signupButton.setForeground(Color.RED);
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.addActionListener(e -> signUpUser());

        switchToLoginButton = new JButton("Already have an account? Login here");
        switchToLoginButton.setForeground(new Color(70, 130, 180));
        switchToLoginButton.addActionListener(e -> showLoginForm());

        panel.add(titleLabel);
        panel.add(nameField);
        panel.add(emailField);
        panel.add(locationField);
        panel.add(bioField);
        panel.add(linkedinField);
        panel.add(xField);
        panel.add(leetcodeField);
        panel.add(githubField);
        panel.add(passwordField);
        panel.add(signupButton);
        panel.add(switchToLoginButton);

        return panel;
    }

    private void showLoginForm() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Login");
    }

    private void showSignupForm() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Sign Up");
    }

    public String loginUser() {
        String email = emailField.getText().trim(); // Trim to avoid extra spaces
        String password = passwordField.getText().trim();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // SQL query to fetch user details
            String query = "SELECT password_hash FROM Users WHERE email = '" + email + "'";
            System.out.println("Executing Query: " + query); // Debugging

            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) { // If a row is found
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    // Update login_check for the user
                    String updateQuery = "UPDATE Users SET login_check = 1 WHERE email = '" + email + "'";
                    System.out.println("Executing Update: " + updateQuery); // Debugging
                    stmt.executeUpdate(updateQuery);

                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    new Dashboard().setVisible(true); // Open dashboard window
                    this.dispose(); // Close the login window
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials, please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials, please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during login: " + ex.getMessage());
        }
        return email; // Return email for further processing if needed
    }


    // Hashing function for SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void signUpUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String location = locationField.getText();
        String bio = bioField.getText();
        String linkedin = linkedinField.getText();
        String xUsername = xField.getText();
        String leetcode = leetcodeField.getText();
        String github = githubField.getText();
        String password = new String(passwordField.getText());

        // Insert into the database
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO Users (name, email, location, bio, linkedin_username, x_username, leetcode_username, github_username, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, location);
                ps.setString(4, bio);
                ps.setString(5, linkedin);
                ps.setString(6, xUsername);
                ps.setString(7, leetcode);
                ps.setString(8, github);
                ps.setString(9, password); // Hash password before storing it ideally

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Sign Up Successful!");
                showLoginForm(); // Switch to login form after successful signup
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during sign up: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login frame = new Login();
            frame.setVisible(true);
        });
    }
}
