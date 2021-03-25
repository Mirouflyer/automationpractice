package advancedSearch;

import base.CommonAPI;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import reporting.TestLogger;

public class KindleBooksSearch extends CommonAPI {


    @FindBy(xpath = "//div[@id='nav-xshop-container']//a[contains(text(),'Kindle Books')]")
    public WebElement KindleBooks;
    @FindBy(xpath ="//a//span[contains(text(),' Advanced Search')]")
    public WebElement advancedSearch;
    @FindBy(xpath ="//div[@class='asSection']//input[@name='field-keywords']")
    public WebElement keywords;
    @FindBy()
    public WebElement author;
    @FindBy()
    public WebElement title ;
    @FindBy()
    public WebElement publisher;
    @FindBy()
    public WebElement subject;
    @FindBy()
    public WebElement language;
    @FindBy()
    public WebElement pub_date;
    @FindBy()
    public WebElement month;
    @FindBy()
    public WebElement year;
    @FindBy()
    public WebElement sortResultBy;
    @FindBy()
    public WebElement searchButton;

    public WebElement getKindleBooks() {
        return KindleBooks;
    }

    public WebElement getAdvancedSearch() {
        return advancedSearch;
    }

    public WebElement getKeywords() {
        return keywords;
    }

    public WebElement getAuthor() {
        return author;
    }

    public WebElement getTitle() {
        return title;
    }

    public WebElement getPublisher() {
        return publisher;
    }

    public WebElement getSubject() {
        return subject;
    }

    public WebElement getLanguage() {
        return language;
    }

    public WebElement getPub_date() {
        return pub_date;
    }

    public WebElement getMonth() {
        return month;
    }

    public WebElement getYear() {
        return year;
    }

    public WebElement getSortResultBy() {
        return sortResultBy;
    }

    public WebElement getSearchButton() {
        return searchButton;
    }


    public void kindleBooksSearch(){

    }


    public void AdvancedSearchHomePage(){
        getKindleBooks().click();
        getAdvancedSearch().click();
    }

    public boolean keywordsIsDisplayed(){
        TestLogger.log(getClass().getSimpleName() + ": " + convertToString(new Object(){}.getClass().getEnclosingMethod().getName()));
        AdvancedSearchHomePage();
        return getKeywords().isDisplayed();
    }
}
