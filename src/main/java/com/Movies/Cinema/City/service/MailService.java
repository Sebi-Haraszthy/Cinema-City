package com.Movies.Cinema.City.service;

import com.Movies.Cinema.City.model.Order;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {
    private JavaMailSender javaMailSender;
    private PdfService pdfService;

    @Autowired
    public MailService(JavaMailSender javaMailSender, PdfService pdfService) {
        this.javaMailSender = javaMailSender;
        this.pdfService = pdfService;
    }

    public void sendOrderConfirmationMessage(String recipientMail, Order order) throws MessagingException, DocumentException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setFrom("cursjustcodeit@gmail.com");
        mimeMessageHelper.setFrom(recipientMail);
        mimeMessageHelper.setSubject("Your reservation at: " + order.getTicketList().get(0).getProjection().getMovie().getMovieName());
        mimeMessageHelper.setText("You have your " + order.getTicketList().size() + "tickets for the movie: " + order.getTicketList().get(0).getProjection().getMovie().getMovieName());
        mimeMessageHelper.addAttachment("Ticket.pdf", (DataSource) pdfService.generateTicketPdf(order));
        javaMailSender.send(message);
    }
}