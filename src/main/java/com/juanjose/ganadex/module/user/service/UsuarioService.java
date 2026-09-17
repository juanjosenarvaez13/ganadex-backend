package com.juanjose.ganadex.module.user.service;

import com.juanjose.ganadex.module.user.dto.request.CambiarPasswordRequest;
import com.juanjose.ganadex.module.user.dto.request.UsuarioCreateRequest;
import com.juanjose.ganadex.module.user.dto.request.UsuarioUpdateRequest;
import com.juanjose.ganadex.module.user.dto.response.UsuarioResponse;

import java.util.List;

/**
 * No existe "eliminar". Un usuario nunca se borra físicamente (tiene
 * animales y movimientos de inventario asociados vía FK) — se desactiva con
 * {@link #desactivar}, preservando toda su trazabilidad histórica.
 */
public interface UsuarioService {

    List<UsuarioResponse> listar();

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse crear(UsuarioCreateRequest request);

    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);

    void cambiarPassword(Long id, CambiarPasswordRequest request);

    UsuarioResponse desactivar(Long id);

    UsuarioResponse reactivar(Long id);
}
