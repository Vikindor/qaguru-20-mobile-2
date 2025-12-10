<h1 align="center">
Wikipedia Mobile App Tests
</h1>

## 📌 About

Automated test suite for the Wikipedia mobile application on Android and iOS.
The project supports local execution (real devices & emulator) and cloud execution via BrowserStack.

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
 
`+` <a href="https://github.com/matteobaccan/owner" target="_blank" rel="noopener noreferrer">OWNER library</a> (configuration)

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

- The APK is automatically downloaded to `src/test/resources/apps/` from GitHub Releases if it is not present locally.

### Android — BrowserStack
```
gradle clean test -Dplatform=bs-and -DuserName=<your-bs-username> -DaccessKey=<your-bs-access-key>
```

### iOS — BrowserStack
```
gradle clean test -Dplatform=bs-ios -DuserName=<your-bs-username> -DaccessKey=<your-bs-access-key>
```

- BrowserStack session names are automatically derived from the test’s `@DisplayName` or method name.

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