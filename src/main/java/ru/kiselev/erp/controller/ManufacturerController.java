package ru.kiselev.erp.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kiselev.erp.dto.request.ManufacturerDto;
import ru.kiselev.erp.model.admin.Manufacturer;
import ru.kiselev.erp.repository.admin.ManufacturerRepository;

@Controller
@RequestMapping("/admin/manufacturers")
public class ManufacturerController {

    private final ManufacturerRepository manufacturerRepository;

    public ManufacturerController(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    @GetMapping
    public String listManufacturers(Model model) {
        model.addAttribute("manufacturers", manufacturerRepository.findAll());
        model.addAttribute("manufacturerDto", new ManufacturerDto());
        model.addAttribute("showForm", false);
        return "admin/list";
    }

    @PostMapping("/save")
    public String addManufacturer(@Valid @ModelAttribute("manufacturerDto") ManufacturerDto manufacturerDto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (manufacturerRepository.existsByCode(manufacturerDto.getCode())) {
            bindingResult.rejectValue("code", "error.code", "Производитель с таким кодом уже существует");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("manufacturers", manufacturerRepository.findAll());
            model.addAttribute("showForm", true);
            return "admin/list";
        }

        try {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setName(manufacturerDto.getName());
            manufacturer.setCode(manufacturerDto.getCode());

            manufacturerRepository.save(manufacturer);

            // Используем flash-атрибут для передачи сообщения
            redirectAttributes.addFlashAttribute("successMessage", "Производитель успешно создан");
            return "redirect:/admin/manufacturers";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании производителя");
            return "redirect:/admin/manufacturers";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteManufacturer(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            if (manufacturerRepository.existsById(id)) {
                manufacturerRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Производитель успешно удален");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Производитель не найден");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении производителя");
        }

        return "redirect:/admin/manufacturers";
    }
}
