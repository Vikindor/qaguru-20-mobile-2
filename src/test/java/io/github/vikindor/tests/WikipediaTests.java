package io.github.vikindor.tests;

import io.github.vikindor.configs.ConfigProvider;
import io.github.vikindor.helpers.A;
import io.github.vikindor.screens.article.ArticleScreen;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.qameta.allure.Allure.step;

@Epic("Wikipedia Mobile App Tests")
@DisplayName("Wikipedia Mobile App Tests")
@Tag("UI")
public class WikipediaTests extends TestBase {

    @Test
    @Feature("Onboarding")
    @DisplayName("Complete onboarding flow and verify all screens")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("UI") @Tag("Critical") @Tag("Smoke") @Tag("Regression")
    void shouldCompleteOnboardingFlow() {
        step("Go through onboarding screen and verify titles", () -> {
            onboardingScreen
                    .verifyTitle("The Free Encyclopedia\n…in over 300 languages")
                    .clickContinueButton()
                    .verifyTitle("New ways to explore")
                    .clickContinueButton()
                    .verifyTitle("Reading lists with sync")
                    .clickContinueButton()
                    .verifyTitle("Data & Privacy")
                    .clickGetStartedButton();
        });

        step("Verify main screen is displayed", mainScreen::verifyAnnouncementTitle);
    }

    @Test
    @Feature("Onboarding")
    @DisplayName("Skip onboarding flow")
    @Severity(SeverityLevel.NORMAL)
    @Tag("UI") @Tag("Sanity") @Tag("Regression")
    void shouldSkipOnboardingFlow() {
        step("Ensure onboarding screen is displayed", () -> {
            onboardingScreen.verifyTitle("The Free Encyclopedia\n…in over 300 languages");
        });

        step("Skip onboarding screen flow", onboardingScreen::clickSkipButton);

        step("Verify main screen is displayed", mainScreen::verifyAnnouncementTitle);
    }

    @Test
    @Feature("Popups")
    @DisplayName("Display Wikipedia Games popup on first launch")
    @Severity(SeverityLevel.NORMAL)
    @Tag("UI") @Tag("Regression") @Tag("Flaky")
    void shouldDisplayWikipediaGamesPopupOnFirstLaunch() {
        step("Skip onboarding screen flow", onboardingScreen::clickSkipButton);

        step("Close and reopen app", () -> {
            A.back();
            A.activateApp(ConfigProvider.config().appPackage());
        });

        step("Verify Wikipedia games popup is displayed", mainScreen::verifyWikipediaGamesModal);

        step("Close Wikipedia games popup", mainScreen::closePopupIfVisible);

        step("Verify main screen is displayed", mainScreen::verifyAnnouncementTitle);
    }

    @ParameterizedTest
    @Feature("Search")
    @DisplayName("Search for article and open the result")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("UI") @Tag("Critical") @Tag("Regression") @Tag("Search")
    @ValueSource(strings = {"Quality Assurance", "JUnit"})
    void shouldSearchForArticleAndOpenIt(String query) {
        ArticleScreen articleScreen = new ArticleScreen();

        onboardingScreen.clickSkipButton();

        step("Search for \"" + query + "\"", () -> {
            mainScreen.searchFor(query);
        });

        step("Open article \"" + query + "\"", () -> {
            mainScreen.openFoundArticle(query);
        });

        articleScreen.closePopup();

        step("Verify article is opened", articleScreen::verifyArticlePage);
    }
}
