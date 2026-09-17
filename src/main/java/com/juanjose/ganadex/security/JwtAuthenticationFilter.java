package com.juanjose.ganadex.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Corre una vez por cada petición HTTP (de ahí OncePerRequestFilter), antes
 * de que la petición llegue a cualquier Controller. Su único trabajo: si
 * hay un header "Authorization: Bearer <token>" válido, le dice al resto de
 * Spring Security "esta petición está autenticada como este usuario". Si no
 * hay token, o es inválido, simplemente no autentica — no lanza error acá;
 * el rechazo (401) lo decide más adelante SecurityConfig según la ruta.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(PREFIJO_BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(PREFIJO_BEARER.length());

        try {
            String username = jwtService.extraerUsername(token);
            boolean yaHayAutenticacion = SecurityContextHolder.getContext().getAuthentication() != null;

            if (username != null && !yaHayAutenticacion) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.esValido(token, userDetails.getUsername())) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (UsernameNotFoundException | io.jsonwebtoken.JwtException e) {
            // Token malformado, o el username del token ya no existe en BD:
            // no autenticamos y dejamos que SecurityConfig decida si la
            // ruta requería autenticación o no.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
