package ru.kiselev.erp.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kiselev.erp.model.admin.VariantCost;

public interface VariantCostRepository extends JpaRepository<VariantCost, Long> {
}
