package com.juanjose.ganadex.module.pasture.service;

import com.juanjose.ganadex.module.pasture.dto.request.RegistrarEntradaPotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.request.RegistrarSalidaPotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.response.HistorialPotreroResponse;

import java.util.List;

public interface HistorialPotreroService {

    /** Abre un registro de ocupación. Falla si el animal ya está en otro potrero. */
    HistorialPotreroResponse registrarEntrada(RegistrarEntradaPotreroRequest request);

    /** Cierra el registro abierto del animal. Falla si no tiene ninguno abierto. */
    HistorialPotreroResponse registrarSalida(Long animalId, RegistrarSalidaPotreroRequest request);

    /** El potrero donde está el animal ahora mismo. Falla (404) si no está en ninguno. */
    HistorialPotreroResponse potreroActualDeAnimal(Long animalId);

    List<HistorialPotreroResponse> historialDeAnimal(Long animalId);

    List<HistorialPotreroResponse> historialDePotrero(Long potreroId);
}
