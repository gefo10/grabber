package com.grabbler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import com.grabbler.repositories.*;

import jakarta.transaction.Transactional;

import com.grabbler.models.*;

@SpringBootApplication
public class GrabblerApiApplication implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public GrabblerApiApplication(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    // private final PasswordEncoder passwordEncoder;
    public static void main(String[] args) {
        SpringApplication.run(GrabblerApiApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        final String ADMIN_ROLE = "ADMIN";
        final String USER_ROLE = "CUSTOMER";

        Role adminRole = roleRepository.findByRoleName(ADMIN_ROLE).orElseGet(() -> {
            Role role = new Role(null, ADMIN_ROLE);
            return roleRepository.save(role);
        });

        roleRepository.findByRoleName(USER_ROLE).orElseGet(() -> {
            Role role = new Role(null, USER_ROLE);
            return roleRepository.save(role);
        });

        final String ADMIN_EMAIL = "admin@gmail.com";

        userRepository.findByEmail(ADMIN_EMAIL).ifPresentOrElse(
                (admin) -> System.out.println("Admin user already exists."),
                () -> {
                    User admin = new User();
                    admin.setFirstName("Admin");
                    admin.setLastName("User");
                    admin.setEmail(ADMIN_EMAIL);
                    admin.setPassword("admin123"); // TODO: ensure to hash the password
                    admin.getRoles().add(adminRole);
                    userRepository.save(admin);
                    System.out.println("Admin user created.");
                });
    }

}
