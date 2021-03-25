package advancedSearchTest;

import advancedSearch.KindleBooksSearch;
import base.CommonAPI;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import reporting.ApplicationLog;
import reporting.TestLogger;

public class advancedSearchTest extends CommonAPI {


    @Test
    public void keywordsIsDisplayedTest(){
        ApplicationLog.epicLogger();
        TestLogger.log(getClass().getSimpleName() + ": " + convertToString(new Object(){}.getClass().getEnclosingMethod().getName()));
        KindleBooksSearch kindleBooksSearch = PageFactory.initElements(driver,KindleBooksSearch.class);
        boolean displayed = kindleBooksSearch.keywordsIsDisplayed();
        Assert.assertEquals(displayed,true);
    }

}
