package runner.util;

/**
 * Bu sınıf, eklediğimiz yeni aksiyonların örnek kullanımını göstermek için tasarlanmıştır.
 * JSON test senaryolarında aşağıdaki örnekleri referans alabilirsiniz.
 */
public class ActionExecutionExample {

    /**
     * Fare Etkileşimleri Örnekleri
     */
    public static final String HOVER_EXAMPLE = """
    {
      "action": "hover",
      "target": { "by": "css", "value": ".menu-item" },
      "description": "Menü öğesinin üzerine gel"
    }
    """;

    public static final String DOUBLE_CLICK_EXAMPLE = """
    {
      "action": "doubleClick",
      "target": { "by": "id", "value": "edit-button" },
      "description": "Düzenle butonuna çift tıkla"
    }
    """;

    public static final String RIGHT_CLICK_EXAMPLE = """
    {
      "action": "rightClick",
      "target": { "by": "css", "value": ".context-menu-item" },
      "description": "Öğeye sağ tıkla"
    }
    """;

    public static final String DRAG_DROP_EXAMPLE = """
    {
      "action": "dragAndDrop",
      "sourceTarget": { "by": "id", "value": "draggable" },
      "destinationTarget": { "by": "id", "value": "droppable" },
      "description": "Öğeyi sürükle ve bırak"
    }
    """;

    /**
     * JavaScript Etkileşimleri Örnekleri
     */
    public static final String EXECUTE_JS_EXAMPLE = """
    {
      "action": "executeJS",
      "script": "return document.title;",
      "description": "Sayfa başlığını getir"
    }
    """;

    public static final String EXECUTE_JS_WITH_ELEMENT_EXAMPLE = """
    {
      "action": "executeJS",
      "target": { "by": "id", "value": "myElement" },
      "script": "arguments[0].style.border='3px solid red';",
      "description": "Elementin etrafına kırmızı çerçeve çiz"
    }
    """;

    public static final String SCROLL_TO_ELEMENT_EXAMPLE = """
    {
      "action": "scrollTo",
      "target": { "by": "id", "value": "footer" },
      "description": "Sayfayı footer'a kadar kaydır"
    }
    """;

    public static final String SCROLL_TO_POSITION_EXAMPLE = """
    {
      "action": "scrollTo",
      "value": "500",
      "description": "Sayfayı 500 piksel aşağı kaydır"
    }
    """;

    public static final String WAIT_FOR_JS_EXAMPLE = """
    {
      "action": "waitForJS",
      "condition": {
        "jsCondition": "return document.readyState === 'complete'"
      },
      "timeout": 10,
      "description": "Sayfa tamamen yüklenene kadar bekle"
    }
    """;

    /**
     * Iframe Etkileşimleri Örnekleri
     */
    public static final String SWITCH_TO_FRAME_BY_INDEX_EXAMPLE = """
    {
      "action": "switchToFrame",
      "frameIdentifier": "0",
      "description": "İlk iframe'e geç"
    }
    """;

    public static final String SWITCH_TO_FRAME_BY_NAME_EXAMPLE = """
    {
      "action": "switchToFrame",
      "frameIdentifier": "myFrame",
      "description": "İsmi 'myFrame' olan iframe'e geç"
    }
    """;

    public static final String SWITCH_TO_FRAME_BY_ELEMENT_EXAMPLE = """
    {
      "action": "switchToFrame",
      "target": { "by": "id", "value": "iframe1" },
      "description": "ID'si 'iframe1' olan iframe'e geç"
    }
    """;

    public static final String SWITCH_TO_DEFAULT_CONTENT_EXAMPLE = """
    {
      "action": "switchToDefaultContent",
      "description": "Ana sayfaya geri dön"
    }
    """;

    public static final String SWITCH_TO_PARENT_FRAME_EXAMPLE = """
    {
      "action": "switchToParentFrame",
      "description": "Üst iframe'e dön"
    }
    """;

    /**
     * Pencere Etkileşimleri Örnekleri
     */
    public static final String SWITCH_TO_WINDOW_BY_TITLE_EXAMPLE = """
    {
      "action": "switchToWindow",
      "windowTitleOrUrl": "Yeni Pencere",
      "description": "Başlığı 'Yeni Pencere' olan pencereye geç"
    }
    """;

    public static final String SWITCH_TO_NEW_WINDOW_EXAMPLE = """
    {
      "action": "switchToWindow",
      "description": "Yeni açılan pencereye geç"
    }
    """;

    public static final String CLOSE_WINDOW_EXAMPLE = """
    {
      "action": "closeWindow",
      "description": "Aktif pencereyi kapat"
    }
    """;

