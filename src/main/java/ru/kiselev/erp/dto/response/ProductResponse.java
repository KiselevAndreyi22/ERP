package ru.kiselev.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kiselev.erp.dto.request.ProductVariantDto;
import ru.kiselev.erp.model.admin.ProductType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private String name;

    private ProductType type;

    private Long manufacturerId;

    private List<ProductVariantDto> variants;

}
