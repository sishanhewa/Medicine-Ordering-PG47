package com.example.medicineordering.singleton;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Singleton class for application configuration
 * This demonstrates the Singleton pattern for managing application-wide configuration
 */
@Component
public class ApplicationConfig {
    
    private static ApplicationConfig instance;
    private static final Object lock = new Object();
    
    @Value("${spring.application.name:Medicine Ordering System}")
    private String applicationName;
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    @Value("${spring.datasource.url:jdbc:sqlserver://localhost:1433;databaseName=MedicineDB}")
    private String databaseUrl;
    
    @Value("${app.version:1.0.0}")
    private String applicationVersion;
    
    @Value("${app.environment:development}")
    private String environment;
    
    @Value("${app.max.file.size:10MB}")
    private String maxFileSize;
    
    @Value("${app.session.timeout:30}")
    private int sessionTimeoutMinutes;
    
    // Private constructor to prevent direct instantiation
    private ApplicationConfig() {
        System.out.println("ApplicationConfig singleton instance created");
    }
    
    /**
     * Gets the singleton instance of ApplicationConfig
     * This demonstrates the Singleton pattern with thread-safe lazy initialization
     * @return the singleton instance
     */
    public static ApplicationConfig getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ApplicationConfig();
                }
            }
        }
        return instance;
    }
    
    /**
     * Gets the application name
     * @return the application name
     */
    public String getApplicationName() {
        return applicationName;
    }
    
    /**
     * Gets the server port
     * @return the server port
     */
    public int getServerPort() {
        return serverPort;
    }
    
    /**
     * Gets the database URL
     * @return the database URL
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }
    
    /**
     * Gets the application version
     * @return the application version
     */
    public String getApplicationVersion() {
        return applicationVersion;
    }
    
    /**
     * Gets the environment
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }
    
    /**
     * Gets the maximum file size
     * @return the maximum file size
     */
    public String getMaxFileSize() {
        return maxFileSize;
    }
    
    /**
     * Gets the session timeout in minutes
     * @return the session timeout in minutes
     */
    public int getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }
    
    /**
     * Checks if the application is in production environment
     * @return true if in production environment
     */
    public boolean isProduction() {
        return "production".equalsIgnoreCase(environment);
    }
    
    /**
     * Checks if the application is in development environment
     * @return true if in development environment
     */
    public boolean isDevelopment() {
        return "development".equalsIgnoreCase(environment);
    }
    
    /**
     * Gets a formatted configuration summary
     * @return configuration summary string
     */
    public String getConfigurationSummary() {
        return String.format(
            "Application: %s v%s | Port: %d | Environment: %s | Database: %s | Max File Size: %s | Session Timeout: %d minutes",
            applicationName, applicationVersion, serverPort, environment, databaseUrl, maxFileSize, sessionTimeoutMinutes
        );
    }
    
    /**
     * Prevents cloning of the singleton instance
     * @return never returns (throws exception)
     * @throws CloneNotSupportedException always thrown
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Singleton cannot be cloned");
    }
}



