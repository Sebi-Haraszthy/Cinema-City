package com.Movies.Cinema.City.service;

import com.Movies.Cinema.City.model.Order;
import com.Movies.Cinema.City.model.Ticket;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import javax.mail.util.ByteArrayDataSource;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

@Service
public class PdfService {
    public DataSource generateTicketPdf(Order order) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(3);
        addTableHeader(table);

        for (Ticket ticket : order.getTicketList()) {
            addRow(ticket, table);
        }

        Chunk chunk = new Chunk("Total price: " + order.getTotalPrice(), font);
        document.add(table);
        document.add(chunk);
        document.close();
        byte[] bytes = outputStream.toByteArray();

        return (DataSource) new ByteArrayDataSource(bytes, "application/pdf");
    }

    private void addRow(Ticket ticket, PdfPTable table) {
        table.addCell(ticket.getSeat().getSeatRow().toString());
        table.addCell(ticket.getSeat().getSeatColumn().toString());
        table.addCell(String.valueOf(ticket.getProjection().getMovie().getPrice() + ticket.getSeat().getExtraPrice()));
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Row", "Column", "Price").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        });
    }
}