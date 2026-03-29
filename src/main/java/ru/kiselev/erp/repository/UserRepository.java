package ru.kiselev.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kiselev.erp.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    void deleteUserById(Long id);
}

