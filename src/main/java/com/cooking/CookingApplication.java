package com.cooking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cooking.mapper")
public class CookingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CookingApplication.class, args);
    }
}
