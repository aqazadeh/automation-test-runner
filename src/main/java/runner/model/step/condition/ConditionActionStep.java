package runner.model.step.condition;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import runner.model.Condition;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@Setter
@ToString
public class ConditionActionStep extends TestStep {
    private Target target;
    private Condition type;
    private Boolean value;
    private Integer timeout;
}
