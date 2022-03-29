package test;

import constants.AppLauncherConstant;
import constants.CaseConstant;
import constants.ProfileConstant;
import driver.InitializeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pageComponents.CasePage;
import pageComponents.LandingPage;
import pageComponents.LoginPage;

public class CaseFeatureTest {

    WebDriver driver = InitializeDriver.getDriver();

    /*
    User Story: Case Management for Service Agents
    Description: Service Agent should successfully create a NEW case (AC #1)
    Test Method Name: caseCreationTest()
    */
    @Test(priority = 1, dataProvider = "caseCreation", dataProviderClass = GetData.class)
    public void caseCreationTest(String caseOrigVal, String caseSubjVal) {
        //Initializing objects
        LoginPage loginPage = new LoginPage(driver);
        LandingPage landPage = new LandingPage(driver);
        CasePage casePage = new CasePage(driver);

        //Login to salesforce as service agent and switch to lightning (if not present by default)
        loginPage.loginToSalesforce(ProfileConstant.serviceAgent);

        //Switch to service cloud and navigate to case tab (if not present by default)
        landPage.appLauncher(AppLauncherConstant.serviceCloud);
        landPage.selectTabName(CaseConstant.caseTab);

        //Create case and save
        casePage.createCase(caseOrigVal, caseSubjVal);

        /*Assertions*/
        //Validate case is created successfully via toast message
        String message = casePage.caseToastMessage();
        Assert.assertTrue(message.contains(CaseConstant.caseCreationMessage), "Issue in case creation: success message failure");

        casePage.currentCaseTab();
        String caseURL = driver.getCurrentUrl();

        //Fetch Case Origin field value
        String caseOriginValue = driver.findElement(By.xpath(String.format(CasePage.fieldValueOnCaseDetail, CaseConstant.caseOriginField))).getText();

        //(+ve) Validate case detail page is displayed and case origin field value as 'Phone'
        if ((caseOrigVal.contentEquals(CaseConstant.phone) && ((caseSubjVal.contentEquals(CaseConstant.caseSubject))))) {
            Assert.assertTrue(casePage.isCaseDetailPageDisplayed(), "Case detail page is not displayed despite correct inputs.\nCase Origin is 'Phone' && Subject is 'Password Request'.\nTest data: " + caseURL);
            Assert.assertEquals(caseOriginValue, CaseConstant.phone, "Case origin field validation failed despite correct inputs.\nCase Origin Input is 'Phone' && Subject input is 'Password Request.\nCase origin post save is " +caseOriginValue+" Expected is 'Phone'\nTest data: " + caseURL);
        }
        //(-ve) Validate when case origin field != 'Phone' and/or subject field != 'Password Request'
        else {
            SoftAssert softAssert = new SoftAssert();
            if (!(caseOrigVal.contentEquals(CaseConstant.phone) || ((caseSubjVal.contentEquals(CaseConstant.caseSubject))))) {
                softAssert.assertEquals(caseOriginValue, caseOrigVal, "Case origin field value mismatch. Expected: "+caseOrigVal+" Found: "+caseOriginValue+"\nTest data: "+caseURL);
                softAssert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page should not be displayed. To be displayed only if Case Origin input value = 'Phone'. Found: " + caseOrigVal + " && Subject input value = 'Password Request'. Found:"+"'"+caseSubjVal+"'\nTest data: " + caseURL);
            } else if (!(caseOrigVal.contentEquals(CaseConstant.phone))) {
                softAssert.assertEquals(caseOriginValue, caseOrigVal, "Case origin field value mismatch. Expected: "+caseOrigVal+"Found: "+caseOriginValue+"\nTest data: "+caseURL);
                softAssert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page should not be displayed. Case Origin input value should be 'Phone'. Found:"+"'"+caseOrigVal+"'\nTest data: " + caseURL);
            } else {
                softAssert.assertEquals(caseOriginValue, caseOrigVal, "Case origin field value mismatch. Expected: "+caseOrigVal+" Found: "+caseOriginValue+"\nTest data: "+caseURL);
                softAssert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page should not be displayed. Subject input value should be 'Password Request'. Found: "+"'"+caseSubjVal+"'\nTest data: " + caseURL);
            }
            softAssert.assertAll();
        }
    }

