package com.restaurant.repository;

import com.restaurant.model.MenuItem;
import com.restaurant.model.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    List<MenuItem> findByCategory(MenuCategory category);
    
    List<MenuItem> findByIsAvailableTrue();
    
    List<MenuItem> findByCategoryAndIsAvailableTrue(MenuCategory category);
    
    @Query("SELECT mi FROM MenuItem mi WHERE mi.isAvailable = true ORDER BY mi.category.displayOrder, mi.name")
    List<MenuItem> findAvailableItemsOrdered();
    
    @Query("SELECT mi FROM MenuItem mi WHERE mi.name LIKE %:name%")
    List<MenuItem> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT mi FROM MenuItem mi WHERE mi.price BETWEEN :minPrice AND :maxPrice")
    List<MenuItem> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}
