package com.restaurant.repository;

import com.restaurant.model.CustomerFeedback;
import com.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {
    
    List<CustomerFeedback> findByCustomer(User customer);
    
    List<CustomerFeedback> findByStatus(CustomerFeedback.FeedbackStatus status);
    
    List<CustomerFeedback> findByPriority(CustomerFeedback.Priority priority);
    
    List<CustomerFeedback> findByIsEscalated(Boolean isEscalated);
    
    List<CustomerFeedback> findByIsAnonymous(Boolean isAnonymous);
    
    List<CustomerFeedback> findByRatingLessThanEqual(Integer maxRating);
    
    List<CustomerFeedback> findByRatingGreaterThanEqual(Integer minRating);
    
    List<CustomerFeedback> findByFeedbackType(CustomerFeedback.FeedbackType feedbackType);
    
    @Query("SELECT cf FROM CustomerFeedback cf WHERE cf.createdAt BETWEEN :startDate AND :endDate ORDER BY cf.createdAt DESC")
    List<CustomerFeedback> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cf FROM CustomerFeedback cf WHERE cf.status = :status AND cf.priority = :priority ORDER BY cf.createdAt ASC")
    List<CustomerFeedback> findByStatusAndPriorityOrderByCreatedAtAsc(@Param("status") CustomerFeedback.FeedbackStatus status,
                                                                     @Param("priority") CustomerFeedback.Priority priority);
    
    @Query("SELECT cf FROM CustomerFeedback cf WHERE cf.rating <= :maxRating AND cf.status = 'PENDING' ORDER BY cf.createdAt ASC")
    List<CustomerFeedback> findLowRatingPendingFeedback(@Param("maxRating") Integer maxRating);
    
    @Query("SELECT cf FROM CustomerFeedback cf WHERE cf.isEscalated = true AND cf.status IN ('ESCALATED', 'IN_PROGRESS') ORDER BY cf.createdAt ASC")
    List<CustomerFeedback> findEscalatedUnresolvedFeedback();
    
    @Query("SELECT cf FROM CustomerFeedback cf WHERE cf.isAnonymous = true ORDER BY cf.createdAt DESC")
    List<CustomerFeedback> findAnonymousFeedback();
    
    @Query("SELECT cf FROM CustomerFeedback cf WHERE cf.followUpRequired = true AND cf.followUpDate <= :currentDate ORDER BY cf.followUpDate ASC")
    List<CustomerFeedback> findFeedbackRequiringFollowUp(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT COUNT(cf) FROM CustomerFeedback cf WHERE cf.createdAt BETWEEN :startDate AND :endDate")
    Long countFeedbackInPeriod(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(cf.rating) FROM CustomerFeedback cf WHERE cf.createdAt BETWEEN :startDate AND :endDate")
    Double getAverageRatingInPeriod(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cf.rating, COUNT(cf) FROM CustomerFeedback cf WHERE cf.createdAt BETWEEN :startDate AND :endDate GROUP BY cf.rating ORDER BY cf.rating")
    List<Object[]> getRatingDistributionInPeriod(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cf.feedbackType, COUNT(cf) FROM CustomerFeedback cf WHERE cf.createdAt BETWEEN :startDate AND :endDate GROUP BY cf.feedbackType")
    List<Object[]> getFeedbackTypeDistributionInPeriod(@Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cf FROM CustomerFeedback cf WHERE cf.status = 'PENDING' AND cf.priority = 'URGENT' ORDER BY cf.createdAt ASC")
    List<CustomerFeedback> findUrgentPendingFeedback();
    
    @Query("SELECT cf FROM CustomerFeedback cf WHERE cf.rating <= 2 AND cf.status IN ('PENDING', 'IN_PROGRESS') ORDER BY cf.createdAt ASC")
    List<CustomerFeedback> findCriticalLowRatingFeedback();
    
    @Query("SELECT cf FROM CustomerFeedback cf WHERE cf.isEscalated = true AND cf.status = 'ESCALATED' AND cf.createdAt <= :thresholdDate ORDER BY cf.createdAt ASC")
    List<CustomerFeedback> findLongUnresolvedEscalatedFeedback(@Param("thresholdDate") LocalDateTime thresholdDate);
}
