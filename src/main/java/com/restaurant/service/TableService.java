package com.restaurant.service;

import com.restaurant.model.RestaurantTable;
import com.restaurant.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {
    @Autowired
    private RestaurantTableRepository tableRepository;

    public List<RestaurantTable> findAvailableTables() {
        return tableRepository.findByAvailableTrue();
    }
}