package runner.util;

import runner.model.Target;
import runner.model.step.TestStep;
import runner.model.step.basic.ClickActionStep;
import runner.model.step.basic.NavigateActionStep;
import runner.model.step.basic.SendValueActionStep;
import runner.model.step.waiting.WaitActionStep;
import runner.model.step.waiting.WaitType;

import java.util.List;
import java.util.ArrayList;

/**
 * Factory for creating test data used in unit and integration tests
 */
public class TestDataFactory {
    
    /**
     * Create a simple target for testing
     */
    public static Target createSimpleTarget() {
        return Target.id("test-element");
    }
    
    /**
     * Create various targets for comprehensive testing
     */
    public static List<Target> createVariousTargets() {
        return List.of(
            Target.id("id-element"),
            Target.name("name-element"),
            Target.css("#css-element"),
            Target.xpath("//div[@id='xpath-element']"),
            Target.className("class-element"),
            Target.tagName("button"),
            Target.linkText("Click Here"),
            Target.partialLinkText("Click")
        );
    }
    
    /**
     * Create invalid targets for negative testing
     */
    public static List<Target> createInvalidTargets() {
        List<Target> targets = new ArrayList<>();
        
        // Null values
        targets.add(new Target(null, "value"));
        targets.add(new Target("id", null));
        targets.add(new Target("", "value"));
        targets.add(new Target("id", ""));
        
        // Invalid locator types
        targets.add(new Target("invalid-type", "value"));
        targets.add(new Target("badLocator", "value"));
        
        return targets;
    }
    
    /**
     * Create a simple test step
     */
    public static TestStep createSimpleTestStep() {
        return ClickActionStep.create()
            .name("Simple Click Step")
            .target(createSimpleTarget())
            .build();
    }
    
    /**
     * Create a list of basic test steps
     */
    public static List<TestStep> createBasicTestSteps() {
        List<TestStep> steps = new ArrayList<>();
        
        // Navigation step
        steps.add(NavigateActionStep.create()
            .name("Navigate to Test Page")
            .url("https://example.com/test")
            .build());
        
        // Input step
        steps.add(SendValueActionStep.create()
            .name("Enter Username")
            .target(Target.id("username"))
            .value("testuser")
            .build());
        
        // Click step
        steps.add(ClickActionStep.create()
            .name("Click Submit Button")
            .target(Target.id("submit-btn"))
            .build());
        
        // Wait step
        steps.add(WaitActionStep.create()
            .name("Wait for Page Load")
            .target(Target.id("result"))
            .waitType(WaitType.VISIBLE)
            .timeout(10)
            .build());
        
        return steps;
    }
    
    /**
     * Create a complex scenario with multiple steps
     */
    public static List<TestStep> createComplexTestScenario() {
        List<TestStep> steps = new ArrayList<>();
        
        // Login flow
        steps.add(NavigateActionStep.create()
            .name("Navigate to Login Page")
            .url("https://example.com/login")
            .build());
        
        steps.add(SendValueActionStep.create()
            .name("Enter Username")
            .target(Target.id("username"))
            .value("testuser")
            .build());
        
        steps.add(SendValueActionStep.create()
            .name("Enter Password")
            .target(Target.id("password"))
            .value("testpass")
            .build());
        
        steps.add(ClickActionStep.create()
            .name("Click Login Button")
            .target(Target.css("button[type='submit']"))
            .build());
        
        steps.add(WaitActionStep.create()
            .name("Wait for Dashboard")
            .target(Target.className("dashboard"))
            .waitType(WaitType.PRESENT)
            .timeout(15)
            .build());
        
        // Dashboard actions
        steps.add(ClickActionStep.create()
            .name("Click Profile Menu")
            .target(Target.id("profile-menu"))
            .build());
        
        steps.add(ClickActionStep.create()
            .name("Click Settings")
            .target(Target.linkText("Settings"))
            .build());
        
        steps.add(WaitActionStep.create()
            .name("Wait for Settings Page")
            .target(Target.xpath("//h1[text()='Settings']"))
            .waitType(WaitType.VISIBLE)
            .timeout(10)
            .build());
        
        return steps;
    }
    
    /**
     * Create test steps that will fail for error testing
     */
    public static List<TestStep> createFailingTestSteps() {
        List<TestStep> steps = new ArrayList<>();
        
        // Step with invalid target
        steps.add(ClickActionStep.create()
            .name("Click Non-existent Element")
            .target(Target.id("non-existent-element"))
            .build());
        
        // Step with malformed XPath
        steps.add(ClickActionStep.create()
            .name("Click with Bad XPath")
            .target(Target.xpath("//div[unclosed"))
            .build());
        
        return steps;
    }
    
    /**
     * Create test steps with various validation issues
     */
    public static List<TestStep> createStepsWithValidationIssues() {
        List<TestStep> steps = new ArrayList<>();
        
        // Step with null action (will fail validation)
        TestStep invalidStep = new TestStep() {
            @Override
            public String getName() { return "Invalid Step"; }
            
            @Override
            public String toString() { return "InvalidStep"; }
        };
        steps.add(invalidStep);
        
        // Step with empty name
        steps.add(ClickActionStep.create()
            .name("")
            .target(createSimpleTarget())
            .build());
        
        // Step with null target
        steps.add(ClickActionStep.create()
            .name("Click with Null Target")
            .target(null)
            .build());
        
        return steps;
    }
    
    /**
     * Create a large number of test steps for performance testing
     */
    public static List<TestStep> createLargeTestScenario(int stepCount) {
        List<TestStep> steps = new ArrayList<>();
        
        for (int i = 0; i < stepCount; i++) {
            steps.add(ClickActionStep.create()
                .name("Step " + (i + 1))
                .target(Target.id("element-" + i))
                .build());
        }
        
        return steps;
    }
    
    /**
     * Create test steps for specific action types
     */
    public static List<TestStep> createStepsForActionType(Class<? extends TestStep> actionType, int count) {
        List<TestStep> steps = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            if (actionType == ClickActionStep.class) {
                steps.add(ClickActionStep.create()
                    .name("Click Step " + (i + 1))
                    .target(Target.id("click-element-" + i))
                    .build());
            } else if (actionType == NavigateActionStep.class) {
                steps.add(NavigateActionStep.create()
                    .name("Navigate Step " + (i + 1))
                    .url("https://example.com/page" + i)
                    .build());
            } else if (actionType == SendValueActionStep.class) {
                steps.add(SendValueActionStep.create()
                    .name("Input Step " + (i + 1))
                    .target(Target.id("input-element-" + i))
                    .value("test value " + i)
                    .build());
            }
        }
        
        return steps;
    }
}