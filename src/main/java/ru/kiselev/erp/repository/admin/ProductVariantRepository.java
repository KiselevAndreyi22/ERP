package ru.kiselev.erp.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kiselev.erp.model.admin.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
}
