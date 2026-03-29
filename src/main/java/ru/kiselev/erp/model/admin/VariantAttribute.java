package ru.kiselev.erp.model.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table (name = "variant_attributes",
        uniqueConstraints =
                {@UniqueConstraint(columnNames = {"variant_id", "name"}) }
)
public class VariantAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "variant_id")
    private ProductVariant productVariant;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Attribute name;

    @Column(name = "value")
    private String value;

}
