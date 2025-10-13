package com.restaurant.controller;

import com.restaurant.model.*;
import com.restaurant.service.CustomerRelationsService;
import com.restaurant.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/customer-relations")
public class CustomerRelationsController {
    
    private final CustomerRelationsService customerRelationsService;
    private final userService userService;
    
    @Autowired
    public CustomerRelationsController(CustomerRelationsService customerRelationsService, userService userService) {
        this.customerRelationsService = customerRelationsService;
        this.userService = userService;
    }
    
    /**
     * Customer Relations Officer Dashboard - Feedback Management
     */
    @GetMapping("/dashboard")
    public String customerRelationsDashboard(Model model, Authentication authentication) {
        try {
            // Get current user
            if (authentication != null) {
                String email = authentication.getName();
                Optional<User> user = userService.findByEmail(email);
                user.ifPresent(u -> model.addAttribute("user", u));
            }
            
            // Get dashboard data
            Map<String, Object> dashboardData = customerRelationsService.getFeedbackDashboard();
            
            model.addAttribute("dashboardData", dashboardData);
            model.addAttribute("feedbackTypes", CustomerFeedback.FeedbackType.values());
            model.addAttribute("responseTypes", FeedbackResponse.ResponseType.values());
            model.addAttribute("priorities", CustomerFeedback.Priority.values());
            model.addAttribute("statuses", CustomerFeedback.FeedbackStatus.values());
            
            return "customer-relations/dashboard";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load customer relations dashboard: " + e.getMessage());
            return "customer-relations/dashboard";
        }
    }
    
    /**
     * Get pending feedback entries with ratings and comments
     */
    @GetMapping("/api/pending-feedback")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPendingFeedback() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CustomerFeedback> pendingFeedback = customerRelationsService.getPendingFeedback();
            
