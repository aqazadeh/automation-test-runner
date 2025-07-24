package runner.validation.impl;

import runner.model.step.TestStep;
import runner.validation.ValidationHandler;
import runner.validation.ValidationResult;

/**
 * Validation handler for TestStep instances
 */
public class TestStepValidationHandler extends ValidationHandler<TestStep> {
    
    @Override
    protected ValidationResult validate(TestStep step) {
        if (step == null) {
            return ValidationResult.failure(getHandlerName(), "TestStep cannot be null");
        }
        
        // Validate that step has an action
        if (step.getAction() == null) {
            return ValidationResult.failure(getHandlerName(), "TestStep action cannot be null");
        }
        
        // Try to get the executor to ensure it's valid
        try {
            step.getAction().executor();
        } catch (Exception e) {
            return ValidationResult.failure(getHandlerName(), 
                "Failed to get executor for TestStep action: " + e.getMessage());
        }
        
        // Check if step has a meaningful name
        if (step.getName() == null || step.getName().trim().isEmpty()) {
            return ValidationResult.successWithWarnings(getHandlerName(), 
                "TestStep has no name - consider adding a descriptive name for better reporting");
        }
        
        return ValidationResult.success();
    }
    
    @Override
    public String getDescription() {
        return "Validates that TestStep has a valid action and executor";
    }
}