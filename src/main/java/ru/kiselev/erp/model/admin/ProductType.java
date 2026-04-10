package ru.kiselev.erp.model.admin;

public enum ProductType {
    SOFA("Диваны"),
    TABLE("Столы"),
    CHAIR("Стулья");

    private final String rusName;

    ProductType(String rusName) {
        this.rusName = rusName;
    }

    public String getRusName() {
        return rusName;
    }

    @Override
    public String toString() {
        return rusName;
    }

    public static ProductType fromRusName(String rusName) {
        for (ProductType type : values()) {
            if (type.rusName.equalsIgnoreCase(rusName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + rusName);
    }

    public ProductType convert(String source) {
        return ProductType.fromRusName(source);
    }
}
