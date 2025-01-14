package FLM1;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class FloodManagementServer extends JFrame {
    private static final int PORT = 9876;
    private JTextArea logArea;
    private JTextField messageField;
    private JButton sendButton;
    private DatagramSocket socket;
    private InetAddress clientAddress;
    private int clientPort;
    private boolean running;
    private Set<String> receivedDistricts;

    public FloodManagementServer() {
        setTitle("Flood Management Server");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        receivedDistricts = new HashSet<>();
        startServer();
    }

    private void startServer() {
        if (running) return;

        running = true;
        new Thread(() -> {
            try {
                socket = new DatagramSocket(PORT);
                log("Server started");

                byte[] buffer = new byte[1234];
                while (running) {
                    DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(requestPacket);

                    String request = new String(requestPacket.getData(), 0, requestPacket.getLength());

                    clientAddress = requestPacket.getAddress();
                    clientPort = requestPacket.getPort();

                    // Parse the district and message from the request
                    String[] parts = request.split("\\|");
                    if (parts.length == 2) {
                        String district = parts[0];
                        String message = parts[1];
                        if (receivedDistricts.add(district)) {
                            log("\nLocation: " + district);
                            log("----------------------");
                        }
                        log("Client: " + message);
                    } else {
                        log("Invalid message format received: " + request);
                    }
                }
            } catch (Exception e) {
                log("Error: " + e.getMessage());
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                log("Server stopped.");
            }
        }).start();
    }

    private void sendMessage() {
        if (clientAddress == null || clientPort == 0) {
            log("No client connected.");
            return;
        }

        try {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(packet);
                log("Admin: " + message);
                messageField.setText("");
            }
        } catch (Exception e) {
            log("Error: " + e.getMessage());
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FloodManagementServer server = new FloodManagementServer();
            server.setVisible(true);
        });
    }
}
