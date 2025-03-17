import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LeetCode {

    private static final String USERNAME = "rudraksha123"; // Predefined Username
    private static final String API_FAISAL = "https://leetcode-api-faisalshohag.vercel.app/";
    private static final String API_ALFA = "https://alfa-leetcode-api.onrender.com/";

    public LeetCode() {
        JSONObject userProfile = fetchLeetCodeData(API_FAISAL + USERNAME);
        JSONObject additionalProfile = fetchLeetCodeData(API_ALFA + USERNAME);

        if (userProfile != null && additionalProfile != null) {
            mergeProfileData(userProfile, additionalProfile);
            SwingUtilities.invokeLater(() -> createAndShowGUI(userProfile));
        } else {
            JOptionPane.showMessageDialog(null,
                    "Failed to fetch LeetCode profile details.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void mergeProfileData(JSONObject userProfile, JSONObject additionalProfile) {
        userProfile.put("name", additionalProfile.getOrDefault("name", "Unknown"));
        userProfile.put("avatar", additionalProfile.getOrDefault("avatar",
                "https://assets.leetcode.com/users/default_avatar.png"));
        userProfile.put("country", additionalProfile.getOrDefault("country", "Unknown"));
        userProfile.put("school", additionalProfile.getOrDefault("school", "N/A"));
        userProfile.put("reputation", additionalProfile.getOrDefault("reputation", "N/A"));
        userProfile.put("contestRating", additionalProfile.getOrDefault("contestRating", "N/A"));
        userProfile.put("gitHub", additionalProfile.getOrDefault("gitHub", null));
        userProfile.put("twitter", additionalProfile.getOrDefault("twitter", null));
        userProfile.put("linkedIN", additionalProfile.getOrDefault("linkedIN", null));
        userProfile.put("website", additionalProfile.getOrDefault("website", null));
    }

    private static void createAndShowGUI(JSONObject userProfile) {
        JFrame frame = new JFrame("LeetCode Profile Viewer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(950, 600);
        frame.setLayout(new BorderLayout());

        // **Header**
        JLabel headerLabel = new JLabel("LEETCODE PROFILE", JLabel.CENTER);
        headerLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(44, 62, 80));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(headerLabel, BorderLayout.NORTH);

        // **Main Panel Layout**
        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel, BorderLayout.WEST);

        // **Left Panel (Profile Stats)**
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        profilePanel.setBackground(new Color(236, 240, 241));
        profilePanel.setPreferredSize(new Dimension(300, frame.getHeight()));

        // **Profile Image**
        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(120, 120));

        try {
            URL imageUrl = new URL((String) userProfile.get("avatar"));
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image img = originalIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            avatarLabel.setText("No Image");
        }

        profilePanel.add(avatarLabel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // **Extract User Data**
        profilePanel.add(GitHub.createBoldLabel("Username: " + USERNAME));
        profilePanel.add(GitHub.createBoldLabel("Name: " + userProfile.get("name")));
        profilePanel.add(GitHub.createBoldLabel("Country: " + userProfile.get("country")));
        profilePanel.add(GitHub.createBoldLabel("School: " + userProfile.get("school")));
        profilePanel.add(GitHub.createBoldLabel("Reputation: " + userProfile.get("reputation")));
        profilePanel.add(GitHub.createBoldLabel("Contest Rating: " + userProfile.get("contestRating")));

        profilePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // **Total Questions**
        profilePanel.add(GitHub.createBoldLabel("Total Questions: " + userProfile.getOrDefault("totalQuestions", "N/A")));
        profilePanel.add(GitHub.createBoldLabel("Easy: " + userProfile.getOrDefault("totalEasy", "N/A")));
        profilePanel.add(GitHub.createBoldLabel("Medium: " + userProfile.getOrDefault("totalMedium", "N/A")));
        profilePanel.add(GitHub.createBoldLabel("Hard: " + userProfile.getOrDefault("totalHard", "N/A")));

        profilePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(profilePanel, BorderLayout.WEST);

        // **Right Panel (Recent Submissions)**
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(580, frame.getHeight()));

        JLabel submissionsHeader = new JLabel("RECENT SUBMISSIONS", JLabel.LEFT);
        submissionsHeader.setFont(new Font("Arial", Font.BOLD, 18));
        submissionsHeader.setForeground(Color.WHITE);
        submissionsHeader.setOpaque(true);
        submissionsHeader.setBackground(new Color(52, 152, 219));
        submissionsHeader.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        rightPanel.add(submissionsHeader, BorderLayout.NORTH);

        JPanel submissionsPanel = new JPanel();
        submissionsPanel.setLayout(new BoxLayout(submissionsPanel, BoxLayout.Y_AXIS));
        submissionsPanel.setBackground(Color.WHITE);
        submissionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSONArray recentSubmissions = (JSONArray) userProfile.get("recentSubmissions");

        if (recentSubmissions != null) {
            for (Object subObject : recentSubmissions) {
                JSONObject submission = (JSONObject) subObject;
                submissionsPanel.add(createSubmissionCard(submission));
                submissionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } else {
            submissionsPanel.add(new JLabel("No recent submissions found."));
        }

        JScrollPane scrollPane = new JScrollPane(submissionsPanel);
        scrollPane.setBorder(null);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static JPanel createSubmissionCard(JSONObject submission) {
        String title = (String) submission.get("title");
        String titleSlug = (String) submission.get("titleSlug");
        String status = (String) submission.get("statusDisplay");
        String lang = (String) submission.get("lang");

        JPanel subCard = new JPanel(new BorderLayout());
        subCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        subCard.setBackground(Color.WHITE);
        subCard.setPreferredSize(new Dimension(540, 70));

        JLabel titleLabel = GitHub.createHyperlinkLabel(title, "https://leetcode.com/problems/" + titleSlug);
        JLabel statusLabel = new JLabel("Status: " + status + " | Language: " + lang);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(statusLabel);

        subCard.add(textPanel, BorderLayout.CENTER);

        return subCard;
    }
    private static JSONObject fetchLeetCodeData(String urlString) {
        try {
            String jsonResponse = fetchJSON(urlString);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String fetchJSON(String urlString) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            return scanner.useDelimiter("\\A").next();
        }
    }



    public static void main(String[] args) {
        new LeetCode();
    }
}
