package com.juanjose.ganadex.module.animal.dto.response;

import com.juanjose.ganadex.module.animal.entity.EstadoAnimal;
import com.juanjose.ganadex.module.animal.entity.SexoAnimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalResponse {

    private Long id;
    private String numeroArete;
    private String nombre;
    private SexoAnimal sexo;
    private LocalDate fechaNacimiento;
    private BigDecimal pesoActual;
    private String color;
    private EstadoAnimal estado;
    private String observaciones;
    private String foto;
    private LocalDateTime fechaRegistro;

    // Relaciones aplanadas: id + nombre, no el objeto completo.
    private Long razaId;
    private String razaNombre;
    private Long usuarioId;
}
