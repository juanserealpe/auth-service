package co.edu.unicauca.services;

import co.edu.unicauca.entities.Account;
import co.edu.unicauca.repositories.AccountRepository;
import co.edu.unicauca.utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la gestión y validación de cuentas de usuario en el sistema.
 *
 * <p>Esta clase actúa como capa intermedia entre el controlador y el repositorio,
 * proporcionando métodos reutilizables para operaciones comunes sobre entidades {@link Account},
 * tales como validación de correo electrónico y codificación de contraseñas.</p>
 *
 * <p>Principales responsabilidades:</p>
 * <ul>
 *   <li>Verificar si un correo ya está registrado en la base de datos.</li>
 *   <li>Validar que un correo no esté en uso antes de registrar una cuenta nueva.</li>
 *   <li>Codificar contraseñas utilizando el {@link PasswordEncoder} de Spring Security.</li>
 *   <li>Preparar objetos {@link Account} para su registro, aplicando buenas prácticas de seguridad.</li>
 * </ul>
 *
 * <p>Depende de:</p>
 * <ul>
 *   <li>{@link AccountRepository} para el acceso a datos de las cuentas.</li>
 *   <li>{@link PasswordEncoder} para la codificación segura de contraseñas.</li>
 * </ul>
 *
 * @author Juan
 * @version 1.0
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository _accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Verifica si un correo electrónico ya existe en la base de datos.
     *
     * @param email Correo electrónico a verificar.
     * @return {@code true} si el correo ya está registrado, {@code false} en caso contrario.
     */
    public boolean emailExists(String email) {
        return _accountRepository.findByEmail(email).isPresent();
    }

    /**
     * Valida que el correo electrónico no se encuentre ya en uso.
     *
     * <p>Si el correo ya está registrado, lanza una excepción {@link IllegalArgumentException}.</p>
     *
     * @param email Correo electrónico a validar.
     * @throws IllegalArgumentException Si el correo ya está en uso.
     */
    public void validateEmailNotExists(String email) {
        if (emailExists(email)) {
            Logger.warn(getClass(), "Email already in use: " + email);
            throw new IllegalArgumentException("Email already in use");
        }
    }

    /**
     * Codifica una contraseña en texto plano utilizando el {@link PasswordEncoder}.
     *
     * <p>Este método se debe usar siempre antes de persistir contraseñas en la base de datos
     * para evitar almacenar información sensible en texto plano.</p>
     *
     * @param rawPassword Contraseña en texto plano.
     * @return Contraseña codificada de forma segura.
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Prepara una cuenta antes de ser registrada en la base de datos.
     *
     * <p>Actualmente, este método codifica la contraseña del usuario usando {@link #encodePassword(String)}.
     * Se puede extender en el futuro para agregar otras validaciones o inicializaciones.</p>
     *
     * @param account Entidad {@link Account} a preparar para el registro.
     */
    public void prepareAccountForRegistration(Account account) {
        account.setPassword(encodePassword(account.getPassword()));
    }
}
