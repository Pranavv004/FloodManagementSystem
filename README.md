# FloodMonitor: Advanced Flood Management Application

## Overview
FloodMonitor is an advanced flood management application designed to provide real-time monitoring, alerts, and safety tips to users. By utilizing weather data, river levels, and predictive analytics, the app forecasts flood risks and notifies users with emergency resources and evacuation routes. FloodMonitor aims to enhance community preparedness and response to flood emergencies.

## Features
- **Real-Time Monitoring**: Access to live weather data and river levels.
- **Alerts and Notifications**: Users receive timely alerts about potential flood risks.
- **User  Problems Submission**: Users can report issues and seek assistance during emergencies.
- **Donation Options**: Users can contribute through clothing or cash donations.
- **Emergency Contacts**: Quick access to emergency contact options.
- **User  Registration and Login**: Secure user accounts for personalized experiences.

## Technologies Used
- **Java**: The application is built using Java Swing for the graphical user interface.
- **PostgreSQL**: The application uses PostgreSQL as the database to store user data, alerts, and notifications.
- **Networking**: Utilizes sockets for chat functionality and real-time communication.
- **Version**: postgresql-42.7.3.jar, json-20140107.jar, apache-tomcat-9.0.89-windows-x64

## Installation
1. **Clone the Repository**:

2. **Set Up PostgreSQL**:
   - Ensure you have PostgreSQL installed and running.
   - Create a database named `users`.
   - Run the SQL scripts provided in the `sql` directory to set up the necessary tables (`register_table`, `alert_table`, `dam_table`, `notification_table`, `userproblem_table`).

3. **Add PostgreSQL JDBC Driver**:
   - Download the PostgreSQL JDBC driver (e.g., `postgresql-42.7.3.jar`) and add it to your project's classpath.

4. **Run the Application**:
   - Compile and run the `FloodManagementApp` class.
   ```bash
   javac -cp .:postgresql-42.7.3.jar FloodManagementApp.java
   java -cp .:postgresql-42.7.3.jar FloodManagementApp
   ```

## Usage
- **Login/Register**: Users can create an account or log in to access personalized features.
- **View Alerts**: Users can view flood alerts and notifications in real-time.
- **Submit Problems**: Users can report issues they encounter during floods.
- **Make Donations**: Users can choose to donate clothing or cash to support flood relief efforts.
- **Emergency Contacts**: Quick access to emergency contact options for immediate assistance.

  
## Acknowledgments
- PostgreSQL for the database management system.
- Java Swing for the graphical user interface.
