package ru.kiselev.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kiselev.erp.model.admin.Manufacturer;
import ru.kiselev.erp.model.admin.ProductType;

@Data
@AllArgsConstructor
public class ProductDto {

    private String name;

    private ProductType type;

    private Manufacturer manufacturer;
}
