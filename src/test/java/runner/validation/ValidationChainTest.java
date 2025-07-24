package runner.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.WebDriver;
import runner.TestBase;
import runner.model.Target;
import runner.model.step.TestStep;
import runner.util.MockWebDriverFactory;
import runner.util.TestDataFactory;
import runner.validation.impl.TargetValidationHandler;
import runner.validation.impl.TestStepValidationHandler;
import runner.validation.impl.WebDriverValidationHandler;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validation Chain Tests")
class ValidationChainTest extends TestBase {
    
    private ValidationHandler<WebDriver> webDriverChain;
    private ValidationHandler<TestStep> testStepChain;
    private ValidationHandler<Target> targetChain;
    
    @BeforeEach
    @Override
    protected void setUpTest() {
        webDriverChain = ValidationChainBuilder.createWebDriverChain();
        testStepChain = ValidationChainBuilder.createTestStepChain();
        targetChain = ValidationChainBuilder.createTargetChain();
    }
    
    @Test
    @DisplayName("WebDriver validation should pass for valid driver")
    void webDriverValidationShouldPassForValidDriver() {
        // Given
        WebDriver mockDriver = MockWebDriverFactory.createMockWebDriver();
        
        // When
        ValidationResult result = webDriverChain.handle(mockDriver);
        
        // Then
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }
    
    @Test
    @DisplayName("WebDriver validation should fail for null driver")
    void webDriverValidationShouldFailForNullDriver() {
        // When
        ValidationResult result = webDriverChain.handle(null);
        
        // Then
        assertTrue(result.isInvalid());
        assertTrue(result.hasErrors());
        assertEquals("WebDriver cannot be null", result.getFirstError());
    }
    
    @Test
    @DisplayName("WebDriver validation should fail for inactive driver")
    void webDriverValidationShouldFailForInactiveDriver() {
        // Given
        WebDriver failingDriver = MockWebDriverFactory.createFailingMockWebDriver();
        
        // When
        ValidationResult result = webDriverChain.handle(failingDriver);
        
        // Then
        assertTrue(result.isInvalid());
        assertTrue(result.hasErrors());
        assertTrue(result.getFirstError().contains("WebDriver appears to be inactive"));
    }
    
    @Test
    @DisplayName("TestStep validation should pass for valid step")
    void testStepValidationShouldPassForValidStep() {
        // Given
        TestStep validStep = TestDataFactory.createSimpleTestStep();
        
        // When
        ValidationResult result = testStepChain.handle(validStep);
        
        // Then
        assertTrue(result.isValid());
    }
    
    @Test
    @DisplayName("TestStep validation should fail for null step")
    void testStepValidationShouldFailForNullStep() {
        // When
        ValidationResult result = testStepChain.handle(null);
        
        // Then
        assertTrue(result.isInvalid());
        assertTrue(result.hasErrors());
        assertEquals("TestStep cannot be null", result.getFirstError());
    }
    
    @Test
    @DisplayName("TestStep validation should warn for unnamed step")
    void testStepValidationShouldWarnForUnnamedStep() {
        // Given
        TestStep unnamedStep = TestDataFactory.createStepsWithValidationIssues().get(1); // Empty name step
        
        // When
        ValidationResult result = testStepChain.handle(unnamedStep);
        
        // Then
        assertTrue(result.isValid());
        assertTrue(result.hasWarnings());
        assertTrue(result.getFirstWarning().contains("no name"));
    }
    
    @Test
    @DisplayName("Target validation should pass for valid targets")
    void targetValidationShouldPassForValidTargets() {
        // Given
        var validTargets = TestDataFactory.createVariousTargets();
        
        for (Target target : validTargets) {
            // When
            ValidationResult result = targetChain.handle(target);
            
            // Then
            assertTrue(result.isValid(), "Target should be valid: " + target);
        }
    }
    
