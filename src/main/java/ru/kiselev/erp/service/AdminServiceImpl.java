package ru.kiselev.erp.service;

import ru.kiselev.erp.dto.request.admin.*;
import ru.kiselev.erp.dto.response.ProductResponse;
import ru.kiselev.erp.model.admin.Product;
import ru.kiselev.erp.model.admin.ProductType;
import ru.kiselev.erp.model.admin.ProductVariant;

import java.util.List;

public interface AdminServiceImpl {

    void register(RegisterUserRequest registerUserRequest);

    void deleteUserById(Long id);

    List<ManufacturerDto> getAllManufacturers();

    ManufacturerDto addManufacturer(ManufacturerDto manufacturerDto);

    void deleteManufacturerById(Long id);

    Product createProduct(CreateProductRequest request);

    void deleteProductById(Long id);

    ProductVariantDto mapToProductVariantDto(ProductVariant productVariant);

    ProductResponse mapToProductResponse(Product product);

    List<ProductResponse> getAllProducts();

    ProductType[] getAllProductTypes();

    void updateProduct(Long productId, UpdateProductRequest request);

    void deleteProductVariant(Long variantId);

    List<ProductEditDto> getProductsForEdit();

}
