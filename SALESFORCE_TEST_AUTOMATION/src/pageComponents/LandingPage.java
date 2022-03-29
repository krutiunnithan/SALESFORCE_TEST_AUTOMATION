package pageComponents;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class LandingPage {
    WebDriver driver;
    WebDriverWait wait;
    Actions action;

    //Constructor
    public LandingPage(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        action = new Actions(driver);
    }

    //Locators
    public static By viewProfileIcon = By.xpath("(//img[@title='User'])[1]");
    public static By switchVersionLink = By.className("switch-to-lightning");
    public static By appLauncherIcon = By.className("slds-icon-waffle");
    public static By searchAppLauncher = By.xpath("//input[@placeholder='Search apps and items...']");
    public static String tabNameText = "//a[@title='%s']";
    public static By tabDropdown = By.xpath("//button[@title='Show Navigation Menu']");
    public static String tabOptions = "//span[text()='%s']";
    public static String cloudName = "//span[@title='%s']";

    //Methods
    /*
    Description: Switches to lightning
    */
    public void switchToLightning() {
        try {
            driver.findElement(switchVersionLink).click();
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
            Assert.assertEquals(driver.findElements(LandingPage.viewProfileIcon).size(), 1);
            Thread.sleep(3000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Description: Switches to tab based on tabValue if not already present
    Parameter:   tabValue - accepts tab name such as 'Cases'
    */
    public void selectTabName(String tabValue) {
        try {
            driver.findElement(By.xpath(String.format(LandingPage.tabNameText, tabValue))).click();
            if (driver.findElements(By.xpath(String.format(LandingPage.tabNameText, tabValue))).size() != 1) {
                driver.findElement(tabDropdown).click();
                driver.findElement(By.xpath(String.format(LandingPage.tabOptions, tabValue))).click();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
    Description: Switches to app based on appName if not already present
    Parameter:   appName - accepts app name such as 'Service Cloud'
    */
    public void appLauncher(String appName) {
        try {
            if (driver.findElements(By.xpath(String.format(LandingPage.cloudName, appName))).size() != 1) {
                driver.findElement(appLauncherIcon).click();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.findElement(searchAppLauncher).sendKeys(appName);
                action.sendKeys(Keys.ENTER).build().perform();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
