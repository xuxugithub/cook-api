package com.cooking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
@MapperScan("com.cooking.mapper")
@Async
public class CookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CookingApplication.class, args);
    }
}
