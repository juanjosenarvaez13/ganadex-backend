package com.juanjose.ganadex.module.pasture.entity;

import com.juanjose.ganadex.common.entity.BaseEntity;
import com.juanjose.ganadex.module.animal.entity.Animal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Historial completo de ocupación de potreros por animal. Corresponde a la
 * tabla {@code historial_potreros}.
 * <p>
 * Un registro con {@code fechaSalida == null} representa la ocupación
 * "abierta" actual del animal. La base de datos garantiza, vía el índice
 * único parcial {@code ux_historial_potreros_animal_abierto}, que un animal
 * no puede tener dos registros abiertos simultáneamente — esa regla de
 * negocio no depende únicamente del Service.
 */
@Entity
@Table(name = "historial_potreros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class HistorialPotrero extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "potrero_id", nullable = false)
    private Potrero potrero;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDateTime fechaEntrada;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
