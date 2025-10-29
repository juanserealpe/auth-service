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

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager _authenticationManager;

    @Autowired
    private JwtUtils _jwtUtils;

    @Autowired
    private AccountRepository _accountRepository;

    @Transactional
    public JwtResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        Logger.info(getClass(), "Attempting login for email: " + loginRequest.getEmail());

        try {
            Authentication auth = _authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            AccountDetails userDetails = (AccountDetails) auth.getPrincipal();

            String accessToken = _jwtUtils.generateJwtToken(userDetails);

            Account account = _accountRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));


            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(a -> a.getAuthority())
                    .toList();

            Logger.success(getClass(), "Login successful for account ID: " + userDetails.getId()
                    + " | Roles: " + roles);

            return new JwtResponseDTO(
                    accessToken,
                    userDetails.getId(),
                    roles
            );

        } catch (BadCredentialsException e) {
            Logger.error(getClass(), "Login failed - Invalid credentials");
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}