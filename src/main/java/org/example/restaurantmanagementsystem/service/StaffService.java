package org.example.restaurantmanagementsystem.service;

import com.restaurant.dto.StaffDTO;
import com.restaurant.entity.Staff;
import com.restaurant.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StaffService<StaffDTO, Staff> {

    @Autowired
    private StaffRepository staffRepository;

    public List<StaffDTO> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<StaffDTO> getStaffById(Long id) {
        return staffRepository.findById(id)
                .map(this::convertToDTO);
    }

    public StaffDTO createStaff(StaffDTO staffDTO) {
        if (staffRepository.existsByEmail(staffDTO.getEmail())) {
            throw new RuntimeException("Staff member with email " + staffDTO.getEmail() + " already exists");
        }

        Staff staff = convertToEntity(staffDTO);
        Staff savedStaff = staffRepository.save(staff);
        return convertToDTO(savedStaff);
    }

    public StaffDTO updateStaff(Long id, StaffDTO staffDTO) {
        Optional<Staff> existingStaff = staffRepository.findById(id);
        if (existingStaff.isPresent()) {
            Staff staff = existingStaff.get();
            staff.setFirstName(staffDTO.getFirstName());
            staff.setLastName(staffDTO.getLastName());
            staff.setEmail(staffDTO.getEmail());
            staff.setPhoneNumber(staffDTO.getPhoneNumber());
            staff.setRole(staffDTO.getRole());

            Staff updatedStaff = staffRepository.save(staff);
            return convertToDTO(updatedStaff);
        }
        throw new RuntimeException("Staff member not found with id: " + id);
    }

    public void deleteStaff(Long id) {
        if (staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
        } else {
            throw new RuntimeException("Staff member not found with id: " + id);
        }
    }

    public List<StaffDTO> getStaffByRole(String role) {
        return staffRepository.findByRole(role).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private StaffDTO convertToDTO(Staff staff) {
        StaffDTO dto = new StaffDTO();
        dto.setId(staff.getId());
        dto.setFirstName(staff.getFirstName());
        dto.setLastName(staff.getLastName());
        dto.setEmail(staff.getEmail());
        dto.setPhoneNumber(staff.getPhoneNumber());
        dto.setRole(staff.getRole());
        return dto;
    }

    private Staff convertToEntity(StaffDTO dto) {
        return new Staff(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getPhoneNumber(),
                dto.getRole()
        );
    }
}