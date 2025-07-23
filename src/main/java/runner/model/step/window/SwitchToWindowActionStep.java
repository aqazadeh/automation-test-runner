package runner.model.step.window;

import lombok.Getter;
import lombok.Setter;
import runner.model.step.TestStep;

/**
 * Action step for switching to a different window or tab
 */
@Getter
@Setter
public class SwitchToWindowActionStep extends TestStep {
    private String windowHandle;     // Window handle to switch to
    private String windowTitleOrUrl; // Window title or URL to match
}