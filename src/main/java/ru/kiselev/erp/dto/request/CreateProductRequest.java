package ru.kiselev.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kiselev.erp.model.admin.ProductType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Это поле требует обязательного ввода!")
    private String name;

    private ProductType type;

    private Long manufacturerId;

    private List<ProductVariantDto> variants;

}
