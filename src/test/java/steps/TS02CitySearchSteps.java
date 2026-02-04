
package steps;

import io.cucumber.java.en.*;

import java.util.List;

import org.testng.Assert;

import core.ScreenshotUtils;
import pages.HomePage;

public class TS02CitySearchSteps {

    private final HomePage home = new HomePage();
    private final ScreenshotUtils screenshotUtil = new ScreenshotUtils(); 

    private final core.ExcelResultsWriter writer =
        new core.ExcelResultsWriter("target/results/ui-observations.xlsx");

    // ---------- City ----------
    @Given("I the District Movies website")
    public void iOpenDistrictMovies() {
        home.open();
    }

    @When("I perform city selection using {string} and {string}")
    public void iPerformCitySelection(String method, String city) {
        switch (method) {
            case "display" : {
                // No action; just validate label later
            	String actual = home.getDisplayedCity();
            	Assert.assertTrue(
                        actual != null ,
                        "City is not displayed on the screen"
                );
            	break;
            }
            case "search" : 
            	home.selectCityBySearch(city);
            	break;
            case "searchError" : 
            	home.selectCity(city);
            	break;
            case "currentLocation" : 
            	home.useCurrentLocation();
            	break;
            case "popular" : 
            	home.selectCityFromPopularChennai();
            	break;
            case "allCities" : 
            	home.selectCityFromAllCitiesChennai();
            	break;
            default : 
            	throw new IllegalArgumentException("Unknown city method: " + method);
        }
    }

    @Then("the home page city should be {string} and {string}")
    public void iValidateCity(String method, String expected) {
        if ("No Results".equalsIgnoreCase(expected)) {
            Assert.assertTrue(home.hasNoCitySuggestions(),
                    "Expected NO city suggestions for invalid input");
        } else if(expected.isEmpty()) {
        	// Nothing
        } else if(method.equalsIgnoreCase("searchError")) {
        	// Nothing
        } else if(method.equalsIgnoreCase("currentLocation")) {
        	String actual = home.getDisplayedCitySubLabel();
            System.out.println(actual);

            Assert.assertTrue(
                    actual != null && actual.toLowerCase().contains(expected.toLowerCase()),
                    "City mismatch. Expected: " + expected + " | Actual: " + actual
            );
        } else {
            String actual = home.getDisplayedCity();
            System.out.println(actual);

            Assert.assertTrue(
                    actual != null && actual.toLowerCase().contains(expected.toLowerCase()),
                    "City mismatch. Expected: " + expected + " | Actual: " + actual
            );
        }
    }

    // ---------- Search ----------
    @Given("I am on the District Movies home page")
    public void iAmOnHome() {
        home.open();
    }

    @When("I validate search for {string}")
    public void iValidateSearchFor(String movie) {
        // Intentionally empty: we branch by expected outcome in the Then
        // (keeps step text clean and avoids duplicate logic).
    }

    @Then("search outcome for {string} should be {string}")
    public void searchOutcomeShouldBe(String movie, String expected) {
        switch (expected) {
            case "BAR" : {
                Assert.assertTrue(home.isSearchBarVisibleAndClickable(),
                        "Search bar is not visible/clickable");
                break;
            }
            case "SUGGEST" : {
                Assert.assertTrue(home.hasSuggestionsFor(movie),
                        "Expected suggestions for 'Avatar'");
                List<String> suggestions = home.getSuggestionTexts();
                screenshotUtil.takeScreenshot("suggestions");

				writer.appendSuggestionItems(
				            /* TestCaseId */ "TC_SF_08",         // or pass from Examples if you added it
				            /* City       */ home.getDisplayedCity(),
				            /* Search     */ movie,
				            /* Items      */ suggestions
				    );
                break;
            }
            case "RESULTS" : {
                home.typeInSearch("Avatar Fire and Ash");
                home.clickSuggestionContaining("Avatar Fire and Ash");
                Assert.assertTrue(home.resultsOrTheatresVisible(),
                        "Expected theatre/results to be visible");
                break;
            }
            case "NO_RESULT" : {
                home.typeInSearch("Avuty");
                home.pressEnterInSearch();
                Assert.assertTrue(home.hasNoResultsMessageFor("Avuty"),
                        "Expected 'no results' message for Avuty");
                break;
            }
            case "TRENDING" : {
                // Try clicking 'Trending' suggestion; fallback to the movie by name
                home.isSearchBarVisibleAndClickable();
                home.trendingClick();
                Assert.assertTrue(home.resultsOrTheatresVisible(),
                        "Expected theatres/results visible after trending selection");
                break;
            }
            default : throw new IllegalArgumentException("Unknown expected result: " + expected);
        }
    }
}
