package co.edu.unicauca.utilities;

import co.edu.unicauca.authentication.AccountDetails;
import co.edu.unicauca.entities.Account;
import co.edu.unicauca.repositories.AccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta cada solicitud HTTP para validar el token JWT.
 * Si el token es válido, se establece la autenticación en el contexto de seguridad.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AccountRepository accountRepository;

    public JwtAuthFilter(JwtUtils jwtUtils, AccountRepository accountRepository) {
        this.jwtUtils = jwtUtils;
        this.accountRepository = accountRepository;
    }

    /**
     * Valida el JWT presente en el encabezado "Authorization" de cada petición.
     * Si el token es correcto, se autentica al usuario en el contexto de Spring Security.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = req.getRequestURI();

        // Saltar rutas públicas
        if (shouldNotFilter(req)) {
            chain.doFilter(req, res);
            return;
        }

        try {
            String header = req.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                Logger.info(getClass(), "JWT detectado en la solicitud a " + requestURI);

                // Validar token
                if (jwtUtils.validateJwtToken(token)) {
                    Long accountId = jwtUtils.getAccountIdFromJwtToken(token);

                    Account account = accountRepository.findById(accountId)
                            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada con ID: " + accountId));

                    // Crear autenticación y establecerla en el contexto
                    AccountDetails accountDetails = new AccountDetails(account);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    accountDetails, null, accountDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    Logger.success(getClass(), "Autenticación establecida para el usuario: " + account.getEmail());
                } else {
                    Logger.warn(getClass(), "JWT inválido para la solicitud a " + requestURI);
                }
            } else {
                Logger.warn(getClass(), "No se encontró JWT en la solicitud a " + requestURI);
            }

        } catch (Exception e) {
            Logger.error(getClass(), "Error al procesar autenticación: " + e.getMessage());
        }

        chain.doFilter(req, res);
    }

    /**
     * Define las rutas que no requieren filtrado JWT (públicas o estáticas).
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/auth/") ||
                path.startsWith("/h2-console") ||
                path.equals("/favicon.ico") ||
                path.startsWith("/degreework/create") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".gif") ||
                path.endsWith(".png") ||
                path.endsWith(".jpg") ||
                path.endsWith(".ico");
    }
}
