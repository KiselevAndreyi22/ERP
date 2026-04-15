package ru.kiselev.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantCostEditDto {
    private Long id;
    private double basePrice;
    private LocalDate validFrom;
    private LocalDate validTo;
}