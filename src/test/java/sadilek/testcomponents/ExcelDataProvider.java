package sadilek.testcomponents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The ExcelDataProvider class provides functionality for reading Excel files
 * and converting them into a 2D Object array for consumption by the test
 * methods in the tests folder.
 * 
 * Each row in the Excel file is represented as a HashMap.
 */
public class ExcelDataProvider {
    private String filePath = "";

    /**
     * Constructs an ExcelDataProvider object with the specified file path.
     *
     * @param filePath the path to the Excel file to read
     */
    public ExcelDataProvider(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads data from the Excel file and returns it as a two-dimensional Object
     * array.
     *
     * @return A 2D Object array containing the Excel data
     * @throws IOException if there is an issue with the supplied excel file path
     */
    public Object[][] getData() throws IOException {
        /* If we mess up the file path assignment, throw an exception */
        if (filePath == null || filePath.isEmpty())
            throw new IOException("there is an issue with the supplied excel file path");

        /* The DataFormatter helps us to to convert Excel cell values to Strings. */
        DataFormatter formatter = new DataFormatter();

        /* Open the Excel file and access the first sheet. */
        FileInputStream fis = new FileInputStream(new File(filePath));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        /* in case our Excel sheet is bad close the workbook and throw an Exception */
        if (sheet == null) {
            workbook.close();
            fis.close();
            throw new IOException("the sheet is null");
        }

        /* Get the count of non-empty rows in the sheet. */
        int rowCount = getRowCount(sheet);

        /*
         * Initialize the data array to store Excel data (as HashMaps). The data array
         * will contain a list of HashMaps
         */
        Object[][] data = new Object[rowCount - 1][1];

        /*
         * Loop through each row, read cell values, put it in a HashMap, and add it to
         * the data array.
         */
        for (int i = 1; i < rowCount; i++) {
            Row row = sheet.getRow(i + 1);

            if (row == null) {
                workbook.close();
                fis.close();
                throw new IOException("no such row in sheet (null)");
            }

            HashMap<String, String> map = new HashMap<>();

            map.put("highlight", formatter.formatCellValue(row.getCell(0)));
            map.put("marke", formatter.formatCellValue(row.getCell(1)));
            map.put("produktart", formatter.formatCellValue(row.getCell(2)));
            map.put("geschenkFur", formatter.formatCellValue(row.getCell(3)));
            map.put("furWen", formatter.formatCellValue(row.getCell(4)));

            data[i - 1][0] = map;
        }

        /* Close workbook and file input stream to prevent any resource leaks. */
        workbook.close();
        fis.close();

        return data;
    }

    /**
     * Returns the number of non-empty rows in the given sheet
     * 
     * @param sheet the Excel sheet for which we need to count the rows
     * @return the number or rows containing data
     */
    private int getRowCount(XSSFSheet sheet) {
        /* Loop through each row and increment count for non-empty rows. */
        int count = 0;
        for (Row row : sheet) {
            if (!isRowEmpty(row)) {
                count++;
            }
        }

        /* Return the count of non-empty rows, subtracting 1 for header row. */
        return count - 1;
    }

    /**
     * Check if a given row is empty.
     *
     * @param row the Excel row to check
     * @return true if the row is empty and false if not
     */
    private boolean isRowEmpty(Row row) {
        /* Check if the row is null or has no cells. */
        if (row == null || row.getLastCellNum() <= 0) {
            return true;
        }

        /* Loop through each cell to see if any cell contains data. */
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
