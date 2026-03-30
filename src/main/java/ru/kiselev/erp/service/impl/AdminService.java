package ru.kiselev.erp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kiselev.erp.dto.request.*;
import ru.kiselev.erp.dto.response.UserDto;
import ru.kiselev.erp.model.Role;
import ru.kiselev.erp.model.User;
import ru.kiselev.erp.model.admin.*;
import ru.kiselev.erp.repository.UserRepository;
import ru.kiselev.erp.repository.admin.ManufacturerRepository;
import ru.kiselev.erp.repository.admin.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminService {

    UserRepository userRepository;
    ManufacturerRepository manufacturerRepository;
    ProductRepository productRepository;
    PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers(){
        return userRepository.findAll()
                .stream().map(UserDto::new)
                .collect(Collectors.toList());
    }

    public void register(String username, String password) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    public void deleteUserById(Long id){
        userRepository.deleteById(id);
    }

    public List<ManufacturerDto> getAllManufacturers(){
        return manufacturerRepository.findAll()
                .stream().map(ManufacturerDto::new)
                .collect(Collectors.toList());
    }

    public ManufacturerDto addManufacturer(ManufacturerDto manufacturerDto){

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(manufacturerDto.getName());
        manufacturer.setCode(manufacturerDto.getCode());

        manufacturerRepository.save(manufacturer);
        return manufacturerDto;
    }

    public void deleteManufacturerById(Long id){
        manufacturerRepository.deleteById(id);
    }

    public ProductDto addProduct(ProductDto productDto){

        Product product = new Product();
        product.setName(productDto.getName());
        product.setManufacturer(productDto.getManufacturer());

        productRepository.save(product);
        return productDto;
    }

    @Transactional
    public Product createProduct(CreateProductRequest request){

        Product product = new Product();
        product.setName(request.getName());
        product.setType(request.getType());

        Manufacturer manufacturer = manufacturerRepository.findById(request.getManufacturerId())
                .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        product.setManufacturer(manufacturer);

        for (ProductVariantDto productVariantDto : request.getVariants()){

            ProductVariant productVariant = new ProductVariant();
            productVariant.setSku(productVariantDto.getSku());
            productVariant.setProduct(product);

            for (VariantAttributeDto attributeDto : productVariantDto.getAttributes()){

                VariantAttribute variantAttribute = new VariantAttribute();
                variantAttribute.setName(attributeDto.getName());
                variantAttribute.setValue(attributeDto.getValue());
                variantAttribute.setProductVariant(productVariant);

                productVariant.getVariantAttributes().add(variantAttribute);
            }

            for (VariantCostDto variantCostDto : productVariantDto.getCosts()){
                VariantCost variantCost = new VariantCost();
                variantCost.setBasePrice(variantCostDto.getBasePrice());
                variantCost.setValidFrom(variantCostDto.getValidFrom());
                variantCost.setValidTo(variantCostDto.getValidTo());

                productVariant.getVariantCosts().add(variantCost);
            }
        }
        return productRepository.save(product);
    }

}
