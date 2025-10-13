package com.restaurant.service;

import com.restaurant.dto.ReservationRequest;
import com.restaurant.model.Reservation;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.User;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantTableRepository tableRepository;

    public List<Reservation> findByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation createReservation(User user, ReservationRequest request) {
        if (user == null) {
            throw new IllegalArgumentException("User must be authenticated to create a reservation");
        }
        
        System.out.println("Processing reservation request...");
        System.out.println("User: " + user.getName() + " (" + user.getEmail() + ")");
        System.out.println("Requested table ID: " + request.getTableId());
        System.out.println("Number of guests: " + request.getNumberOfGuests());
        System.out.println("Reservation date/time: " + request.getReservationDateTime());
        
        RestaurantTable table;
        if (request.getTableId() != null) {
            System.out.println("Using specified table ID: " + request.getTableId());
            table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new IllegalArgumentException("Table not found"));
        } else {
            System.out.println("Auto-assigning table for " + request.getNumberOfGuests() + " guests");
            // Auto-assign a table based on number of guests
            table = findSuitableTable(request.getNumberOfGuests());
            if (table == null) {
                throw new IllegalArgumentException("No suitable table available for " + request.getNumberOfGuests() + " guests");
            }
        }
        
        System.out.println("Selected table: " + table.getName() + " (Capacity: " + table.getCapacity() + ")");
        
        Reservation reservation = new Reservation(user, table, request.getReservationDateTime(), request.getNumberOfGuests());
        reservation.setSpecialRequests(request.getSpecialRequests());
        
        System.out.println("Saving reservation...");
        Reservation savedReservation = reservationRepository.save(reservation);
        System.out.println("Reservation saved with ID: " + savedReservation.getId());
        
        return savedReservation;
    }
    
    private RestaurantTable findSuitableTable(Integer numberOfGuests) {
        List<RestaurantTable> availableTables = tableRepository.findByAvailableTrue();
        return availableTables.stream()
                .filter(table -> table.getCapacity() >= numberOfGuests)
                .findFirst()
                .orElse(null);
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public void confirmReservation(Long id) {
        Reservation res = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        res.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservationRepository.save(res);
    }

    public void cancelReservation(Long id) {
        Reservation res = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        res.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(res);
    }

    public void completeReservation(Long id) {
        Reservation res = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        res.setStatus(Reservation.ReservationStatus.COMPLETED);
        reservationRepository.save(res);
    }

    public List<String> getAvailableTimeSlots(LocalDateTime dateTime, int guests) {
        return new ArrayList<>();
    }
}