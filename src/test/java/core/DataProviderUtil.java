
package core;

import org.testng.annotations.DataProvider;

import java.util.List;

public class DataProviderUtil {

    @DataProvider(name = "authData")
    public static Object[][] getAuthData() {

        List<Object[]> excelData =
                ExcelReader.getTestData(
                        "src/test/resources/testdata/authentication.xlsx",
                        "Auth"
                );

        return excelData.toArray(new Object[0][0]);
    }
}
