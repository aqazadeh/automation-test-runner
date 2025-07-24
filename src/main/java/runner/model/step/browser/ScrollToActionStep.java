package runner.model.step.browser;

import lombok.Getter;
import lombok.ToString;
import runner.model.Target;
import runner.model.step.TestStep;

@Getter
@ToString(callSuper = true)
public class ScrollToActionStep extends TestStep {
    private Target target;
    private Integer x;
    private Integer y;
    
    public void setTarget(Target target) {
        this.target = target;
    }
    
    public void setX(Integer x) {
        this.x = x;
    }
    
    public void setY(Integer y) {
        this.y = y;
    }
    
    public static ScrollToActionStep createToElement(Target target) {
        ScrollToActionStep step = new ScrollToActionStep();
        step.setTarget(target);
        return step;
    }
    
    public static ScrollToActionStep createToCoordinates(int x, int y) {
        ScrollToActionStep step = new ScrollToActionStep();
        step.setX(x);
        step.setY(y);
        return step;
    }
}
