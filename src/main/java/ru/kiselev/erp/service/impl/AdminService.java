package ru.kiselev.erp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kiselev.erp.dto.request.*;
import ru.kiselev.erp.dto.response.ProductResponse;
import ru.kiselev.erp.dto.response.UserDto;
import ru.kiselev.erp.exception.InvalidCostValueException;
import ru.kiselev.erp.exception.InvalidDateRangeException;
import ru.kiselev.erp.exception.ProductAlreadyExistsByNameException;
import ru.kiselev.erp.exception.VariantSkuAlreadyExistException;
import ru.kiselev.erp.model.Role;
import ru.kiselev.erp.model.User;
import ru.kiselev.erp.model.admin.*;
import ru.kiselev.erp.repository.UserRepository;
import ru.kiselev.erp.repository.admin.ManufacturerRepository;
import ru.kiselev.erp.repository.admin.ProductRepository;
import ru.kiselev.erp.repository.admin.ProductVariantRepository;
import ru.kiselev.erp.repository.admin.VariantAttributeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminService {

    UserRepository userRepository;
    ManufacturerRepository manufacturerRepository;
    ProductRepository productRepository;
    ProductVariantRepository productVariantRepository;
    VariantAttributeRepository variantAttributeRepository;
    PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers(){
        return userRepository.findAllWithoutAdmin()
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

    @Transactional
    public Product createProduct(CreateProductRequest request){

        Product product = new Product();

        if(productRepository.existsByName(request.getName())) {
            throw new ProductAlreadyExistsByNameException();
        }

        product.setName(request.getName());
        product.setType(request.getType());

        Manufacturer manufacturer = manufacturerRepository.findById(request.getManufacturerId())
                .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
        product.setManufacturer(manufacturer);

        if(request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (ProductVariantDto productVariantDto : request.getVariants()) {

                ProductVariant productVariant = new ProductVariant();

                if(productVariantRepository.existsBySku(productVariantDto.getSku())){
                    throw new VariantSkuAlreadyExistException();
                }
                productVariant.setSku(productVariantDto.getSku());
                productVariant.setProduct(product);

                product.getProductVariants().add(productVariant);

                if (productVariantDto.getVariantAttributes() != null) {
                    for (VariantAttributeDto attributeDto : productVariantDto.getVariantAttributes()) {

                        VariantAttribute variantAttribute = new VariantAttribute();
                        variantAttribute.setName(attributeDto.getName());
                        variantAttribute.setValue(attributeDto.getValue());
                        variantAttribute.setProductVariant(productVariant);

                        productVariant.getVariantAttributes().add(variantAttribute);
                    }
                }

                if (productVariantDto.getVariantCosts() != null) {
                    for (VariantCostDto dto : productVariantDto.getVariantCosts()) {

                        VariantCost variantCost = new VariantCost();
                        variantCost.setProductVariant(productVariant);

                        if(dto.getBasePrice() <= 0){
                            throw new InvalidCostValueException();
                        }

                        variantCost.setBasePrice(dto.getBasePrice());

                        if(dto.getValidFrom().isBefore(LocalDate.now())) {
                            throw new InvalidDateRangeException();
                        }

                        if(dto.getValidTo().isBefore(LocalDate.now())) {
                            throw new InvalidDateRangeException();
                        }

                        variantCost.setValidFrom(dto.getValidFrom());
                        variantCost.setValidTo(dto.getValidTo());

                        productVariant.getVariantCosts().add(variantCost);
                    }
                }
            }
        }
        return productRepository.save(product);
    }

    public void deleteProductById(Long id){
        productRepository.deleteById(id);
    }

    private ProductVariantDto mapToProductVariantDto(ProductVariant productVariant){
        ProductVariantDto productVariantDto = new ProductVariantDto();

        productVariantDto.setSku(productVariant.getSku());

        productVariantDto.setVariantAttributes(
                productVariant.getVariantAttributes().stream()
                        .map(attr->{
                            VariantAttributeDto variantAttributeDto = new VariantAttributeDto();
                            variantAttributeDto.setName(attr.getName());
                            variantAttributeDto.setValue(attr.getValue());
                            return variantAttributeDto;
                        })
                        .toList()
        );

        productVariantDto.setVariantCosts(
                productVariant.getVariantCosts().stream()
                        .map(cost->{
                            VariantCostDto variantCostDto = new VariantCostDto();
                            variantCostDto.setBasePrice(cost.getBasePrice());
                            variantCostDto.setValidFrom(cost.getValidFrom());
                            variantCostDto.setValidTo(cost.getValidTo());
                            return variantCostDto;
                        })
                        .toList()
        );

        return productVariantDto;
    }

    private ProductResponse mapToProductResponse(Product product){
        ProductResponse productResponse = new ProductResponse();

        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setType(product.getType());
        productResponse.setManufacturer(product.getManufacturer());

        productResponse.setProductVariants(
                product.getProductVariants().stream()
                        .map(this::mapToProductVariantDto)
                        .toList()
        );

        return productResponse;
    }

    public List<ProductResponse> getAllProducts(){
        return productRepository.findAll()
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    public ProductType[] getAllProductTypes(){
        return ProductType.values();
    }
    //Цену надо искать только текущую, которая активна на данный момент

}
