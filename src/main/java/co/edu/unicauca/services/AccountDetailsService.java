package co.edu.unicauca.services;

import co.edu.unicauca.authentication.AccountDetails;
import co.edu.unicauca.entities.Account;
import co.edu.unicauca.repositories.AccountRepository;
import co.edu.unicauca.utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Servicio de Spring Security para cargar detalles de usuario durante la autenticación.
 * NO debe contener lógica de negocio, solo la carga de datos para Spring Security.
 */
@Service
public class AccountDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository _accountRepository;

    /**
     * Método requerido por Spring Security para cargar un usuario por su username (email en este caso)
     * Este método es llamado automáticamente por el AuthenticationManager durante el login
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Logger.info(getClass(), "Loading user details for email: " + email);

        Account account = _accountRepository.findByEmail(email)
                .orElseThrow(() -> {
                    Logger.error(getClass(), "User not found with email: " + email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        Logger.success(getClass(), "User details loaded successfully for email: " + email);
        return new AccountDetails(account);
    }
}
