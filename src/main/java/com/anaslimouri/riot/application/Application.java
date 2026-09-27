package com.anaslimouri.riot.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.anaslimouri.riot")
public class Application {

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
