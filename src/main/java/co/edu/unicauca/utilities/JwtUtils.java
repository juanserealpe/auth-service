package co.edu.unicauca.utilities;

import co.edu.unicauca.authentication.AccountDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

/**
 * Clase de utilidad para manejar la creación, validación y extracción de datos
 * desde tokens JWT (JSON Web Tokens). Se utiliza para la autenticación basada en tokens.
 */
@Component
public class JwtUtils {

    // Clave secreta usada para firmar y verificar los tokens JWT.
    // Se inyecta desde application.properties o application.yml
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Tiempo de expiración del token en milisegundos.
    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    /**
     * Genera un token JWT utilizando los detalles de la cuenta autenticada.
     * Incluye información personalizada (claims) como el id de la cuenta y sus roles.
     *
     * @param accountDetails objeto con los datos del usuario autenticado
     * @return token JWT firmado con el algoritmo HS256
     */
    public String generateJwtToken(AccountDetails accountDetails) {
        // Convertir los roles del usuario en una lista de Strings
        List<String> roles = accountDetails.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        // Construcción del token JWT con claims personalizados
        return Jwts.builder()
                .claim("idAccount", accountDetails.getId()) // Agrega el ID de la cuenta
                .claim("roles", roles) // Agrega los roles del usuario
                .setIssuedAt(new Date()) // Fecha de emisión
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Fecha de expiración
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256) // Firma del token
                .compact(); // Genera el token en formato String
    }

    /**
     * Obtiene el ID de la cuenta a partir del token JWT.
     *
     * @param token token JWT
     * @return id de la cuenta (Long)
     */
    public Long getAccountIdFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object idValue = claims.get("idAccount");
        if (idValue == null) {
            throw new IllegalArgumentException("El token no contiene el claim 'idAccount'");
        }

        return Long.parseLong(idValue.toString());
    }


    /**
     * Extrae el correo electrónico (email) almacenado como claim dentro del token JWT.
     *
     * @param token token JWT
     * @return email contenido en el token
     */
    public String getEmailFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody(); // Extrae los claims del token

        return claims.get("email", String.class); // Obtiene el claim "email"
    }

    /**
     * Valida la integridad y vigencia de un token JWT.
     * Verifica que esté bien formado, firmado correctamente y no haya expirado.
     *
     * @param token token JWT a validar
     * @return true si el token es válido, false si no lo es
     */
    public boolean validateJwtToken(String token) {
        try {
            // Intenta parsear el token, si no lanza excepción es válido
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;

            // Captura de posibles errores en el token
        } catch (MalformedJwtException e) {
            Logger.error(getClass(), "Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            Logger.error(getClass(), "JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            Logger.error(getClass(), "JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Logger.error(getClass(), "JWT claims string is empty: " + e.getMessage());
        }
        return false; // Si hubo algún error, el token no es válido
    }
}
