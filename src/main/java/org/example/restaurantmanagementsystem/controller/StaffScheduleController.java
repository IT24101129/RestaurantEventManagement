package org.example.restaurantmanagementsystem.controller;

import com.restaurant.dto.StaffScheduleDTO;
import com.restaurant.service.StaffScheduleService;
import com.restaurant.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/manager/schedules")
public class StaffScheduleController<StaffScheduleDTO> {

    @Autowired
    protected org.example.restaurantmanagementsystem.controller.StaffScheduleService scheduleService;

    @Autowired
    private StaffService staffService;

    @GetMapping
    public String scheduleManagementPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        // Set default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = startDate.plusWeeks(2);
        }

        try {
            List<StaffScheduleDTO> schedules = scheduleService.getClass(startDate, endDate);

            model.addAttribute("schedules", schedules);
            model.addAttribute("staffList", staffService.getAllStaff());
            model.addAttribute("scheduleDTO", new StaffScheduleDTO());
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

        } catch (Exception e) {
            model.addAttribute("error", "Error loading schedules: " + e.getMessage());
            model.addAttribute("schedules", List.of());
            model.addAttribute("staffList", staffService.getAllStaff());
            model.addAttribute("scheduleDTO", new StaffScheduleDTO());
        }

        return "schedule-management";
    }

    @PostMapping("/add")
    public String addSchedule(@Valid @ModelAttribute("scheduleDTO") StaffScheduleDTO scheduleDTO,
                              BindingResult result, Model model) {

        if (result.hasErrors()) {
            // Return to form with validation errors
            model.addAttribute("staffList", staffService.getAllStaff());
            model.addAttribute("schedules", scheduleService.getAllSchedules());
            model.addAttribute("startDate", LocalDate.now());
            model.addAttribute("endDate", LocalDate.now().plusWeeks(2));
            return "schedule-management";
        }

        try {
            scheduleService.createSchedule(scheduleDTO);
            return "redirect:/manager/schedules?success=Schedule+added+successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating schedule: " + e.getMessage());
            model.addAttribute("staffList", staffService.getAllStaff());
            model.addAttribute("schedules", scheduleService.getAllSchedules());
            model.addAttribute("startDate", LocalDate.now());
            model.addAttribute("endDate", LocalDate.now().plusWeeks(2));
            return "schedule-management";
        }
    }

    @GetMapping("/edit/{id}")
    public String editScheduleForm(@PathVariable("id") Long id, Model model) {
        try {
            // Get all schedules to find the one we're editing
            StaffScheduleDTO scheduleDTO = scheduleService.getAllSchedules().stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));

            model.addAttribute("scheduleDTO", scheduleDTO);
            model.addAttribute("staffList", staffService.getAllStaff());
            model.addAttribute("schedules", scheduleService.getAllSchedules());
            model.addAttribute("startDate", LocalDate.now());
            model.addAttribute("endDate", LocalDate.now().plusWeeks(2));

            return "schedule-management";
        } catch (Exception e) {
            return "redirect:/manager/schedules?error=" + e.getMessage();
        }
    }

    @PostMapping("/update/{id}")
    public String updateSchedule(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("scheduleDTO") StaffScheduleDTO scheduleDTO,
                                 BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("staffList", staffService.getAllStaff());
            model.addAttribute("schedules", scheduleService.getAllSchedules());
            model.addAttribute("startDate", LocalDate.now());
            model.addAttribute("endDate", LocalDate.now().plusWeeks(2));
            return "schedule-management";
        }

        try {
            scheduleService.updateSchedule(id, scheduleDTO);
            return "redirect:/manager/schedules?success=Schedule+updated+successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating schedule: " + e.getMessage());
            model.addAttribute("staffList", staffService.getAllStaff());
            model.addAttribute("schedules", scheduleService.getAllSchedules());
            model.addAttribute("startDate", LocalDate.now());
            model.addAttribute("endDate", LocalDate.now().plusWeeks(2));
            return "schedule-management";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable("id") Long id) {
        try {
            scheduleService.deleteSchedule(id);
            return "redirect:/manager/schedules?success=Schedule+deleted+successfully";
        } catch (Exception e) {
            return "redirect:/manager/schedules?error=" + e.getMessage();
        }
    }

    // API endpoints for AJAX calls
    @GetMapping("/api")
    @ResponseBody
    public List<StaffScheduleDTO> getSchedulesApi(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            if (startDate != null && endDate != null) {
                return scheduleService.getSchedulesByDateRange(startDate, endDate);
            }
            return scheduleService.getAllSchedules();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching schedules: " + e.getMessage());
        }
    }

    @GetMapping("/api/staff/{staffId}")
    @ResponseBody
    public List<StaffScheduleDTO> getSchedulesByStaff(@PathVariable("staffId") Long staffId) {
        try {
            return scheduleService.getSchedulesByStaff(staffId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching staff schedules: " + e.getMessage());
        }
    }

    @GetMapping("/api/date/{date}")
    @ResponseBody
    public List<StaffScheduleDTO> getSchedulesByDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return scheduleService.getSchedulesByDate(date);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching schedules for date: " + e.getMessage());
        }
    }
}