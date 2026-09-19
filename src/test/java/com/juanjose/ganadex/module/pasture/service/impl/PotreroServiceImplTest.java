package com.juanjose.ganadex.module.pasture.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.pasture.dto.request.PotreroCambiarEstadoRequest;
import com.juanjose.ganadex.module.pasture.dto.request.PotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.response.PotreroResponse;
import com.juanjose.ganadex.module.pasture.entity.EstadoPotrero;
import com.juanjose.ganadex.module.pasture.entity.HistorialPotrero;
import com.juanjose.ganadex.module.pasture.entity.Potrero;
import com.juanjose.ganadex.module.pasture.mapper.PotreroMapper;
import com.juanjose.ganadex.module.pasture.repository.HistorialPotreroRepository;
import com.juanjose.ganadex.module.pasture.repository.PotreroRepository;
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
class PotreroServiceImplTest {

    @Mock
    private PotreroRepository potreroRepository;

    @Mock
    private HistorialPotreroRepository historialPotreroRepository;

    @Mock
    private PotreroMapper potreroMapper;

    @InjectMocks
    private PotreroServiceImpl potreroService;

    private Potrero potrero(Long id, String nombre, EstadoPotrero estado) {
        return Potrero.builder().id(id).nombre(nombre).area(BigDecimal.valueOf(5000)).estado(estado).build();
    }

    private PotreroResponse response(Long id, String nombre, EstadoPotrero estado) {
        return PotreroResponse.builder().id(id).nombre(nombre).estado(estado).build();
    }

    @Nested
    @DisplayName("crear")
    class Crear {

        @Test
        @DisplayName("con nombre disponible debe guardar")
        void conNombreDisponible_debeGuardar() {
            PotreroRequest request = PotreroRequest.builder().nombre("Potrero 1").area(BigDecimal.valueOf(5000)).build();
            Potrero nuevo = Potrero.builder().nombre("Potrero 1").area(BigDecimal.valueOf(5000)).build();
            Potrero guardado = potrero(1L, "Potrero 1", EstadoPotrero.DISPONIBLE);

            when(potreroRepository.findByNombre("Potrero 1")).thenReturn(Optional.empty());
            when(potreroMapper.toEntity(request)).thenReturn(nuevo);
            when(potreroRepository.save(nuevo)).thenReturn(guardado);
            when(potreroMapper.toResponse(guardado)).thenReturn(response(1L, "Potrero 1", EstadoPotrero.DISPONIBLE));

            PotreroResponse resultado = potreroService.crear(request);

            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("con nombre duplicado debe lanzar BusinessException")
        void conNombreDuplicado_debeLanzarBusinessException() {
            PotreroRequest request = PotreroRequest.builder().nombre("Potrero 1").area(BigDecimal.TEN).build();
            when(potreroRepository.findByNombre("Potrero 1"))
                    .thenReturn(Optional.of(potrero(9L, "Potrero 1", EstadoPotrero.DISPONIBLE)));

            assertThatThrownBy(() -> potreroService.crear(request)).isInstanceOf(BusinessException.class);
            verify(potreroRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("cambiarEstado")
    class CambiarEstado {

        @Test
        @DisplayName("intentar poner OCUPADO manualmente debe lanzar BusinessException")
        void aOcupadoManualmente_debeLanzarBusinessException() {
            Potrero existente = potrero(1L, "Potrero 1", EstadoPotrero.DISPONIBLE);
            PotreroCambiarEstadoRequest request =
                    PotreroCambiarEstadoRequest.builder().nuevoEstado(EstadoPotrero.OCUPADO).build();

            when(potreroRepository.findById(1L)).thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> potreroService.cambiarEstado(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("automáticamente");

            verify(potreroRepository, never()).save(any());
        }

        @Test
        @DisplayName("con animales actualmente en el potrero debe lanzar BusinessException")
        void conAnimalesActuales_debeLanzarBusinessException() {
            Potrero existente = potrero(1L, "Potrero 1", EstadoPotrero.OCUPADO);
            PotreroCambiarEstadoRequest request =
                    PotreroCambiarEstadoRequest.builder().nuevoEstado(EstadoPotrero.MANTENIMIENTO).build();

            when(potreroRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(historialPotreroRepository.findByPotreroIdAndFechaSalidaIsNull(1L))
                    .thenReturn(List.of(new HistorialPotrero()));

            assertThatThrownBy(() -> potreroService.cambiarEstado(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("tiene animales");

            verify(potreroRepository, never()).save(any());
        }

        @Test
        @DisplayName("sin animales actuales debe permitir pasar a MANTENIMIENTO")
        void sinAnimalesActuales_debePermitirMantenimiento() {
            Potrero existente = potrero(1L, "Potrero 1", EstadoPotrero.DISPONIBLE);
            PotreroCambiarEstadoRequest request =
                    PotreroCambiarEstadoRequest.builder().nuevoEstado(EstadoPotrero.MANTENIMIENTO).build();

            when(potreroRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(historialPotreroRepository.findByPotreroIdAndFechaSalidaIsNull(1L)).thenReturn(List.of());
            when(potreroRepository.save(existente)).thenReturn(existente);
            when(potreroMapper.toResponse(existente)).thenReturn(response(1L, "Potrero 1", EstadoPotrero.MANTENIMIENTO));

            PotreroResponse resultado = potreroService.cambiarEstado(1L, request);

            assertThat(resultado.getEstado()).isEqualTo(EstadoPotrero.MANTENIMIENTO);
        }
    }

    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        @Test
        @DisplayName("sin historial asociado debe eliminar")
        void sinHistorial_debeEliminar() {
            Potrero existente = potrero(1L, "Potrero 1", EstadoPotrero.DISPONIBLE);
            when(potreroRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(historialPotreroRepository.existsByPotreroId(1L)).thenReturn(false);

            potreroService.eliminar(1L);

            verify(potreroRepository).delete(existente);
        }

        @Test
        @DisplayName("con historial asociado debe lanzar BusinessException")
        void conHistorial_debeLanzarBusinessException() {
            Potrero existente = potrero(1L, "Potrero 1", EstadoPotrero.DISPONIBLE);
            when(potreroRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(historialPotreroRepository.existsByPotreroId(1L)).thenReturn(true);

            assertThatThrownBy(() -> potreroService.eliminar(1L)).isInstanceOf(BusinessException.class);
            verify(potreroRepository, never()).delete(any());
        }
    }

    @Test
    @DisplayName("obtenerPorId con id inexistente debe lanzar ResourceNotFoundException")
    void obtenerPorId_conIdInexistente_debeLanzarResourceNotFoundException() {
        when(potreroRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> potreroService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
