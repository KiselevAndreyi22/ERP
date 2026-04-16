package ru.kiselev.erp.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kiselev.erp.model.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {

    String username;

    String password;

    Role role;
}
