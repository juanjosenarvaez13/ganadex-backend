package com.juanjose.ganadex.module.dashboard.service.impl;

import com.juanjose.ganadex.module.animal.entity.EstadoAnimal;
import com.juanjose.ganadex.module.animal.entity.SexoAnimal;
import com.juanjose.ganadex.module.animal.repository.AnimalRepository;
import com.juanjose.ganadex.module.dashboard.dto.response.DashboardResponse;
import com.juanjose.ganadex.module.inventory.entity.Producto;
import com.juanjose.ganadex.module.inventory.repository.ProductoRepository;
import com.juanjose.ganadex.module.pasture.entity.EstadoPotrero;
import com.juanjose.ganadex.module.pasture.repository.PotreroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * DashboardServiceImpl no tiene reglas de negocio propias: solo agrega
 * conteos que ya viven en Animal, Potrero y Producto. Esta prueba verifica
 * que combine correctamente esos números, no que los calcule.
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private PotreroRepository potreroRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    @DisplayName("obtenerResumen debe combinar todos los conteos de los tres repositorios")
    void obtenerResumen_debeCombinarTodosLosConteos() {
        when(animalRepository.countByEstado(EstadoAnimal.ACTIVO)).thenReturn(12L);
        when(animalRepository.countByEstadoAndSexo(EstadoAnimal.ACTIVO, SexoAnimal.MACHO)).thenReturn(5L);
        when(animalRepository.countByEstadoAndSexo(EstadoAnimal.ACTIVO, SexoAnimal.HEMBRA)).thenReturn(7L);

        when(potreroRepository.count()).thenReturn(4L);
        when(potreroRepository.countByEstado(EstadoPotrero.DISPONIBLE)).thenReturn(2L);
        when(potreroRepository.countByEstado(EstadoPotrero.OCUPADO)).thenReturn(2L);
        when(potreroRepository.countByEstado(EstadoPotrero.MANTENIMIENTO)).thenReturn(0L);

        Producto sal = Producto.builder().id(1L).nombre("Sal mineralizada").unidadMedida("kg")
                .stockActual(BigDecimal.valueOf(2)).stockMinimo(BigDecimal.TEN).build();
        when(productoRepository.count()).thenReturn(3L);
        when(productoRepository.findConStockBajoMinimo()).thenReturn(List.of(sal));

        DashboardResponse resultado = dashboardService.obtenerResumen();

        assertThat(resultado.totalAnimalesActivos()).isEqualTo(12L);
        assertThat(resultado.totalMachosActivos()).isEqualTo(5L);
        assertThat(resultado.totalHembrasActivas()).isEqualTo(7L);
        assertThat(resultado.totalPotreros()).isEqualTo(4L);
        assertThat(resultado.potrerosDisponibles()).isEqualTo(2L);
        assertThat(resultado.potrerosOcupados()).isEqualTo(2L);
        assertThat(resultado.potrerosEnMantenimiento()).isEqualTo(0L);
        assertThat(resultado.totalProductos()).isEqualTo(3L);
        assertThat(resultado.productosConStockBajo()).hasSize(1);
        assertThat(resultado.productosConStockBajo().get(0).nombre()).isEqualTo("Sal mineralizada");
    }

    @Test
    @DisplayName("sin productos en alerta, la lista de stock bajo debe quedar vacía")
    void sinProductosEnAlerta_listaDebeQuedarVacia() {
        when(animalRepository.countByEstado(EstadoAnimal.ACTIVO)).thenReturn(0L);
        when(animalRepository.countByEstadoAndSexo(EstadoAnimal.ACTIVO, SexoAnimal.MACHO)).thenReturn(0L);
        when(animalRepository.countByEstadoAndSexo(EstadoAnimal.ACTIVO, SexoAnimal.HEMBRA)).thenReturn(0L);
        when(potreroRepository.count()).thenReturn(0L);
        when(potreroRepository.countByEstado(EstadoPotrero.DISPONIBLE)).thenReturn(0L);
        when(potreroRepository.countByEstado(EstadoPotrero.OCUPADO)).thenReturn(0L);
        when(potreroRepository.countByEstado(EstadoPotrero.MANTENIMIENTO)).thenReturn(0L);
        when(productoRepository.count()).thenReturn(0L);
        when(productoRepository.findConStockBajoMinimo()).thenReturn(List.of());

        DashboardResponse resultado = dashboardService.obtenerResumen();

        assertThat(resultado.productosConStockBajo()).isEmpty();
    }
}
