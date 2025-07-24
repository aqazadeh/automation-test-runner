package runner.model.step.basic;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class NavigateActionStep extends TestStep {
    private String url;
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public static NavigateActionStep create(String url) {
        NavigateActionStep step = new NavigateActionStep();
        step.setUrl(url);
        return step;
    }
}
