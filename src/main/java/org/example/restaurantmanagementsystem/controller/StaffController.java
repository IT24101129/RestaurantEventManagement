package org.example.restaurantmanagementsystem.controller;

import com.restaurant.management.model.Staff;
import com.restaurant.management.dto.StaffDTO;
import com.restaurant.management.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping
    public String staffDashboard(Model model) {
        List<Staff> staffList = staffService.getActiveStaff();
        model.addAttribute("staffList", staffList);
        model.addAttribute("staffDTO", new StaffDTO());
        return "manager/staff-management";
    }

    @GetMapping("/add")
    public String showAddStaffForm(Model model) {
        model.addAttribute("staffDTO", new StaffDTO());
        return "manager/add-staff";
    }

    @PostMapping("/add")
    public String addStaff(@Valid @ModelAttribute StaffDTO staffDTO,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "manager/add-staff";
        }

        try {
            staffService.createStaff(staffDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Staff member added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding staff member: " + e.getMessage());
        }

        return "redirect:/manager/staff";
    }

    @GetMapping("/edit/{id}")
    public String showEditStaffForm(@PathVariable Long id, Model model) {
        Optional<Staff> staff = staffService.getStaffById(id);
        if (staff.isEmpty()) {
            return "redirect:/manager/staff";
        }

        Staff staffMember = staff.get();
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setId(staffMember.getId());
        staffDTO.setFirstName(staffMember.getFirstName());
        staffDTO.setLastName(staffMember.getLastName());
        staffDTO.setEmail(staffMember.getEmail());
        staffDTO.setPhoneNumber(staffMember.getPhoneNumber());
        staffDTO.setRole(staffMember.getRole());

        model.addAttribute("staffDTO", staffDTO);
        return "manager/edit-staff";
    }

    @PostMapping("/edit/{id}")
    public String updateStaff(@PathVariable Long id,
                              @Valid @ModelAttribute StaffDTO staffDTO,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "manager/edit-staff";
        }

        try {
            staffService.updateStaff(id, staffDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Staff member updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating staff member: " + e.getMessage());
        }

        return "redirect:/manager/staff";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateStaff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (staffService.deactivateStaff(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Staff member deactivated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deactivating staff member!");
        }
        return "redirect:/manager/staff";
    }

    @GetMapping("/search")
    public String searchStaff(@RequestParam String searchTerm, Model model) {
        List<Staff> staffList = staffService.searchStaff(searchTerm);
        model.addAttribute("staffList", staffList);
        model.addAttribute("searchTerm", searchTerm);
        return "manager/staff-management";
    }
}