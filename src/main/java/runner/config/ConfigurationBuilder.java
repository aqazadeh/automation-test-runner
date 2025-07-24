package runner.config;

import runner.util.WebDriverFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for creating test configuration with runtime overrides
 */
public class ConfigurationBuilder {
    
    private String environment;
    private final Map<String, String> overrides;
    
    private ConfigurationBuilder() {
        this.overrides = new HashMap<>();
    }
    
    public static ConfigurationBuilder create() {
        return new ConfigurationBuilder();
    }
    
    public static ConfigurationBuilder forEnvironment(String environment) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.environment = environment;
        return builder;
    }
    
    // Browser Configuration Overrides
    public ConfigurationBuilder browser(WebDriverFactory.BrowserType browserType) {
        overrides.put("browser", browserType.name());
        return this;
    }
    
    public ConfigurationBuilder headless(boolean headless) {
        overrides.put("headless", String.valueOf(headless));
        return this;
    }
    
    public ConfigurationBuilder maximizeWindow(boolean maximize) {
        overrides.put("maximize.window", String.valueOf(maximize));
        return this;
    }
    
    public ConfigurationBuilder downloadDirectory(String directory) {
        overrides.put("download.directory", directory);
        return this;
    }
    
    // Timeout Configuration Overrides
    public ConfigurationBuilder implicitWait(int seconds) {
        overrides.put("implicit.wait.seconds", String.valueOf(seconds));
        return this;
    }
    
    public ConfigurationBuilder explicitWait(int seconds) {
        overrides.put("explicit.wait.seconds", String.valueOf(seconds));
        return this;
    }
    
    public ConfigurationBuilder pageLoadTimeout(int seconds) {
        overrides.put("page.load.timeout.seconds", String.valueOf(seconds));
        return this;
    }
    
    public ConfigurationBuilder scriptTimeout(int seconds) {
        overrides.put("script.timeout.seconds", String.valueOf(seconds));
        return this;
    }
    
    // Application Configuration Overrides
    public ConfigurationBuilder baseUrl(String url) {
        overrides.put("base.url", url);
        return this;
    }
    
    public ConfigurationBuilder apiBaseUrl(String url) {
        overrides.put("api.base.url", url);
        return this;
    }
    
    // Test Data Overrides
    public ConfigurationBuilder testUser(String user) {
        overrides.put("test.user", user);
        return this;
    }
    
    public ConfigurationBuilder testPassword(String password) {
        overrides.put("test.password", password);
        return this;
    }
    
    public ConfigurationBuilder testDataDir(String directory) {
        overrides.put("test.data.dir", directory);
        return this;
    }
    
    // Output Configuration Overrides
    public ConfigurationBuilder screenshotDirectory(String directory) {
        overrides.put("screenshot.directory", directory);
        return this;
    }
    
    public ConfigurationBuilder reportsDirectory(String directory) {
        overrides.put("reports.directory", directory);
        return this;
    }
    
    public ConfigurationBuilder screenshotOnFailure(boolean enabled) {
        overrides.put("screenshot.on.failure", String.valueOf(enabled));
        return this;
    }
    
    public ConfigurationBuilder videoRecording(boolean enabled) {
        overrides.put("video.recording", String.valueOf(enabled));
        return this;
    }
    
    // Database Configuration Overrides
    public ConfigurationBuilder databaseUrl(String url) {
        overrides.put("database.url", url);
        return this;
    }
    
    public ConfigurationBuilder databaseCredentials(String username, String password) {
        overrides.put("database.username", username);
        overrides.put("database.password", password);
        return this;
    }
    
    // Execution Configuration Overrides
    public ConfigurationBuilder retryCount(int count) {
        overrides.put("retry.count", String.valueOf(count));
        return this;
    }
    
    public ConfigurationBuilder threadCount(int count) {
        overrides.put("thread.count", String.valueOf(count));
        return this;
    }
    
    // Generic property override
    public ConfigurationBuilder property(String key, String value) {
        overrides.put(key, value);
        return this;
    }
    
    public ConfigurationBuilder property(String key, boolean value) {
        overrides.put(key, String.valueOf(value));
        return this;
    }
    
    public ConfigurationBuilder property(String key, int value) {
        overrides.put(key, String.valueOf(value));
        return this;
    }
    
    // Build the configuration
    public TestConfiguration build() {
        // Apply system property overrides before building
        applySystemPropertyOverrides();
        
        // Create configuration with environment
        TestConfiguration config = environment != null ? 
            TestConfiguration.getInstance(environment) : 
            TestConfiguration.getInstance();
        
        return config;
    }
    
    private void applySystemPropertyOverrides() {
        // Set system properties with "test." prefix for TestConfiguration to pick up
        overrides.forEach((key, value) -> {
            System.setProperty("test." + key, value);
        });
    }
    
    // Utility methods for common configurations
    public static TestConfiguration developmentConfig() {
        return ConfigurationBuilder.forEnvironment("dev")
                .browser(WebDriverFactory.BrowserType.CHROME)
                .headless(false)
                .maximizeWindow(true)
                .implicitWait(15)
                .retryCount(1)
                .build();
    }
    
    public static TestConfiguration testingConfig() {
        return ConfigurationBuilder.forEnvironment("test")
                .browser(WebDriverFactory.BrowserType.CHROME)
                .headless(true)
                .threadCount(2)
                .retryCount(2)
                .screenshotOnFailure(true)
                .videoRecording(true)
                .build();
    }
    
    public static TestConfiguration ciConfig() {
        return ConfigurationBuilder.forEnvironment("ci")
                .browser(WebDriverFactory.BrowserType.CHROME)
                .headless(true)
                .threadCount(3)
                .retryCount(2)
                .screenshotOnFailure(true)
                .videoRecording(true)
                .build();
    }
    
    public static TestConfiguration productionConfig() {
        return ConfigurationBuilder.forEnvironment("prod")
                .browser(WebDriverFactory.BrowserType.CHROME)
                .headless(true)
                .threadCount(1)
                .retryCount(3)
                .screenshotOnFailure(true)
                .videoRecording(false)
                .build();
    }
    
    // Debug configuration for troubleshooting
    public static TestConfiguration debugConfig() {
        return ConfigurationBuilder.forEnvironment("dev")
                .browser(WebDriverFactory.BrowserType.CHROME)
                .headless(false)
                .maximizeWindow(true)
                .implicitWait(30)
                .explicitWait(30)
                .pageLoadTimeout(60)
                .retryCount(0)
                .screenshotOnFailure(true)
                .property("debug.mode", true)
                .property("slow.execution", true)
                .build();
    }
    
    // Mobile configuration
    public static TestConfiguration mobileConfig() {
        return ConfigurationBuilder.forEnvironment("test")
                .browser(WebDriverFactory.BrowserType.CHROME)
                .headless(true)
                .maximizeWindow(false)
                .property("mobile.emulation", true)
                .property("device.name", "iPhone 12")
                .build();
    }
}