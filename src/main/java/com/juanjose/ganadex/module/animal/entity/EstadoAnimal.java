package com.juanjose.ganadex.module.animal.entity;

/**
 * Estado del ciclo de vida del animal. Los animales nunca se eliminan
 * físicamente: este campo controla su estado. Valores alineados con el
 * CHECK constraint {@code ck_animales_estado} de V1__initial_schema.sql.
 */
public enum EstadoAnimal {
    ACTIVO,
    VENDIDO,
    MUERTO,
    DESCARTADO
}
