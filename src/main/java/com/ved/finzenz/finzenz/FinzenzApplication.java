package com.ved.finzenz.finzenz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinzenzApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinzenzApplication.class, args);
	}

}
