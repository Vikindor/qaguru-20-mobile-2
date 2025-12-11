package io.github.vikindor.helpers;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.android.AndroidDriver;
import io.github.vikindor.configs.ConfigProvider;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.codeborne.selenide.Selenide.sessionId;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.github.vikindor.configs.MobilePlatform.*;
import static io.restassured.RestAssured.given;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class AllureAttach {

    @Attachment(value = "Screenshot", type = "image/png")
    public static byte[] screenshot() {
        return ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "Page source", type = "text/plain")
    public static byte[] pageSource() {
        return getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }

    @Attachment(value = "Browser console logs", type = "text/plain")
    public static String browserConsoleLogs() {
        return String.join("\n", Selenide.getWebDriverLogs(BROWSER));
    }

    @Attachment(value = "Video", type = "text/html", fileExtension = ".html")
    public static String selenoidVideo() {
        return "<html><body><video width='100%' height='100%' controls autoplay><source src='"
                + getSelenoidVideoUrl()
                + "' type='video/mp4'></video></body></html>";
    }

    private static URL getSelenoidVideoUrl() {
        String videoUrl = "https://selenoid.autotests.cloud/video/" + sessionId() + ".mp4";
        try {
            return new URL(videoUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Attachment(value = "Video", type = "video/mp4", fileExtension = ".mp4")
    public static byte[] appiumVideo() {
        try {
            AndroidDriver driver = (AndroidDriver) WebDriverRunner.getWebDriver();
            if (driver == null) {
                return new byte[0];
            }

            String base64 = driver.stopRecordingScreen();
            if (base64 == null || base64.isEmpty()) {
                return new byte[0];
            }

            return Base64.getDecoder().decode(base64);

        } catch (Exception e) {
            System.err.println("Failed to attach screen recording: " + e.getMessage());
            return new byte[0];
        }
    }

    @Attachment(value = "Video", type = "text/html", fileExtension = ".html")
    public static String browserStackVideo(String sessionId) {
        return "<html><body><video width='100%' height='100%' controls autoplay><source src='"
                + getBrowserStackVideoUrl(sessionId)
                + "' type='video/mp4'></video></body></html>";
    }

    private static String getBrowserStackVideoUrl(String sessionId) {
        String platform = System.getProperty(PROPERTY);
        if (platform.equals(BS_ANDROID) || platform.equals(BS_IOS)) {
            String url = String.format("https://api.browserstack.com/app-automate/sessions/%s.json", sessionId);
            return given()
                    .auth().basic(ConfigProvider.config().browserstackUser(), ConfigProvider.config().browserstackKey())
                    .get(url)
                    .then()
                    .statusCode(200)
                    .extract().path("automation_session.video_url");
        }
        return null;
    }
}
