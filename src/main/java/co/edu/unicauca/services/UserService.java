package co.edu.unicauca.services;

import co.edu.unicauca.entities.Account;
import co.edu.unicauca.entities.User;
import co.edu.unicauca.enums.Role;
import co.edu.unicauca.repositories.UserRepository;
import co.edu.unicauca.utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de gestionar el registro de nuevos usuarios
 * y vincularlos con sus cuentas y roles correspondientes.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private AccountService _accountService;

    /**
     * Registra un nuevo usuario en el sistema, validando su correo,
     * codificando su contraseña y asignando los roles proporcionados.
     *
     * @param prmUser Usuario a registrar (incluye la cuenta asociada).
     * @param roles Roles que se asignarán al nuevo usuario.
     * @return Usuario registrado y persistido en la base de datos.
     */
    @Transactional
    public User userRegister(User prmUser, Role... roles) {
        Logger.info(getClass(), "Intentando registrar usuario: " + prmUser.getAccount().getEmail());

        // Validar y preparar cuenta
        _accountService.validateEmailNotExists(prmUser.getAccount().getEmail());
        _accountService.prepareAccountForRegistration(prmUser.getAccount());

        // Asignar roles a la cuenta
        Account account = prmUser.getAccount();
        for (Role role : roles) {
            account.addRole(role);
        }

        // Guardar usuario en la base de datos
        User resultSave = _userRepository.save(prmUser);
        Logger.success(getClass(), "Usuario registrado con éxito. ID: " +
                resultSave.getIdUser() + " - Roles: " + account.getRoles());

        return resultSave;
    }
}
