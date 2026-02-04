package steps;

import io.cucumber.java.en.*;

import java.util.List;

import org.testng.Assert;

import core.ScreenshotUtils;
import pages.HomePage;
import pages.MovieShowtimesPage;
import pages.TheatreDetailsPage;

public class TS03TheatresFiltersSteps {

    private final HomePage home = new HomePage();
    private MovieShowtimesPage showtimes;
    private TheatreDetailsPage theatrePage;
    private final ScreenshotUtils screenshotUtil = new ScreenshotUtils(); 

    private final core.ExcelResultsWriter resultsWriter =
        new core.ExcelResultsWriter("target/results/ui-observations.xlsx");

    @Given("open the District Movies website")
    public void openSite() { home.open(); }

    @And("I set city to {string}")
    public void setCity(String city) { home.selectCityBySearch(city); }

    @When("I search and open movie {string}")
    public void searchAndOpenMovie(String movie) {
        home.typeInSearch(movie);
        home.clickSuggestionContaining(movie);
        showtimes = new MovieShowtimesPage();
        Assert.assertTrue(showtimes.isLoaded(), "Movie showtimes page did not load");
    }

    @Then("theatres list should be visible")
    public void theatresShouldBeVisible() {
        Assert.assertTrue(showtimes.areTheatresListed(), "No theatres are listed for the movie");

        List<String> theatres = showtimes.getTheatreNames(20);
        screenshotUtil.takeScreenshot("theatres");
        resultsWriter.appendTheatreItems(
            /* TestCaseId */ "TC_TT_001",
            /* City       */ home.getDisplayedCity(),
            /* Movie      */ "Avatar Fire and Ash",
            /* Theatres   */ theatres
        );

    }

    @When("I open theatre number {string}")
    public void openTheatreNumber(String idxStr) {
        int index = Integer.parseInt(idxStr); // 1-based
        theatrePage = showtimes.openTheatreByIndex(index);
        Assert.assertTrue(theatrePage.isLoaded(), "Theatre details page didn't load");
    }

    @Then("theatre details page should show name and address")
    public void verifyTheatreDetails() {
        Assert.assertTrue(theatrePage.hasNameAndAddress(), "Theatre name/address missing");
    }

    @When("I change show date to {string}")
    public void iChangeShowDateTo(String relative) {
        Assert.assertTrue(showtimes.changeDate(relative), "Failed to change date to: " + relative);
    }

    @Then("showtimes should be visible for {string}")
    public void showtimesShouldBeVisibleFor(String relative) {
        Assert.assertTrue(showtimes.showtimesVisible(), "Showtimes not visible after changing date");
    }

    @Then("cancellation policy should be visible")
    public void cancellationPolicyShouldBeVisible() {
        Assert.assertTrue(theatrePage.cancellationPolicyDisplayed(),
                "Cancellation policy not displayed");
    }

    @Then("services and amenities should be loaded")
    public void amenitiesShouldBeLoaded() {
        Assert.assertTrue(theatrePage.amenitiesPresent(),
                "Amenities not loaded or blank");
    }

    @When("I apply filter type {string} with value {string}")
    public void applyFilter(String type, String value) {
        Assert.assertTrue(showtimes.applyFilter(type, value),
                "Failed to apply filter: " + type + " -> " + value);
    }

    @Then("filtered showtimes should match selection {string} {string}")
    public void verifyFilteredShowtimes(String type, String value) {
        Assert.assertTrue(showtimes.filteredShowtimesMatch(type, value),
                "Filtered showtimes do not reflect: " + type + " -> " + value);
    }
}
