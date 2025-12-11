package io.github.vikindor.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.appium.java_client.android.AndroidDriver;
import io.github.vikindor.configs.ConfigProvider;
import io.github.vikindor.drivers.MobileDriver;
import io.github.vikindor.helpers.AllureAttach;
import io.github.vikindor.screens.main.MainScreen;
import io.github.vikindor.screens.onboarding.OnboardingScreen;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

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

    @BeforeAll
    static void cleanAllureResults() {
        try {
            Path results = Paths.get(System.getProperty("user.dir"), "build", "allure-results");

            if (Files.exists(results)) {
                Files.walk(results)
                     .filter(path -> !path.equals(results))
                     .sorted(Comparator.reverseOrder())
                     .map(Path::toFile)
                     .forEach(File::delete);
            }
        } catch (Exception e) {
            System.err.println("Failed to clean allure-results: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUpTest(TestInfo testInfo) {
        String bsSessionName = testInfo.getDisplayName();
        System.setProperty("bs.sessionName", bsSessionName);

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        open();

        AndroidDriver driver = (AndroidDriver) WebDriverRunner.getWebDriver();
        driver.startRecordingScreen();

        onboardingScreen = new OnboardingScreen();
        mainScreen = new MainScreen();
    }

    @AfterEach
    void tearDown() {
        String platform = System.getProperty(PROPERTY);
        String sessionId = Selenide.sessionId().toString();

        AllureAttach.screenshot();
        AllureAttach.pageSource();

        if (platform.equals(EMULATOR_ANDROID) || platform.equals(REAL_ANDROID)) {
            AllureAttach.appiumVideo();
        }

        closeWebDriver();

        if (platform.equals(BS_ANDROID) || platform.equals(BS_IOS)) {
            AllureAttach.browserStackVideo(sessionId);
        }
    }
}
