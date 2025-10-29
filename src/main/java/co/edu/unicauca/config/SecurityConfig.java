package co.edu.unicauca.config;

import co.edu.unicauca.services.AccountDetailsService;
import co.edu.unicauca.utilities.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configura la seguridad de la aplicación, incluyendo autenticación,
 * autorización y manejo del token JWT.
 *
 * <p>Esta clase define cómo Spring Security manejará las solicitudes HTTP,
 * la validación de usuarios y la protección de endpoints.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AccountDetailsService accountDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, AccountDetailsService accountDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.accountDetailsService = accountDetailsService;
    }

    /**
     * Configura el AuthenticationManager para usar nuestro servicio de usuarios
     * y el codificador de contraseñas.
     *
     * @param http objeto HttpSecurity
     * @param encoder codificador de contraseñas (BCrypt)
     * @return AuthenticationManager configurado
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder encoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(accountDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return new ProviderManager(authProvider);
    }

    /**
     * Define la cadena de filtros de seguridad.
     *
     * - Desactiva CSRF (para APIs REST).
     * - Define sesiones sin estado (JWT).
     * - Permite acceso libre a /auth/** y /h2-console/**.
     * - Requiere autenticación para el resto de rutas.
     * - Agrega el filtro personalizado de JWT antes del filtro estándar de login.
     *
     * @param http objeto HttpSecurity
     * @return SecurityFilterChain configurada
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivar protección CSRF (innecesario en JWT)
                .csrf(csrf -> csrf.disable())

                // Permitir uso de H2 Console en iframes
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                // Indicar que la API no mantiene sesiones
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        // Añadir el filtro JWT antes del filtro de autenticación estándar
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
