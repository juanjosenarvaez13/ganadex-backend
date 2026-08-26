package com.juanjose.ganadex.module.pasture.entity;

import com.juanjose.ganadex.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Potrero de la finca. Corresponde a la tabla {@code potreros}.
 */
@Entity
@Table(name = "potreros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Potrero extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    /** Área del potrero en metros cuadrados. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal area;

    @Column(name = "tipo_pasto", length = 100)
    private String tipoPasto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPotrero estado = EstadoPotrero.DISPONIBLE;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
