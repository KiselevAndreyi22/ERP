package ru.kiselev.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kiselev.erp.model.admin.ProductType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEditDto {
    private Long id;
    private String name;
    private ProductType type;
    private Long manufacturerId;
    private List<ProductVariantEditDto> productVariants;
}
