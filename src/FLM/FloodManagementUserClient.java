package FLM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FloodManagementUserClient extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private String selectedDistrict;
    private JLabel districtLabel;
    private JPanel homePanel;

    public FloodManagementUserClient(String serverAddress, int serverPort) {
        setTitle("Flood Management User");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new CardLayout());

        try {
            this.serverAddress = InetAddress.getByName(serverAddress);
            this.serverPort = serverPort;
            socket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Initialize the home panel and chat panel
        initHomePanel();
        initChatPanel();

        // Start with the home panel
        showHomePanel();
    }

    private void initHomePanel() {
        homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Flood Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 20));
        homePanel.add(welcomeLabel, BorderLayout.CENTER);

        JButton selectDistrictButton = new JButton("Select District");
        selectDistrictButton.addActionListener(e -> promptForDistrict());
        homePanel.add(selectDistrictButton, BorderLayout.SOUTH);

        add(homePanel, "Home");
    }

    private void initChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Panel to display the selected district
        JPanel districtPanel = new JPanel(new BorderLayout());
        districtLabel = new JLabel();
        districtLabel.setHorizontalAlignment(SwingConstants.CENTER);
        districtLabel.setFont(new Font("Serif", Font.BOLD, 16));
        districtPanel.add(districtLabel, BorderLayout.NORTH);

        // Adding components to the chat panel
        chatPanel.add(districtPanel, BorderLayout.NORTH);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        add(chatPanel, "Chat");
    }

    private void promptForDistrict() {
        String[] districts = {"Thiruvananthapuram", "Kollam", "Pathanamthitta", "Alappuzha", "Kottayam", "Idukki", "Ernakulam", "Thrissur", "Palakkad", "Malappuram", "Kozhikode", "Wayanad", "Kannur", "Kasaragod"};
        selectedDistrict = (String) JOptionPane.showInputDialog(this, "Select District:", "District Selection",
                JOptionPane.QUESTION_MESSAGE, null, districts, districts[0]);

        if (selectedDistrict != null) {
            // Set the selected district in the label and switch to the chat panel
            districtLabel.setText("Selected District: " + selectedDistrict);
            showChatPanel();
            // Start the thread to listen for responses
            new Thread(this::listenForResponses).start();
        } else {
            // If "Cancel" is clicked, show the home panel
            showHomePanel();
        }
    }

    private void sendMessage() {
        try {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                chatArea.append("  You: " + message + "\n");
                // Include the district in the message sent to the server
                String fullMessage = selectedDistrict + "|" + message;
                byte[] buffer = fullMessage.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
                socket.send(packet);
                messageField.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForResponses() {
        try {
            byte[] buffer = new byte[1234];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                chatArea.append("  Admin: " + response + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showHomePanel() {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "Home");
    }

    private void showChatPanel() {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "Chat");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FloodManagementUserClient userClient = new FloodManagementUserClient("localhost", 9876);
                userClient.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
