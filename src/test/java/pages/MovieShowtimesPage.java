package pages;

import core.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class MovieShowtimesPage extends BasePage {

    // Page load / header
    private final By movieHeader = By.xpath("//div[@id='toScrollClientHeight']//h1[contains(@class,'largeHeading')]");
    // Theatres list
    private final By theatreNameCards = By.xpath("//div[@class='MovieSessionsListing_titleFlex__mE_KX']");
    private final By theatreClickableName = By.xpath("//div[@class='MovieSessionsListing_titleFlex__mE_KX']//a[contains(text(),'Chennai')]");
    private final By viewTheatreDetails = By.xpath("//button//span[text()='View all movies playing here']");

    // Date strip (today/tomorrow)
    private final By dateStrip = By.xpath("//div[@class='DatesMobileV2_datesMonthWrap__uhFLu']");

    // Showtimes container
    private final By showtimeButtons = By.xpath("//div[contains(@class,'timeblock')]//parent::div");
    private final By showtimesContainer = By.xpath("//li[contains(@class,'MovieSessionsListing_movieSessions__c4gaO')]//parent::ul");

    // Filters
    private final By filterAppliedChip = By.xpath("//*[contains(@class,'chip') or contains(@class,'tag')][.//text()]");
    private final By langContinue = By.xpath("//button//span[text()='Proceed']");
    private final By langSelect = By.xpath("//li[@aria-label='filter']//following-sibling::li[1]");
    private final By language = By.xpath("//li[@aria-label='filter']//following-sibling::li[1]//div");
    private final By filtersButton = By.xpath("//*[text()='Filters']");
    private final By showtimeFilter = By.xpath("//div[text()='Show Time']");
    private final By distance = By.xpath("//div[contains(text(),'Distance')]");
    private final By distanceFilter = By.xpath("//span[contains(text(),'Distance')]");
    private final By viewShowsButton = By.xpath("//*[@data-testid='button' and contains(@aria-label,'shows')]");

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(movieHeader),
                ExpectedConditions.presenceOfElementLocated(theatreNameCards)
            ));
            return true;
        } catch (TimeoutException e) { return false; }
    }

    public boolean areTheatresListed() {
        waitVisible(showtimesContainer);
        return driver.findElements(theatreNameCards).size() > 0;
    }

    public TheatreDetailsPage openTheatreByIndex(int oneBasedIndex) {
        
        try {
            List<WebElement> cards = driver.findElements(theatreClickableName);
            cards.get(0).click();
        } catch (Exception e) {
            // fallback: click first clickable name on page
            click(theatreClickableName);
            click(viewTheatreDetails);
        }
        return new TheatreDetailsPage();
    }

    public boolean changeDate(String relative) {
        if (!isPresent(dateStrip)) return false;

        int day = getTargetDayOfMonth(relative);

        // Find clickable pills inside the strip
        String stripXpath = dateStrip.toString().replace("By.xpath: ", "").trim();

        // Common clickable elements in strips
        By pill = By.xpath(stripXpath + "//a//div//div[text()='" + day + "']");

        try {
            // If your framework has wait, use it instead of sleep
            click(pill);
        } catch (Exception e) {
            // Fallback: try clicking the container itself if pills are wrapped
            System.out.println(e.getMessage());
        }

        // Prefer explicit wait instead of sleep if possible
        wait.withTimeout(Duration.ofSeconds(5));

        return showtimesVisible();
    }

    private int parseOffsetDays(String relative) {
        if (relative == null) return 0;
        String r = relative.trim().toLowerCase();

        if (r.equals("today")) return 0;
        if (r.equals("tomorrow")) return 1;

        if (r.matches("^\\+\\d+$")) return Integer.parseInt(r.substring(1));
        if (r.matches("^day\\+\\d+$")) return Integer.parseInt(r.substring(4));
        if (r.matches("^\\d+$")) return Integer.parseInt(r);

        return 0;
    }

    public int getTargetDayOfMonth(String relative) {
        int offset = parseOffsetDays(relative);
        return LocalDate.now().plusDays(offset).getDayOfMonth();
    }

    public boolean showtimesVisible() {
        try {
            wait.withTimeout(Duration.ofSeconds(6));
            return driver.findElements(showtimeButtons).size() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean applyFilter(String type, String value) {
        try {
            // Open filter panel if a toggle exists
//            if (isPresent(filtersToggleBtn)) {
//                click(filtersToggleBtn);
//            }

            // Some UIs show inline filters; proceed anyway.

            By option;

            String t = type == null ? "" : type.toLowerCase().trim();
//            String vLower = value == null ? "" : value.toLowerCase().trim();

            if (t.equals("language")) {
                option = By.xpath(
                    "//div[contains(@class,'RadioButton_radioButtonContainer')]//input"
                );
                click(langSelect);
                
                List<WebElement> lis = driver.findElements(option);
                int found = 0;
                
                for(WebElement li : lis) {
                	String text = li.getAttribute("value");
                	System.out.println("in for " + text);
                	
                	if(text.equalsIgnoreCase(value)) {
                		found = 1;
                		safeClickJS(li);;
                		System.out.println("clicked inside if");
                		break;
                	}
                }
                
                if(found == 0) click(option);
                System.out.println("found :" + found);
                click(langContinue);

            } else if (t.equals("show time") || t.equals("showtime")) {
            	click(filtersButton);
                click(showtimeFilter);
                List<WebElement> options = driver.findElements(By.cssSelector(
                    "div:not(.CreateFilters_disabledFilter__UnZUg) input[type='checkbox']"
                ));
//                for(WebElement selectOpt : options) {
//                	safeClickJS(selectOpt);
//                	if(selectOpt.isSelected()) {
//                		break;
//                	}
//                }
                //for now
                safeClickJS(options.get(options.size()-1));
            } else if (t.equals("distance")) {
            	click(filtersButton);
                List<WebElement> options = driver.findElements(distanceFilter);
                for(WebElement selectOpt : options) {
                	safeClickJS(selectOpt);
                	if(selectOpt.isSelected()) {
                		break;
                	}
                }
            } 
            else {
                throw new IllegalArgumentException("Unknown filter type: " + type);
            }

            // Close panel if a close/apply exists (optional)
            try {
                
                if (isPresent(viewShowsButton)) {
                    click(viewShowsButton);
                }
            } catch (Exception ignored) {}

            // Small wait for refresh (better to replace with explicit wait on loader)
            try { Thread.sleep(900); } catch (InterruptedException ignored) {}

            return showtimesVisible();

        } catch (Exception e) {
            e.printStackTrace(); // helpful for debugging
            return false;
        }
    }


    public boolean filteredShowtimesMatch(String type, String value) {
        // Heuristic validation:
        // 1) A chip/tag for the filter appears OR
        // 2) At least one showtime is visible (post-filter) and no "empty" UI
    	
    	if(type.equalsIgnoreCase("language")) {
    		String text = waitVisible(language).getText();
    		System.out.println(text);
    		return text.equalsIgnoreCase(value);
    	} else if(type.equalsIgnoreCase("Show Time")) {
    		By path = By.xpath("//div[text()='" + value + "']");
    		System.out.println(path);
    		waitVisible(path);
    		return isPresent(path);
    	} else if(type.equalsIgnoreCase("distance")) {
    		return isPresent(distance);
    	} else {
    		return false;
    	}
    }
    
    public java.util.List<String> getTheatreNames(int max) {
        java.util.List<String> names = new java.util.ArrayList<>();
        java.util.List<org.openqa.selenium.WebElement> cards = driver.findElements(theatreNameCards);
        int limit = Math.min(max, cards.size());
        for (int i = 0; i < limit; i++) {
            String t = cards.get(i).getText();
            if (t != null && !t.trim().isEmpty()) names.add(t.trim());
        }
        return names;
    }
}
