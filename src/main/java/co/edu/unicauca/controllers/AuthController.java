package co.edu.unicauca.controllers;

import co.edu.unicauca.dtos.JwtResponseDTO;
import co.edu.unicauca.dtos.LoginRequestDTO;
import co.edu.unicauca.entities.User;
import co.edu.unicauca.enums.Role;
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

    /**
     * Endpoint para registrar un nuevo usuario con el rol de STUDENT.
     *
     * @param user objeto User con la información del estudiante.
     * @return ResponseEntity con el usuario creado o el error correspondiente.
     */
    @PostMapping("/register-student")
    public ResponseEntity<?> registerStudent(@RequestBody User user) {
        try {
            // Registra el usuario con el rol STUDENT
            User saved = _userService.userRegister(user, Role.STUDENT);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            // Si hay un error de validación, devuelve 400
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Si ocurre un error general, devuelve 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering the student");
        }
    }

    /**
     * Endpoint para registrar un nuevo usuario con el rol de COORDINATOR.
     * Idealmente este endpoint debería estar protegido por permisos de administrador.
     *
     * @param user objeto User con la información del coordinador.
     * @return ResponseEntity con el usuario creado o error correspondiente.
     */
    @PostMapping("/register-coordinator")
    public ResponseEntity<?> registerCoordinator(@RequestBody User user) {
        try {
            // Registra el usuario con el rol COORDINATOR
            User saved = _userService.userRegister(user, Role.COORDINATOR);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering the coordinator");
        }
    }

    /**
     * Endpoint para registrar un nuevo usuario con el rol de DIRECTOR.
     * También debería estar protegido por permisos de administrador.
     *
     * @param user objeto User con la información del director.
     * @return ResponseEntity con el usuario creado o error correspondiente.
     */
    @PostMapping("/register-director")
    public ResponseEntity<?> registerDirector(@RequestBody User user) {
        try {
            // Registra el usuario con el rol DIRECTOR
            User saved = _userService.userRegister(user, Role.DIRECTOR);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering the director");
        }
    }
}
