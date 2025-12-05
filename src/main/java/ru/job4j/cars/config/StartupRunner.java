package ru.job4j.cars.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("\n=========================================");
        System.out.println("Приложение успешно запущено!");
        System.out.println("Доступно по адресу: http://localhost:8080");
        System.out.println("=========================================\n");
    }
}