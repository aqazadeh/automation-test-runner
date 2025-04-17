package runner.util;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import runner.manager.ReportManager;
import runner.model.TestStep;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ActionExecutor {

    private final WebDriver driver;
    private final JavascriptExecutor jsExecutor;

    public ActionExecutor(WebDriver driver) {
        this.driver = driver;
        this.jsExecutor = (JavascriptExecutor) driver;
        ReportManager.setWebDriver(driver);
    }

    public void execute(TestStep step) {
        try {
            // Adım başlangıcını raporla
            ReportManager.logStep(Status.INFO, step.getAction(),
                    "Hedef: " + step.getTarget() + ", Değer: " + step.getValue());

            System.out.println("Action: " + step.getAction());
            System.out.println("Selector: " + step.getTarget());
            System.out.println("Value: " + step.getValue());

            if (step.condition != null && !checkCondition(step.condition)) {
                ReportManager.logStep(Status.SKIP, "Atlanan adım",
                        "Koşul sağlanmadığı için adım atlandı: " + step.getAction());
                return;
            }

            switch (step.action) {
                // --- Temel Aksiyonlar ---
                case "navigate" -> {
                    driver.get(step.url);
                    ReportManager.logStep(Status.PASS, "Sayfa Aç", "URL: " + step.url);
                }
                case "click" -> {
                    find(step).click();
                    ReportManager.logStep(Status.PASS, "Tıkla", "Elemana tıklandı");
                }
                case "type" -> {
                    find(step).sendKeys(step.value);
                    ReportManager.logStep(Status.PASS, "Yazı Yaz", "Değer: " + step.value);
                }
                case "clear" -> {
                    find(step).clear();
                    ReportManager.logStep(Status.PASS, "Temizle", "Eleman temizlendi");
                }
                case "selectOption" -> {
                    WebElement el = find(step);
                    new Select(el).selectByVisibleText(step.value);
                    ReportManager.logStep(Status.PASS, "Seçim Yap", "Seçilen değer: " + step.value);
                }
                case "waitFor" -> {
                    waitFor(step);
                    ReportManager.logStep(Status.PASS, "Bekle",
                            "Eleman için " + (step.timeout != null ? step.timeout : 10) + " saniye beklendi");
                }
                case "assertText" -> {
                    String actual = find(step).getText();
                    if (!actual.contains(step.value)) {
                        ReportManager.logStep(Status.FAIL, "Metin Kontrolü",
                                "Beklenen: " + step.value + ", Bulunan: " + actual);
                        ReportManager.attachScreenshot("AssertionFail");
                        throw new AssertionError("Expected text: " + step.value + ", but found: " + actual);
                    }
                    ReportManager.logStep(Status.PASS, "Metin Kontrolü",
                            "Metin doğrulama başarılı: " + step.value);
                }
                case "repeat" -> {
                    ReportManager.logStep(Status.INFO, "Tekrar Başlangıcı",
                            step.times + " kez tekrarlanacak");
                    for (int i = 0; i < step.times; i++) {
                        ReportManager.logStep(Status.INFO, "Tekrar " + (i + 1), "");
                        for (TestStep inner : step.steps) {
                            execute(inner);
                        }
                    }
                    ReportManager.logStep(Status.PASS, "Tekrar Sonu", "");
                }
                case "screenshot" -> {
                    TakesScreenshot ts = (TakesScreenshot) driver;
                    File src = ts.getScreenshotAs(OutputType.FILE);
                    File dest = new File("screenshots/" + step.value);
                    dest.getParentFile().mkdirs();
                    Files.copy(src.toPath(), dest.toPath());
                    ReportManager.attachScreenshot(step.value);
                    ReportManager.logStep(Status.PASS, "Ekran Görüntüsü",
                            "Ekran görüntüsü alındı: " + step.value);
                }

                // --- Fare Etkileşimleri ---
                case "hover" -> {
                    WebElement element = find(step);
                    new Actions(driver).moveToElement(element).perform();
                    ReportManager.logStep(Status.PASS, "Fare Üzerinde Gezdirme",
                            "Fare elementin üzerine getirildi");
                }
                case "doubleClick" -> {
                    WebElement element = find(step);
                    new Actions(driver).doubleClick(element).perform();
                    ReportManager.logStep(Status.PASS, "Çift Tıklama",
                            "Elemana çift tıklandı");
                }
                case "rightClick" -> {
                    WebElement element = find(step);
                    new Actions(driver).contextClick(element).perform();
                    ReportManager.logStep(Status.PASS, "Sağ Tıklama",
                            "Elemana sağ tıklandı");
                }
                case "dragAndDrop" -> {
                    WebElement source = driver.findElement(getBy(step.sourceTarget));
                    WebElement target = driver.findElement(getBy(step.destinationTarget));
                    new Actions(driver).dragAndDrop(source, target).perform();
                    ReportManager.logStep(Status.PASS, "Sürükle Bırak",
                            "Eleman sürüklendi ve bırakıldı");
                }

                // --- JavaScript Etkileşimleri ---
                case "executeJS" -> {
                    Object result;
                    if (step.getTarget() != null) {
                        // Eleman ile JavaScript çalıştırma
                        WebElement element = find(step);
                        result = jsExecutor.executeScript(step.script, element);
                    } else {
                        // Genel JavaScript çalıştırma
                        result = jsExecutor.executeScript(step.script);
                    }
                    ReportManager.logStep(Status.PASS, "JavaScript Çalıştır",
                            "Script: " + step.script + (result != null ? ", Sonuç: " + result : ""));
                }
                case "scrollTo" -> {
                    if (step.getTarget() != null) {
                        // Elemana scroll
                        WebElement element = find(step);
                        jsExecutor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
                        ReportManager.logStep(Status.PASS, "Sayfayı Kaydır",
                                "Sayfa belirtilen elemana kaydırıldı");
                    } else {
                        // Koordinata scroll
                        jsExecutor.executeScript("window.scrollTo({left: 0, top: " + step.value + ", behavior: 'smooth'});");
                        ReportManager.logStep(Status.PASS, "Sayfayı Kaydır",
                                "Sayfa belirtilen pozisyona kaydırıldı: " + step.value);
                    }
                }
                case "waitForJS" -> {
                    new WebDriverWait(driver, Duration.ofSeconds(step.timeout != null ? step.timeout : 10))
                            .until((WebDriver d) -> {
                                return (Boolean) jsExecutor.executeScript(step.condition.jsCondition);
                            });
                    ReportManager.logStep(Status.PASS, "JS için Bekle",
                            "JavaScript koşulu sağlandı: " + step.condition.jsCondition);
                }

                // --- Iframe Etkileşimleri ---
                case "switchToFrame" -> {
                    if (step.frameIdentifier != null) {
                        try {
                            // İsim veya ID ile geçiş
                            driver.switchTo().frame(step.frameIdentifier);
                        } catch (Exception e) {
                            // Index ile geçiş
                            try {
                                driver.switchTo().frame(Integer.parseInt(step.frameIdentifier));
                            } catch (NumberFormatException nfe) {
                                throw new IllegalArgumentException("Geçersiz frame tanımlayıcısı: " + step.frameIdentifier);
                            }
                        }
                    } else if (step.getTarget() != null) {
                        // WebElement ile geçiş
                        WebElement frameElement = find(step);
                        driver.switchTo().frame(frameElement);
                    }
                    ReportManager.logStep(Status.PASS, "Frame'e Geç",
                            "İframe'e geçiş yapıldı: " + (step.frameIdentifier != null ? step.frameIdentifier : "element"));
                }
                case "switchToDefaultContent" -> {
                    driver.switchTo().defaultContent();
                    ReportManager.logStep(Status.PASS, "Ana İçeriğe Dön",
                            "Sayfa ana içeriğine dönüldü");
                }
                case "switchToParentFrame" -> {
                    driver.switchTo().parentFrame();
                    ReportManager.logStep(Status.PASS, "Üst Frame'e Dön",
                            "Üst iframe'e dönüldü");
                }

                // --- Pencere Etkileşimleri ---
                case "switchToWindow" -> {
                    if (step.windowHandle != null) {
                        // Handle ile geçiş
                        driver.switchTo().window(step.windowHandle);
                    } else if (step.windowTitleOrUrl != null) {
                        // Başlık veya URL ile geçiş
                        boolean switched = false;
                        Set<String> handles = driver.getWindowHandles();
                        String currentHandle = driver.getWindowHandle();
                        
                        for (String handle : handles) {
                            driver.switchTo().window(handle);
                            if (driver.getTitle().contains(step.windowTitleOrUrl) || 
                                driver.getCurrentUrl().contains(step.windowTitleOrUrl)) {
                                switched = true;
                                break;
                            }
                        }
                        
                        if (!switched) {
                            driver.switchTo().window(currentHandle);
                            throw new NoSuchWindowException("Belirtilen başlık veya URL ile pencere bulunamadı: " + step.windowTitleOrUrl);
                        }
                    } else {
                        // Yeni açılan pencereye geçiş
                        String currentHandle = driver.getWindowHandle();
                        Set<String> handles = driver.getWindowHandles();
                        handles.remove(currentHandle);
                        
                        if (handles.isEmpty()) {
                            throw new NoSuchWindowException("Yeni pencere bulunamadı");
                        }
                        
                        driver.switchTo().window(handles.iterator().next());
                    }
                    ReportManager.logStep(Status.PASS, "Pencereye Geç",
                            "Belirtilen pencereye geçildi: " + driver.getTitle());
                }
                case "closeWindow" -> {
                    driver.close();
                    // Ana pencereye geri dön
                    if (!driver.getWindowHandles().isEmpty()) {
                        driver.switchTo().window(driver.getWindowHandles().iterator().next());
                    }
                    ReportManager.logStep(Status.PASS, "Pencereyi Kapat",
                            "Aktif pencere kapatıldı");
                }
                case "getWindowHandles" -> {
                    Set<String> handles = driver.getWindowHandles();
                    List<String> handlesList = new ArrayList<>(handles);
                    ReportManager.logStep(Status.PASS, "Pencere Tanımlayıcılarını Al",
                            "Açık pencere sayısı: " + handles.size() + ", Tanımlayıcılar: " + handlesList);
                }

                // --- Alert Diyalog Etkileşimleri ---
                case "handleAlert" -> {
                    Alert alert = driver.switchTo().alert();
                    switch (step.alertAction) {
                        case "accept" -> {
                            alert.accept();
                            ReportManager.logStep(Status.PASS, "Alert'ı Kabul Et",
                                    "Alert diyaloğu kabul edildi");
                        }
                        case "dismiss" -> {
                            alert.dismiss();
                            ReportManager.logStep(Status.PASS, "Alert'ı Reddet",
                                    "Alert diyaloğu reddedildi");
                        }
                        case "getText" -> {
                            String alertText = alert.getText();
                            ReportManager.logStep(Status.PASS, "Alert Metnini Al",
                                    "Alert metni: " + alertText);
                        }
                        case "sendKeys" -> {
                            alert.sendKeys(step.value);
                            ReportManager.logStep(Status.PASS, "Alert'a Metin Gönder",
                                    "Alert'a metin gönderildi: " + step.value);
                        }
                        default -> throw new IllegalArgumentException("Geçersiz alert aksiyonu: " + step.alertAction);
                    }
                }

                // --- Klavye Etkileşimleri ---
                case "pressKey" -> {
                    if (step.getTarget() != null) {
                        // Belirtilen elemana tuş gönder
                        WebElement element = find(step);
                        if (step.key.startsWith("Keys.")) {
                            String keyName = step.key.substring(5); // "Keys." kısmını çıkar
                            Keys key = Keys.valueOf(keyName);
                            element.sendKeys(key);
                        } else {
                            element.sendKeys(step.key);
                        }
                    } else {
                        // Aktif elemana tuş gönder
                        Actions actions = new Actions(driver);
                        if (step.key.startsWith("Keys.")) {
                            String keyName = step.key.substring(5); // "Keys." kısmını çıkar
                            Keys key = Keys.valueOf(keyName);
                            actions.sendKeys(key).perform();
                        } else {
                            actions.sendKeys(step.key).perform();
                        }
                    }
                    ReportManager.logStep(Status.PASS, "Tuşa Bas",
                            "Tuş basıldı: " + step.key);
                }

                // --- Attribute ve Element Özellikleri İşlemleri ---
                case "getAttribute" -> {
                    WebElement element = find(step);
                    String attributeValue = element.getAttribute(step.attribute);
                    ReportManager.logStep(Status.PASS, "Özellik Değeri Al",
                            "Özellik: " + step.attribute + ", Değer: " + attributeValue);
                }
                case "getCssValue" -> {
                    WebElement element = find(step);
                    String cssValue = element.getCssValue(step.attribute);
                    ReportManager.logStep(Status.PASS, "CSS Değeri Al",
                            "CSS Özelliği: " + step.attribute + ", Değer: " + cssValue);
                }
                case "isElementDisplayed" -> {
                    boolean isDisplayed = find(step).isDisplayed();
                    ReportManager.logStep(Status.PASS, "Eleman Görünür mü?",
                            "Görünürlük durumu: " + isDisplayed);
                }
                case "isElementEnabled" -> {
                    boolean isEnabled = find(step).isEnabled();
                    ReportManager.logStep(Status.PASS, "Eleman Etkin mi?",
                            "Etkinlik durumu: " + isEnabled);
                }
                case "isElementSelected" -> {
                    boolean isSelected = find(step).isSelected();
                    ReportManager.logStep(Status.PASS, "Eleman Seçili mi?",
                            "Seçim durumu: " + isSelected);
                }
                
                // --- Bekletme ve Zamanlama ---
                case "sleep" -> {
                    int sleepTime = step.timeout != null ? step.timeout : 1;
                    Thread.sleep(sleepTime * 1000L);
                    ReportManager.logStep(Status.PASS, "Bekle",
                            sleepTime + " saniye beklendi");
                }
                case "waitForElementToBeClickable" -> {
                    new WebDriverWait(driver, Duration.ofSeconds(step.timeout != null ? step.timeout : 10))
                            .until(ExpectedConditions.elementToBeClickable(getBy(step.target)));
                    ReportManager.logStep(Status.PASS, "Tıklanabilir Olana Kadar Bekle",
                            "Eleman tıklanabilir olana kadar beklendi");
                }
                case "waitForElementToBeInvisible" -> {
                    new WebDriverWait(driver, Duration.ofSeconds(step.timeout != null ? step.timeout : 10))
                            .until(ExpectedConditions.invisibilityOfElementLocated(getBy(step.target)));
                    ReportManager.logStep(Status.PASS, "Görünmez Olana Kadar Bekle",
                            "Eleman görünmez olana kadar beklendi");
                }
                
                case "back" -> {
                    driver.navigate().back();
                    ReportManager.logStep(Status.PASS, "Geri Git",
                            "Tarayıcı geçmişinde bir sayfa geri gidildi");
                }
                case "forward" -> {
                    driver.navigate().forward();
                    ReportManager.logStep(Status.PASS, "İleri Git",
                            "Tarayıcı geçmişinde bir sayfa ileri gidildi");
                }
                case "refresh" -> {
                    driver.navigate().refresh();
                    ReportManager.logStep(Status.PASS, "Sayfayı Yenile",
                            "Sayfa yenilendi");
                }

                default -> {
                    ReportManager.logStep(Status.FAIL, "Bilinmeyen Aksiyon",
                            "Bilinmeyen aksiyon: " + step.action);
                    throw new IllegalArgumentException("Unknown action: " + step.action);
                }
            }
        } catch (Exception e) {
            // Hata durumunda ekran görüntüsü al
            ReportManager.attachScreenshot("Error_" + step.action);

            // Hatayı raporla
            ReportManager.logStep(Status.FAIL, "Hata",
                    "Aksiyon: " + step.action + " -> " + e.getMessage());

            System.err.println("[FAILED] Action: " + step.action + " -> " + e.getMessage());

            if (Boolean.FALSE.equals(step.continueOnFailure)) {
                throw new RuntimeException(e);
            }
        }
    }

    private WebElement find(TestStep step) {
        return driver.findElement(getBy(step.target));
    }

    private By getBy(TestStep.Target target) {
        return switch (target.by) {
            case "id" -> By.id(target.value);
            case "name" -> By.name(target.value);
            case "css" -> By.cssSelector(target.value);
            case "xpath" -> By.xpath(target.value);
            case "className" -> By.className(target.value);
            case "tagName" -> By.tagName(target.value);
            case "linkText" -> By.linkText(target.value);
            case "partialLinkText" -> By.partialLinkText(target.value);
            default -> throw new IllegalArgumentException("Unknown locator: " + target.by);
        };
    }

    private void waitFor(TestStep step) {
        new WebDriverWait(driver, Duration.ofSeconds(step.timeout != null ? step.timeout : 10))
                .until(ExpectedConditions.visibilityOfElementLocated(getBy(step.target)));
    }

    private boolean checkCondition(TestStep.Condition condition) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            
            if (condition.jsCondition != null) {
                // JavaScript koşulu kontrol et
                return (Boolean) jsExecutor.executeScript(condition.jsCondition);
            }
            
            switch (condition.type) {
                case "visible" -> wait.until(ExpectedConditions.visibilityOfElementLocated(getBy(condition.target)));
                case "present" -> wait.until(ExpectedConditions.presenceOfElementLocated(getBy(condition.target)));
                case "clickable" -> wait.until(ExpectedConditions.elementToBeClickable(getBy(condition.target)));
                case "invisible" -> wait.until(ExpectedConditions.invisibilityOfElementLocated(getBy(condition.target)));
                case "selected" -> wait.until(ExpectedConditions.elementToBeSelected(getBy(condition.target)));
                case "titleContains" -> wait.until(ExpectedConditions.titleContains(condition.target.value));
                case "urlContains" -> wait.until(ExpectedConditions.urlContains(condition.target.value));
                default -> throw new IllegalArgumentException("Unsupported condition: " + condition.type);
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
    
    /**
     * Yeniden deneme mekanizması ile elementi bulma
     * @param step Test adımı
     * @param maxRetries Maksimum deneme sayısı
     * @param retryInterval Denemeler arasındaki bekleme süresi (ms)
     * @return Bulunan web elementi
     */
    private WebElement findWithRetry(TestStep step, int maxRetries, long retryInterval) {
        int attempts = 0;
        StaleElementReferenceException lastException = null;
        
        while (attempts < maxRetries) {
            try {
                return driver.findElement(getBy(step.target));
            } catch (StaleElementReferenceException e) {
                lastException = e;
                attempts++;
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        // Tüm denemeler başarısız oldu
        if (lastException != null) {
            throw lastException;
        } else {
            throw new NoSuchElementException("Element bulunamadı: " + step.target);
        }
    }
}