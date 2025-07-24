package runner.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import runner.config.TestConfiguration;
import runner.model.Target;

import java.util.List;
import java.util.Optional;

/**
 * Example class demonstrating how to use the utility classes
 * This class is for documentation and testing purposes
 */
public class WebDriverUtilsExample {
    
    public static void demonstrateWebDriverFactory() {
        // Create driver using configuration
        WebDriver configDriver = WebDriverFactory.createDriverFromConfig();
        
        // Create different browser instances
        WebDriver chromeDriver = WebDriverFactory.createDriver(WebDriverFactory.BrowserType.CHROME);
        WebDriver firefoxDriver = WebDriverFactory.createDriver(WebDriverFactory.BrowserType.FIREFOX);
        
        // Create with custom configuration
        WebDriver customDriver = WebDriverFactory.createDriver(
            WebDriverFactory.WebDriverConfig.builder()
                .browserType(WebDriverFactory.BrowserType.CHROME)
                .headless(true)
                .implicitWaitSeconds(15)
                .pageLoadTimeoutSeconds(60)
        );
        
        // Clean up
        WebDriverFactory.quitDriver(configDriver);
        WebDriverFactory.quitDriver(chromeDriver);
        WebDriverFactory.quitDriver(firefoxDriver);
        WebDriverFactory.quitDriver(customDriver);
    }
    
    public static void demonstrateWebDriverWaitUtil(WebDriver driver) {
        Target submitButton = Target.id("submit-btn");
        Target loadingSpinner = Target.css(".loading-spinner");
        Target errorMessage = Target.className("error-message");
        
        // Wait for element to be present
        WebElement button = WebDriverWaitUtil.waitForElement(driver, submitButton, WebDriverWaitUtil.WaitCondition.PRESENT);
        
        // Wait for element to be clickable
        WebElement clickableButton = WebDriverWaitUtil.waitForElement(driver, submitButton, WebDriverWaitUtil.WaitCondition.CLICKABLE, 15);
        
        // Wait for element to disappear
        boolean spinnerGone = WebDriverWaitUtil.waitForInvisibility(driver, loadingSpinner, 30);
        
        // Wait for text to appear
        boolean errorAppeared = WebDriverWaitUtil.waitForTextPresent(driver, errorMessage, "Validation failed", 10);
        
        // Wait for page conditions
        boolean correctPage = WebDriverWaitUtil.waitForUrlContains(driver, "dashboard");
        boolean titleCorrect = WebDriverWaitUtil.waitForTitleContains(driver, "User Dashboard");
        
        // Wait for JavaScript condition
        boolean jsReady = WebDriverWaitUtil.waitForJavaScriptCondition(driver, "return jQuery.active == 0", 20);
        
        // Custom wait condition
        String userName = WebDriverWaitUtil.waitForCustomCondition(driver, 
            webDriver -> {
                Target userNameElement = Target.id("username");
                ElementFinder finder = ElementFinder.using(webDriver);
                return finder.getElementText(userNameElement);
            }, 10);
    }
    