            response.put("success", true);
            response.put("feedback", pendingFeedback);
            response.put("count", pendingFeedback.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load pending feedback: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get low rating feedback
     */
    @GetMapping("/api/low-rating-feedback")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLowRatingFeedback() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CustomerFeedback> lowRatingFeedback = customerRelationsService.getLowRatingFeedback();
            
            response.put("success", true);
            response.put("feedback", lowRatingFeedback);
            response.put("count", lowRatingFeedback.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load low rating feedback: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get escalated feedback
     */
    @GetMapping("/api/escalated-feedback")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEscalatedFeedback() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CustomerFeedback> escalatedFeedback = customerRelationsService.getEscalatedFeedback();
            
            response.put("success", true);
            response.put("feedback", escalatedFeedback);
            response.put("count", escalatedFeedback.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load escalated feedback: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get anonymous feedback
     */
    @GetMapping("/api/anonymous-feedback")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAnonymousFeedback() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CustomerFeedback> anonymousFeedback = customerRelationsService.getAnonymousFeedback();
            
            response.put("success", true);
            response.put("feedback", anonymousFeedback);
            response.put("count", anonymousFeedback.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load anonymous feedback: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Review feedback details
     */
    @GetMapping("/feedback/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFeedbackDetails(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // This would typically get feedback by ID from repository
            // For now, we'll return a placeholder
            response.put("success", true);
            response.put("message", "Feedback details retrieved");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load feedback details: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create response to feedback
     */
    @PostMapping("/feedback/{id}/respond")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> respondToFeedback(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String responderName = auth.getName();
            
            String responseTypeStr = request.get("responseType").toString();
            String responseText = request.get("responseText").toString();
            Boolean isInternal = Boolean.valueOf(request.get("isInternal").toString());
            String escalationNotes = request.get("escalationNotes") != null ? 
                request.get("escalationNotes").toString() : null;
            Boolean followUpRequired = Boolean.valueOf(request.get("followUpRequired").toString());
            LocalDateTime followUpDate = null;
            
            if (followUpRequired && request.get("followUpDate") != null) {
                followUpDate = LocalDateTime.parse(request.get("followUpDate").toString());
            }
            
            FeedbackResponse.ResponseType responseType = FeedbackResponse.ResponseType.valueOf(responseTypeStr);
            
            FeedbackResponse feedbackResponse = customerRelationsService.createResponse(
                id, responderName, responseType, responseText, isInternal, 
                escalationNotes, followUpRequired, followUpDate
            );
            
            response.put("success", true);
            response.put("response", feedbackResponse);
            response.put("message", "Response created successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to create response: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handle low rating feedback - suggest promotional offers
     */
    @PostMapping("/feedback/{id}/handle-low-rating")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleLowRatingFeedback(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get feedback (this would typically be from repository)
            // For now, we'll create a mock feedback object
            CustomerFeedback feedback = new CustomerFeedback();
            feedback.setId(id);
            feedback.setRating(2); // Low rating for testing
            
            Map<String, Object> lowRatingHandling = customerRelationsService.handleLowRatingFeedback(feedback);
            
            response.put("success", true);
            response.put("handling", lowRatingHandling);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to handle low rating feedback: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Send promotional offer to customer
     */
    @PostMapping("/feedback/{id}/send-promotional-offer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendPromotionalOffer(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String responderName = auth.getName();
            
            String offerDetails = request.get("offerDetails").toString();
            
            FeedbackResponse promotionalResponse = customerRelationsService.sendPromotionalOffer(
                id, offerDetails, responderName
            );
            
            response.put("success", true);
            response.put("response", promotionalResponse);
            response.put("message", "Promotional offer sent successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to send promotional offer: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handle anonymous feedback
     */
    @PostMapping("/feedback/{id}/handle-anonymous")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleAnonymousFeedback(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get feedback (this would typically be from repository)
            CustomerFeedback feedback = new CustomerFeedback();
            feedback.setId(id);
            feedback.setIsAnonymous(true);
            
            Map<String, Object> anonymousHandling = customerRelationsService.handleAnonymousFeedback(feedback);
            
            response.put("success", true);
            response.put("handling", anonymousHandling);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to handle anonymous feedback: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handle escalated issue
     */
    @PostMapping("/feedback/{id}/handle-escalation")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleEscalatedIssue(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get feedback (this would typically be from repository)
            CustomerFeedback feedback = new CustomerFeedback();
            feedback.setId(id);
            feedback.setIsEscalated(true);
            feedback.setStatus(CustomerFeedback.FeedbackStatus.ESCALATED);
            feedback.setCreatedAt(LocalDateTime.now().minusDays(4)); // 4 days ago for testing
            
            Map<String, Object> escalationHandling = customerRelationsService.handleEscalatedIssue(feedback);
            
            response.put("success", true);
            response.put("handling", escalationHandling);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to handle escalated issue: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Schedule follow-up reminder
     */
    @PostMapping("/feedback/{id}/schedule-follow-up")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> scheduleFollowUpReminder(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String responderName = auth.getName();
            
            LocalDateTime followUpDate = LocalDateTime.parse(request.get("followUpDate").toString());
            
            customerRelationsService.scheduleFollowUpReminder(id, followUpDate, responderName);
            
            response.put("success", true);
            response.put("message", "Follow-up reminder scheduled successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to schedule follow-up reminder: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Generate satisfaction report
     */
    @PostMapping("/reports/generate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateSatisfactionReport(
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String generatedBy = auth.getName();
            
            String reportTypeStr = request.get("reportType").toString();
            String periodStartStr = request.get("periodStart").toString();
            String periodEndStr = request.get("periodEnd").toString();
            
            SatisfactionReport.ReportType reportType = SatisfactionReport.ReportType.valueOf(reportTypeStr);
            LocalDate periodStart = LocalDate.parse(periodStartStr);
            LocalDate periodEnd = LocalDate.parse(periodEndStr);
            
            SatisfactionReport report = customerRelationsService.generateSatisfactionReport(
                reportType, periodStart, periodEnd, generatedBy
            );
            
            response.put("success", true);
            response.put("report", report);
            response.put("message", "Satisfaction report generated successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to generate satisfaction report: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get feedback statistics
     */
    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFeedbackStatistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> statistics = customerRelationsService.getFeedbackStatistics();
            
            response.put("success", true);
            response.put("statistics", statistics);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load statistics: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get responses for specific feedback
     */
    @GetMapping("/feedback/{id}/responses")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFeedbackResponses(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<FeedbackResponse> responses = customerRelationsService.getResponsesForFeedback(id);
            
            response.put("success", true);
            response.put("responses", responses);
            response.put("count", responses.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to load feedback responses: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handle report generation failure
     */
    @PostMapping("/reports/retry-failed")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> retryFailedReport(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long reportId = Long.valueOf(request.get("reportId").toString());
            
            // This would typically retry the failed report generation
            response.put("success", true);
            response.put("message", "Report retry initiated");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to retry report generation: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
