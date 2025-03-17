import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginApp::createAndShowGUI);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.getContentPane().setBackground(Color.WHITE); // Set background color

        // Title Label
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        frame.add(titleLabel);

        // Email Field
        JTextField emailField = new JTextField();
        styleTextField(emailField, "Email");
        frame.add(emailField);

        // Password Field
        JPasswordField passwordField = new JPasswordField();
        styleTextField(passwordField, "Password");
        frame.add(passwordField);

        // Login Button
        JButton loginButton = createStyledButton("Login", new Color(30, 144, 255), Color.WHITE);
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(email, password)) {
                JOptionPane.showMessageDialog(frame, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose(); // Close login window
                new Dashboard().setVisible(true); // Open dashboard window
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.add(Box.createRigidArea(new Dimension(0, 15))); // Spacing
        frame.add(loginButton);

        // Signup Button
        JButton switchToSignupButton = new JButton("Don't have an account? Sign Up here");
        switchToSignupButton.setForeground(Color.RED);
        switchToSignupButton.setFont(new Font("Arial", Font.PLAIN, 12));
        switchToSignupButton.setFocusPainted(false);
        switchToSignupButton.setContentAreaFilled(false); // Make it look like a link
        switchToSignupButton.setBorderPainted(false);
        switchToSignupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        switchToSignupButton.addActionListener(e -> {
            frame.dispose(); // Close the login window
            SwingUtilities.invokeLater(Signup::new); // Open the signup window
        });
        frame.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        frame.add(switchToSignupButton);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static boolean authenticateUser(String email, String password) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT password_hash FROM users WHERE email = ?")) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPasswordHash = resultSet.getString("password_hash");
                    if (storedPasswordHash.equals(password)) { // Use proper hashing for production
                        try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE users SET login_check = CASE WHEN email = ? THEN 1 ELSE 0 END")) {
                            updateStatement.setString(1, email);
                            updateStatement.executeUpdate();
                        }
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void styleTextField(JTextField field, String title) {
        field.setBorder(BorderFactory.createTitledBorder(title));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    }

    private static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(150, 40));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        return button;
    }
}
