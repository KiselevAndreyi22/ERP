package ru.kiselev.erp.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDto {

    private Long id;

    private String sku;

    private List<VariantAttributeDto> variantAttributes;

    private List<VariantCostDto> variantCosts;
}
