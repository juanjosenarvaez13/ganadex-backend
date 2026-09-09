package com.juanjose.ganadex.module.pasture.service;

import com.juanjose.ganadex.module.pasture.dto.request.PotreroCambiarEstadoRequest;
import com.juanjose.ganadex.module.pasture.dto.request.PotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.response.PotreroResponse;

import java.util.List;

public interface PotreroService {

    List<PotreroResponse> listarTodos();

    PotreroResponse obtenerPorId(Long id);

    PotreroResponse crear(PotreroRequest request);

    PotreroResponse actualizar(Long id, PotreroRequest request);

    /**
     * Solo permite transiciones manuales (DISPONIBLE <-> MANTENIMIENTO).
     * OCUPADO lo asigna automáticamente HistorialPotreroService cuando un
     * animal entra al potrero — nunca se establece a mano.
     */
    PotreroResponse cambiarEstado(Long id, PotreroCambiarEstadoRequest request);

    void eliminar(Long id);
}
