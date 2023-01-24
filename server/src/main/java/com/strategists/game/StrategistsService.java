package com.strategists.game;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
public class StrategistsService implements CommandLineRunner {

	public static void main(String... args) {
		SpringApplication.run(StrategistsService.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("We are up guys!");
	}

}
