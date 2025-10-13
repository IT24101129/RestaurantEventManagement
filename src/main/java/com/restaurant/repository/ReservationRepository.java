package com.restaurant.repository;

import com.restaurant.model.Reservation;
import com.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByUserOrderByCreatedAtDesc(User user);
    List<Reservation> findByTableIdAndReservationDateTimeBetweenAndStatusIn(
            Long tableId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            List<Reservation.ReservationStatus> statuses
    );
    
    List<Reservation> findByReservationDateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
