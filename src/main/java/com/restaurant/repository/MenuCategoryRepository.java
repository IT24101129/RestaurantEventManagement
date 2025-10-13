package com.restaurant.repository;

import com.restaurant.model.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
    
    List<MenuCategory> findByIsActiveTrue();
    
    List<MenuCategory> findByIsActiveTrueOrderByDisplayOrder();
    
    @Query("SELECT mc FROM MenuCategory mc WHERE mc.isActive = true ORDER BY mc.displayOrder ASC")
    List<MenuCategory> findActiveCategoriesOrdered();
    
    @Query("SELECT mc FROM MenuCategory mc WHERE mc.name LIKE %:name%")
    List<MenuCategory> findByNameContaining(@Param("name") String name);
}
