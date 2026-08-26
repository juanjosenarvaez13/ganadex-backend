package com.juanjose.ganadex.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Envoltorio de respuesta consistente para toda la API de Ganadex.
 * <p>
 * Todas las respuestas (éxito o error) siguen esta misma forma:
 * {@code { "success": true, "message": "...", "data": {} } }
 *
 * @param success indica si la operación fue exitosa.
 * @param message mensaje legible para el cliente/consumidor de la API.
 * @param data    payload de la respuesta. Null en errores sin detalle adicional.
 * @param <T>     tipo del payload.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, String message, T data) {

    private static final String DEFAULT_SUCCESS_MESSAGE = "Operación realizada correctamente.";

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
