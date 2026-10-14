package com.tradeforge.singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DESIGN PATTERN 1: SINGLETON PATTERN
 * 
 * Responsibility:
 * Provides a single, globally accessible, and shared database connection manager
 * across the entire application using JDBC.
 * 
 * Key characteristics:
 * 1. Private static instance: eagerly initialized.
 * 2. Private constructor: prevents external instantiation with 'new'.
 * 3. Public static getInstance(): global access point.
 * 4. Reuses the shared JDBC connection safely, reconnecting if closed.
 * 5. Supports persistent configuration via tradeforge.properties or environment variables.
 */
public final class DatabaseConnection {

    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    private static final String CONFIG_FILE = "tradeforge.properties";

    private String url;
    private String username;
    private String password;

    private Connection connection;

    private DatabaseConnection() {
        loadConfiguration();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            System.err.println("Warning: MySQL JDBC Driver class not found: " + exception.getMessage());
        }
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    /**
     * Loads database credentials from classpath or tradeforge.properties if present,
     * otherwise falls back to environment variables, then defaults.
     */
    public synchronized void loadConfiguration() {
        Properties props = new Properties();

        // 1. Try loading from bundled classpath resource
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
        }

        // 2. Try loading from external file (overrides classpath)
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
            } catch (IOException ignored) {
            }
        }

        this.url = props.getProperty("db.url",
                System.getenv().getOrDefault("TRADEFORGE_DB_URL",
                        "jdbc:mysql://localhost:3306/tradeforge_db?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false"));
        this.username = props.getProperty("db.user",
                System.getenv().getOrDefault("TRADEFORGE_DB_USER", "root"));
        this.password = props.getProperty("db.password",
                System.getenv().getOrDefault("TRADEFORGE_DB_PASSWORD", "anshul"));
    }

    /**
     * Saves updated configuration to tradeforge.properties for persistence.
     */
    public synchronized void saveConfiguration(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        closeConnection();

        Properties props = new Properties();
        props.setProperty("db.url", url);
        props.setProperty("db.user", username);
        props.setProperty("db.password", password);

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "TradeForge Database Credentials");
        } catch (IOException e) {
            System.err.println("Warning: Could not save " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Retrieves the shared connection. Reconnects if closed.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    /**
     * Tests a specific set of credentials without changing the active state.
     */
    public static void testCredentials(String testUrl, String testUser, String testPass) throws SQLException {
        try (Connection testConn = DriverManager.getConnection(testUrl, testUser, testPass)) {
            // Success if no exception thrown
        }
    }

    public synchronized void setCredentials(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        closeConnection();
    }

    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            } finally {
                connection = null;
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