    /*
    User Story: Case Management for Service Agents
    Description: Service Agent should successfully create a case (AC #1)
               : Checks for case EDIT scenarios
    Test Method Name: caseCreationEditTest()
    */
    @Test(priority = 2, dataProvider = "caseCreationEdit", dataProviderClass = GetData.class)
    public void caseCreationEditTest(String caseOrigVal, String caseSubjVal, String changeOriginValue, String changeSubjValue) {
        //Initializing objects
        LoginPage loginPage = new LoginPage(driver);
        LandingPage landPage = new LandingPage(driver);
        CasePage casePage = new CasePage(driver);

        //Login to salesforce as service agent and switch to lightning (if not present by default)
        loginPage.loginToSalesforce(ProfileConstant.serviceAgent);

        //Switch to service cloud and navigate to case tab (if not present by default)
        landPage.appLauncher(AppLauncherConstant.serviceCloud);
        landPage.selectTabName(CaseConstant.caseTab);

        //Create case and save
        casePage.createCase(caseOrigVal, caseSubjVal);

        //Validate case is created successfully via toast message
        String message = casePage.caseToastMessage();
        Assert.assertTrue(message.contains(CaseConstant.caseCreationMessage), "Issue in case creation: success message failure");

        //Edit case
        String caseURL = driver.getCurrentUrl();
        casePage.editCase(caseURL, true, changeOriginValue, changeSubjValue);
        driver.navigate().refresh();
        /*Assertions*/
        if (changeOriginValue.contentEquals(CaseConstant.phone) && changeSubjValue.contentEquals(CaseConstant.caseSubject)) {
            //Validate Case Origin field value post case edit
            String caseOriginValue = driver.findElement(By.xpath(String.format(CasePage.fieldValueOnCaseDetail, CaseConstant.caseOriginField))).getText();
            Assert.assertEquals(caseOriginValue, changeOriginValue, "Case origin field value mismatch. Expected: " + changeOriginValue + ". Found: " + caseOriginValue +"\nTest data: "+ caseURL);
            //Validate case detail page is not displayed post case edit
            Assert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page is displayed. To be displayed only on new case creation and not on edit.\nTest data: " + caseURL);
        }
    }

    /*
    User Story: Case Management for Service Agents
    Description: A default Subject should always be assigned to the Case (AC #2)
    Test Method Name: defaultSubjectAssignmentToCaseTest()
    */
    @Test(priority = 3, dataProvider = "caseDefaultAssignment", dataProviderClass = GetData.class)
    public void defaultSubjectAssignmentToCaseTest(String caseOrigVal, String caseSubjVal) {
        //Initializing objects
        LoginPage loginPage = new LoginPage(driver);
        LandingPage landPage = new LandingPage(driver);
        CasePage casePage = new CasePage(driver);

        //Login to salesforce as service agent and switch to lightning (if not present by default)
        loginPage.loginToSalesforce(ProfileConstant.serviceAgent);

        //Switch to service cloud and navigate to case tab (if not present by default)
        landPage.appLauncher(AppLauncherConstant.serviceCloud);
        landPage.selectTabName(CaseConstant.caseTab);

        //Create case and save
        casePage.createCase(caseOrigVal, caseSubjVal);

        //Validate case is created successfully via toast message
        String message = casePage.caseToastMessage();
        Assert.assertTrue(message.contains(CaseConstant.caseCreationMessage), "Issue in case creation: success message failure");

        String caseNumber = casePage.currentCaseTab();

        /*Assertions*/
        //(+ve) Validate subject is not blank and case detail page is displayed
        if ((caseOrigVal.equals(CaseConstant.phone) && ((caseSubjVal == null || caseSubjVal.trim().isEmpty())))) {
            casePage.caseDefaultSubjectAutopopulate(CaseConstant.caseSubjectDefault, true, caseNumber);
            Assert.assertTrue(casePage.isCaseDetailPageDisplayed(), "Case detail page is not displayed despite correct inputs.\nCase Origin is 'Phone' && Subject is blank.\nTest Data: " + caseNumber);
        }
        //(-ve) Validate subject is blank and case detail page is not displayed
        else {
            SoftAssert softAssert;
            if (!(caseOrigVal.equals(CaseConstant.phone) || ((caseSubjVal == null || caseSubjVal.trim().isEmpty())))) {
                softAssert = casePage.caseDefaultSubjectAutopopulate(caseSubjVal, false, caseNumber);
                softAssert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page should not be displayed. To be displayed only if Case Origin input value = 'Phone'. Found: " + caseOrigVal + " && Subject = blank. Found:"+"'"+caseSubjVal+"'\n Test data: "+caseNumber);
            } else if (!(caseOrigVal.contentEquals(CaseConstant.phone))) {
                softAssert = casePage.caseDefaultSubjectAutopopulate(caseSubjVal, false, caseNumber);
                softAssert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page should not be displayed. Case Origin input value should be 'Phone'. Found:"+"'"+caseOrigVal+"'\n Test data: "+caseNumber);
            } else {
                softAssert = casePage.caseDefaultSubjectAutopopulate(caseSubjVal, false, caseNumber);
                softAssert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page should not be displayed. Subject input value should be blank. Found:"+"'"+caseSubjVal+"'\n Test data: "+caseNumber);
            }
            softAssert.assertAll();
        }
    }

