package FLM;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class EmergencyContactsApp {
    private JFrame frame;
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;
    private Point initialClick;

    // Define contact details for each district
    private Map<String, DistrictContactDetails> districtContacts;

    public EmergencyContactsApp() {
        // Create frame and set it undecorated
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up main panel with BorderLayout
        panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(173, 216, 230)); // Light blue color

        // Initialize the contact details
        initializeContacts();

        // Create the UI components
        createUI();

        // Add the main panel to the frame
        frame.setContentPane(panel);

        // Create a custom title bar
        createCustomTitleBar();

        // Make frame visible
        frame.setVisible(true);
    }

    private void initializeContacts() {
        districtContacts = new HashMap<>();

        districtContacts.put("Thiruvananthapuram ", new DistrictContactDetails(
            "0471-2233111",
            "Medical College Hospital: 0471-2528386",
            "0471-100",
            "0471-2552056",
            "0471-2330000"
        ));
        districtContacts.put("Ernakulam ", new DistrictContactDetails(
            "0484-2423513",
            "Medical College Hospital: 0484-2754000",
            "0484-100",
            "0484-2390400",
            "0484-2340400"
        ));
        districtContacts.put("Thrissur ", new DistrictContactDetails(
            "0487-2362424",
            "Government Medical College: 0487-2200311",
            "0487-100",
            "0487-2331424",
            "0487-2324000"
        ));
        districtContacts.put("Palakkad ", new DistrictContactDetails(
            "0491-2505309",
            " Hospital: 0491-2531348",
            "0491-100",
            "0491-2570911",
            "0491-2525000"
        ));
        districtContacts.put("Malappuram ", new DistrictContactDetails(
            "0483-2736320",
            " Hospital: 0483-2734866",
            "0483-100",
            "0483-2737100",
            "0483-2737000"
        ));
        districtContacts.put("Kozhikode ", new DistrictContactDetails(
            "0495-2371002",
            "Government Medical College: 0495-2350217",
            "0495-100",
            "0495-2722056",
            "0495-2721000"
        ));
        districtContacts.put("Kannur ", new DistrictContactDetails(
            "0497-2713266",
            "Government Medical College: 0497-2852200",
            "0497-100",
            "0497-2701700",
            "0497-2700000"
        ));
        districtContacts.put("Wayanad ", new DistrictContactDetails(
            "04936-204151",
            " Hospital: 04936-202316",
            "04936-100",
            "04936-202003",
            "04936-202100"
        ));
        districtContacts.put("Pathanamthitta ", new DistrictContactDetails(
            "0468-2322515",
            "General Hospital: 0468-2222368",
            "0468-100",
            "0468-2222515",
            "0468-2222000"
        ));
        districtContacts.put("Kollam ", new DistrictContactDetails(
            "0474-2794002",
            " Hospital: 0474-2794355",
            "0474-100",
            "0474-2764400",
            "0474-2740000"
        ));
        districtContacts.put("Alappuzha ", new DistrictContactDetails(
            "1077",
            "Medical College Hospital: 0477-2282700",
            "0477-100",
            "0477-2242056",
            "0477-2230000"
        ));
        districtContacts.put("Kasaragod ", new DistrictContactDetails(
            "1077",
            " Hospital: 04994-223624",
            "04994-100",
            "04994-223402",
            "04994-222100"
        ));
        districtContacts.put("Idukki ", new DistrictContactDetails(
            "1077",
            " Hospital: 04862-232271",
            "0486-100",
            "04862-232056",
            "04862-232100"
        ));
        districtContacts.put("Kottayam ", new DistrictContactDetails(
            "1077",
            "Medical College Hospital: 0481-2597311",
            "0481-100",
            "0481-2560424",
            "0481-2560000"
        ));
    }

    private void createUI() {
        // Create top panel for the heading
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.LIGHT_GRAY);

        JLabel headerLabel = new JLabel("Emergency Contacts");
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Reduced the font size for better appearance
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Reduced padding
        topPanel.add(headerLabel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, districtContacts.size())); // Arrange buttons in a single row

        Dimension buttonSize = new Dimension(200, 30); // Set a common button size

        for (String district : districtContacts.keySet()) {
            JButton districtButton = new JButton(district) {
                @Override
                public Dimension getPreferredSize() {
                    return buttonSize;
                }
            };
            districtButton.setHorizontalAlignment(SwingConstants.CENTER); // Center align the text on the button
            districtButton.setFont(new Font("Arial", Font.PLAIN, 12)); // Set smaller font for buttons
            districtButton.setMargin(new Insets(2, 2, 2, 2)); // Set smaller padding
            districtButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showDistrictDetails(district);
                }
            });
            buttonPanel.add(districtButton);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Create table for details
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        panel.add(tableScrollPane, BorderLayout.CENTER);
    }

    private void createCustomTitleBar() {
        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(173, 216, 230)); // Light blue color
        titleBar.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Emergency Contact Numbers");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        titleBar.add(titleLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("X");
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setForeground(Color.RED);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the main application window
            }
        });
        titleBar.add(closeButton, BorderLayout.EAST);

        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                frame.getComponentAt(initialClick);
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // get location of Window
                int thisX = frame.getLocation().x;
                int thisY = frame.getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window to this position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                frame.setLocation(X, Y);
            }
        });

        panel.add(titleBar, BorderLayout.NORTH);
    }

    private void showDistrictDetails(String district) {
        DistrictContactDetails details = districtContacts.get(district);

        // Clear the table
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        // Set table columns
        tableModel.addColumn("Contact Type");
        tableModel.addColumn("Contact Number");

        // Add rows
        tableModel.addRow(new Object[]{" Control Room", details.getControlRoom()});
        tableModel.addRow(new Object[]{"Medical College Hospital", details.getMedicalCollege()});
        tableModel.addRow(new Object[]{"Fire Station", details.getFireStation()});
        tableModel.addRow(new Object[]{"Police Station", details.getPoliceStation()});
        tableModel.addRow(new Object[]{"Ambulance", details.getAmbulance()});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EmergencyContactsApp();
            }
        });
    }
}

class DistrictContactDetails {
    private String controlRoom;
    private String medicalCollege;
    private String fireStation;
    private String policeStation;
    private String ambulance;

    public DistrictContactDetails(String controlRoom, String medicalCollege, String fireStation, String policeStation, String ambulance) {
        this.controlRoom = controlRoom;
        this.medicalCollege = medicalCollege;
        this.fireStation = fireStation;
        this.policeStation = policeStation;
        this.ambulance = ambulance;
    }

    public String getControlRoom() {
        return controlRoom;
    }

    public String getMedicalCollege() {
        return medicalCollege;
    }

    public String getFireStation() {
        return fireStation;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public String getAmbulance() {
        return ambulance;
    }
}
