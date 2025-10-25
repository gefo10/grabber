package com.grabbler.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.grabbler.models.Role;
import com.grabbler.models.User;
import com.grabbler.repositories.RoleRepository;
import com.grabbler.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Component
@Profile("!test") // Don't run during tests
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.getRoles().add(adminRole);
                    userRepository.save(admin);
                    System.out.println("Admin user created.");
                });
    }
}
