package co.edu.unicauca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de beans generales utilizados en la aplicación.
 *
 * <p>Esta clase se encarga de definir y registrar componentes comunes
 * (beans) en el contexto de Spring para que puedan ser inyectados
 * automáticamente en otras partes del sistema mediante {@code @Autowired}.</p>
 *
 * <p>Actualmente, define un único bean:
 * <ul>
 *     <li>{@link PasswordEncoder}: utilizado para la codificación y verificación
 *         de contraseñas de los usuarios, implementado con {@link BCryptPasswordEncoder}.</li>
 * </ul>
 *
 * <p><strong>Importancia de BCrypt:</strong></p>
 * <p>BCrypt aplica un algoritmo de hash adaptativo, lo que significa que su costo
 * computacional puede aumentarse con el tiempo, haciendo más difícil para los atacantes
 * usar fuerza bruta incluso si obtienen el hash.</p>
 *
 * @author Juan
 * @version 1.0
 */
@Configuration
public class BeansConfig {

    /**
     * Bean de tipo {@link PasswordEncoder} basado en el algoritmo BCrypt.
     *
     * <p>Este bean será utilizado por Spring Security y otros servicios
     * (como {@code AccountService}) para codificar contraseñas antes de
     * guardarlas en la base de datos, y también para verificarlas durante la autenticación.</p>
     *
     * @return una instancia de {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
