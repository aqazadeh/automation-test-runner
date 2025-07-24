package runner.validation.impl;

import org.openqa.selenium.WebDriver;
import runner.validation.ValidationHandler;
import runner.validation.ValidationResult;

/**
 * Validation handler for WebDriver instances
 */
public class WebDriverValidationHandler extends ValidationHandler<WebDriver> {
    
    @Override
    protected ValidationResult validate(WebDriver driver) {
        if (driver == null) {
            return ValidationResult.failure(getHandlerName(), "WebDriver cannot be null");
        }
        
        try {
            // Test if the driver is still active by getting the current URL
            String currentUrl = driver.getCurrentUrl();
            
            // Check if we're on about:blank (might indicate driver just started)
            if ("about:blank".equals(currentUrl)) {
                return ValidationResult.successWithWarnings(getHandlerName(), 
                    "WebDriver is on about:blank page - may need navigation");
            }
            
            return ValidationResult.success();
            
        } catch (Exception e) {
            return ValidationResult.failure(getHandlerName(), 
                "WebDriver appears to be inactive or closed: " + e.getMessage());
        }
    }
    
    @Override
    public String getDescription() {
        return "Validates that WebDriver is not null and is active";
    }
}