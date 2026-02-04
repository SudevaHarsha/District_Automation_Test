package core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class ScreenshotUtils extends BasePage {

    public String takeScreenshot(String name) {
        try {
            String dir = ConfigReader.get("screenshot.dir");
            if (dir == null || dir.isBlank()) {
                dir = "test-output/screenshots"; // fallback
            }

            Files.createDirectories(Path.of(dir));

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String fileName = name + "_" + timestamp + ".png";
            Path dest = Path.of(dir, fileName);

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), dest);

            System.out.println("✅ Screenshot saved: " + dest.toAbsolutePath());
            return dest.toString();

        } catch (Exception e) {
            System.out.println("❌ Screenshot failed: " + e.getMessage());
            return null;
        }
    }

}
