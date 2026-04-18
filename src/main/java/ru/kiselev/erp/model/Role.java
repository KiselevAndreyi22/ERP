package ru.kiselev.erp.model;

import ru.kiselev.erp.model.admin.ProductType;

public enum Role {
    USER("Пользователь"),
    FINANC("Финансист"),
    MANAGER("Менеджер"),
    ADMIN("Администратор");

    private final String rusName;

    Role(String rusName) {
        this.rusName = rusName;
    }

    public String getRusName() {
        return rusName;
    }

    @Override
    public String toString() {
        return rusName;
    }

    public static Role fromRusName(String rusName) {
        for (Role role : values()) {
            if (role.rusName.equalsIgnoreCase(rusName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + rusName);
    }

    public ProductType convert(String source) {
        return ProductType.fromRusName(source);
    }
}
