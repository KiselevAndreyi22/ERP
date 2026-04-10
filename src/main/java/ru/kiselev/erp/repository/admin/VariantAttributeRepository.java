package ru.kiselev.erp.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kiselev.erp.model.admin.VariantAttribute;

import java.util.List;

public interface VariantAttributeRepository extends JpaRepository<VariantAttribute, Long> {

    List<VariantAttribute> findAllById(Long id);
}
