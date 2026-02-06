package com.boatsafari.service;

import com.boatsafari.model.Booking;
import com.boatsafari.model.BookingStatus;
import com.boatsafari.model.Trip; // Add this import
import com.boatsafari.repository.BookingRepository;
import com.boatsafari.repository.TripRepository; // Add this import
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PDFExportService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TripRepository tripRepository; // Add this

    private static final Color PRIMARY_COLOR = new DeviceRgb(0, 83, 78); // Sri Lanka Green
    private static final Color ACCENT_COLOR = new DeviceRgb(255, 127, 50); // Sri Lanka Orange

    public byte[] generateRevenueReport() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Set up fonts
            PdfFont headerFont = createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont boldFont = createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = createFont(StandardFonts.HELVETICA);

            // Add header
            addHeader(document, headerFont);

            // Add report title
            addReportTitle(document, "Revenue Report - Completed Bookings");

            // Add summary statistics
            addSummarySection(document, boldFont, normalFont);

            // Add detailed bookings table
            addBookingsTable(document, boldFont, normalFont);

            // Add footer
            addFooter(document, normalFont);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    public byte[] generateTripPerformanceReport() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont headerFont = createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont boldFont = createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = createFont(StandardFonts.HELVETICA);

            addHeader(document, headerFont);
            addReportTitle(document, "Trip Performance Analysis Report");
            addTripPerformanceSummary(document, boldFont, normalFont);
            addTripPerformanceTableRealData(document, boldFont, normalFont); // Renamed method
            addPerformanceInsights(document, boldFont, normalFont);
            addFooter(document, normalFont);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate trip performance PDF", e);
        }
    }

    // Helper method to create fonts with proper exception handling
    private PdfFont createFont(String fontName) {
        try {
            return PdfFontFactory.createFont(fontName);
        } catch (IOException e) {
            // Fallback to default font if specified font fails
            try {
                return PdfFontFactory.createFont(StandardFonts.HELVETICA);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to create PDF font", ex);
            }
        }
    }

    private void addHeader(Document document, PdfFont headerFont) {
        // Company header
        Paragraph companyHeader = new Paragraph("Boat Safari Sri Lanka")
                .setFont(headerFont)
                .setFontSize(18)
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);

        Paragraph subHeader = new Paragraph("Operations Management System")
                .setFont(headerFont)
                .setFontSize(12)
                .setFontColor(ACCENT_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);

        document.add(companyHeader);
        document.add(subHeader);
    }

    private void addReportTitle(Document document, String title) {
        PdfFont titleFont = createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont dateFont = createFont(StandardFonts.HELVETICA);

        Paragraph reportTitle = new Paragraph(title)
                .setFont(titleFont)
                .setFontSize(16)
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);

        String generatedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Paragraph dateInfo = new Paragraph("Generated on: " + generatedDate)
                .setFont(dateFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);

        document.add(reportTitle);
        document.add(dateInfo);
    }

    private void addSummarySection(Document document, PdfFont boldFont, PdfFont normalFont) {
        // Get completed bookings for summary
        List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);

        // Calculate summary statistics
        BigDecimal totalRevenue = completedBookings.stream()
                .map(b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = totalRevenue.multiply(new BigDecimal("0.10"));
        BigDecimal netRevenue = totalRevenue.subtract(totalTax);
        BigDecimal companyRevenue = netRevenue.multiply(new BigDecimal("0.20"));
        BigDecimal ownerPayout = netRevenue.subtract(companyRevenue);

        // Create summary table
        float[] columnWidths = {1, 1};
        Table summaryTable = new Table(UnitValue.createPercentArray(columnWidths));
        summaryTable.setWidth(UnitValue.createPercentValue(80));
        summaryTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
        summaryTable.setMarginBottom(20);

        // Add summary rows
        addSummaryRow(summaryTable, "Total Completed Bookings", String.valueOf(completedBookings.size()), boldFont, normalFont);
        addSummaryRow(summaryTable, "Total Revenue", "Rs. " + formatCurrency(totalRevenue), boldFont, normalFont);
        addSummaryRow(summaryTable, "Tax Amount (10%)", "Rs. " + formatCurrency(totalTax), boldFont, normalFont);
        addSummaryRow(summaryTable, "Net Revenue", "Rs. " + formatCurrency(netRevenue), boldFont, normalFont);
        addSummaryRow(summaryTable, "Company Revenue (20%)", "Rs. " + formatCurrency(companyRevenue), boldFont, normalFont);
        addSummaryRow(summaryTable, "Owner Payout (80%)", "Rs. " + formatCurrency(ownerPayout), boldFont, normalFont);

        document.add(summaryTable);
    }

    private void addSummaryRow(Table table, String label, String value, PdfFont boldFont, PdfFont normalFont) {
        table.addCell(new Cell().add(new Paragraph(label).setFont(boldFont)).setBackgroundColor(new DeviceRgb(240, 240, 240)));
        table.addCell(new Cell().add(new Paragraph(value).setFont(normalFont)));
    }

    private void addBookingsTable(Document document, PdfFont boldFont, PdfFont normalFont) {
        // Get completed bookings
        List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);

        // Create table
        float[] columnWidths = {3, 4, 4, 3, 3, 4};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(20);

        // Add table headers
        String[] headers = {"Booking ID", "Customer", "Trip", "People", "Amount", "Date"};
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setFont(boldFont))
                    .setBackgroundColor(PRIMARY_COLOR)
                    .setFontColor(ColorConstants.WHITE));
        }

        // Add table rows
        for (Booking booking : completedBookings) {
            table.addCell(new Cell().add(new Paragraph("#" + booking.getId()).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(booking.getFirstName() + " " + booking.getLastName()).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(booking.getTrip() != null ? booking.getTrip().getName() : "N/A").setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(booking.getNumberOfPeople())).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph("Rs. " + formatCurrency(booking.getTotalPrice())).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(formatDate(booking.getBookingDate())).setFont(normalFont)));
        }

        document.add(new Paragraph("Detailed Bookings").setFont(boldFont).setFontSize(12).setMarginBottom(10));
        document.add(table);
    }

    // Trip Performance Summary Section
    private void addTripPerformanceSummary(Document document, PdfFont boldFont, PdfFont normalFont) {
        List<Trip> activeTrips = tripRepository.findByActiveTrue();
        List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);

        // Calculate overall statistics
        long totalCompletedBookings = completedBookings.size();
        BigDecimal totalRevenue = completedBookings.stream()
                .map(b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double avgUtilization = activeTrips.stream()
                .mapToDouble(trip -> calculateTripUtilization(trip))
                .average()
                .orElse(0.0);

        // Create summary table
        float[] columnWidths = {1, 1};
        Table summaryTable = new Table(UnitValue.createPercentArray(columnWidths));
        summaryTable.setWidth(UnitValue.createPercentValue(80));
        summaryTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
        summaryTable.setMarginBottom(20);

        addSummaryRow(summaryTable, "Active Trips", String.valueOf(activeTrips.size()), boldFont, normalFont);
        addSummaryRow(summaryTable, "Total Completed Bookings", String.valueOf(totalCompletedBookings), boldFont, normalFont);
        addSummaryRow(summaryTable, "Total Revenue", "Rs. " + formatCurrency(totalRevenue), boldFont, normalFont);
        addSummaryRow(summaryTable, "Average Utilization", String.format("%.1f%%", avgUtilization), boldFont, normalFont);

        document.add(new Paragraph("Performance Summary").setFont(boldFont).setFontSize(14).setMarginBottom(10));
        document.add(summaryTable);
    }

    // Renamed method to avoid duplicate
    private void addTripPerformanceTableRealData(Document document, PdfFont boldFont, PdfFont normalFont) {
        List<Trip> activeTrips = tripRepository.findByActiveTrue();

        // Create table with proper columns
        float[] columnWidths = {4, 3, 3, 3, 3, 3, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(20);

        // Add table headers
        String[] headers = {"Trip Name", "Type", "Bookings", "Revenue", "Avg. People", "Utilization", "Performance"};
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setFont(boldFont))
                    .setBackgroundColor(PRIMARY_COLOR)
                    .setFontColor(ColorConstants.WHITE));
        }

        // Add table rows with actual data
        for (Trip trip : activeTrips) {
            // Get bookings for this trip
            List<Booking> tripBookings = bookingRepository.findByTripId(trip.getId());
            List<Booking> completedBookings = tripBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                    .collect(Collectors.toList());

            // Calculate performance metrics
            long totalBookings = completedBookings.size();
            BigDecimal revenue = completedBookings.stream()
                    .map(b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double avgPeople = completedBookings.stream()
                    .mapToInt(Booking::getNumberOfPeople)
                    .average()
                    .orElse(0.0);

            double utilization = calculateTripUtilization(trip);
            String performance = getPerformanceRating(utilization);

            // Add row to table
            table.addCell(new Cell().add(new Paragraph(trip.getName()).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(trip.getType()).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(totalBookings)).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph("Rs. " + formatCurrency(revenue)).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(String.format("%.1f", avgPeople)).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(String.format("%.1f%%", utilization)).setFont(normalFont)));
            table.addCell(new Cell().add(new Paragraph(performance).setFont(normalFont))
                    .setFontColor(getPerformanceColor(performance)));
        }

        document.add(new Paragraph("Trip Performance Details").setFont(boldFont).setFontSize(14).setMarginBottom(10));
        document.add(table);
    }

    // Performance Insights Section
    private void addPerformanceInsights(Document document, PdfFont boldFont, PdfFont normalFont) {
        List<Trip> activeTrips = tripRepository.findByActiveTrue();
        List<Booking> completedBookings = bookingRepository.findByStatus(BookingStatus.COMPLETED);

        if (activeTrips.isEmpty()) {
            document.add(new Paragraph("No performance data available.").setFont(normalFont));
            return;
        }

        // Find best and worst performing trips
        Trip bestTrip = null;
        Trip worstTrip = null;
        double maxRevenue = 0;
        double minRevenue = Double.MAX_VALUE;

        for (Trip trip : activeTrips) {
            List<Booking> tripCompletedBookings = bookingRepository.findByTripId(trip.getId()).stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                    .collect(Collectors.toList());

            double tripRevenue = tripCompletedBookings.stream()
                    .mapToDouble(b -> b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0.0)
                    .sum();

            if (tripRevenue > maxRevenue) {
                maxRevenue = tripRevenue;
                bestTrip = trip;
            }
            if (tripRevenue < minRevenue && tripCompletedBookings.size() > 0) {
                minRevenue = tripRevenue;
                worstTrip = trip;
            }
        }

        // Create insights section
        Paragraph insightsHeader = new Paragraph("Performance Insights")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginBottom(10);
        document.add(insightsHeader);

        if (bestTrip != null) {
            document.add(new Paragraph("🏆 Best Performing Trip: " + bestTrip.getName())
                    .setFont(normalFont)
                    .setMarginBottom(5)
                    .setFontColor(new DeviceRgb(0, 100, 0))); // Dark green
        }

        if (worstTrip != null && worstTrip != bestTrip) {
            document.add(new Paragraph("📊 Needs Attention: " + worstTrip.getName())
                    .setFont(normalFont)
                    .setMarginBottom(5)
                    .setFontColor(new DeviceRgb(178, 34, 34))); // Firebrick red
        }

        // Add utilization insights
        double avgUtilization = activeTrips.stream()
                .mapToDouble(trip -> calculateTripUtilization(trip))
                .average()
                .orElse(0.0);

        String utilizationStatus = avgUtilization >= 70 ? "Excellent" :
                avgUtilization >= 50 ? "Good" :
                        avgUtilization >= 30 ? "Average" : "Needs Improvement";

        document.add(new Paragraph("📈 Overall Utilization: " + String.format("%.1f%% (%s)", avgUtilization, utilizationStatus))
                .setFont(normalFont)
                .setMarginBottom(5));

        // Add booking distribution insight
        long totalBookings = completedBookings.size();
        if (totalBookings > 0) {
            double bookingPerTrip = (double) totalBookings / activeTrips.size();
            document.add(new Paragraph("📋 Average Bookings per Trip: " + String.format("%.1f", bookingPerTrip))
                    .setFont(normalFont)
                    .setMarginBottom(10));
        }
    }

    // Remove the old placeholder method - KEEP ONLY ONE VERSION
    // private void addTripPerformanceTable(Document document, PdfFont boldFont, PdfFont normalFont) {
    //     // This method is replaced by addTripPerformanceTableRealData
    // }

    private void addFooter(Document document, PdfFont normalFont) {
        Paragraph footer = new Paragraph("\n\nThis report was generated automatically by Boat Safari Operations Management System.")
                .setFont(normalFont)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);

        document.add(footer);
    }

    // Helper method to calculate trip utilization
    private double calculateTripUtilization(Trip trip) {
        List<Booking> completedBookings = bookingRepository.findByTripId(trip.getId()).stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .collect(Collectors.toList());

        if (completedBookings.isEmpty() || trip.getCapacity() <= 0) {
            return 0.0;
        }

        int totalPassengers = completedBookings.stream()
                .mapToInt(Booking::getNumberOfPeople)
                .sum();

        double utilization = ((double) totalPassengers / (trip.getCapacity() * completedBookings.size())) * 100;
        return Math.min(utilization, 100.0);
    }

    // Helper method to get performance rating
    private String getPerformanceRating(double utilization) {
        return utilization >= 80 ? "Excellent" :
                utilization >= 60 ? "Good" :
                        utilization >= 40 ? "Average" : "Low";
    }

    // Helper method to get performance color
    private Color getPerformanceColor(String performance) {
        switch (performance.toLowerCase()) {
            case "excellent":
                return new DeviceRgb(0, 100, 0); // Dark green
            case "good":
                return new DeviceRgb(0, 0, 139); // Dark blue
            case "average":
                return new DeviceRgb(218, 165, 32); // Golden rod
            case "low":
                return new DeviceRgb(178, 34, 34); // Firebrick red
            default:
                return ColorConstants.BLACK;
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,.2f", amount);
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) return "N/A";
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}