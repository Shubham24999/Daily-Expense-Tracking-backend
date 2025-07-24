package com.backend.tracker.helper;

import com.backend.tracker.entity.ExpenseDetails;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfExportUtil {

    public static ByteArrayInputStream generatePdf(List<ExpenseDetails> expenses) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Expense Report", headerFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // empty line

            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[] { 1, 2, 4, 3 });

            Font tableHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            addTableHeader(table, tableHeader, "SrNo.", "Spent Amount", "Spent Details", "Created On");

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            int srNo = 1;
            for (ExpenseDetails exp : expenses) {
                table.addCell(String.valueOf(srNo++));
                table.addCell(String.valueOf(exp.getSpentAmount()));
                table.addCell(exp.getSpentDetails() != null ? exp.getSpentDetails() : "");
                table.addCell(sdf.format(new Date(exp.getExpenseCreatedTimeEpoch() * 1000)));
            }

            document.add(table);
            document.add(new Paragraph(" ")); // space before footer

            // Footer section
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            Paragraph thanks = new Paragraph("Thanks for using Expense Tracker", footerFont);
            thanks.setAlignment(Element.ALIGN_CENTER);
            document.add(thanks);

            Paragraph contact = new Paragraph(
                    "Shubham Gupta\nshubhamgupta240999@gmail.com\n+91-7309731400",
                    infoFont);
            contact.setAlignment(Element.ALIGN_CENTER);
            document.add(contact);

            document.close();
        } catch (DocumentException ex) {
            throw new RuntimeException("Error generating PDF", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static void addTableHeader(PdfPTable table, Font font, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, font));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }
}
