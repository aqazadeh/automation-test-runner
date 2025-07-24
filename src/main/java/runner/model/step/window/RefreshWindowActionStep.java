package runner.model.step.window;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class RefreshWindowActionStep extends TestStep {
    // No additional fields needed for browser refresh
    
    public static RefreshWindowActionStep create() {
        return new RefreshWindowActionStep();
    }
}
