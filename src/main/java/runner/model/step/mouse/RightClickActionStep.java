package runner.model.step.mouse;

import lombok.Getter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class RightClickActionStep extends TestStep {
    private Target target;
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public static RightClickActionStep create(Target target) {
        RightClickActionStep step = new RightClickActionStep();
        step.setTarget(target);
        return step;
    }
}
