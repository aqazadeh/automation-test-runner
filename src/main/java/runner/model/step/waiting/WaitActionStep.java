package runner.model.step.waiting;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@Setter
@ToString
public class WaitActionStep extends TestStep {
    private Target target;
    private Integer timeout;
}
