package com.juanjose.ganadex.module.breed.service;

import com.juanjose.ganadex.module.breed.dto.request.RazaRequest;
import com.juanjose.ganadex.module.breed.dto.response.RazaResponse;

import java.util.List;

public interface RazaService {

    List<RazaResponse> listarTodas();

    RazaResponse obtenerPorId(Long id);

    RazaResponse crear(RazaRequest request);

    RazaResponse actualizar(Long id, RazaRequest request);

    void eliminar(Long id);
}
