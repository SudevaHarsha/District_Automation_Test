package pages;

import core.BasePage;

import java.time.Duration;
import java.util.List;
//import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

    private final By profileBtn = By.xpath("//div[@aria-label='User Avatar']");
    
    // Common
    private final By body = By.tagName("body");

    // City label (top-left)
    private final By cityLabel = By.xpath(
        "//div[@id='master-header']//div//div[1]//div[1]//button//div//div[2]//span[1]"
    );
    
    private final By citySubLabel = By.xpath(
        "//div[@id='master-header']//div//div[1]//div[1]//button//div//div[2]//span[2]"
    );
    // City UI
    private final By cityChooserBtn = By.xpath(
        "//*[@id='master-header']//div//div[1]//div[1]//button"
    );
    private final By citySearchInput = By.xpath("//input[contains(@placeholder,'Search') and (contains(@placeholder,'city') or contains(@placeholder,'City'))]");
    private final By citySuggestionItems = By.xpath("//h5[text()='Select Location']//ancestor::div[2]//child::div[2]//div//button");
    private final By popularCityChennai = By.xpath("//h5[text()='Popular Cities']//following-sibling::div//div[5]");
    private final By allCitiesLetterC = By.xpath("//h5[text()='All Cities']//following-sibling::div[1]//button[3]");
    private final By allCitiesChennai = By.xpath("//h5[text()='All Cities']//following-sibling::div[2]//span[text()='Chennai']");
    private final By useCurrentLocationBtn = By.xpath("//span[text() = 'Use Current Location']");

    // Search
    private final By searchBar = By.xpath("//a[@href='/search']//*[normalize-space(text())='Search for events, movies and restaurants']");
    private final By searchInput = By.xpath("/html/body/div[4]/div/div/div/div/div[1]/div[1]/div/input");
    private final By suggestionContainer = By.cssSelector("div[style*='overscroll-behavior: none']");
    private final By suggestionItems = By.xpath("//span[text()='Movie']//preceding-sibling::h5");
    private final By noResultsMsg = By.xpath("//span[contains(text(),'Sorry')]");
    private final By theatresList = By.xpath("//li[contains(@class,'movieSessions')]");
    private final By langContinue = By.xpath("//button//span[text()='Proceed']");
    private final By trendingList = By.xpath("//span[text()='Trending in ']//following-sibling::div//div[1]");

    public void openSite() {
        driver.get("https://www.district.in/movies");
    }
    
    public void clickProfile() {
		click(profileBtn);
	}
    
    public void open() {
        driver.get("https://www.district.in/movies");
        waitVisible(body);
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // ----- City -----

    private void openCityChooser() {
        waitClickable(cityChooserBtn);
        safeClickJS(driver.findElement(cityChooserBtn));
    }
    
    public void selectCity(String city) {
    	openCityChooser();
        WebElement input = waitVisible(citySearchInput);
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.clear();
        input.sendKeys(city);
    }
    
    public void selectCityBySearch(String city) {
        selectCity(city);

        // Wait until any suggestion contains the target
        wait.until(d -> driver.findElements(citySuggestionItems).stream()
                .anyMatch(el -> {
                    String t = el.getText();
                    return t != null && t.toLowerCase().contains(city.toLowerCase());
                }));

        List<WebElement> items = driver.findElements(citySuggestionItems);

     // Assumes: List<WebElement> items; String city;
        WebElement target = items.get(0); // default to first item (same as orElse(items.get(0)))

        for (WebElement el : items) {
            String text = el.getText();
            if (text != null && city.equalsIgnoreCase(text.trim())) {
                target = el;
                break; // stop at the first match (same behavior as findFirst)
            }
        }

        WebElement oldHeader = driver.findElement(cityLabel);
        try {
            target.click();
        } catch (Exception e) {
            safeClickJS(target);
        }

        // Wait for re-render and final text
        try {
            wait.until(ExpectedConditions.stalenessOf(oldHeader));
        } catch (Exception ignored) { /* not always necessary */ }

        wait.until(ExpectedConditions.textToBePresentInElementLocated(cityLabel, city));
    }

    public String getDisplayedCity() {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(cityLabel));
            String text = el.getText();
            return text == null ? "" : text.trim();
        } catch (Exception e) {
            return "";
        }
    }
    
    public String getDisplayedCitySubLabel() {
        try {
        	wait.withTimeout(Duration.ofSeconds(15));
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(citySubLabel));
            System.out.println(el.getText());
            return el.getText().trim();
        } catch (Exception e) { return ""; }
    }

    public boolean hasNoCitySuggestions() {
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        return driver.findElements(citySuggestionItems).isEmpty();
    }

    public void selectCityFromPopularChennai() {
        openCityChooser();
        waitClickable(popularCityChennai).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(cityLabel, "Chennai"));
    }

    public void selectCityFromAllCitiesChennai() {
        openCityChooser();
        click(allCitiesLetterC);
        WebElement el = waitVisible(allCitiesChennai);
        scrollIntoView(el);
        safeClickJS(el);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(cityLabel, "Chennai"));
    }

    public void useCurrentLocation() {
        openCityChooser();
        waitVisible(useCurrentLocationBtn);
        click(useCurrentLocationBtn);
        String text = waitVisible(citySubLabel).getText();
        System.out.println(text);
        
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {};
    }
    
    // Returns suggestion texts after typing (ensures suggestion list rendered)
    public java.util.List<String> getSuggestionTexts() {
        java.util.List<String> out = new java.util.ArrayList<>();
        try {
            // Wait briefly for suggestions to populate
            Thread.sleep(600);
            java.util.List<org.openqa.selenium.WebElement> items =
                    driver.findElements(suggestionItems); // you already defined suggestionItems
            for (org.openqa.selenium.WebElement it : items) {
                String t = it.getText();
                if (t != null && !t.trim().isEmpty()) out.add(t.trim());
            }
        } catch (Exception ignored) {}
        return out;
    }

    // ----- Search -----
    public boolean isSearchBarVisibleAndClickable() {
        try {
        	System.out.println("hi");
//            WebElement in = waitVisible(searchInput);
//            System.out.println("bye");
            click(searchBar);
            return true;
        } catch (Exception e) { return false; }
    }

    public void typeInSearch(String text) {
    	System.out.println(text);
        click(searchBar);
        WebElement in = waitClickable(searchInput);
        in.clear();
        in.sendKeys(text);
    }
    
    public void clickLanguageContinue() {
    	waitClickable(langContinue);
    	click(langContinue);
    }

    public boolean hasSuggestionsFor(String text) {
        typeInSearch(text);
        wait.withTimeout(Duration.ofSeconds(5));
        return isPresent(suggestionContainer) && driver.findElements(suggestionItems).size() > 0;
    }
    
    public void pressEnterInSearch() {
        try {
            WebElement in = waitVisible(searchInput);
            in.sendKeys(Keys.ENTER);
        } catch (Exception ignored) {}
    }

    public void clickSuggestionContaining(String text) {
     final By itemBy = By.xpath(
         "//span[text()='Movie']//preceding-sibling::h5[contains(text(),'Avatar')]"
     );


     // Ensure overlay/items are present first
     wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(suggestionItems, 0));

     for (int attempt = 0; attempt < 3; attempt++) {
         try {
             // Re-locate right before clicking
             WebElement target = wait.until(ExpectedConditions.elementToBeClickable(itemBy));
             target.click();
             //return;
         } catch (StaleElementReferenceException e) {
             // Re-sync with the refreshed DOM and loop again
             wait.until(ExpectedConditions.refreshed(
                 ExpectedConditions.presenceOfAllElementsLocatedBy(suggestionItems)
             ));
         } catch (Exception clickInterference) {
             // Optional: fall back to JS click or ENTER if overlay keeps re-rendering
             // safeClickJS(driver.findElement(itemBy));
             // return;
         }
     }

     clickLanguageContinue();
 }

    public boolean hasNoResultsMessageFor(String ignoredText) {
        waitVisible(body);
        System.out.println(noResultsMsg);
        return isPresent(noResultsMsg);
    }
    
    public void trendingClick() {
    	List<WebElement> items = driver.findElements(trendingList);
        items.get(0).click();
	}

    public boolean resultsOrTheatresVisible() {
        try {
            wait.withTimeout(Duration.ofSeconds(8));
            List<WebElement> ele = driver.findElements(theatresList);
            System.out.println(ele.get(0).getText());
            
            return ele.size() > 0;
        } catch (Exception e) { return false; }
    }
}
