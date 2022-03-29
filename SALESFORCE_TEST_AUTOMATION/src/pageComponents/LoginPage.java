package pageComponents;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;


public class LoginPage {
    WebDriver driver;
    WebDriverWait wait;

    //Constructor
    public LoginPage(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    //Locators
    public static By usernameInput = By.id("username");
    public static By passwordInput = By.id("password");
    public static By loginButton = By.id("Login");

    //Methods
    /*
    Description: Reads the .properties file values
    Parameters:  Profile - accepts userName such as 'Service Agent'
                 fileName - accepts properties fileName such as 'loginData.properties'
    return:      String[] - returns string array with app url and login credentials
    */
    public String[] readPropertyFile(String profile, String fileName) throws IOException {
        String url = "", username = "", password = "";
        FileInputStream file = null;
        try {
            String propertyFilePath = "./src/resources/";
            fileName = propertyFilePath + fileName;
            file = new FileInputStream(fileName);
            Properties prop = new Properties();
            prop.load(file);
            url = prop.getProperty("loginURL");
            username = prop.getProperty(profile + "Username");
            password = prop.getProperty(profile + "Password");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert file != null;
            file.close();
        }
        return new String[]{url, username, password};
    }

    /*
    Description: Logs into salesforce.com based on given profile
                 Switches to lightning if classic is displayed
    Parameter:   Profile - accepts userName such as 'Service Agent'
    */
    public void loginToSalesforce(String profile) {
        try {
            String[] creds = readPropertyFile(profile, "loginData.properties");
            driver.get(creds[0]);
            driver.findElement(usernameInput).sendKeys(creds[1]);
            driver.findElement(passwordInput).sendKeys(creds[2]);
            driver.findElement(loginButton).click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            if(driver.findElements(LandingPage.viewProfileIcon).size() != 1){
                LandingPage landPage = new LandingPage(driver);
                landPage.switchToLightning();
            }
        }
        catch (IOException | TimeoutException e){
            e.printStackTrace();
        }
    }
}
