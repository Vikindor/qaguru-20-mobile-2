package io.github.vikindor.screens.article;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.id;

public class ArticleScreen {

    private final SelenideElement findInArticleButton = $(id("org.wikipedia.alpha:id/page_find_in_article"));
    private final SelenideElement popupCloseButton = $(id("org.wikipedia.alpha:id/closeButton"));

    public ArticleScreen verifyArticlePage() {
        // The release build of the app is not debuggable, so the article content cannot be inspected:
        // the main text is rendered inside a non-debuggable WebView (or as a canvas-like surface),
        // meaning Appium cannot access or assert its internal text. Therefore, this button is the
        // only reliable native indicator that the article page has been opened.
        findInArticleButton.shouldBe(visible);
        return this;
    }

    public ArticleScreen closePopup() {
        popupCloseButton.click();
        return this;
    }
}
