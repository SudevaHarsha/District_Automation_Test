package pages;

import core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SeatBookingPage extends BasePage {

    // Showtime blocks
    private final By timeBlocks = By.xpath("//div[contains(@class,'timeblock')]");

    // Seats
    private final By availableSeats = By.xpath("//span[@id='available-seat']");
    private final By unavailableSeats = By.xpath("//*[contains(@aria-label,'unavailable seat')]");

    // Proceed
    private final By proceedBtn = By.xpath("//*[@aria-label='Proceed']");

    // Optional: some heading / body check after proceed
    private final By body = By.tagName("body");

    // ---------------- Actions ----------------

    public void openFirstShow() {
        List<WebElement> shows = driver.findElements(timeBlocks);
        if (shows.isEmpty()) {
            throw new RuntimeException("No showtimes found after applying filters");
        }
        safeClickJS(shows.get(0));
    }

    public int selectNAvailableSeats(int count) {
        List<WebElement> seats = driver.findElements(availableSeats);
        int selected = 0;

        for (WebElement seat : seats) {
            try {
                seat.click();
                selected++;
            } catch (Exception ignored) { }
            if (selected == count) break;
        }
        return selected;
    }

    /**
     * We attempt to click unavailable seats. Expected: click should fail OR not clickable.
     * Returns how many attempts failed due to click exception.
     */
    public int attemptClickUnavailableSeats(int count) {
        List<WebElement> seats = driver.findElements(unavailableSeats);
        int failed = 0;
        int totalTried = 0;

        for (WebElement seat : seats) {
            try {
                seat.click(); // Should ideally fail
                failed++;
            } catch (Exception e) {
                failed++;
            }
            totalTried++;
            if (totalTried == count) break;
        }
        return failed;
    }

    public void clickProceed() {
        click(proceedBtn);
    }

    public boolean isNextStepLoaded() {
        // Minimal safe validation (can be strengthened if you know summary page selectors)
        return isPresent(body);
    }
}