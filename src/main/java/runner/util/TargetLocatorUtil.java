package runner.util;

import org.openqa.selenium.By;
import runner.model.Target;

/**
 * Utility class for converting Target objects to Selenium By locators.
 * This centralizes the target-to-locator conversion logic to avoid duplication.
 */
public final class TargetLocatorUtil {
    
    private TargetLocatorUtil() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Converts a Target object to a Selenium By locator.
     * 
     * @param target The target object containing locator information
     * @return Selenium By locator
     * @throws IllegalArgumentException if target is null or has invalid locator type
     */
    public static By getBy(Target target) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        
        String locatorType = target.getBy();
        String locatorValue = target.getValue();
        
        if (locatorType == null || locatorType.trim().isEmpty()) {
            throw new IllegalArgumentException("Target locator type cannot be null or empty");
        }
        
        if (locatorValue == null || locatorValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Target locator value cannot be null or empty");
        }
        
        return switch (locatorType) {
            case "id" -> By.id(locatorValue);
            case "name" -> By.name(locatorValue);
            case "css" -> By.cssSelector(locatorValue);
            case "xpath" -> By.xpath(locatorValue);
            case "className" -> By.className(locatorValue);
            case "tagName" -> By.tagName(locatorValue);
            case "linkText" -> By.linkText(locatorValue);
            case "partialLinkText" -> By.partialLinkText(locatorValue);
            default -> throw new IllegalArgumentException(
                "Unknown locator type: '" + locatorType + "'. " +
                "Supported types: id, name, css, xpath, className, tagName, linkText, partialLinkText"
            );
        };
    }
    
    /**
     * Validates that a target is not null and has valid locator information.
     * 
     * @param target The target to validate
     * @throws IllegalArgumentException if target is invalid
     */
    public static void validateTarget(Target target) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        
        if (target.getBy() == null || target.getBy().trim().isEmpty()) {
            throw new IllegalArgumentException("Target locator type cannot be null or empty");
        }
        
        if (target.getValue() == null || target.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Target locator value cannot be null or empty");
        }
    }
}