package ru.kiselev.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kiselev.erp.model.admin.Manufacturer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManufacturerDto {

    private Long id;

    @NotBlank(message = "Это поле требует обязательного ввода!")
    private String name;

    @NotBlank(message = "Это поле требует обязательного ввода!")
    @Pattern(
            regexp = "^(\\d+|[A-Z]{3}-\\d+|GRP\\-d\\d+)$",
            message = "Ошибка: код должен быть в формате: 12345, SUP-12345, MSK-12345 или GRP1-12345!"
    )
    private String code;

    public ManufacturerDto(Manufacturer manufacturer) {
        this.id = manufacturer.getId();
        this.name = manufacturer.getName();
        this.code = manufacturer.getCode();
    }

}
