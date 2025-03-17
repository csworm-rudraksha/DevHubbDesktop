import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.*;
import java.sql.*;

public class Dashboard extends JFrame {
    private JPanel friendsPanel , leftPanel,rightPanel,detailsPanel;  // Panel to display friends' list
    private JTextField searchBar; // Search bar for searching friends

    public Dashboard() {
        // Frame setup
        setTitle("Dashboard");
        setSize(709, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set layout for main frame
        setResizable(false);
        setLayout(new BorderLayout());

        // Left panel
        leftPanel = new JPanel();
        leftPanel.setBackground(new Color(220, 220, 220));
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBorder(new EmptyBorder(10, 17, 10, 10));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

// Add glue for vertical centering
        leftPanel.add(Box.createVerticalGlue());

        displayUserDetails(leftPanel);


        // Right panel
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(Color.WHITE); // Light gray color for right panel
        rightPanel.setPreferredSize(new Dimension(400, 0)); // Set fixed width
        rightPanel.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Color.LIGHT_GRAY)); // Left border onlys

        // Top panel with search bar and add button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 6, 4));


        // Search bar
        JLabel FriendsLabel = new JLabel("Friendfolio", JLabel.CENTER); // Center the label text
        FriendsLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Increase font size
        FriendsLabel.setBackground(Color.WHITE);
        FriendsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(0, 40));
        searchBar.setFont(new Font("Arial", Font.PLAIN, 16));
        searchBar.setBorder(BorderFactory.createTitledBorder("Search"));
        searchBar.setBackground(Color.WHITE);
        searchBar.addCaretListener(e -> searchFriends()); // Add listener to search bar

// Add friend button
        JButton addButton = new JButton("+");
        addButton.setFont(new Font("Arial", Font.BOLD, 20));
        addButton.setBackground(Color.lightGray);
        addButton.setPreferredSize(new Dimension(60, 40)); // Set preferred size for consistent height

// Add action listener to show the popup on button click
        addButton.addActionListener(e -> showAddFriendPopup());

// Add components to top panel
        topPanel.setLayout(new BorderLayout());
        topPanel.add(FriendsLabel, BorderLayout.NORTH);
        topPanel.add(searchBar, BorderLayout.CENTER);
        topPanel.add(addButton, BorderLayout.EAST);

        // Add top panel to right panel
        rightPanel.add(topPanel, BorderLayout.NORTH);

        // Panel to display friends' list
        friendsPanel = new JPanel();
        friendsPanel.setLayout(new BoxLayout(friendsPanel, BoxLayout.Y_AXIS)); // Vertical layout for friends
        friendsPanel.setBackground(Color.WHITE);
        friendsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(friendsPanel);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Fetch and display the list of friends
        displayFriends();

        // Add panels to the frame
        add(leftPanel);
