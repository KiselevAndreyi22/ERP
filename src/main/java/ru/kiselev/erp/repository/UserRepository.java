package ru.kiselev.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kiselev.erp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("""
    SELECT u
    FROM User u 
    WHERE u.role != 'ADMIN'
""")
    List<User> findAllWithoutAdmin();

}

