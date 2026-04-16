package ru.kiselev.erp.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kiselev.erp.dto.request.admin.*;
import ru.kiselev.erp.dto.response.ProductResponse;
import ru.kiselev.erp.dto.response.UserDto;
import ru.kiselev.erp.exception.*;
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

    public void register(RegisterUserRequest registerUserRequest) {

        User user = new User();

        if(userRepository.findByUsername(registerUserRequest.getUsername()).isPresent()){
            throw new UsernameAlreadyExistException();
        }

        user.setUsername(registerUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setRole(registerUserRequest.getRole());

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

                        if(dto.getValidTo().isBefore(dto.getValidFrom())) {
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

    @Transactional
    public void updateProduct(Long productId, UpdateProductRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getName() != null && !request.getName().equals(product.getName())) {
            if (productRepository.existsByNameAndIdNot(request.getName(), product.getId())) {
                throw new ProductAlreadyExistsByNameException();
            }
            product.setName(request.getName());
        }

        if (request.getType() != null) {
            product.setType(request.getType());
        }

        if (request.getManufacturerId() != null) {
            Manufacturer manufacturer = manufacturerRepository.findById(request.getManufacturerId())
                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
            product.setManufacturer(manufacturer);
        }

        if (request.getVariants() != null) {
            for (ProductVariantDto variantDto : request.getVariants()) {

                ProductVariant variant;

                if (variantDto.getId() != null) {
                    variant = product.getProductVariants().stream()
                            .filter(v -> v.getId().equals(variantDto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Variant not found"));

                    if (variantDto.getSku() != null && !variantDto.getSku().equals(variant.getSku())) {
                        if (productVariantRepository.existsBySkuAndIdNot(variantDto.getSku(), variant.getId())) {
                            throw new VariantSkuAlreadyExistException();
                        }
                        variant.setSku(variantDto.getSku());
                    }

                } else {
                    if (productVariantRepository.existsBySku(variantDto.getSku())) {
                        throw new VariantSkuAlreadyExistException();
                    }

                    variant = new ProductVariant();
                    variant.setSku(variantDto.getSku());
                    variant.setProduct(product);

                    product.getProductVariants().add(variant);
                }

                // --- ATTRIBUTES ---
                if (variantDto.getVariantAttributes() != null) {
                    for (VariantAttributeDto attrDto : variantDto.getVariantAttributes()) {

                        VariantAttribute attribute;

                        if (attrDto.getId() != null) {
                            attribute = variant.getVariantAttributes().stream()
                                    .filter(a -> a.getId().equals(attrDto.getId()))
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("Attribute not found"));

                            if (attrDto.getName() != null) {
                                attribute.setName(attrDto.getName());
                            }
                            if (attrDto.getValue() != null) {
                                attribute.setValue(attrDto.getValue());
                            }

                        } else {
                            attribute = new VariantAttribute();
                            attribute.setName(attrDto.getName());
                            attribute.setValue(attrDto.getValue());
                            attribute.setProductVariant(variant);

                            variant.getVariantAttributes().add(attribute);
                        }
                    }
                }

                // --- COSTS ---
                if (variantDto.getVariantCosts() != null) {
                    for (VariantCostDto costDto : variantDto.getVariantCosts()) {

                        VariantCost cost;

                        if (costDto.getId() != null) {
                            cost = variant.getVariantCosts().stream()
                                    .filter(c -> c.getId().equals(costDto.getId()))
                                    .findFirst()
                                    .orElseThrow(() -> new RuntimeException("Cost not found"));

                        } else {
                            cost = new VariantCost();
                            cost.setProductVariant(variant);
                            variant.getVariantCosts().add(cost);
                        }

                        if (costDto.getBasePrice() <= 0) {
                            throw new InvalidCostValueException();
                        }

                        if (costDto.getValidFrom().isBefore(LocalDate.now())) {
                            throw new InvalidDateRangeException();
                        }

                        if (costDto.getValidTo().isBefore(costDto.getValidFrom())) {
                            throw new InvalidDateRangeException();
                        }

                        cost.setBasePrice(costDto.getBasePrice());
                        cost.setValidFrom(costDto.getValidFrom());
                        cost.setValidTo(costDto.getValidTo());
                    }
                }
            }
        }

        productRepository.save(product);
    }

    public void deleteProductVariant(Long variantId) {

        if(!productVariantRepository.existsById(variantId)) {
            throw new RuntimeException();
        }

        productVariantRepository.deleteById(variantId);
    }

    public List<ProductEditDto> getProductsForEdit() {
        return productRepository.findAll().stream()
                .map(product -> new ProductEditDto(
                        product.getId(),
                        product.getName(),
                        product.getType(),
                        product.getManufacturer() != null ? product.getManufacturer().getId() : null,
                        product.getProductVariants().stream()
                                .map(variant -> new ProductVariantEditDto(
                                        variant.getId(),
                                        variant.getSku(),
                                        variant.getVariantAttributes().stream()
                                                .map(attr -> new VariantAttributeEditDto(
                                                        attr.getId(),
                                                        attr.getName(),
                                                        attr.getValue()
                                                ))
                                                .toList(),
                                        variant.getVariantCosts().stream()
                                                .map(cost -> new VariantCostEditDto(
                                                        cost.getId(),
                                                        cost.getBasePrice(),
                                                        cost.getValidFrom(),
                                                        cost.getValidTo()
                                                ))
                                                .toList()
                                ))
                                .toList()
                ))
                .toList();
    }
    //Цену надо искать только текущую, которая активна на данный момент


}
