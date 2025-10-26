package com.example.medicineordering.singleton;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Singleton class for application logging service
 * This demonstrates the Singleton pattern for centralized logging management
 */
@Component
public class LoggingService {
    
    private static LoggingService instance;
    private static final Object lock = new Object();
    
    private final ConcurrentLinkedQueue<LogEntry> logEntries;
    private final DateTimeFormatter formatter;
    private LogLevel minimumLogLevel;
    
    // Private constructor to prevent direct instantiation
    private LoggingService() {
        this.logEntries = new ConcurrentLinkedQueue<>();
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        this.minimumLogLevel = LogLevel.INFO;
        System.out.println("LoggingService singleton instance created");
    }
    
    /**
     * Gets the singleton instance of LoggingService
     * This demonstrates the Singleton pattern with thread-safe lazy initialization
     * @return the singleton instance
     */
    public static LoggingService getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new LoggingService();
                }
            }
        }
        return instance;
    }
    
    /**
     * Logs a message with the specified level
     * @param level the log level
     * @param message the message to log
     * @param source the source of the log (class/method name)
     */
    public void log(LogLevel level, String message, String source) {
        if (level.ordinal() >= minimumLogLevel.ordinal()) {
            LogEntry entry = new LogEntry(
                LocalDateTime.now(),
                level,
                message,
                source,
                Thread.currentThread().getName()
            );
            logEntries.offer(entry);
            
            // Print to console for immediate visibility
            String formattedMessage = String.format("[%s] [%s] [%s] [%s] %s",
                entry.getTimestamp().format(formatter),
                entry.getLevel(),
                entry.getSource(),
                entry.getThreadName(),
                entry.getMessage()
            );
            System.out.println(formattedMessage);
        }
    }
    
    /**
     * Logs an info message
     * @param message the message to log
     * @param source the source of the log
     */
    public void info(String message, String source) {
        log(LogLevel.INFO, message, source);
    }
    
    /**
     * Logs a warning message
     * @param message the message to log
     * @param source the source of the log
     */
    public void warn(String message, String source) {
        log(LogLevel.WARN, message, source);
    }
    
    /**
     * Logs an error message
     * @param message the message to log
     * @param source the source of the log
     */
    public void error(String message, String source) {
        log(LogLevel.ERROR, message, source);
    }
    
    /**
     * Logs a debug message
     * @param message the message to log
     * @param source the source of the log
     */
    public void debug(String message, String source) {
        log(LogLevel.DEBUG, message, source);
    }
    
    /**
     * Gets all log entries
     * @return list of all log entries
     */
    public List<LogEntry> getAllLogEntries() {
        return new ArrayList<>(logEntries);
    }
    
    /**
     * Gets log entries by level
     * @param level the log level to filter by
     * @return list of log entries with the specified level
     */
    public List<LogEntry> getLogEntriesByLevel(LogLevel level) {
        return logEntries.stream()
                .filter(entry -> entry.getLevel() == level)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Gets recent log entries
     * @param count the number of recent entries to return
     * @return list of recent log entries
     */
    public List<LogEntry> getRecentLogEntries(int count) {
        return logEntries.stream()
                .skip(Math.max(0, logEntries.size() - count))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Clears all log entries
     */
    public void clearLogs() {
        logEntries.clear();
        info("Log entries cleared", "LoggingService");
    }
    
    /**
     * Sets the minimum log level
     * @param level the minimum log level
     */
    public void setMinimumLogLevel(LogLevel level) {
        this.minimumLogLevel = level;
        info("Minimum log level set to: " + level, "LoggingService");
    }
    
    /**
     * Gets the current log count
     * @return the number of log entries
     */
    public int getLogCount() {
        return logEntries.size();
    }
    
    /**
     * Log levels enumeration
     */
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
    
    /**
     * Log entry class
     */
    public static class LogEntry {
        private final LocalDateTime timestamp;
        private final LogLevel level;
        private final String message;
        private final String source;
        private final String threadName;
        
        public LogEntry(LocalDateTime timestamp, LogLevel level, String message, String source, String threadName) {
            this.timestamp = timestamp;
            this.level = level;
            this.message = message;
            this.source = source;
            this.threadName = threadName;
        }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public LogLevel getLevel() { return level; }
        public String getMessage() { return message; }
        public String getSource() { return source; }
        public String getThreadName() { return threadName; }
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



