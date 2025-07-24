package runner.model.step.mouse;

import lombok.Getter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class HoverActionStep extends TestStep {
    private Target target;
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public static HoverActionStep create(Target target) {
        HoverActionStep step = new HoverActionStep();
        step.setTarget(target);
        return step;
    }
}
