package com.grabbler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class GrabblerApiApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GrabblerApiApplication.class, args);
	}

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hello World from CommandLineRunner");
    }

}
