package org.example.restaurantmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository<Staff> extends JpaRepository<Staff, Long> {
    Optional<Staff> findByEmail(String email);
    List<Staff> findByRole(String role);

    @Query("SELECT s FROM Staff s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Staff> findByNameContainingIgnoreCase(@Param("name") String name);

    boolean existsByEmail(String email);
}