package com.backend.tracker.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.backend.tracker.entity.ExpenseDetails;

public class ExcelExportUtils {

    public static String[] HEADERS = { "ID", "Spent Amount", "Spent Details", "Expense Created Time" };
    public static String SHEET_NAME = "expenses";

    public static ByteArrayInputStream dataToExcel(List<ExpenseDetails> expenseList) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET_NAME);

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
            }

            // Date formatter
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            int rowIdx = 1;
            for (ExpenseDetails expense : expenseList) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(expense.getId());
                row.createCell(1).setCellValue(expense.getSpentAmount());
                row.createCell(2).setCellValue(expense.getSpentDetails());
                row.createCell(3).setCellValue(
                        dateFormat.format(new Date(expense.getExpenseCreatedTimeEpoch())));
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel file: " + e.getMessage());
        }
    }
}
