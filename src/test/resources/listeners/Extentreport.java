import java.io.File;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Extentreport implements ITestListener {
    private ExtentSparkReporter sparkReporter;  // UI of the report
    private ExtentReports extent;               // populate common info on the report
    private ExtentTest test;                    // creating test case entries in the report

    @Override
    public void onStart(ITestContext context) {
        // Ensure reports folder exists
        new File(System.getProperty("user.dir") + "/reports").mkdirs();

        // Initialize ExtentSparkReporter with the report file path
        sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/reports/myReport.html");

        sparkReporter.config().setDocumentTitle("Automation Report"); 
        sparkReporter.config().setReportName("Finding Hospitals");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Computer Name", "localhost");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Tester Name", "Sudeva Harsha");
        extent.setSystemInfo("OS", "Windows11");
        extent.setSystemInfo("Browser", "Chrome");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test = extent.createTest(result.getName());
        test.log(Status.PASS, "Test case PASSED: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test = extent.createTest(result.getName());
        test.log(Status.FAIL, "Test case FAILED: " + result.getName());
        test.log(Status.FAIL, result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test = extent.createTest(result.getName());
        test.log(Status.SKIP, "Test case SKIPPED: " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush(); // writes the report to disk
    }
}
