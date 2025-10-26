package org.example.restaurantmanagementsystem.service;

import com.restaurant.dto.StaffScheduleDTO;
import com.restaurant.entity.Staff;
import com.restaurant.entity.StaffSchedule;
import com.restaurant.repository.StaffRepository;
import com.restaurant.repository.StaffScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StaffScheduleService<Staff, StaffSchedule> {

    @Autowired
    private StaffScheduleRepository scheduleRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private NotificationService notificationService;

    public List<StaffScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StaffScheduleDTO> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        return scheduleRepository.findSchedulesInDateRange(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StaffScheduleDTO createSchedule(StaffScheduleDTO scheduleDTO) {
        Optional<Staff> staff = staffRepository.findById(scheduleDTO.getStaffId());
        if (staff.isEmpty()) {
            throw new RuntimeException("Staff member not found with id: " + scheduleDTO.getStaffId());
        }

        // Check for scheduling conflicts
        List<StaffSchedule> conflicts = scheduleRepository.findConflictingSchedules(
                staff.get(),
                scheduleDTO.getScheduleDate(),
                scheduleDTO.getStartTime(),
                scheduleDTO.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Scheduling conflict detected for staff member: " + staff.get().getFullName());
        }

        StaffSchedule schedule = convertToEntity(scheduleDTO);
        schedule.setStaff(staff.get());

        StaffSchedule savedSchedule = scheduleRepository.save(schedule);

        // Send notification
        notificationService.sendScheduleNotification(savedSchedule);

        return convertToDTO(savedSchedule);
    }

    public StaffScheduleDTO updateSchedule(Long id, StaffScheduleDTO scheduleDTO) {
        Optional<StaffSchedule> existingSchedule = scheduleRepository.findById(id);
        if (existingSchedule.isPresent()) {
            StaffSchedule schedule = existingSchedule.get();

            // Check for conflicts excluding current schedule
            List<StaffSchedule> conflicts = scheduleRepository.findConflictingSchedules(
                            schedule.getStaff(),
                            scheduleDTO.getScheduleDate(),
                            scheduleDTO.getStartTime(),
                            scheduleDTO.getEndTime()
                    ).stream()
                    .filter(conflict -> !conflict.getId().equals(id))
                    .collect(Collectors.toList());

            if (!conflicts.isEmpty()) {
                throw new RuntimeException("Scheduling conflict detected");
            }

            schedule.setScheduleDate(scheduleDTO.getScheduleDate());
            schedule.setStartTime(scheduleDTO.getStartTime());
            schedule.setEndTime(scheduleDTO.getEndTime());
            schedule.setShiftType(scheduleDTO.getShiftType());
            schedule.setNotes(scheduleDTO.getNotes());

            StaffSchedule updatedSchedule = scheduleRepository.save(schedule);
            return convertToDTO(updatedSchedule);
        }
        throw new RuntimeException("Schedule not found with id: " + id);
    }

    public void deleteSchedule(Long id) {
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Schedule not found with id: " + id);
        }
    }

    public List<StaffScheduleDTO> getSchedulesByStaff(Long staffId) {
        Optional<Staff> staff = staffRepository.findById(staffId);
        if (staff.isPresent()) {
            return scheduleRepository.findByStaff(staff.get()).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Staff member not found with id: " + staffId);
    }

    public List<StaffScheduleDTO> getSchedulesByDate(LocalDate date) {
        return scheduleRepository.findByScheduleDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private StaffScheduleDTO convertToDTO(StaffSchedule schedule) {
        StaffScheduleDTO dto = new StaffScheduleDTO();
        dto.setId(schedule.getId());
        dto.setStaffId(schedule.getStaff().getId());
        dto.setStaffName(schedule.getStaff().getFullName());
        dto.setScheduleDate(schedule.getScheduleDate());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setShiftType(schedule.getShiftType());
        dto.setNotes(schedule.getNotes());
        dto.setStatus(schedule.getStatus());
        return dto;
    }

    private StaffSchedule convertToEntity(StaffScheduleDTO dto) {
        return new StaffSchedule(
                null, // staff will be set separately
                dto.getScheduleDate(),
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getShiftType()
        );
    }
}