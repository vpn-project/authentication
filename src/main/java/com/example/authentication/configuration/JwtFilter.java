package com.example.authentication.configuration;

import com.example.authentication.utils.JwtTokensUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokensUtil jwtTokensUtil;

    @SuppressWarnings("MagicNumber")
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain
    )
        throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtTokensUtil.getUsername(jwt);
                log.info(username);
            } catch (ExpiredJwtException e) {
                log.error("Jwt token was expired");
            } catch (MalformedJwtException e) {
                log.error("Jwt token is incorrect");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var token = new UsernamePasswordAuthenticationToken(
                username,
                null,
                jwtTokensUtil.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }

}
