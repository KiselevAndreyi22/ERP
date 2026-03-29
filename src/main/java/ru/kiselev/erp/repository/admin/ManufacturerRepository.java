package ru.kiselev.erp.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kiselev.erp.model.admin.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    boolean existsByCode(String code);

}
