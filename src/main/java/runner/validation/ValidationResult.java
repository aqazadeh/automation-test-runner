package runner.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a validation operation
 */
public class ValidationResult {
    
    private final boolean valid;
    private final List<String> errors;
    private final List<String> warnings;
    private final String handlerName;
    
    private ValidationResult(boolean valid, List<String> errors, List<String> warnings, String handlerName) {
        this.valid = valid;
        this.errors = new ArrayList<>(errors != null ? errors : List.of());
        this.warnings = new ArrayList<>(warnings != null ? warnings : List.of());
        this.handlerName = handlerName;
    }
    
    /**
     * Create a successful validation result
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null, null, null);
    }
    
    /**
     * Create a successful validation result with warnings
     */
    public static ValidationResult successWithWarnings(String... warnings) {
        return new ValidationResult(true, null, List.of(warnings), null);
    }
    
    /**
     * Create a successful validation result with warnings from a specific handler
     */
    public static ValidationResult successWithWarnings(String handlerName, String... warnings) {
        return new ValidationResult(true, null, List.of(warnings), handlerName);
    }
    
    /**
     * Create a failed validation result with a single error
     */
    public static ValidationResult failure(String error) {
        return new ValidationResult(false, List.of(error), null, null);
    }
    
    /**
     * Create a failed validation result with multiple errors
     */
    public static ValidationResult failure(String... errors) {
        return new ValidationResult(false, List.of(errors), null, null);
    }
    
    /**
     * Create a failed validation result with errors and warnings
     */
    public static ValidationResult failure(List<String> errors, List<String> warnings) {
        return new ValidationResult(false, errors, warnings, null);
    }
    
    /**
     * Create a failed validation result from a specific handler
     */
    public static ValidationResult failure(String handlerName, String error) {
        return new ValidationResult(false, List.of(error), null, handlerName);
    }
    
    /**
     * Create a failed validation result from a specific handler with multiple errors
     */
    public static ValidationResult failure(String handlerName, String... errors) {
        return new ValidationResult(false, List.of(errors), null, handlerName);
    }
    
    /**
     * Check if the validation was successful
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Check if the validation failed
     */
    public boolean isInvalid() {
        return !valid;
    }
    
    /**
     * Get all validation errors
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
    
    /**
     * Get all validation warnings
     */
    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
    
    /**
     * Get the name of the handler that produced this result
     */
    public String getHandlerName() {
        return handlerName;
    }
    
    /**
     * Check if there are any warnings
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * Check if there are any errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Get the first error message, or null if no errors
     */
    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }
    
    /**
     * Get the first warning message, or null if no warnings
     */
    public String getFirstWarning() {
        return warnings.isEmpty() ? null : warnings.get(0);
    }
    
    /**
     * Get all errors as a single string
     */
    public String getErrorsAsString() {
        return String.join("; ", errors);
    }
    
    /**
     * Get all warnings as a single string
     */
    public String getWarningsAsString() {
        return String.join("; ", warnings);
    }
    
    /**
     * Combine this result with another result
     */
    public ValidationResult combine(ValidationResult other) {
        if (other == null) {
            return this;
        }
        
        boolean combinedValid = this.valid && other.valid;
        List<String> combinedErrors = new ArrayList<>(this.errors);
        combinedErrors.addAll(other.errors);
        
        List<String> combinedWarnings = new ArrayList<>(this.warnings);
        combinedWarnings.addAll(other.warnings);
        
        String combinedHandlerName = this.handlerName != null ? this.handlerName : other.handlerName;
        
        return new ValidationResult(combinedValid, combinedErrors, combinedWarnings, combinedHandlerName);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationResult{valid=").append(valid);
        
        if (handlerName != null) {
            sb.append(", handler='").append(handlerName).append("'");
        }
        
        if (!errors.isEmpty()) {
            sb.append(", errors=[").append(String.join(", ", errors)).append("]");
        }
        
        if (!warnings.isEmpty()) {
            sb.append(", warnings=[").append(String.join(", ", warnings)).append("]");
        }
        
        sb.append("}");
        return sb.toString();
    }
}