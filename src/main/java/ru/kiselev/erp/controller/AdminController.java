package ru.kiselev.erp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kiselev.erp.dto.request.CreateProductRequest;
import ru.kiselev.erp.dto.request.ManufacturerDto;
import ru.kiselev.erp.repository.admin.ManufacturerRepository;
import ru.kiselev.erp.repository.admin.ProductRepository;
import ru.kiselev.erp.service.impl.AdminService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final ManufacturerRepository manufacturerRepository;
    private final ProductRepository productRepository;

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

    //
    @GetMapping("/manufacturers")
    public String listManufacturers(Model model) {
        model.addAttribute("manufacturers", adminService.getAllManufacturers());
        model.addAttribute("manufacturerDto", new ManufacturerDto());
        model.addAttribute("showForm", false);
        return "admin/list";
    }

    @PostMapping("/manufacturers/save")
    public String addManufacturer(@Valid @ModelAttribute("manufacturerDto") ManufacturerDto manufacturerDto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (manufacturerRepository.existsByCode(manufacturerDto.getCode())) {
            bindingResult.rejectValue("code", "error.code", "Производитель с таким кодом уже существует");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("manufacturers", adminService.getAllManufacturers());
            model.addAttribute("showForm", true);
            return "admin/list";
        }

        try {
            adminService.addManufacturer(manufacturerDto);
            redirectAttributes.addFlashAttribute("successMessage", "Производитель успешно создан");
            return "redirect:/admin/manufacturers";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании производителя");
            return "redirect:/admin/manufacturers";
        }
    }

    @PostMapping("/manufacturers/delete/{id}")
    public String deleteManufacturer(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            if (manufacturerRepository.existsById(id)) {
                adminService.deleteManufacturerById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Производитель успешно удален");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Производитель не найден");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении производителя");
        }

        return "redirect:/admin/manufacturers";
    }

    @GetMapping("/products")
    public String listProducts(//@PathVariable Long productId,
                               //@PathVariable Long variantId,
                               Model model) {
        model.addAttribute("products", adminService.getAllProducts());
        //model.addAttribute("productVariants", adminService.getAllProductVariantsById(productId));
        //model.addAttribute("VariantAttributes", adminService.getAllVariantAttributesByVariantId(variantId));
        model.addAttribute("manufacturers", adminService.getAllManufacturers());
        model.addAttribute("productTypes", adminService.getAllProductTypes());
        model.addAttribute("showProductForm", false);
        return "admin/products";
    }

    @PostMapping("/products/save")
    public String addProduct(@Valid @ModelAttribute("productDto") CreateProductRequest createProductRequest,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            String error = bindingResult.getFieldErrors()
                    .stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .findFirst()
                    .orElse("Ошибка валидации формы");

            redirectAttributes.addFlashAttribute("errorMessage", error);
            redirectAttributes.addFlashAttribute("showProductForm", true);

            return "redirect:/admin/products";
        }

        try {
            adminService.createProduct(createProductRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Продукт успешно создан");

        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Продукт с таким названием уже существует");
            redirectAttributes.addFlashAttribute("showProductForm", true);
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            if (productRepository.existsById(id)) {
                adminService.deleteProductById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Продукт успешно удален");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Продукт не найден");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении продукта");
        }

        return "redirect:/admin/products";
    }
}
