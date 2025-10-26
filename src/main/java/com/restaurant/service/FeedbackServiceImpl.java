package com.restaurant.service;

import com.restaurant.dto.FeedbackRequest;
import com.restaurant.dto.FeedbackResponse;
import com.restaurant.model.Feedback;
import com.restaurant.repository.FeedbackRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    
    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public FeedbackResponse submitFeedback(FeedbackRequest feedbackRequest) {
        Feedback feedback = new Feedback();
        feedback.setRating(feedbackRequest.getRating());
        feedback.setComment(feedbackRequest.getComment());
        feedback.setCustomerName(feedbackRequest.getCustomerName());
        feedback.setCustomerEmail(feedbackRequest.getCustomerEmail());
        feedback.setIsAnonymous(feedbackRequest.getIsAnonymous());
        
        // If feedback is anonymous, clear personal information
        if (Boolean.TRUE.equals(feedback.getIsAnonymous())) {
            feedback.setCustomerName(null);
            feedback.setCustomerEmail(null);
        }
        
        // Check for low rating and set flag for follow-up
        if (feedback.getRating() != null && feedback.getRating() <= 2) {
            // This will be processed by the scheduled task
            feedback.setEscalated(true);
            feedback.setEscalationNotes("Low rating - requires attention");
        }
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return FeedbackResponse.fromEntity(savedFeedback);
    }

    @Override
    public List<FeedbackResponse> getAllPendingFeedback() {
        return feedbackRepository.findByIsRespondedFalseOrderByCreatedAtDesc().stream()
                .map(FeedbackResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackResponse respondToFeedback(Long feedbackId, String response, boolean escalate, 
                                           String escalationNotes, LocalDateTime followUpDate) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found with id: " + feedbackId));
        
        feedback.setResponded(true);
        feedback.setResponse(response);
        feedback.setResponseDate(LocalDateTime.now());
        feedback.setEscalated(escalate);
        
        if (escalate) {
            feedback.setEscalationNotes(escalationNotes);
            feedback.setFollowUpDate(followUpDate);
        }
        
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return FeedbackResponse.fromEntity(updatedFeedback);
    }

    @Override
    public FeedbackResponse getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .map(FeedbackResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found with id: " + id));
    }

    @Override
    public List<FeedbackResponse> getEscalatedFeedbacks() {
        return feedbackRepository.findByIsEscalatedTrueAndIsRespondedFalse().stream()
                .map(FeedbackResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void sendPromotionalOffer(Long feedbackId, String offerDetails) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found with id: " + feedbackId));
        
        // In a real implementation, this would send an email with the promotional offer
        System.out.println("Sending promotional offer to: " + feedback.getCustomerEmail());
        System.out.println("Offer details: " + offerDetails);
    }

    @Scheduled(cron = "0 0 9 * * ?") // Run every day at 9 AM
    @Override
    public void notifyManagementOfCriticalIssues() {
        List<Feedback> criticalFeedbacks = feedbackRepository.findByIsEscalatedTrueAndIsRespondedFalse();
        
        if (!criticalFeedbacks.isEmpty()) {
            // In a real implementation, this would send an email to management
            System.out.println("\n=== CRITICAL FEEDBACK SUMMARY ===");
            criticalFeedbacks.forEach(feedback -> {
                System.out.printf("Feedback ID: %d, Rating: %d, Issue: %s%n", 
                        feedback.getId(), 
                        feedback.getRating(), 
                        feedback.getEscalationNotes());
            });
            System.out.println("==============================\n");
        }
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    @Override
    public void processFollowUpReminders() {
        List<Feedback> followUps = feedbackRepository.findByFollowUpDateBeforeAndIsRespondedFalse(LocalDateTime.now());
        
        followUps.forEach(feedback -> {
            // In a real implementation, this would send a notification to the assigned staff
            System.out.printf("FOLLOW-UP REMINDER for Feedback ID: %d - %s%n", 
                    feedback.getId(), 
                    feedback.getEscalationNotes());
        });
    }

    @Scheduled(cron = "0 0 10 * * ?") // Run every day at 10 AM
    public void processLowRatings() {
        // Get all low ratings (1-2 stars) from the past week that haven't been addressed
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        
        List<Feedback> lowRatings = feedbackRepository.findByRatingLessThanEqualAndCreatedAtAfterAndIsRespondedFalse(
                2, oneWeekAgo);
        
        lowRatings.forEach(feedback -> {
            if (feedback.getCustomerEmail() != null && !feedback.getCustomerEmail().isEmpty()) {
                String offerCode = "APOLOGY" + feedback.getId();
                sendPromotionalOffer(feedback.getId(), 
                    "We're sorry to hear about your experience. Please accept this 20% off your next visit. Use code: " + offerCode);
            }
        });
    }
    
    // Helper method to find by rating and date range
    private List<Feedback> findByRatingLessThanEqualAndCreatedAtAfterAndIsRespondedFalse(
            int maxRating, LocalDateTime date) {
        return feedbackRepository.findAll().stream()
                .filter(f -> f.getRating() != null && 
                            f.getRating() <= maxRating && 
                            f.getCreatedAt().isAfter(date) &&
                            !f.getResponded())
                .collect(Collectors.toList());
    }
}
