package runners;

import org.testng.annotations.DataProvider;

import io.cucumber.testng.*;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"steps"},
    plugin = {
        "pretty",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    tags = "@TS_01 or @TS_02 or @TS_03 or @TS_04",
    monochrome = true
)
public class Master_Runner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}