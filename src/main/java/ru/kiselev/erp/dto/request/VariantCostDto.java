package ru.kiselev.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantCostDto {

    private double basePrice;

    @NotNull
    private LocalDate validFrom;

    @NotNull
    private LocalDate validTo;
}
