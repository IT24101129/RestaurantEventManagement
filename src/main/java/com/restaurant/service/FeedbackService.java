package com.restaurant.service;

import com.restaurant.dto.FeedbackRequest;
import com.restaurant.dto.FeedbackResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackService {
    FeedbackResponse submitFeedback(FeedbackRequest feedbackRequest);
    List<FeedbackResponse> getAllPendingFeedback();
    FeedbackResponse respondToFeedback(Long feedbackId, String response, boolean escalate, String escalationNotes, LocalDateTime followUpDate);
    FeedbackResponse getFeedbackById(Long id);
    List<FeedbackResponse> getEscalatedFeedbacks();
    void sendPromotionalOffer(Long feedbackId, String offerDetails);
    void notifyManagementOfCriticalIssues();
    void processFollowUpReminders();
}
