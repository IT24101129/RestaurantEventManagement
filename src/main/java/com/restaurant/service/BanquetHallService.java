package com.restaurant.service;

import com.restaurant.model.*;
import com.restaurant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BanquetHallService {
    
    private final EventBookingRepository eventBookingRepository;
    private final EventStaffAssignmentRepository eventStaffAssignmentRepository;
    private final EventEquipmentRepository eventEquipmentRepository;
    private final StaffRepository staffRepository;
    private final NotificationService notificationService;
    
    @Autowired
    public BanquetHallService(EventBookingRepository eventBookingRepository,
                             EventStaffAssignmentRepository eventStaffAssignmentRepository,
                             EventEquipmentRepository eventEquipmentRepository,
                             StaffRepository staffRepository,
                             NotificationService notificationService) {
        this.eventBookingRepository = eventBookingRepository;
        this.eventStaffAssignmentRepository = eventStaffAssignmentRepository;
        this.eventEquipmentRepository = eventEquipmentRepository;
        this.staffRepository = staffRepository;
        this.notificationService = notificationService;
    }
    
    // Event Schedule Management
    public List<EventBooking> getUpcomingEvents(LocalDate startDate, LocalDate endDate) {
        return eventBookingRepository.findByEventDateBetweenOrderByEventDateAscStartTimeAsc(startDate, endDate);
    }
    
    public List<EventBooking> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1);
        return getUpcomingEvents(today, nextMonth);
    }
    
    public Map<String, Object> getEventScheduleDashboard(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Get upcoming events
        List<EventBooking> upcomingEvents = getUpcomingEvents(startDate, endDate);
        dashboard.put("upcomingEvents", upcomingEvents);
        
        // Get events by status
        Map<EventBooking.BookingStatus, List<EventBooking>> eventsByStatus = upcomingEvents.stream()
            .collect(Collectors.groupingBy(EventBooking::getStatus));
        dashboard.put("eventsByStatus", eventsByStatus);
        
        // Get staff assignments
        List<EventStaffAssignment> staffAssignments = eventStaffAssignmentRepository
            .findActiveAssignmentsForDateRange(startDate, endDate);
        dashboard.put("staffAssignments", staffAssignments);
        
        // Get equipment allocations
        List<EventEquipment> equipmentAllocations = eventEquipmentRepository
            .findActiveEquipmentForDateRange(startDate, endDate);
        dashboard.put("equipmentAllocations", equipmentAllocations);
        
        // Get available staff
        List<Staff> availableStaff = staffRepository.findByIsAvailableTrue();
        dashboard.put("availableStaff", availableStaff);
        
        // Get hall availability conflicts
        List<EventBooking> conflicts = findHallAvailabilityConflicts(startDate, endDate);
        dashboard.put("conflicts", conflicts);
        
        return dashboard;
    }
    
    // Hall Availability Management
    public List<EventBooking> findHallAvailabilityConflicts(LocalDate startDate, LocalDate endDate) {
        List<EventBooking> allBookings = getUpcomingEvents(startDate, endDate);
        List<EventBooking> conflicts = new ArrayList<>();
        
        for (int i = 0; i < allBookings.size(); i++) {
            for (int j = i + 1; j < allBookings.size(); j++) {
                EventBooking booking1 = allBookings.get(i);
                EventBooking booking2 = allBookings.get(j);
                
                if (hasTimeConflict(booking1, booking2)) {
                    conflicts.add(booking1);
                    conflicts.add(booking2);
                }
            }
        }
        
        return conflicts.stream().distinct().collect(Collectors.toList());
    }
    
    private boolean hasTimeConflict(EventBooking booking1, EventBooking booking2) {
        if (!booking1.getEventDate().equals(booking2.getEventDate())) {
            return false;
        }
        
        LocalTime start1 = booking1.getStartTime();
        LocalTime end1 = booking1.getEndTime();
        LocalTime start2 = booking2.getStartTime();
        LocalTime end2 = booking2.getEndTime();
        
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
    
    public List<LocalTime> getAlternativeTimeSlots(LocalDate date, LocalTime preferredStart, LocalTime preferredEnd) {
        List<LocalTime> alternatives = new ArrayList<>();
        
        // Check for available slots around the preferred time
        for (int hour = 9; hour <= 21; hour++) {
            LocalTime slotStart = LocalTime.of(hour, 0);
            LocalTime slotEnd = slotStart.plusHours(2); // 2-hour slots
            
            if (isTimeSlotAvailable(date, slotStart, slotEnd)) {
                alternatives.add(slotStart);
            }
        }
        
        return alternatives;
    }
    
    private boolean isTimeSlotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<EventBooking> existingBookings = eventBookingRepository.findByEventDate(date);
        
        return existingBookings.stream().noneMatch(booking -> 
            startTime.isBefore(booking.getEndTime()) && endTime.isAfter(booking.getStartTime())
        );
    }
    
    // Staff Assignment Management
    public EventStaffAssignment assignStaffToEvent(Long eventBookingId, Long staffId, String role, 
                                                   Integer assignedHours, String startTime, String endTime, 
                                                   String assignedBy) {
        EventBooking eventBooking = eventBookingRepository.findById(eventBookingId)
            .orElseThrow(() -> new RuntimeException("Event booking not found"));
        
        Staff staff = staffRepository.findById(staffId)
            .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Check if staff is available
        if (!isStaffAvailableForEvent(staff, eventBooking.getEventDate())) {
            throw new RuntimeException("Staff is not available for this event date");
        }
        
        EventStaffAssignment assignment = new EventStaffAssignment(
            eventBooking, staff, role, assignedHours, startTime, endTime, assignedBy
        );
        
        EventStaffAssignment savedAssignment = eventStaffAssignmentRepository.save(assignment);
        
        // Send notification to staff
        notificationService.sendEventStaffAssignmentNotification(savedAssignment);
        
        return savedAssignment;
    }
    
    public boolean isStaffAvailableForEvent(Staff staff, LocalDate eventDate) {
        Long activeAssignments = eventStaffAssignmentRepository
            .countActiveAssignmentsForStaffOnDate(staff, eventDate);
        return activeAssignments == 0;
    }
    
    public List<Staff> getAvailableStaffForEvent(LocalDate eventDate, String role) {
        List<Staff> allStaff = staffRepository.findByIsAvailableTrue();
        
        return allStaff.stream()
            .filter(staff -> isStaffAvailableForEvent(staff, eventDate))
            .filter(staff -> role == null || staff.getPosition().name().contains(role.toUpperCase()))
            .collect(Collectors.toList());
    }
    
    // Equipment Management
    public EventEquipment assignEquipmentToEvent(Long eventBookingId, String equipmentName, 
                                                String equipmentType, Integer quantity, 
                                                BigDecimal unitCost, String assignedBy) {
        EventBooking eventBooking = eventBookingRepository.findById(eventBookingId)
            .orElseThrow(() -> new RuntimeException("Event booking not found"));
        
        EventEquipment equipment = new EventEquipment(
            eventBooking, equipmentName, equipmentType, quantity, unitCost, assignedBy
        );
        
        return eventEquipmentRepository.save(equipment);
    }
    
    public List<EventEquipment> getEquipmentForEvent(Long eventBookingId) {
        EventBooking eventBooking = eventBookingRepository.findById(eventBookingId)
            .orElseThrow(() -> new RuntimeException("Event booking not found"));
        
        return eventEquipmentRepository.findByEventBooking(eventBooking);
    }
    
    public Map<String, Integer> getResourceAllocationSummary(LocalDate date) {
        List<EventEquipment> equipment = eventEquipmentRepository.findActiveEquipmentForDate(date);
        
        return equipment.stream()
            .collect(Collectors.groupingBy(
                EventEquipment::getEquipmentType,
                Collectors.summingInt(EventEquipment::getQuantity)
            ));
    }
    
    public boolean checkResourceAvailability(String equipmentType, Integer requestedQuantity, LocalDate date) {
        Map<String, Integer> allocation = getResourceAllocationSummary(date);
        Integer currentAllocation = allocation.getOrDefault(equipmentType, 0);
        
        // Assume we have a maximum capacity for each equipment type
        Map<String, Integer> maxCapacity = Map.of(
            "Audio", 10,
            "Visual", 15,
            "Furniture", 50,
            "Catering", 20
        );
        
        Integer maxAvailable = maxCapacity.getOrDefault(equipmentType, 10);
        return (currentAllocation + requestedQuantity) <= maxAvailable;
    }
    
    // Schedule Confirmation and Updates
    public EventBooking confirmEventSchedule(Long eventBookingId, String confirmedBy) {
        EventBooking eventBooking = eventBookingRepository.findById(eventBookingId)
            .orElseThrow(() -> new RuntimeException("Event booking not found"));
        
        eventBooking.setStatus(EventBooking.BookingStatus.CONFIRMED);
        EventBooking savedBooking = eventBookingRepository.save(eventBooking);
        
        // Send confirmation notifications to assigned staff
        List<EventStaffAssignment> assignments = eventStaffAssignmentRepository
            .findByEventBooking(eventBooking);
        
        for (EventStaffAssignment assignment : assignments) {
            notificationService.sendEventScheduleConfirmationNotification(assignment);
        }
        
        return savedBooking;
    }
    
    public EventBooking updateEventStatus(Long eventBookingId, EventBooking.BookingStatus newStatus) {
        EventBooking eventBooking = eventBookingRepository.findById(eventBookingId)
            .orElseThrow(() -> new RuntimeException("Event booking not found"));
        
        eventBooking.setStatus(newStatus);
        return eventBookingRepository.save(eventBooking);
    }
    
    // Error Handling and Branching Actions
    public Map<String, Object> handleEventDataLoadFailure() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "Failed to load event data");
        response.put("suggestion", "Please check your internet connection and try again");
        response.put("retryAction", "refreshData");
        return response;
    }
    
    public Map<String, Object> handleHallBookingConflict(EventBooking conflictingBooking) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "Hall is already booked for the selected time");
        response.put("conflictingBooking", conflictingBooking);
        response.put("suggestion", "Please select an alternative time slot");
        response.put("alternativeSlots", getAlternativeTimeSlots(
            conflictingBooking.getEventDate(), 
            conflictingBooking.getStartTime(), 
            conflictingBooking.getEndTime()
        ));
        return response;
    }
    
    public Map<String, Object> handleResourceAllocationExceeded(String equipmentType, Integer requestedQuantity) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "Resource allocation exceeds available limits");
        response.put("equipmentType", equipmentType);
        response.put("requestedQuantity", requestedQuantity);
        response.put("suggestion", "Please reduce the quantity or select alternative equipment");
        return response;
    }
    
    public Map<String, Object> handleStaffNotificationFailure(EventStaffAssignment assignment) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", "Failed to send notification to assigned staff");
        response.put("assignment", assignment);
        response.put("suggestion", "Please manually contact the staff member");
        response.put("manualFollowUp", true);
        return response;
    }
}
