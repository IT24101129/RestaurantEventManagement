package com.restaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_responses")
public class FeedbackResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    private CustomerFeedback feedback;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responded_by")
    private User respondedBy;
    
    @Column(name = "responder_name", nullable = false)
    private String responderName;
    
    @Column(name = "response_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResponseType responseType;
    
    @Column(name = "response_text", columnDefinition = "TEXT", nullable = false)
    private String responseText;
    
    @Column(name = "is_internal", nullable = false)
    private Boolean isInternal = false;
    
    @Column(name = "is_escalated", nullable = false)
    private Boolean isEscalated = false;
    
    @Column(name = "escalation_notes", columnDefinition = "TEXT")
    private String escalationNotes;
    
    @Column(name = "follow_up_required", nullable = false)
    private Boolean followUpRequired = false;
    
    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;
    
    @Column(name = "promotional_offer_sent", nullable = false)
    private Boolean promotionalOfferSent = false;
    
    @Column(name = "offer_details", columnDefinition = "TEXT")
    private String offerDetails;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public FeedbackResponse() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public FeedbackResponse(CustomerFeedback feedback, String responderName, 
                           ResponseType responseType, String responseText) {
        this();
        this.feedback = feedback;
        this.responderName = responderName;
        this.responseType = responseType;
        this.responseText = responseText;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public CustomerFeedback getFeedback() { return feedback; }
    public void setFeedback(CustomerFeedback feedback) { this.feedback = feedback; }
    
    public User getRespondedBy() { return respondedBy; }
    public void setRespondedBy(User respondedBy) { this.respondedBy = respondedBy; }
    
    public String getResponderName() { return responderName; }
    public void setResponderName(String responderName) { this.responderName = responderName; }
    
    public ResponseType getResponseType() { return responseType; }
    public void setResponseType(ResponseType responseType) { this.responseType = responseType; }
    
    public String getResponseText() { return responseText; }
    public void setResponseText(String responseText) { this.responseText = responseText; }
    
    public Boolean getIsInternal() { return isInternal; }
    public void setIsInternal(Boolean isInternal) { this.isInternal = isInternal; }
    
    public Boolean getIsEscalated() { return isEscalated; }
    public void setIsEscalated(Boolean isEscalated) { this.isEscalated = isEscalated; }
    
    public String getEscalationNotes() { return escalationNotes; }
    public void setEscalationNotes(String escalationNotes) { this.escalationNotes = escalationNotes; }
    
    public Boolean getFollowUpRequired() { return followUpRequired; }
    public void setFollowUpRequired(Boolean followUpRequired) { this.followUpRequired = followUpRequired; }
    
    public LocalDateTime getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDateTime followUpDate) { this.followUpDate = followUpDate; }
    
    public Boolean getPromotionalOfferSent() { return promotionalOfferSent; }
    public void setPromotionalOfferSent(Boolean promotionalOfferSent) { this.promotionalOfferSent = promotionalOfferSent; }
    
    public String getOfferDetails() { return offerDetails; }
    public void setOfferDetails(String offerDetails) { this.offerDetails = offerDetails; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Response Type Enum
    public enum ResponseType {
        ACKNOWLEDGMENT("Acknowledgment"),
        APOLOGY("Apology"),
        EXPLANATION("Explanation"),
        RESOLUTION("Resolution"),
        ESCALATION("Escalation"),
        FOLLOW_UP("Follow-up"),
        PROMOTIONAL_OFFER("Promotional Offer"),
        GENERAL_RESPONSE("General Response");
        
        private final String displayName;
        
        ResponseType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
