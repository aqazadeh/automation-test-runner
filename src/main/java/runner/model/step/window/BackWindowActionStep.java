package runner.model.step.window;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class BackWindowActionStep extends TestStep {
    // No additional fields needed for browser back navigation
    
    public static BackWindowActionStep create() {
        return new BackWindowActionStep();
    }
}
