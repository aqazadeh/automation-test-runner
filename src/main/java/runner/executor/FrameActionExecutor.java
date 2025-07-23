//package runner.executor;
//
//import com.aventstack.extentreports.Status;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import runner.manager.ReportManager;
//
///**
// * Executor for frame/iframe related actions
// */
//public class FrameActionExecutor extends ActionExecutorBase {
//
//    public FrameActionExecutor(WebDriver driver) {
//        super(driver);
//    }
//
//    @Override
//    public boolean canExecute(String action) {
//        return switch (action) {
//            case "switchToFrame", "switchToDefaultContent", "switchToParentFrame" -> true;
//            default -> false;
//        };
//    }
//
//    @Override
//    public void execute(TestStep step) {
//        switch (step.getAction()) {
//            case "switchToFrame" -> {
//                if (step.getFrameIdentifier() != null) {
//                    try {
//                        // Switch by name or ID
//                        driver.switchTo().frame(step.getFrameIdentifier());
//                    } catch (Exception e) {
//                        // Switch by index
//                        try {
//                            driver.switchTo().frame(Integer.parseInt(step.getFrameIdentifier()));
//                        } catch (NumberFormatException nfe) {
//                            throw new IllegalArgumentException("Invalid frame identifier: " + step.getFrameIdentifier());
//                        }
//                    }
//                } else if (step.getTarget() != null) {
//                    // Switch by WebElement
//                    WebElement frameElement = find(step);
//                    driver.switchTo().frame(frameElement);
//                }
//                ReportManager.logStep(Status.PASS, "Frame'e Geç",
//                        "İframe'e geçiş yapıldı: " + (step.getFrameIdentifier() != null ? step.getFrameIdentifier() : "element"));
//            }
//            case "switchToDefaultContent" -> {
//                driver.switchTo().defaultContent();
//                ReportManager.logStep(Status.PASS, "Ana İçeriğe Dön",
//                        "Sayfa ana içeriğine dönüldü");
//            }
//            case "switchToParentFrame" -> {
//                driver.switchTo().parentFrame();
//                ReportManager.logStep(Status.PASS, "Üst Frame'e Dön",
//                        "Üst iframe'e dönüldü");
//            }
//            default -> throw new IllegalArgumentException("Unsupported action: " + step.getAction());
//        }
//    }
//}
