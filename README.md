<h1 align="center">
Wikipedia Mobile App Tests
</h1>

Automated test suite for the Wikipedia mobile application on Android and iOS.
The project supports local execution (real devices & emulator) and cloud execution via BrowserStack.

## 📌 Table of Contents

- [🛠 Tech stack](#-tech-stack)
- [✨ Features](#-features)
- [📱 Supported Platforms](#-supported-platforms)
  - [Android](#android)
  - [iOS](#ios)
- [🚀 Running Tests](#-running-tests)
  - [Android — Local Emulator](#android--local-emulator)
  - [Android — Real Device](#android--real-device)
  - [Android — BrowserStack](#android--browserstack)
  - [iOS — BrowserStack](#ios--browserstack)
- [🔍 Appium Inspector](#-appium-inspector)
- [🎥 Appium screen recording (local & BrowserStack)](#-appium-screen-recording-local--browserstack)
  - [Local Appium recording](#local-appium-recording)
    - [Lifecycle](#lifecycle)
    - [Important](#important)
  - [BrowserStack recording](#browserstack-recording)
    - [Lifecycle](#lifecycle-1)
    - [Notes](#notes)
  - [Integration example (TestBase)](#integration-example-testbase)
  - [Supported environments](#supported-environments)
- [🧠 ADB Device Detector](#-adb-device-detector)
  - [⚙️ How it works](#️-how-it-works)
  - [📋 Example usage in the driver](#-example-usage-in-the-driver)

## 🛠 Tech stack

<p align="center">
  <a href="https://www.jetbrains.com/idea/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/intellij_idea.png" alt="IntelliJ IDEA logo" title="IntelliJ IDEA"/></a>
  <a href="https://developer.android.com/studio" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/android_studio.png" alt="Android Studio logo" title="Android Studio"/></a>
  <a href="https://gradle.org/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/gradle.png" alt="Gradle logo" title="Gradle"/></a>
  <a href="https://www.java.com/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/java.png" alt="Java logo" title="Java"/></a>  
  <a href="https://junit.org/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/junit.png" alt="JUnit logo" title="JUnit"/></a>
  <a href="https://selenide.org/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/selenide.png" alt="Selenide logo" title="Selenide"/></a>  
  <a href="https://appium.io/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/appium.png" alt="Appium logo" title="Appium"/></a>
  <a href="https://appium.github.io/appium-inspector/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/appium_inspector.png" alt="Appium Inspector logo" title="Appium Inspector"/></a>
  <a href="https://www.browserstack.com/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/browserstack.png" alt="BrowserStack logo" title="BrowserStack"/></a>
  <a href="https://git-scm.com/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/git.png" alt="Git logo" title="Git"/></a>
  <a href="https://github.com/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/github.png" alt="GitHub logo" title="GitHub"/></a>
  <a href="https://qameta.io/" target="_blank" rel="noopener noreferrer"><img width="6%" src="media/logos/allure_report.png" alt="Allure Report logo" title="Allure Report"/></a>
</p>
 
`+` <a href="https://github.com/matteobaccan/owner" target="_blank" rel="noopener noreferrer">Owner library</a> (configuration)

## ✨ Features

- **Centralized configuration via the Owner library**   
All environment- and platform-specific settings are managed through typed Owner interfaces.

 
- **Automatic real-device detection via ADB**  
A custom ADB detector identifies connected physical Android devices, ignores emulators, extracts the device serial, and reads its Android version.
This allows real-device tests to run without manually specifying deviceName or platformVersion in configuration files.


- **Automatic APK download**  
The APK is automatically downloaded to `src/test/resources/apps/` from GitHub Releases if it is not present locally, ensuring that local Android runs always use the latest app build.


- **Automatic BrowserStack session naming**  
BrowserStack session names are automatically derived from the test’s `@DisplayName` (or the test method name), making sessions easy to identify in the BS dashboard.


## 📱 Supported Platforms

### Android

- Local Android Emulator
- Real Android device
- BrowserStack Android devices

### iOS

- BrowserStack real devices
- iOS applications provided as uploaded `.ipa` files (`bs://<app-id>`)

## 🚀 Running Tests

### Android — Local Emulator

```
gradle clean test -Dplatform=emul-and
```

### Android — Real Device

```
gradle clean test -Dplatform=real-and
```

### Android — BrowserStack

```
gradle clean test -Dplatform=bs-and -DuserName=<your-bs-username> -DaccessKey=<your-bs-access-key>
```

### iOS — BrowserStack

```
gradle clean test -Dplatform=bs-ios -DuserName=<your-bs-username> -DaccessKey=<your-bs-access-key>
```

## 🔍 Appium Inspector

JSON capabilities set:

```
{
  "platformName": "Android",
  "appium:automationName": "UiAutomator2",
  "appium:deviceName": "Android Emulator",
  "appium:platformVersion": "16.0",
  "appium:appPackage": "org.wikipedia.alpha",
  "appium:appActivity": "org.wikipedia.main.MainActivity",
  "appium:noReset": false
}
```

## 🎥 Appium screen recording

This project supports two independent screen recording flows:  
- **Local Appium recording** — recording is started and stopped using native Appium commands and attached to Allure as binary MP4.
- **BrowserStack recording** — BrowserStack records the session automatically and exposes the video URL through its REST API; the framework embeds this video into an HTML wrapper for Allure.

---

## Local Appium recording

**Start recording** — executed in `@BeforeEach` right after the driver session is created:

```java
AndroidDriver driver = (AndroidDriver) WebDriverRunner.getWebDriver();
driver.startRecordingScreen();
```

**Stop + attach** — executed in `@AfterEach` **before** `closeWebDriver()`:

```java
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
```

### Important

- `stopRecordingScreen()` **must be called before** `closeWebDriver()`.
- After the WebDriver is closed, Appium unbinds the driver from the current thread, and the recording cannot be retrieved anymore.

---

## BrowserStack recording

BrowserStack provides its own session video. Local Appium recording is **not used** for BS runs.

### Lifecycle

In `@AfterEach`, the framework:

1. Retrieves BrowserStack session ID via `Selenide.sessionId()`
2. Calls BS REST API to get `automation_session.video_url`
3. Wraps this video URL into an HTML `<video>` tag
4. Attaches the HTML snippet to Allure

```java
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
                .extract()
                .path("automation_session.video_url");
    }

    return null;
}
```

### Notes

- BrowserStack generates the recording shortly after the test session ends, so the framework first finishes the test (including closing the driver) and only then fetches the video via the API.
- Session ID must be taken *before* closing the session.

---

## Integration example (TestBase)

```java
@BeforeEach
void setUpTest(TestInfo testInfo) {
    String bsSessionName = testInfo.getDisplayName();
    System.setProperty("bs.sessionName", bsSessionName);

    SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

    open();

    AndroidDriver driver = (AndroidDriver) WebDriverRunner.getWebDriver(); // only for local Appium runs
    driver.startRecordingScreen(); // only for local Appium runs
}

@AfterEach
void tearDown() {
    String platform = System.getProperty(PROPERTY);
    String sessionId = Selenide.sessionId().toString();

    AllureAttach.screenshot();
    AllureAttach.pageSource();

    if (platform.equals(EMULATOR_ANDROID) || platform.equals(REAL_ANDROID)) {
        AllureAttach.appiumVideo(); // stop + attach Appium video
    }

    closeWebDriver();

    if (platform.equals(BS_ANDROID) || platform.equals(BS_IOS)) {
        AllureAttach.browserStackVideo(sessionId); // attach BrowserStack video
    }
}
```

---

## Supported environments

| Platform             | Supported | Notes                                 |
|----------------------|-----------|---------------------------------------|
| Android Emulator     | ✔         | Uses native Appium screen recording   |
| Android real device  | ✔         | Uses native Appium screen recording   |
| BrowserStack Android | ✔         | Video is provided automatically by BS |
| iOS                  | ✔         | Video is provided automatically by BS |


This unified approach keeps Allure reporting consistent while using the optimal recording strategy for each environment.

---

## 🧠 ADB Device Detector

The project includes a standalone utility that automatically detects a connected **physical Android device** using ADB.  
This allows real-device test runs without specifying `deviceName` or `platformVersion` in configuration files.

### ⚙️ How it works

The detector:

- Attempts to locate the adb executable (PATH, ANDROID_HOME, ANDROID_SDK_ROOT, common SDK paths)
- Executes `adb devices`
- **Ignores all emulators** (any device whose ID starts with `emulator-`)
- Selects the **first physical device** if multiple are connected
- Reads the device’s Android version via:
    - `ro.build.version.release` (primary source)
- Throws a clear error if:
    - no physical devices are connected
    - adb is unavailable
    - version cannot be determined

This makes real-device execution completely automatic and configuration-free.

### 📋 Example usage in the driver

```java
import utils.AdbDeviceDetector;

AdbDeviceDetector.DeviceInfo adbDetector = AdbDeviceDetector.detectDeviceInfo();

options.setDeviceName(adbDetector.deviceId);
options.setPlatformVersion(adbDetector.platformVersion);
```

When using this logic, you do **not** need to define `deviceName` or `platformVersion` in properties for real-device runs — the detector supplies them dynamically.
