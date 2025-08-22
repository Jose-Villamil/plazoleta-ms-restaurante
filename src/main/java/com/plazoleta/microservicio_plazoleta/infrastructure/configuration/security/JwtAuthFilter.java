package com.plazoleta.microservicio_plazoleta.infrastructure.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private Logger log = Logger.getLogger(JwtAuthFilter.class.getName());
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7);
            try {
                var claims = jwtProvider.parse(token).getBody();
                String email = claims.getSubject();
                @SuppressWarnings("unchecked")
                var roles = ((List<Object>) claims.get("roles"))
                        .stream().map(Object::toString)
                        .map(SimpleGrantedAuthority::new).toList();

                var auth = new UsernamePasswordAuthenticationToken(email, null, roles);
                auth.setDetails(Map.of("uid", claims.get("uid")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ex) {
                log.info(ex.getMessage());
            }
        }
        chain.doFilter(req, res);
    }
}

