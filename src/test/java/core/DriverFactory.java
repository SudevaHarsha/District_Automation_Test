package core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.nio.file.Paths;

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

                EdgeOptions options = new EdgeOptions();
                    options.addArguments("--headless=new");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--remote-debugging-port=0");
                    options.addArguments("--window-size=1920,1080");

                // Isolate profile to avoid locks in CI
                String userDataDir = Paths.get(System.getProperty("java.io.tmpdir"), "edge-profile").toString();
                options.addArguments("--user-data-dir=" + userDataDir);

                // Do NOT set driver path; Selenium Manager (Selenium 4.6+) resolves msedgedriver
                driver = new EdgeDriver(options);

                break;
            }
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                // ✅ SAFE – does NOT crash Chrome
                options.addArguments("--disable-blink-features=AutomationControlled");
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
