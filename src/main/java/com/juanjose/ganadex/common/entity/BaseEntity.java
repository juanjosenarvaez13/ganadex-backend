package com.juanjose.ganadex.common.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;

/**
 * Superclase para todas las entidades de Ganadex.
 * <p>
 * Centraliza el id autogenerado y una implementación de {@code equals}/
 * {@code hashCode} basada exclusivamente en el id, siguiendo la práctica
 * recomendada para entidades JPA (Vlad Mihalcea): comparar por id evita los
 * problemas clásicos de comparar todos los campos (rompe con proxies de
 * Hibernate, y con {@code @Data} de Lombok en especial). Dos entidades son
 * iguales solo si ambas están persistidas (id no nulo) y tienen el mismo id.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseEntity other)) {
            return false;
        }
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
