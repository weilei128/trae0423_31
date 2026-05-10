package com.pharmacy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.pharmacy.mapper")
public class PharmacyInventoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(PharmacyInventoryApplication.class, args);
    }
}
