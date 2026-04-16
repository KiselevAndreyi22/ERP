package ru.kiselev.erp.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kiselev.erp.model.admin.Manufacturer;
import ru.kiselev.erp.model.admin.ProductType;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductDto {

    private Long id;

    private String name;

    private ProductType type;

    private Manufacturer manufacturer;

    public ProductDto(Long id, String name, ProductType type, Manufacturer manufacturer, List<ProductVariantDto> variants) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.manufacturer = manufacturer;
    }
}
