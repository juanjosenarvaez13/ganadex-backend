package com.juanjose.ganadex.module.inventory.dto.request;

import com.juanjose.ganadex.module.inventory.entity.TipoMovimiento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventarioRequest {

    @NotNull(message = "El producto es obligatorio.")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria.")
    @Positive(message = "La cantidad debe ser mayor a cero.")
    private BigDecimal cantidad;

    @NotNull(message = "El tipo de movimiento es obligatorio.")
    private TipoMovimiento tipoMovimiento;

    // Opcional: si no se envía, el Service la asigna como LocalDateTime.now().
    @PastOrPresent(message = "La fecha del movimiento no puede ser futura.")
    private LocalDateTime fecha;

    @Size(max = 255)
    private String motivo;

    private String observaciones;

    // TODO (paso 9 - JWT): se reemplaza por el usuario del SecurityContext.
    @NotNull(message = "El usuario es obligatorio.")
    private Long usuarioId;
}
