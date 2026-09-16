package com.juanjose.ganadex.module.inventory.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.inventory.dto.request.ProductoRequest;
import com.juanjose.ganadex.module.inventory.dto.response.ProductoResponse;
import com.juanjose.ganadex.module.inventory.entity.Producto;
import com.juanjose.ganadex.module.inventory.mapper.ProductoMapper;
import com.juanjose.ganadex.module.inventory.repository.MovimientoInventarioRepository;
import com.juanjose.ganadex.module.inventory.repository.ProductoRepository;
import com.juanjose.ganadex.module.inventory.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarConStockBajo() {
        return productoRepository.findConStockBajoMinimo().stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        return productoMapper.toResponse(buscarPorIdOLanzar(id));
    }

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        validarNombreDisponible(request.getNombre(), null);

        Producto producto = productoMapper.toEntity(request);
        Producto guardado = productoRepository.save(producto);

        log.info("Producto creado: id={}, nombre={}", guardado.getId(), guardado.getNombre());
        return productoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarPorIdOLanzar(id);
        validarNombreDisponible(request.getNombre(), id);

        // stockActual no se toca: el mapper lo ignora explícitamente
        // (ver paso 13), esta edición nunca lo modifica.
        productoMapper.updateEntityFromRequest(request, producto);
        Producto actualizado = productoRepository.save(producto);

        log.info("Producto actualizado: id={}", actualizado.getId());
        return productoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarPorIdOLanzar(id);

        if (movimientoInventarioRepository.existsByProductoId(id)) {
            throw new BusinessException(
                    "No se puede eliminar el producto '%s' porque tiene movimientos de inventario asociados."
                            .formatted(producto.getNombre()));
        }

        productoRepository.delete(producto);
        log.info("Producto eliminado: id={}", id);
    }

    private Producto buscarPorIdOLanzar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Producto", id));
    }

    private void validarNombreDisponible(String nombre, Long excludeId) {
        productoRepository.findByNombre(nombre).ifPresent(existente -> {
            if (!existente.getId().equals(excludeId)) {
                throw new BusinessException(
                        "Ya existe un producto registrado con el nombre '%s'.".formatted(nombre));
            }
        });
    }
}
