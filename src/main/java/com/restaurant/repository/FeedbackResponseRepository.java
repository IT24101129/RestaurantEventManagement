package com.restaurant.repository;

import com.restaurant.model.FeedbackResponse;
import com.restaurant.model.CustomerFeedback;
import com.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackResponseRepository extends JpaRepository<FeedbackResponse, Long> {
    
    List<FeedbackResponse> findByFeedback(CustomerFeedback feedback);
    
    List<FeedbackResponse> findByRespondedBy(User respondedBy);
    
    List<FeedbackResponse> findByResponseType(FeedbackResponse.ResponseType responseType);
    
    List<FeedbackResponse> findByIsInternal(Boolean isInternal);
    
    List<FeedbackResponse> findByIsEscalated(Boolean isEscalated);
    
    List<FeedbackResponse> findByFollowUpRequired(Boolean followUpRequired);
    
    List<FeedbackResponse> findByPromotionalOfferSent(Boolean promotionalOfferSent);
    
    @Query("SELECT fr FROM FeedbackResponse fr WHERE fr.feedback = :feedback ORDER BY fr.createdAt ASC")
    List<FeedbackResponse> findByFeedbackOrderByCreatedAtAsc(@Param("feedback") CustomerFeedback feedback);
    
    @Query("SELECT fr FROM FeedbackResponse fr WHERE fr.followUpRequired = true AND fr.followUpDate <= :currentDate ORDER BY fr.followUpDate ASC")
    List<FeedbackResponse> findResponsesRequiringFollowUp(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT fr FROM FeedbackResponse fr WHERE fr.isEscalated = true AND fr.createdAt BETWEEN :startDate AND :endDate ORDER BY fr.createdAt DESC")
    List<FeedbackResponse> findEscalatedResponsesInPeriod(@Param("startDate") LocalDateTime startDate, 
                                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT fr FROM FeedbackResponse fr WHERE fr.promotionalOfferSent = true AND fr.createdAt BETWEEN :startDate AND :endDate ORDER BY fr.createdAt DESC")
    List<FeedbackResponse> findPromotionalOffersInPeriod(@Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(fr) FROM FeedbackResponse fr WHERE fr.createdAt BETWEEN :startDate AND :endDate")
    Long countResponsesInPeriod(@Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT fr.responseType, COUNT(fr) FROM FeedbackResponse fr WHERE fr.createdAt BETWEEN :startDate AND :endDate GROUP BY fr.responseType")
    List<Object[]> getResponseTypeDistributionInPeriod(@Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT fr FROM FeedbackResponse fr WHERE fr.feedback = :feedback AND fr.isInternal = false ORDER BY fr.createdAt DESC")
    List<FeedbackResponse> findPublicResponsesForFeedback(@Param("feedback") CustomerFeedback feedback);
    
    @Query("SELECT fr FROM FeedbackResponse fr WHERE fr.feedback = :feedback AND fr.isInternal = true ORDER BY fr.createdAt DESC")
    List<FeedbackResponse> findInternalResponsesForFeedback(@Param("feedback") CustomerFeedback feedback);
}
