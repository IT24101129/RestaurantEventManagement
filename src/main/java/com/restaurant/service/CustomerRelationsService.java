package com.restaurant.service;

import com.restaurant.model.*;
import com.restaurant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerRelationsService {
    
    private final CustomerFeedbackRepository feedbackRepository;
    private final FeedbackResponseRepository responseRepository;
    private final SatisfactionReportRepository reportRepository;
    private final NotificationService notificationService;
    
    @Autowired
    public CustomerRelationsService(CustomerFeedbackRepository feedbackRepository,
                                   FeedbackResponseRepository responseRepository,
                                   SatisfactionReportRepository reportRepository,
                                   NotificationService notificationService) {
        this.feedbackRepository = feedbackRepository;
        this.responseRepository = responseRepository;
        this.reportRepository = reportRepository;
        this.notificationService = notificationService;
    }
    
    // Feedback Management
    public List<CustomerFeedback> getPendingFeedback() {
        return feedbackRepository.findByStatus(CustomerFeedback.FeedbackStatus.PENDING);
    }
    
    public List<CustomerFeedback> getFeedbackByPriority(CustomerFeedback.Priority priority) {
        return feedbackRepository.findByPriority(priority);
    }
    
    public List<CustomerFeedback> getLowRatingFeedback() {
        return feedbackRepository.findLowRatingPendingFeedback(3); // 3 stars and below
    }
    
    public List<CustomerFeedback> getEscalatedFeedback() {
        return feedbackRepository.findEscalatedUnresolvedFeedback();
    }
    
    public List<CustomerFeedback> getAnonymousFeedback() {
        return feedbackRepository.findAnonymousFeedback();
    }
    
    public List<CustomerFeedback> getFeedbackRequiringFollowUp() {
        return feedbackRepository.findFeedbackRequiringFollowUp(LocalDateTime.now());
    }
    
    public Map<String, Object> getFeedbackDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Get pending feedback
        List<CustomerFeedback> pendingFeedback = getPendingFeedback();
        dashboard.put("pendingFeedback", pendingFeedback);
        
        // Get feedback by priority
        Map<CustomerFeedback.Priority, List<CustomerFeedback>> feedbackByPriority = new HashMap<>();
        for (CustomerFeedback.Priority priority : CustomerFeedback.Priority.values()) {
            feedbackByPriority.put(priority, feedbackRepository.findByPriority(priority));
        }
        dashboard.put("feedbackByPriority", feedbackByPriority);
        
        // Get low rating feedback
        List<CustomerFeedback> lowRatingFeedback = getLowRatingFeedback();
        dashboard.put("lowRatingFeedback", lowRatingFeedback);
        
        // Get escalated feedback
        List<CustomerFeedback> escalatedFeedback = getEscalatedFeedback();
        dashboard.put("escalatedFeedback", escalatedFeedback);
        
        // Get anonymous feedback
        List<CustomerFeedback> anonymousFeedback = getAnonymousFeedback();
        dashboard.put("anonymousFeedback", anonymousFeedback);
        
        // Get statistics
        Map<String, Object> statistics = getFeedbackStatistics();
        dashboard.put("statistics", statistics);
        
        return dashboard;
    }
    
    // Response Management
    public FeedbackResponse createResponse(Long feedbackId, String responderName, 
                                          FeedbackResponse.ResponseType responseType, 
                                          String responseText, Boolean isInternal, 
                                          String escalationNotes, Boolean followUpRequired, 
                                          LocalDateTime followUpDate) {
        
        CustomerFeedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        FeedbackResponse response = new FeedbackResponse(feedback, responderName, responseType, responseText);
        response.setIsInternal(isInternal);
        response.setIsEscalated(escalationNotes != null && !escalationNotes.trim().isEmpty());
        response.setEscalationNotes(escalationNotes);
        response.setFollowUpRequired(followUpRequired);
        response.setFollowUpDate(followUpDate);
        
        FeedbackResponse savedResponse = responseRepository.save(response);
        
        // Update feedback status
        if (isInternal) {
            feedback.setStatus(CustomerFeedback.FeedbackStatus.IN_PROGRESS);
        } else {
            feedback.setStatus(CustomerFeedback.FeedbackStatus.RESPONDED);
        }
        
        if (response.getIsEscalated()) {
            feedback.setIsEscalated(true);
            feedback.setEscalationReason(escalationNotes);
            feedback.setStatus(CustomerFeedback.FeedbackStatus.ESCALATED);
        }
        
        feedbackRepository.save(feedback);
        
        // Send notification if not internal
        if (!isInternal) {
            notificationService.sendFeedbackResponseNotification(feedback, savedResponse);
        }
        
        return savedResponse;
    }
    
    public List<FeedbackResponse> getResponsesForFeedback(Long feedbackId) {
        CustomerFeedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        return responseRepository.findByFeedback(feedback);
    }
    
    // Low Rating Handling
    public Map<String, Object> handleLowRatingFeedback(CustomerFeedback feedback) {
        Map<String, Object> result = new HashMap<>();
        
        if (feedback.getRating() <= 2) {
            // Suggest promotional offer
            String promotionalOffer = generatePromotionalOffer(feedback);
            result.put("suggestPromotionalOffer", true);
            result.put("promotionalOffer", promotionalOffer);
            result.put("feedbackId", feedback.getId());
        }
        
        return result;
    }
    
    public FeedbackResponse sendPromotionalOffer(Long feedbackId, String offerDetails, String responderName) {
        CustomerFeedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        FeedbackResponse response = new FeedbackResponse(feedback, responderName, 
            FeedbackResponse.ResponseType.PROMOTIONAL_OFFER, offerDetails);
        response.setPromotionalOfferSent(true);
        response.setOfferDetails(offerDetails);
        
        FeedbackResponse savedResponse = responseRepository.save(response);
        
        // Update feedback status
        feedback.setStatus(CustomerFeedback.FeedbackStatus.RESPONDED);
        feedbackRepository.save(feedback);
        
        // Send promotional offer notification
        notificationService.sendPromotionalOfferNotification(feedback, savedResponse);
        
        return savedResponse;
    }
    
    // Anonymous Feedback Handling
    public Map<String, Object> handleAnonymousFeedback(CustomerFeedback feedback) {
        Map<String, Object> result = new HashMap<>();
        
        if (feedback.getIsAnonymous()) {
            result.put("isAnonymous", true);
            result.put("limitedResponseOptions", true);
            result.put("suggestedResponse", "Thank you for your feedback. We appreciate your input and will use it to improve our services.");
        }
        
        return result;
    }
    
    // Escalation Management
    public Map<String, Object> handleEscalatedIssue(CustomerFeedback feedback) {
        Map<String, Object> result = new HashMap<>();
        
        if (feedback.getIsEscalated() && feedback.getStatus() == CustomerFeedback.FeedbackStatus.ESCALATED) {
            // Check if issue has been unresolved for too long
            LocalDateTime threshold = LocalDateTime.now().minusDays(3); // 3 days threshold
            if (feedback.getCreatedAt().isBefore(threshold)) {
                result.put("needsFollowUp", true);
                result.put("followUpDate", LocalDateTime.now().plusDays(1));
                result.put("feedbackId", feedback.getId());
                result.put("suggestion", "Schedule follow-up reminder for escalated issue");
            }
        }
        
        return result;
    }
    
    public void scheduleFollowUpReminder(Long feedbackId, LocalDateTime followUpDate, String responderName) {
        CustomerFeedback feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        FeedbackResponse response = new FeedbackResponse(feedback, responderName, 
            FeedbackResponse.ResponseType.FOLLOW_UP, "Follow-up reminder scheduled");
        response.setFollowUpRequired(true);
        response.setFollowUpDate(followUpDate);
        response.setIsInternal(true);
        
        responseRepository.save(response);
        
        // Schedule notification
        notificationService.scheduleFollowUpReminder(feedback, followUpDate);
    }
    
    // Satisfaction Report Generation
    public SatisfactionReport generateSatisfactionReport(SatisfactionReport.ReportType reportType, 
                                                        LocalDate periodStart, LocalDate periodEnd, 
                                                        String generatedBy) {
        
        try {
            LocalDateTime startDateTime = periodStart.atStartOfDay();
            LocalDateTime endDateTime = periodEnd.atTime(23, 59, 59);
            
            // Get feedback data for the period
            List<CustomerFeedback> feedbackList = feedbackRepository.findByCreatedAtBetween(startDateTime, endDateTime);
            
            // Calculate statistics
            SatisfactionReport report = new SatisfactionReport(LocalDate.now(), reportType, periodStart, periodEnd);
            report.setGeneratedBy(generatedBy);
            
            // Basic counts
            report.setTotalFeedbackCount(feedbackList.size());
            
            if (!feedbackList.isEmpty()) {
                // Calculate average rating
                Double averageRating = feedbackList.stream()
                    .mapToInt(CustomerFeedback::getRating)
                    .average()
                    .orElse(0.0);
                report.setAverageRating(averageRating);
                
                // Rating distribution
                long highRatingCount = feedbackList.stream()
                    .filter(f -> f.getRating() >= 4)
                    .count();
                long mediumRatingCount = feedbackList.stream()
                    .filter(f -> f.getRating() == 3)
                    .count();
                long lowRatingCount = feedbackList.stream()
                    .filter(f -> f.getRating() <= 2)
                    .count();
                
                report.setHighRatingCount((int) highRatingCount);
                report.setMediumRatingCount((int) mediumRatingCount);
                report.setLowRatingCount((int) lowRatingCount);
                
                // Resolved and escalated issues
                long resolvedCount = feedbackList.stream()
                    .filter(f -> f.getStatus() == CustomerFeedback.FeedbackStatus.RESOLVED)
                    .count();
                long escalatedCount = feedbackList.stream()
                    .filter(f -> f.getIsEscalated())
                    .count();
                long criticalCount = feedbackList.stream()
                    .filter(f -> f.getRating() <= 2)
                    .count();
                
                report.setResolvedIssuesCount((int) resolvedCount);
                report.setEscalatedIssuesCount((int) escalatedCount);
                report.setCriticalIssuesCount((int) criticalCount);
                
                // Promotional offers sent
                long promotionalOffers = responseRepository.countResponsesInPeriod(startDateTime, endDateTime);
                report.setPromotionalOffersSent((int) promotionalOffers);
                
                // Calculate satisfaction score (0-100)
                double satisfactionScore = (highRatingCount * 100.0 + mediumRatingCount * 60.0 + lowRatingCount * 20.0) / feedbackList.size();
                report.setSatisfactionScore(satisfactionScore);
                
                // Generate summaries
                report.setSummary(generateReportSummary(report));
                report.setCriticalIssuesSummary(generateCriticalIssuesSummary(feedbackList));
                report.setRecommendations(generateRecommendations(report));
            }
            
            SatisfactionReport savedReport = reportRepository.save(report);
            
            // Send notification to management
            notificationService.sendSatisfactionReportToManagement(savedReport);
            
            return savedReport;
            
        } catch (Exception e) {
            // Handle report generation failure
            return handleReportGenerationFailure(reportType, periodStart, periodEnd, generatedBy, e);
        }
    }
    
    private SatisfactionReport handleReportGenerationFailure(SatisfactionReport.ReportType reportType, 
                                                           LocalDate periodStart, LocalDate periodEnd, 
                                                           String generatedBy, Exception error) {
        
        // Create failed report
        SatisfactionReport failedReport = new SatisfactionReport(LocalDate.now(), reportType, periodStart, periodEnd);
        failedReport.setStatus(SatisfactionReport.ReportStatus.FAILED);
        failedReport.setGeneratedBy(generatedBy);
        failedReport.setSummary("Report generation failed: " + error.getMessage());
        
        SatisfactionReport savedReport = reportRepository.save(failedReport);
        
        // Alert technical support
        notificationService.sendTechnicalSupportAlert("Satisfaction Report Generation Failed", 
            "Failed to generate " + reportType.getDisplayName() + " for period " + periodStart + " to " + periodEnd, 
            error);
        
        // Retry after delay
        scheduleReportRetry(savedReport);
        
        return savedReport;
    }
    
    private void scheduleReportRetry(SatisfactionReport report) {
        // In a real implementation, this would use a job scheduler
        // For now, we'll just update the status
        report.setStatus(SatisfactionReport.ReportStatus.RETRYING);
        reportRepository.save(report);
    }
    
    // Statistics and Analytics
    public Map<String, Object> getFeedbackStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusWeeks(1);
        LocalDateTime monthAgo = now.minusMonths(1);
        
        // Weekly statistics
        Long weeklyCount = feedbackRepository.countFeedbackInPeriod(weekAgo, now);
        Double weeklyAverage = feedbackRepository.getAverageRatingInPeriod(weekAgo, now);
        
        // Monthly statistics
        Long monthlyCount = feedbackRepository.countFeedbackInPeriod(monthAgo, now);
        Double monthlyAverage = feedbackRepository.getAverageRatingInPeriod(monthAgo, now);
        
        // Pending feedback count
        Long pendingCount = (long) getPendingFeedback().size();
        
        // Escalated feedback count
        Long escalatedCount = (long) getEscalatedFeedback().size();
        
        stats.put("weeklyFeedbackCount", weeklyCount);
        stats.put("weeklyAverageRating", weeklyAverage);
        stats.put("monthlyFeedbackCount", monthlyCount);
        stats.put("monthlyAverageRating", monthlyAverage);
        stats.put("pendingFeedbackCount", pendingCount);
        stats.put("escalatedFeedbackCount", escalatedCount);
        
        return stats;
    }
    
    // Helper Methods
    private String generatePromotionalOffer(CustomerFeedback feedback) {
        StringBuilder offer = new StringBuilder();
        offer.append("Thank you for your feedback. We apologize for any inconvenience. ");
        
        if (feedback.getRating() == 1) {
            offer.append("As a gesture of goodwill, we'd like to offer you a 50% discount on your next visit. ");
        } else if (feedback.getRating() == 2) {
            offer.append("We'd like to offer you a 25% discount on your next visit. ");
        } else {
            offer.append("We'd like to offer you a complimentary dessert on your next visit. ");
        }
        
        offer.append("Please present this message when you visit us again.");
        
        return offer.toString();
    }
    
    private String generateReportSummary(SatisfactionReport report) {
        StringBuilder summary = new StringBuilder();
        summary.append("Satisfaction Report for ").append(report.getPeriodStart()).append(" to ").append(report.getPeriodEnd()).append("\n\n");
        summary.append("Total Feedback: ").append(report.getTotalFeedbackCount()).append("\n");
        summary.append("Average Rating: ").append(String.format("%.2f", report.getAverageRating())).append("\n");
        summary.append("Satisfaction Score: ").append(String.format("%.1f", report.getSatisfactionScore())).append("%\n");
        summary.append("High Ratings (4-5): ").append(report.getHighRatingCount()).append("\n");
        summary.append("Medium Ratings (3): ").append(report.getMediumRatingCount()).append("\n");
        summary.append("Low Ratings (1-2): ").append(report.getLowRatingCount()).append("\n");
        summary.append("Resolved Issues: ").append(report.getResolvedIssuesCount()).append("\n");
        summary.append("Escalated Issues: ").append(report.getEscalatedIssuesCount()).append("\n");
        summary.append("Critical Issues: ").append(report.getCriticalIssuesCount()).append("\n");
        
        return summary.toString();
    }
    
    private String generateCriticalIssuesSummary(List<CustomerFeedback> feedbackList) {
        List<CustomerFeedback> criticalIssues = feedbackList.stream()
            .filter(f -> f.getRating() <= 2)
            .collect(Collectors.toList());
        
        if (criticalIssues.isEmpty()) {
            return "No critical issues identified in this period.";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Critical Issues Summary (").append(criticalIssues.size()).append(" issues):\n\n");
        
        Map<CustomerFeedback.FeedbackType, Long> issueTypes = criticalIssues.stream()
            .collect(Collectors.groupingBy(CustomerFeedback::getFeedbackType, Collectors.counting()));
        
        for (Map.Entry<CustomerFeedback.FeedbackType, Long> entry : issueTypes.entrySet()) {
            summary.append("- ").append(entry.getKey().getDisplayName()).append(": ").append(entry.getValue()).append(" issues\n");
        }
        
        return summary.toString();
    }
    
    private String generateRecommendations(SatisfactionReport report) {
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("Recommendations:\n\n");
        
        if (report.getSatisfactionScore() < 70) {
            recommendations.append("1. Immediate action required - satisfaction score below 70%\n");
            recommendations.append("2. Review and address low-rating feedback promptly\n");
            recommendations.append("3. Consider implementing customer service training\n");
        }
        
        if (report.getCriticalIssuesCount() > 0) {
            recommendations.append("4. Address ").append(report.getCriticalIssuesCount()).append(" critical issues identified\n");
        }
        
        if (report.getEscalatedIssuesCount() > 0) {
            recommendations.append("5. Follow up on ").append(report.getEscalatedIssuesCount()).append(" escalated issues\n");
        }
        
        if (report.getHighRatingCount() > report.getLowRatingCount()) {
            recommendations.append("6. Continue current practices - positive feedback outweighs negative\n");
        }
        
        return recommendations.toString();
    }
}
