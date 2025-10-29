package co.edu.unicauca.services;

import co.edu.unicauca.authentication.AccountDetails;
import co.edu.unicauca.dtos.JwtResponseDTO;
import co.edu.unicauca.dtos.LoginRequestDTO;
import co.edu.unicauca.entities.Account;
import co.edu.unicauca.repositories.AccountRepository;
import co.edu.unicauca.utilities.JwtUtils;
import co.edu.unicauca.utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio encargado de manejar la autenticación de usuarios
 * y la generación de tokens JWT.
 */
@Service
public class AuthService {

    @Autowired
    private AuthenticationManager _authenticationManager;

    @Autowired
    private JwtUtils _jwtUtils;

    @Autowired
    private AccountRepository _accountRepository;

    /**
     * Autentica al usuario con las credenciales proporcionadas.
     *
     * Si las credenciales son válidas, genera y devuelve un token JWT
     * junto con la información del usuario autenticado.
     *
     * @param loginRequest DTO que contiene el correo y la contraseña.
     * @return {@link JwtResponseDTO} con el token JWT, el ID del usuario y sus roles.
     * @throws BadCredentialsException si las credenciales son inválidas.
     */
    @Transactional
    public JwtResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        Logger.info(getClass(), "Intentando iniciar sesión con el correo: " + loginRequest.getEmail());

        try {
            // Autenticar usuario con Spring Security
            Authentication auth = _authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Obtener detalles del usuario autenticado
            AccountDetails userDetails = (AccountDetails) auth.getPrincipal();

            // Generar token JWT
            String accessToken = _jwtUtils.generateJwtToken(userDetails);

            // Verificar que la cuenta exista en la base de datos
            Account account = _accountRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

            // Extraer roles del usuario
            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(a -> a.getAuthority())
                    .toList();

            Logger.success(getClass(), "Inicio de sesión exitoso para el ID: " + userDetails.getId()
                    + " | Roles: " + roles);

            // Retornar respuesta con token y roles
            return new JwtResponseDTO(
                    accessToken,
                    userDetails.getId(),
                    roles
            );

        } catch (BadCredentialsException e) {
            Logger.error(getClass(), "Error de inicio de sesión - Credenciales inválidas");
            throw new BadCredentialsException("Correo o contraseña incorrectos");
        }
    }
}
