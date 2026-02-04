package steps;

import io.cucumber.java.en.*;
import org.testng.Assert;

import core.ScreenshotUtils;
import pages.HomePage;
import pages.SeatBookingPage;

public class TS04SeatSelectionSteps {

    private SeatBookingPage seatPage;
    private HomePage home;
    private final ScreenshotUtils screenshotUtil = new ScreenshotUtils(); 

    @Given("District movie booking page {string} and {string}")
    public void openMovieBookingPage(String city, String movie) {
        home = new HomePage();
        home.open();
        home.selectCityBySearch("Chennai");
        home.typeInSearch(movie);
        home.clickSuggestionContaining(movie);
        seatPage = new SeatBookingPage();
    }

    @When("I open the first available showtime")
    public void openFirstShowtime() {
        seatPage.openFirstShow();
    }

    @Then("I should be able to select {string} available seats")
    public void selectAvailableSeats(String countStr) {
        int count = Integer.parseInt(countStr);
        int selected = seatPage.selectNAvailableSeats(count);
        screenshotUtil.takeScreenshot("seats");
        Assert.assertEquals(selected, count, "Could not select required available seats");
    }

    @Then("I should not be able to select {string} unavailable seats")
    public void validateUnavailableSeatsNotClickable(String countStr) {
        int count = Integer.parseInt(countStr);
        int failedClicks = seatPage.attemptClickUnavailableSeats(count);

        // If unavailable seats truly blocked, at least 1 click should fail
        Assert.assertTrue(failedClicks > 0,
                "Unavailable seats were clickable unexpectedly (no failures recorded).");
    }

    @When("I click Proceed")
    public void clickProceed() {
        seatPage.clickProceed();
    }

    @Then("I should reach booking summary or next step")
    public void verifyNextStep() {
        Assert.assertTrue(seatPage.isNextStepLoaded(),
                "Next step did not load after clicking Proceed");
    }
}