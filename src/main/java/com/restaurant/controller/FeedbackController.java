package com.restaurant.controller;

import com.restaurant.dto.FeedbackRequest;
import com.restaurant.dto.FeedbackResponse;
import com.restaurant.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> submitFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        FeedbackResponse response = feedbackService.submitFeedback(feedbackRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FeedbackResponse>> getPendingFeedback() {
        return ResponseEntity.ok(feedbackService.getAllPendingFeedback());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    @GetMapping("/escalated")
    public ResponseEntity<List<FeedbackResponse>> getEscalatedFeedbacks() {
        return ResponseEntity.ok(feedbackService.getEscalatedFeedbacks());
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<FeedbackResponse> respondToFeedback(
            @PathVariable Long id,
            @RequestParam String response,
            @RequestParam(required = false, defaultValue = "false") boolean escalate,
            @RequestParam(required = false) String escalationNotes,
            @RequestParam(required = false) LocalDateTime followUpDate) {
        
        FeedbackResponse feedbackResponse = feedbackService.respondToFeedback(
                id, response, escalate, escalationNotes, followUpDate);
        
        return ResponseEntity.ok(feedbackResponse);
    }

    @PostMapping("/{id}/send-offer")
    public ResponseEntity<Void> sendPromotionalOffer(
            @PathVariable Long id,
            @RequestParam String offerDetails) {
        
        feedbackService.sendPromotionalOffer(id, offerDetails);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<String> getFeedbackSummary() {
        feedbackService.notifyManagementOfCriticalIssues();
        return ResponseEntity.ok("Feedback summary has been generated and notifications sent.");
    }
}
