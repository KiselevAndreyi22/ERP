package ru.kiselev.erp.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kiselev.erp.model.admin.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
}
