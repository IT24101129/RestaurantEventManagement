package com.restaurant.repository;

import com.restaurant.model.Order;
import com.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUser(User user);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByOrderType(Order.OrderType orderType);
    
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt ASC")
    List<Order> findByStatusOrderByCreatedAtAsc(@Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.status = :status")
    List<Order> findByUserAndStatus(@Param("user") User user, @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.createdAt ASC")
    List<Order> findByStatusInOrderByCreatedAtAsc(@Param("statuses") List<Order.OrderStatus> statuses);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.estimatedDeliveryTime <= :currentTime")
    List<Order> findOverdueOrders(@Param("status") Order.OrderStatus status, @Param("currentTime") LocalDateTime currentTime);
}
