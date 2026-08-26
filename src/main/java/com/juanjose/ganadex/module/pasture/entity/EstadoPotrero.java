package com.juanjose.ganadex.module.pasture.entity;

/**
 * Estado de un potrero. Valores alineados con el CHECK constraint
 * {@code ck_potreros_estado} de V1__initial_schema.sql.
 */
public enum EstadoPotrero {
    DISPONIBLE,
    OCUPADO,
    MANTENIMIENTO
}
