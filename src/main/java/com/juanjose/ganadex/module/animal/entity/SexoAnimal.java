package com.juanjose.ganadex.module.animal.entity;

/**
 * Sexo del animal. Los valores deben coincidir exactamente con el CHECK
 * constraint {@code ck_animales_sexo} definido en V1__initial_schema.sql.
 */
public enum SexoAnimal {
    MACHO,
    HEMBRA
}