    /*
    User Story: Case Management for Service Agents
    Description: A default Subject should always be assigned to the Case (AC #2)
               : Checks for case EDIT scenarios
    Test Method Name: defaultSubjectAssignmentToCaseEditTest()
    */
    @Test(priority = 4, dataProvider = "caseDefaultAssignmentEdit", dataProviderClass = GetData.class)
    public void defaultSubjectAssignmentToCaseEditTest(String caseOrigVal, String caseSubjVal, String changeOriginValue, String changeSubjValue) {
        //Initializing objects
        LoginPage loginPage = new LoginPage(driver);
        LandingPage landPage = new LandingPage(driver);
        CasePage casePage = new CasePage(driver);

        //Login to salesforce as service agent and switch to lightning (if not present by default)
        loginPage.loginToSalesforce(ProfileConstant.serviceAgent);

        //Switch to service cloud and navigate to case tab (if not present by default)
        landPage.appLauncher(AppLauncherConstant.serviceCloud);
        landPage.selectTabName(CaseConstant.caseTab);

        //Create case and save
        casePage.createCase(caseOrigVal, caseSubjVal);

        //Validate case is created successfully via toast message
        String message = casePage.caseToastMessage();
        Assert.assertTrue(message.contains(CaseConstant.caseCreationMessage), "Issue in case creation: success message failure");

        //Edit case
        String caseNumber = casePage.currentCaseTab();
        String caseURL = driver.getCurrentUrl();
        casePage.editCase(caseURL, true, changeOriginValue, changeSubjValue);

        String caseOriginValue = driver.findElement(By.xpath(String.format(CasePage.fieldValueOnCaseDetail, CaseConstant.caseOriginField))).getText();
        String caseSubjectValue = driver.findElement(By.xpath(String.format(CasePage.fieldValueOnCaseDetail, CaseConstant.caseSubjectField))).getText();

        /*Assertions*/
        //(+ve) Validate subject is not blank and case detail page is displayed
        if ((changeOriginValue.equals(CaseConstant.phone) && ((changeSubjValue == null || changeSubjValue.trim().isEmpty())))) {
            casePage.caseDefaultSubjectAutopopulate(changeSubjValue, true, caseNumber);
            Assert.assertTrue(casePage.isCaseDetailPageDisplayed(), "Case detail page is not displayed despite correct inputs.\nCase Origin is 'Phone' && Subject is blank." + caseNumber);
        }
        //(-ve) Validate subject is blank and case detail page is not displayed
        else {
            SoftAssert softAssert;
            if (!(changeOriginValue.equals(CaseConstant.phone) || ((changeSubjValue == null || changeSubjValue.trim().isEmpty())))) {
                softAssert = casePage.caseDefaultSubjectAutopopulate(changeSubjValue, false, caseNumber);
                softAssert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page should not be displayed. To be displayed only if Case Origin = 'Phone'. Found: " + caseOriginValue + " && Subject = blank. Found:"+"'"+caseSubjectValue+"'");
            } else if (!(changeOriginValue.contentEquals(CaseConstant.phone))) {
                softAssert = casePage.caseDefaultSubjectAutopopulate(changeSubjValue, false, caseNumber);
                softAssert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page should not be displayed. Case Origin should be 'Phone'. Found:"+"'"+caseOriginValue+"'");
            } else {
                softAssert = casePage.caseDefaultSubjectAutopopulate(changeSubjValue, false, caseNumber);
                softAssert.assertFalse(casePage.isCaseDetailPageDisplayed(), "Case detail page should not be displayed. Subject should be blank. Found:"+"'"+caseSubjectValue+"'");
            }
            softAssert.assertAll();
        }

    }

    /*
    User Story: Case Management for Service Agents
    Description: Service Agent Should not Delete Cases (AC #3)
    Test Method Name: caseNonDeletionTest()
    */
    @Test(priority = 5)
    public void caseNonDeletionTest() {
        //Initializing objects
        LoginPage loginPage = new LoginPage(driver);
        LandingPage landPage = new LandingPage(driver);
        CasePage casePage = new CasePage(driver);

        //Login to salesforce as service agent and switch to lightning (if not present by default)
        loginPage.loginToSalesforce(ProfileConstant.serviceAgent);

        //Switch to service cloud and navigate to case tab (if not present by default)
        landPage.appLauncher(AppLauncherConstant.serviceCloud);
        landPage.selectTabName(CaseConstant.caseTab);

        /*Assertions*/
        //Validate case cannot be deleted from case list view
        String errorMessageListView = casePage.caseListViewDeletion();
        Assert.assertEquals(errorMessageListView, CaseConstant.caseDeletionMessage,"Case deletion error message mismatch.\nExpected is: " + CaseConstant.caseDeletionMessage + "\nFound: " + errorMessageListView);

        //Validate case cannot be deleted from case record level
        String errorMessageRecordLevel = casePage.caseRecordDeletion();
        Assert.assertEquals(errorMessageRecordLevel, CaseConstant.caseDeletionMessage,"Case deletion error message mismatch.\nExpected is: " + CaseConstant.caseDeletionMessage + "\nFound: " + errorMessageRecordLevel);
    }

    @AfterTest
    public void quit() {
        // Close all the browser instances
        driver.quit();
    }
}
