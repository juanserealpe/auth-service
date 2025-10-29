package co.edu.unicauca.services;

import co.edu.unicauca.authentication.AccountDetails;
import co.edu.unicauca.dtos.JwtResponseDTO;
import co.edu.unicauca.dtos.LoginRequestDTO;
import co.edu.unicauca.entities.Account;
import co.edu.unicauca.entities.RefreshToken;
import co.edu.unicauca.exceptions.TokenRefreshException;
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
    private RefreshTokenService _refreshTokenService;

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

            RefreshToken refreshToken = _refreshTokenService.createRefreshToken(account);

            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(a -> a.getAuthority())
                    .toList();

            Logger.success(getClass(), "Login successful for account ID: " + userDetails.getId()
                    + " | Roles: " + roles);

            return new JwtResponseDTO(
                    accessToken,
                    refreshToken.getToken(),
                    userDetails.getId(),
                    roles
            );

        } catch (BadCredentialsException e) {
            Logger.error(getClass(), "Login failed - Invalid credentials");
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Transactional
    public JwtResponseDTO refreshAccessToken(String requestRefreshToken) {
        Logger.info(getClass(), "Attempting to refresh access token");

        try {
            RefreshToken refreshToken = _refreshTokenService.findByToken(requestRefreshToken);
            _refreshTokenService.verifyExpiration(refreshToken);

            Account account = refreshToken.getAccount();

            AccountDetails userDetails = new AccountDetails(account);
            String newAccessToken = _jwtUtils.generateJwtToken(userDetails);

            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(a -> a.getAuthority())
                    .toList();

            Logger.success(getClass(), "Access token refreshed successfully for account ID: "
                    + account.getIdAccount());

            return new JwtResponseDTO(newAccessToken, account.getIdAccount(), roles);

        } catch (TokenRefreshException e) {
            Logger.error(getClass(), "Token refresh failed: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void logout(String refreshToken) {
        Logger.info(getClass(), "Processing logout");

        if (refreshToken != null && !refreshToken.isEmpty()) {
            _refreshTokenService.revokeToken(refreshToken);
            Logger.success(getClass(), "Logout successful - token revoked");
        }
    }

    @Transactional
    public void logoutAllDevices(Long accountId) {
        Logger.info(getClass(), "Logging out all devices for account ID: " + accountId);

        Account account = _accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        _refreshTokenService.revokeTokensByAccount(account);
        Logger.success(getClass(), "All devices logged out for account ID: " + accountId);
    }
}