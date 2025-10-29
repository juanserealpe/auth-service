package co.edu.unicauca.dtos;

import java.util.List;

public class JwtResponseDTO {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long idAccount;
    private List<String> roles;

    // Constructors
    public JwtResponseDTO(String token, String refreshToken, Long idAccount, List<String> roles) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.idAccount = idAccount;
        this.roles = roles;
    }

    // Constructor without refresh
    public JwtResponseDTO(String token, Long idAccount, List<String> roles) {
        this.token = token;
        this.idAccount = idAccount;
        this.roles = roles;
    }

    public JwtResponseDTO() {}

    // Getters y setters

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getIdAccount() { return idAccount; }
    public void setIdAccount(Long idAccount) { this.idAccount = idAccount; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}