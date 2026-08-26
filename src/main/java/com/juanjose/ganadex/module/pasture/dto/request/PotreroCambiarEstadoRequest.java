package com.juanjose.ganadex.module.pasture.dto.request;

import com.juanjose.ganadex.module.pasture.entity.EstadoPotrero;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para el endpoint dedicado de cambio de estado de un potrero.
 * Igual que en Animal: el estado no se edita en el PUT general porque
 * OCUPADO/DISPONIBLE en la práctica lo va a derivar el Service a partir de
 * si hay o no un registro abierto en historial_potreros; este endpoint
 * queda para el caso manual (ej. marcar MANTENIMIENTO).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PotreroCambiarEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio.")
    private EstadoPotrero nuevoEstado;
}
