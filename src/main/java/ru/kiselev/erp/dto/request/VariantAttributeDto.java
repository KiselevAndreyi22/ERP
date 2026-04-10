package ru.kiselev.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantAttributeDto {

    private String name;

    private String value;
}
