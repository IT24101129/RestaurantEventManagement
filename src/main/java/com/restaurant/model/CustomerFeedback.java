package com.restaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_feedback")
public class CustomerFeedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "customer_email")
    private String customerEmail;
    
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;
    
    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5 scale
    
    @Column(name = "feedback_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackType feedbackType;
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "reservation_id")
    private Long reservationId;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackStatus status = FeedbackStatus.PENDING;
    
    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;
    
    @Column(name = "is_escalated", nullable = false)
    private Boolean isEscalated = false;
    
    @Column(name = "escalation_reason", columnDefinition = "TEXT")
    private String escalationReason;
    
    @Column(name = "follow_up_required", nullable = false)
    private Boolean followUpRequired = false;
    
    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<FeedbackResponse> responses;
    
    // Constructors
    public CustomerFeedback() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public CustomerFeedback(String customerName, String customerEmail, Integer rating, 
                           FeedbackType feedbackType, String comment) {
        this();
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.rating = rating;
        this.feedbackType = feedbackType;
        this.comment = comment;
        this.isAnonymous = false;
    }
    
    public CustomerFeedback(Integer rating, FeedbackType feedbackType, String comment) {
        this();
        this.rating = rating;
        this.feedbackType = feedbackType;
        this.comment = comment;
        this.isAnonymous = true;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { 
        this.rating = rating;
        // Auto-set priority based on rating
        if (rating <= 2) {
            this.priority = Priority.HIGH;
        } else if (rating == 3) {
            this.priority = Priority.MEDIUM;
        } else {
            this.priority = Priority.LOW;
        }
    }
    
    public FeedbackType getFeedbackType() { return feedbackType; }
    public void setFeedbackType(FeedbackType feedbackType) { this.feedbackType = feedbackType; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    
    public FeedbackStatus getStatus() { return status; }
    public void setStatus(FeedbackStatus status) { this.status = status; }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public Boolean getIsEscalated() { return isEscalated; }
    public void setIsEscalated(Boolean isEscalated) { this.isEscalated = isEscalated; }
    
    public String getEscalationReason() { return escalationReason; }
    public void setEscalationReason(String escalationReason) { this.escalationReason = escalationReason; }
    
    public Boolean getFollowUpRequired() { return followUpRequired; }
    public void setFollowUpRequired(Boolean followUpRequired) { this.followUpRequired = followUpRequired; }
    
    public LocalDateTime getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDateTime followUpDate) { this.followUpDate = followUpDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public java.util.List<FeedbackResponse> getResponses() { return responses; }
    public void setResponses(java.util.List<FeedbackResponse> responses) { this.responses = responses; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum FeedbackType {
        GENERAL("General Feedback"),
        FOOD_QUALITY("Food Quality"),
        SERVICE("Service"),
        AMBIANCE("Ambiance"),
        CLEANLINESS("Cleanliness"),
        VALUE("Value for Money"),
        STAFF("Staff Behavior"),
        COMPLAINT("Complaint"),
        SUGGESTION("Suggestion"),
        COMPLIMENT("Compliment");
        
        private final String displayName;
        
        FeedbackType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum FeedbackStatus {
        PENDING("Pending Review"),
        IN_PROGRESS("In Progress"),
        RESPONDED("Responded"),
        ESCALATED("Escalated"),
        RESOLVED("Resolved"),
        CLOSED("Closed");
        
        private final String displayName;
        
        FeedbackStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum Priority {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        URGENT("Urgent");
        
        private final String displayName;
        
        Priority(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
