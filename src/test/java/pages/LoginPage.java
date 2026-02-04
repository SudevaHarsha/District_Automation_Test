package pages;

import core.BasePage;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

    private final By mobileInput = By.xpath("//input[@name='mobileNumber']");
    private final By continueBtn = By.xpath("//button[contains(text(),'Continue')]");
//    private final By otpText = By.xpath("//*[contains(text(),'OTP')]");
    private final By errorPhoneMsg = By.xpath("//p[contains(text(),'valid')]");
    private final By errorOtpMsg = By.xpath("//p[contains(text(),'incorrect')]");
    private final By otpSubmit = By.xpath("//span[text()='Continue']//parent::button");
    private final By phoneNum = By.xpath("//span[contains(text(),'+91')]");
    private final By logoutButton = By.xpath("//span[text()='Logout']");
    
    private final By otpBoxes = By.xpath("//input[contains(@aria-label,'digit')]");

    public void openSite() {
        driver.get("https://www.district.in/movies");
    }

    public void enterMobile(String mobile) {
        type(mobileInput, mobile);
    }

    public void clickContinue() {
        click(continueBtn);
    }
    
    public void clickLogout() {
        click(logoutButton);
    }
    
    public void enterOtp() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public boolean isPhoneDisplayed() {
        return isPresent(phoneNum);
    }

    public boolean isErrorOtpDisplayed() {
    	List<WebElement> boxes = driver.findElements(otpBoxes);
    	for(WebElement box : boxes) {
    		box.sendKeys("9");
    	}
    	WebElement submit = waitClickable(otpSubmit);
		safeClickJS(submit);
        return isPresent(errorOtpMsg);
    }
    
    public boolean isErrorDisplayed() {
        return isPresent(errorPhoneMsg);
    }
}
