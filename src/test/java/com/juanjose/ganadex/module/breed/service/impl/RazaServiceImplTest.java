package com.juanjose.ganadex.module.breed.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.animal.repository.AnimalRepository;
import com.juanjose.ganadex.module.breed.dto.request.RazaRequest;
import com.juanjose.ganadex.module.breed.dto.response.RazaResponse;
import com.juanjose.ganadex.module.breed.entity.Raza;
import com.juanjose.ganadex.module.breed.mapper.RazaMapper;
import com.juanjose.ganadex.module.breed.repository.RazaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link RazaServiceImpl}. Todas las dependencias
 * (repositorios, mapper) se simulan con Mockito — estas pruebas no tocan
 * ninguna base de datos, solo validan la lógica de negocio del Service.
 */
@ExtendWith(MockitoExtension.class)
class RazaServiceImplTest {

    @Mock
    private RazaRepository razaRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private RazaMapper razaMapper;

    @InjectMocks
    private RazaServiceImpl razaService;

    private Raza raza(Long id, String nombre) {
        return Raza.builder().id(id).nombre(nombre).descripcion("Descripción").build();
    }

    private RazaResponse response(Long id, String nombre) {
        return RazaResponse.builder().id(id).nombre(nombre).build();
    }

    @Test
    @DisplayName("listarTodas debe mapear y devolver todas las razas existentes")
    void listarTodas_debeRetornarTodasLasRazasMapeadas() {
        Raza brahman = raza(1L, "Brahman");
        Raza angus = raza(2L, "Angus");
        when(razaRepository.findAll()).thenReturn(List.of(brahman, angus));
        when(razaMapper.toResponse(brahman)).thenReturn(response(1L, "Brahman"));
        when(razaMapper.toResponse(angus)).thenReturn(response(2L, "Angus"));

        List<RazaResponse> resultado = razaService.listarTodas();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(RazaResponse::getNombre).containsExactly("Brahman", "Angus");
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("con id existente debe retornar la raza mapeada")
        void conIdExistente_debeRetornarLaRaza() {
            Raza brahman = raza(1L, "Brahman");
            when(razaRepository.findById(1L)).thenReturn(Optional.of(brahman));
            when(razaMapper.toResponse(brahman)).thenReturn(response(1L, "Brahman"));

            RazaResponse resultado = razaService.obtenerPorId(1L);

            assertThat(resultado.getNombre()).isEqualTo("Brahman");
        }

        @Test
        @DisplayName("con id inexistente debe lanzar ResourceNotFoundException")
        void conIdInexistente_debeLanzarResourceNotFoundException() {
            when(razaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> razaService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("crear")
    class Crear {

        @Test
        @DisplayName("con nombre disponible debe guardar y retornar la raza creada")
        void conNombreDisponible_debeGuardarYRetornarRaza() {
            RazaRequest request = RazaRequest.builder().nombre("Brahman").descripcion("Resistente al calor").build();
            Raza entidad = raza(null, "Brahman");
            Raza guardada = raza(1L, "Brahman");

            when(razaRepository.findByNombre("Brahman")).thenReturn(Optional.empty());
            when(razaMapper.toEntity(request)).thenReturn(entidad);
            when(razaRepository.save(entidad)).thenReturn(guardada);
            when(razaMapper.toResponse(guardada)).thenReturn(response(1L, "Brahman"));

            RazaResponse resultado = razaService.crear(request);

            assertThat(resultado.getId()).isEqualTo(1L);
            verify(razaRepository).save(entidad);
        }

        @Test
        @DisplayName("con nombre ya usado por otra raza debe lanzar BusinessException y no guardar")
        void conNombreYaExistente_debeLanzarBusinessException() {
            RazaRequest request = RazaRequest.builder().nombre("Brahman").build();
            when(razaRepository.findByNombre("Brahman")).thenReturn(Optional.of(raza(5L, "Brahman")));

            assertThatThrownBy(() -> razaService.crear(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Brahman");

            verify(razaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar los campos y retornar la raza actualizada")
        void debeActualizarCamposYRetornar() {
            Raza existente = raza(1L, "Brahman");
            RazaRequest request = RazaRequest.builder().nombre("Brahman Rojo").build();

            when(razaRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(razaRepository.findByNombre("Brahman Rojo")).thenReturn(Optional.empty());
            when(razaRepository.save(existente)).thenReturn(existente);
            when(razaMapper.toResponse(existente)).thenReturn(response(1L, "Brahman Rojo"));

            RazaResponse resultado = razaService.actualizar(1L, request);

            assertThat(resultado.getNombre()).isEqualTo("Brahman Rojo");
            verify(razaMapper).updateEntityFromRequest(request, existente);
        }

        @Test
        @DisplayName("permite guardar sin cambiar el nombre (el nombre encontrado es la misma raza)")
        void permiteGuardarSinCambiarNombre() {
            Raza existente = raza(1L, "Brahman");
            RazaRequest request = RazaRequest.builder().nombre("Brahman").build();

            when(razaRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(razaRepository.findByNombre("Brahman")).thenReturn(Optional.of(existente));
            when(razaRepository.save(existente)).thenReturn(existente);
            when(razaMapper.toResponse(existente)).thenReturn(response(1L, "Brahman"));

            RazaResponse resultado = razaService.actualizar(1L, request);

            assertThat(resultado.getNombre()).isEqualTo("Brahman");
        }

        @Test
        @DisplayName("con nombre en uso por otra raza debe lanzar BusinessException")
        void conNombreEnUsoPorOtraRaza_debeLanzarBusinessException() {
            Raza existente = raza(1L, "Brahman");
            RazaRequest request = RazaRequest.builder().nombre("Angus").build();

            when(razaRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(razaRepository.findByNombre("Angus")).thenReturn(Optional.of(raza(2L, "Angus")));

            assertThatThrownBy(() -> razaService.actualizar(1L, request))
                    .isInstanceOf(BusinessException.class);

            verify(razaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        @Test
        @DisplayName("sin animales asociados debe eliminar la raza")
        void sinAnimalesAsociados_debeEliminar() {
            Raza existente = raza(1L, "Brahman");
            when(razaRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(animalRepository.existsByRazaId(1L)).thenReturn(false);

            razaService.eliminar(1L);

            verify(razaRepository, times(1)).delete(existente);
        }

        @Test
        @DisplayName("con animales asociados debe lanzar BusinessException y no eliminar")
        void conAnimalesAsociados_debeLanzarBusinessException() {
            Raza existente = raza(1L, "Brahman");
            when(razaRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(animalRepository.existsByRazaId(1L)).thenReturn(true);

            assertThatThrownBy(() -> razaService.eliminar(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Brahman");

            verify(razaRepository, never()).delete(any());
        }

        @Test
        @DisplayName("con id inexistente debe lanzar ResourceNotFoundException")
        void conIdInexistente_debeLanzarResourceNotFoundException() {
            when(razaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> razaService.eliminar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
