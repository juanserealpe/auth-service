package co.edu.unicauca.services;

import co.edu.unicauca.entities.Account;
import co.edu.unicauca.repositories.AccountRepository;
import co.edu.unicauca.utilities.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository _accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean emailExists(String email) {
        return _accountRepository.findByEmail(email).isPresent();
    }

    public void validateEmailNotExists(String email) {
        if (emailExists(email)) {
            Logger.warn(getClass(), "Email already in use: " + email);
            throw new IllegalArgumentException("Email already in use");
        }
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public void prepareAccountForRegistration(Account account) {
        account.setPassword(encodePassword(account.getPassword()));
    }
}