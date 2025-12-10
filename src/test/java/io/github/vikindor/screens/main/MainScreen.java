package io.github.vikindor.screens.main;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.id;

public class MainScreen {

    private final SelenideElement announcementTitle = $(id("org.wikipedia.alpha:id/view_announcement_text"));
    private final SelenideElement popupContainer = $(id("org.wikipedia.alpha:id/container"));
    private final SelenideElement popupPlayGameButton = $(id("org.wikipedia.alpha:id/playGameButton"));
    private final SelenideElement popupCloseButton = $(id("org.wikipedia.alpha:id/closeButton"));

    public final String ANNOUNCEMENT_TITLE = "Customize your Explore feed";

    SearchComponent searchComponent = new SearchComponent();

    public MainScreen verifyAnnouncementTitle() {
        announcementTitle.shouldHave(text(ANNOUNCEMENT_TITLE));
        return this;
    }

    public MainScreen searchFor(String text) {
        searchComponent.enterTextIntoSearchBar(text);
        return this;
    }

    public MainScreen openFoundArticle(String title) {
        searchComponent.openArticle(title);
        return this;
    }

    public boolean isPopupVisible() {
        return popupContainer.exists() && popupContainer.isDisplayed();
    }

    public MainScreen closePopupIfVisible() {
        if (isPopupVisible()) {
            popupCloseButton.click();
        }
        return this;
    }

    public MainScreen verifyWikipediaGamesModal() {
        popupPlayGameButton.shouldBe(visible);
        return this;
    }
}
