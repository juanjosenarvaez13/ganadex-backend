package com.juanjose.ganadex.module.animal.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.animal.dto.request.AnimalCambiarEstadoRequest;
import com.juanjose.ganadex.module.animal.dto.request.AnimalCreateRequest;
import com.juanjose.ganadex.module.animal.dto.request.AnimalUpdateRequest;
import com.juanjose.ganadex.module.animal.dto.response.AnimalResponse;
import com.juanjose.ganadex.module.animal.entity.Animal;
import com.juanjose.ganadex.module.animal.entity.EstadoAnimal;
import com.juanjose.ganadex.module.animal.entity.SexoAnimal;
import com.juanjose.ganadex.module.animal.mapper.AnimalMapper;
import com.juanjose.ganadex.module.animal.repository.AnimalRepository;
import com.juanjose.ganadex.module.breed.entity.Raza;
import com.juanjose.ganadex.module.breed.repository.RazaRepository;
import com.juanjose.ganadex.module.user.entity.Usuario;
import com.juanjose.ganadex.security.AuthenticatedUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link AnimalServiceImpl}, con foco especial en la
 * máquina de estados de {@code cambiarEstado} (ver TRANSICIONES_VALIDAS):
 * ACTIVO es el único estado del que se puede salir; VENDIDO/MUERTO/DESCARTADO
 * son terminales.
 */
