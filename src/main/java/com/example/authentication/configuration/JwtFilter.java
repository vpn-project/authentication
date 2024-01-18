package com.example.authentication.configuration;

import com.example.authentication.service.UserService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokensUtil jwtTokensUtil;

    private final UserService userService;

    @SuppressWarnings("MagicNumber")
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain
    )
        throws ServletException, IOException {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String username = null;
        String jwt = null;
        UserDetails userDetails = null;
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
            userDetails = userService.findByUsername(username);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var token = new UsernamePasswordAuthenticationToken(
                username,
                null,
                jwtTokensUtil.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // session and ip
            SecurityContextHolder.getContext().setAuthentication(token);
            request.setAttribute("username", userDetails.getUsername());
            request.setAttribute("authorities", userDetails.getAuthorities());
        }
        filterChain.doFilter(request, response);
    }

}
