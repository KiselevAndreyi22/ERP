package ru.kiselev.erp.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantAttributeDto {

    private Long id;

    private String name;

    private String value;
}
