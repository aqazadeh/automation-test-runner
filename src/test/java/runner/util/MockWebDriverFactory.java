package runner.util;

import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Factory for creating mock WebDriver instances for testing
 */
public class MockWebDriverFactory {
    
    /**
     * Create a basic mock WebDriver that returns reasonable defaults
     */
    public static WebDriver createMockWebDriver() {
        WebDriver mockDriver = Mockito.mock(WebDriver.class);
        
        // Set up basic behavior
        when(mockDriver.getCurrentUrl()).thenReturn("https://example.com/test");
        when(mockDriver.getTitle()).thenReturn("Test Page");
        when(mockDriver.getPageSource()).thenReturn("<html><body>Test Page</body></html>");
        
        return mockDriver;
    }
    
    /**
     * Create a mock WebDriver that can find elements
     */
    public static WebDriver createMockWebDriverWithElements() {
        WebDriver mockDriver = createMockWebDriver();
        
        // Create mock elements
        WebElement mockElement = Mockito.mock(WebElement.class);
        when(mockElement.isDisplayed()).thenReturn(true);
        when(mockElement.isEnabled()).thenReturn(true);
        when(mockElement.getText()).thenReturn("Mock Element Text");
        when(mockElement.getAttribute(any())).thenReturn("mock-value");
        when(mockElement.getTagName()).thenReturn("div");
        
        // Set up element finding
        when(mockDriver.findElement(any(By.class))).thenReturn(mockElement);
        when(mockDriver.findElements(any(By.class))).thenReturn(List.of(mockElement));
        
        return mockDriver;
    }
    
    /**
     * Create a mock WebDriver that throws exceptions
     */
    public static WebDriver createFailingMockWebDriver() {
        WebDriver mockDriver = Mockito.mock(WebDriver.class);
        
        when(mockDriver.getCurrentUrl()).thenThrow(new RuntimeException("Mock WebDriver failure"));
        when(mockDriver.findElement(any(By.class))).thenThrow(new org.openqa.selenium.NoSuchElementException("Mock element not found"));
        
        return mockDriver;
    }
    
    /**
     * Create a mock WebDriver for specific URL navigation testing
     */
    public static WebDriver createNavigationMockWebDriver() {
        WebDriver mockDriver = createMockWebDriver();
        WebDriver.Navigation mockNavigation = Mockito.mock(WebDriver.Navigation.class);
        
        when(mockDriver.navigate()).thenReturn(mockNavigation);
        
        return mockDriver;
    }
    
    /**
     * Create a mock WebDriver with window management capabilities
     */
    public static WebDriver createWindowMockWebDriver() {
        WebDriver mockDriver = createMockWebDriver();
        WebDriver.TargetLocator mockTargetLocator = Mockito.mock(WebDriver.TargetLocator.class);
        WebDriver.Options mockOptions = Mockito.mock(WebDriver.Options.class);
        WebDriver.Window mockWindow = Mockito.mock(WebDriver.Window.class);
        
        when(mockDriver.switchTo()).thenReturn(mockTargetLocator);
        when(mockDriver.manage()).thenReturn(mockOptions);
        when(mockOptions.window()).thenReturn(mockWindow);
        when(mockTargetLocator.window(any())).thenReturn(mockDriver);
        
        return mockDriver;
    }
    
    /**
     * Create a mock WebDriver that can be closed
     */
    public static WebDriver createCloseableMockWebDriver() {
        WebDriver mockDriver = createMockWebDriver();
        
        // Mock the quit method to do nothing (successful close)
        Mockito.doNothing().when(mockDriver).quit();
        Mockito.doNothing().when(mockDriver).close();
        
        return mockDriver;
    }
    
    /**
     * Create multiple mock WebDrivers for parallel testing
     */
    public static List<WebDriver> createMultipleMockWebDrivers(int count) {
        List<WebDriver> drivers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            WebDriver driver = createMockWebDriverWithElements();
            when(driver.getCurrentUrl()).thenReturn("https://example.com/test" + i);
            drivers.add(driver);
        }
        return drivers;
    }
}