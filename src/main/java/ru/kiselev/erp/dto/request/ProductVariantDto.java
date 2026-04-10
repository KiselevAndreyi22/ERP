package ru.kiselev.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantDto {

    @NotBlank(message = "Это поле требует обязательного ввода!")
    private String sku;

    private List<VariantAttributeDto> attributes;

    private List<VariantCostDto> costs;
}
