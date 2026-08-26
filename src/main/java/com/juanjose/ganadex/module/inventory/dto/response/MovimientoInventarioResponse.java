package com.juanjose.ganadex.module.inventory.dto.response;

import com.juanjose.ganadex.module.inventory.entity.TipoMovimiento;
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
public class MovimientoInventarioResponse {

    private Long id;

    private Long productoId;
    private String productoNombre;

    private Long usuarioId;
    private String usuarioNombre;

    private BigDecimal cantidad;
    private TipoMovimiento tipoMovimiento;
    private LocalDateTime fecha;
    private String motivo;
    private String observaciones;
}
