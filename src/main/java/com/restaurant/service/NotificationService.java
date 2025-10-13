package com.restaurant.service;

import com.restaurant.model.Reservation;
import com.restaurant.model.Schedule;
import com.restaurant.model.Staff;
import com.restaurant.model.EventBooking;
import com.restaurant.model.EventStaffAssignment;
import com.restaurant.model.CustomerFeedback;
import com.restaurant.model.FeedbackResponse;
import com.restaurant.model.SatisfactionReport;
import com.restaurant.model.Order;
import com.restaurant.model.KitchenTask;
import com.restaurant.model.InventoryItem;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Service for handling restaurant notifications including email confirmations,
 * reminders, and SMS notifications.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.contact.email:reservations@restaurant.com}")
    private String restaurantEmail;

    @Value("${app.contact.phone:+1 (555) 123-4567}")
    private String restaurantPhone;

    @Value("${app.name:Restaurant Management System}")
    private String restaurantName;

    /**
     * Sends a reservation confirmation email to the user.
     * 
     * @param reservation the reservation to confirm
     */
    @Async
    public void sendReservationConfirmation(Reservation reservation) {
        if (!validateReservation(reservation)) {
            logger.error("Cannot send confirmation: invalid reservation data");
            return;
        }
        
        String userEmail = reservation.getUser().getEmail();
        if (!isValidEmail(userEmail)) {
            logger.error("Cannot send confirmation: invalid email address: {}", userEmail);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(userEmail);
            helper.setFrom(restaurantEmail);
            helper.setSubject("Reservation Confirmation - " + restaurantName);
            
            String htmlContent = buildHtmlConfirmationEmail(reservation);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Confirmation email sent successfully to: {}", userEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send confirmation email to {}: {}", userEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending confirmation email to {}: {}", userEmail, e.getMessage(), e);
        }
    }

    /**
     * Sends a reservation confirmation email to a guest (non-registered user).
     * 
     * @param guestEmail the guest's email address
     * @param reservation the reservation to confirm
     */
    @Async
    public void sendGuestReservationConfirmation(String guestEmail, Reservation reservation) {
        if (!StringUtils.hasText(guestEmail) || !validateReservation(reservation)) {
            logger.error("Cannot send guest confirmation: invalid email or reservation data");
            return;
        }
        
        if (!isValidEmail(guestEmail)) {
            logger.error("Cannot send guest confirmation: invalid email address: {}", guestEmail);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(guestEmail);
            helper.setFrom(restaurantEmail);
            helper.setSubject("Reservation Confirmation - " + restaurantName);
            
            String htmlContent = buildHtmlGuestConfirmationEmail(reservation);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Guest confirmation email sent successfully to: {}", guestEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send guest confirmation email to {}: {}", guestEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending guest confirmation email to {}: {}", guestEmail, e.getMessage(), e);
        }
    }

    /**
     * Sends a reservation reminder email to the user.
     * 
     * @param reservation the reservation to remind about
     */
    @Async
    public void sendReservationReminder(Reservation reservation) {
        if (!validateReservation(reservation)) {
            logger.error("Cannot send reminder: invalid reservation data");
            return;
        }
        
        String userEmail = reservation.getUser().getEmail();
        if (!isValidEmail(userEmail)) {
            logger.error("Cannot send reminder: invalid email address: {}", userEmail);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(userEmail);
            helper.setFrom(restaurantEmail);
            helper.setSubject("Reservation Reminder - " + restaurantName);
            
            String htmlContent = buildHtmlReminderEmail(reservation);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Reminder email sent successfully to: {}", userEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send reminder email to {}: {}", userEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending reminder email to {}: {}", userEmail, e.getMessage(), e);
        }
    }

    /**
     * Sends an SMS confirmation for a reservation.
     * Note: This is a placeholder implementation. In production, integrate with SMS service like Twilio.
     * 
     * @param phoneNumber the phone number to send SMS to
     * @param reservation the reservation to confirm
     */
    @Async
    public void sendSMSConfirmation(String phoneNumber, Reservation reservation) {
        if (!StringUtils.hasText(phoneNumber) || !validateReservation(reservation)) {
            logger.error("Cannot send SMS: invalid phone number or reservation data");
            return;
        }
        
        if (!isValidPhoneNumber(phoneNumber)) {
            logger.error("Cannot send SMS: invalid phone number format: {}", phoneNumber);
            return;
        }
        
        try {
            String message = buildSMSMessage(reservation);
            
            // TODO: Integrate with actual SMS service (Twilio, AWS SNS, etc.)
            // For now, log the message that would be sent
            logger.info("SMS would be sent to {}: {}", phoneNumber, message);
            
            // In production, replace with actual SMS service call:
            // smsService.sendSMS(phoneNumber, message);
            
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
        }
    }

    /**
     * Validates reservation data for notification sending.
     * 
     * @param reservation the reservation to validate
     * @return true if valid, false otherwise
     */
    private boolean validateReservation(Reservation reservation) {
        if (reservation == null) {
            return false;
        }
        
        if (reservation.getUser() == null) {
            return false;
        }
        
        if (!StringUtils.hasText(reservation.getUser().getEmail())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates email address format.
     * 
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        return EMAIL_REGEX.matcher(email).matches();
    }
    
    /**
     * Validates phone number format (basic validation).
     * 
     * @param phoneNumber the phone number to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            return false;
        }
        // Basic phone number validation - remove all non-digits and check length
        String digitsOnly = phoneNumber.replaceAll("\\D", "");
        return digitsOnly.length() >= 10 && digitsOnly.length() <= 15;
    }
    
    /**
     * Builds SMS message content for reservation confirmation.
     * 
     * @param reservation the reservation
     * @return formatted SMS message
     */
    private String buildSMSMessage(Reservation reservation) {
        StringBuilder message = new StringBuilder();
        message.append("Your reservation for ");
        
        if (reservation.getReservationDateTime() != null) {
            message.append(reservation.getReservationDateTime()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a")));
        } else {
            message.append("your selected time");
        }
        
        message.append(" has been confirmed.");
        
        if (reservation.getTable() != null && reservation.getTable().getName() != null) {
            message.append(" Table: ").append(reservation.getTable().getName());
        }
        
        if (reservation.getNumberOfGuests() != null) {
            message.append(" Party: ").append(reservation.getNumberOfGuests()).append(" guests");
        }
        
        return message.toString();
    }

    /**
     * Builds HTML confirmation email content.
     * 
     * @param reservation the reservation
     * @return HTML email content
     */
    private String buildHtmlConfirmationEmail(Reservation reservation) {
        if (reservation == null) {
            return "<p>Reservation details not available.</p>";
        }
        
        String userName = (reservation.getUser() != null && reservation.getUser().getName() != null) 
            ? reservation.getUser().getName() : "Valued Customer";
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#2c3e50;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".reservation-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #3498db;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Reservation Confirmation</h2>");
        html.append("<p>Dear ").append(userName).append(",</p>");
        html.append("<p>Thank you for your reservation! Here are your details:</p>");
        html.append("<div class='reservation-details'>");
        html.append("<h3>Reservation Details</h3>");
        html.append("<p><strong>Reservation ID:</strong> #").append(reservation.getId() != null ? reservation.getId() : "N/A").append("</p>");
        
        if (reservation.getReservationDateTime() != null) {
            html.append("<p><strong>Date & Time:</strong> ").append(reservation.getReservationDateTime()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a"))).append("</p>");
        }
        
        if (reservation.getTable() != null && reservation.getTable().getName() != null) {
            html.append("<p><strong>Table:</strong> ").append(reservation.getTable().getName()).append("</p>");
        }
        
        if (reservation.getNumberOfGuests() != null) {
            html.append("<p><strong>Party Size:</strong> ").append(reservation.getNumberOfGuests()).append(" guests</p>");
        }
        
        if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) {
            html.append("<p><strong>Special Requests:</strong> ").append(reservation.getSpecialRequests()).append("</p>");
        }
        
        html.append("<p><strong>Status:</strong> ").append(reservation.getStatus() != null ? reservation.getStatus() : "PENDING").append("</p>");
        html.append("</div>");
        html.append("<p>We look forward to serving you!</p>");
        html.append("<p>Best regards,<br>").append(restaurantName).append(" Team</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    private String buildConfirmationEmail(Reservation reservation) {
        if (reservation == null) {
            return "Reservation details not available.";
        }
        
        StringBuilder email = new StringBuilder();
        String userName = (reservation.getUser() != null && reservation.getUser().getName() != null) 
            ? reservation.getUser().getName() : "Valued Customer";
        email.append("Dear ").append(userName).append(",\n\n");
        email.append("Thank you for your reservation! Here are the details:\n\n");
        email.append("Reservation ID: #").append(reservation.getId() != null ? reservation.getId() : "N/A").append("\n");
        
        if (reservation.getReservationDateTime() != null) {
            email.append("Date & Time: ").append(reservation.getReservationDateTime()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a"))).append("\n");
        }
        
        if (reservation.getTable() != null && reservation.getTable().getName() != null) {
            email.append("Table: ").append(reservation.getTable().getName()).append("\n");
        }
        
        if (reservation.getNumberOfGuests() != null) {
            email.append("Party Size: ").append(reservation.getNumberOfGuests()).append(" guests\n");
        }
        
        if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) {
            email.append("Special Requests: ").append(reservation.getSpecialRequests()).append("\n");
        }
        
        email.append("\nStatus: ").append(reservation.getStatus() != null ? reservation.getStatus() : "PENDING").append("\n\n");
        email.append("We look forward to serving you!\n\n");
        email.append("Best regards,\n");
        email.append(restaurantName).append(" Team\n\n");
        email.append("Contact: ").append(restaurantPhone).append("\n");
        email.append("Email: ").append(restaurantEmail);
        
        return email.toString();
    }

    /**
     * Builds HTML guest confirmation email content.
     * 
     * @param reservation the reservation
     * @return HTML email content
     */
    private String buildHtmlGuestConfirmationEmail(Reservation reservation) {
        if (reservation == null) {
            return "<p>Reservation details not available.</p>";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#2c3e50;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".reservation-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #3498db;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Reservation Confirmation</h2>");
        html.append("<p>Dear Guest,</p>");
        html.append("<p>Thank you for your reservation! Here are your details:</p>");
        html.append("<div class='reservation-details'>");
        html.append("<h3>Reservation Details</h3>");
        html.append("<p><strong>Reservation ID:</strong> #").append(reservation.getId() != null ? reservation.getId() : "N/A").append("</p>");
        
        if (reservation.getReservationDateTime() != null) {
            html.append("<p><strong>Date & Time:</strong> ").append(reservation.getReservationDateTime()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a"))).append("</p>");
        }
        
        if (reservation.getTable() != null && reservation.getTable().getName() != null) {
            html.append("<p><strong>Table:</strong> ").append(reservation.getTable().getName()).append("</p>");
        }
        
        if (reservation.getNumberOfGuests() != null) {
            html.append("<p><strong>Party Size:</strong> ").append(reservation.getNumberOfGuests()).append(" guests</p>");
        }
        
        if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) {
            html.append("<p><strong>Special Requests:</strong> ").append(reservation.getSpecialRequests()).append("</p>");
        }
        
        html.append("<p><strong>Status:</strong> ").append(reservation.getStatus() != null ? reservation.getStatus() : "PENDING").append("</p>");
        html.append("</div>");
        html.append("<p>We look forward to serving you!</p>");
        html.append("<p>Best regards,<br>").append(restaurantName).append(" Team</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    private String buildGuestConfirmationEmail(Reservation reservation) {
        if (reservation == null) {
            return "Reservation details not available.";
        }
        
        StringBuilder email = new StringBuilder();
        email.append("Dear Guest,\n\n");
        email.append("Thank you for your reservation! Here are the details:\n\n");
        email.append("Reservation ID: #").append(reservation.getId() != null ? reservation.getId() : "N/A").append("\n");
        
        if (reservation.getReservationDateTime() != null) {
            email.append("Date & Time: ").append(reservation.getReservationDateTime()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a"))).append("\n");
        }
        
        if (reservation.getTable() != null && reservation.getTable().getName() != null) {
            email.append("Table: ").append(reservation.getTable().getName()).append("\n");
        }
        
        if (reservation.getNumberOfGuests() != null) {
            email.append("Party Size: ").append(reservation.getNumberOfGuests()).append(" guests\n");
        }
        
        if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().isEmpty()) {
            email.append("Special Requests: ").append(reservation.getSpecialRequests()).append("\n");
        }
        
        email.append("\nStatus: ").append(reservation.getStatus() != null ? reservation.getStatus() : "PENDING").append("\n\n");
        email.append("We look forward to serving you!\n\n");
        email.append("Best regards,\n");
        email.append(restaurantName).append(" Team\n\n");
        email.append("Contact: ").append(restaurantPhone).append("\n");
        email.append("Email: ").append(restaurantEmail);
        
        return email.toString();
    }

    /**
     * Builds HTML reminder email content.
     * 
     * @param reservation the reservation
     * @return HTML email content
     */
    private String buildHtmlReminderEmail(Reservation reservation) {
        if (reservation == null) {
            return "<p>Reservation details not available.</p>";
        }
        
        String userName = (reservation.getUser() != null && reservation.getUser().getName() != null) 
            ? reservation.getUser().getName() : "Valued Customer";
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#e74c3c;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".reservation-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #e74c3c;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Reservation Reminder</h2>");
        html.append("<p>Dear ").append(userName).append(",</p>");
        html.append("<p>This is a friendly reminder about your upcoming reservation:</p>");
        html.append("<div class='reservation-details'>");
        html.append("<h3>Reservation Details</h3>");
        html.append("<p><strong>Reservation ID:</strong> #").append(reservation.getId() != null ? reservation.getId() : "N/A").append("</p>");
        
        if (reservation.getReservationDateTime() != null) {
            html.append("<p><strong>Date & Time:</strong> ").append(reservation.getReservationDateTime()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a"))).append("</p>");
        }
        
        if (reservation.getTable() != null && reservation.getTable().getName() != null) {
            html.append("<p><strong>Table:</strong> ").append(reservation.getTable().getName()).append("</p>");
        }
        
        if (reservation.getNumberOfGuests() != null) {
            html.append("<p><strong>Party Size:</strong> ").append(reservation.getNumberOfGuests()).append(" guests</p>");
        }
        
        html.append("</div>");
        html.append("<p>We can't wait to see you tomorrow!</p>");
        html.append("<p>Best regards,<br>").append(restaurantName).append(" Team</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    private String buildReminderEmail(Reservation reservation) {
        if (reservation == null) {
            return "Reservation details not available.";
        }
        
        StringBuilder email = new StringBuilder();
        String userName = (reservation.getUser() != null && reservation.getUser().getName() != null) 
            ? reservation.getUser().getName() : "Valued Customer";
        email.append("Dear ").append(userName).append(",\n\n");
        email.append("This is a friendly reminder about your upcoming reservation:\n\n");
        email.append("Reservation ID: #").append(reservation.getId() != null ? reservation.getId() : "N/A").append("\n");
        
        if (reservation.getReservationDateTime() != null) {
            email.append("Date & Time: ").append(reservation.getReservationDateTime()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a"))).append("\n");
        }
        
        if (reservation.getTable() != null && reservation.getTable().getName() != null) {
            email.append("Table: ").append(reservation.getTable().getName()).append("\n");
        }
        
        if (reservation.getNumberOfGuests() != null) {
            email.append("Party Size: ").append(reservation.getNumberOfGuests()).append(" guests\n\n");
        }
        
        email.append("We can't wait to see you tomorrow!\n\n");
        email.append("Best regards,\n");
        email.append(restaurantName).append(" Team");
        
        return email.toString();
    }

    /**
     * Sends a schedule notification to staff members.
     * 
     * @param schedule the schedule to notify about
     */
    @Async
    public void sendScheduleNotification(Schedule schedule) {
        if (!validateSchedule(schedule)) {
            logger.error("Cannot send schedule notification: invalid schedule data");
            return;
        }
        
        String staffEmail = schedule.getStaff().getEmail();
        if (!isValidEmail(staffEmail)) {
            logger.error("Cannot send schedule notification: invalid email address: {}", staffEmail);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(staffEmail);
            helper.setFrom(restaurantEmail);
            helper.setSubject("New Schedule Assignment - " + restaurantName);
            
            String htmlContent = buildHtmlScheduleNotification(schedule);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Schedule notification sent successfully to: {}", staffEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send schedule notification to {}: {}", staffEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending schedule notification to {}: {}", staffEmail, e.getMessage(), e);
        }
    }

    /**
     * Sends a schedule update notification to staff members.
     * 
     * @param schedule the updated schedule
     */
    @Async
    public void sendScheduleUpdateNotification(Schedule schedule) {
        if (!validateSchedule(schedule)) {
            logger.error("Cannot send schedule update notification: invalid schedule data");
            return;
        }
        
        String staffEmail = schedule.getStaff().getEmail();
        if (!isValidEmail(staffEmail)) {
            logger.error("Cannot send schedule update notification: invalid email address: {}", staffEmail);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(staffEmail);
            helper.setFrom(restaurantEmail);
            helper.setSubject("Schedule Update - " + restaurantName);
            
            String htmlContent = buildHtmlScheduleUpdateNotification(schedule);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Schedule update notification sent successfully to: {}", staffEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send schedule update notification to {}: {}", staffEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending schedule update notification to {}: {}", staffEmail, e.getMessage(), e);
        }
    }

    /**
     * Sends a schedule cancellation notification to staff members.
     * 
     * @param schedule the cancelled schedule
     */
    @Async
    public void sendScheduleCancellationNotification(Schedule schedule) {
        if (!validateSchedule(schedule)) {
            logger.error("Cannot send schedule cancellation notification: invalid schedule data");
            return;
        }
        
        String staffEmail = schedule.getStaff().getEmail();
        if (!isValidEmail(staffEmail)) {
            logger.error("Cannot send schedule cancellation notification: invalid email address: {}", staffEmail);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(staffEmail);
            helper.setFrom(restaurantEmail);
            helper.setSubject("Schedule Cancellation - " + restaurantName);
            
            String htmlContent = buildHtmlScheduleCancellationNotification(schedule);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("Schedule cancellation notification sent successfully to: {}", staffEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send schedule cancellation notification to {}: {}", staffEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error sending schedule cancellation notification to {}: {}", staffEmail, e.getMessage(), e);
        }
    }

    /**
     * Validates schedule data for notification sending.
     * 
     * @param schedule the schedule to validate
     * @return true if valid, false otherwise
     */
    private boolean validateSchedule(Schedule schedule) {
        if (schedule == null) {
            return false;
        }
        
        if (schedule.getStaff() == null) {
            return false;
        }
        
        if (!StringUtils.hasText(schedule.getStaff().getEmail())) {
            return false;
        }
        
        return true;
    }

    /**
     * Builds HTML schedule notification email content.
     * 
     * @param schedule the schedule
     * @return HTML email content
     */
    private String buildHtmlScheduleNotification(Schedule schedule) {
        if (schedule == null) {
            return "<p>Schedule details not available.</p>";
        }
        
        String staffName = (schedule.getStaff() != null && schedule.getStaff().getName() != null) 
            ? schedule.getStaff().getName() : "Staff Member";
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#27ae60;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".schedule-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #27ae60;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>New Schedule Assignment</h2>");
        html.append("<p>Dear ").append(staffName).append(",</p>");
        html.append("<p>You have been assigned a new shift. Here are the details:</p>");
        html.append("<div class='schedule-details'>");
        html.append("<h3>Schedule Details</h3>");
        html.append("<p><strong>Schedule ID:</strong> #").append(schedule.getId() != null ? schedule.getId() : "N/A").append("</p>");
        
        if (schedule.getScheduleDate() != null) {
            html.append("<p><strong>Date:</strong> ").append(schedule.getScheduleDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))).append("</p>");
        }
        
        if (schedule.getStartTime() != null) {
            html.append("<p><strong>Start Time:</strong> ").append(schedule.getStartTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        if (schedule.getEndTime() != null) {
            html.append("<p><strong>End Time:</strong> ").append(schedule.getEndTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        if (schedule.getStaff() != null && schedule.getStaff().getPosition() != null) {
            html.append("<p><strong>Position:</strong> ").append(schedule.getStaff().getPosition()).append("</p>");
        }
        
        if (schedule.getNotes() != null && !schedule.getNotes().isEmpty()) {
            html.append("<p><strong>Notes:</strong> ").append(schedule.getNotes()).append("</p>");
        }
        
        html.append("<p><strong>Status:</strong> ").append(schedule.getStatus() != null ? schedule.getStatus() : "SCHEDULED").append("</p>");
        html.append("</div>");
        html.append("<p>Please confirm your availability for this shift.</p>");
        html.append("<p>Best regards,<br>").append(restaurantName).append(" Management</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML schedule update notification email content.
     * 
     * @param schedule the updated schedule
     * @return HTML email content
     */
    private String buildHtmlScheduleUpdateNotification(Schedule schedule) {
        if (schedule == null) {
            return "<p>Schedule details not available.</p>";
        }
        
        String staffName = (schedule.getStaff() != null && schedule.getStaff().getName() != null) 
            ? schedule.getStaff().getName() : "Staff Member";
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#f39c12;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".schedule-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #f39c12;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Schedule Update</h2>");
        html.append("<p>Dear ").append(staffName).append(",</p>");
        html.append("<p>Your schedule has been updated. Here are the new details:</p>");
        html.append("<div class='schedule-details'>");
        html.append("<h3>Updated Schedule Details</h3>");
        html.append("<p><strong>Schedule ID:</strong> #").append(schedule.getId() != null ? schedule.getId() : "N/A").append("</p>");
        
        if (schedule.getScheduleDate() != null) {
            html.append("<p><strong>Date:</strong> ").append(schedule.getScheduleDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))).append("</p>");
        }
        
        if (schedule.getStartTime() != null) {
            html.append("<p><strong>Start Time:</strong> ").append(schedule.getStartTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        if (schedule.getEndTime() != null) {
            html.append("<p><strong>End Time:</strong> ").append(schedule.getEndTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        if (schedule.getStaff() != null && schedule.getStaff().getPosition() != null) {
            html.append("<p><strong>Position:</strong> ").append(schedule.getStaff().getPosition()).append("</p>");
        }
        
        if (schedule.getNotes() != null && !schedule.getNotes().isEmpty()) {
            html.append("<p><strong>Notes:</strong> ").append(schedule.getNotes()).append("</p>");
        }
        
        html.append("<p><strong>Status:</strong> ").append(schedule.getStatus() != null ? schedule.getStatus() : "SCHEDULED").append("</p>");
        html.append("</div>");
        html.append("<p>Please review the updated schedule and confirm your availability.</p>");
        html.append("<p>Best regards,<br>").append(restaurantName).append(" Management</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML schedule cancellation notification email content.
     * 
     * @param schedule the cancelled schedule
     * @return HTML email content
     */
    private String buildHtmlScheduleCancellationNotification(Schedule schedule) {
        if (schedule == null) {
            return "<p>Schedule details not available.</p>";
        }
        
        String staffName = (schedule.getStaff() != null && schedule.getStaff().getName() != null) 
            ? schedule.getStaff().getName() : "Staff Member";
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#e74c3c;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".schedule-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #e74c3c;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Schedule Cancellation</h2>");
        html.append("<p>Dear ").append(staffName).append(",</p>");
        html.append("<p>Your scheduled shift has been cancelled. Here are the details:</p>");
        html.append("<div class='schedule-details'>");
        html.append("<h3>Cancelled Schedule Details</h3>");
        html.append("<p><strong>Schedule ID:</strong> #").append(schedule.getId() != null ? schedule.getId() : "N/A").append("</p>");
        
        if (schedule.getScheduleDate() != null) {
            html.append("<p><strong>Date:</strong> ").append(schedule.getScheduleDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))).append("</p>");
        }
        
        if (schedule.getStartTime() != null) {
            html.append("<p><strong>Start Time:</strong> ").append(schedule.getStartTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        if (schedule.getEndTime() != null) {
            html.append("<p><strong>End Time:</strong> ").append(schedule.getEndTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        if (schedule.getStaff() != null && schedule.getStaff().getPosition() != null) {
            html.append("<p><strong>Position:</strong> ").append(schedule.getStaff().getPosition()).append("</p>");
        }
        
        html.append("</div>");
        html.append("<p>We apologize for any inconvenience. Please contact management if you have any questions.</p>");
        html.append("<p>Best regards,<br>").append(restaurantName).append(" Management</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Sends an event booking confirmation email to the client.
     * 
     * @param booking the event booking to confirm
     */
    @Async
    public void sendEventBookingConfirmation(EventBooking booking) {
        if (booking == null || booking.getClientEmail() == null) {
            logger.warn("Cannot send event booking confirmation: booking or email is null");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getClientEmail());
            helper.setSubject("Event Booking Confirmation - " + restaurantName);
            helper.setText(buildHtmlEventBookingConfirmation(booking), true);

            mailSender.send(message);
            logger.info("Event booking confirmation email sent to: {}", booking.getClientEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send event booking confirmation email to: {}", 
                        booking.getClientEmail(), e);
        }
    }

    /**
     * Sends an event booking update notification email to the client.
     * 
     * @param booking the updated event booking
     */
    @Async
    public void sendEventBookingUpdate(EventBooking booking) {
        if (booking == null || booking.getClientEmail() == null) {
            logger.warn("Cannot send event booking update: booking or email is null");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getClientEmail());
            helper.setSubject("Event Booking Update - " + restaurantName);
            helper.setText(buildHtmlEventBookingUpdate(booking), true);

            mailSender.send(message);
            logger.info("Event booking update email sent to: {}", booking.getClientEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send event booking update email to: {}", 
                        booking.getClientEmail(), e);
        }
    }

    /**
     * Sends an event booking cancellation notification email to the client.
     * 
     * @param booking the cancelled event booking
     */
    @Async
    public void sendEventBookingCancellation(EventBooking booking) {
        if (booking == null || booking.getClientEmail() == null) {
            logger.warn("Cannot send event booking cancellation: booking or email is null");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getClientEmail());
            helper.setSubject("Event Booking Cancellation - " + restaurantName);
            helper.setText(buildHtmlEventBookingCancellation(booking), true);

            mailSender.send(message);
            logger.info("Event booking cancellation email sent to: {}", booking.getClientEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send event booking cancellation email to: {}", 
                        booking.getClientEmail(), e);
        }
    }

    /**
     * Builds HTML event booking confirmation email content.
     * 
     * @param booking the event booking
     * @return HTML email content
     */
    private String buildHtmlEventBookingConfirmation(EventBooking booking) {
        if (booking == null) {
            return "<p>Event booking details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#2c3e50;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".booking-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #3498db;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Event Booking Confirmation</h2>");
        html.append("<p>Dear ").append(booking.getClientName()).append(",</p>");
        html.append("<p>Thank you for booking your event with us! Your booking has been confirmed.</p>");
        html.append("<div class='booking-details'>");
        html.append("<h3>Booking Details</h3>");
        html.append("<p><strong>Booking ID:</strong> #").append(booking.getId() != null ? booking.getId() : "N/A").append("</p>");
        
        if (booking.getEvent() != null) {
            html.append("<p><strong>Event:</strong> ").append(booking.getEvent().getEventName()).append("</p>");
            html.append("<p><strong>Event Type:</strong> ").append(booking.getEvent().getEventType().getDisplayName()).append("</p>");
        }
        
        if (booking.getEventDate() != null) {
            html.append("<p><strong>Date:</strong> ").append(booking.getEventDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))).append("</p>");
        }
        
        if (booking.getStartTime() != null) {
            html.append("<p><strong>Start Time:</strong> ").append(booking.getStartTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        if (booking.getEndTime() != null) {
            html.append("<p><strong>End Time:</strong> ").append(booking.getEndTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        html.append("<p><strong>Guest Count:</strong> ").append(booking.getGuestCount() != null ? booking.getGuestCount() : "N/A").append("</p>");
        html.append("<p><strong>Total Price:</strong> $").append(booking.getTotalPrice() != null ? String.format("%.2f", booking.getTotalPrice()) : "0.00").append("</p>");
        
        if (booking.getSpecialRequirements() != null && !booking.getSpecialRequirements().trim().isEmpty()) {
            html.append("<p><strong>Special Requirements:</strong> ").append(booking.getSpecialRequirements()).append("</p>");
        }
        
        html.append("</div>");
        html.append("<p>We look forward to hosting your event! If you have any questions, please contact us.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML event booking update notification email content.
     * 
     * @param booking the updated event booking
     * @return HTML email content
     */
    private String buildHtmlEventBookingUpdate(EventBooking booking) {
        if (booking == null) {
            return "<p>Event booking details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#f39c12;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".booking-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #f39c12;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Event Booking Update</h2>");
        html.append("<p>Dear ").append(booking.getClientName()).append(",</p>");
        html.append("<p>Your event booking has been updated. Here are the current details:</p>");
        html.append("<div class='booking-details'>");
        html.append("<h3>Updated Booking Details</h3>");
        html.append("<p><strong>Booking ID:</strong> #").append(booking.getId() != null ? booking.getId() : "N/A").append("</p>");
        
        if (booking.getEvent() != null) {
            html.append("<p><strong>Event:</strong> ").append(booking.getEvent().getEventName()).append("</p>");
            html.append("<p><strong>Event Type:</strong> ").append(booking.getEvent().getEventType().getDisplayName()).append("</p>");
        }
        
        if (booking.getEventDate() != null) {
            html.append("<p><strong>Date:</strong> ").append(booking.getEventDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))).append("</p>");
        }
        
        if (booking.getStartTime() != null) {
            html.append("<p><strong>Start Time:</strong> ").append(booking.getStartTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        if (booking.getEndTime() != null) {
            html.append("<p><strong>End Time:</strong> ").append(booking.getEndTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        html.append("<p><strong>Guest Count:</strong> ").append(booking.getGuestCount() != null ? booking.getGuestCount() : "N/A").append("</p>");
        html.append("<p><strong>Total Price:</strong> $").append(booking.getTotalPrice() != null ? String.format("%.2f", booking.getTotalPrice()) : "0.00").append("</p>");
        
        if (booking.getSpecialRequirements() != null && !booking.getSpecialRequirements().trim().isEmpty()) {
            html.append("<p><strong>Special Requirements:</strong> ").append(booking.getSpecialRequirements()).append("</p>");
        }
        
        html.append("</div>");
        html.append("<p>If you have any questions about these changes, please contact us.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML event booking cancellation notification email content.
     * 
     * @param booking the cancelled event booking
     * @return HTML email content
     */
    private String buildHtmlEventBookingCancellation(EventBooking booking) {
        if (booking == null) {
            return "<p>Event booking details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#e74c3c;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".booking-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #e74c3c;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Event Booking Cancellation</h2>");
        html.append("<p>Dear ").append(booking.getClientName()).append(",</p>");
        html.append("<p>Your event booking has been cancelled. Here are the details of the cancelled booking:</p>");
        html.append("<div class='booking-details'>");
        html.append("<h3>Cancelled Booking Details</h3>");
        html.append("<p><strong>Booking ID:</strong> #").append(booking.getId() != null ? booking.getId() : "N/A").append("</p>");
        
        if (booking.getEvent() != null) {
            html.append("<p><strong>Event:</strong> ").append(booking.getEvent().getEventName()).append("</p>");
            html.append("<p><strong>Event Type:</strong> ").append(booking.getEvent().getEventType().getDisplayName()).append("</p>");
        }
        
        if (booking.getEventDate() != null) {
            html.append("<p><strong>Date:</strong> ").append(booking.getEventDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))).append("</p>");
        }
        
        if (booking.getStartTime() != null) {
            html.append("<p><strong>Start Time:</strong> ").append(booking.getStartTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        if (booking.getEndTime() != null) {
            html.append("<p><strong>End Time:</strong> ").append(booking.getEndTime()
                .format(DateTimeFormatter.ofPattern("h:mm a"))).append("</p>");
        }
        
        html.append("</div>");
        html.append("<p>We're sorry for any inconvenience. If you'd like to book another event, please contact us.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Sends an order ready notification to front-end staff.
     * 
     * @param order the ready order
     */
    @Async
    public void sendOrderReadyNotification(Order order) {
        if (order == null) {
            logger.warn("Cannot send order ready notification: order is null");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("frontend@restaurant.com"); // Front-end staff email
            helper.setSubject("Order Ready - " + restaurantName);
            helper.setText(buildHtmlOrderReadyNotification(order), true);

            mailSender.send(message);
            logger.info("Order ready notification sent for order: {}", order.getId());

        } catch (MessagingException e) {
            logger.error("Failed to send order ready notification for order: {}", order.getId(), e);
        }
    }

    /**
     * Sends a low stock alert to the Head Chef.
     * 
     * @param lowStockItems the items with low stock
     */
    @Async
    public void sendLowStockAlert(List<InventoryItem> lowStockItems) {
        if (lowStockItems == null || lowStockItems.isEmpty()) {
            logger.warn("Cannot send low stock alert: no items provided");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("headchef@restaurant.com"); // Head Chef email
            helper.setSubject("Low Stock Alert - " + restaurantName);
            helper.setText(buildHtmlLowStockAlert(lowStockItems), true);

            mailSender.send(message);
            logger.info("Low stock alert sent for {} items", lowStockItems.size());

        } catch (MessagingException e) {
            logger.error("Failed to send low stock alert", e);
        }
    }

    /**
     * Sends a task delay notification to front-end staff.
     * 
     * @param task the delayed task
     */
    @Async
    public void sendTaskDelayNotification(KitchenTask task) {
        if (task == null) {
            logger.warn("Cannot send task delay notification: task is null");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("frontend@restaurant.com"); // Front-end staff email
            helper.setSubject("Kitchen Task Delayed - " + restaurantName);
            helper.setText(buildHtmlTaskDelayNotification(task), true);

            mailSender.send(message);
            logger.info("Task delay notification sent for task: {}", task.getId());

        } catch (MessagingException e) {
            logger.error("Failed to send task delay notification for task: {}", task.getId(), e);
        }
    }

    /**
     * Builds HTML order ready notification email content.
     * 
     * @param order the ready order
     * @return HTML email content
     */
    private String buildHtmlOrderReadyNotification(Order order) {
        if (order == null) {
            return "<p>Order details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#4caf50;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".order-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #4caf50;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Order Ready for Pickup</h2>");
        html.append("<p>An order is ready for pickup. Please notify the customer.</p>");
        html.append("<div class='order-details'>");
        html.append("<h3>Order Details</h3>");
        html.append("<p><strong>Order ID:</strong> #").append(order.getId() != null ? order.getId() : "N/A").append("</p>");
        html.append("<p><strong>Order Type:</strong> ").append(order.getOrderType() != null ? order.getOrderType().name() : "N/A").append("</p>");
        html.append("<p><strong>Total Amount:</strong> $").append(order.getTotalAmount() != null ? String.format("%.2f", order.getTotalAmount()) : "0.00").append("</p>");
        
        if (order.getSpecialInstructions() != null && !order.getSpecialInstructions().trim().isEmpty()) {
            html.append("<p><strong>Special Instructions:</strong> ").append(order.getSpecialInstructions()).append("</p>");
        }
        
        html.append("</div>");
        html.append("<p>Please prepare the order for customer pickup.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML low stock alert email content.
     * 
     * @param lowStockItems the items with low stock
     * @return HTML email content
     */
    private String buildHtmlLowStockAlert(List<InventoryItem> lowStockItems) {
        if (lowStockItems == null || lowStockItems.isEmpty()) {
            return "<p>No low stock items found.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#ff9800;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".item-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #ff9800;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Low Stock Alert</h2>");
        html.append("<p>The following items are running low on stock and need to be restocked:</p>");
        
        for (InventoryItem item : lowStockItems) {
            html.append("<div class='item-details'>");
            html.append("<h4>").append(item.getItemName()).append("</h4>");
            html.append("<p><strong>Current Stock:</strong> ").append(item.getCurrentStock()).append(" ").append(item.getUnit()).append("</p>");
            html.append("<p><strong>Minimum Required:</strong> ").append(item.getMinimumStock()).append(" ").append(item.getUnit()).append("</p>");
            html.append("<p><strong>Supplier:</strong> ").append(item.getSupplier()).append("</p>");
            html.append("</div>");
        }
        
        html.append("<p><strong>Action Required:</strong> Please contact suppliers to restock these items immediately.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Sends an event staff assignment notification to staff members.
     * 
     * @param assignment the staff assignment
     */
    @Async
    public void sendEventStaffAssignmentNotification(EventStaffAssignment assignment) {
        if (assignment == null || assignment.getStaff() == null) {
            logger.warn("Cannot send event staff assignment notification: assignment or staff is null");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(assignment.getStaff().getEmail());
            helper.setSubject("Event Assignment - " + restaurantName);
            helper.setText(buildHtmlEventStaffAssignmentNotification(assignment), true);

            mailSender.send(message);
            logger.info("Event staff assignment notification sent to: {}", assignment.getStaff().getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send event staff assignment notification to: {}", 
                        assignment.getStaff().getEmail(), e);
        }
    }

    /**
     * Sends an event schedule confirmation notification to assigned staff.
     * 
     * @param assignment the staff assignment
     */
    @Async
    public void sendEventScheduleConfirmationNotification(EventStaffAssignment assignment) {
        if (assignment == null || assignment.getStaff() == null) {
            logger.warn("Cannot send event schedule confirmation: assignment or staff is null");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(assignment.getStaff().getEmail());
            helper.setSubject("Event Schedule Confirmed - " + restaurantName);
            helper.setText(buildHtmlEventScheduleConfirmationNotification(assignment), true);

            mailSender.send(message);
            logger.info("Event schedule confirmation sent to: {}", assignment.getStaff().getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send event schedule confirmation to: {}", 
                        assignment.getStaff().getEmail(), e);
        }
    }

    /**
     * Builds HTML task delay notification email content.
     * 
     * @param task the delayed task
     * @return HTML email content
     */
    private String buildHtmlTaskDelayNotification(KitchenTask task) {
        if (task == null) {
            return "<p>Task details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#f44336;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".task-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #f44336;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Kitchen Task Delayed</h2>");
        html.append("<p>A kitchen task is taking longer than expected. Please inform the customer about the delay.</p>");
        html.append("<div class='task-details'>");
        html.append("<h3>Task Details</h3>");
        html.append("<p><strong>Task ID:</strong> #").append(task.getId() != null ? task.getId() : "N/A").append("</p>");
        html.append("<p><strong>Item:</strong> ").append(task.getItemName()).append("</p>");
        html.append("<p><strong>Quantity:</strong> ").append(task.getQuantity()).append("</p>");
        html.append("<p><strong>Assigned Staff:</strong> ").append(task.getStaff() != null ? task.getStaff().getName() : "N/A").append("</p>");
        html.append("<p><strong>Estimated Duration:</strong> ").append(task.getEstimatedDurationMinutes()).append(" minutes</p>");
        
        if (task.getDietaryNotes() != null && !task.getDietaryNotes().trim().isEmpty()) {
            html.append("<p><strong>Dietary Notes:</strong> ").append(task.getDietaryNotes()).append("</p>");
        }
        
        html.append("</div>");
        html.append("<p>Please update the customer about the estimated completion time.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML event staff assignment notification email content.
     * 
     * @param assignment the staff assignment
     * @return HTML email content
     */
    private String buildHtmlEventStaffAssignmentNotification(EventStaffAssignment assignment) {
        if (assignment == null) {
            return "<p>Assignment details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#2196f3;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".assignment-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #2196f3;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Event Assignment</h2>");
        html.append("<p>You have been assigned to work at an upcoming event. Please review the details below.</p>");
        html.append("<div class='assignment-details'>");
        html.append("<h3>Assignment Details</h3>");
        html.append("<p><strong>Event:</strong> ").append(assignment.getEventBooking().getEvent().getEventName()).append("</p>");
        html.append("<p><strong>Date:</strong> ").append(assignment.getEventBooking().getEventDate()).append("</p>");
        html.append("<p><strong>Time:</strong> ").append(assignment.getStartTime()).append(" - ").append(assignment.getEndTime()).append("</p>");
        html.append("<p><strong>Role:</strong> ").append(assignment.getRole()).append("</p>");
        html.append("<p><strong>Assigned Hours:</strong> ").append(assignment.getAssignedHours()).append(" hours</p>");
        html.append("<p><strong>Client:</strong> ").append(assignment.getEventBooking().getClientName()).append("</p>");
        
        if (assignment.getNotes() != null && !assignment.getNotes().trim().isEmpty()) {
            html.append("<p><strong>Notes:</strong> ").append(assignment.getNotes()).append("</p>");
        }
        
        html.append("</div>");
        html.append("<p>Please confirm your availability and contact the supervisor if you have any questions.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML event schedule confirmation notification email content.
     * 
     * @param assignment the staff assignment
     * @return HTML email content
     */
    private String buildHtmlEventScheduleConfirmationNotification(EventStaffAssignment assignment) {
        if (assignment == null) {
            return "<p>Assignment details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#4caf50;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".confirmation-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #4caf50;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Event Schedule Confirmed</h2>");
        html.append("<p>The event schedule has been confirmed. Please prepare for your assigned role.</p>");
        html.append("<div class='confirmation-details'>");
        html.append("<h3>Confirmed Assignment</h3>");
        html.append("<p><strong>Event:</strong> ").append(assignment.getEventBooking().getEvent().getEventName()).append("</p>");
        html.append("<p><strong>Date:</strong> ").append(assignment.getEventBooking().getEventDate()).append("</p>");
        html.append("<p><strong>Time:</strong> ").append(assignment.getStartTime()).append(" - ").append(assignment.getEndTime()).append("</p>");
        html.append("<p><strong>Role:</strong> ").append(assignment.getRole()).append("</p>");
        html.append("<p><strong>Status:</strong> ").append(assignment.getStatus().getDisplayName()).append("</p>");
        
        if (assignment.getNotes() != null && !assignment.getNotes().trim().isEmpty()) {
            html.append("<p><strong>Notes:</strong> ").append(assignment.getNotes()).append("</p>");
        }
        
        html.append("</div>");
        html.append("<p>Please arrive 15 minutes before your scheduled start time.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Sends a feedback response notification to the customer.
     * 
     * @param feedback the customer feedback
     * @param response the feedback response
     */
    @Async
    public void sendFeedbackResponseNotification(CustomerFeedback feedback, FeedbackResponse response) {
        if (feedback == null || response == null) {
            logger.warn("Cannot send feedback response notification: feedback or response is null");
            return;
        }

        // Skip notification for anonymous feedback
        if (feedback.getIsAnonymous()) {
            logger.info("Skipping notification for anonymous feedback: {}", feedback.getId());
            return;
        }

        String customerEmail = feedback.getCustomerEmail();
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            logger.warn("Cannot send feedback response notification: customer email is null or empty");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(customerEmail);
            helper.setSubject("Response to Your Feedback - " + restaurantName);
            helper.setText(buildHtmlFeedbackResponseNotification(feedback, response), true);

            mailSender.send(message);
            logger.info("Feedback response notification sent to: {}", customerEmail);

        } catch (MessagingException e) {
            logger.error("Failed to send feedback response notification to: {}", customerEmail, e);
        }
    }


    /**
     * Sends a promotional offer notification to the customer.
     * 
     * @param feedback the customer feedback
     * @param response the promotional offer response
     */
    @Async
    public void sendPromotionalOfferNotification(CustomerFeedback feedback, FeedbackResponse response) {
        if (feedback == null || response == null) {
            logger.warn("Cannot send promotional offer notification: feedback or response is null");
            return;
        }

        // Skip notification for anonymous feedback
        if (feedback.getIsAnonymous()) {
            logger.info("Skipping promotional offer notification for anonymous feedback: {}", feedback.getId());
            return;
        }

        String customerEmail = feedback.getCustomerEmail();
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            logger.warn("Cannot send promotional offer notification: customer email is null or empty");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(customerEmail);
            helper.setSubject("Special Offer for You - " + restaurantName);
            helper.setText(buildHtmlPromotionalOfferNotification(feedback, response), true);

            mailSender.send(message);
            logger.info("Promotional offer notification sent to: {}", customerEmail);

        } catch (MessagingException e) {
            logger.error("Failed to send promotional offer notification to: {}", customerEmail, e);
        }
    }

    /**
     * Sends a satisfaction report to management.
     * 
     * @param report the satisfaction report
     */
    @Async
    public void sendSatisfactionReportToManagement(SatisfactionReport report) {
        if (report == null) {
            logger.warn("Cannot send satisfaction report: report is null");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("management@restaurant.com"); // Management email
            helper.setSubject("Satisfaction Report - " + restaurantName);
            helper.setText(buildHtmlSatisfactionReport(report), true);

            mailSender.send(message);
            logger.info("Satisfaction report sent to management for period: {} to {}", 
                       report.getPeriodStart(), report.getPeriodEnd());

        } catch (MessagingException e) {
            logger.error("Failed to send satisfaction report to management", e);
        }
    }

    /**
     * Schedules a follow-up reminder for escalated feedback.
     * 
     * @param feedback the escalated feedback
     * @param followUpDate the follow-up date
     */
    @Async
    public void scheduleFollowUpReminder(CustomerFeedback feedback, LocalDateTime followUpDate) {
        if (feedback == null || followUpDate == null) {
            logger.warn("Cannot schedule follow-up reminder: feedback or follow-up date is null");
            return;
        }

        // In a real implementation, this would use a job scheduler
        // For now, we'll just log the reminder
        logger.info("Follow-up reminder scheduled for feedback ID: {} on {}", 
                   feedback.getId(), followUpDate);
    }

    /**
     * Sends a technical support alert.
     * 
     * @param subject the alert subject
     * @param message the alert message
     * @param error the error that occurred
     */
    @Async
    public void sendTechnicalSupportAlert(String subject, String message, Exception error) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo("techsupport@restaurant.com"); // Technical support email
            helper.setSubject("Technical Alert - " + subject);
            helper.setText(buildHtmlTechnicalSupportAlert(subject, message, error), true);

            mailSender.send(mimeMessage);
            logger.info("Technical support alert sent: {}", subject);

        } catch (MessagingException e) {
            logger.error("Failed to send technical support alert: {}", subject, e);
        }
    }

    /**
     * Builds HTML feedback response notification email content.
     * 
     * @param feedback the customer feedback
     * @param response the feedback response
     * @return HTML email content
     */
    private String buildHtmlFeedbackResponseNotification(CustomerFeedback feedback, FeedbackResponse response) {
        if (feedback == null || response == null) {
            return "<p>Feedback details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#2196f3;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".feedback-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #2196f3;}");
        html.append(".response-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #4caf50;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Response to Your Feedback</h2>");
        html.append("<p>Dear ").append(feedback.getCustomerName()).append(",</p>");
        html.append("<p>Thank you for your feedback. We have reviewed your comments and would like to respond:</p>");
        
        html.append("<div class='feedback-details'>");
        html.append("<h3>Your Feedback</h3>");
        html.append("<p><strong>Rating:</strong> ").append(feedback.getRating()).append("/5 stars</p>");
        html.append("<p><strong>Type:</strong> ").append(feedback.getFeedbackType().getDisplayName()).append("</p>");
        if (feedback.getComment() != null && !feedback.getComment().trim().isEmpty()) {
            html.append("<p><strong>Comment:</strong> ").append(feedback.getComment()).append("</p>");
        }
        html.append("</div>");
        
        html.append("<div class='response-details'>");
        html.append("<h3>Our Response</h3>");
        html.append("<p><strong>Response Type:</strong> ").append(response.getResponseType().getDisplayName()).append("</p>");
        html.append("<p>").append(response.getResponseText()).append("</p>");
        html.append("<p><strong>Responded by:</strong> ").append(response.getResponderName()).append("</p>");
        html.append("</div>");
        
        html.append("<p>We appreciate your business and look forward to serving you again.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML promotional offer notification email content.
     * 
     * @param feedback the customer feedback
     * @param response the promotional offer response
     * @return HTML email content
     */
    private String buildHtmlPromotionalOfferNotification(CustomerFeedback feedback, FeedbackResponse response) {
        if (feedback == null || response == null) {
            return "<p>Offer details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#ff9800;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".offer-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #ff9800;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Special Offer for You</h2>");
        html.append("<p>Dear ").append(feedback.getCustomerName()).append(",</p>");
        html.append("<p>We value your feedback and would like to make it up to you with a special offer:</p>");
        
        html.append("<div class='offer-details'>");
        html.append("<h3>Your Special Offer</h3>");
        html.append("<p>").append(response.getOfferDetails()).append("</p>");
        html.append("<p><strong>Valid until:</strong> ").append(LocalDateTime.now().plusMonths(1).toLocalDate()).append("</p>");
        html.append("</div>");
        
        html.append("<p>We hope to see you again soon and provide you with an excellent dining experience.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>Contact: ").append(restaurantPhone).append(" | Email: ").append(restaurantEmail).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML satisfaction report email content.
     * 
     * @param report the satisfaction report
     * @return HTML email content
     */
    private String buildHtmlSatisfactionReport(SatisfactionReport report) {
        if (report == null) {
            return "<p>Report details not available.</p>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:800px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#9c27b0;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".summary{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #9c27b0;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>").append(restaurantName).append("</h1></div>");
        html.append("<div class='content'><h2>Satisfaction Report</h2>");
        html.append("<p><strong>Report Type:</strong> ").append(report.getReportType().getDisplayName()).append("</p>");
        html.append("<p><strong>Period:</strong> ").append(report.getPeriodStart()).append(" to ").append(report.getPeriodEnd()).append("</p>");
        
        html.append("<div class='summary'>");
        html.append("<h3>Executive Summary</h3>");
        html.append("<p>").append(report.getSummary()).append("</p>");
        
        if (report.getCriticalIssuesSummary() != null && !report.getCriticalIssuesSummary().trim().isEmpty()) {
            html.append("<h4>Critical Issues</h4>");
            html.append("<p>").append(report.getCriticalIssuesSummary()).append("</p>");
        }
        
        if (report.getRecommendations() != null && !report.getRecommendations().trim().isEmpty()) {
            html.append("<h4>Recommendations</h4>");
            html.append("<p>").append(report.getRecommendations()).append("</p>");
        }
        html.append("</div>");
        
        html.append("</div><div class='footer'>");
        html.append("<p>Generated by: ").append(report.getGeneratedBy()).append(" | ").append(restaurantName).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML technical support alert email content.
     * 
     * @param subject the alert subject
     * @param message the alert message
     * @param error the error that occurred
     * @return HTML email content
     */
    private String buildHtmlTechnicalSupportAlert(String subject, String message, Exception error) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#f44336;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".error-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #f44336;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>Technical Alert</h1></div>");
        html.append("<div class='content'><h2>").append(subject).append("</h2>");
        html.append("<p>").append(message).append("</p>");
        
        html.append("<div class='error-details'>");
        html.append("<h3>Error Details</h3>");
        html.append("<p><strong>Error:</strong> ").append(error.getMessage()).append("</p>");
        html.append("<p><strong>Time:</strong> ").append(LocalDateTime.now()).append("</p>");
        html.append("</div>");
        
        html.append("<p>Please investigate and resolve this issue as soon as possible.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>").append(restaurantName).append(" - Technical Support</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML email for order ready notification
     */
    private String buildOrderReadyEmail(Order order) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#4CAF50;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".order-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #4CAF50;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>Order Ready!</h1></div>");
        html.append("<div class='content'><h2>Your order is ready for pickup</h2>");
        html.append("<p>Dear ").append(order.getUser().getName()).append(",</p>");
        html.append("<p>Great news! Your order #").append(order.getId()).append(" is ready for pickup.</p>");
        
        html.append("<div class='order-details'>");
        html.append("<h3>Order Details</h3>");
        html.append("<p><strong>Order #:</strong> ").append(order.getId()).append("</p>");
        html.append("<p><strong>Total Amount:</strong> $").append(order.getTotalAmount()).append("</p>");
        html.append("<p><strong>Order Type:</strong> ").append(order.getOrderType()).append("</p>");
        html.append("</div>");
        
        html.append("<p>Please come to the restaurant to collect your order. Thank you for choosing ").append(restaurantName).append("!</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>").append(restaurantName).append(" - ").append(restaurantPhone).append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML email for low stock alert
     */
    private String buildLowStockAlertEmail(List<InventoryItem> lowStockItems) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#f44336;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".alert-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #f44336;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>Low Stock Alert</h1></div>");
        html.append("<div class='content'><h2>Inventory items running low</h2>");
        html.append("<p>The following items are running low on stock and need to be restocked:</p>");
        
        html.append("<div class='alert-details'>");
        html.append("<h3>Low Stock Items</h3>");
        html.append("<ul>");
        for (InventoryItem item : lowStockItems) {
            html.append("<li><strong>").append(item.getItemName()).append("</strong> - Current: ").append(item.getCurrentStock()).append(" ").append(item.getUnit()).append(" (Min: ").append(item.getMinimumStock()).append(")</li>");
        }
        html.append("</ul>");
        html.append("</div>");
        
        html.append("<p>Please arrange for restocking of these items as soon as possible.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>").append(restaurantName).append(" - Inventory Management</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }

    /**
     * Builds HTML email for task delay notification
     */
    private String buildTaskDelayEmail(KitchenTask task) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;}");
        html.append(".header{background:#ff9800;color:white;padding:20px;text-align:center;border-radius:5px 5px 0 0;}");
        html.append(".content{background:#f9f9f9;padding:20px;border-radius:0 0 5px 5px;}");
        html.append(".task-details{background:white;padding:15px;margin:15px 0;border-radius:5px;border-left:4px solid #ff9800;}");
        html.append(".footer{text-align:center;margin-top:20px;color:#666;font-size:12px;}</style></head>");
        html.append("<body><div class='header'><h1>Task Delay Alert</h1></div>");
        html.append("<div class='content'><h2>Kitchen task is taking longer than expected</h2>");
        html.append("<p>A kitchen task has been running for longer than the estimated duration.</p>");
        
        html.append("<div class='task-details'>");
        html.append("<h3>Task Details</h3>");
        html.append("<p><strong>Task ID:</strong> ").append(task.getId()).append("</p>");
        html.append("<p><strong>Item:</strong> ").append(task.getItemName()).append("</p>");
        html.append("<p><strong>Quantity:</strong> ").append(task.getQuantity()).append("</p>");
        html.append("<p><strong>Assigned to:</strong> ").append(task.getStaff().getName()).append("</p>");
        html.append("<p><strong>Estimated Duration:</strong> ").append(task.getEstimatedDurationMinutes()).append(" minutes</p>");
        html.append("<p><strong>Started at:</strong> ").append(task.getStartedAt()).append("</p>");
        html.append("</div>");
        
        html.append("<p>Please check on this task and provide assistance if needed.</p>");
        html.append("</div><div class='footer'>");
        html.append("<p>").append(restaurantName).append(" - Kitchen Management</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }
}