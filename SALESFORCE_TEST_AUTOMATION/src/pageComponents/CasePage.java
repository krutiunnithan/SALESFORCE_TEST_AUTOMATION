package pageComponents;

import constants.CaseConstant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.Random;

public class CasePage {
    WebDriver driver;
    WebDriverWait wait;
    Actions action;
    JavascriptExecutor js;
    Random rand = new Random();

    //Constructor
    public CasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        action = new Actions(driver);
        js = (JavascriptExecutor) driver;
    }

    //Locators
    public static By newButton = By.xpath("//div[@title='New']");
    public static String caseTitledDropDown = "//span[text()='Case Origin']/../following-sibling::div//a";
    public static String caseTitledDropDownValue = "//div[@class='select-options']/ul/li/a[text()='%s']";
    public static By caseSubjectInput = By.xpath("//span[text()='Subject']/../following-sibling::input");
    public static String accessButton = "//button[@title='%s']";
    public static By caseDetailSection = By.xpath("(//div[@class='slds-tabs_default']//a[text()='Details'])[last()]");
    public static String fieldValueOnCaseDetail = "//span[text()='%s']/../following-sibling::div//lightning-formatted-text";
    public static By caseMessage = By.xpath("//span[contains(@class, 'toastMessage')]");
    public static By caseSubjectOnHeader = By.xpath("//div[text()='Case']/..//lightning-formatted-text");
    public static String caseSubjectUnderAllUpdates = "//span[@class='cuf-rsFieldLabel']/span[text()='%s']/..//following-sibling::span[@class='cuf-rsFieldValue']";
    public static By caseRecord = By.xpath("//span[@title='Case Number']//ancestor::thead/following-sibling::tbody//th//a");
    public static String caseRecordActionDropdown = "(//table[@data-aura-class='uiVirtualDataTable']//tbody/tr[%d]/td)[last()]";
    public static String actionsDropdownOption = "//a[@title='%s']";
    public static String caseDeleteConfirmBoxOption = "//button[@title='%s']";
    public static By caseDeleteError = By.xpath("//*[@class='detail slds-text-align--center']");
    public static By caseTableRows = By.xpath("//table[@data-aura-class='uiVirtualDataTable']//tbody/tr");
    public static By delButton = By.xpath("//button[@name='Delete']");
    public static String caseRecordNum = "//span[@title='Case Number']//ancestor::thead/following-sibling::tbody//th//a[text()='%s']";
    public static By editButton = By.xpath("//button[@name='Edit']");
    public static By delCaseBox = By.xpath("//h2[text()='Delete Case']");

    //Methods
    /*
    Description: Clicks on New button
    */
    public void clickNewButton() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(newButton));
            driver.findElement(newButton).click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    Description: Selects value from 'Case Origin' field drop down
    Parameter:   value - accepts drop down value such as 'Phone'
    */
    public void selectValueFromCaseOrigin(String value) {
        try {
            js.executeScript("arguments[0].click();", driver.findElement(By.xpath(String.format(caseTitledDropDown, "Case Origin"))));
            driver.findElement(By.xpath(String.format(caseTitledDropDownValue, value))).click();
            if(!(value.equalsIgnoreCase("Email") || value.equalsIgnoreCase("Web") || value.equalsIgnoreCase("--None--")  || value.equalsIgnoreCase("None"))) {
                String val = driver.findElement(By.xpath(caseTitledDropDown)).getText();
                if (!(val.contentEquals("Phone"))) {
                    Assert.fail("Input case origin value and its picklist value must be 'Phone'");
                }
            }
        }
        catch (Exception e){
            if(!(value.equalsIgnoreCase("Email") || value.equalsIgnoreCase("Web") || value.equalsIgnoreCase("--None--") || value.equalsIgnoreCase("None"))) {
                if (value.contentEquals("Phone")) {
                    Assert.fail("Picklist of Case Origin must contain 'Phone'");
                } else {
                    Assert.fail("Case Origin value to be passed must be 'Phone'. Found: " + value);
                }
            }
            else{
                e.printStackTrace();
            }
        }
    }

    /*
    Description: Enters case subject
    Parameter:   value - accepts value such as 'Password Request'
    */
    public void enterCaseSubject(String value) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(caseSubjectInput));
            driver.findElement(caseSubjectInput).sendKeys(value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Description: Validates if case detail page is displayed
    return:      boolean value of true/false based on case detail page display
    */
    public boolean isCaseDetailPageDisplayed() {
        boolean flag = false;
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
            if (driver.findElement(caseDetailSection).isDisplayed()){
                flag = true;
            }
        }
        catch (Exception e){
            flag = false;
        }
        return flag;
    }

    /*
    Description: Fetches case toast message
    return:      returns string value of toast message
    */
    public String caseToastMessage() {
        String[] msg = null;
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(caseMessage));
            String message = driver.findElement(caseMessage).getText();
            msg = message.split(" ");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert msg != null;
        return msg[0] + " " + msg[2] + " " + msg[3];
    }

    /*
    Description: Fetches and validates auto populated value for case subject
    Parameter:   subValue - case subject value
                 flag - true/false
                 caseNumber - case record number
    return:      returns softAssert object
    */
    public SoftAssert caseDefaultSubjectAutopopulate(String subValue, boolean flag, String caseNumber) {
        SoftAssert softAssert = new SoftAssert();
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            String caseSubOnDetailPage = driver.findElement(By.xpath(String.format(fieldValueOnCaseDetail, "Subject"))).getText();
            String caseSubOnHeader = driver.findElement(caseSubjectOnHeader).getText();
            String caseSubUnderAllUpdates = driver.findElement(By.xpath(String.format(caseSubjectUnderAllUpdates, "Subject"))).getText();
            String[] subjectValues = {caseSubOnDetailPage, caseSubOnHeader, caseSubUnderAllUpdates};
            int count = 0;
            for (String subjectValue : subjectValues) {
                  if (!flag){
                      count++;
                      String location = null;
                      if (count == 1) {
                          location = "AT CASE DETAIL SECTION:";
                      }
                      else if(count == 2){
                          location = "AT CASE HEADER SECTION:";
                      }
                      else if(count == 3){
                          location = "AT CASE ALL UPDATES SECTION:";
                      }
                      if (subjectValue.isEmpty()){
                          softAssert.assertTrue(true);
                      }
                      else {
                          String caseOriginValue = driver.findElement(By.xpath(String.format(CasePage.fieldValueOnCaseDetail, CaseConstant.caseOriginField))).getText();
                          if (!(caseOriginValue.equals(CaseConstant.phone)) && subValue != null && !(subValue.equals(""))) {
                              softAssert.fail("\n"+location+"\nCase Number: " + caseNumber + "\nDefault case subject must be blank. Found: " +"'"+subjectValue+"'"+ "\nCase origin field must be 'Phone'. Found: " + caseOriginValue + "\nSubject field on case must be blank. Found: "+"'"+subValue+"'");
                          }
                          else if (!caseOriginValue.equals(CaseConstant.phone)){
                              softAssert.fail("\n"+location+"\nCase Number: " + caseNumber + "\nDefault case subject must be blank. Found: " +"'"+subjectValue+"'"+ "\nCase origin field must be 'Phone'. Found: " + caseOriginValue);
                          }
                          else {
                              softAssert.fail("\n"+location+"\nCase Number: " + caseNumber + "\nDefault case subject must be blank. Found: " +"'"+subjectValue+"'"+ "\nSubject field on case must be blank. Found: " +"'"+subValue+"'");
                          }
                      }
                  }
                  else {
                      count++;
                      String location = null;
                      if (count == 1) {
                          location = "AT CASE DETAIL SECTION:";
                      }
                      else if(count == 2){
                          location = "AT CASE HEADER SECTION:";
                      }
                      else if(count == 3){
                          location = "AT CASE ALL UPDATES SECTION:";
                      }
                      if (subjectValue.isEmpty()){
                          Assert.fail("\n"+location+"\nCase Number: " + caseNumber + "\nDefault case subject is blank");
                      }
                      else {
                          String caseOriginValue = driver.findElement(By.xpath(String.format(CasePage.fieldValueOnCaseDetail, CaseConstant.caseSubjectField))).getText();
                          Assert.assertEquals(subjectValue, CaseConstant.caseSubjectDefault, "\n"+location+"\nCase Number: " + caseNumber + "\nCase subject mismatch \nExpected: " + CaseConstant.caseSubjectDefault + "\nFound: " +"'"+subjectValue+"'");
                      }
                  }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return softAssert;
    }

    /*
    Description: Deletes a case from list view -> action drop down
    return:      Error message
    */
    public String caseListViewDeletion() {
        String errorMsg = null;
        try {
            int rowCount = driver.findElements(caseTableRows).size();
            if (rowCount == 0) {
                Assert.fail("No case found to perform deletion");
            } else {
                int num = rand.nextInt(rowCount - 1);
                if (num == 0) {num++;}

                String caseNum = driver.findElements(caseRecord).get(num).getText();

                driver.findElement(By.xpath(String.format(caseRecordActionDropdown, num))).click();
                driver.findElement(By.xpath(String.format(actionsDropdownOption, "Delete"))).click();

                wait.until(ExpectedConditions.visibilityOfElementLocated(delCaseBox));
                Assert.assertTrue(driver.findElement(delCaseBox).isDisplayed(),"Case delete confirmation popup is not displayed");
                Assert.assertTrue(driver.findElement(By.xpath(String.format(caseDeleteConfirmBoxOption, "Delete"))).isDisplayed(),"Delete button on Case delete confirmation popup is not displayed");
                WebElement confirmBox = driver.findElement(By.xpath(String.format(caseDeleteConfirmBoxOption, "Delete")));
                confirmBox.click();

                wait.until(ExpectedConditions.visibilityOfElementLocated(caseDeleteError));
                Assert.assertTrue(driver.findElement(caseDeleteError).isDisplayed(),"Case delete error popup is not displayed");
                errorMsg = driver.findElement(caseDeleteError).getText();

                driver.findElement(By.xpath(String.format(accessButton, "Close this window"))).click();
                Assert.assertTrue(driver.findElement(By.xpath(String.format(caseRecordNum, caseNum))).isDisplayed(), "Case is deleted" + caseNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorMsg;
    }

    /*
    Description: Deletes a case from record level
    return:      Error message
    */
    public String caseRecordDeletion() {
        String errorMsg = null;
        try {
            int rowCount = driver.findElements(caseTableRows).size();
            if (rowCount == 0) {
                Assert.fail("No case found to perform deletion");
            } else {
                int num = rand.nextInt(rowCount - 1);
                if (num == 0) {num++;}

                String caseNum = driver.findElements(caseRecord).get(num).getText();
                wait.until(ExpectedConditions.elementToBeClickable(caseRecord));
                driver.findElements(caseRecord).get(num).click();
                js.executeScript("arguments[0].click();", driver.findElement(delButton));

                wait.until(ExpectedConditions.visibilityOfElementLocated(delCaseBox));
                Assert.assertTrue(driver.findElement(delCaseBox).isDisplayed(),"Case delete confirmation popup is not displayed");
                Assert.assertTrue(driver.findElement(By.xpath(String.format(caseDeleteConfirmBoxOption, "Delete"))).isDisplayed(),"Delete button on Case delete confirmation popup is not displayed");
                WebElement confirmBox = driver.findElement(By.xpath(String.format(caseDeleteConfirmBoxOption, "Delete")));
                confirmBox.click();

                wait.until(ExpectedConditions.visibilityOfElementLocated(caseDeleteError));
                Assert.assertTrue(driver.findElement(caseDeleteError).isDisplayed(),"Case delete error popup is not displayed");
                errorMsg = driver.findElement(caseDeleteError).getText();

                driver.findElement(By.xpath(String.format(accessButton, "Close this window"))).click();
                driver.navigate().refresh();
                String caseNumber = driver.findElement(By.xpath(String.format(fieldValueOnCaseDetail, "Case Number")+"[last()]")).getText();
                Assert.assertEquals(caseNumber, caseNum,"Case is deleted" + caseNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorMsg;
    }

    /*
    Description: Clicks on current case tab
    return:      Case number
    */
    public String currentCaseTab() {
        String caseNum = null;
        try {
            driver.navigate().refresh();
            caseNum = driver.findElement(By.xpath(String.format(fieldValueOnCaseDetail, "Case Number") + "[last()]")).getText();
            driver.findElement(By.xpath(String.format(actionsDropdownOption, caseNum))).click();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseNum;
    }

    /*
    Description: Edits case
    Parameter:   caseURL - URL of case to edit
                 flag - true/false
                 subValue - Text to enter
                 originVal - Case origin picklist value
    */
    public void editCase(String caseURL, boolean flag, String originVal, String subValue) {
        try {
            driver.get(caseURL);
            Thread.sleep(9000);
            driver.findElement(editButton).click();
            selectValueFromCaseOrigin(originVal);
            driver.findElement(caseSubjectInput).clear();
            if (flag) {
                driver.findElement(caseSubjectInput).sendKeys(subValue);
            }
            driver.findElement(By.xpath(String.format(accessButton, "Save"))).click();
            if (originVal.contentEquals("--None--") || originVal.equalsIgnoreCase("None") || originVal.contentEquals("--none--")) {
                if (driver.findElement(By.xpath("//li[contains(text(),'Case Origin')]")).getText().equalsIgnoreCase(CaseConstant.caseOriginError)) {
                    Assert.fail("\nCase save failed during case edit\nCase Origin field value is mandatory. Select a value other than --None--");
                }
            } else {
                wait.until(ExpectedConditions.visibilityOfElementLocated(caseDetailSection));
                Thread.sleep(9000);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    Description: Creates case
    Parameter:   subValue - Text to enter
                 originVal - Case origin picklist value
    */
    public void createCase(String originVal, String subValue) {
        try {
            clickNewButton();
            selectValueFromCaseOrigin(originVal);
            enterCaseSubject(subValue);
            driver.findElement(By.xpath(String.format(accessButton, "Save"))).click();
            if (originVal.contentEquals("--None--") || originVal.equalsIgnoreCase("None") || originVal.contentEquals("--none--")) {
                if (driver.findElement(By.xpath("//li[contains(text(),'Case Origin')]")).getText().equalsIgnoreCase(CaseConstant.caseOriginError)) {
                    Assert.fail("\nCase creation failed\nCase Origin field value is mandatory. Select a value other than --None--");
                }
            } else {
                wait.until(ExpectedConditions.visibilityOfElementLocated(caseDetailSection));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
