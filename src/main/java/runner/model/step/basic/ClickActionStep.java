package runner.model.step.basic;

import lombok.Getter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class ClickActionStep extends TestStep {
    private Target target;
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public static ClickActionStep create(Target target) {
        ClickActionStep step = new ClickActionStep();
        step.setTarget(target);
        return step;
    }
}
