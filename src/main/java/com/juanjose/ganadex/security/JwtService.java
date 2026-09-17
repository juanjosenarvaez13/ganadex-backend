package com.juanjose.ganadex.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Genera y valida los JWT de Ganadex. No sabe nada de Usuario ni de
 * Spring Security — solo trabaja con un username (String) y un token
 * (String). Esa separación es a propósito: esta clase se podría reutilizar
 * en cualquier proyecto sin cambiar una línea.
 */
@Component
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${ganadex.jwt.secret}") String secret,
            @Value("${ganadex.jwt.expiration-ms}") long expirationMs) {
        // HS256 exige una clave de al menos 256 bits (32 bytes). Si el
        // secret configurado es más corto, esto falla al arrancar la app
        // —a propósito: mejor un error claro al inicio que un token débil.
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generarToken(String username) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusMillis(expirationMs)))
                .signWith(secretKey)
                .compact();
    }

    public String extraerUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /** Válido = la firma es correcta, no expiró, y el subject coincide con el usuario esperado. */
    public boolean esValido(String token, String usernameEsperado) {
        try {
            Claims claims = parseClaims(token);
            boolean coincideUsername = claims.getSubject().equals(usernameEsperado);
            boolean noExpirado = claims.getExpiration().after(new Date());
            return coincideUsername && noExpirado;
        } catch (JwtException | IllegalArgumentException e) {
            // Firma inválida, token malformado, o expirado (jjwt lanza
            // ExpiredJwtException, que extiende JwtException) => no válido.
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
