package com.example.moneymanagement.Model;

import android.os.Message;
import android.se.omapi.Session;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    // Replace these fields with your email credentials
    private static final String FROM_EMAIL = "your_email@gmail.com";
    private static final String PASSWORD = "your_password";

    public static void sendEmailGuidelines(String itemName, String userEmail) {
        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Create a session with authentication
//        Session session = Session.getInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
//            }
//        });
//
//        try {
//            // Create a MimeMessage object
//            Message message = new MimeMessage(session);
//
//            // Set the sender and recipient addresses
//            message.setFrom(new InternetAddress(FROM_EMAIL));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
//
//            // Set the email subject and content
//            message.setSubject("Redemption Guidelines for " + itemName);
//            message.setText("Congratulations on redeeming " + itemName + "! Here are the redemption guidelines.");
//
//            // Send the email
//            Transport.send(message);
//
//            System.out.println("Email sent successfully!");
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            System.out.println("Failed to send email: " + e.getMessage());
//        }
    }
}
