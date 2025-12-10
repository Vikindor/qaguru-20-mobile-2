package io.github.vikindor.screens.main;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.id;

class SearchComponent {

    private final SelenideElement searchContainer = $(id("org.wikipedia.alpha:id/search_container"));
    private final SelenideElement searchInput = $(id("org.wikipedia.alpha:id/search_src_text"));
    private final SelenideElement searchResultsList = $(id("org.wikipedia.alpha:id/search_results_list"));
    private final ElementsCollection searchResultsItems = searchResultsList.$$(id("org.wikipedia.alpha:id/page_list_item_title"));

    void enterTextIntoSearchBar(String text) {
        searchContainer.click();
        searchInput.sendKeys(text);
    }

    void openArticle(String title) {
        searchResultsItems.findBy(text(title)).click();
    }
}