    public static void demonstrateElementFinder(WebDriver driver) {
        ElementFinder finder = ElementFinder.using(driver);
        
        // Basic element finding
        Target loginForm = Target.id("login-form");
        Optional<WebElement> form = finder.findElement(loginForm);
        
        if (form.isPresent()) {
            System.out.println("Login form found");
        }
        
        // Find with wait
        Target submitButton = Target.css("button[type='submit']");
        WebElement button = finder.findElementWithWaitOrThrow(submitButton, 10);
        
        // Find multiple elements
        Target inputFields = Target.tagName("input");
        List<WebElement> inputs = finder.findElements(inputFields);
        System.out.println("Found " + inputs.size() + " input fields");
        
        // Find only visible elements
        List<WebElement> visibleInputs = finder.findVisibleElements(inputFields);
        System.out.println("Found " + visibleInputs.size() + " visible input fields");
        
        // Find by text content
        Target errorMessages = Target.className("error");
        Optional<WebElement> specificError = finder.findElementByText(errorMessages, "Invalid username");
        
        // Find by attribute
        Target requiredFields = Target.tagName("input");
        List<WebElement> requiredInputs = finder.findElementsByAttribute(requiredFields, "required", "true");
        
        // Work with select elements
        Target countrySelect = Target.id("country");
        Optional<Select> selectElement = finder.findSelectElement(countrySelect);
        
        if (selectElement.isPresent()) {
            Select select = selectElement.get();
            select.selectByVisibleText("United States");
        }
        
        // Table operations
        Target dataTable = Target.id("data-table");
        Optional<WebElement> cell = finder.findTableCell(dataTable, 0, 1); // First row, second column
        List<WebElement> firstRow = finder.findTableRow(dataTable, 0);
        
        // Element state checking
        boolean isVisible = finder.isElementVisible(submitButton);
        boolean isEnabled = finder.isElementEnabled(submitButton);
        boolean isClickable = finder.isElementClickable(submitButton);
        
        // Get element information
        String buttonText = finder.getElementText(submitButton);
        String buttonClass = finder.getElementAttribute(submitButton, "class");
        String inputValue = finder.getElementValue(Target.id("username"));
    }
    
    public static void demonstrateIntegratedUsage(WebDriver driver) {
        // Combined usage of all utilities
        ElementFinder finder = ElementFinder.using(driver);
        
        // Navigate to login page
        driver.get("https://example.com/login");
        
        // Wait for page to load
        WebDriverWaitUtil.waitForTitleContains(driver, "Login");
        
        // Find and fill username field
        Target usernameField = Target.id("username");
        WebElement username = finder.findElementWithWaitOrThrow(usernameField, 10);
        username.sendKeys("testuser");
        
        // Find and fill password field
        Target passwordField = Target.id("password");
        WebElement password = finder.findElementWithWaitOrThrow(passwordField, 10);
        password.sendKeys("testpass");
        
        // Wait for submit button to be clickable and click it
        Target submitButton = Target.css("button[type='submit']");
        WebElement button = WebDriverWaitUtil.waitForElement(driver, submitButton, WebDriverWaitUtil.WaitCondition.CLICKABLE);
        button.click();
        
        // Wait for dashboard page
        WebDriverWaitUtil.waitForUrlContains(driver, "dashboard", 15);
        
        // Verify user info is displayed
        Target userInfo = Target.className("user-info");
        boolean userInfoVisible = finder.isElementVisible(userInfo);
        String displayedName = finder.getElementText(Target.id("display-name"));
        
        System.out.println("Login successful: " + userInfoVisible);
        System.out.println("User name: " + displayedName);
    }
    
    // Example of creating a reusable test helper
    public static class LoginHelper {
        private final WebDriver driver;
        private final ElementFinder finder;
        
        public LoginHelper(WebDriver driver) {
            this.driver = driver;
            this.finder = ElementFinder.using(driver);
        }
        
        public void login(String username, String password) {
            // Navigate to login
            driver.get("https://example.com/login");
            
            // Wait for page load
            WebDriverWaitUtil.waitForElement(driver, Target.id("login-form"), WebDriverWaitUtil.WaitCondition.PRESENT);
            
            // Fill credentials
            finder.findElementWithWaitOrThrow(Target.id("username"), 10).sendKeys(username);
            finder.findElementWithWaitOrThrow(Target.id("password"), 10).sendKeys(password);
            
            // Submit
            WebElement submitButton = WebDriverWaitUtil.waitForElement(driver, 
                Target.css("button[type='submit']"), WebDriverWaitUtil.WaitCondition.CLICKABLE);
            submitButton.click();
            
            // Wait for redirect
            WebDriverWaitUtil.waitForUrlContains(driver, "dashboard", 15);
        }
        
        public boolean isLoggedIn() {
            return finder.isElementVisible(Target.className("user-info"));
        }
        
        public void logout() {
            if (isLoggedIn()) {
                finder.findElementWithWaitOrThrow(Target.id("logout-btn"), 5).click();
                WebDriverWaitUtil.waitForUrlContains(driver, "login", 10);
            }
        }
    }
}