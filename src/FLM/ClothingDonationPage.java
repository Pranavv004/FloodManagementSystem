package FLM;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClothingDonationPage extends JFrame {
    private JComboBox<String> districtComboBox;
    private Map<String, String> districtDetails;
    private JTextField donorNameField;
    private JPanel genderPanel;
    private ButtonGroup genderGroup;
    private JRadioButton maleButton;
    private JRadioButton femaleButton;
    private JComboBox<String> sizeComboBox;

    public ClothingDonationPage() {
        setTitle("Clothing Donation for Kerala Flood Relief");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(173, 216, 230)); // Light blue color

        initializeDistrictDetails();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        donorNameField = new JTextField(20);
        add(donorNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Type of Clothing:"), gbc);

        ButtonGroup typeGroup = new ButtonGroup();
        JRadioButton adultButton = new JRadioButton("Adult");
        JRadioButton childrenButton = new JRadioButton("Children (under 12)");
        JRadioButton elderlyButton = new JRadioButton("Elderly (above 60)");

        typeGroup.add(adultButton);
        typeGroup.add(childrenButton);
        typeGroup.add(elderlyButton);

        JPanel typePanel = new JPanel(new GridLayout(3, 1));
        typePanel.add(adultButton);
        typePanel.add(childrenButton);
        typePanel.add(elderlyButton);

        gbc.gridx = 1;
        add(typePanel, gbc);

        // Add gender selection panel
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Gender:"), gbc);

        genderGroup = new ButtonGroup();
        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");

        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        genderPanel = new JPanel(new GridLayout(2, 1));
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);

        gbc.gridx = 1;
        add(genderPanel, gbc);

        // Add combo box for district selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Select District:"), gbc);
        gbc.gridx = 1;
        districtComboBox = new JComboBox<>(districtDetails.keySet().toArray(new String[0]));
        add(districtComboBox, gbc);

        // Add size selection panel
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Clothing Size:"), gbc);
        gbc.gridx = 1;
        sizeComboBox = new JComboBox<>(new String[]{"S", "M", "L", "XL", "XXL", "XXXL"});
        add(sizeComboBox, gbc);

        JButton submitDonationButton = new JButton("Submit");
        styleButton(submitDonationButton);
        submitDonationButton.addActionListener(e -> submitDonation());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitDonationButton, gbc);
    }

    private void initializeDistrictDetails() {
        districtDetails = new HashMap<>();
        districtDetails.put("Thiruvananthapuram", "Location: Central Stadium\nAddress: Near Secretariat, Palayam, Thiruvananthapuram, Kerala 695001\nContact Number: +91 471 2332144\nCapacity: 1500");
        districtDetails.put("Kollam", "Location: Government Boys High School\nAddress: Chinnakada, Kollam, Kerala 691001\nContact Number: +91 474 2745623\nCapacity: 1200");
        districtDetails.put("Pathanamthitta", "Location: Mount Tabor School\nAddress: Pathanamthitta, Kerala 689645\nContact Number: +91 468 2225285\nCapacity: 1000");
        districtDetails.put("Alappuzha", "Location: SDV Boys High School\nAddress: West of Beach Road, Alappuzha, Kerala 688012\nContact Number: +91 477 2251567\nCapacity: 800");
        districtDetails.put("Kottayam", "Location: Baker Memorial School\nAddress: Kottayam, Kerala 686001\nContact Number: +91 481 2565321\nCapacity: 1100");
        districtDetails.put("Idukki", "Location: Govt. High School, Munnar\nAddress: Munnar, Idukki, Kerala 685612\nContact Number: +91 4865 230567\nCapacity: 900");
        districtDetails.put("Ernakulam", "Location: Rajagiri Public School\nAddress: Kalamassery, Ernakulam, Kerala 683104\nContact Number: +91 484 2555564\nCapacity: 1300");
        districtDetails.put("Thrissur", "Location: St. Thomas College\nAddress: Thrissur, Kerala 680001\nContact Number: +91 487 2331464\nCapacity: 1400");
        districtDetails.put("Palakkad", "Location: Govt. Victoria College\nAddress: Palakkad, Kerala 678001\nContact Number: +91 491 2521101\nCapacity: 1000");
        districtDetails.put("Malappuram", "Location: MES College\nAddress: Malappuram, Kerala 676509\nContact Number: +91 483 2734767\nCapacity: 1200");
        districtDetails.put("Kozhikode", "Location: Govt. Model Higher Secondary School\nAddress: Kozhikode, Kerala 673001\nContact Number: +91 495 2721245\nCapacity: 1500");
        districtDetails.put("Wayanad", "Location: St. Mary's College\nAddress: Sulthan Bathery, Wayanad, Kerala 673592\nContact Number: +91 4936 220377\nCapacity: 800");
        districtDetails.put("Kannur", "Location: Kannur University\nAddress: Thavakkara, Kannur, Kerala 670002\nContact Number: +91 497 2715335\nCapacity: 1200");
        districtDetails.put("Kasaragod", "Location: Govt. College\nAddress: Kasaragod, Kerala 671123\nContact Number: +91 4994 230567\nCapacity: 900");
    }

    private void submitDonation() {
	String name = donorNameField.getText();
	String userRegex ="^([a-zA-Z\\s]{3,20})$";
	Pattern pattern = Pattern.compile(userRegex);
	Matcher matcher1 = pattern.matcher(name);
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else if(!matcher1.matches()) {
        	JOptionPane.showMessageDialog(this, "Invalid Name", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Ensure a clothing type is selected
        if (!isClothingTypeSelected()) {
            JOptionPane.showMessageDialog(this, "Please select a type of clothing.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ensure a gender is selected
        if (!isGenderSelected()) {
            JOptionPane.showMessageDialog(this, "Please select a gender.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedDistrict = (String) districtComboBox.getSelectedItem();
        String details = districtDetails.get(selectedDistrict);
        JOptionPane.showMessageDialog(this, details, "Relief Camp Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isClothingTypeSelected() {
        return genderPanel.isVisible();
    }

    private boolean isGenderSelected() {
        return maleButton.isSelected() || femaleButton.isSelected();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(220, 20, 60)); // Crimson color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setUI(new BasicButtonUI());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 69, 0)); // Orange red color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(220, 20, 60)); // Crimson color
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClothingDonationPage frame = new ClothingDonationPage();
            frame.setLocationRelativeTo(null); // Center the frame
            frame.setVisible(true);
        });
    }
}
