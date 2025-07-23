package runner.model.step.basic;

import lombok.Getter;
import lombok.Setter;
import runner.model.Target;

@Getter
@Setter
public class AssertTextActionStep {
    private Target target;
    private String value;
}
