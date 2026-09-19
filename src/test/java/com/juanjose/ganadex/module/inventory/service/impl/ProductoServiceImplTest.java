package com.juanjose.ganadex.module.inventory.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.inventory.dto.request.ProductoRequest;
import com.juanjose.ganadex.module.inventory.dto.response.ProductoResponse;
import com.juanjose.ganadex.module.inventory.entity.Producto;
import com.juanjose.ganadex.module.inventory.mapper.ProductoMapper;
import com.juanjose.ganadex.module.inventory.repository.MovimientoInventarioRepository;
import com.juanjose.ganadex.module.inventory.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto(Long id, String nombre, BigDecimal stockActual, BigDecimal stockMinimo) {
        return Producto.builder().id(id).nombre(nombre).unidadMedida("kg")
                .stockActual(stockActual).stockMinimo(stockMinimo).build();
    }

    private ProductoResponse response(Long id, String nombre) {
        return ProductoResponse.builder().id(id).nombre(nombre).build();
    }

    @Test
    @DisplayName("listarConStockBajo debe delegar en la query dedicada del repository")
    void listarConStockBajo_debeDelegarEnRepository() {
        Producto sal = producto(1L, "Sal mineralizada", BigDecimal.valueOf(2), BigDecimal.valueOf(10));
        when(productoRepository.findConStockBajoMinimo()).thenReturn(List.of(sal));
        when(productoMapper.toResponse(sal)).thenReturn(response(1L, "Sal mineralizada"));

        List<ProductoResponse> resultado = productoService.listarConStockBajo();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Sal mineralizada");
    }

    @Nested
    @DisplayName("crear")
    class Crear {

        @Test
        @DisplayName("con nombre disponible debe guardar")
        void conNombreDisponible_debeGuardar() {
            ProductoRequest request = ProductoRequest.builder()
                    .nombre("Sal mineralizada").unidadMedida("kg").stockMinimo(BigDecimal.TEN).build();
            Producto nuevo = Producto.builder().nombre("Sal mineralizada").unidadMedida("kg").build();
            Producto guardado = producto(1L, "Sal mineralizada", BigDecimal.ZERO, BigDecimal.TEN);

            when(productoRepository.findByNombre("Sal mineralizada")).thenReturn(Optional.empty());
            when(productoMapper.toEntity(request)).thenReturn(nuevo);
            when(productoRepository.save(nuevo)).thenReturn(guardado);
            when(productoMapper.toResponse(guardado)).thenReturn(response(1L, "Sal mineralizada"));

            ProductoResponse resultado = productoService.crear(request);

            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("con nombre duplicado debe lanzar BusinessException")
        void conNombreDuplicado_debeLanzarBusinessException() {
            ProductoRequest request = ProductoRequest.builder()
                    .nombre("Sal mineralizada").unidadMedida("kg").stockMinimo(BigDecimal.TEN).build();
            when(productoRepository.findByNombre("Sal mineralizada"))
                    .thenReturn(Optional.of(producto(9L, "Sal mineralizada", BigDecimal.ZERO, BigDecimal.TEN)));

            assertThatThrownBy(() -> productoService.crear(request)).isInstanceOf(BusinessException.class);
            verify(productoRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("actualizar no debe tocar el stockActual (el mapper lo ignora explícitamente)")
    void actualizar_noDebeExponerEdicionDirectaDeStock() {
        Producto existente = producto(1L, "Sal mineralizada", BigDecimal.valueOf(15), BigDecimal.TEN);
        ProductoRequest request = ProductoRequest.builder()
                .nombre("Sal mineralizada").unidadMedida("kg").stockMinimo(BigDecimal.valueOf(20)).build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.findByNombre("Sal mineralizada")).thenReturn(Optional.of(existente));
        when(productoRepository.save(existente)).thenReturn(existente);
        when(productoMapper.toResponse(existente)).thenReturn(response(1L, "Sal mineralizada"));

        productoService.actualizar(1L, request);

        // ProductoRequest ni siquiera tiene un campo stockActual: la única
        // forma de que cambie el stock es a través de MovimientoInventario.
        assertThat(existente.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(15));
        verify(productoMapper).updateEntityFromRequest(request, existente);
    }

    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        @Test
        @DisplayName("con movimientos asociados debe lanzar BusinessException")
        void conMovimientosAsociados_debeLanzarBusinessException() {
            Producto existente = producto(1L, "Sal mineralizada", BigDecimal.ZERO, BigDecimal.TEN);
            when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(movimientoInventarioRepository.existsByProductoId(1L)).thenReturn(true);

            assertThatThrownBy(() -> productoService.eliminar(1L)).isInstanceOf(BusinessException.class);
            verify(productoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("sin movimientos asociados debe eliminar")
        void sinMovimientosAsociados_debeEliminar() {
            Producto existente = producto(1L, "Sal mineralizada", BigDecimal.ZERO, BigDecimal.TEN);
            when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(movimientoInventarioRepository.existsByProductoId(1L)).thenReturn(false);

            productoService.eliminar(1L);

            verify(productoRepository).delete(existente);
        }
    }

    @Test
    @DisplayName("obtenerPorId con id inexistente debe lanzar ResourceNotFoundException")
    void obtenerPorId_conIdInexistente_debeLanzarResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
