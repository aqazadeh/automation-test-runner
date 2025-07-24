package runner.model.step.basic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;


@Getter
@ToString(callSuper = true)
public class SendValueActionStep extends TestStep {
    private Target target;
    private String value;
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public static SendValueActionStep create(Target target, String value) {
        SendValueActionStep step = new SendValueActionStep();
        step.setTarget(target);
        step.setValue(value);
        return step;
    }
}
