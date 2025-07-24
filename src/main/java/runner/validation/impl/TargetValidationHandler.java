package runner.validation.impl;

import runner.model.Target;
import runner.validation.ValidationHandler;
import runner.validation.ValidationResult;

import java.util.Set;

/**
 * Validation handler for Target instances (element locators)
 */
public class TargetValidationHandler extends ValidationHandler<Target> {
    
    private static final Set<String> VALID_LOCATOR_TYPES = Set.of(
        "id", "name", "css", "xpath", "className", "tagName", "linkText", "partialLinkText"
    );
    
    @Override
    protected ValidationResult validate(Target target) {
        if (target == null) {
            return ValidationResult.failure(getHandlerName(), "Target cannot be null");
        }
        
        // Validate locator type
        String locatorType = target.getBy();
        if (locatorType == null || locatorType.trim().isEmpty()) {
            return ValidationResult.failure(getHandlerName(), "Target locator type cannot be null or empty");
        }
        
        if (!VALID_LOCATOR_TYPES.contains(locatorType)) {
            return ValidationResult.failure(getHandlerName(), 
                "Invalid locator type: '" + locatorType + "'. Valid types: " + VALID_LOCATOR_TYPES);
        }
        
        // Validate locator value
        String locatorValue = target.getValue();
        if (locatorValue == null || locatorValue.trim().isEmpty()) {
            return ValidationResult.failure(getHandlerName(), 
                "Target locator value cannot be null or empty for type: " + locatorType);
        }
        
        // Specific validations for different locator types
        ValidationResult specificValidation = validateSpecificLocatorType(locatorType, locatorValue);
        if (specificValidation.isInvalid()) {
            return specificValidation;
        }
        
        return specificValidation; // May contain warnings
    }
    
    private ValidationResult validateSpecificLocatorType(String locatorType, String locatorValue) {
        return switch (locatorType) {
            case "id" -> validateId(locatorValue);
            case "name" -> validateName(locatorValue);
            case "css" -> validateCss(locatorValue);
            case "xpath" -> validateXPath(locatorValue);
            case "className" -> validateClassName(locatorValue);
            case "tagName" -> validateTagName(locatorValue);
            case "linkText", "partialLinkText" -> validateLinkText(locatorValue);
            default -> ValidationResult.success(); // Already validated in main method
        };
    }
    
    private ValidationResult validateId(String id) {
        if (id.contains(" ")) {
            return ValidationResult.failure(getHandlerName(), "ID locator should not contain spaces: " + id);
        }
        
        if (id.startsWith("#")) {
            return ValidationResult.successWithWarnings(getHandlerName(), 
                "ID locator starts with '#' - this might be intended for CSS selector instead");
        }
        
        return ValidationResult.success();
    }
    
    private ValidationResult validateName(String name) {
        if (name.trim().length() == 0) {
            return ValidationResult.failure(getHandlerName(), "Name locator cannot be just whitespace");
        }
        
        return ValidationResult.success();
    }
    
    private ValidationResult validateCss(String css) {
        if (css.startsWith("//")) {
            return ValidationResult.failure(getHandlerName(), 
                "CSS selector starts with '//' - this looks like XPath syntax");
        }
        
        // Basic CSS validation - check for common mistakes
        if (css.contains("text()") || css.contains("contains(")) {
            return ValidationResult.failure(getHandlerName(), 
                "CSS selector contains XPath functions - use XPath locator type instead");
        }
        
        return ValidationResult.success();
    }
    
    private ValidationResult validateXPath(String xpath) {
        if (!xpath.startsWith("//") && !xpath.startsWith("/") && !xpath.startsWith("(")) {
            return ValidationResult.successWithWarnings(getHandlerName(), 
                "XPath does not start with '//' or '/' - this might not be a valid XPath expression");
        }
        
        if (xpath.contains("#") && !xpath.contains("[@id=")) {
            return ValidationResult.successWithWarnings(getHandlerName(), 
                "XPath contains '#' - this looks like CSS selector syntax");
        }
        
        return ValidationResult.success();
    }
    
    private ValidationResult validateClassName(String className) {
        if (className.contains(" ")) {
            return ValidationResult.failure(getHandlerName(), 
                "Class name locator should contain a single class name, not multiple: " + className);
        }
        
        if (className.startsWith(".")) {
            return ValidationResult.successWithWarnings(getHandlerName(), 
                "Class name starts with '.' - this might be intended for CSS selector instead");
        }
        
        return ValidationResult.success();
    }
    
    private ValidationResult validateTagName(String tagName) {
        if (tagName.contains(" ") || tagName.contains(".") || tagName.contains("#")) {
            return ValidationResult.failure(getHandlerName(), 
                "Tag name should be a simple HTML tag name: " + tagName);
        }
        
        if (!tagName.toLowerCase().equals(tagName)) {
            return ValidationResult.successWithWarnings(getHandlerName(), 
                "Tag name contains uppercase letters - HTML tag names are case-insensitive but lowercase is recommended");
        }
        
        return ValidationResult.success();
    }
    
    private ValidationResult validateLinkText(String linkText) {
        if (linkText.trim().isEmpty()) {
            return ValidationResult.failure(getHandlerName(), "Link text cannot be empty or just whitespace");
        }
        
        if (linkText.startsWith("//") || linkText.contains("[@")) {
            return ValidationResult.successWithWarnings(getHandlerName(), 
                "Link text looks like XPath - consider using XPath locator type instead");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public String getDescription() {
        return "Validates Target locator type and value for common issues and best practices";
    }
}