package runner.validation;

import lombok.extern.slf4j.Slf4j;
import runner.model.Target;
import runner.model.step.TestStep;
import runner.validation.impl.TargetValidationHandler;
import runner.validation.impl.TestStepValidationHandler;
import runner.validation.impl.WebDriverValidationHandler;
import org.openqa.selenium.WebDriver;

/**
 * Builder for creating validation chains using the Chain of Responsibility pattern
 */
@Slf4j
public class ValidationChainBuilder {
    
    /**
     * Create a default validation chain for WebDriver
     */
    public static ValidationHandler<WebDriver> createWebDriverChain() {
        log.debug("Creating WebDriver validation chain");
        return new WebDriverValidationHandler();
    }
    
    /**
     * Create a default validation chain for TestStep
     */
    public static ValidationHandler<TestStep> createTestStepChain() {
        log.debug("Creating TestStep validation chain");
        return new TestStepValidationHandler();
    }
    
    /**
     * Create a default validation chain for Target
     */
    public static ValidationHandler<Target> createTargetChain() {
        log.debug("Creating Target validation chain");
        return new TargetValidationHandler();
    }
    
    /**
     * Create a comprehensive validation chain for TestStep with Target validation
     */
    public static ValidationHandler<TestStep> createComprehensiveTestStepChain() {
        log.debug("Creating comprehensive TestStep validation chain");
        
        ValidationHandler<TestStep> stepHandler = new TestStepValidationHandler();
        ValidationHandler<TestStep> targetHandler = new TestStepTargetValidationHandler();
        
        stepHandler.setNext(targetHandler);
        return stepHandler;
    }
    
    /**
     * Helper class to validate Target objects within TestStep context
     */
    private static class TestStepTargetValidationHandler extends ValidationHandler<TestStep> {
        
        private final TargetValidationHandler targetValidator = new TargetValidationHandler();
        
        @Override
        protected ValidationResult validate(TestStep step) {
            // For steps that have target-based actions, validate the target
            if (hasTarget(step)) {
                Target target = extractTarget(step);
                if (target != null) {
                    ValidationResult targetResult = targetValidator.handle(target);
                    if (targetResult.isInvalid()) {
                        return ValidationResult.failure(getHandlerName(), 
                            "Target validation failed: " + targetResult.getErrorsAsString());
                    }
                    if (targetResult.hasWarnings()) {
                        return ValidationResult.successWithWarnings(getHandlerName(), 
                            "Target validation warnings: " + targetResult.getWarningsAsString());
                    }
                }
            }
            
            return ValidationResult.success();
        }
        
        private boolean hasTarget(TestStep step) {
            // Check if step has methods that suggest it uses a Target
            try {
                step.getClass().getMethod("getTarget");
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        
        private Target extractTarget(TestStep step) {
            try {
                Object target = step.getClass().getMethod("getTarget").invoke(step);
                return target instanceof Target ? (Target) target : null;
            } catch (Exception e) {
                log.debug("Could not extract target from step: {}", e.getMessage());
                return null;
            }
        }
        
        @Override
        public String getDescription() {
            return "Validates Target objects within TestStep context";
        }
    }
    
    /**
     * Fluent builder for creating custom validation chains
     */
    public static class FluentChainBuilder<T> {
        private ValidationHandler<T> firstHandler;
        private ValidationHandler<T> lastHandler;
        
        public FluentChainBuilder<T> add(ValidationHandler<T> handler) {
            if (handler == null) {
                log.warn("Attempted to add null handler to validation chain");
                return this;
            }
            
            if (firstHandler == null) {
                firstHandler = handler;
                lastHandler = handler;
            } else {
                lastHandler.setNext(handler);
                lastHandler = handler;
            }
            
            log.debug("Added {} to validation chain", handler.getHandlerName());
            return this;
        }
        
        public ValidationHandler<T> build() {
            if (firstHandler == null) {
                log.warn("Building empty validation chain");
            } else {
                log.debug("Built validation chain with {} as first handler", firstHandler.getHandlerName());
            }
            return firstHandler;
        }
    }
    
    /**
     * Start building a custom validation chain
     */
    public static <T> FluentChainBuilder<T> custom() {
        return new FluentChainBuilder<>();
    }
    
    /**
     * Create a validation chain for a specific environment
     */
    public static ValidationHandler<TestStep> createForEnvironment(String environment) {
        log.debug("Creating validation chain for environment: {}", environment);
        
        FluentChainBuilder<TestStep> builder = new FluentChainBuilder<>();
        
        // Always include basic step validation
        builder.add(new TestStepValidationHandler());
        
        switch (environment != null ? environment.toLowerCase() : "default") {
            case "dev", "development" -> {
                // Development: comprehensive validation with all checks
                builder.add(new TestStepTargetValidationHandler());
            }
            case "test", "testing" -> {
                // Testing: standard validation
                builder.add(new TestStepTargetValidationHandler());
            }
            case "ci", "continuous-integration" -> {
                // CI: essential validation only (faster execution)
                // Only basic step validation
            }
            case "prod", "production" -> {
                // Production: minimal validation (performance critical)
                // Only basic step validation
            }
            default -> {
                // Default: comprehensive validation
                builder.add(new TestStepTargetValidationHandler());
            }
        }
        
        return builder.build();
    }
    
    /**
     * Create a validation chain with performance considerations
     */
    public static ValidationHandler<TestStep> createForPerformance(boolean highPerformance) {
        log.debug("Creating validation chain for performance requirements (high performance: {})", highPerformance);
        
        FluentChainBuilder<TestStep> builder = new FluentChainBuilder<>();
        
        // Always include basic validation
        builder.add(new TestStepValidationHandler());
        
        if (!highPerformance) {
            // Include additional validation if performance is not critical
            builder.add(new TestStepTargetValidationHandler());
        }
        
        return builder.build();
    }
}