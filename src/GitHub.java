import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GitHub {

    public GitHub() {
        String username = "csworm-rudraksha"; // Example GitHub username
        JSONObject userProfile = getProfileDetails(username);

        if (userProfile != null) {
            SwingUtilities.invokeLater(() -> createAndShowGUI(userProfile));
        } else {
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch GitHub profile details.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createAndShowGUI(JSONObject userProfile) {
        JFrame frame = new JFrame("GitHub Profile Viewer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("GITHUB PROFILE", JLabel.CENTER);
        headerLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(44, 62, 80)); // Dark Blue-Grey
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(headerLabel, BorderLayout.NORTH);

        // Main Layout Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        // **📌 Profile Section (Left Panel - Centered)**
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        profilePanel.setBackground(new Color(236, 240, 241)); // Light Grey
        profilePanel.setPreferredSize(new Dimension(300, frame.getHeight()));

        String avatarUrl = (String) userProfile.get("avatar_url");
        String userName = (String) userProfile.get("login");
        String name = (String) userProfile.get("name");
        Long repos = (Long) userProfile.get("public_repos");
        Long followers = (Long) userProfile.get("followers");
        Long following = (Long) userProfile.get("following");

        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(120, 120));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (avatarUrl != null) {
            try {
                ImageIcon icon = new ImageIcon(new URL(avatarUrl));
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                avatarLabel.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                avatarLabel.setIcon(getDefaultAvatar());
            }
        } else {
            avatarLabel.setIcon(getDefaultAvatar());
        }

        JLabel userNameLabel = createBoldLabel("USER NAME: " + userName);
        JLabel nameLabel = createBoldLabel("NAME: " + (name != null ? name : "N/A"));
        JLabel reposLabel = createBoldLabel("PUBLIC REPOSITORIES: " + repos);
        JLabel followersLabel = createBoldLabel("FOLLOWERS: " + followers + " | FOLLOWING: " + following);

        profilePanel.add(Box.createVerticalGlue()); // Push to Center
        profilePanel.add(avatarLabel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        profilePanel.add(userNameLabel);
        profilePanel.add(nameLabel);
        profilePanel.add(reposLabel);
        profilePanel.add(followersLabel);
        profilePanel.add(Box.createVerticalGlue()); // Push to Center

        mainPanel.add(profilePanel, BorderLayout.WEST);

        // **📌 Repositories Section (Right Panel - Fixed Width)**
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(580, frame.getHeight())); // Fixed Width

        JLabel repoHeader = new JLabel("REPOSITORIES", JLabel.LEFT);
        repoHeader.setFont(new Font("Arial", Font.BOLD, 18));
        repoHeader.setForeground(Color.WHITE);
        repoHeader.setOpaque(true);
        repoHeader.setBackground(new Color(52, 152, 219)); // Bright Blue
        repoHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        rightPanel.add(repoHeader, BorderLayout.NORTH);

        JPanel reposPanel = new JPanel();
        reposPanel.setLayout(new BoxLayout(reposPanel, BoxLayout.Y_AXIS));
        reposPanel.setBackground(Color.WHITE);
        reposPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String reposUrl = (String) userProfile.get("repos_url");
        JSONArray userRepos = getRepositories(reposUrl);

        if (userRepos != null) {
            for (Object repoObject : userRepos) {
                JSONObject repo = (JSONObject) repoObject;
                reposPanel.add(createRepoCard(repo));
                reposPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } else {
            reposPanel.add(new JLabel("Repositories could not be retrieved."));
        }

        JScrollPane scrollPane = new JScrollPane(reposPanel);
        scrollPane.setBorder(null);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static JPanel createRepoCard(JSONObject repo) {
        String repoName = (String) repo.get("name");
        String repoDescription = (String) repo.get("description");
        String repoHtmlUrl = (String) repo.get("html_url");

        JPanel repoCard = new JPanel();
        repoCard.setLayout(new BorderLayout(10, 10));
        repoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15))); // Padding inside card
        repoCard.setPreferredSize(new Dimension(360, 70)); // Slightly bigger card
        repoCard.setOpaque(true); // Ensure visibility

        // **Left Panel for Title & Description**
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        // **Name Label (Modern Hyperlink Style)**
        JLabel nameLabel = createHyperlinkLabel(repoName, repoHtmlUrl);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(52, 152, 219)); // Bright Blue
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // **Spacing between title and description**
        textPanel.add(nameLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 6))); // 6px gap

        // **Truncate description if it's too long**
        String truncatedDescription = (repoDescription != null && repoDescription.length() > 55)
                ? repoDescription.substring(0, 52) + "..."
                : repoDescription != null ? repoDescription : "No description available";

        JLabel descLabel = new JLabel(truncatedDescription);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(90, 90, 90)); // Softer text color
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(descLabel);

        // **"Open" Button (Right Side)**
        JButton openButton = new JButton("Open") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // **Draw Rounded Button**
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Rounded edges
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                g.setColor(getForeground());
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        openButton.setFont(new Font("Arial", Font.BOLD, 12));
        openButton.setForeground(Color.WHITE);
        openButton.setBackground(new Color(106, 178, 227)); // Bright Blue
        openButton.setOpaque(true); // Ensures Background Color is Applied
        openButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Padding
        openButton.setFocusPainted(false);
        openButton.setContentAreaFilled(false);
        openButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // **Hover Effect for Button**
        openButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                openButton.setBackground(new Color(41, 128, 185)); // Darker Blue on Hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                openButton.setBackground(new Color(52, 152, 219)); // Reset Color
            }
        });

        // **Open Link on Click**
        openButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI(repoHtmlUrl));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // **Adding Components to Card**
        repoCard.add(textPanel, BorderLayout.CENTER);
        repoCard.add(openButton, BorderLayout.EAST);

        return repoCard;
    }






    private static JLabel createWrappedLabel(String text) {
        JLabel label = new JLabel("<html><p style='width:500px;'>" + text + "</p></html>");
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }

    static JLabel createHyperlinkLabel(String text, String url) {
        JLabel label = new JLabel("<html><a href='" + url + "'>" + text + "</a></html>");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return label;
    }

    static JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private static ImageIcon getDefaultAvatar() {
        return new ImageIcon(new ImageIcon("default_avatar.png").getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
    }

    private static JSONObject getProfileDetails(String username) {
        return fetchJSONObject("https://api.github.com/users/" + username);
    }

    private static JSONArray getRepositories(String reposUrl) {
        return fetchJSONArray(reposUrl);
    }

    private static JSONObject fetchJSONObject(String urlString) {
        try {
            String jsonResponse = fetchJSON(urlString);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONArray fetchJSONArray(String urlString) {
        try {
            String jsonResponse = fetchJSON(urlString);
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String fetchJSON(String urlString) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            return scanner.useDelimiter("\\A").next();
        }
    }



    public static void main(String[] args) {
        new GitHub();
    }
}
