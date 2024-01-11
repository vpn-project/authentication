package com.example.authentication.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokensUtil {

    @Value("${jwt.secret}")
    public String secret;

    @Value("${jwt.lifetime}")
    public Duration jwtLifeTime;

    @SuppressWarnings("MultipleStringLiterals")
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        var rolesList = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        claims.put("roles", rolesList);
        var issuedDate = new Date();
        var expiredDate = new Date(issuedDate.getTime() + jwtLifeTime.toMillis());

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(issuedDate)
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }

    @SuppressWarnings("MultipleStringLiterals")
    public List<String> getRoles(String token) {
        return getAllClaimsFromToken(token).get("roles", List.class);
    }

    public String getUsername(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

}
