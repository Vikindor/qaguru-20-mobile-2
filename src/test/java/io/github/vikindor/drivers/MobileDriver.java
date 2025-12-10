package io.github.vikindor.drivers;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.github.vikindor.configs.ConfigProvider;
import io.github.vikindor.utils.AdbDeviceDetector;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.NonNull;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static io.appium.java_client.remote.AutomationName.ANDROID_UIAUTOMATOR2;
import static io.github.vikindor.configs.MobilePlatform.*;

public class MobileDriver implements WebDriverProvider {

    @NonNull
    @Override
    public WebDriver createDriver(@NonNull Capabilities capabilities) {
        String platform = System.getProperty(PROPERTY);

        return switch (platform) {
            case EMULATOR_ANDROID, REAL_ANDROID -> localAndroidDriver();
            case BS_ANDROID -> bsAndroidDriver();
            case BS_IOS -> bsIOSDriver();
            default -> throw new AssertionError("Wrong platform name: " + platform);
        };
    }

    private WebDriver localAndroidDriver() {
        AdbDeviceDetector.DeviceInfo adbDetector = AdbDeviceDetector.detectDeviceInfo();

        UiAutomator2Options options = new UiAutomator2Options();

        options.setPlatformName(ANDROID);
        options.setAutomationName(ANDROID_UIAUTOMATOR2);
        options.setDeviceName(adbDetector.deviceId);
        options.setPlatformVersion(adbDetector.platformVersion);
        options.setApp(getAppPath());
        options.setAppPackage(ConfigProvider.config().appPackage());
        options.setAppActivity(ConfigProvider.config().appActivity());
        options.setAutoGrantPermissions(false);

        return new AndroidDriver(getServerUrl(), options);
    }

    private WebDriver bsAndroidDriver() {
        String bsSessionName = System.getProperty("bs.sessionName", ConfigProvider.config().sessionName());

        UiAutomator2Options options = new UiAutomator2Options();

        options.setApp(ConfigProvider.config().browserstackApp());
        options.setDeviceName(ConfigProvider.config().deviceName());
        options.setPlatformVersion(ConfigProvider.config().platformVersion());
        options.setCapability("project", ConfigProvider.config().projectName());
        options.setCapability("build", ConfigProvider.config().buildName());
        options.setCapability("name", bsSessionName);

        return new AndroidDriver(getServerUrl(), options);
    }

    private WebDriver bsIOSDriver() {
        String bsSessionName = System.getProperty("bs.sessionName", ConfigProvider.config().sessionName());

        XCUITestOptions options = new XCUITestOptions();

        options.setApp(ConfigProvider.config().browserstackApp());
        options.setDeviceName(ConfigProvider.config().deviceName());
        options.setPlatformVersion(ConfigProvider.config().platformVersion());
        options.setCapability("project", ConfigProvider.config().projectName());
        options.setCapability("build", ConfigProvider.config().buildName());
        options.setCapability("name", bsSessionName);

        return new IOSDriver(getServerUrl(), options);
    }

    private URL getServerUrl() {
        String platform = System.getProperty(PROPERTY);
        try {
            if (platform.equals(BS_ANDROID) || platform.equals(BS_IOS)) {
                return new URL(String.format(
                        ConfigProvider.config().browserstackUrl(),
                        ConfigProvider.config().browserstackUser(),
                        ConfigProvider.config().browserstackKey()
                ));
            } else if (platform.equals(REAL_ANDROID) || platform.equals(EMULATOR_ANDROID)) {
                return new URI(ConfigProvider.config().appiumUrl()).toURL();
            } else {
                throw new RuntimeException("Wrong platform name");
            }
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAppPath() {
        String appUrl = "https://github.com/wikimedia/apps-android-wikipedia/releases/download/latest/"
                + ConfigProvider.config().app();
        String appPath = "src/test/resources/apps/" + ConfigProvider.config().app();

        File app = new File(appPath);
        if (!app.exists()) {
            try (InputStream in = new URL(appUrl).openStream()) {
                FileUtils.copyInputStreamToFile(in, app);
            } catch (IOException e) {
                throw new AssertionError("Could not get application: " + ConfigProvider.config().app(), e);
            }
        }
        return app.getAbsolutePath();
    }
}
