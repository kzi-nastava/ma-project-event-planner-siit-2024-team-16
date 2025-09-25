package com.example.evenmate.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;

import com.example.evenmate.models.event.AgendaItem;
import com.example.evenmate.models.event.Event;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDetailsPdf {
    //todo add invitations to report
    public static PdfDocument getDocument(Event event) {
        PdfDocument pdf = new PdfDocument();

        int pageWidth = 595;
        int pageHeight = 842;
        int yPosition = 20;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        paint.setTextSize(18);
        String title = event != null && event.getName() != null ? event.getName().toUpperCase() : "EVENT DETAILS";
        float titleWidth = paint.measureText(title);
        canvas.drawText(title, (pageWidth - titleWidth) / 2f, yPosition, paint);

        yPosition += 30;

        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        assert event != null;
        String[] info = {
                "Type: " + event.getType().getName(),
                "Date: " + event.getDate(),
                "Location: " + event.getAddress().getStreetName() + " " + event.getAddress().getStreetNumber() + ", " +
                        event.getAddress().getCity() + ", " + event.getAddress().getCountry(),
                "Organizer: " + event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName(),
                "Max number of participants: " + event.getMaxAttendees(),
                "Access type: " + (event.getIsPrivate() ? "Private (invitation only)" : "Open to the public")
        };

        for (String line : info) {
            canvas.drawText(line, 20, yPosition, paint);
            yPosition += 20;
        }

        yPosition += 10;

        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            canvas.drawText("Description:", 20, yPosition, paint);
            yPosition += 20;

            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            String description = event.getDescription();
            List<String> lines = splitTextToLines(description, pageWidth - 40, paint);

            for (String descLine : lines) {
                canvas.drawText(descLine, 20, yPosition, paint);
                yPosition += 15;
            }

            yPosition += 15;
        }

        List<AgendaItem> agendaItems = event.getAgendaItems();
        yPosition += 20;
        drawAgendaTable(agendaItems, pdf, canvas, paint, pageWidth, pageHeight, yPosition);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(8);
        String footerText = "Evenmate " + DateFormat.getDateInstance().format(new Date());
        canvas.drawText(footerText, 20, pageHeight - 20, paint);

        pdf.finishPage(page);

        return pdf;
    }

    private static List<String> splitTextToLines(String text, int maxWidth, Paint paint) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (paint.measureText(currentLine + " " + word) < maxWidth) {
                currentLine.append(currentLine.length() == 0 ? "" : " ").append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString());
        return lines;
    }

    private static void drawAgendaTable(List<AgendaItem> agendaItems, PdfDocument pdf, Canvas canvas, Paint paint, int pageWidth, int pageHeight, int startY) {
        if (agendaItems == null || agendaItems.isEmpty()) return;

        int xStart = 20;
        int tableWidth = pageWidth - 40;
        int rowHeight = 40;
        int headerHeight = 45;

        int[] colWidths = {100, 150, 200, 100};
        String[] headers = {"Time", "Name", "Description", "Location"};

        Paint linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);

        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);

        fillPaint.setColor(Color.LTGRAY);
        canvas.drawRect(xStart, startY, xStart + tableWidth, startY + headerHeight, fillPaint);
        canvas.drawRect(xStart, startY, xStart + tableWidth, startY + headerHeight, linePaint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(12);

        int xPos = xStart + 10;
        for (int i = 0; i < headers.length; i++) {
            canvas.drawText(headers[i], xPos, startY + 28, paint);
            xPos += colWidths[i];
        }

        int yPosition = startY + headerHeight;

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        int rowIndex = 0;

        for (AgendaItem item : agendaItems) {
            if (yPosition + rowHeight > pageHeight - 40) {
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create();
                PdfDocument.Page page = pdf.startPage(pageInfo);
                canvas = page.getCanvas();
                yPosition = 40;
            }

            if (rowIndex % 2 == 0) {
                fillPaint.setColor(Color.rgb(245, 245, 245));
                canvas.drawRect(xStart, yPosition, xStart + tableWidth, yPosition + rowHeight, fillPaint);
            }

            canvas.drawRect(xStart, yPosition, xStart + tableWidth, yPosition + rowHeight, linePaint);

            String[] rowData = {
                    (item.getStartTime() != null ? item.getStartTime() : "") +
                            " - " +
                            (item.getEndTime() != null ? item.getEndTime() : ""),
                    item.getName() != null ? item.getName() : "",
                    item.getDescription() != null ? item.getDescription() : "",
                    item.getLocation() != null ? item.getLocation() : ""
            };

            xPos = xStart + 10;
            for (int i = 0; i < rowData.length; i++) {
                List<String> lines = splitTextToLines(rowData[i], colWidths[i] - 20, paint);
                int textY = yPosition + 20;
                for (String line : lines) {
                    canvas.drawText(line, xPos, textY, paint);
                    textY += 15;
                }
                xPos += colWidths[i];
            }

            yPosition += rowHeight;
            rowIndex++;
        }
    }

}
