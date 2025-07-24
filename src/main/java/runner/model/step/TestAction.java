package runner.model.step;

import runner.executor.ActionExecutor;
import runner.executor.alert.*;
import runner.executor.basic.*;
import runner.executor.browser.*;
import runner.executor.condition.ConditionActionExecutor;
import runner.executor.mouse.*;
import runner.executor.waiting.WaitActionExecutor;
import runner.executor.window.*;

@SuppressWarnings("unchecked")
public enum TestAction {
    // Basic Actions
    NAVIGATE {
        @Override
        public NavigateActionExecutor executor() {
            return new NavigateActionExecutor();
        }
    },
    CLICK {
        @Override
        public ClickActionExecutor executor() {
            return new ClickActionExecutor();
        }
    },
    SEND_VALUE {
        @Override
        public SendValueActionExecutor executor() {
            return new SendValueActionExecutor();
        }
    },
    SELECT_OPTION {
        @Override
        public SelectOptionActionExecutor executor() {
            return new SelectOptionActionExecutor();
        }
    },
    ASSERT_TEXT {
        @Override
        public AssertTextActionExecutor executor() {
            return new AssertTextActionExecutor();
        }
    },
    SCREENSHOT {
        @Override
        public ScreenshootActionExecutor executor() {
            return new ScreenshootActionExecutor();
        }
    },
    CLEAR {
        @Override
        public ClearActionExecutor executor() {
            return new ClearActionExecutor();
        }
    },
    
    // Browser Actions
    EXECUTE_SCRIPT {
        @Override
        public ExecuteScriptActionExecutor executor() {
            return new ExecuteScriptActionExecutor();
        }
    },
    SCROLL_TO {
        @Override
        public ScrollToActionExecutor executor() {
            return new ScrollToActionExecutor();
        }
    },
    WAIT_FOR_JS {
        @Override
        public WaitForJsActionExecutor executor() {
            return new WaitForJsActionExecutor();
        }
    },
    
    // Window Actions
    SWITCH_TO_WINDOW {
        @Override
        public SwitchToWindowActionExecutor executor() {
            return new SwitchToWindowActionExecutor();
        }
    },
    CLOSE_WINDOW {
        @Override
        public CloseWindowActionExecutor executor() {
            return new CloseWindowActionExecutor();
        }
    },
    BACK_WINDOW {
        @Override
        public BackWindowActionExecutor executor() {
            return new BackWindowActionExecutor();
        }
    },
    FORWARD_WINDOW {
        @Override
        public ForwardWindowActionExecutor executor() {
            return new ForwardWindowActionExecutor();
        }
    },
    REFRESH_WINDOW {
        @Override
        public RefreshWindowActionExecutor executor() {
            return new RefreshWindowActionExecutor();
        }
    },
    
    // Alert Actions
    ACCEPT_ALERT {
        @Override
        public AcceptAlertActionExecutor executor() {
            return new AcceptAlertActionExecutor();
        }
    },
    DISMISS_ALERT {
        @Override
        public DismissAlertActionExecutor executor() {
            return new DismissAlertActionExecutor();
        }
    },
    SEND_VALUE_TO_ALERT {
        @Override
        public SendValueToAlertActionExecutor executor() {
            return new SendValueToAlertActionExecutor();
        }
    },
    
    // Mouse Actions
    HOVER {
        @Override
        public HoverActionExecutor executor() {
            return new HoverActionExecutor();
        }
    },
    RIGHT_CLICK {
        @Override
        public RightClickActionExecutor executor() {
            return new RightClickActionExecutor();
        }
    },
    DOUBLE_CLICK {
        @Override
        public DoubleClickActionExecutor executor() {
            return new DoubleClickActionExecutor();
        }
    },
    DRAG_AND_DROP {
        @Override
        public DragAndDropActionExecutor executor() {
            return new DragAndDropActionExecutor();
        }
    },
    
    // Waiting & Condition Actions
    WAIT {
        @Override
        public WaitActionExecutor executor() {
            return new WaitActionExecutor();
        }
    },
    CONDITION {
        @Override
        public ConditionActionExecutor executor() {
            return new ConditionActionExecutor();
        }
    };
    
    public abstract <T extends TestStep> ActionExecutor<T> executor();
}