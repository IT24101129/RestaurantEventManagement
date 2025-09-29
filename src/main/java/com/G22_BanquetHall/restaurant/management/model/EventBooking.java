package com.G22_BanquetHall.restaurant.management.model;

import java.time.LocalDateTime;

public class EventBooking {
    private Long eventId;
    private String eventName; // Added for form
    private String clientName; // Added for form
    private String clientEmail; // Added for form
    private String type;
    private int numGuests;
    private double totalCost;
    private String requirements;
    private String specialNotes;
    private String status;
    private Long hallId;
    private Long coordinatorUserId;
    private Long scheduleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String dateTimeStr; // Temporary field for form input

    // Getters and Setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getNumGuests() { return numGuests; }
    public void setNumGuests(int numGuests) { this.numGuests = numGuests; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public String getSpecialNotes() { return specialNotes; }
    public void setSpecialNotes(String specialNotes) { this.specialNotes = specialNotes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getHallId() { return hallId; }
    public void setHallId(Long hallId) { this.hallId = hallId; }
    public Long getCoordinatorUserId() { return coordinatorUserId; }
    public void setCoordinatorUserId(Long coordinatorUserId) { this.coordinatorUserId = coordinatorUserId; }
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getDateTimeStr() { return dateTimeStr; }
    public void setDateTimeStr(String dateTimeStr) { this.dateTimeStr = dateTimeStr; }
}