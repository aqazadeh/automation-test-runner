package runner.model.step.basic;

import lombok.Getter;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
public class ClearActionStep extends TestStep {
    private Target target;
}
