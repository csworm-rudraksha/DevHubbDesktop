import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Signup {

    private JPanel signupPanel;
    private JTextField emailField, nameField, locationField, bioField, linkedinField, xField, leetcodeField, githubField;
    private JButton nextButton, switchToLoginButton;

    public Signup() {
        JFrame frame = new JFrame("Sign Up");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        signupPanel = createSignupPanel();
        frame.add(signupPanel);

        frame.setVisible(true);
    }

    private JPanel createSignupPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Title Label
        JLabel titleLabel = new JLabel("Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Form Panel (Two Columns)
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        formPanel.setBackground(Color.WHITE);

        nameField = createStyledTextField("Name");
        emailField = createStyledTextField("Email");
        locationField = createStyledTextField("Location");
        bioField = createStyledTextField("Bio");
        linkedinField = createStyledTextField("LinkedIn Username");
        xField = createStyledTextField("X Username");
        leetcodeField = createStyledTextField("LeetCode Username");
        githubField = createStyledTextField("GitHub Username");

        // Adding fields in two-column layout
        formPanel.add(nameField);
        formPanel.add(emailField);
        formPanel.add(locationField);
        formPanel.add(bioField);
        formPanel.add(linkedinField);
        formPanel.add(xField);
        formPanel.add(leetcodeField);
        formPanel.add(githubField);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);

        nextButton = createStyledButton("Next", new Color(30, 144, 255), Color.WHITE);
        nextButton.setMargin(new Insets(10, 20, 10, 20)); // TOP, LEFT, BOTTOM, RIGHT Padding
        nextButton.addActionListener(e -> openPasswordFrame());

        switchToLoginButton = new JButton("Already have an account? Login here");
        switchToLoginButton.setForeground(Color.RED);
        switchToLoginButton.setFont(new Font("Arial", Font.PLAIN, 12));
        switchToLoginButton.setFocusPainted(false);
        switchToLoginButton.setContentAreaFilled(false);
        switchToLoginButton.setBorderPainted(false);
        switchToLoginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        switchToLoginButton.addActionListener(e -> showLoginForm());

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacing
        buttonPanel.add(nextButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 8))); // Spacing
        buttonPanel.add(switchToLoginButton);

        // Adding to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void openPasswordFrame() {
        JFrame passwordFrame = new JFrame("Set Password");
        passwordFrame.setSize(400, 280);
        passwordFrame.setLocationRelativeTo(null);
        passwordFrame.getContentPane().setBackground(Color.WHITE); // Matching Theme
        passwordFrame.setResizable(false);

        // Main Panel
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        passwordPanel.setBackground(Color.WHITE);

        // Title Label
        JLabel instructionLabel = new JLabel("Set Your Password", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        instructionLabel.setForeground(new Color(30, 144, 255)); // Matching theme color
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password Field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(280, 40));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sign Up Button
        JButton signUpButton = createStyledButton("Sign Up", new Color(30, 144, 255), Color.WHITE);
        signUpButton.setPreferredSize(new Dimension(220, 45)); // Bigger for better UI
        signUpButton.setMaximumSize(new Dimension(220, 45));
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.addActionListener(e -> signUpUser(passwordField.getPassword(), passwordFrame));

        // Adding Components to Panel with Spacing
        passwordPanel.add(instructionLabel);
        passwordPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacing
        passwordPanel.add(passwordField);
        passwordPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing
        passwordPanel.add(signUpButton);

        // Final Setup
        passwordFrame.add(passwordPanel);
        passwordFrame.setVisible(true);
    }


    private void signUpUser(char[] passwordArray, JFrame passwordFrame) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String location = locationField.getText().trim();
        String bio = bioField.getText().trim();
        String linkedin = linkedinField.getText().trim();
        String xUsername = xField.getText().trim();
        String leetcode = leetcodeField.getText().trim();
        String github = githubField.getText().trim();
        String password = new String(passwordArray).trim();

        // 🔍 Validate Required Fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(passwordFrame, "Name, Email, and Password are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // 📝 Insert User Data
            String query = "INSERT INTO users (name, email, location, bio, linkedin_username, x_username, leetcode_username, github_username, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, location);
                ps.setString(4, bio);
                ps.setString(5, linkedin);
                ps.setString(6, xUsername);
                ps.setString(7, leetcode);
                ps.setString(8, github);
                ps.setString(9, password); // Storing plain text password as per requirement

                ps.executeUpdate();
                JOptionPane.showMessageDialog(passwordFrame, "Sign Up Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // 🛑 Dispose both frames before redirecting to login
                passwordFrame.dispose(); // Close password frame
                disposeSignupFrame();   // Close signup frame

                // Redirect to Login Page
                LoginApp.createAndShowGUI();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(passwordFrame, "Error during sign-up: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void disposeSignupFrame() {
        Window signupWindow = SwingUtilities.getWindowAncestor(signupPanel);
        if (signupWindow != null) {
            signupWindow.dispose(); // 🔴 Fully closes the signup JFrame
        }
    }



    private void showDashboard() {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }

    private void showLoginForm() {
        SwingUtilities.invokeLater(() -> new LoginApp().createAndShowGUI());
    }

    private JTextField createStyledTextField(String title) {
        JTextField textField = new JTextField();
        textField.setBorder(BorderFactory.createTitledBorder(title));
        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setMaximumSize(new Dimension(200, 30));
        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Increased font size
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 35)); // Increased size
        button.setMaximumSize(new Dimension(250, 50)); // Uniform size across buttons
        button.setMargin(new Insets(12, 25, 12, 25)); // Padding for better UI
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        return button;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Signup::new);
    }
}
