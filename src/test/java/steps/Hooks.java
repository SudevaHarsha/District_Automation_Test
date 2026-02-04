package steps;

import core.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

    @Before(order = 0)
    public void setUp() {
        DriverFactory.initDriver();
    }

    @After(order = 0)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