//        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    private void displayUserDetails(JPanel leftPanel) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT name, email, location, linkedin_username, x_username, leetcode_username, github_username, profile_image FROM users WHERE login_check=1 LIMIT 1";

            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String location = rs.getString("location");
                    String linkedin = rs.getString("linkedin_username");
                    String xUsername = rs.getString("x_username");
                    String leetcode = rs.getString("leetcode_username");
                    String github = rs.getString("github_username");
                    String profileImageUrl = rs.getString("profile_image");

                    // **📌 Main Container (VERTICAL LAYOUT, FULL LEFT)**
                    JPanel mainContainer = new JPanel();
                    mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
                    mainContainer.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 5)); // Align fully left
                    mainContainer.setOpaque(false);
                    mainContainer.setPreferredSize(new Dimension(280, 430));

                    // **🖼 Profile Image Panel (CENTERED)**
                    JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2)); // Center the profile image
                    imagePanel.setOpaque(false);
                    imagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Add bottom spacing

                    JLabel profileImageLabel = new JLabel();
                    profileImageLabel.setPreferredSize(new Dimension(160, 160));

                    // **🖼 Load Image from URL**
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        try {
                            URL imageUrl = new URL(profileImageUrl);
                            ImageIcon originalIcon = new ImageIcon(imageUrl);
                            Image img = originalIcon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                            profileImageLabel.setIcon(new ImageIcon(img));
                        } catch (Exception e) {
                            profileImageLabel.setIcon(getDefaultProfileIcon());
                        }
                    } else {
                        profileImageLabel.setIcon(getDefaultProfileIcon());
                    }

                    imagePanel.add(profileImageLabel);
                    mainContainer.add(imagePanel);

                    // **🔘 Edit Profile Button Panel (LEFT ALIGNED)**
                    JPanel editButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                    editButtonPanel.setOpaque(false);
                    editButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                    JButton editButton = getStyledButton("Edit Profile");
                    editButton.addActionListener(e -> showEditUserPopup(name, email, location, linkedin, xUsername, leetcode, github));

                    editButtonPanel.add(editButton);
                    mainContainer.add(editButtonPanel);

                    // **📌 User Details Panel (EXTREME LEFT ALIGNMENT, SEPARATE LINES)**
                    JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 0, 1)); // 1 Column, Multiple Rows
                    detailsPanel.setOpaque(false);
                    detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                    detailsPanel.add(new JLabel("<html><b>Name:</b> " + name + "</html>"));
                    detailsPanel.add(new JLabel("<html><b>Email:</b> " + email + "</html>"));
                    detailsPanel.add(new JLabel("<html><b>Location:</b> " + location + "</html>"));
                    detailsPanel.add(new JLabel("<html><b>LinkedIn:</b> " + linkedin + "</html>"));
                    detailsPanel.add(new JLabel("<html><b>Twitter:</b> " + xUsername + "</html>"));
                    detailsPanel.add(new JLabel("<html><b>LeetCode:</b> " + leetcode + "</html>"));
                    detailsPanel.add(new JLabel("<html><b>GitHub:</b> " + github + "</html>"));

                    mainContainer.add(detailsPanel);

                    // **🔗 GitHub & LeetCode Buttons Panel (LEFT ALIGNED)**
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
                    buttonPanel.setOpaque(false);
                    buttonPanel.setBorder(BorderFactory.createEmptyBorder(17, 0, 0, 0));

                    JButton githubButton = getStyledButton("GitHub Profile");
                    githubButton.addActionListener(e -> SwingUtilities.invokeLater(() -> new GitHub()));

                    JButton leetcodeButton = getStyledButton("LeetCode Profile");

                    buttonPanel.add(githubButton);
                    buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                    buttonPanel.add(leetcodeButton);

                    mainContainer.add(buttonPanel);

                    // **📌 Add Everything to Left Panel**
                    leftPanel.removeAll();
                    leftPanel.add(mainContainer);
                    leftPanel.revalidate();
                    leftPanel.repaint();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching user data: " + e.getMessage());
        }
    }





    private JButton getStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 30)); // Ensure it fits within the frame
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(0, 102, 204)); // Set blue background
        button.setForeground(Color.WHITE); // White text for contrast
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        button.setOpaque(true); // Ensure background color is applied

        // Hover Effect (Darker blue on hover)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 82, 164)); // Darker blue
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204)); // Original blue
            }
        });

        return button;
    }


    private ImageIcon getDefaultProfileIcon() {
        try {
            URL defaultImageUrl = new URL("https://www.w3schools.com/w3images/avatar2.png"); // Example placeholder
            ImageIcon originalIcon = new ImageIcon(defaultImageUrl);
            Image img = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return new ImageIcon();
        }
    }


    //
    private void showEditUserPopup(String name, String email, String location,
                                   String linkedin, String xUsername, String leetcode, String github) {
        JPanel editFriendPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField nameField = new JTextField(name);
        JTextField locationField = new JTextField(location);
        JTextField linkedinField = new JTextField(linkedin);
        JTextField twitterField = new JTextField(xUsername);
        JTextField leetcodeField = new JTextField(leetcode);
        JTextField githubField = new JTextField(github);

        editFriendPanel.add(new JLabel("Name:"));
        editFriendPanel.add(nameField);
        editFriendPanel.add(new JLabel("Location:"));
        editFriendPanel.add(locationField);
        editFriendPanel.add(new JLabel("LinkedIn:"));
        editFriendPanel.add(linkedinField);
        editFriendPanel.add(new JLabel("Twitter:"));
        editFriendPanel.add(twitterField);
        editFriendPanel.add(new JLabel("LeetCode:"));
        editFriendPanel.add(leetcodeField);
        editFriendPanel.add(new JLabel("GitHub:"));
        editFriendPanel.add(githubField);

        int option = JOptionPane.showConfirmDialog(this, editFriendPanel, "Edit User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String editedName = nameField.getText();
            String editedLocation = locationField.getText();
            String editedLinkedin = linkedinField.getText();
            String editedTwitter = twitterField.getText();
            String editedLeetcode = leetcodeField.getText();
            String editedGithub = githubField.getText();

            // Call updateUser without email change
            updateUser(editedName, editedLocation, editedLinkedin, editedTwitter, editedLeetcode, editedGithub, email);
        }
    }

    //////
    private void updateUser(String name, String location,
                            String linkedin, String x, String leetcode, String github, String email) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE users SET name = ?, location = ?, linkedin_username = ?, x_username = ?, leetcode_username = ?, github_username = ? WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, location);
                ps.setString(3, linkedin);
                ps.setString(4, x);
                ps.setString(5, leetcode);
                ps.setString(6, github);
                ps.setString(7, email); // Use existing email to identify the user

                ps.executeUpdate();
                leftPanel.removeAll();
                displayUserDetails(leftPanel);
                JOptionPane.showMessageDialog(this, "User's details updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating user's details: " + e.getMessage());
        }
    }


    // Method to fetch and display the list of friends
    private void displayFriends() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, name, email FROM friends";
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                // Clear the current list of friends
                friendsPanel.removeAll();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String email = rs.getString("email");

                    // Create a panel for each friend (like a card)
                    JPanel friendCard = new JPanel();
                    friendCard.setLayout(new BorderLayout());
                    friendCard.setBackground(Color.WHITE);
                    friendCard.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                    friendCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // Fixed height

                    // Add friend's name and email to the card
                    JLabel friendLabel = new JLabel("<html><b>" + name + "</b><br><span style='color:gray'>" + email + "</span></html>");
                    friendLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                    // Add hover effect to highlight the card
                    friendCard.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                            friendCard.setBackground(new Color(240, 240, 240)); // Light gray on hover
                        }
                        public void mouseExited(java.awt.event.MouseEvent evt) {
                            friendCard.setBackground(Color.WHITE); // Reset to white
                        }
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            showFriendDetails(id); // Show details popup on click
                        }
                    });

                    // Add an optional button for actions (e.g., delete)
                    JButton deleteButton = new JButton("Delete");
                    deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));
                    deleteButton.addActionListener(e -> deleteFriend(id)); // Delete friend logic

                    deleteButton.setBackground(new Color(220, 53, 69));
                    deleteButton.setForeground(Color.RED);

                    // Arrange components in the card
                    friendCard.add(friendLabel, BorderLayout.CENTER);
                    friendCard.add(deleteButton, BorderLayout.EAST);

                    // Add the card to the main friends panel
                    friendsPanel.add(friendCard);
                    friendsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between cards
                }

                // Refresh the panel to display the updated list
                friendsPanel.revalidate();
                friendsPanel.repaint();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching friends: " + e.getMessage());
        }
    }


    // Method to search friends based on the name in the search bar
    private void searchFriends() {
        String searchText = searchBar.getText().trim().toLowerCase();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, name, email FROM friends WHERE LOWER(name) LIKE ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, "%" + searchText + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    // Clear the current list of friends
                    friendsPanel.removeAll();

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");

                        // Create a panel for each friend (like a card)
                        JPanel friendCard = new JPanel();
                        friendCard.setLayout(new BorderLayout());
                        friendCard.setBackground(Color.WHITE);
                        friendCard.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                                BorderFactory.createEmptyBorder(10, 10, 10, 10)
                        ));
                        friendCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // Fixed height

                        // Add friend's name and email to the card
                        JLabel friendLabel = new JLabel("<html><b>" + name + "</b><br><span style='color:gray'>" + email + "</span></html>");
                        friendLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                        // Add hover effect to highlight the card
                        friendCard.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseEntered(java.awt.event.MouseEvent evt) {
                                friendCard.setBackground(new Color(240, 240, 240)); // Light gray on hover
                            }

                            public void mouseExited(java.awt.event.MouseEvent evt) {
                                friendCard.setBackground(Color.WHITE); // Reset to white
                            }

                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                showFriendDetails(id); // Show details popup on click
                            }
                        });

                        // Add an optional delete button
                        JButton deleteButton = new JButton("Delete");
                        deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));
                        deleteButton.setBackground(new Color(220, 53, 69));
                        deleteButton.setForeground(Color.RED);
                        deleteButton.addActionListener(e -> deleteFriend(id)); // Delete friend logic

                        // Arrange components in the card
                        friendCard.add(friendLabel, BorderLayout.CENTER);
                        friendCard.add(deleteButton, BorderLayout.EAST);

                        // Add the card to the main friends panel
                        friendsPanel.add(friendCard);
                        friendsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between cards
                    }

                    // Refresh the panel to display the updated list
                    friendsPanel.revalidate();
                    friendsPanel.repaint();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching friends: " + e.getMessage());
        }
    }


    // Method to show a popup with all details of a selected friend
    private void showFriendDetails(int friendId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM friends WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, friendId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String contact = rs.getString("contact");
                    String description = rs.getString("description");
                    String linkedinLink = rs.getString("linkedin_link");
                    String twitterLink = rs.getString("twitter_link");
                    String leetcodeLink = rs.getString("leetcode_link");
                    String githubLink = rs.getString("github_link");

                    // Create a panel to show all the details with a light gray background
                    detailsPanel = new JPanel(new GridLayout(9, 2, 10, 10));
                    detailsPanel.setBackground(new Color(245, 245, 245)); // Light gray background

                    detailsPanel.add(createStyledLabel("Name:"));
                    detailsPanel.add(createStyledLabel(name));
                    detailsPanel.add(createStyledLabel("Email:"));
                    detailsPanel.add(createStyledLabel(email));
                    detailsPanel.add(createStyledLabel("Contact:"));
                    detailsPanel.add(createStyledLabel(contact));
                    detailsPanel.add(createStyledLabel("Description:"));
                    detailsPanel.add(createStyledLabel(description));

                    // Create clickable links using JEditorPane
                    detailsPanel.add(createStyledLabel("LinkedIn:"));
                    detailsPanel.add(createHyperlinkLabel(linkedinLink));

                    detailsPanel.add(createStyledLabel("Twitter:"));
                    detailsPanel.add(createHyperlinkLabel(twitterLink));

                    detailsPanel.add(createStyledLabel("LeetCode:"));
                    detailsPanel.add(createHyperlinkLabel(leetcodeLink));

                    detailsPanel.add(createStyledLabel("GitHub:"));
                    detailsPanel.add(createHyperlinkLabel(githubLink));

                    // Create buttons for delete and edit with custom styling
                    JButton deleteButton = createStyledButton("Delete");
                    JButton editButton = createStyledButton("Edit");

                    // Set actions for the buttons
                    deleteButton.addActionListener(e -> deleteFriend(friendId));
                    editButton.addActionListener(e -> showEditFriendPopup(friendId, name, email, contact, description, linkedinLink, twitterLink, leetcodeLink, githubLink));

                    JPanel buttonPanel = new JPanel();
                    buttonPanel.setBackground(new Color(245, 245, 245)); // Match background with the details panel
                    buttonPanel.add(deleteButton);
                    buttonPanel.add(editButton);

                    // Create the final panel for the dialog with a light gray background
                    JPanel finalPanel = new JPanel(new BorderLayout(20, 20));
                    finalPanel.setBackground(new Color(245, 245, 245)); // Light gray background
                    finalPanel.add(detailsPanel, BorderLayout.CENTER);
                    finalPanel.add(buttonPanel, BorderLayout.SOUTH);

                    // Create a customized dialog with a fixed size (width: 500, height: 400)
                    JOptionPane.showMessageDialog(this, finalPanel, "Friend Details", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching friend details: " + e.getMessage());
        }
    }


    // Create a styled label with a larger font size and custom color
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14)); // Larger font size
        label.setForeground(new Color(60, 60, 60)); // Darker gray color for better readability
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Bold text
        button.setBackground(new Color(0, 123, 255)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setPreferredSize(new Dimension(120, 40)); // Button size
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
        button.setFocusPainted(false); // Remove focus painting
        button.setContentAreaFilled(true); // Ensure button fills with color

        // Optional: Add hover effects or transitions for more interactivity
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2)); // Add a border for styling
        return button;
    }




    // Helper method to create a clickable hyperlink label
    private JLabel createHyperlinkLabel(String link) {
        JLabel linkLabel = new JLabel("<html><a href='#'>" + link + "</a></html>");
        linkLabel.setForeground(Color.BLUE);
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(link)); // Open link in browser
                } catch (IOException | URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return linkLabel;
    }


    // Method to delete a friend from the database
    private void deleteFriend(int friendId) {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this friend?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM friends WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setInt(1, friendId);
                    ps.executeUpdate();

                    // Show confirmation dialog
                    JOptionPane.showMessageDialog(this, "Friend deleted successfully.");

                    // Close the details panel dialog (specific to this window)
                    // Get the window that is showing the friend details (which is a JOptionPane window in this case)
                    SwingUtilities.getWindowAncestor(detailsPanel).setVisible(false); // Close the details dialog

                    displayFriends(); // Refresh the list to reflect the change
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting friend: " + e.getMessage());
            }
        }
    }


    // Method to show the popup to add a new friend
    private void showAddFriendPopup() {
        JPanel addFriendPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField contactField = new JTextField();
        JTextArea descriptionArea = new JTextArea();
        JTextField linkedinField = new JTextField();
        JTextField twitterField = new JTextField();
        JTextField leetcodeField = new JTextField();
        JTextField githubField = new JTextField();

        addFriendPanel.add(new JLabel("Name:"));
        addFriendPanel.add(nameField);
        addFriendPanel.add(new JLabel("Email:"));
        addFriendPanel.add(emailField);
        addFriendPanel.add(new JLabel("Contact:"));
        addFriendPanel.add(contactField);
        addFriendPanel.add(new JLabel("Description:"));
        addFriendPanel.add(new JScrollPane(descriptionArea));
        addFriendPanel.add(new JLabel("LinkedIn:"));
        addFriendPanel.add(linkedinField);
        addFriendPanel.add(new JLabel("Twitter:"));
        addFriendPanel.add(twitterField);
        addFriendPanel.add(new JLabel("LeetCode:"));
        addFriendPanel.add(leetcodeField);
        addFriendPanel.add(new JLabel("GitHub:"));
        addFriendPanel.add(githubField);

        int option = JOptionPane.showConfirmDialog(this, addFriendPanel, "Add Friend", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String email = emailField.getText();
            String contact = contactField.getText();
            String description = descriptionArea.getText();
            String linkedinLink = linkedinField.getText();
            String twitterLink = twitterField.getText();
            String leetcodeLink = leetcodeField.getText();
            String githubLink = githubField.getText();

            addNewFriend(name, email, contact, description, linkedinLink, twitterLink, leetcodeLink, githubLink);
        }
    }
    // Method to show the popup for editing a friend's details
    private void showEditFriendPopup(int friendId, String name, String email, String contact, String description,
                                     String linkedinLink, String twitterLink, String leetcodeLink, String githubLink) {
        JPanel editFriendPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Create input fields with the current values
        JTextField nameField = new JTextField(name);
        JTextField emailField = new JTextField(email);
        JTextField contactField = new JTextField(contact);
        JTextArea descriptionArea = new JTextArea(description);
        JTextField linkedinField = new JTextField(linkedinLink);
        JTextField twitterField = new JTextField(twitterLink);
        JTextField leetcodeField = new JTextField(leetcodeLink);
        JTextField githubField = new JTextField(githubLink);

        editFriendPanel.add(new JLabel("Name:"));
        editFriendPanel.add(nameField);
        editFriendPanel.add(new JLabel("Email:"));
        editFriendPanel.add(emailField);
        editFriendPanel.add(new JLabel("Contact:"));
        editFriendPanel.add(contactField);
        editFriendPanel.add(new JLabel("Description:"));
        editFriendPanel.add(new JScrollPane(descriptionArea));
        editFriendPanel.add(new JLabel("LinkedIn:"));
        editFriendPanel.add(linkedinField);
        editFriendPanel.add(new JLabel("Twitter:"));
        editFriendPanel.add(twitterField);
        editFriendPanel.add(new JLabel("LeetCode:"));
        editFriendPanel.add(leetcodeField);
        editFriendPanel.add(new JLabel("GitHub:"));
        editFriendPanel.add(githubField);

        int option = JOptionPane.showConfirmDialog(this, editFriendPanel, "Edit Friend", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            // Retrieve the edited values
            String editedName = nameField.getText();
            String editedEmail = emailField.getText();
            String editedContact = contactField.getText();
            String editedDescription = descriptionArea.getText();
            String editedLinkedinLink = linkedinField.getText();
            String editedTwitterLink = twitterField.getText();
            String editedLeetcodeLink = leetcodeField.getText();
            String editedGithubLink = githubField.getText();

            // Call method to update the friend in the database
            updateFriend(friendId, editedName, editedEmail, editedContact, editedDescription, editedLinkedinLink,
                    editedTwitterLink, editedLeetcodeLink, editedGithubLink);
        }
    }

    // Method to update the friend's details in the database
    private void updateFriend(int friendId, String name, String email, String contact, String description,
                              String linkedinLink, String twitterLink, String leetcodeLink, String githubLink) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE friends SET name = ?, email = ?, contact = ?, description = ?, linkedin_link = ?, twitter_link = ?, leetcode_link = ?, github_link = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, contact);
                ps.setString(4, description);
                ps.setString(5, linkedinLink);
                ps.setString(6, twitterLink);
                ps.setString(7, leetcodeLink);
                ps.setString(8, githubLink);
                ps.setInt(9, friendId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Friend's details updated successfully.");
                displayFriends(); // Refresh the list to reflect changes
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating friend's details: " + e.getMessage());
        }
    }


    // Method to add a new friend to the database
    private void addNewFriend(String name, String email, String contact, String description, String linkedinLink, String twitterLink, String leetcodeLink, String githubLink) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO friends (name, email, contact, description, linkedin_link, twitter_link, leetcode_link, github_link) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, contact);
                ps.setString(4, description);
                ps.setString(5, linkedinLink);
                ps.setString(6, twitterLink);
                ps.setString(7, leetcodeLink);
                ps.setString(8, githubLink);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "New friend added successfully.");
                displayFriends(); // Refresh the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding new friend: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
    }
}
