package runner.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.time.Duration;

public class WebDriverFactory {
    
    public enum BrowserType {
        CHROME,
        FIREFOX,
        EDGE,
        SAFARI
    }
    
    public static class WebDriverConfig {
        private BrowserType browserType = BrowserType.CHROME;
        private boolean headless = false;
        private boolean maximized = true;
        private int implicitWaitSeconds = 10;
        private int pageLoadTimeoutSeconds = 30;
        private int scriptTimeoutSeconds = 30;
        private String downloadDirectory;
        private boolean acceptInsecureCertificates = true;
        
        public static WebDriverConfig builder() {
            return new WebDriverConfig();
        }
        
        public WebDriverConfig browserType(BrowserType browserType) {
            this.browserType = browserType;
            return this;
        }
        
        public WebDriverConfig headless(boolean headless) {
            this.headless = headless;
            return this;
        }
        
        public WebDriverConfig maximized(boolean maximized) {
            this.maximized = maximized;
            return this;
        }
        
        public WebDriverConfig implicitWaitSeconds(int seconds) {
            this.implicitWaitSeconds = seconds;
            return this;
        }
        
        public WebDriverConfig pageLoadTimeoutSeconds(int seconds) {
            this.pageLoadTimeoutSeconds = seconds;
            return this;
        }
        
        public WebDriverConfig scriptTimeoutSeconds(int seconds) {
            this.scriptTimeoutSeconds = seconds;
            return this;
        }
        
        public WebDriverConfig downloadDirectory(String directory) {
            this.downloadDirectory = directory;
            return this;
        }
        
        public WebDriverConfig acceptInsecureCertificates(boolean accept) {
            this.acceptInsecureCertificates = accept;
            return this;
        }
        
        // Getters
        public BrowserType getBrowserType() { return browserType; }
        public boolean isHeadless() { return headless; }
        public boolean isMaximized() { return maximized; }
        public int getImplicitWaitSeconds() { return implicitWaitSeconds; }
        public int getPageLoadTimeoutSeconds() { return pageLoadTimeoutSeconds; }
        public int getScriptTimeoutSeconds() { return scriptTimeoutSeconds; }
        public String getDownloadDirectory() { return downloadDirectory; }
        public boolean isAcceptInsecureCertificates() { return acceptInsecureCertificates; }
    }
    
    public static WebDriver createDriver() {
        return createDriver(WebDriverConfig.builder());
    }
    
    public static WebDriver createDriver(BrowserType browserType) {
        return createDriver(WebDriverConfig.builder().browserType(browserType));
    }
    
    // Create driver from test configuration
    public static WebDriver createDriverFromConfig() {
        runner.config.TestConfiguration config = runner.config.TestConfiguration.getInstance();
        return createDriver(config.createWebDriverConfig());
    }
    
    public static WebDriver createDriverFromConfig(String environment) {
        runner.config.TestConfiguration config = runner.config.TestConfiguration.getInstance(environment);
        return createDriver(config.createWebDriverConfig());
    }
    
    public static WebDriver createDriver(WebDriverConfig config) {
        WebDriver driver = switch (config.getBrowserType()) {
            case CHROME -> createChromeDriver(config);
            case FIREFOX -> createFirefoxDriver(config);
            case EDGE -> createEdgeDriver(config);
            case SAFARI -> createSafariDriver(config);
        };
        
        configureDriver(driver, config);
        return driver;
    }
    
    private static WebDriver createChromeDriver(WebDriverConfig config) {
        ChromeOptions options = new ChromeOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless");
        }
        
        if (config.isMaximized()) {
            options.addArguments("--start-maximized");
        }
        
        if (config.getDownloadDirectory() != null) {
            options.addArguments("--download-directory=" + config.getDownloadDirectory());
        }
        
        if (config.isAcceptInsecureCertificates()) {
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--ignore-ssl-errors");
            options.addArguments("--allow-running-insecure-content");
        }
        
        // Common Chrome arguments for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        
        return new ChromeDriver(options);
    }
    
    private static WebDriver createFirefoxDriver(WebDriverConfig config) {
        FirefoxOptions options = new FirefoxOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless");
        }
        
        if (config.isAcceptInsecureCertificates()) {
            options.setAcceptInsecureCerts(true);
        }
        
        return new FirefoxDriver(options);
    }
    
    private static WebDriver createEdgeDriver(WebDriverConfig config) {
        EdgeOptions options = new EdgeOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless");
        }
        
        if (config.isMaximized()) {
            options.addArguments("--start-maximized");
        }
        
        if (config.isAcceptInsecureCertificates()) {
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--ignore-ssl-errors");
        }
        
        return new EdgeDriver(options);
    }
    
    private static WebDriver createSafariDriver(WebDriverConfig config) {
        SafariOptions options = new SafariOptions();
        
        if (config.isAcceptInsecureCertificates()) {
            options.setAcceptInsecureCerts(true);
        }
        
        return new SafariDriver(options);
    }
    
    private static void configureDriver(WebDriver driver, WebDriverConfig config) {
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWaitSeconds()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeoutSeconds()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(config.getScriptTimeoutSeconds()));
        
        // Maximize window if not done via options and not headless
        if (config.isMaximized() && !config.isHeadless() && 
            (config.getBrowserType() == BrowserType.FIREFOX || config.getBrowserType() == BrowserType.SAFARI)) {
            driver.manage().window().maximize();
        }
    }
    
    public static void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error quitting WebDriver: " + e.getMessage());
            }
        }
    }
}