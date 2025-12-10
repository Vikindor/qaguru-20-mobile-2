package io.github.vikindor.screens.onboarding;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.id;

public class OnboardingScreen {

    private final SelenideElement
            titleElement = $(id("org.wikipedia.alpha:id/primaryTextView")),
            skipButton = $(id("org.wikipedia.alpha:id/fragment_onboarding_skip_button")),
            continueButton = $(id("org.wikipedia.alpha:id/fragment_onboarding_forward_button")),
            getStartedButton = $(id("org.wikipedia.alpha:id/fragment_onboarding_done_button"));

    public OnboardingScreen clickSkipButton(){
        skipButton.click();
        return this;
    }

    public OnboardingScreen clickContinueButton(){
        continueButton.click();
        return this;
    }

    public OnboardingScreen clickGetStartedButton(){
        getStartedButton.click();
        return this;
    }

    public OnboardingScreen verifyTitle(String title){
        titleElement.shouldHave(text(title));
        return this;
    }
}
