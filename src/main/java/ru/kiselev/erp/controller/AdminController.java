package ru.kiselev.erp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kiselev.erp.dto.request.admin.CreateProductRequest;
import ru.kiselev.erp.dto.request.admin.ManufacturerDto;
import ru.kiselev.erp.dto.request.admin.RegisterUserRequest;
import ru.kiselev.erp.dto.request.admin.UpdateProductRequest;
import ru.kiselev.erp.exception.*;
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
    public String register(@ModelAttribute("registerUserDto") RegisterUserRequest registerUserRequest,
                           RedirectAttributes redirectAttributes) {
        try {
            adminService.register(registerUserRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно создан");
        } catch (UsernameAlreadyExistException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь с таким именем уже существует");
            redirectAttributes.addFlashAttribute("showForm", true);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam Long id,
                            RedirectAttributes redirectAttributes) {
        adminService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удалён");
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

        }
        catch (IllegalArgumentException e) {
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
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении производителя");
        }

        return "redirect:/admin/manufacturers";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", adminService.getAllProducts());
        model.addAttribute("manufacturers", adminService.getAllManufacturers());
        model.addAttribute("productTypes", adminService.getAllProductTypes());
        model.addAttribute("productsEditData", adminService.getProductsForEdit());

        if (!model.containsAttribute("productDto")) {
            model.addAttribute("productDto", new CreateProductRequest());
        }

        if (!model.containsAttribute("showProductForm")) {
            model.addAttribute("showProductForm", false);
        }

        return "admin/products";
    }

    private void fillModel(Model model) {
        model.addAttribute("products", adminService.getAllProducts());
        model.addAttribute("manufacturers", adminService.getAllManufacturers());
        model.addAttribute("productTypes", adminService.getAllProductTypes());
    }

    @PostMapping("/products/save")
    public String addProduct(
            @Valid @ModelAttribute("productDto") CreateProductRequest dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            fillModel(model);

            model.addAttribute("productDto", dto);
            model.addAttribute("showProductForm", true);
            model.addAttribute("errorMessage", "Ошибка валидации формы");

            return "admin/products";
        }

        try {
            adminService.createProduct(dto);

            redirectAttributes.addFlashAttribute("successMessage", "Продукт успешно создан");
            return "redirect:/admin/products";

        }
        catch (ProductAlreadyExistsByNameException p) {

            fillModel(model);

            model.addAttribute("productDto", dto);
            model.addAttribute("showProductForm", true);
            model.addAttribute("errorMessage", "Продукт с таким названием уже существует");

            return "admin/products";
        }
        catch (VariantSkuAlreadyExistException e) {

            fillModel(model);

            model.addAttribute("productDto", dto);
            model.addAttribute("showProductForm", true);
            model.addAttribute("errorMessage", "Такой артикул уже существует");

            return "admin/products";

        }
        catch (InvalidCostValueException c) {

            fillModel(model);

            model.addAttribute("productDto", dto);
            model.addAttribute("showProductForm", true);
            model.addAttribute("errorMessage", "Цена указана некорректно");

            return "admin/products";

        }
        catch (InvalidDateRangeException e) {

            fillModel(model);

            model.addAttribute("productDto", dto);
            model.addAttribute("showProductForm", true);
            model.addAttribute("errorMessage", "Дата валидности цены некорректна");

            return "admin/products";

        }
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
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении продукта");
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute UpdateProductRequest request,
                                RedirectAttributes redirectAttributes) {

        try {
            adminService.updateProduct(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Продукт успешно обновлён");

        } catch (ProductAlreadyExistsByNameException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Продукт с таким названием уже существует");

        } catch (VariantSkuAlreadyExistException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Такой артикул уже существует");

        } catch (InvalidCostValueException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Цена указана некорректно");

        } catch (InvalidDateRangeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Некорректный диапазон дат");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка обновления продукта");
        }

        return "redirect:/admin/products";
    }

    @PostMapping("products/variants/delete/{id}")
    public String deleteProductVariant(@PathVariable Long id,
                                         RedirectAttributes redirectAttributes) {

        try {
            adminService.deleteProductVariant(id);
            redirectAttributes.addFlashAttribute("successMessage", "Вариант успешно удален");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("successMessage", "Ошибка удаления продукта");
        }

        return "redirect:/admin/products";
    }
}
