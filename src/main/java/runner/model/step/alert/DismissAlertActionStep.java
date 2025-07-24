package runner.model.step.alert;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class DismissAlertActionStep extends TestStep {
    private Integer timeout; // Optional timeout in seconds
    
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
    
    public static DismissAlertActionStep create() {
        return new DismissAlertActionStep();
    }
    
    public static DismissAlertActionStep create(Integer timeout) {
        DismissAlertActionStep step = new DismissAlertActionStep();
        step.setTimeout(timeout);
        return step;
    }
}
