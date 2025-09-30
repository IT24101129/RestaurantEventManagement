package com.resturant.restaurantapp.repository;

import com.resturant.restaurantapp.model.Menu;
import com.resturant.restaurantapp.model.MenuItem;
import com.resturant.restaurantapp.model.MenuPackageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuPackageItemRepository extends JpaRepository<MenuPackageItem, Long> {
    
    List<MenuPackageItem> findByMenu(Menu menu);
    
    List<MenuPackageItem> findByMenuItem(MenuItem menuItem);
    
    List<MenuPackageItem> findByMenuAndIsIncludedTrue(Menu menu);
    
    List<MenuPackageItem> findByMenuAndIsOptionalTrue(Menu menu);
    
    @Query("SELECT mpi FROM MenuPackageItem mpi WHERE mpi.menu = :menu AND mpi.isIncluded = true ORDER BY mpi.menuItem.itemCategory, mpi.menuItem.itemName")
    List<MenuPackageItem> findIncludedItemsByMenu(@Param("menu") Menu menu);
    
    @Query("SELECT mpi FROM MenuPackageItem mpi WHERE mpi.menu = :menu AND mpi.isOptional = true ORDER BY mpi.menuItem.itemCategory, mpi.menuItem.itemName")
    List<MenuPackageItem> findOptionalItemsByMenu(@Param("menu") Menu menu);
    
    @Query("SELECT mpi FROM MenuPackageItem mpi WHERE mpi.menu = :menu ORDER BY mpi.menuItem.itemCategory, mpi.menuItem.itemName")
    List<MenuPackageItem> findAllItemsByMenu(@Param("menu") Menu menu);
    
    boolean existsByMenuAndMenuItem(Menu menu, MenuItem menuItem);
    
    void deleteByMenu(Menu menu);
}

