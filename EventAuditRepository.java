package com.resturant.restaurantapp.repository;

import com.resturant.restaurantapp.model.EventAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventAuditRepository extends JpaRepository<EventAudit, Long> {

    List<EventAudit> findByEventIdOrderByChangedAtDesc(String eventId);

    List<EventAudit> findByEventPrimaryIdOrderByChangedAtDesc(Long eventPrimaryId);

    List<EventAudit> findByChangeTypeOrderByChangedAtDesc(EventAudit.ChangeType changeType);

    List<EventAudit> findByChangedByOrderByChangedAtDesc(String changedBy);

    @Query("SELECT ea FROM EventAudit ea WHERE ea.changedAt >= :startDate AND ea.changedAt <= :endDate ORDER BY ea.changedAt DESC")
    List<EventAudit> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ea FROM EventAudit ea WHERE ea.eventId = :eventId AND ea.fieldName = :fieldName ORDER BY ea.changedAt DESC")
    List<EventAudit> findByEventIdAndFieldName(@Param("eventId") String eventId, @Param("fieldName") String fieldName);

    @Query("SELECT ea FROM EventAudit ea WHERE ea.eventId = :eventId AND ea.changeType = :changeType ORDER BY ea.changedAt DESC")
    List<EventAudit> findByEventIdAndChangeType(@Param("eventId") String eventId, @Param("changeType") EventAudit.ChangeType changeType);

    @Query("SELECT COUNT(ea) FROM EventAudit ea WHERE ea.eventId = :eventId")
    Long countByEventId(@Param("eventId") String eventId);

    @Query("SELECT ea FROM EventAudit ea ORDER BY ea.changedAt DESC")
    List<EventAudit> findAllOrderByChangedAtDesc();
}

