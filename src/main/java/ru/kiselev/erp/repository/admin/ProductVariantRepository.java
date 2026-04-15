package ru.kiselev.erp.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kiselev.erp.model.admin.ProductVariant;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);
}
