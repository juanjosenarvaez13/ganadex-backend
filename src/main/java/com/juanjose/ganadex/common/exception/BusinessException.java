package com.juanjose.ganadex.common.exception;

/**
 * Se lanza cuando una operación es válida sintácticamente pero viola una
 * regla de negocio (ej. intentar registrar la entrada de un animal a un
 * potrero cuando ya tiene un registro de ocupación abierto en otro).
 * El {@link GlobalExceptionHandler} la traduce a HTTP 409 (Conflict).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
