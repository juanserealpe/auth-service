package co.edu.unicauca.repositories;

import co.edu.unicauca.entities.User;
import co.edu.unicauca.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN u.account.roles r WHERE r = :role")
    List<User> findByAccountRolesContaining(@Param("role") Role role);

    Optional<User> findByAccount_Email(String email);
    List<User> findByAccount_EmailIn(List<String> emails);
}
