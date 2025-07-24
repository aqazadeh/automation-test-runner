package runner.model.step.window;

import runner.model.step.TestStep;

/**
 * Action step for closing the current window or tab
 */
public class CloseWindowActionStep extends TestStep {
    // No additional fields needed
    
    public static CloseWindowActionStep create() {
        return new CloseWindowActionStep();
    }
}