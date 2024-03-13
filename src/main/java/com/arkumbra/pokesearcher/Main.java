package com.arkumbra.pokesearcher;

import com.arkumbra.pokesearcher.controller.PokeSearcherController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com")
public class Main {

  public static void main(String[] args) {
    System.out.println("Start!");
    SpringApplication.run(Main.class, args);
  }
}
