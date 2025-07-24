package runner.model.step.condition;

import lombok.Getter;
import lombok.ToString;
import runner.model.Condition;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class ConditionActionStep extends TestStep {
    private Target target;
    private Condition type;
    private Boolean value;
    private Integer timeout;
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public void setType(Condition type) {
        this.type = type;
    }
    
    public void setValue(Boolean value) {
        this.value = value;
    }
    
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    
    public static ConditionActionStep create(Target target, Condition type, Boolean value) {
        ConditionActionStep step = new ConditionActionStep();
        step.setTarget(target);
        step.setType(type);
        step.setValue(value);
        return step;
    }
    
    public static ConditionActionStep create(Target target, Condition type, Boolean value, Integer timeout) {
        ConditionActionStep step = new ConditionActionStep();
        step.setTarget(target);
        step.setType(type);
        step.setValue(value);
        step.setTimeout(timeout);
        return step;
    }
}
