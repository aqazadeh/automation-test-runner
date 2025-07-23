package runner.model.step.mouse;

import lombok.Getter;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
public class RightClickActionStep extends TestStep {
    private Target target;
}
