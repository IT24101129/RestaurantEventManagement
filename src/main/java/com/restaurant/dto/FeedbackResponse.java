package com.restaurant.dto;

import com.restaurant.model.Feedback;

import java.time.LocalDateTime;

public class FeedbackResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private String customerName;
    private Boolean isAnonymous;
    private LocalDateTime createdAt;
    private Boolean isResponded;
    private String response;
    private LocalDateTime responseDate;
    private Boolean isEscalated;
    private String escalationNotes;
    private LocalDateTime followUpDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsResponded() { return isResponded; }
    public void setIsResponded(Boolean isResponded) { this.isResponded = isResponded; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public LocalDateTime getResponseDate() { return responseDate; }
    public void setResponseDate(LocalDateTime responseDate) { this.responseDate = responseDate; }

    public Boolean getIsEscalated() { return isEscalated; }
    public void setIsEscalated(Boolean isEscalated) { this.isEscalated = isEscalated; }

    public String getEscalationNotes() { return escalationNotes; }
    public void setEscalationNotes(String escalationNotes) { this.escalationNotes = escalationNotes; }

    public LocalDateTime getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDateTime followUpDate) { this.followUpDate = followUpDate; }

    public static FeedbackResponse fromEntity(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId());
        response.setRating(feedback.getRating());
        response.setComment(feedback.getComment());
        response.setCustomerName(feedback.getCustomerName());
        response.setIsAnonymous(feedback.getIsAnonymous());
        response.setCreatedAt(feedback.getCreatedAt());
        response.setIsResponded(feedback.getResponded());
        response.setResponse(feedback.getResponse());
        response.setResponseDate(feedback.getResponseDate());
        response.setIsEscalated(feedback.getEscalated());
        response.setEscalationNotes(feedback.getEscalationNotes());
        response.setFollowUpDate(feedback.getFollowUpDate());
        return response;
    }
}
