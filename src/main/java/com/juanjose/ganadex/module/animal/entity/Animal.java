package com.juanjose.ganadex.module.animal.entity;

import com.juanjose.ganadex.common.entity.BaseEntity;
import com.juanjose.ganadex.module.breed.entity.Raza;
import com.juanjose.ganadex.module.user.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Animal de la finca. Corresponde a la tabla {@code animales}.
 * <p>
 * No se elimina físicamente: el ciclo de vida se controla con
 * {@link EstadoAnimal}. El potrero actual del animal NO vive aquí — se
 * deriva consultando el registro abierto (fecha_salida IS NULL) en
 * {@code historial_potreros}.
 */
@Entity
@Table(name = "animales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Animal extends BaseEntity {

    @Column(name = "numero_arete", nullable = false, unique = true, length = 50)
    private String numeroArete;

    @Column(length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SexoAnimal sexo;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "peso_actual", precision = 6, scale = 2)
    private BigDecimal pesoActual;

    @Column(length = 50)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoAnimal estado = EstadoAnimal.ACTIVO;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(length = 255)
    private String foto;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raza_id", nullable = false)
    private Raza raza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
