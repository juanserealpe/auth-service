package co.edu.unicauca.services;

import co.edu.unicauca.dtos.UserRegisterDTO;
import co.edu.unicauca.entities.Account;
import co.edu.unicauca.entities.User;
import co.edu.unicauca.enums.Role;
import co.edu.unicauca.repositories.UserRepository;
import co.edu.unicauca.utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
public class UserService {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private AccountService _accountService;

    /**
     * Registra un nuevo usuario en el sistema, validando su correo,
     * codificando su contraseña y asignando los roles proporcionados.
     */
    @Transactional
    public User userRegister(UserRegisterDTO dto) {
        User user = dto.getUser();
        Account account = dto.getAccount();

        user.setAccount(account);
        account.setUser(user);
        _accountService.validateEmailNotExists(account.getEmail());
        _accountService.prepareAccountForRegistration(account);

        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            account.setRoles(new HashSet<>(dto.getRoles()));
        }
        User savedUser = _userRepository.save(user);

        Logger.success(getClass(), "Usuario registrado con éxito. ID: " +
                savedUser.getIdUser() + " - Roles: " + account.getRoles());

        return savedUser;
    }
}
