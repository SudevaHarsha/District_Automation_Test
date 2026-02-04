
package steps;

import io.cucumber.java.en.*;
import org.testng.Assert;

import pages.HomePage;
import pages.LoginPage;

public class AuthenticationSteps {

    LoginPage login = new LoginPage();
    HomePage home = new HomePage();

    @Given("I open the District Movies website")
    public void openWebsite() {
        login.openSite();
        
        home.clickProfile();
    }

    @When("I enter mobile number {string}")
    public void enterMobile(String mobile) {
        login.enterMobile(mobile);
    }

    @When("I click continue")
    public void clickContinue() {
        login.clickContinue();
    }


    @Then("login result should be {string}")
    public void validateResult(String result) {

        switch (result) {
            case "OTP" :
//			login.enterOtp();
//			home.clickProfile();
//            Assert.assertTrue(login.isPhoneDisplayed(),
//                    "OTP screen not displayed");
//            login.clickLogout();
            break;
            case "INVALID OTP" :
//            		Assert.assertTrue(login.isErrorOtpDisplayed(),
//                        "Error message not displayed");
            		break;
            case "ERROR" :
        		Assert.assertTrue(login.isErrorDisplayed(),
                    "Error message not displayed");
        		break;
            case "INVALID" :
                    Assert.assertTrue(login.isErrorDisplayed(),
                            "Error message not displayed");
                    break;
            default :
                    Assert.fail("Invalid expected result: " + result);
        }
    }

}
