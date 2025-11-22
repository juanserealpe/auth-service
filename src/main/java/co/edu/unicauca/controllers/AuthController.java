package co.edu.unicauca.controllers;

import co.edu.unicauca.dtos.JwtResponseDTO;
import co.edu.unicauca.dtos.LoginRequestDTO;
import co.edu.unicauca.dtos.UserRegisterDTO;
import co.edu.unicauca.entities.User;
import co.edu.unicauca.enums.Role;
import co.edu.unicauca.services.AccountService;
import co.edu.unicauca.services.AuthService;
import co.edu.unicauca.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador encargado de manejar las operaciones relacionadas con la autenticación y registro de usuarios.
 * Expone endpoints para iniciar sesión y registrar nuevos usuarios con roles específicos.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Servicio encargado del proceso de autenticación y generación del token JWT
    @Autowired
    private AuthService _authService;

    // Servicio que maneja el registro de nuevos usuarios y asignación de roles
    @Autowired
    private UserService _userService;
    @Autowired
    AccountService _accountService;

    /**
     * Endpoint para autenticar un usuario y generar un token JWT si las credenciales son válidas.
     *
     * @param loginRequest objeto con las credenciales (email y password) del usuario.
     * @return ResponseEntity con el token JWT si la autenticación es exitosa o un mensaje de error en caso contrario.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // Autentica al usuario y genera un token JWT
            JwtResponseDTO response = _authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            // Si las credenciales no son válidas, devuelve error 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");

        } catch (Exception e) {
            // Si ocurre un error inesperado, devuelve error 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during authentication");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO request) {
        try {
            User saved = _userService.userRegister(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering the user");
        }
    }

    /**
     * Endpoint simple para validar si una cuenta tiene un rol específico.
     * Diseñado para ser consumido de forma síncrona por otros microservicios mediante Feign Client.
     *
     * @param accountId ID de la cuenta a validar
     * @param expectedRole Rol esperado (ej: STUDENT, DIRECTOR, COORDINATOR, etc.)
     * @return ResponseEntity con boolean: true si tiene el rol, false si no
     */
    @GetMapping("/validate-role/{accountId}/{expectedRole}")
    public ResponseEntity<Boolean> validateUserRole(
            @PathVariable Long accountId,
            @PathVariable String expectedRole) {
        try {
            Role roleEnum = Role.valueOf(expectedRole.toUpperCase());

            boolean hasRole = _accountService.validateAccountRole(accountId, roleEnum);

            return ResponseEntity.ok(hasRole);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(false);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
    /**
     * Endpoint para obtener el ID de una cuenta a partir de su email.
     * Diseñado para ser consumido por otros microservicios mediante Feign Client.
     *
     * @param userEmail Email del usuario cuya cuenta se desea buscar
     * @return ResponseEntity con el ID de la cuenta (Long) si existe
     */
    @GetMapping("/account-id")
    public ResponseEntity<?> getAccountIdByEmail(@RequestParam String userEmail) {
        try {
            Long accountId = _accountService.getAccountIdByEmail(userEmail);
            return ResponseEntity.ok(accountId);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the account ID");
        }
    }
}
