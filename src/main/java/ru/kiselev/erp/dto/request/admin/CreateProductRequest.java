package ru.kiselev.erp.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kiselev.erp.model.admin.ProductType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {

    private String name;

    private ProductType type;

    private Long manufacturerId;

    private List<ProductVariantDto> variants;

}
