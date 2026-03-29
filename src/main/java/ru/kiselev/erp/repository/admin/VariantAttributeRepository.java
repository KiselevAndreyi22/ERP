package ru.kiselev.erp.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kiselev.erp.model.admin.VariantAttribute;

public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {
}
