package com.grabbler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ContextLoadTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
        System.out.println("✅ Application context loaded successfully!");

        // Print all beans to help debug
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        System.out.println("\n=== Loaded Beans ===");
        for (String beanName : beanNames) {
            System.out.println("  - " + beanName);
        }
    }
}
