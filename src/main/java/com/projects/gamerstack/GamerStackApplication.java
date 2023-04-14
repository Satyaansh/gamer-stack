package com.projects.gamerstack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GamerStackApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamerStackApplication.class, args);
	}

}
