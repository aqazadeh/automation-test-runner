package runner.model.step.mouse;

import lombok.Getter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class DoubleClickActionStep extends TestStep {
    private Target target;
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public static DoubleClickActionStep create(Target target) {
        DoubleClickActionStep step = new DoubleClickActionStep();
        step.setTarget(target);
        return step;
    }
}
