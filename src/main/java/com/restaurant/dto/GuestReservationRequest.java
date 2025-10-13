package com.restaurant.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GuestReservationRequest {

    // Guest contact information
    @NotBlank(message = "Guest name is required")
    private String guestName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String guestEmail;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String guestPhone;

    // Reservation details
    @NotNull(message = "Reservation date is required")
    private LocalDate reservationDate;

    @NotNull(message = "Reservation time is required")
    private String reservationTime;

    @Min(value = 1, message = "Number of guests must be at least 1")
    @Max(value = 50, message = "Maximum 50 guests allowed for group reservations")
    @NotNull(message = "Number of guests is required")
    private Integer numberOfGuests;

    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;

    // Group reservation details
    private boolean isGroupReservation = false;
    private boolean requiresMultipleTables = false;
    private String groupEventType;
    private String dietaryRestrictions;

    // Payment preferences
    private boolean requiresPayment = false;
    private String paymentMethod;

    // Computed property for the combined date and time
    public LocalDateTime getReservationDateTime() {
        if (reservationDate != null && reservationTime != null) {
            LocalTime time = LocalTime.parse(reservationTime);
            return LocalDateTime.of(reservationDate, time);
        }
        return null;
    }

    // Constructors
    public GuestReservationRequest() {}

    // Getters and Setters
    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        // Auto-detect if this is a group reservation
        this.isGroupReservation = numberOfGuests > 8;
        this.requiresMultipleTables = numberOfGuests > 10;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public boolean isGroupReservation() {
        return isGroupReservation;
    }

    public void setGroupReservation(boolean groupReservation) {
        isGroupReservation = groupReservation;
    }

    public boolean isRequiresMultipleTables() {
        return requiresMultipleTables;
    }

    public void setRequiresMultipleTables(boolean requiresMultipleTables) {
        this.requiresMultipleTables = requiresMultipleTables;
    }

    public String getGroupEventType() {
        return groupEventType;
    }

    public void setGroupEventType(String groupEventType) {
        this.groupEventType = groupEventType;
    }

    public String getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public void setDietaryRestrictions(String dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public boolean isRequiresPayment() {
        return requiresPayment;
    }

    public void setRequiresPayment(boolean requiresPayment) {
        this.requiresPayment = requiresPayment;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
