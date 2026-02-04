package core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;

import java.time.Duration;

public class DriverFactory {

    private static WebDriver driver;

    private DriverFactory() {
        // Prevent instantiation
    }

    public static void initDriver() {
        if (driver != null) {
            // Already initialized; no-op or throw if you prefer strict behavior
            return;
        }

        String browser = ConfigReader.get("browser").toLowerCase();

        switch (browser) {
            case "edge": {
                // Assumes msedgedriver is on PATH or webdriver.edge.driver is set
                EdgeOptions options = new EdgeOptions();
                
                options.addArguments("--headless=new");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
        
                // Helpful for CI stability
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--no-first-run");
                options.addArguments("--no-default-browser-check");
                options.addArguments("--disable-features=Translate,BackForwardCache");
                options.addArguments("--log-level=2");

                driver = new EdgeDriver(options);
            }
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                // ✅ SAFE – does NOT crash Chrome
                options.addArguments("--disable-blink-features=AutomationControlled");
                
                options.addArguments("--headless=new");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
        
                // Helpful for CI stability
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--no-first-run");
                options.addArguments("--no-default-browser-check");
                options.addArguments("--disable-features=Translate,BackForwardCache");
                options.addArguments("--log-level=2");

                driver = new ChromeDriver(options);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        // Timeouts & defaults
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(ConfigReader.getInt("implicitWait"))
        );
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize(); // optional but commonly expected
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            throw new IllegalStateException(
                "WebDriver is not initialized. Call DriverFactory.initDriver() before getDriver()."
            );
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }
}
