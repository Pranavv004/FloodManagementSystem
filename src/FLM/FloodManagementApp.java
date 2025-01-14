package FLM;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.border.EmptyBorder; 
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class FloodManagementApp extends JFrame {
    private JTextArea alertsArea;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField phoneField2;
    private JTextField locationField;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static JLabel notificationLabel;
    private JTable damTable;
    DefaultTableModel tableModel;
    private List<String> notificationsList;
    private boolean isLoggedIn = false;//intially not logged in
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField RphoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private boolean isWeatherPanelVisible = true;
    private JPanel weatherPanel;
    private JButton toggleWeatherButton;
    JTabbedPane tabbedPane;
    String newNotification;
    DatagramSocket ds = null;
    String initial_noti;
    String url="jdbc:postgresql://localhost:5432/users";
    String user="postgres";
    String pass="123";

    private void submitDonation1(String type) {
	    if (!isLoggedIn) {
	        JOptionPane.showMessageDialog(this, "Please login to make a donation.");
	        return;
	    }
	    if (type.equals("Cloth")) {
	        new ClothingDonationPage().setVisible(true);
	    } else if (type.equals("Cash")) {
	        new CashDonationPage().setVisible(true);
	    }
	}
 
    public FloodManagementApp() {
	    setTitle("Flood Management System");
	    setSize(1200, 800);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setLayout(new BorderLayout());

	    // Initialize user database and notifications list
	    notificationsList = new ArrayList<>();

	    // Add weather information section
	    weatherPanel = WeatherApp.createWeatherPanel();
	    add(weatherPanel, BorderLayout.WEST);

	    // Create a button to toggle the weather panel visibility
	    toggleWeatherButton = new JButton("Hide Weather Panel");
	    toggleWeatherButton.setPreferredSize(new Dimension(120, 25)); // Reduced size
	    toggleWeatherButton.addActionListener(this::toggleWeatherPanel);

	    // Set background color
	    getContentPane().setBackground(new Color(173, 216, 230)); // Light blue color

	    // Top panel with header and live notification ticker
	    JPanel topPanel = new JPanel(new BorderLayout());
	    topPanel.setBackground(new Color(2, 209, 250)); // Light coral color
	    topPanel.setPreferredSize(new Dimension(getWidth(), 100)); // Increase the height
	    topPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // Add top padding to top panel

	    // Login button
	    JButton loginButton = new JButton("Login");
	    styleButton(loginButton);
	    loginButton.setPreferredSize(new Dimension(100, 30)); // Reduced size
	    loginButton.addActionListener(e -> loginUser());
	    topPanel.add(loginButton, BorderLayout.WEST);
	    loginButton.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding to button

	    // Logout button
	    JButton logoutButton = new JButton("Logout");
	    styleButton(logoutButton);
	    logoutButton.setPreferredSize(new Dimension(100, 30)); // Reduced size
	    logoutButton.addActionListener(e -> logoutUser());
	    topPanel.add(logoutButton, BorderLayout.EAST);
	    logoutButton.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add padding to button

	    JLabel headerLabel = new JLabel("FLOOD MANAGEMENT", SwingConstants.CENTER);
	    headerLabel.setFont(new Font("Arial", Font.BOLD, 33)); // Increase the font size for better appearance
	    headerLabel.setForeground(Color.BLACK);
	    headerLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
	    topPanel.add(headerLabel, BorderLayout.CENTER);

	    // Create the scrolling notification label
	    notificationLabel = new JLabel("No Notifications:  ", SwingConstants.LEFT);
	    notificationLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    notificationLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    notificationLabel.setForeground(Color.BLACK);

	    JButton refreshButton = new JButton("Refresh");
	    styleButton(refreshButton);
	    refreshButton.addActionListener(e -> refresh());

	    JButton notificationsButton = new JButton("Notifications");
	    styleButton(notificationsButton);
	    notificationsButton.addActionListener(e -> showAllNotifications());

	    JPanel notificationPanel = new JPanel(new BorderLayout());
	    notificationPanel.setPreferredSize(new Dimension(getWidth(), 50)); // Increase the height
	    notificationPanel.setBackground(new Color(2, 209, 250));
	    notificationPanel.add(notificationLabel, BorderLayout.CENTER);
	    notificationPanel.add(notificationsButton, BorderLayout.EAST);

	    JPanel leftNotificationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    leftNotificationPanel.setBackground(new Color(2, 209, 250));
	    leftNotificationPanel.add(notificationsButton);

	    JPanel rightNotificationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    rightNotificationPanel.setBackground(new Color(2, 209, 250));
	    rightNotificationPanel.add(notificationLabel);
	    rightNotificationPanel.add(refreshButton);

	    notificationPanel.add(rightNotificationPanel, BorderLayout.EAST);
	    notificationPanel.add(leftNotificationPanel, BorderLayout.WEST);

	    topPanel.add(notificationPanel, BorderLayout.SOUTH);

	    // Implement the scrolling effect
	    Timer timer = new Timer(250, e -> {
	        String text = notificationLabel.getText();
	        notificationLabel.setText(text.substring(1) + text.charAt(0));
	    });
	    timer.start();

	    add(topPanel, BorderLayout.NORTH);

	    // Main content panel with tabs
	    tabbedPane = new JTabbedPane();

	    // Alerts tab
	    JPanel alertsPanel = new JPanel(new BorderLayout());
	    alertsPanel.setBackground(new Color(173, 216, 230)); // Light blue color

	    alertsArea = new JTextArea();
	    alertsArea.setEditable(false);
	    alertsPanel.add(new JScrollPane(alertsArea), BorderLayout.CENTER);

	    // Adding Dam Table to alerts panel
	    String[] columnNames = { "Dam", "District", "Max Capacity (Mm3)", "Current Level (Mm3)", "Last Updated" };
	    Object[][] data = {};

	    tableModel = new DefaultTableModel(data, columnNames) {
	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return false; // Make the table non-editable
	        }
	    };

	    damTable = new JTable(tableModel);
	    alertsPanel.add(new JScrollPane(damTable), BorderLayout.SOUTH);

	    tabbedPane.addTab("Flood Alerts", alertsPanel);

	    // User Problems tab (formerly Emergency Contact tab)
	    JPanel userProblemsPanel = new JPanel(new GridLayout(10, 10, 10, 10));
	    userProblemsPanel.setBackground(new Color(173, 216, 230)); // Light blue color

	    // Create a titled border with an empty border for padding
	    TitledBorder titledBorder = BorderFactory.createTitledBorder(
	            BorderFactory.createLineBorder(Color.BLACK), "",
	            TitledBorder.DEFAULT_JUSTIFICATION,
	            TitledBorder.DEFAULT_POSITION,
	            new Font("Arial", Font.BOLD, 14),
	            Color.BLACK);

	    EmptyBorder paddingBorder = new EmptyBorder(20, 20, 20, 20); // Add padding to the inside of the border
	    CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(titledBorder, paddingBorder);

	    userProblemsPanel.setBorder(compoundBorder); // Set the compound border
	    GridBagConstraints gbc1 = new GridBagConstraints();
	    gbc1.insets = new Insets(10, 10, 10, 10); // Padding between elements

	    gbc1.gridx = 0;
	    gbc1.gridy = 0;
	    gbc1.gridwidth = 2;
	    userProblemsPanel.add(new JLabel("Name:"), gbc1);
	    gbc1.gridx = 2;
	    gbc1.gridy = 0;
	    gbc1.gridwidth = 2;
	    nameField = new JTextField();
	    nameField.setPreferredSize(new Dimension(100, 30)); // Set preferred size for nameField
	    userProblemsPanel.add(nameField, gbc1);

	    gbc1.gridx = 0;
	    gbc1.gridy = 1;
	    gbc1.gridwidth = 2;
	    userProblemsPanel.add(new JLabel("Phone:"), gbc1);

	    gbc1.gridx = 2;
	    gbc1.gridy = 1;
	    gbc1.gridwidth = 2;
	    phoneField2 = new JTextField();
	    phoneField2.setPreferredSize(new Dimension(100, 30)); // Set preferred size for phoneField
	    userProblemsPanel.add(phoneField2, gbc1);

	    gbc1.gridx = 0;
	    gbc1.gridy = 2;
	    gbc1.gridwidth = 2;
	    userProblemsPanel.add(new JLabel("Location:"), gbc1);
	    locationField = new JTextField();
	    locationField.setPreferredSize(new Dimension(100, 30)); // Set preferred size for locationField
	    userProblemsPanel.add(locationField, gbc1);

	    gbc1.gridx = 0;
	    gbc1.gridy = 3;
	    gbc1.gridwidth = 2;
	    userProblemsPanel.add(new JLabel("Describe your problem:"), gbc1);

	    gbc1.gridx = 2;
	    gbc1.gridy = 3;
	    gbc1.gridwidth = 2;
	    JTextArea problemDescriptionArea = new JTextArea();
	    problemDescriptionArea.setLineWrap(true);
	    problemDescriptionArea.setWrapStyleWord(true);
	    problemDescriptionArea.setPreferredSize(new Dimension(300, 100));
	    JScrollPane scrollPane = new JScrollPane(problemDescriptionArea);
	    scrollPane.setPreferredSize(new Dimension(300, 100));
	    userProblemsPanel.add(scrollPane, gbc1);

	    // Add an empty label for spacing before the button
	    gbc1.gridx = 0;
	    gbc1.gridy = 4;
	    gbc1.gridwidth = 4;
	    userProblemsPanel.add(new JLabel(), gbc1);

	    JButton submitContactButton = new JButton("Submit");
	    styleButton(submitContactButton);
	    submitContactButton.addActionListener(e -> submitUserProblem(problemDescriptionArea,phoneField2,locationField,nameField));
	    gbc1.gridx = 0;
	    gbc1.gridy = 5;
	    gbc1.gridwidth = 4;
	    gbc1.anchor = GridBagConstraints.CENTER;
	    userProblemsPanel.add(submitContactButton, gbc1);

	    userProblemsPanel.setPreferredSize(new Dimension(450, 500));

	    JPanel centeredProblemsPanel = new JPanel(new GridBagLayout());
	    centeredProblemsPanel.setBackground(new Color(173, 216, 230));
	    centeredProblemsPanel.add(userProblemsPanel);

	    tabbedPane.addTab("User Problems", centeredProblemsPanel);

	    // Donation tab
	    JPanel donationPanel = new JPanel(new BorderLayout());
	    donationPanel.setBackground(new Color(173, 216, 230)); // Light blue color

	    JPanel donationFormPanel = new JPanel(new GridBagLayout());
	    donationFormPanel.setBorder(BorderFactory.createTitledBorder("Donation"));
	    donationFormPanel.setBackground(new Color(173, 216, 230)); // Light blue color

	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = 0;
	    gbc.gridy = GridBagConstraints.RELATIVE;
	    gbc.insets = new Insets(10, 10, 10, 10);
	    gbc.anchor = GridBagConstraints.CENTER;

	    JButton clothButton = new JButton("Cloth");
	    styleButton(clothButton);
	    clothButton.addActionListener(e -> submitDonation1("Cloth"));
	    donationFormPanel.add(clothButton, gbc);

	    JButton cashButton = new JButton("Cash");
	    styleButton(cashButton);
	    cashButton.addActionListener(e -> submitDonation1("Cash"));
	    donationFormPanel.add(cashButton, gbc);

	    JPanel centeredDonationPanel = new JPanel(new GridBagLayout());
	    centeredDonationPanel.setBackground(new Color(173, 216, 230));
	    centeredDonationPanel.add(donationFormPanel, new GridBagConstraints());

	    donationPanel.add(centeredDonationPanel, BorderLayout.CENTER);

	    tabbedPane.addTab("Donations", donationPanel);

	    add(tabbedPane, BorderLayout.CENTER);
	    tabbedPane.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	            if (tabbedPane.getSelectedIndex() == 1 || tabbedPane.getSelectedIndex() == 2) {
	                if (!isLoggedIn) {
	                    JOptionPane.showMessageDialog(FloodManagementApp.this, "Please login to access this tab.");
	                    tabbedPane.setSelectedIndex(0); // Redirect to the first tab
	                }
	            }
	        }
	    });

	    // Emergency Contacts panel at the bottom
	    JPanel emergencyContactsPanel = new JPanel(new BorderLayout());
	    emergencyContactsPanel.setBackground(new Color(255, 0, 0)); // Light coral color
	    emergencyContactsPanel.setBorder(BorderFactory.createTitledBorder(""));
	    emergencyContactsPanel.setPreferredSize(new Dimension(getWidth(), 80)); // Reduced height

	    JLabel contactsLabel = new JLabel("                              Emergency Contacts", SwingConstants.CENTER);
	    contactsLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Increase font size
	    contactsLabel.setForeground(Color.WHITE); // Change font color to white
	    emergencyContactsPanel.add(contactsLabel, BorderLayout.NORTH);

	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Changed to FlowLayout for single row
	    buttonPanel.setBackground(new Color(255, 0, 0)); // Match background color

	    JButton callButton = new JButton("Call");
	    styleButton(callButton);
	    callButton.setPreferredSize(new Dimension(80, 25)); // Reduced size
	    callButton.addActionListener(e -> initiateCall());
	    buttonPanel.add(callButton);

	    JButton chatButton = new JButton("Chat");
	    styleButton(chatButton);
	    chatButton.setPreferredSize(new Dimension(80, 25)); // Reduced size
	    chatButton.addActionListener(e -> initiateChat());
	    buttonPanel.add(chatButton);

	    JPanel weatherButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    weatherButtonPanel.setBackground(new Color(255, 0, 0)); // Match background color
	    weatherButtonPanel.add(toggleWeatherButton);

	    emergencyContactsPanel.add(weatherButtonPanel, BorderLayout.WEST);
	    emergencyContactsPanel.add(buttonPanel, BorderLayout.CENTER);

	    add(emergencyContactsPanel, BorderLayout.SOUTH);

	    // Alerts panel text alignment
	    alertsArea.setLineWrap(true);
	    alertsArea.setWrapStyleWord(true);

	    refresh();

	    setVisible(true);
	}

    void refresh() {
	    notification();
	    retrivedam();
	    loadalert();
    }
    
    private void toggleWeatherPanel(ActionEvent e) {
	        isWeatherPanelVisible = !isWeatherPanelVisible;
	        if (isWeatherPanelVisible) {
	            add(weatherPanel, BorderLayout.WEST);
	            toggleWeatherButton.setText("Hide Weather");
	        } else {
	            remove(weatherPanel);
	            toggleWeatherButton.setText("Show Weather");
	        }
	        revalidate();
	        repaint();
	    }
    
    void loadalert(){
	    String query = "SELECT * FROM alert_table";
	        try {
	            Class.forName("org.postgresql.Driver");
	            Connection con = DriverManager.getConnection(url, user, pass);
	            Statement stmt = con.createStatement();
	            ResultSet resultSet = stmt.executeQuery(query);
	            while (resultSet.next()) {
	        	    alertsArea.setText(resultSet.getString("alert"));
	            }
	            resultSet.close();
	            stmt.close();
	            con.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
    }
    void retrivedam() {
		String query="select  * from dam_table";
		try {
			Class.forName("org.postgresql.Driver");
			Connection con=DriverManager.getConnection(url,user,pass);
			Statement sta=con.createStatement();
			ResultSet result=sta.executeQuery(query);
			
			 tableModel.setRowCount(0);
			while(result.next()) {
				 tableModel.addRow(new Object[]{
						 result.getString("name"),
						 result.getString("district"),
						 result.getString("maxlvl"),
						 result.getString("currlvl"),
						 result.getString("lastupdate")
						 });
			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
    void notification() {
	        String query3 = "SELECT * FROM notification_table ORDER BY time DESC LIMIT 1;";
	        try {
	        	//Thread.sleep(5000);
	                Class.forName("org.postgresql.Driver");
	                Connection c = DriverManager.getConnection(url, user, pass);
			Statement s=c.createStatement();
			ResultSet result=s.executeQuery(query3);
			if(result.next()) {
				if(result.getString("noti").equals(initial_noti)) {
				}//checking if 1st loaded notification is same when refreshed
				else {
					 initial_noti = result.getString("noti");
					notificationLabel.setText(result.getString("noti") + "        ");
				}
			}
			c.close();
			}
			catch(Exception e){
				System.out.print(e);
			}
    }
    
    private void styleButton(JButton button) {
        button.setBackground(Color.WHITE); // Default background color
        button.setForeground(Color.BLACK); // Default text color
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Bold font
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add padding
        button.setFocusPainted(false); // Remove focus border
        button.setUI(new RoundedButtonUI()); // Apply rounded button UI
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.BLACK); // Hover background color
                button.setForeground(Color.WHITE); // Hover text color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE); // Default background color
                button.setForeground(Color.BLACK); // Default text color
            }
        });
    }

    // Custom ButtonUI for rounded buttons
    private static class RoundedButtonUI extends BasicButtonUI {
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            AbstractButton button = (AbstractButton) c;
            button.setOpaque(false);
            button.setBorder(new EmptyBorder(5, 15, 5, 15));
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            paintBackground(g, button, button.getModel().isPressed() ? 2 : 0);
            super.paint(g, c);
        }

        private void paintBackground(Graphics g, JComponent c, int yOffset) {
            Dimension size = c.getSize();
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground());
            g2.fillRoundRect(0, yOffset, size.width, size.height - yOffset, 30, 30);
        }
    }

    private void initializeChatConnection() {
    }
    void loginUser() {
	    if (isLoggedIn) {
	        JOptionPane.showMessageDialog(this, "User already logged in");
	        return;
	    }

	    JPanel loginPanel = new JPanel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(9,9, 9, 9); // Reduced insets for less margin

	    JTextField logusernameField = new JTextField();
	    JPasswordField logpasswordField = new JPasswordField();

	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.gridwidth = 1;
	    gbc.fill = GridBagConstraints.NONE;
	    loginPanel.add(new JLabel("Username:"), gbc);

	    gbc.gridx = 1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    logusernameField.setPreferredSize(new Dimension(200, 30));
	    loginPanel.add(logusernameField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.fill = GridBagConstraints.NONE;
	    loginPanel.add(new JLabel("Password:"), gbc);

	    gbc.gridx = 1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    logpasswordField.setPreferredSize(new Dimension(200, 30));
	    loginPanel.add(logpasswordField, gbc);

	    // Show password checkbox
	    JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
	    gbc.gridx = 1; // Aligning the checkbox to the same column as the password field
	    gbc.gridy = 2; // Row just below the password field
	    gbc.anchor = GridBagConstraints.WEST; // Align to the left
	    loginPanel.add(showPasswordCheckBox, gbc);

	    showPasswordCheckBox.addActionListener(e -> {
	        if (showPasswordCheckBox.isSelected()) {
	            logpasswordField.setEchoChar((char) 0);
	        } else {
	            logpasswordField.setEchoChar('•');
	        }
	    });

	    JPanel combinedPanel = new JPanel(new BorderLayout());
	    combinedPanel.add(loginPanel, BorderLayout.CENTER);

	    JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    JButton registerButton = new JButton("Don't have an account?");
	    registerButton.addActionListener(e -> openRegisterInterface());
	    registerPanel.add(registerButton);
	    combinedPanel.add(registerPanel, BorderLayout.SOUTH);

	    int option = JOptionPane.showConfirmDialog(this, combinedPanel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

	    if (option == JOptionPane.OK_OPTION) {
	        String query2 = "insert into register_table values(?,?,?,?)";
	        try {
	            String username = logusernameField.getText();
	            String password = new String(logpasswordField.getPassword());
	            if (username.isEmpty() || password.isEmpty()) {
	                JOptionPane.showMessageDialog(this, "Fill all details");
	            } else {
	                Class.forName("org.postgresql.Driver");
	                Connection c = DriverManager.getConnection(url, user, pass);
	                String query1 = "select * from register_table where username='" + username + "' and password='" + password + "'";
	                Statement checkacc = c.createStatement();
	                ResultSet res = checkacc.executeQuery(query1);
	                if (res.next()) {
	                    JOptionPane.showMessageDialog(this, "Login Successful");
	                    isLoggedIn = true;
	                } else {
	                    JOptionPane.showMessageDialog(this, "Invalid username or password.");
	                }
	                c.close();
	                checkacc.close();
	            }
	        } catch (Exception e) {
	            System.out.println(e);
	        }
	    }
	}


    void submitUserProblem(JTextArea problemDescriptionArea,JTextField phoneField,JTextField locationField,JTextField nameField) {
		String query2="insert into userproblem_table values(?,?,?,?)";
		String name = nameField.getText();
		    String phone = phoneField.getText();
		    String location = locationField.getText();
		 String problem = problemDescriptionArea.getText();
		try {
			if(name.isEmpty() || phone.isEmpty() || location.isEmpty() || problem.isEmpty()){
				JOptionPane.showMessageDialog(this,"Fill all details");
			}
			else {
				Class.forName("org.postgresql.Driver");
				Connection c=DriverManager.getConnection(url,user,pass);
				PreparedStatement uproblem=c.prepareStatement(query2);
				String userRegex ="^([a-zA-Z\\.]{3,50})$";
				Pattern pattern = Pattern.compile(userRegex);
				Matcher matcher1 = pattern.matcher(name);
				
				String phoneRegex ="^\\d{10}$";
				Pattern pattern1 = Pattern.compile(phoneRegex);
				Matcher matcher2 = pattern1.matcher(phone);
				
				String locationRegex ="^([a-zA-Z0-9\\s,]{2,75})$";
				Pattern pattern3 = Pattern.compile(locationRegex);
				Matcher matcher3 = pattern3.matcher(location);
				
				String problemRegex = "^([a-zA-Z0-9-/\\.\\s,]+)$";
				Pattern pattern4 = Pattern.compile(problemRegex);
				Matcher matcher4 = pattern4.matcher(problem);
				if(!matcher1.matches()) {
					JOptionPane.showMessageDialog(this,"Invalid Name format, Maximum 20 characters");
				}
				else if(!matcher2.matches()) {
					JOptionPane.showMessageDialog(this,"Invalid Phone number, must be 10 digits.");
				}
				else if(!matcher3.matches()){
					JOptionPane.showMessageDialog(this,"Please enter a valid location ,maximum 75 characters)");
				}
				else if(!matcher4.matches() || problem.length() > 25000) {
				JOptionPane.showMessageDialog(this, "Please enter a valid problem, maximum 25000 characters.");
				}	    
				else {
					uproblem.setString(1,name);
					uproblem.setLong(2, Long.parseLong(phone));//convert phone number string to long to match that in DB
					uproblem.setString(3,location);
					uproblem.setString(4,problem);
					int check=uproblem.executeUpdate();
					if(check>0) {
						JOptionPane.showMessageDialog(this,"User problem submitted");
						nameField.setText("");//for clearing fields after submiting
						phoneField.setText("");
						locationField.setText("");
						problemDescriptionArea.setText("");
					}
				}
			c.close();
			}
	}catch(Exception e) {
		System.out.println(e);
	}
    }

    private void submitDonation(String type) {
        // Implement the functionality to submit donation information
        JOptionPane.showMessageDialog(this, "Donation submitted:\nType: " + type);
    }
    
    private void initiateCall() {
	    EmergencyContactsApp EmergencyContactsApp = new EmergencyContactsApp();
    }
    void openRegisterInterface() {
	    JPanel registerPanel = new JPanel(new GridBagLayout());
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(10, 10, 10, 10);

	    usernameField = new JTextField();
	    emailField = new JTextField();
	    RphoneField = new JTextField();
	    passwordField = new JPasswordField();
	    confirmPasswordField = new JPasswordField();

	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    registerPanel.add(new JLabel("Username:"), gbc);
	    gbc.gridx = 1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    usernameField.setPreferredSize(new Dimension(200, 30));
	    registerPanel.add(usernameField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    gbc.fill = GridBagConstraints.NONE;
	    registerPanel.add(new JLabel("Email:"), gbc);
	    gbc.gridx = 1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    emailField.setPreferredSize(new Dimension(200, 30));
	    registerPanel.add(emailField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.fill = GridBagConstraints.NONE;
	    registerPanel.add(new JLabel("Phone:"), gbc);
	    gbc.gridx = 1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    RphoneField.setPreferredSize(new Dimension(200, 30));
	    registerPanel.add(RphoneField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 3;
	    gbc.fill = GridBagConstraints.NONE;
	    registerPanel.add(new JLabel("Password:"), gbc);
	    gbc.gridx = 1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    passwordField.setPreferredSize(new Dimension(200, 30));
	    registerPanel.add(passwordField, gbc);

	    gbc.gridx = 0;
	    gbc.gridy = 4;
	    gbc.fill = GridBagConstraints.NONE;
	    registerPanel.add(new JLabel("Confirm Password:"), gbc);
	    gbc.gridx = 1;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    confirmPasswordField.setPreferredSize(new Dimension(200, 30));
	    registerPanel.add(confirmPasswordField, gbc);

	    JOptionPane optionPane = new JOptionPane(registerPanel, JOptionPane.PLAIN_MESSAGE);
	    optionPane.setOptions(new Object[]{"Register", "Cancel"});
	    JDialog dialog = optionPane.createDialog(this, "Register");

	    dialog.setVisible(true);

	    Object selectedValue = optionPane.getValue();

	    if (selectedValue != null && selectedValue.equals("Register")) {
	        String password = new String(passwordField.getPassword());
	        String confirmPassword = new String(confirmPasswordField.getPassword());

	        if (!password.equals(confirmPassword)) {
	            JOptionPane.showMessageDialog(this, "Passwords do not match.");
	        } else {
	        	registeruser();
	        }
	    }
	}


    private void logoutUser() {
	    if (!isLoggedIn) {
	        JOptionPane.showMessageDialog(this, "No account logged in");
	        return;
	    }
	    else {
		JOptionPane.showMessageDialog(this, "Logged out successfully!");
		tabbedPane.setSelectedIndex(0);
		isLoggedIn = false;
	    }
    }

    private void showAllNotifications() {
        JFrame notificationsFrame = new JFrame("All Notifications");
        notificationsFrame.setSize(500, 400);
        notificationsFrame.setLayout(new BorderLayout());
        
	String query = "SELECT noti, to_char(time, 'HH24:MI') AS formatted_time FROM notification_table";
	notificationsList.clear();//clear the notifications to avoid duplicate
	try {
		Class.forName("org.postgresql.Driver");
		Connection con=DriverManager.getConnection(url,user,pass);
		Statement s=con.createStatement();
		ResultSet result=s.executeQuery(query);
		while(result.next()) {
			notificationsList.add(result.getString("formatted_time")+"         "+result.getString("noti"));
		}
		result.close();
		s.close();
		con.close();
	}
	catch(Exception e) {
		System.out.println(e);
	}
        
        JTextArea notificationsArea = new JTextArea();
        notificationsArea.setEditable(false);
        for (String notification : notificationsList) {
            notificationsArea.append(notification + "\n");
        }

        notificationsFrame.add(new JScrollPane(notificationsArea), BorderLayout.CENTER);
        notificationsFrame.setVisible(true);
    }
    void registeruser() {
		String query2="insert into register_table values(?,?,?,?)";
		try {
			if(usernameField.getText().isEmpty() || emailField.getText().isEmpty()  || RphoneField.getText().isEmpty()  || passwordField.getText().isEmpty()  || confirmPasswordField.getText().isEmpty()){
				JOptionPane.showMessageDialog(this,"Fill all details");
			}
			else {
				Class.forName("org.postgresql.Driver");
				Connection c=DriverManager.getConnection(url,user,pass);
				String query1="select * from register_table where username='"+usernameField.getText()+"' or email='"+emailField.getText()+"'";
				Statement checkacc=c.createStatement();
				ResultSet res=checkacc.executeQuery(query1);
				if(res.next()) {
					JOptionPane.showMessageDialog(this,"Username or email already exist!");
				}
				else {
					PreparedStatement register=c.prepareStatement(query2);
					String userRegex ="^[a-z]+$";
					Pattern pattern1 = Pattern.compile(userRegex);
					Matcher matcher1 = pattern1.matcher(usernameField.getText());
					
					String passRegex ="^([a-zA-Z0-9]+)$";
					Pattern pattern2 = Pattern.compile(passRegex);
					Matcher matcher2 = pattern2.matcher(passwordField.getText());
					
					String emailRegex ="^([a-zA-Z0-9\\._-]+)@([a-zA-Z0-9-]+)\\.([a-zA-Z0-9]{2,4})$";
					Pattern pattern = Pattern.compile(emailRegex);
					Matcher matcher = pattern.matcher(emailField.getText());
					if(!matcher1.matches()) {
						JOptionPane.showMessageDialog(this,"Invalid, username must be alphabets (lowercase)");
					}
					else if(usernameField.getText().length()>20 && usernameField.getText().length()<3) {
						JOptionPane.showMessageDialog(this,"Username must be between 3-20 characters");
					}
					else if( !matcher2.matches()) {
						JOptionPane.showMessageDialog(this,"Invalid password only alphanumeric characters");
					}
					else if( !matcher.matches()) {
						JOptionPane.showMessageDialog(this,"Invalid email");
					}
					else if(RphoneField.getText().length()>10 || RphoneField.getText().length()<10) {
						JOptionPane.showMessageDialog(this,"Invalid Phone number");
					}
					else if(passwordField.getText().length()<8) {
						JOptionPane.showMessageDialog(this,"Password characters should minimum 8");
					}
					else if(passwordField.getText().length()>20) {
						JOptionPane.showMessageDialog(this,"Password characters should maximum 20");
					}
					else {
						register.setString(1,usernameField.getText());
						register.setString(2,emailField.getText());
						register.setString(3,RphoneField.getText());
						register.setLong(3,Long.parseLong(RphoneField.getText()));
						register.setString(4,passwordField.getText());
						int check=register.executeUpdate();
						if(check>0) {
							JOptionPane.showMessageDialog(this,"Account Registered Successfully");
						}
					}
				}	
				c.close();
				checkacc.close();
			}
		}catch(Exception e) {
			System.out.println(e);
		}
	}
    private void initiateChat() {
        SwingUtilities.invokeLater(() -> {
            try {
                FloodManagementUserClient userClient = new FloodManagementUserClient("localhost", 9876);
                userClient.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    } 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FloodManagementApp app = new FloodManagementApp();
            app.setVisible(true);
        });
    }

	public void updateFloodStatus(String cityName, double temperature) {
		// TODO Auto-generated method stub
		
	}
}

