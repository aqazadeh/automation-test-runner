package runner.model.step.window;

import lombok.Getter;
import lombok.ToString;
import runner.model.step.TestStep;

/**
 * Action step for switching to a different window or tab
 */
@Getter
@ToString(callSuper = true)
public class SwitchToWindowActionStep extends TestStep {
    private String windowHandle;     // Window handle to switch to
    private String windowTitleOrUrl; // Window title or URL to match
    
    public void setWindowHandle(String windowHandle) {
        this.windowHandle = windowHandle;
    }
    
    public void setWindowTitleOrUrl(String windowTitleOrUrl) {
        this.windowTitleOrUrl = windowTitleOrUrl;
    }
    
    public static SwitchToWindowActionStep createByHandle(String windowHandle) {
        SwitchToWindowActionStep step = new SwitchToWindowActionStep();
        step.setWindowHandle(windowHandle);
        return step;
    }
    
    public static SwitchToWindowActionStep createByTitleOrUrl(String windowTitleOrUrl) {
        SwitchToWindowActionStep step = new SwitchToWindowActionStep();
        step.setWindowTitleOrUrl(windowTitleOrUrl);
        return step;
    }
}