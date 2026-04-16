package ru.kiselev.erp.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantEditDto {
    private Long id;
    private String sku;
    private List<VariantAttributeEditDto> variantAttributes;
    private List<VariantCostEditDto> variantCosts;
}