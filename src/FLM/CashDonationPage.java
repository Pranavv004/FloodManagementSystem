package FLM;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class CashDonationPage extends JFrame {
    private JTextField donorNameField;
    private JTextField donorPhoneField;
    private JTextField donorAmountField;
    private JTextField upiIdField;
    private JTextField cardNumberField;
    private JPasswordField securityCodeField;
    private JTextField expiryDateField;
    private JPanel paymentDetailsPanel;
    private CardLayout cardLayout;
    private static int paymentCounter = 0; // Counter for payments
    private static final Random RANDOM = new Random();
    String url="jdbc:postgresql://localhost:5432/users";
    String user="postgres";
    String pass="123";
    boolean donate=false;

    private static int successfulPayments = 0; // Counter for successful payments

    public CashDonationPage() {
        setTitle("Cash Donation");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(173, 216, 230)); // Light blue color

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
        add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        donorPhoneField = new JTextField(20);
        add(donorPhoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        donorAmountField = new JTextField(20);
        add(donorAmountField, gbc);

        // Add radio buttons for payment method selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Payment Method:"), gbc);

        ButtonGroup paymentMethodGroup = new ButtonGroup();
        JRadioButton upiButton = new JRadioButton("UPI");
        JRadioButton creditCardButton = new JRadioButton("Credit Card");
        JRadioButton debitCardButton = new JRadioButton("Debit Card");

        paymentMethodGroup.add(upiButton);
        paymentMethodGroup.add(creditCardButton);
        paymentMethodGroup.add(debitCardButton);

        JPanel paymentMethodPanel = new JPanel(new GridLayout(3, 1));
        paymentMethodPanel.add(upiButton);
        paymentMethodPanel.add(creditCardButton);
        paymentMethodPanel.add(debitCardButton);

        gbc.gridx = 1;
        add(paymentMethodPanel, gbc);

        // Add a panel for payment details with CardLayout
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        paymentDetailsPanel = new JPanel(new CardLayout());
        add(paymentDetailsPanel, gbc);

        // UPI panel
        JPanel upiPanel = new JPanel(new GridBagLayout());
        GridBagConstraints upiGbc = new GridBagConstraints();
        upiGbc.insets = new Insets(5, 5, 5, 5);
        upiGbc.anchor = GridBagConstraints.WEST;

        upiGbc.gridx = 0;
        upiGbc.gridy = 0;
        upiPanel.add(new JLabel("UPI ID:"), upiGbc);
        upiGbc.gridx = 1;
        upiIdField = new JTextField(20);
        upiPanel.add(upiIdField, upiGbc);

        // Card panel (shared for both credit and debit card)
        JPanel cardPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(5, 5, 5, 5);
        cardGbc.anchor = GridBagConstraints.WEST;

        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardPanel.add(new JLabel("Card Number:"), cardGbc);
        cardGbc.gridx = 1;
        cardNumberField = new JTextField(20);
        cardPanel.add(cardNumberField, cardGbc);

        cardGbc.gridx = 0;
        cardGbc.gridy = 1;
        cardPanel.add(new JLabel("CVV Code:"), cardGbc);
        cardGbc.gridx = 1;
        securityCodeField = new JPasswordField(20);
        cardPanel.add(securityCodeField, cardGbc);

        cardGbc.gridx = 0;
        cardGbc.gridy = 2;
        cardPanel.add(new JLabel("Expiry Date (MM/YY):"), cardGbc);
        cardGbc.gridx = 1;
        expiryDateField = new JTextField(20);
        cardPanel.add(expiryDateField, cardGbc);

        // Add panels to CardLayout panel
        paymentDetailsPanel.add(upiPanel, "UPI");
        paymentDetailsPanel.add(cardPanel, "CARD");

        cardLayout = (CardLayout) paymentDetailsPanel.getLayout();

        // Add action listeners for radio buttons
        upiButton.addActionListener(e -> cardLayout.show(paymentDetailsPanel, "UPI"));
        creditCardButton.addActionListener(e -> cardLayout.show(paymentDetailsPanel, "CARD"));
        debitCardButton.addActionListener(e -> cardLayout.show(paymentDetailsPanel, "CARD"));

        // Default selection
        upiButton.setSelected(true);
        cardLayout.show(paymentDetailsPanel, "UPI");

        JButton submitDonationButton = new JButton("Submit");
        styleButton(submitDonationButton);
        submitDonationButton.addActionListener(e -> Donationcheck());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitDonationButton, gbc);
    }

    private void Donationcheck() {
        String name = donorNameField.getText();
        String phone = donorPhoneField.getText();
        String amount = donorAmountField.getText();
       
        if (name.isEmpty() || phone.isEmpty() || amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!name.matches("[a-zA-Z\\s]{3,30}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.length()>10) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Phone number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
        if (Integer.parseInt(amount)<1 || !amount.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isUPIMethodSelected()) {
            String upiId = upiIdField.getText();
            if (upiId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the UPI ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (upiId.length()<3 || upiId.length()>45 || !upiId.matches("^[a-zA-Z0-9.]+@[a-zA-Z]+$")) {// Updated UPI regex
                JOptionPane.showMessageDialog(this, "Please enter a valid UPI ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            submitdonation("upi");
            
        } else {
            String cardNumber = cardNumberField.getText();
            String securityCode = new String(securityCodeField.getPassword());
            String expiryDate = expiryDateField.getText();

            if (cardNumber.isEmpty() || securityCode.isEmpty() || expiryDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all the card details.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!cardNumber.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 16-digit card number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!securityCode.matches("\\d{3}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 3-digit CVV code.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidExpiryDate(expiryDate)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid expiry date in the format MM/yy.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            submitdonation("card");
        }
    }

    private boolean isUPIMethodSelected() {
        // Check if UPI radio button is selected
        for (Component component : paymentDetailsPanel.getComponents()) {
            if (component.isVisible() && component instanceof JPanel) {
                for (Component subComponent : ((JPanel) component).getComponents()) {
                    if (subComponent instanceof JTextField && subComponent == upiIdField) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isValidExpiryDate(String expiryDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(expiryDate);
            // Check if the date is not before the current date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return !date.before(calendar.getTime());
        } catch (ParseException e) {
            return false;
        }
    }

    private void sendPaymentToGPay(String amount, String paymentMethod, String name) {
        // Simulate sending payment details to GPay interface
        
    }

    private void sendPaymentToGPay(String amount, String paymentMethod, String name, String upiId) {
        // Simulate sending UPI payment details to GPay interface

    }

    private boolean processPayment(float amount, String paymentMethod, String name) {
	    paymentCounter++;
	    if(paymentCounter>5)
	    {
		    paymentCounter=0;
	    }
	    boolean isPaymentSuccessful = paymentCounter % 5 != RANDOM.nextInt(5);
	    if (isPaymentSuccessful) {
		    return true;
	    } else {
	        return false;
	    }
	}

	private boolean processPayment(float amount, String paymentMethod, String name, String upiId) throws IOException, URISyntaxException {
	    paymentCounter++;
	    if(paymentCounter>5)
	    {
		    paymentCounter=0;
	    }
	    boolean isPaymentSuccessful = paymentCounter % 5 != RANDOM.nextInt(5);
	    if (isPaymentSuccessful) {    
		                String url = "https://pay.google.com/gp/w/u/0/home/activity";
		                Desktop.getDesktop().browse(new URI(url));
		                return true;
	    } 
	    else {
	        return false;
	    }
	}
	void submitdonation(String method) {
	        String name = donorNameField.getText();
	        String ph = donorPhoneField.getText();
	        long phone = Long.parseLong(ph);
	        String am = donorAmountField.getText();
	        float amount = Float.parseFloat(am);
		 String query = "insert into donation_table values(?,?,?)";
		 boolean ok;
	         try {
	     		       if(method.equals("card")){
	     			       	ok=processPayment(amount, "CARD", name);
	     		       }
	     		       else {
		     			  String upiId = upiIdField.getText();
		     			  ok=processPayment(amount,"UPI", name, upiId);
	     		       }
	     		 if(ok==true) {
	     			 Connection c = DriverManager.getConnection(url, user, pass);
		        	 PreparedStatement result = c.prepareStatement(query);
		        	 result.setString(1, name);
		        	 result.setLong(2, phone);
		        	 result.setFloat(3, amount);
		        	 int check = result.executeUpdate();
		        	 if (check > 0) {
		        		 JOptionPane.showMessageDialog(this, "The payment is successful");
		        	 }
		        	 else {
		        		 JOptionPane.showMessageDialog(this, "The payment attempt is not successful", "Error", JOptionPane.ERROR_MESSAGE);
		        	 }
	     		}
	     		else {
		     		JOptionPane.showMessageDialog(this, "The payment attempt is not successful", "Error", JOptionPane.ERROR_MESSAGE);
		     	       }
	         }
	         catch (Exception e) {
	             System.out.println(e);
	         }
	}
    private void styleButton(JButton button) {
        button.setUI(new BasicButtonUI());
        button.setBackground(new Color(0, 102, 204)); // Blue color
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 51, 153)); // Darker blue on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 102, 204)); // Original blue color
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CashDonationPage().setVisible(true);
        });
    }
}
