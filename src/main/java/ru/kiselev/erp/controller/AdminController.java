package ru.kiselev.erp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kiselev.erp.dto.request.ManufacturerDto;
import ru.kiselev.erp.model.admin.Manufacturer;
import ru.kiselev.erp.repository.admin.ManufacturerRepository;
import ru.kiselev.erp.service.impl.AdminService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final ManufacturerRepository manufacturerRepository;

    @GetMapping("/users")
    public String getAllUsers(Model model){
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password) {

        adminService.register(username, password);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam Long id) {
        adminService.deleteUserById(id);
        return "redirect:/admin/users";
    }

    /*@GetMapping("/manufacturers")
    public String getAllManufacturers(Model model){
        model.addAttribute("manufacturers", adminService.getAllManufacturers());
        return "manufacturers";
    }

    @PostMapping("/delete-manufacturer")
    public String deleteManufacturer(@RequestParam Long id) {
        adminService.deleteManufacturerById(id);
        return "redirect:/admin/manufacturers";
    }*/

    @PostMapping("/save")
    public String addManufacturer(@ModelAttribute("manufacturerDto") ManufacturerDto manufacturerDto,
                                  RedirectAttributes redirectAttributes) {
        try {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setName(manufacturerDto.getName());
            manufacturer.setCode(manufacturerDto.getCode());

            manufacturerRepository.save(manufacturer);

            redirectAttributes.addFlashAttribute("successMessage", "Производитель успешно создан");
            return "redirect:/manufacturers";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании производителя");
            return "redirect:/manufacturers/new";
        }
    }
}
