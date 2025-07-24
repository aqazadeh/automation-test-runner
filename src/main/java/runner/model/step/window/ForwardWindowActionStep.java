package runner.model.step.window;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class ForwardWindowActionStep extends TestStep {
    // No additional fields needed for browser forward navigation
    
    public static ForwardWindowActionStep create() {
        return new ForwardWindowActionStep();
    }
}
