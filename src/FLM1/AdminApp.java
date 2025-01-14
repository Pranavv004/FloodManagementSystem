package FLM1;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.DatagramPacket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminApp extends JFrame {
    private static AdminApp instance;
    private JTextArea alertsArea;
    public JTextField notificationField;
    private JTable damTable;
    private JTable problemsTable;
    JTable donationsTable;
    private DefaultTableModel tableModel;
    private DefaultTableModel tableModel2;
    private DefaultTableModel donationsTableModel;
    private List<String> notificationsList;
    private JLabel notificationLabel;
    String noti = null;
    byte[] buffer = new byte[1024];
    DatagramPacket dp2;
    String msg2;
    String url = "jdbc:postgresql://localhost:5432/users";
    String user = "postgres";
    String pass = "123";

    private AdminApp() {
        setTitle("Admin Panel");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize notifications list
        notificationsList = new ArrayList<>();

        // Set background color
        getContentPane().setBackground(new Color(173, 216, 230)); // Light blue color

        // Top panel with header and live notification ticker
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(2, 209, 250)); // Light coral color
        topPanel.setPreferredSize(new Dimension(getWidth(), 100)); // Increase the height
        topPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // Add top padding to top panel

        JLabel headerLabel = new JLabel("Admin Panel", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 33)); // Increase the font size for better appearance
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        topPanel.add(headerLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Main content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Notifications tab
        JPanel notificationsPanel = new JPanel(new BorderLayout());
        notificationsPanel.setBackground(new Color(173, 216, 230)); // Light blue color

        notificationLabel = new JLabel("Live Notification: " + (notificationsList.isEmpty() ? "No notifications" : notificationsList.get(0)), SwingConstants.LEFT);
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        notificationLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        notificationLabel.setForeground(Color.BLACK);

        JPanel notificationEditPanel = new JPanel(new GridLayout(3, 1)); // Increase grid layout rows to 3
        notificationField = new JTextField();
        JButton updateNotificationButton = new JButton("Update Live Notification");
        styleButton(updateNotificationButton);
        updateNotificationButton.addActionListener(e -> addnotification());

        // Add the new Notifications button
        JButton notificationsButton = new JButton("Notifications");
        styleButton(notificationsButton);
        notificationsButton.addActionListener(e -> showAllNotifications());

        notificationEditPanel.add(notificationField);// Notification type area
        notificationEditPanel.add(updateNotificationButton);// 1st bottom button
        notificationEditPanel.add(notificationsButton); // 2nd bottom button

        notificationsPanel.add(notificationLabel, BorderLayout.NORTH);
        notificationsPanel.add(notificationEditPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Edit Notifications", notificationsPanel);
        
   
        // Alerts tab
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.setBackground(new Color(173, 216, 230)); // Light blue color

        alertsArea = new JTextArea();
        alertsArea.setEditable(true);
        alertsPanel.add(new JScrollPane(alertsArea), BorderLayout.CENTER);

        // Add the update button for alerts
        JButton updateAlertButton = new JButton("Update Alert");
        styleButton(updateAlertButton);
        updateAlertButton.addActionListener(e -> updateAlert());

        alertsPanel.add(updateAlertButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Edit Alerts", alertsPanel);

        // Dam Data tab
        JPanel damDataPanel = new JPanel(new BorderLayout());
        damDataPanel.setBackground(new Color(173, 216, 230)); // Light blue color

        String[] columnNames = { "Dam", "District", "Max Capacity (Mm3)", "Current Level (Mm3)", "Last Updated" };
        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table non-editable
            }
        };

        damTable = new JTable(tableModel);
        damDataPanel.add(new JScrollPane(damTable), BorderLayout.CENTER);

        JPanel damEditPanel = new JPanel(new GridLayout(2, 1));
        JButton addRowButton = new JButton("Add Row");
        styleButton(addRowButton);
        addRowButton.addActionListener(e -> addRowToDamTable());
        JButton removeRowButton = new JButton("Remove Selected Row");
        styleButton(removeRowButton);
        removeRowButton.addActionListener(e -> removeSelectedRowFromDamTable());

        damEditPanel.add(addRowButton);
        damEditPanel.add(removeRowButton);

        damDataPanel.add(damEditPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Edit Dam Data", damDataPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Problems Viewer tab
        tableModel2 = new DefaultTableModel(new String[]{"Name", "Phone", "Location", "Problem"}, 0) {
        	    public boolean isCellEditable(int row, int column) {
        	        return false; // Make the table non-editable
        	    }
        	};
        problemsTable = new JTable(tableModel2);
        problemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        problemsTable.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                super.setSelectionInterval(-1, -1);
            }
        });
        
        // Donations Viewer tab
        donationsTableModel = new DefaultTableModel(new String[]{"Username", "Phone", "Amount"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        donationsTable = new JTable(donationsTableModel);
        JScrollPane donationsScrollPane = new JScrollPane(donationsTable);
        tabbedPane.addTab("View Donations", null, donationsScrollPane, "View user donations");

 
        // Set custom cell renderer for wrapping text in the Problem column
        problemsTable.getColumnModel().getColumn(3).setCellRenderer(new TextAreaRenderer());
        problemsTable.setRowHeight(50);

        JScrollPane scrollPane = new JScrollPane(problemsTable);
        tabbedPane.addTab("View problems", null, scrollPane, "View user problems");

        // Bottom panel with footer
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(2, 209, 250)); // Light coral color
        JLabel footerLabel = new JLabel("© 2024 Flood Management System");
        bottomPanel.add(footerLabel);
        add(bottomPanel, BorderLayout.SOUTH);
        
        retrivedam();
        retriveProblems();
        loadalert();
        loadnoti();
        retriveDonations();
        // Add the FloodManagementServer tab
        FloodManagementServer floodServer = new FloodManagementServer();
        JPanel floodServerPanel = new JPanel(new BorderLayout());
        floodServerPanel.add(floodServer.getContentPane(), BorderLayout.CENTER);
        tabbedPane.addTab("Flood Management Server", floodServerPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
    void retriveDonations() {
	    String query = "SELECT * FROM donation_table";
	    try {
	        Class.forName("org.postgresql.Driver");
	        Connection con = DriverManager.getConnection(url, user, pass);
	        Statement stmt = con.createStatement();
	        ResultSet resultSet = stmt.executeQuery(query);
	        
	        // Clear existing rows
	        donationsTableModel.setRowCount(0);

	        // Add rows to the table model from the result set
	        while (resultSet.next()) {
	            String username = resultSet.getString("name");
	            String phone = resultSet.getString("phone");
	            String amount = resultSet.getString("amount");
	            donationsTableModel.addRow(new Object[]{username, phone, amount});
	        }
	        resultSet.close();
	        stmt.close();
	        con.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
    
    public DefaultTableModel getDamTableModel() {
        return tableModel;
    }
    
    void loadnoti() {
	        String query3 = "SELECT * FROM notification_table ORDER BY time DESC LIMIT 1;";
	        try {
	                Class.forName("org.postgresql.Driver");
	                Connection c = DriverManager.getConnection(url, user, pass);
			Statement s=c.createStatement();
			ResultSet result=s.executeQuery(query3);
			if(result.next()) {
					notificationLabel.setText("Live notification: "+result.getString("noti"));
				}
			c.close();
			}
			catch(Exception e){
				System.out.print(e);
			}
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
    void addnotification() {
        noti = notificationField.getText();
        if (noti.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Notification text cannot be empty.");
        } else {
                String query = "insert into notification_table values(?)";
                try {
                    Connection c = DriverManager.getConnection(url, user, pass);
                    PreparedStatement result = c.prepareStatement(query);
                    result.setString(1, noti);
                    int check = result.executeUpdate();
                    if (check > 0) {
                	    notificationLabel.setText(noti);
                        JOptionPane.showMessageDialog(this, "Notification Added");
                    } else {
                        System.out.println("Notification cannot be Added");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
        }
    }
    
    void retriveProblems() {
        String query = "SELECT username, phone, location, problem FROM userproblem_table";
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            // Add rows to the table model from the result set
            while (resultSet.next()) {
                String name = resultSet.getString("username");
                String phone = resultSet.getString("phone");
                String location = resultSet.getString("location");
                String problem = resultSet.getString("problem");
                tableModel2.addRow(new Object[]{name, phone, location, problem});
            }

            resultSet.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAllNotifications() {
        StringBuilder notifications = new StringBuilder("All Notifications:\n");
        for (String notification : notificationsList) {
            notifications.append(notification).append("\n");
        }
        JOptionPane.showMessageDialog(this, notifications.toString());
    }

    void updateAlert() {
        String newAlert = alertsArea.getText();
        if (!newAlert.isEmpty()) {
                String query = "update alert_table set alert=?";
                try {
                	    Class.forName("org.postgresql.Driver");
			    Connection c = DriverManager.getConnection(url, user, pass);
			    PreparedStatement result = c.prepareStatement(query);
			    result.setString(1,newAlert);
			    int check = result.executeUpdate();
			    if (check > 0) {
				    alertsArea.setText(newAlert);
				    JOptionPane.showMessageDialog(this, "Alert updated successfully!");
			    }
			    else {
				    JOptionPane.showMessageDialog(this, "Alert cannot be updated!");
			    }
			} 
                catch (Exception e) {
			    System.out.println(e);
			}
        }
        else {
            JOptionPane.showMessageDialog(this, "Alert text cannot be empty.");
        }
    }

    void retrivedam() {
        String query = "select  * from dam_table";
        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement sta = con.createStatement();
            ResultSet result = sta.executeQuery(query);
            while (result.next()) {
                tableModel.addRow(new Object[]{result.getString("name"), result.getString("district"), result.getString("maxlvl"), result.getString("currlvl"), result.getString("lastupdate")});
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void addRowToDamTable() {
        String dam = JOptionPane.showInputDialog(this, "Enter Dam Name:");
        String district = JOptionPane.showInputDialog(this, "Enter District:");
        String maxCapacity = JOptionPane.showInputDialog(this, "Enter Max Capacity (Mm3):");
        String currentLevel = JOptionPane.showInputDialog(this, "Enter Current Level (Mm3):");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String lastUpdated = LocalDate.now().format(formatter);
        
	String damRegex ="^([a-zA-Z]{4,30})$";
	Pattern pattern = Pattern.compile(damRegex);
	Matcher matcher1 = pattern.matcher(dam);
	
	String districtRegex ="^([a-zA-Z]{6,18})$";
	Pattern pattern2 = Pattern.compile(districtRegex);
	Matcher matcher2 = pattern2.matcher(district);
	
	String digitRegex ="^\\d{0,6}$";
	Pattern pattern3 = Pattern.compile(digitRegex);
	Matcher matcher3 = pattern3.matcher(maxCapacity);
	
	String digit2Regex ="^\\d{0,6}$";
	Pattern pattern4 = Pattern.compile(digitRegex);
	Matcher matcher4 = pattern4.matcher(currentLevel);
	
	if(!matcher1.matches()) {
		JOptionPane.showMessageDialog(this,"Invalid Dam name");
	}
	
	else if(!matcher2.matches()) {
		JOptionPane.showMessageDialog(this,"Invalid District name");
	}
	
	else if(!matcher3.matches()) {
		JOptionPane.showMessageDialog(this,"Invalid Maximum capacity");
	}
	
	else if(!matcher4.matches()) {
		JOptionPane.showMessageDialog(this,"Invalid Current level");
	}
       
        else if (dam != null && district != null && maxCapacity != null && currentLevel != null && lastUpdated != null) {
            String query = "insert into dam_table values(?,?,?,?,?)";
            try {
                Connection c = DriverManager.getConnection(url, user, pass);
                PreparedStatement result = c.prepareStatement(query);
                result.setString(1, dam);
                result.setString(2, district);
                result.setString(3, maxCapacity);
                result.setString(4, currentLevel);
                result.setString(5, lastUpdated);
                int check = result.executeUpdate();
                if (check > 0) {
                    JOptionPane.showMessageDialog(this, "Dam info added");
                    tableModel.addRow(new Object[]{dam, district, Integer.parseInt(maxCapacity), Integer.parseInt(currentLevel), lastUpdated});
                } else {
                    System.out.println("Dam cannot be added");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.");
        }
    }

    private void removeSelectedRowFromDamTable() {
        int selectedRow = damTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) damTable.getValueAt(selectedRow, 0);
            String query = "delete from dam_table where name=?";
            try {
                Connection c = DriverManager.getConnection(url, user, pass);
                PreparedStatement result = c.prepareStatement(query);
                result.setString(1, name);
                int check = result.executeUpdate();
                if (check == 1) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Dam removed");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to remove.");
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(2, 209, 250)); // Light coral color
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setUI(new BasicButtonUI());
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 105, 180)); // Darker coral color
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(2, 209, 250)); // Light coral color
            }
        });
    }

    public static AdminApp getInstance() {
        if (instance == null) {
            instance = new AdminApp();
        }
        return instance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminApp adminApp = AdminApp.getInstance();
            adminApp.setVisible(true);
        });
    }

    // Custom cell renderer to wrap text in the Problem column
    static class TextAreaRenderer extends JTextArea implements TableCellRenderer {
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            if (table.getRowHeight(row) != getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
    }
}
