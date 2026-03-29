package ru.kiselev.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kiselev.erp.model.Role;
import ru.kiselev.erp.model.User;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String username;

    private Role role;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}