    @Test
    @DisplayName("Target validation should fail for invalid targets")
    void targetValidationShouldFailForInvalidTargets() {
        // Given
        var invalidTargets = TestDataFactory.createInvalidTargets();
        
        for (Target target : invalidTargets) {
            // When
            ValidationResult result = targetChain.handle(target);
            
            // Then
            assertTrue(result.isInvalid(), "Target should be invalid: " + target);
            assertTrue(result.hasErrors());
        }
    }
    
    @Test
    @DisplayName("Target validation should provide specific error messages")
    void targetValidationShouldProvideSpecificErrorMessages() {
        // Given
        Target nullTarget = null;
        Target emptyLocatorType = new Target("", "value");
        Target invalidLocatorType = new Target("invalid", "value");
        Target emptyValue = new Target("id", "");
        
        // When & Then
        ValidationResult result1 = targetChain.handle(nullTarget);
        assertTrue(result1.getFirstError().contains("cannot be null"));
        
        ValidationResult result2 = targetChain.handle(emptyLocatorType);
        assertTrue(result2.getFirstError().contains("cannot be null or empty"));
        
        ValidationResult result3 = targetChain.handle(invalidLocatorType);
        assertTrue(result3.getFirstError().contains("Invalid locator type"));
        
        ValidationResult result4 = targetChain.handle(emptyValue);
        assertTrue(result4.getFirstError().contains("value cannot be null or empty"));
    }
    
    @Test
    @DisplayName("Should create comprehensive validation chain")
    void shouldCreateComprehensiveValidationChain() {
        // Given
        ValidationHandler<TestStep> comprehensiveChain = ValidationChainBuilder.createComprehensiveTestStepChain();
        TestStep validStep = TestDataFactory.createSimpleTestStep();
        
        // When
        ValidationResult result = comprehensiveChain.handle(validStep);
        
        // Then
        assertTrue(result.isValid());
        assertNotNull(comprehensiveChain);
    }
    
    @Test
    @DisplayName("Should create environment-specific validation chains")
    void shouldCreateEnvironmentSpecificValidationChains() {
        // Given
        ValidationHandler<TestStep> devChain = ValidationChainBuilder.createForEnvironment("dev");
        ValidationHandler<TestStep> prodChain = ValidationChainBuilder.createForEnvironment("prod");
        
        TestStep validStep = TestDataFactory.createSimpleTestStep();
        
        // When
        ValidationResult devResult = devChain.handle(validStep);
        ValidationResult prodResult = prodChain.handle(validStep);
        
        // Then
        assertTrue(devResult.isValid());
        assertTrue(prodResult.isValid());
        assertNotNull(devChain);
        assertNotNull(prodChain);
    }
    
    @Test
    @DisplayName("Should create performance-aware validation chains")
    void shouldCreatePerformanceAwareValidationChains() {
        // Given
        ValidationHandler<TestStep> highPerfChain = ValidationChainBuilder.createForPerformance(true);
        ValidationHandler<TestStep> standardChain = ValidationChainBuilder.createForPerformance(false);
        
        TestStep validStep = TestDataFactory.createSimpleTestStep();
        
        // When
        ValidationResult highPerfResult = highPerfChain.handle(validStep);
        ValidationResult standardResult = standardChain.handle(validStep);
        
        // Then
        assertTrue(highPerfResult.isValid());
        assertTrue(standardResult.isValid());
        assertNotNull(highPerfChain);
        assertNotNull(standardChain);
    }
    
    @Test
    @DisplayName("Should support fluent chain building")
    void shouldSupportFluentChainBuilding() {
        // Given & When
        ValidationHandler<TestStep> customChain = ValidationChainBuilder.<TestStep>custom()
            .add(new TestStepValidationHandler())
            .build();
        
        TestStep validStep = TestDataFactory.createSimpleTestStep();
        ValidationResult result = customChain.handle(validStep);
        
        // Then
        assertNotNull(customChain);
        assertTrue(result.isValid());
    }
    
    @Test
    @DisplayName("Should handle empty fluent chain gracefully")
    void shouldHandleEmptyFluentChainGracefully() {
        // Given & When
        ValidationHandler<TestStep> emptyChain = ValidationChainBuilder.<TestStep>custom().build();
        
        // Then
        assertNull(emptyChain);
    }
}