package runner.model.step.basic;

import lombok.Getter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class ClearActionStep extends TestStep {
    private Target target;
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public static ClearActionStep create(Target target) {
        ClearActionStep step = new ClearActionStep();
        step.setTarget(target);
        return step;
    }
}
