package com.example.CodGenerate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodGenerateApplication {

  public static void main(String[] args) {
    SpringApplication.run(CodGenerateApplication.class, args);
  }

}
