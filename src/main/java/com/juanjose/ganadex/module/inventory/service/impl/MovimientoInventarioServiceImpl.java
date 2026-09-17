package com.juanjose.ganadex.module.inventory.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.inventory.dto.request.MovimientoInventarioRequest;
import com.juanjose.ganadex.module.inventory.dto.response.MovimientoInventarioResponse;
import com.juanjose.ganadex.module.inventory.entity.MovimientoInventario;
import com.juanjose.ganadex.module.inventory.entity.Producto;
import com.juanjose.ganadex.module.inventory.entity.TipoMovimiento;
import com.juanjose.ganadex.module.inventory.mapper.MovimientoInventarioMapper;
import com.juanjose.ganadex.module.inventory.repository.MovimientoInventarioRepository;
import com.juanjose.ganadex.module.inventory.repository.ProductoRepository;
import com.juanjose.ganadex.module.inventory.service.MovimientoInventarioService;
import com.juanjose.ganadex.module.user.entity.Usuario;
import com.juanjose.ganadex.security.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ProductoRepository productoRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final MovimientoInventarioMapper movimientoInventarioMapper;

    @Override
    @Transactional
    public MovimientoInventarioResponse registrar(MovimientoInventarioRequest request) {
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> ResourceNotFoundException.of("Producto", request.getProductoId()));
        Usuario usuario = authenticatedUserProvider.obtenerUsuarioActual();

        aplicarMovimientoAlStock(producto, request.getTipoMovimiento(), request.getCantidad());
        productoRepository.save(producto);

        MovimientoInventario movimiento = movimientoInventarioMapper.toEntity(request);
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setFecha(request.getFecha() != null ? request.getFecha() : LocalDateTime.now());

        MovimientoInventario guardado = movimientoInventarioRepository.save(movimiento);

        log.info("Movimiento registrado: producto={}, tipo={}, cantidad={}, stockResultante={}",
                producto.getNombre(), request.getTipoMovimiento(), request.getCantidad(), producto.getStockActual());
        return movimientoInventarioMapper.toResponse(guardado);
    }

    /**
     * ENTRADA suma, SALIDA resta (validando que haya suficiente stock antes
     * de restar), AJUSTE fija el stock exactamente en "cantidad" — es una
     * corrección absoluta, no un delta (ver decisión del paso 14).
     */
    private void aplicarMovimientoAlStock(Producto producto, TipoMovimiento tipo, BigDecimal cantidad) {
        switch (tipo) {
            case ENTRADA -> producto.setStockActual(producto.getStockActual().add(cantidad));
            case SALIDA -> {
                if (producto.getStockActual().compareTo(cantidad) < 0) {
                    throw new BusinessException(
                            "Stock insuficiente para '%s': disponible %s %s, se intentó retirar %s."
                                    .formatted(producto.getNombre(), producto.getStockActual(),
                                            producto.getUnidadMedida(), cantidad));
                }
                producto.setStockActual(producto.getStockActual().subtract(cantidad));
            }
            case AJUSTE -> producto.setStockActual(cantidad);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponse> listarPorProducto(Long productoId, Pageable pageable) {
        return movimientoInventarioRepository.findByProductoIdOrderByFechaDesc(productoId, pageable)
                .map(movimientoInventarioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoInventarioResponse> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return movimientoInventarioRepository.findByUsuarioIdOrderByFechaDesc(usuarioId, pageable)
                .map(movimientoInventarioMapper::toResponse);
    }
}