    public static final String GET_WINDOW_HANDLES_EXAMPLE = """
    {
      "action": "getWindowHandles",
      "description": "Açık pencerelerin listesini al"
    }
    """;

    /**
     * Alert Diyalog Etkileşimleri Örnekleri
     */
    public static final String HANDLE_ALERT_ACCEPT_EXAMPLE = """
    {
      "action": "handleAlert",
      "alertAction": "accept",
      "description": "Alert'i kabul et (Tamam'a bas)"
    }
    """;

    public static final String HANDLE_ALERT_DISMISS_EXAMPLE = """
    {
      "action": "handleAlert",
      "alertAction": "dismiss",
      "description": "Alert'i reddet (İptal'e bas)"
    }
    """;

    public static final String HANDLE_ALERT_GET_TEXT_EXAMPLE = """
    {
      "action": "handleAlert",
      "alertAction": "getText",
      "description": "Alert metnini al"
    }
    """;

    public static final String HANDLE_ALERT_SEND_KEYS_EXAMPLE = """
    {
      "action": "handleAlert",
      "alertAction": "sendKeys",
      "value": "test input",
      "description": "Alert'e metin gir"
    }
    """;

    /**
     * Klavye Etkileşimleri Örnekleri
     */
    public static final String PRESS_KEY_EXAMPLE = """
    {
      "action": "pressKey",
      "key": "Keys.ENTER",
      "description": "ENTER tuşuna bas"
    }
    """;

    public static final String PRESS_KEY_TO_ELEMENT_EXAMPLE = """
    {
      "action": "pressKey",
      "target": { "by": "id", "value": "searchInput" },
      "key": "Keys.ESCAPE",
      "description": "Arama kutusunda ESC tuşuna bas"
    }
    """;

    /**
     * Attribute ve Element Özellikleri İşlemleri Örnekleri
     */
    public static final String GET_ATTRIBUTE_EXAMPLE = """
    {
      "action": "getAttribute",
      "target": { "by": "id", "value": "myLink" },
      "attribute": "href",
      "description": "Link'in href değerini al"
    }
    """;

    public static final String GET_CSS_VALUE_EXAMPLE = """
    {
      "action": "getCssValue",
      "target": { "by": "id", "value": "header" },
      "attribute": "background-color",
      "description": "Header'ın arka plan rengini al"
    }
    """;

    public static final String IS_ELEMENT_DISPLAYED_EXAMPLE = """
    {
      "action": "isElementDisplayed",
      "target": { "by": "id", "value": "errorMessage" },
      "description": "Hata mesajı görünür mü kontrol et"
    }
    """;

    /**
     * Gelişmiş Bekleme ve Zamanlama Örnekleri
     */
    public static final String SLEEP_EXAMPLE = """
    {
      "action": "sleep",
      "timeout": 2,
      "description": "2 saniye bekle"
    }
    """;

    public static final String WAIT_FOR_ELEMENT_CLICKABLE_EXAMPLE = """
    {
      "action": "waitForElementToBeClickable",
      "target": { "by": "id", "value": "submitButton" },
      "timeout": 5,
      "description": "Gönder butonunun tıklanabilir olmasını bekle"
    }
    """;

    public static final String WAIT_FOR_ELEMENT_INVISIBLE_EXAMPLE = """
    {
      "action": "waitForElementToBeInvisible",
      "target": { "by": "css", "value": ".loading-spinner" },
      "timeout": 10,
      "description": "Yükleme simgesinin kaybolmasını bekle"
    }
    """;

    /**
     * Tarayıcı Gezinme Örnekleri
     */
    public static final String BACK_EXAMPLE = """
    {
      "action": "back",
      "description": "Önceki sayfaya dön"
    }
    """;

    public static final String FORWARD_EXAMPLE = """
    {
      "action": "forward",
      "description": "Sonraki sayfaya git"
    }
    """;

    public static final String REFRESH_EXAMPLE = """
    {
      "action": "refresh",
      "description": "Sayfayı yenile"
    }
    """;

    /**
     * Koşullu Aksiyon Örnekleri
     */
    public static final String CONDITIONAL_ACTION_EXAMPLE = """
    {
      "action": "click",
      "target": { "by": "id", "value": "closeButton" },
      "condition": {
        "target": { "by": "id", "value": "popup" },
        "type": "visible"
      },
      "description": "Popup görünür ise kapat butonuna tıkla"
    }
    """;

    public static final String CONDITIONAL_JS_ACTION_EXAMPLE = """
    {
      "action": "click",
      "target": { "by": "id", "value": "accept-cookies" },
      "condition": {
        "jsCondition": "return document.cookie.indexOf('accepted') === -1"
      },
      "description": "Çerezler kabul edilmemiş ise çerezleri kabul et"
    }
    """;
}