@ExtendWith(MockitoExtension.class)
class AnimalServiceImplTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private RazaRepository razaRepository;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private AnimalMapper animalMapper;

    @InjectMocks
    private AnimalServiceImpl animalService;

    private Raza raza() {
        return Raza.builder().id(10L).nombre("Brahman").build();
    }

    private Usuario usuario() {
        return Usuario.builder().id(1L).username("jbernal").activo(true).build();
    }

    private Animal animal(Long id, String arete, EstadoAnimal estado) {
        return Animal.builder().id(id).numeroArete(arete).sexo(SexoAnimal.HEMBRA).estado(estado).raza(raza()).build();
    }

    private AnimalResponse response(Long id, String arete, EstadoAnimal estado) {
        return AnimalResponse.builder().id(id).numeroArete(arete).estado(estado).build();
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("sin filtro de estado debe usar findAll paginado")
        void sinFiltro_debeUsarFindAll() {
            Pageable pageable = Pageable.unpaged();
            Animal a = animal(1L, "A001", EstadoAnimal.ACTIVO);
            when(animalRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(a)));
            when(animalMapper.toResponse(a)).thenReturn(response(1L, "A001", EstadoAnimal.ACTIVO));

            Page<AnimalResponse> resultado = animalService.listar(null, pageable);

            assertThat(resultado.getContent()).hasSize(1);
            verify(animalRepository, never()).findByEstado(any(), any());
        }

        @Test
        @DisplayName("con filtro de estado debe usar findByEstado")
        void conFiltro_debeUsarFindByEstado() {
            Pageable pageable = Pageable.unpaged();
            Animal a = animal(1L, "A001", EstadoAnimal.VENDIDO);
            when(animalRepository.findByEstado(EstadoAnimal.VENDIDO, pageable)).thenReturn(new PageImpl<>(List.of(a)));
            when(animalMapper.toResponse(a)).thenReturn(response(1L, "A001", EstadoAnimal.VENDIDO));

            Page<AnimalResponse> resultado = animalService.listar(EstadoAnimal.VENDIDO, pageable);

            assertThat(resultado.getContent()).hasSize(1);
            verify(animalRepository, never()).findAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("crear")
    class Crear {

        @Test
        @DisplayName("con arete disponible debe resolver raza y usuario autenticado, y guardar")
        void conAreteDisponible_debeGuardar() {
            AnimalCreateRequest request = AnimalCreateRequest.builder()
                    .numeroArete("A001").sexo(SexoAnimal.HEMBRA).razaId(10L).build();
            Animal nuevo = Animal.builder().numeroArete("A001").sexo(SexoAnimal.HEMBRA).build();
            Animal guardado = animal(1L, "A001", EstadoAnimal.ACTIVO);

            when(animalRepository.findByNumeroArete("A001")).thenReturn(Optional.empty());
            when(razaRepository.findById(10L)).thenReturn(Optional.of(raza()));
            when(authenticatedUserProvider.obtenerUsuarioActual()).thenReturn(usuario());
            when(animalMapper.toEntity(request)).thenReturn(nuevo);
            when(animalRepository.save(nuevo)).thenReturn(guardado);
            when(animalMapper.toResponse(guardado)).thenReturn(response(1L, "A001", EstadoAnimal.ACTIVO));

            AnimalResponse resultado = animalService.crear(request);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(nuevo.getRaza()).isEqualTo(raza());
            assertThat(nuevo.getUsuario()).isEqualTo(usuario());
        }

        @Test
        @DisplayName("con arete ya registrado debe lanzar BusinessException")
        void conAreteDuplicado_debeLanzarBusinessException() {
            AnimalCreateRequest request = AnimalCreateRequest.builder().numeroArete("A001").razaId(10L).build();
            when(animalRepository.findByNumeroArete("A001"))
                    .thenReturn(Optional.of(animal(5L, "A001", EstadoAnimal.ACTIVO)));

            assertThatThrownBy(() -> animalService.crear(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("A001");

            verify(animalRepository, never()).save(any());
        }

        @Test
        @DisplayName("con raza inexistente debe lanzar ResourceNotFoundException")
        void conRazaInexistente_debeLanzarResourceNotFoundException() {
            AnimalCreateRequest request = AnimalCreateRequest.builder().numeroArete("A001").razaId(999L).build();
            when(animalRepository.findByNumeroArete("A001")).thenReturn(Optional.empty());
            when(razaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> animalService.crear(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(animalRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("actualizar")
    class Actualizar {

        @Test
        @DisplayName("con arete disponible para ese animal debe actualizar")
        void debeActualizar() {
            Animal existente = animal(1L, "A001", EstadoAnimal.ACTIVO);
            AnimalUpdateRequest request = AnimalUpdateRequest.builder()
                    .numeroArete("A001").sexo(SexoAnimal.HEMBRA).razaId(10L).build();

            when(animalRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(animalRepository.findByNumeroArete("A001")).thenReturn(Optional.of(existente));
            when(razaRepository.findById(10L)).thenReturn(Optional.of(raza()));
            when(animalRepository.save(existente)).thenReturn(existente);
            when(animalMapper.toResponse(existente)).thenReturn(response(1L, "A001", EstadoAnimal.ACTIVO));

            AnimalResponse resultado = animalService.actualizar(1L, request);

            assertThat(resultado.getId()).isEqualTo(1L);
            verify(animalMapper).updateEntityFromRequest(request, existente);
        }

        @Test
        @DisplayName("con arete en uso por otro animal debe lanzar BusinessException")
        void conAreteEnUsoPorOtroAnimal_debeLanzarBusinessException() {
            Animal existente = animal(1L, "A001", EstadoAnimal.ACTIVO);
            Animal otro = animal(2L, "A002", EstadoAnimal.ACTIVO);
            AnimalUpdateRequest request = AnimalUpdateRequest.builder().numeroArete("A002").razaId(10L).build();

            when(animalRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(animalRepository.findByNumeroArete("A002")).thenReturn(Optional.of(otro));

            assertThatThrownBy(() -> animalService.actualizar(1L, request))
                    .isInstanceOf(BusinessException.class);

            verify(animalRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("cambiarEstado — máquina de estados")
    class CambiarEstado {

        @ParameterizedTest(name = "ACTIVO -> {0} debe estar permitido")
        @EnumSource(value = EstadoAnimal.class, names = {"VENDIDO", "MUERTO", "DESCARTADO"})
        @DisplayName("desde ACTIVO, las tres transiciones terminales deben estar permitidas")
        void desdeActivo_transicionesTerminales_debenPermitirse(EstadoAnimal nuevoEstado) {
            Animal existente = animal(1L, "A001", EstadoAnimal.ACTIVO);
            AnimalCambiarEstadoRequest request = AnimalCambiarEstadoRequest.builder().nuevoEstado(nuevoEstado).build();

            when(animalRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(animalRepository.save(existente)).thenReturn(existente);
            when(animalMapper.toResponse(existente)).thenReturn(response(1L, "A001", nuevoEstado));

            AnimalResponse resultado = animalService.cambiarEstado(1L, request);

            assertThat(resultado.getEstado()).isEqualTo(nuevoEstado);
            assertThat(existente.getEstado()).isEqualTo(nuevoEstado);
        }

        @ParameterizedTest(name = "{0} es terminal: no debe permitir ninguna transición")
        @EnumSource(value = EstadoAnimal.class, names = {"VENDIDO", "MUERTO", "DESCARTADO"})
        @DisplayName("desde un estado terminal, cualquier transición debe lanzar BusinessException")
        void desdeEstadoTerminal_cualquierTransicion_debeLanzarBusinessException(EstadoAnimal estadoTerminal) {
            Animal existente = animal(1L, "A001", estadoTerminal);
            AnimalCambiarEstadoRequest request =
                    AnimalCambiarEstadoRequest.builder().nuevoEstado(EstadoAnimal.ACTIVO).build();

            when(animalRepository.findById(1L)).thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> animalService.cambiarEstado(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(estadoTerminal.name());

            verify(animalRepository, never()).save(any());
        }

        @Test
        @DisplayName("con animal inexistente debe lanzar ResourceNotFoundException")
        void animalInexistente_debeLanzarResourceNotFoundException() {
            AnimalCambiarEstadoRequest request =
                    AnimalCambiarEstadoRequest.builder().nuevoEstado(EstadoAnimal.VENDIDO).build();
            when(animalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> animalService.cambiarEstado(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("si viene observaciones, debe actualizarlas junto con el estado")
        void conObservaciones_debeActualizarlas() {
            Animal existente = animal(1L, "A001", EstadoAnimal.ACTIVO);
            AnimalCambiarEstadoRequest request = AnimalCambiarEstadoRequest.builder()
                    .nuevoEstado(EstadoAnimal.MUERTO)
                    .observaciones("Murió por causas naturales.")
                    .build();

            when(animalRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(animalRepository.save(existente)).thenReturn(existente);
            when(animalMapper.toResponse(existente)).thenReturn(response(1L, "A001", EstadoAnimal.MUERTO));

            animalService.cambiarEstado(1L, request);

            assertThat(existente.getObservaciones()).isEqualTo("Murió por causas naturales.");
        }
    }
}
