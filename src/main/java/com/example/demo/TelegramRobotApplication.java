package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan(value = "com.example.demo.db.dao")
@EnableScheduling
@SpringBootApplication
public class TelegramRobotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramRobotApplication.class, args);
	}

}
