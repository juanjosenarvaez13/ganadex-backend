package com.juanjose.ganadex.module.pasture.service.impl;

import com.juanjose.ganadex.common.exception.BusinessException;
import com.juanjose.ganadex.common.exception.ResourceNotFoundException;
import com.juanjose.ganadex.module.animal.entity.Animal;
import com.juanjose.ganadex.module.animal.entity.SexoAnimal;
import com.juanjose.ganadex.module.animal.repository.AnimalRepository;
import com.juanjose.ganadex.module.pasture.dto.request.RegistrarEntradaPotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.request.RegistrarSalidaPotreroRequest;
import com.juanjose.ganadex.module.pasture.dto.response.HistorialPotreroResponse;
import com.juanjose.ganadex.module.pasture.entity.EstadoPotrero;
import com.juanjose.ganadex.module.pasture.entity.HistorialPotrero;
import com.juanjose.ganadex.module.pasture.entity.Potrero;
import com.juanjose.ganadex.module.pasture.mapper.HistorialPotreroMapper;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistorialPotreroServiceImplTest {

    @Mock
    private HistorialPotreroRepository historialPotreroRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private PotreroRepository potreroRepository;

    @Mock
    private HistorialPotreroMapper historialPotreroMapper;

    @InjectMocks
    private HistorialPotreroServiceImpl historialPotreroService;

    private Animal animal(Long id, String arete) {
        return Animal.builder().id(id).numeroArete(arete).sexo(SexoAnimal.HEMBRA).build();
    }

    private Potrero potrero(Long id, String nombre, EstadoPotrero estado) {
        return Potrero.builder().id(id).nombre(nombre).area(BigDecimal.valueOf(3000)).estado(estado).build();
    }

    @Nested
    @DisplayName("registrarEntrada")
    class RegistrarEntrada {

        @Test
        @DisplayName("caso feliz: abre historial y marca el potrero como OCUPADO")
        void casoFeliz_debeAbrirHistorialYOcuparPotrero() {
            Animal animal = animal(1L, "A001");
            Potrero potrero = potrero(2L, "Potrero 1", EstadoPotrero.DISPONIBLE);
            RegistrarEntradaPotreroRequest request = RegistrarEntradaPotreroRequest.builder()
                    .animalId(1L).potreroId(2L).fechaEntrada(LocalDateTime.now().minusHours(1)).build();

            when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
            when(potreroRepository.findById(2L)).thenReturn(Optional.of(potrero));
            when(historialPotreroRepository.findByAnimalIdAndFechaSalidaIsNull(1L)).thenReturn(Optional.empty());
            when(historialPotreroRepository.save(any(HistorialPotrero.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(historialPotreroMapper.toResponse(any(HistorialPotrero.class)))
                    .thenReturn(HistorialPotreroResponse.builder().animalId(1L).potreroId(2L).build());

            HistorialPotreroResponse resultado = historialPotreroService.registrarEntrada(request);

            assertThat(resultado.getAnimalId()).isEqualTo(1L);
            assertThat(potrero.getEstado()).isEqualTo(EstadoPotrero.OCUPADO);
            verify(potreroRepository).save(potrero);
        }

        @Test
        @DisplayName("con animal inexistente debe lanzar ResourceNotFoundException")
        void animalInexistente_debeLanzarResourceNotFoundException() {
            RegistrarEntradaPotreroRequest request = RegistrarEntradaPotreroRequest.builder()
                    .animalId(99L).potreroId(2L).fechaEntrada(LocalDateTime.now()).build();
            when(animalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> historialPotreroService.registrarEntrada(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("con potrero inexistente debe lanzar ResourceNotFoundException")
        void potreroInexistente_debeLanzarResourceNotFoundException() {
            RegistrarEntradaPotreroRequest request = RegistrarEntradaPotreroRequest.builder()
                    .animalId(1L).potreroId(99L).fechaEntrada(LocalDateTime.now()).build();
            when(animalRepository.findById(1L)).thenReturn(Optional.of(animal(1L, "A001")));
            when(potreroRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> historialPotreroService.registrarEntrada(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("si el animal ya tiene una ocupación abierta debe lanzar BusinessException")
        void animalYaOcupado_debeLanzarBusinessException() {
            Animal animal = animal(1L, "A001");
            Potrero potreroDestino = potrero(2L, "Potrero 2", EstadoPotrero.DISPONIBLE);
            Potrero potreroActual = potrero(3L, "Potrero 3", EstadoPotrero.OCUPADO);
            HistorialPotrero abierto = HistorialPotrero.builder()
                    .animal(animal).potrero(potreroActual).fechaEntrada(LocalDateTime.now().minusDays(2)).build();

            RegistrarEntradaPotreroRequest request = RegistrarEntradaPotreroRequest.builder()
                    .animalId(1L).potreroId(2L).fechaEntrada(LocalDateTime.now()).build();

            when(animalRepository.findById(1L)).thenReturn(Optional.of(animal));
            when(potreroRepository.findById(2L)).thenReturn(Optional.of(potreroDestino));
            when(historialPotreroRepository.findByAnimalIdAndFechaSalidaIsNull(1L)).thenReturn(Optional.of(abierto));

            assertThatThrownBy(() -> historialPotreroService.registrarEntrada(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Potrero 3");

            verify(historialPotreroRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("registrarSalida")
    class RegistrarSalida {

        @Test
        @DisplayName("si era el último animal del potrero, debe liberarlo (vuelve a DISPONIBLE)")
        void ultimoAnimalDelPotrero_debeLiberarPotrero() {
            Potrero potrero = potrero(2L, "Potrero 1", EstadoPotrero.OCUPADO);
            HistorialPotrero abierto = HistorialPotrero.builder()
                    .animal(animal(1L, "A001")).potrero(potrero)
                    .fechaEntrada(LocalDateTime.now().minusDays(5)).build();
            RegistrarSalidaPotreroRequest request =
                    RegistrarSalidaPotreroRequest.builder().fechaSalida(LocalDateTime.now()).build();

            when(historialPotreroRepository.findByAnimalIdAndFechaSalidaIsNull(1L)).thenReturn(Optional.of(abierto));
            when(historialPotreroRepository.save(abierto)).thenReturn(abierto);
            when(historialPotreroRepository.findByPotreroIdAndFechaSalidaIsNull(2L)).thenReturn(List.of());
            when(historialPotreroMapper.toResponse(abierto)).thenReturn(HistorialPotreroResponse.builder().build());

            historialPotreroService.registrarSalida(1L, request);

            assertThat(potrero.getEstado()).isEqualTo(EstadoPotrero.DISPONIBLE);
            assertThat(abierto.getFechaSalida()).isNotNull();
            verify(potreroRepository).save(potrero);
        }

        @Test
        @DisplayName("si quedan otros animales en el potrero, NO debe liberarlo")
        void quedanOtrosAnimales_noDebeLiberarPotrero() {
            Potrero potrero = potrero(2L, "Potrero 1", EstadoPotrero.OCUPADO);
            HistorialPotrero abierto = HistorialPotrero.builder()
                    .animal(animal(1L, "A001")).potrero(potrero)
                    .fechaEntrada(LocalDateTime.now().minusDays(5)).build();
            HistorialPotrero otroAbierto = HistorialPotrero.builder()
                    .animal(animal(2L, "A002")).potrero(potrero)
                    .fechaEntrada(LocalDateTime.now().minusDays(1)).build();
            RegistrarSalidaPotreroRequest request =
                    RegistrarSalidaPotreroRequest.builder().fechaSalida(LocalDateTime.now()).build();

            when(historialPotreroRepository.findByAnimalIdAndFechaSalidaIsNull(1L)).thenReturn(Optional.of(abierto));
            when(historialPotreroRepository.save(abierto)).thenReturn(abierto);
            when(historialPotreroRepository.findByPotreroIdAndFechaSalidaIsNull(2L)).thenReturn(List.of(otroAbierto));
            when(historialPotreroMapper.toResponse(abierto)).thenReturn(HistorialPotreroResponse.builder().build());

            historialPotreroService.registrarSalida(1L, request);

            assertThat(potrero.getEstado()).isEqualTo(EstadoPotrero.OCUPADO);
            verify(potreroRepository, never()).save(any());
        }

        @Test
        @DisplayName("sin ocupación abierta debe lanzar BusinessException")
        void sinOcupacionAbierta_debeLanzarBusinessException() {
            RegistrarSalidaPotreroRequest request =
                    RegistrarSalidaPotreroRequest.builder().fechaSalida(LocalDateTime.now()).build();
            when(historialPotreroRepository.findByAnimalIdAndFechaSalidaIsNull(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> historialPotreroService.registrarSalida(1L, request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        @DisplayName("con fecha de salida anterior a la entrada debe lanzar BusinessException")
        void fechaSalidaAnteriorAEntrada_debeLanzarBusinessException() {
            LocalDateTime fechaEntrada = LocalDateTime.now().minusDays(1);
            HistorialPotrero abierto = HistorialPotrero.builder()
                    .animal(animal(1L, "A001")).potrero(potrero(2L, "Potrero 1", EstadoPotrero.OCUPADO))
                    .fechaEntrada(fechaEntrada).build();
            RegistrarSalidaPotreroRequest request = RegistrarSalidaPotreroRequest.builder()
                    .fechaSalida(fechaEntrada.minusDays(1)).build();

            when(historialPotreroRepository.findByAnimalIdAndFechaSalidaIsNull(1L)).thenReturn(Optional.of(abierto));

            assertThatThrownBy(() -> historialPotreroService.registrarSalida(1L, request))
                    .isInstanceOf(BusinessException.class);

            verify(historialPotreroRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("potreroActualDeAnimal sin ocupación abierta debe lanzar ResourceNotFoundException")
    void potreroActualDeAnimal_sinOcupacion_debeLanzarResourceNotFoundException() {
        when(historialPotreroRepository.findByAnimalIdAndFechaSalidaIsNull(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> historialPotreroService.potreroActualDeAnimal(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("historialDeAnimal debe delegar en el repository ordenado por fecha de entrada desc")
    void historialDeAnimal_debeDelegarEnRepository() {
        when(historialPotreroRepository.findByAnimalIdOrderByFechaEntradaDesc(1L)).thenReturn(List.of());

        List<HistorialPotreroResponse> resultado = historialPotreroService.historialDeAnimal(1L);

        assertThat(resultado).isEmpty();
    }
}
