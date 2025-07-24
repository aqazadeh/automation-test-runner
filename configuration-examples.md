# Configuration System Examples

## Basic Usage

### Using Default Configuration
```java
// Load default configuration (default.properties)
TestConfiguration config = TestConfiguration.getInstance();

// Create WebDriver using configuration
WebDriver driver = WebDriverFactory.createDriverFromConfig();
```

### Using Environment-Specific Configuration
```java
// Load environment-specific configuration
TestConfiguration config = TestConfiguration.getInstance("test");

// Create WebDriver for specific environment
WebDriver driver = WebDriverFactory.createDriverFromConfig("ci");
```

## Configuration Builder Examples

### Development Configuration
```java
TestConfiguration config = ConfigurationBuilder.developmentConfig();
// Uses dev.properties + Chrome headless=false, maximized=true, retries=1
```

### CI/CD Configuration
```java
TestConfiguration config = ConfigurationBuilder.ciConfig();
// Uses ci.properties + Chrome headless=true, 3 threads, 2 retries
```

### Debug Configuration
```java
TestConfiguration config = ConfigurationBuilder.debugConfig();
// Uses dev.properties + extended timeouts, screenshots on failure
```

### Custom Configuration
```java
TestConfiguration config = ConfigurationBuilder.forEnvironment("test")
    .browser(WebDriverFactory.BrowserType.FIREFOX)
    .headless(true)
    .implicitWait(20)
    .explicitWait(30)
    .baseUrl("https://staging.myapp.com")
    .screenshotOnFailure(true)
    .retryCount(3)
    .build();
```

## Runtime Property Overrides

### System Properties
```bash
# Override browser type
java -Dtest.browser=FIREFOX -jar test-runner.jar

# Override base URL
java -Dtest.base.url=https://localhost:8080 -jar test-runner.jar

# Override multiple properties
java -Dtest.browser=CHROME -Dtest.headless=true -Dtest.thread.count=2 -jar test-runner.jar
```

### Programmatic Overrides
```java
TestConfiguration config = ConfigurationBuilder.forEnvironment("dev")
    .property("custom.setting", "value")
    .property("debug.enabled", true)
    .property("timeout.multiplier", 2)
    .build();
```

## Environment Variables in Properties Files

### Using Environment Variables
```properties
# In any properties file
database.url=${DATABASE_URL}
test.password=${TEST_PASSWORD}
api.key=${API_KEY:default-key}  # with default value
```

### CI Environment Example
```bash
export DATABASE_URL=jdbc:postgresql://test-db:5432/testdb
export TEST_PASSWORD=ci-test-password
export API_KEY=ci-api-key-123
```

## Integration with Existing Classes

### WebDriver Creation
```java
// Using configuration-based factory methods
WebDriver driver = WebDriverFactory.createDriverFromConfig();
WebDriver driver = WebDriverFactory.createDriverFromConfig("prod");

// Traditional approach still works
WebDriver driver = WebDriverFactory.createDriver(BrowserType.CHROME);
```

### Wait Utilities
```java
// WebDriverWaitUtil automatically uses configuration timeouts
WebElement element = WebDriverWaitUtil.waitForElement(driver, target, WaitCondition.VISIBLE);

// ElementFinder uses configuration timeouts
ElementFinder finder = ElementFinder.using(driver);
Optional<WebElement> element = finder.findElementWithWait(target); // Uses config timeout
```

## Configuration Access in Tests

### Accessing Configuration Values
```java
TestConfiguration config = TestConfiguration.getInstance();

String baseUrl = config.getBaseUrl();
String apiUrl = config.getApiBaseUrl();
String testUser = config.getTestUser();
int retryCount = config.getRetryCount();
boolean screenshotOnFailure = config.isScreenshotOnFailure();
```

### Creating WebDriver Configuration
```java
TestConfiguration config = TestConfiguration.getInstance();
WebDriverFactory.WebDriverConfig webDriverConfig = config.createWebDriverConfig();
WebDriver driver = WebDriverFactory.createDriver(webDriverConfig);
```

## Testing Configuration

### Unit Test Example
```java
@Test
public void testConfigurationLoading() {
    TestConfiguration config = ConfigurationBuilder.forEnvironment("test")
        .browser(BrowserType.CHROME)
        .baseUrl("https://test.example.com")
        .build();
    
    assertEquals("https://test.example.com", config.getBaseUrl());
    assertEquals(BrowserType.CHROME, config.getBrowserType());
}
```

### Integration Test Example
```java
@Test
public void testWebDriverCreation() {
    TestConfiguration config = ConfigurationBuilder.testingConfig();
    WebDriver driver = WebDriverFactory.createDriver(config.createWebDriverConfig());
    
    assertNotNull(driver);
    // Test WebDriver functionality
    
    WebDriverFactory.quitDriver(driver);
}
```

## Best Practices

1. **Environment Separation**: Use different properties files for each environment
2. **Sensitive Data**: Use environment variables for passwords and API keys
3. **Default Values**: Provide sensible defaults in default.properties
4. **Configuration Validation**: The system automatically validates configuration values
5. **Centralized Access**: Use TestConfiguration.getInstance() for consistent access
6. **Builder Pattern**: Use ConfigurationBuilder for complex runtime configurations