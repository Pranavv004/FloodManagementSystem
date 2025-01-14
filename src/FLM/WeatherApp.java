package FLM;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApp {

    private static final String API_KEY = "be84c7c4a55f007640172627979fffc6"; // Replace with your OpenWeatherMap API key
    private static final String[] MAIN_CITIES = {"Thiruvananthapuram", "Kollam", "Kochi", "Kozhikode"};

    private static FloodManagementApp floodManagementApp; // Reference to the FloodManagementApp instance

    // Method to set the instance of FloodManagementApp
    public static void setFloodManagementApp(FloodManagementApp app) {
        floodManagementApp = app;
    }

    public static void displayWeather(String cityName, JTextArea weatherTextArea, JLabel weatherLabel) throws Exception {
        // Update weatherLabel to indicate fetching weather
        weatherLabel.setText("Fetching weather for " + cityName + "...");

        // Create a URL for the API request using the city name
        String apiUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" + cityName + "&appid=" + API_KEY;

        // Send an HTTP GET request to the API
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Check for valid response code from the server
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            weatherTextArea.append("Failed to fetch weather for " + cityName+ "\n");
            weatherLabel.setText("Failed to fetch weather for " + cityName);
            return;
        }

        // Read and process the API response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Parse the JSON response
        JSONObject forecastData = new JSONObject(response.toString());

        JSONArray forecastList = forecastData.getJSONArray("list");

        // Display the forecast data to the user
        weatherTextArea.append("\nWeather forecast for " + cityName + ":\n");
        for (int i = 0; i < 5 && i < forecastList.length(); i++) { // Limit to 5 entries for brevity
            JSONObject forecast = forecastList.getJSONObject(i);
            String dateTime = forecast.getString("dt_txt");
            JSONObject main = forecast.getJSONObject("main");
            double temperature = main.getDouble("temp") - 273.15; // Convert from Kelvin to Celsius
            String weatherDescription = forecast.getJSONArray("weather").getJSONObject(0).getString("description");
            
            weatherTextArea.append("DateTime: " + dateTime + ", Temperature: " + String.format("%.2f", temperature) + "°C, Weather: " + weatherDescription + "\n");

            // Update FloodManagementApp with weather data
            if (floodManagementApp != null) {
                floodManagementApp.updateFloodStatus(cityName, temperature);
            }

            // Determine if it's a rainy or sunny day (example logic)
            if (weatherDescription.contains("rain")) {
                // Take appropriate action for rain
            } else if (weatherDescription.contains("sun")) {
                // Take appropriate action for sunny weather
            }
        }
        weatherTextArea.append("\n");

        // Reset weatherLabel after fetching weather
        weatherLabel.setText("Weather fetched for " + cityName);
    }

    public static void displayMainCitiesWeather(JTextArea weatherTextArea, JLabel weatherLabel) throws Exception {
        for (String city : MAIN_CITIES) {
            displayWeather(city, weatherTextArea, weatherLabel);
        }
    }

    public static JPanel createWeatherPanel() {
        // Setting up GUI components
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JTextArea weatherTextArea = new JTextArea(30, 43);
        weatherTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(weatherTextArea);

        JLabel weatherLabel = new JLabel("Fetching weather...");
        JLabel instructionLabel = new JLabel("Enter city name:");
        JTextField cityTextField = new JTextField(20);
        JButton fetchWeatherButton = new JButton("Fetch Weather");

        leftPanel.add(scrollPane);
        leftPanel.add(weatherLabel);
        leftPanel.add(instructionLabel);
        leftPanel.add(cityTextField);
        leftPanel.add(fetchWeatherButton);

        // Initial display of main cities weather
        SwingUtilities.invokeLater(() -> {
            try {
                displayMainCitiesWeather(weatherTextArea, weatherLabel);
            } catch (Exception e) {
                e.printStackTrace();
                weatherTextArea.setText("Error fetching weather data for main cities.");
            }
        });

        // Action listener for the button
        fetchWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cityName = cityTextField.getText().trim();
                if (!cityName.isEmpty()) {
                    try {
                        displayWeather(cityName, weatherTextArea, weatherLabel);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        weatherTextArea.append("Error fetching weather data for " + cityName + ".\n");
                        weatherLabel.setText("Error fetching weather for " + cityName);
                    }
                } else {
                    weatherTextArea.append("Please enter a city name.\n");
                    weatherLabel.setText("Please enter a city name.");
                }
            }
        });

        return leftPanel;
    }

    public static void main(String[] args) {
        // Start WeatherApp as a standalone application
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Weather App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(createWeatherPanel());
            frame.pack();
            frame.setVisible(true);
            
            // Assuming you have an instance of FloodManagementApp, you can set it here
            FloodManagementApp floodManagementApp = new FloodManagementApp();
            setFloodManagementApp(floodManagementApp);
        });
    }
}
