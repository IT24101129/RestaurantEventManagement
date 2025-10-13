package com.restaurant.repository;

import com.restaurant.model.KitchenTask;
import com.restaurant.model.Order;
import com.restaurant.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KitchenTaskRepository extends JpaRepository<KitchenTask, Long> {
    
    List<KitchenTask> findByOrder(Order order);
    
    List<KitchenTask> findByStaff(Staff staff);
    
    List<KitchenTask> findByStatus(KitchenTask.TaskStatus status);
    
    List<KitchenTask> findByPriority(KitchenTask.Priority priority);
    
    @Query("SELECT kt FROM KitchenTask kt WHERE kt.status = 'PENDING' ORDER BY kt.priority DESC, kt.createdAt ASC")
    List<KitchenTask> findPendingTasksOrderedByPriority();
    
    @Query("SELECT kt FROM KitchenTask kt WHERE kt.status = 'IN_PROGRESS' ORDER BY kt.startedAt ASC")
    List<KitchenTask> findInProgressTasks();
    
    @Query("SELECT kt FROM KitchenTask kt WHERE kt.staff = :staff AND kt.status IN ('PENDING', 'IN_PROGRESS') ORDER BY kt.priority DESC, kt.createdAt ASC")
    List<KitchenTask> findActiveTasksByStaff(@Param("staff") Staff staff);
    
    @Query("SELECT kt FROM KitchenTask kt WHERE kt.order = :order AND kt.status = :status")
    List<KitchenTask> findTasksByOrderAndStatus(@Param("order") Order order, @Param("status") KitchenTask.TaskStatus status);
    
    @Query("SELECT kt FROM KitchenTask kt WHERE kt.status = 'IN_PROGRESS' AND kt.startedAt < :threshold")
    List<KitchenTask> findDelayedTasks(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT COUNT(kt) FROM KitchenTask kt WHERE kt.staff = :staff AND kt.status = 'IN_PROGRESS'")
    Long countActiveTasksByStaff(@Param("staff") Staff staff);
    
    @Query("SELECT kt FROM KitchenTask kt WHERE kt.status = 'PENDING' AND kt.priority = 'URGENT' ORDER BY kt.createdAt ASC")
    List<KitchenTask> findUrgentPendingTasks();
    
    @Query("SELECT kt FROM KitchenTask kt WHERE kt.status = 'IN_PROGRESS' AND kt.startedAt <= :timeThreshold")
    List<KitchenTask> findOverdueTasks(@Param("timeThreshold") LocalDateTime timeThreshold);
    
    @Query("SELECT kt FROM KitchenTask kt WHERE kt.order = :order ORDER BY kt.priority DESC, kt.createdAt ASC")
    List<KitchenTask> findTasksByOrderOrderedByPriority(@Param("order") Order order);
}
