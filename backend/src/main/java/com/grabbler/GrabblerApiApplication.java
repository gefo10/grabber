package com.grabbler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GrabblerApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(GrabblerApiApplication.class, args);
  }
}
