package com.restaurant.controller;

import com.restaurant.model.Schedule;
import com.restaurant.model.Staff;
import com.restaurant.model.User;
import com.restaurant.service.ScheduleService;
import com.restaurant.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final ScheduleService scheduleService;
    private final userService userService;

    @Autowired
    public ManagerController(ScheduleService scheduleService, userService userService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String managerDashboard(Model model, Authentication authentication) {
        // Get current user info
        if (authentication != null) {
            String email = authentication.getName();
            Optional<User> user = userService.findByEmail(email);
            user.ifPresent(u -> model.addAttribute("user", u));
        }

        // Get current week's schedules for overview
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        Map<LocalDate, List<Schedule>> schedules = scheduleService.getSchedulesGroupedByDate(weekStart, weekEnd);
        List<Staff> availableStaff = scheduleService.getAvailableStaff();
        
        model.addAttribute("schedules", schedules);
        model.addAttribute("availableStaff", availableStaff);
        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekEnd);
        model.addAttribute("today", today);
        
        return "manager/dashboard";
    }

    @GetMapping("/staff-schedule")
    public String staffScheduleDashboard(Model model, Authentication authentication) {
        // Get current user info
        if (authentication != null) {
            String email = authentication.getName();
            Optional<User> user = userService.findByEmail(email);
            user.ifPresent(u -> model.addAttribute("user", u));
        }

        // Get current week's schedules
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        Map<LocalDate, List<Schedule>> schedules = scheduleService.getSchedulesGroupedByDate(weekStart, weekEnd);
        List<Staff> availableStaff = scheduleService.getAvailableStaff();
        
        model.addAttribute("schedules", schedules);
        model.addAttribute("availableStaff", availableStaff);
        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekEnd);
        model.addAttribute("today", today);
        
        return "manager/dashboard";
    }
}
