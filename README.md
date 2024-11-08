# ShareAcademia

ShareAcademia is a Java-based application designed for managing student academic details, including attendance and GPA calculations. This project includes a graphical user interface (GUI) built with Java Swing and connects to a MySQL database to store and retrieve student information.

## Features

- **Student Details Management**: View and manage details like ID, name, age, GPA, contact information, department, and address.
- **Attendance Tracking**: Manage attendance records for students.
- **GPA Calculator**: Calculate GPA based on user-provided grades and credits.

## Screenshots

- **Student Details**:
  ![Student Details](https://github.com/tanishpoddar/ShareAcademia/blob/main/screenshots/screenshot1.jpg)

- **GPA Calculator**:
  ![GPA Calculator](https://github.com/tanishpoddar/ShareAcademia/blob/main/screenshots/screenshot2.jpg)


## Prerequisites

- **Java Development Kit (JDK)**: Ensure that you have JDK 8 or above installed.
- **MySQL**: Install MySQL and set up the necessary database as outlined below.
- **MySQL Connector/J**: This JDBC driver is required for connecting Java to MySQL.

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/ShareAcademia.git
   cd ShareAcademia
   ```

2. **Database Setup**:
   - Execute the SQL commands in `SQL Code.txt` to create the database and tables required by the application.
   - Open MySQL and run:
     ```sql
     SOURCE path/to/SQL Code.txt;
     ```

3. **Configure Database Credentials**:
   - Update the MySQL credentials in `main.java` with your own:
     ```java
     private static final String USER = "your_mysql_username";
     private static final String PASSWORD = "your_mysql_password";
     ```

4. **Install MySQL Connector/J**:
   - Download [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) and add it to your project’s classpath.

5. **Compile and Run**:
   - Compile the Java files:
     ```bash
     javac main.java
     ```
   - Run the application:
     ```bash
     java main
     ```

## Project Structure

- `main.java`: The main application file containing the core logic and GUI setup.
- `SQL Code.txt`: SQL script for creating the required database and table (`studentDetailsDB` and `studentDetails` table).

## Dependencies

- `javax.swing`
- `java.awt`
- `java.sql`
- `mysql:mysql-connector-java:8.0.33` (or compatible version)

## Future Enhancements

- **Real-Time Alerts**: Add notifications for students with low attendance.
- **Improved GPA Calculation**: Extend support for different grading scales.
- **Web Interface**: Add a web version of ShareAcademia using Java Spring.

## Contributing

If you’d like to contribute, please fork the repository and make changes as you’d like. Pull requests are welcome!  
