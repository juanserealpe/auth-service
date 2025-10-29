package co.edu.unicauca.entities;

import co.edu.unicauca.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_account")
    private Long idAccount;

    @Column(nullable = false, unique = true, name = "email")
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "account_roles", joinColumns = @JoinColumn(name = "account_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "account")
    @JsonIgnore
    private User user;

    // Getters & setters
    public Long getIdAccount() {return idAccount;}
    public void setIdAccount(Long idAccount) {this.idAccount = idAccount;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Helper methods
    public void addRole(Role role) {this.roles.add(role);}
    public void removeRole(Role role) {this.roles.remove(role);}
    public boolean hasRole(Role role) {return this.roles.contains(role);}
}