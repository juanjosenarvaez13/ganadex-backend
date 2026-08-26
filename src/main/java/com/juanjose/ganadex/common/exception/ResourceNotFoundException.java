package com.juanjose.ganadex.common.exception;

/**
 * Se lanza cuando se busca un recurso (por id u otro identificador) que no
 * existe. El {@link GlobalExceptionHandler} la traduce a HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Atajo para el caso más común: "Entidad con id X no fue encontrada".
     */
    public static ResourceNotFoundException of(String entityName, Object id) {
        return new ResourceNotFoundException(
                "%s con id %s no fue encontrado.".formatted(entityName, id));
    }
}
