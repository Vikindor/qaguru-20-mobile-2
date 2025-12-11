package io.github.vikindor.helpers;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

public final class A {

    private A() {}

    private static AndroidDriver driver() {
        return (AndroidDriver) WebDriverRunner.getWebDriver();
    }

    public static void back() {
        driver().pressKey(new KeyEvent(AndroidKey.BACK));
    }

    public static void home() {
        driver().pressKey(new KeyEvent(AndroidKey.HOME));
    }

    public static void appSwitch() {
        driver().pressKey(new KeyEvent(AndroidKey.APP_SWITCH));
    }

    public static void activateApp(String appPackage) {
        driver().activateApp(appPackage);
    }
}
