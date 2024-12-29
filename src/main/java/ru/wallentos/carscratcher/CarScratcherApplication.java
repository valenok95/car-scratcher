package ru.wallentos.carscratcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCORS
public class CarScratcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarScratcherApplication.class, args);
    }

}
