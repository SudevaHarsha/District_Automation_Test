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

    private DriverFactory() {}

    public static void initDriver() {
        if (driver != null) return;

        String browser;
        try {
            browser = String.valueOf(ConfigReader.get("browser")).toLowerCase();
        } catch (Exception e) {
            browser = "edge"; // default
        }

        boolean headless = isCi(); // default to headless in CI
        try {
            String h = ConfigReader.get("headless");
            if (h != null) headless = Boolean.parseBoolean(h);
        } catch (Exception ignored) {}

        // Workspace-based user data dir to avoid locks and permission issues
        String workspace = System.getenv().getOrDefault("WORKSPACE", System.getProperty("user.dir"));
        String userDataDir = Paths.get(workspace, ".browser-profile").toString();

        switch (browser) {
            case "edge": {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--remote-debugging-port=0");
                    options.addArguments("--window-size=1920,1080");
                }
                options.addArguments("--user-data-dir=" + userDataDir);
                options.addArguments("--no-first-run");
                options.addArguments("--no-default-browser-check");
                // Helps on some enterprise Windows images with code integrity checks
                options.addArguments("--disable-features=RendererCodeIntegrity");

                // Let Selenium Manager resolve msedgedriver automatically
                driver = new EdgeDriver(options);
                break;
            }
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-blink-features=AutomationControlled");
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--remote-debugging-port=0");
                    options.addArguments("--window-size=1920,1080");
                }
                options.addArguments("--user-data-dir=" + userDataDir);
                options.addArguments("--no-first-run");
                options.addArguments("--no-default-browser-check");

                driver = new ChromeDriver(options);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        int implicit = 10;
        try { implicit = ConfigReader.getInt("implicitWait"); } catch (Exception ignored) {}
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().deleteAllCookies();

        // Do not maximize when headless
        if (!headless) {
            try { driver.manage().window().maximize(); } catch (Exception ignored) {}
        }

        System.out.printf("[DriverFactory] Browser=%s, headless=%s, profile=%s%n",
                browser, headless, userDataDir);
    }

    public static WebDriver getDriver() {
        if (driver == null) throw new IllegalStateException("Call initDriver() first.");
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            try { driver.quit(); } finally { driver = null; }
        }
    }

    private static boolean isCi() {
        return System.getenv("JENKINS_HOME") != null
                || System.getenv("BUILD_NUMBER") != null
                || System.getenv("CI") != null;
    }
}