package com.restaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "satisfaction_reports")
public class SatisfactionReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;
    
    @Column(name = "report_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;
    
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;
    
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;
    
    @Column(name = "total_feedback_count", nullable = false)
    private Integer totalFeedbackCount = 0;
    
    @Column(name = "average_rating", columnDefinition = "DECIMAL(3,2)")
    private Double averageRating;
    
    @Column(name = "high_rating_count", nullable = false)
    private Integer highRatingCount = 0; // 4-5 stars
    
    @Column(name = "medium_rating_count", nullable = false)
    private Integer mediumRatingCount = 0; // 3 stars
    
    @Column(name = "low_rating_count", nullable = false)
    private Integer lowRatingCount = 0; // 1-2 stars
    
    @Column(name = "resolved_issues_count", nullable = false)
    private Integer resolvedIssuesCount = 0;
    
    @Column(name = "escalated_issues_count", nullable = false)
    private Integer escalatedIssuesCount = 0;
    
    @Column(name = "critical_issues_count", nullable = false)
    private Integer criticalIssuesCount = 0;
    
    @Column(name = "promotional_offers_sent", nullable = false)
    private Integer promotionalOffersSent = 0;
    
    @Column(name = "satisfaction_score", columnDefinition = "DECIMAL(3,2)")
    private Double satisfactionScore;
    
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "critical_issues_summary", columnDefinition = "TEXT")
    private String criticalIssuesSummary;
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.GENERATED;
    
    @Column(name = "generated_by")
    private String generatedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public SatisfactionReport() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public SatisfactionReport(LocalDate reportDate, ReportType reportType, 
                             LocalDate periodStart, LocalDate periodEnd) {
        this();
        this.reportDate = reportDate;
        this.reportType = reportType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    
    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    
    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    
    public Integer getTotalFeedbackCount() { return totalFeedbackCount; }
    public void setTotalFeedbackCount(Integer totalFeedbackCount) { this.totalFeedbackCount = totalFeedbackCount; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public Integer getHighRatingCount() { return highRatingCount; }
    public void setHighRatingCount(Integer highRatingCount) { this.highRatingCount = highRatingCount; }
    
    public Integer getMediumRatingCount() { return mediumRatingCount; }
    public void setMediumRatingCount(Integer mediumRatingCount) { this.mediumRatingCount = mediumRatingCount; }
    
    public Integer getLowRatingCount() { return lowRatingCount; }
    public void setLowRatingCount(Integer lowRatingCount) { this.lowRatingCount = lowRatingCount; }
    
    public Integer getResolvedIssuesCount() { return resolvedIssuesCount; }
    public void setResolvedIssuesCount(Integer resolvedIssuesCount) { this.resolvedIssuesCount = resolvedIssuesCount; }
    
    public Integer getEscalatedIssuesCount() { return escalatedIssuesCount; }
    public void setEscalatedIssuesCount(Integer escalatedIssuesCount) { this.escalatedIssuesCount = escalatedIssuesCount; }
    
    public Integer getCriticalIssuesCount() { return criticalIssuesCount; }
    public void setCriticalIssuesCount(Integer criticalIssuesCount) { this.criticalIssuesCount = criticalIssuesCount; }
    
    public Integer getPromotionalOffersSent() { return promotionalOffersSent; }
    public void setPromotionalOffersSent(Integer promotionalOffersSent) { this.promotionalOffersSent = promotionalOffersSent; }
    
    public Double getSatisfactionScore() { return satisfactionScore; }
    public void setSatisfactionScore(Double satisfactionScore) { this.satisfactionScore = satisfactionScore; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getCriticalIssuesSummary() { return criticalIssuesSummary; }
    public void setCriticalIssuesSummary(String criticalIssuesSummary) { this.criticalIssuesSummary = criticalIssuesSummary; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum ReportType {
        DAILY("Daily Report"),
        WEEKLY("Weekly Report"),
        MONTHLY("Monthly Report"),
        QUARTERLY("Quarterly Report"),
        YEARLY("Yearly Report"),
        CUSTOM("Custom Period Report");
        
        private final String displayName;
        
        ReportType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ReportStatus {
        GENERATED("Generated"),
        SENT("Sent to Management"),
        FAILED("Generation Failed"),
        RETRYING("Retrying");
        
        private final String displayName;
        
        ReportStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
