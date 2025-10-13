package com.restaurant.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservationRequest {

    // Table ID is optional - can be auto-assigned
    private Long tableId;

    @NotNull(message = "Reservation date is required")
    private LocalDate reservationDate;

    @NotNull(message = "Reservation time is required")
    private String reservationTime;

    @Min(value = 1, message = "Number of guests must be at least 1")
    @Max(value = 20, message = "Maximum 20 guests allowed")
    @NotNull(message = "Number of guests is required")
    private Integer numberOfGuests;

    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;

    // Computed property for the combined date and time
    public LocalDateTime getReservationDateTime() {
        if (reservationDate != null && reservationTime != null) {
            LocalTime time = LocalTime.parse(reservationTime);
            return LocalDateTime.of(reservationDate, time);
        }
        return null;
    }

    // Constructors
    public ReservationRequest() {}

    public ReservationRequest(Long tableId, LocalDateTime reservationDateTime, Integer numberOfGuests) {
        this.tableId = tableId;
        this.reservationDate = reservationDateTime.toLocalDate();
        this.reservationTime = reservationDateTime.toLocalTime().toString();
        this.numberOfGuests = numberOfGuests;
    }

    // Getters and Setters
    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
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
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
}
