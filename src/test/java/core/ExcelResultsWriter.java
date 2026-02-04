package core;

import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ExcelResultsWriter {

    private static final ReentrantLock LOCK = new ReentrantLock(true);

    // One row per item (no CSV)
    private static final String[] SUGGESTIONS_HEADER = {
            "RunId", "TestCaseId", "City", "SearchQuery", "ItemIndex", "SuggestionText", "Timestamp"
    };
    private static final String[] THEATRES_HEADER = {
            "RunId", "TestCaseId", "City", "Movie", "ItemIndex", "TheatreName", "Timestamp"
    };

    private final Path filePath;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String runId;

    public ExcelResultsWriter(String outputPath) {
        this.filePath = Paths.get(outputPath);
        this.runId = System.getProperty("runId", UUID.randomUUID().toString().substring(0, 8));
        ensureWorkbookExists();
    }

    /* ---------------- Initialization & Safety ---------------- */

    private void ensureWorkbookExists() {
        LOCK.lock();
        try {
            Files.createDirectories(filePath.getParent());

            boolean needCreate = Files.notExists(filePath);
            if (!needCreate) {
                try {
                    if (Files.size(filePath) == 0) {
                        needCreate = true; // zero-byte file
                    }
                } catch (IOException e) {
                    needCreate = true;
                }
            }

            if (needCreate) {
                try (Workbook wb = new XSSFWorkbook()) {
                    writeHeader(wb.createSheet("Suggestions"), SUGGESTIONS_HEADER);
                    writeHeader(wb.createSheet("Theatres"), THEATRES_HEADER);
                    atomicWrite(wb);
                }
                return;
            }

            // Validate openable; if not, recreate fresh
            try (InputStream in = Files.newInputStream(filePath);
                 Workbook wb = new XSSFWorkbook(in)) {
                ensureSheetAndHeader(wb, "Suggestions", SUGGESTIONS_HEADER);
                ensureSheetAndHeader(wb, "Theatres", THEATRES_HEADER);
                atomicWrite(wb);
            } catch (EmptyFileException | org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException bad) {
                try (Workbook wb = new XSSFWorkbook()) {
                    writeHeader(wb.createSheet("Suggestions"), SUGGESTIONS_HEADER);
                    writeHeader(wb.createSheet("Theatres"), THEATRES_HEADER);
                    atomicWrite(wb);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Excel file: " + filePath, e);
        } finally {
            LOCK.unlock();
        }
    }

    private void ensureSheetAndHeader(Workbook wb, String sheetName, String[] header) {
        Sheet sheet = wb.getSheet(sheetName);
        if (sheet == null) {
            writeHeader(wb.createSheet(sheetName), header);
        } else if (sheet.getPhysicalNumberOfRows() == 0) {
            writeHeader(sheet, header);
        } else {
            // If headers are old (from a previous CSV version), you can choose to recreate here.
            // For simplicity, we keep as-is. If columns mismatch, delete the old file once.
        }
    }

    private void writeHeader(Sheet sheet, String[] headers) {
        Row r = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            r.createCell(i, CellType.STRING).setCellValue(headers[i]);
            sheet.setColumnWidth(i, Math.min(100 * 256, 35 * 256));
        }
    }

    private void atomicWrite(Workbook wb) throws IOException {
        Path tmp = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        try (OutputStream out = Files.newOutputStream(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            wb.write(out);
        }
        try {
            Files.move(tmp, filePath, ATOMIC_MOVE, REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tmp, filePath, REPLACE_EXISTING);
        }
    }

    /* ---------------- Public API (Per-item rows) ---------------- */

    /** Write each suggestion as its own row in 'Suggestions' sheet */
    public void appendSuggestionItems(String testCaseId, String city, String query, List<String> suggestions) {
        if (suggestions == null) suggestions = Collections.emptyList();
        String timestamp = df.format(new Date());
        int idx = 1;
        for (String s : suggestions) {
            String text = s == null ? "" : s.trim();
            String[] row = {
                    runId, nz(testCaseId), nz(city), nz(query),
                    String.valueOf(idx++),
                    text,
                    timestamp
            };
            appendRow("Suggestions", row);
        }
        // If you want a record even when 0 suggestions, add a blank row:
        if (suggestions.isEmpty()) {
            String[] row = {
                    runId, nz(testCaseId), nz(city), nz(query),
                    "0", "", timestamp
            };
            appendRow("Suggestions", row);
        }
    }

    /** Write each theatre as its own row in 'Theatres' sheet */
    public void appendTheatreItems(String testCaseId, String city, String movie, List<String> theatres) {
        if (theatres == null) theatres = Collections.emptyList();
        String timestamp = df.format(new Date());
        int idx = 1;
        for (String t : theatres) {
            String name = t == null ? "" : t.trim();
            String[] row = {
                    runId, nz(testCaseId), nz(city), nz(movie),
                    String.valueOf(idx++),
                    name,
                    timestamp
            };
            appendRow("Theatres", row);
        }
        if (theatres.isEmpty()) {
            String[] row = {
                    runId, nz(testCaseId), nz(city), nz(movie),
                    "0", "", timestamp
            };
            appendRow("Theatres", row);
        }
    }

    /* ---------------- Internal row append ---------------- */

    private void appendRow(String sheetName, String[] values) {
        LOCK.lock();
        try (InputStream in = Files.newInputStream(filePath);
             Workbook wb = new XSSFWorkbook(in)) {

            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                sheet = wb.createSheet(sheetName);
                if ("Suggestions".equals(sheetName)) writeHeader(sheet, SUGGESTIONS_HEADER);
                else writeHeader(sheet, THEATRES_HEADER);
            }

            int rowNum = sheet.getLastRowNum() + 1;
            Row r = sheet.createRow(rowNum);
            for (int i = 0; i < values.length; i++) {
                r.createCell(i, CellType.STRING).setCellValue(values[i]);
            }
            atomicWrite(wb);
        } catch (IOException e) {
            throw new RuntimeException("Failed appending row into " + sheetName + " (" + filePath + ")", e);
        } finally {
            LOCK.unlock();
        }
    }

    private String nz(String s) { return s == null ? "" : s; }
}
