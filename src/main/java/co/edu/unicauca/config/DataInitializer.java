package co.edu.unicauca.config;

import co.edu.unicauca.entities.Account;
import co.edu.unicauca.entities.User;
import co.edu.unicauca.enums.Role;
import co.edu.unicauca.dtos.UserRegisterDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Configuration
public class DataInitializer {

    private static final String AUTH_URL = "http://localhost:8083/auth/register";

    @Bean
    CommandLineRunner initUsers() {
        return args -> {
            RestTemplate restTemplate = new RestTemplate();

            try {
                // ==== DIRECTOR ====
                User directorUser = new User();
                directorUser.setNames("Carlos");
                directorUser.setLastNames("González");

                Account directorAccount = new Account();
                directorAccount.setEmail("director@unicauca.edu.co");
                directorAccount.setPassword("123456");
                directorAccount.setRoles(Set.of(Role.DIRECTOR));

                UserRegisterDTO directorDTO = new UserRegisterDTO();
                directorDTO.setUser(directorUser);
                directorDTO.setAccount(directorAccount);

                restTemplate.postForEntity(AUTH_URL, directorDTO, String.class);
                System.out.println("[INFO] Usuario DIRECTOR registrado exitosamente.");

            } catch (Exception e) {
                System.out.println("[WARN] El usuario DIRECTOR ya existe o el servicio no está disponible.");
            }

            try {
                // ==== COORDINADOR ====
                User coordinatorUser = new User();
                coordinatorUser.setNames("Laura");
                coordinatorUser.setLastNames("Martínez");

                Account coordinatorAccount = new Account();
                coordinatorAccount.setEmail("coordinador@unicauca.edu.co");
                coordinatorAccount.setPassword("123456");
                coordinatorAccount.setRoles(Set.of(Role.COORDINATOR));

                UserRegisterDTO coordinatorDTO = new UserRegisterDTO();
                coordinatorDTO.setUser(coordinatorUser);
                coordinatorDTO.setAccount(coordinatorAccount);

                restTemplate.postForEntity(AUTH_URL, coordinatorDTO, String.class);
                System.out.println("[INFO] Usuario COORDINADOR registrado exitosamente.");

            } catch (Exception e) {
                System.out.println("[WARN] El usuario COORDINADOR ya existe o el servicio no está disponible.");
            }
        };
    }
}
