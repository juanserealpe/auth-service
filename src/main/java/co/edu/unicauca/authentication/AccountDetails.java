package co.edu.unicauca.authentication;

import co.edu.unicauca.entities.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Clase que implementa la interfaz {@link UserDetails} de Spring Security.
 *
 * Esta clase actúa como un adaptador entre la entidad {@link Account}
 * y el sistema de autenticación de Spring Security. Permite que la información
 * del usuario (correo, contraseña, roles, etc.) sea interpretada correctamente
 * por el framework durante el proceso de autenticación y autorización.
 */
public class AccountDetails implements UserDetails {

    /** Entidad que representa la cuenta del usuario autenticado. */
    private final Account account;

    /**
     * Constructor que recibe la cuenta asociada al usuario autenticado.
     *
     * @param account instancia de {@link Account} que contiene la información del usuario.
     */
    public AccountDetails(Account account) {
        this.account = account;
    }

    /**
     * Devuelve la colección de roles (authorities) que posee el usuario.
     *
     * Cada rol del usuario se transforma en un objeto {@link SimpleGrantedAuthority},
     * que es el formato reconocido por Spring Security.
     *
     * @return colección de objetos {@link GrantedAuthority} con los roles del usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve la contraseña encriptada del usuario.
     *
     * @return contraseña encriptada almacenada en la entidad {@link Account}.
     */
    @Override
    public String getPassword() {
        return account.getPassword();
    }

    /**
     * Devuelve el nombre de usuario (en este caso, el correo electrónico).
     *
     * @return correo electrónico del usuario.
     */
    @Override
    public String getUsername() {
        return account.getEmail();
    }

    /**
     * Indica si la cuenta del usuario ha expirado.
     *
     * @return siempre {@code true}, lo que indica que la cuenta nunca expira.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta del usuario está bloqueada.
     *
     * @return siempre {@code true}, lo que indica que la cuenta no está bloqueada.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales del usuario (contraseña) han expirado.
     *
     * @return siempre {@code true}, lo que indica que las credenciales son válidas.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta del usuario está habilitada.
     *
     * @return siempre {@code true}, lo que indica que la cuenta está activa.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Devuelve el ID único de la cuenta del usuario.
     *
     * @return identificador único de la cuenta.
     */
    public Long getId() {
        return account.getIdAccount();
    }

    /**
     * Devuelve la entidad {@link Account} asociada a este usuario.
     *
     * @return instancia de {@link Account}.
     */
    public Account getAccount() {
        return account;
    }
}
