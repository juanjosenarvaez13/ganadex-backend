package com.juanjose.ganadex.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtService no depende de Spring ni de ningún repositorio — se instancia
 * directamente con un secret de prueba, sin necesidad de mocks ni de
 * levantar el contexto de la aplicación.
 */
class JwtServiceTest {

    // 32+ caracteres, igual que exige HS256 (ver WeakKeyException documentado
    // en JwtService).
    private static final String SECRET_DE_PRUEBA = "clave-de-prueba-para-jwt-con-32-caracteres-o-mas";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET_DE_PRUEBA, 60_000L);
    }

    @Test
    @DisplayName("un token generado debe permitir extraer el mismo username")
    void generarToken_yExtraerUsername_debeCoincidir() {
        String token = jwtService.generarToken("jbernal");

        assertThat(jwtService.extraerUsername(token)).isEqualTo("jbernal");
    }

    @Test
    @DisplayName("un token recién generado debe ser válido para su propio username")
    void esValido_conTokenRecienGenerado_debeSerTrue() {
        String token = jwtService.generarToken("jbernal");

        assertThat(jwtService.esValido(token, "jbernal")).isTrue();
    }

    @Test
    @DisplayName("un token válido pero para un username distinto no debe pasar la validación")
    void esValido_conUsernameDistinto_debeSerFalse() {
        String token = jwtService.generarToken("jbernal");

        assertThat(jwtService.esValido(token, "otro-usuario")).isFalse();
    }

    @Test
    @DisplayName("un token ya expirado no debe ser válido")
    void esValido_conTokenExpirado_debeSerFalse() throws InterruptedException {
        JwtService jwtServiceExpiraRapido = new JwtService(SECRET_DE_PRUEBA, 1L);
        String token = jwtServiceExpiraRapido.generarToken("jbernal");

        Thread.sleep(20); // asegura que la expiración (1ms) ya pasó

        assertThat(jwtServiceExpiraRapido.esValido(token, "jbernal")).isFalse();
    }

    @Test
    @DisplayName("un token malformado no debe ser válido (no debe lanzar excepción)")
    void esValido_conTokenMalformado_debeSerFalseSinLanzarExcepcion() {
        assertThat(jwtService.esValido("esto-no-es-un-jwt-real", "jbernal")).isFalse();
    }

    @Test
    @DisplayName("un token firmado con otra clave no debe ser válido")
    void esValido_conFirmaDeOtraClave_debeSerFalse() {
        JwtService otroServicio = new JwtService("otra-clave-completamente-distinta-de-32-chars", 60_000L);
        String tokenFirmadoConOtraClave = otroServicio.generarToken("jbernal");

        assertThat(jwtService.esValido(tokenFirmadoConOtraClave, "jbernal")).isFalse();
    }
}
