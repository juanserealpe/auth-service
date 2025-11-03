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
import java.util.List;

/**
 * Servicio encargado de gestionar el registro de nuevos usuarios
 * y vincularlos con sus cuentas y roles correspondientes.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository _userRepository;

    @Transactional
    public User userRegister(UserRegisterDTO dto) {
        User user = dto.getUser();
        Account account = dto.getAccount();

        user.setAccount(account);
        account.setUser(user);

        if (dto.getRoles() != null) {
            account.setRoles(new HashSet<>(dto.getRoles()));
        }

        User savedUser = _userRepository.save(user);

        return savedUser;
    }

}
