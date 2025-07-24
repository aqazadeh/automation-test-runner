package runner.model.step.alert;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class SendValueToAlertActionStep extends TestStep {
    private String value;
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public static SendValueToAlertActionStep create(String value) {
        SendValueToAlertActionStep step = new SendValueToAlertActionStep();
        step.setValue(value);
        return step;
    }
}
