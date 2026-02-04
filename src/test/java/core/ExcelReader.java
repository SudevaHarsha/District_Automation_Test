
package core;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {

    public static List<Object[]> getTestData(String excelPath, String sheetName) {

        List<Object[]> data = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Iterator<Row> rows = sheet.iterator();

            while (rows.hasNext()) {
                Row row = rows.next();

                String execute = formatter.formatCellValue
                        (row.getCell(3));

                if (execute.equalsIgnoreCase("Y")) {

                    String testCaseId = formatter.formatCellValue
                            (row.getCell(0));
                    String mobile = formatter.formatCellValue
                            (row.getCell(1));
                    String result = formatter.formatCellValue
                            (row.getCell(2));

                    data.add(new Object[]{testCaseId, mobile, result});
                }
            }

        } catch (Exception e) {
        	System.out.println(e);
            throw new RuntimeException("Error reading excel file");
        }

        return data;
    }
}
