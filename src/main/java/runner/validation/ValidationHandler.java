package runner.validation;

/**
 * Abstract base class for validation handlers in the Chain of Responsibility pattern
 */
public abstract class ValidationHandler<T> {
    
    protected ValidationHandler<T> nextHandler;
    
    /**
     * Set the next handler in the chain
     * @param handler The next handler
     * @return This handler for method chaining
     */
    public ValidationHandler<T> setNext(ValidationHandler<T> handler) {
        this.nextHandler = handler;
        return handler;
    }
    
    /**
     * Handle validation request
     * @param context The validation context
     * @return ValidationResult indicating success or failure
     */
    public final ValidationResult handle(T context) {
        // Perform this handler's validation
        ValidationResult result = validate(context);
        
        // If validation failed, return immediately
        if (!result.isValid()) {
            return result;
        }
        
        // If there's a next handler, delegate to it
        if (nextHandler != null) {
            return nextHandler.handle(context);
        }
        
        // All validations passed
        return ValidationResult.success();
    }
    
    /**
     * Perform the specific validation logic for this handler
     * @param context The validation context
     * @return ValidationResult indicating success or failure
     */
    protected abstract ValidationResult validate(T context);
    
    /**
     * Get the name of this validation handler
     * @return Handler name
     */
    public String getHandlerName() {
        return getClass().getSimpleName();
    }
    
    /**
     * Get a description of what this handler validates
     * @return Handler description
     */
    public abstract String getDescription();
}