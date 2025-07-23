package runner.model.step.basic;

import lombok.Getter;
import lombok.Setter;
import runner.model.Target;
import runner.model.step.TestStep;


@Getter
@Setter
public class SelectOptionActionStep extends TestStep {
    private Target target;
    private String value;
}
