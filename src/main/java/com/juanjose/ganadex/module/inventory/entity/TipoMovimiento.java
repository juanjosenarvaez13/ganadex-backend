package com.juanjose.ganadex.module.inventory.entity;

/**
 * Tipo de movimiento de inventario. Valores alineados con el CHECK
 * constraint {@code ck_movimientos_inventario_tipo} de V1__initial_schema.sql.
 */
public enum TipoMovimiento {
    ENTRADA,
    SALIDA,
    AJUSTE
}
