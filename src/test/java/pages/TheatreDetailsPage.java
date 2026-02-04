package pages;

import core.BasePage;
import org.openqa.selenium.*;

import java.util.List;

public class TheatreDetailsPage extends BasePage {

    private final By theatreName = By.xpath("//*[self::h1 or self::h2][contains(@class,'MovieDetailWidget_movieName__3hIYZ')]");
    private final By theatreAddress = By.xpath("//*[self::h1 or self::h2][contains(@class,'MovieDetailWidget_movieName__3hIYZ')]//ancestor::div[2]//div[@class='MovieDetailWidget_subHeading__PeHSJ']//span");
    private final By cancellationPolicy = By.xpath(
            "//div[contains(@class,'CinemaAmenities_amenity')]//div[contains(text(),'cancellation')]"
    );
    private final By amenitiesSection = By.xpath("//h2[contains(text(),'amenities')]//parent::div");
    private final By amenitiesItems = By.xpath("//h2[contains(text(),'amenities')]//following-sibling::div//div[contains(@class,'CinemaAmenities_amenity')]");

    public boolean isLoaded() {
        try {
            waitVisible(theatreName);
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean hasNameAndAddress() {
        boolean hasName = isPresent(theatreName);
        boolean hasAddress = isPresent(theatreAddress);
        return hasName && hasAddress;
    }

    public boolean cancellationPolicyDisplayed() {
        return isPresent(cancellationPolicy);
    }

    public boolean amenitiesPresent() {
        if (!isPresent(amenitiesSection)) return false;
        List<WebElement> items = driver.findElements(amenitiesItems);
        return items != null && !items.isEmpty();
    }
}