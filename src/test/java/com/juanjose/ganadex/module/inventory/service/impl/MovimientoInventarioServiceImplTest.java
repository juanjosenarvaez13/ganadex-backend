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
import com.juanjose.ganadex.module.user.entity.Usuario;
import com.juanjose.ganadex.security.AuthenticatedUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

/**
 * Foco principal: la semántica de {@code aplicarMovimientoAlStock}. ENTRADA y
 * SALIDA son deltas (suman/restan), AJUSTE es una corrección absoluta (fija
 * el stock en el valor exacto de "cantidad") — decisión de negocio confirmada
 * explícitamente en el paso 14.
 */
@ExtendWith(MockitoExtension.class)
class MovimientoInventarioServiceImplTest {

    @Mock
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private MovimientoInventarioMapper movimientoInventarioMapper;

    @InjectMocks
    private MovimientoInventarioServiceImpl movimientoInventarioService;

    private Producto producto(BigDecimal stockActual) {
        return Producto.builder().id(1L).nombre("Sal mineralizada").unidadMedida("kg")
                .stockActual(stockActual).stockMinimo(BigDecimal.TEN).build();
    }

    private Usuario usuario() {
        return Usuario.builder().id(1L).username("jbernal").build();
    }

    private void mockeoBasico(Producto producto, MovimientoInventario entidadMapeada) {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(authenticatedUserProvider.obtenerUsuarioActual()).thenReturn(usuario());
        when(movimientoInventarioMapper.toEntity(any(MovimientoInventarioRequest.class))).thenReturn(entidadMapeada);
        when(movimientoInventarioRepository.save(any(MovimientoInventario.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(movimientoInventarioMapper.toResponse(any(MovimientoInventario.class)))
                .thenReturn(MovimientoInventarioResponse.builder().build());
    }

    @Test
    @DisplayName("ENTRADA debe sumar la cantidad al stock actual")
    void entrada_debeSumarAlStock() {
        Producto producto = producto(BigDecimal.valueOf(10));
        mockeoBasico(producto, new MovimientoInventario());

        MovimientoInventarioRequest request = MovimientoInventarioRequest.builder()
                .productoId(1L).cantidad(BigDecimal.valueOf(5)).tipoMovimiento(TipoMovimiento.ENTRADA).build();

        movimientoInventarioService.registrar(request);

        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(15));
    }

    @Test
    @DisplayName("SALIDA con stock suficiente debe restar la cantidad")
    void salidaConStockSuficiente_debeRestarDelStock() {
        Producto producto = producto(BigDecimal.valueOf(10));
        mockeoBasico(producto, new MovimientoInventario());

        MovimientoInventarioRequest request = MovimientoInventarioRequest.builder()
                .productoId(1L).cantidad(BigDecimal.valueOf(4)).tipoMovimiento(TipoMovimiento.SALIDA).build();

        movimientoInventarioService.registrar(request);

        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(6));
    }

    @Test
    @DisplayName("SALIDA con stock insuficiente debe lanzar BusinessException y no guardar nada")
    void salidaConStockInsuficiente_debeLanzarBusinessException() {
        Producto producto = producto(BigDecimal.valueOf(3));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        MovimientoInventarioRequest request = MovimientoInventarioRequest.builder()
                .productoId(1L).cantidad(BigDecimal.valueOf(10)).tipoMovimiento(TipoMovimiento.SALIDA).build();

        assertThatThrownBy(() -> movimientoInventarioService.registrar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock insuficiente");

        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(3));
        verify0MovimientosGuardados();
    }

    @Test
    @DisplayName("AJUSTE debe fijar el stock exactamente en 'cantidad', sin importar el valor anterior")
    void ajuste_debeFijarStockAbsolutamente() {
        Producto producto = producto(BigDecimal.valueOf(999));
        mockeoBasico(producto, new MovimientoInventario());

        MovimientoInventarioRequest request = MovimientoInventarioRequest.builder()
                .productoId(1L).cantidad(BigDecimal.valueOf(42)).tipoMovimiento(TipoMovimiento.AJUSTE).build();

        movimientoInventarioService.registrar(request);

        assertThat(producto.getStockActual()).isEqualByComparingTo(BigDecimal.valueOf(42));
    }

    @Test
    @DisplayName("con producto inexistente debe lanzar ResourceNotFoundException")
    void productoInexistente_debeLanzarResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        MovimientoInventarioRequest request = MovimientoInventarioRequest.builder()
                .productoId(99L).cantidad(BigDecimal.ONE).tipoMovimiento(TipoMovimiento.ENTRADA).build();

        assertThatThrownBy(() -> movimientoInventarioService.registrar(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("sin fecha explícita, el movimiento debe registrarse con la fecha/hora actual")
    void sinFecha_debeUsarFechaActual() {
        Producto producto = producto(BigDecimal.valueOf(10));
        MovimientoInventario entidad = new MovimientoInventario();
        mockeoBasico(producto, entidad);

        MovimientoInventarioRequest request = MovimientoInventarioRequest.builder()
                .productoId(1L).cantidad(BigDecimal.ONE).tipoMovimiento(TipoMovimiento.ENTRADA).fecha(null).build();

        movimientoInventarioService.registrar(request);

        assertThat(entidad.getFecha()).isNotNull();
    }

    private void verify0MovimientosGuardados() {
        org.mockito.Mockito.verify(movimientoInventarioRepository, never()).save(any());
    }
}
