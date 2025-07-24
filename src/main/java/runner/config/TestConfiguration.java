package runner.config;

import runner.util.WebDriverFactory;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestConfiguration {
    
    private static TestConfiguration instance;
    private final Properties properties;
    private final String environment;
    
    // Default configuration values
    private static final String DEFAULT_BROWSER = "CHROME";
    private static final boolean DEFAULT_HEADLESS = false;
    private static final int DEFAULT_IMPLICIT_WAIT = 10;
    private static final int DEFAULT_PAGE_LOAD_TIMEOUT = 30;
    private static final int DEFAULT_SCRIPT_TIMEOUT = 30;
    private static final int DEFAULT_EXPLICIT_WAIT = 10;
    private static final boolean DEFAULT_MAXIMIZE_WINDOW = true;
    private static final String DEFAULT_SCREENSHOT_DIR = "screenshots";
    private static final String DEFAULT_REPORTS_DIR = "reports";
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    
    private TestConfiguration(String environment) {
        this.environment = environment != null ? environment : "default";
        this.properties = loadProperties();
    }
    
    public static TestConfiguration getInstance() {
        if (instance == null) {
            synchronized (TestConfiguration.class) {
                if (instance == null) {
                    String env = System.getProperty("test.environment", 
                                System.getenv("TEST_ENVIRONMENT"));
                    instance = new TestConfiguration(env);
                }
            }
        }
        return instance;
    }
    
    public static TestConfiguration getInstance(String environment) {
        instance = new TestConfiguration(environment);
        return instance;
    }
    
    private Properties loadProperties() {
        Properties props = new Properties();
        
        // Load default properties first
        loadDefaultProperties(props);
        
        // Load environment-specific properties
        loadEnvironmentProperties(props, environment);
        
        // Override with system properties
        overrideWithSystemProperties(props);
        
        return props;
    }
    
    private void loadDefaultProperties(Properties props) {
        try (InputStream is = getClass().getResourceAsStream("/config/default.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load default.properties: " + e.getMessage());
        }
    }
    
    private void loadEnvironmentProperties(Properties props, String env) {
        String[] possibleFiles = {
            "/config/" + env + ".properties",
            "/config/environments/" + env + ".properties",
            "config/" + env + ".properties"
        };
        
        for (String file : possibleFiles) {
            try (InputStream is = getClass().getResourceAsStream(file)) {
                if (is != null) {
                    props.load(is);
                    System.out.println("Loaded configuration from: " + file);
                    return;
                }
            } catch (IOException e) {
                System.err.println("Warning: Could not load " + file + ": " + e.getMessage());
            }
        }
        
        // Try loading from file system
        Path configPath = Paths.get("config", env + ".properties");
        if (Files.exists(configPath)) {
            try (InputStream is = Files.newInputStream(configPath)) {
                props.load(is);
                System.out.println("Loaded configuration from file: " + configPath);
            } catch (IOException e) {
                System.err.println("Warning: Could not load " + configPath + ": " + e.getMessage());
            }
        }
    }
    
    private void overrideWithSystemProperties(Properties props) {
        // Override with system properties that start with "test."
        System.getProperties().forEach((key, value) -> {
            String keyStr = key.toString();
            if (keyStr.startsWith("test.")) {
                String configKey = keyStr.substring(5); // Remove "test." prefix
                props.setProperty(configKey, value.toString());
            }
        });
    }
    
    // Browser Configuration
    public WebDriverFactory.BrowserType getBrowserType() {
        String browser = getProperty("browser", DEFAULT_BROWSER).toUpperCase();
        try {
            return WebDriverFactory.BrowserType.valueOf(browser);
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Invalid browser type '" + browser + "', using default: " + DEFAULT_BROWSER);
            return WebDriverFactory.BrowserType.valueOf(DEFAULT_BROWSER);
        }
    }
    
    public boolean isHeadless() {
        return getBooleanProperty("headless", DEFAULT_HEADLESS);
    }
    
    public boolean isMaximizeWindow() {
        return getBooleanProperty("maximize.window", DEFAULT_MAXIMIZE_WINDOW);
    }
    
    public String getDownloadDirectory() {
        return getProperty("download.directory", null);
    }
    
    // Timeout Configuration
    public int getImplicitWaitSeconds() {
        return getIntProperty("implicit.wait.seconds", DEFAULT_IMPLICIT_WAIT);
    }
    
    public int getPageLoadTimeoutSeconds() {
        return getIntProperty("page.load.timeout.seconds", DEFAULT_PAGE_LOAD_TIMEOUT);
    }
    
    public int getScriptTimeoutSeconds() {
        return getIntProperty("script.timeout.seconds", DEFAULT_SCRIPT_TIMEOUT);
    }
    
    public int getExplicitWaitSeconds() {
        return getIntProperty("explicit.wait.seconds", DEFAULT_EXPLICIT_WAIT);
    }
    
    // Application Configuration
    public String getBaseUrl() {
        return getProperty("base.url", DEFAULT_BASE_URL);
    }
    
    public String getApiBaseUrl() {
        return getProperty("api.base.url", getBaseUrl() + "/api");
    }
    
    // Test Data Configuration
    public String getTestDataDir() {
        return getProperty("test.data.dir", "src/test/resources/testdata");
    }
    
    public String getTestUser() {
        return getProperty("test.user", "testuser");
    }
    
    public String getTestPassword() {
        return getProperty("test.password", "testpass");
    }
    
    // Output Configuration
    public String getScreenshotDirectory() {
        return getProperty("screenshot.directory", DEFAULT_SCREENSHOT_DIR);
    }
    
    public String getReportsDirectory() {
        return getProperty("reports.directory", DEFAULT_REPORTS_DIR);
    }
    
    public boolean isScreenshotOnFailure() {
        return getBooleanProperty("screenshot.on.failure", true);
    }
    
    public boolean isVideoRecording() {
        return getBooleanProperty("video.recording", false);
    }
    
    // Database Configuration (if needed)
    public String getDatabaseUrl() {
        return getProperty("database.url", null);
    }
    
    public String getDatabaseUsername() {
        return getProperty("database.username", null);
    }
    
    public String getDatabasePassword() {
        return getProperty("database.password", null);
    }
    
    // Retry Configuration
    public int getRetryCount() {
        return getIntProperty("retry.count", 0);
    }
    
    public boolean isRetryEnabled() {
        return getRetryCount() > 0;
    }
    
    // Parallel Execution Configuration
    public int getThreadCount() {
        return getIntProperty("thread.count", 1);
    }
    
    public boolean isParallelExecution() {
        return getThreadCount() > 1;
    }
    
    // Environment Information
    public String getEnvironment() {
        return environment;
    }
    
    public boolean isProduction() {
        return "prod".equalsIgnoreCase(environment) || "production".equalsIgnoreCase(environment);
    }
    
    public boolean isDevelopment() {
        return "dev".equalsIgnoreCase(environment) || "development".equalsIgnoreCase(environment);
    }
    
    public boolean isTesting() {
        return "test".equalsIgnoreCase(environment) || "testing".equalsIgnoreCase(environment);
    }
    
    // Generic property access methods
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Warning: Invalid integer value for " + key + ": " + value + ", using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    public long getLongProperty(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            System.err.println("Warning: Invalid long value for " + key + ": " + value + ", using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    public double getDoubleProperty(String key, double defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Warning: Invalid double value for " + key + ": " + value + ", using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    // Configuration validation
    public void validate() {
        System.out.println("=== Test Configuration ===");
        System.out.println("Environment: " + getEnvironment());
        System.out.println("Browser: " + getBrowserType());
        System.out.println("Headless: " + isHeadless());
        System.out.println("Base URL: " + getBaseUrl());
        System.out.println("Implicit Wait: " + getImplicitWaitSeconds() + "s");
        System.out.println("Explicit Wait: " + getExplicitWaitSeconds() + "s");
        System.out.println("Screenshot Directory: " + getScreenshotDirectory());
        System.out.println("Reports Directory: " + getReportsDirectory());
        
        if (isParallelExecution()) {
            System.out.println("Parallel Execution: " + getThreadCount() + " threads");
        }
        
        if (isRetryEnabled()) {
            System.out.println("Retry Count: " + getRetryCount());
        }
        
        System.out.println("========================");
    }
    
    // Create WebDriverFactory config from test configuration
    public WebDriverFactory.WebDriverConfig createWebDriverConfig() {
        return WebDriverFactory.WebDriverConfig.builder()
                .browserType(getBrowserType())
                .headless(isHeadless())
                .maximized(isMaximizeWindow())
                .implicitWaitSeconds(getImplicitWaitSeconds())
                .pageLoadTimeoutSeconds(getPageLoadTimeoutSeconds())
                .scriptTimeoutSeconds(getScriptTimeoutSeconds())
                .downloadDirectory(getDownloadDirectory())
                .acceptInsecureCertificates(!isProduction());
    }
    
    // Reset instance (useful for testing)
    public static void reset() {
        instance = null;
    }
}