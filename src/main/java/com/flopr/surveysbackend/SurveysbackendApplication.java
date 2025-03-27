package com.flopr.surveysbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SurveysbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurveysbackendApplication.class, args);
		System.out.println("UPPPP!");
	}
}
