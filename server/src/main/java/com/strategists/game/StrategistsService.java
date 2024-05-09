package com.strategists.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StrategistsService {

	public static void main(String... args) {
		SpringApplication.run(StrategistsService.class, args);
	}

}
