package core;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait")));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected void safeClickJS(WebElement element) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", element);
    }

    protected boolean isPresent(By locator) {
        try {
        	wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
	public void scrollIntoView(WebElement element) {
	    try {
	        JavascriptExecutor js = (JavascriptExecutor) driver;
	
	        // Scroll the element into the center of the viewport
	        js.executeScript(
	            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});",
	            element
	        );
	
	        // Small stability wait so the page settles
	        Thread.sleep(150);
	
	    } catch (Exception e) {
	        // Fallback: basic scroll if centered scroll fails
	        try {
	            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	        } catch (Exception ignored) {}
	    }
	}
	

//	// ---- Robust text waits ----
//	protected String getTextSafe(By locator) {
//	    WebElement el = waitVisible(locator);
//	    scrollIntoView(el);
//	    return el.getText() == null ? "" : el.getText().trim();
//	}
//
//	protected boolean waitUntilTextEquals(By locator, String expected, Duration timeout) {
//	    return new org.openqa.selenium.support.ui.WebDriverWait(driver, timeout)
//	        .until(d -> {
//	            try {
//	                String txt = getTextSafe(locator);
//	                return expected.equalsIgnoreCase(txt);
//	            } catch (Exception e) { return false; }
//	        });
//	}
//
//	protected boolean waitUntilTextContains(By locator, String expected, Duration timeout) {
//	    return new org.openqa.selenium.support.ui.WebDriverWait(driver, timeout)
//	        .until(d -> {
//	            try {
//	                String txt = getTextSafe(locator);
//	                return txt.toLowerCase().contains(expected.toLowerCase());
//	            } catch (Exception e) { return false; }
//	        });
//	}
//
//	/** Wait until text becomes non-empty and not a placeholder like 'Select City' */
//	protected String waitUntilNonEmptyText(By locator, Duration timeout) {
//	    return new org.openqa.selenium.support.ui.WebDriverWait(driver, timeout)
//	        .until(d -> {
//	            try {
//	                String txt = getTextSafe(locator);
//	                if (txt != null && !txt.trim().isEmpty() && !txt.matches("(?i)select\\s*city|choose\\s*city")) {
//	                    return txt.trim();
//	                }
//	                return null;
//	            } catch (Exception e) { return null; }
//	        });
//	}
}
