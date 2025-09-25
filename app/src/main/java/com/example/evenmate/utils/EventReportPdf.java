package com.example.evenmate.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.view.View;

import com.example.evenmate.models.Review;
import com.example.evenmate.models.event.Event;

import java.text.SimpleDateFormat;

public class EventReportPdf {
    public static PdfDocument getDocument(Event event,  View chartView) {
        PdfDocument pdf = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int marginLeft = 20;
        int y;

        // HEADER
        paint.setColor(Color.rgb(166, 104, 151));
        canvas.drawRect(0, 0, pageInfo.getPageWidth(), 60, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(20f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawText("Event Ratings Report", marginLeft, 40, paint);

        y = 80;

        // EVENT DETAILS BOX
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(marginLeft, y, pageInfo.getPageWidth() - marginLeft, y + 60, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(14f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        canvas.drawText("Event Details", marginLeft + 5, y + 20, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(12f);
        y += 40;
        canvas.drawText("Name: " + event.getName(), marginLeft + 5, y, paint);
        y += 20;
        canvas.drawText("Description: " + event.getDescription(), marginLeft + 5, y, paint);

        y += 40;

        // SUMMARY STATISTICS
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(marginLeft, y, pageInfo.getPageWidth() - marginLeft, y + 60, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        paint.setTextSize(14f);
        canvas.drawText("Summary Statistics", marginLeft + 5, y + 20, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(12f);
        y += 40;

        int totalVoters = event.getReviews().size();
        double averageGrade = calculateAverageGrade(event);

        canvas.drawText("Total Voters: " + totalVoters, marginLeft + 5, y, paint);
        y += 20;
        canvas.drawText(String.format("Average Rating: %.2f / 5.0 stars", averageGrade), marginLeft + 5, y, paint);

        y += 40;

        // RATING DISTRIBUTION
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        paint.setTextSize(14f);
        canvas.drawText("Rating Distribution", marginLeft, y, paint);
        y += 20;

        paint.setTextSize(12f);
        canvas.drawText("Rating", marginLeft, y, paint);
        canvas.drawText("Count", marginLeft + 60, y, paint);
        canvas.drawText("Percentage", marginLeft + 120, y, paint);
        canvas.drawText("Visual", marginLeft + 200, y, paint);

        y += 10;
        paint.setStrokeWidth(1);
        canvas.drawLine(marginLeft, y, pageInfo.getPageWidth() - marginLeft, y, paint);
        y += 20;

        int[] starCounts = new int[5];
        for (int i = 0; i < 5; i++) {
            int stars = i + 1;
            starCounts[i] = (int) event.getReviews().stream()
                    .filter(r -> r.getStars() == stars)
                    .count();
        }

        for (int i = 4; i >= 0; i--) {
            int stars = i + 1;
            int count = starCounts[i];
            double percentage = totalVoters > 0 ? (count * 100.0 / totalVoters) : 0.0;

            canvas.drawText(stars + " Star" + (stars > 1 ? "s" : ""), marginLeft, y, paint);
            canvas.drawText(String.valueOf(count), marginLeft + 60, y, paint);
            canvas.drawText(String.format("%.1f%%", percentage), marginLeft + 120, y, paint);

            paint.setColor(Color.rgb(166, 104, 151));
            int barWidth = (int) (percentage / 100.0 * 100);
            canvas.drawRect(marginLeft + 200, y - 10, marginLeft + 200 + barWidth, y - 5, paint);

            paint.setColor(Color.BLACK);
            y += 20;
        }

        y += 30;

        // CHART IMAGE (ratingChart equivalent)
        Bitmap chartBitmap = getChartBitmap(chartView);
        if (chartBitmap != null) {
            int chartWidth = 400;
            int chartHeight = 300;
            canvas.drawBitmap(Bitmap.createScaledBitmap(chartBitmap, chartWidth, chartHeight, false),
                    marginLeft, y, paint);
        }

        // FOOTER
        paint.setTextSize(8f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setColor(Color.GRAY);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        canvas.drawText("Generated on: " + currentDate, marginLeft, pageInfo.getPageHeight() - 20, paint);

        pdf.finishPage(page);
        return pdf;
    }

    private static double calculateAverageGrade(Event event) {
        if (event.getReviews().isEmpty()) return 0.0;
        return event.getReviews().stream().mapToInt(Review::getStars).average().orElse(0.0);
    }

    private static Bitmap getChartBitmap(View chartView) {
        if (chartView == null) return null;

        Bitmap bitmap = Bitmap.createBitmap(chartView.getWidth(), chartView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        chartView.draw(canvas);
        return bitmap;
    }


}
