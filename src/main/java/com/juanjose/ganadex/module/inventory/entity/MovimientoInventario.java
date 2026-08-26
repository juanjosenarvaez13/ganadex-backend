package com.juanjose.ganadex.module.inventory.entity;

import com.juanjose.ganadex.common.entity.BaseEntity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Movimiento de entrada, salida o ajuste de inventario. Corresponde a la
 * tabla {@code movimientos_inventario}.
 * <p>
 * {@code fecha} es un dato de negocio (cuándo ocurrió el movimiento), no una
 * marca de auditoría automática: el Service es responsable de asignarla
 * (por defecto {@code LocalDateTime.now()}, salvo que se registre un
 * movimiento retroactivo).
 */
@Entity
@Table(name = "movimientos_inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovimientoInventario extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 10)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 255)
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
