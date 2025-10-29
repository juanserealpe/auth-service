package co.edu.unicauca.utilities;

import co.edu.unicauca.authentication.AccountDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(AccountDetails accountDetails) {
        // Convertir roles a lista de strings
        List<String> roles = accountDetails.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return Jwts.builder()
                .claim("idAccount", accountDetails.getId()) // idAccount como claim
                .claim("roles", roles) // roles como lista de strings
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getAccountIdFromJwtToken(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return Long.parseLong(subject);
    }

    public String getEmailFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("email", String.class);
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            Logger.error(getClass(), "Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            Logger.error(getClass(), "JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            Logger.error(getClass(), "JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Logger.error(getClass(), "JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}