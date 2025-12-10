package io.github.vikindor.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.github.vikindor.configs.ConfigProvider;
import io.github.vikindor.drivers.MobileDriver;
import io.github.vikindor.helpers.Attach;
import io.github.vikindor.screens.main.MainScreen;
import io.github.vikindor.screens.onboarding.OnboardingScreen;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static io.github.vikindor.configs.MobilePlatform.*;

public class TestBase {

    protected OnboardingScreen onboardingScreen;
    protected MainScreen mainScreen;

    @BeforeAll
    static void setupConfig() {
        Configuration.browser = MobileDriver.class.getName();
        Configuration.browserSize = null;
        Configuration.timeout = ConfigProvider.config().timeout();
    }

    @BeforeEach
    void setUpTest(TestInfo testInfo) {
        String bsSessionName = testInfo.getDisplayName();
        System.setProperty("bs.sessionName", bsSessionName);

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
        open();

        onboardingScreen = new OnboardingScreen();
        mainScreen = new MainScreen();
    }

    @AfterEach
    void tearDown() {
        String platform = System.getProperty(PROPERTY);
        String sessionId = Selenide.sessionId().toString();

        Attach.screenshot();
        Attach.pageSource();

        closeWebDriver();

        if (platform.equals(BS_ANDROID) || platform.equals(BS_IOS)) {
            Attach.browserStackVideo(sessionId);
        }
    }
}
