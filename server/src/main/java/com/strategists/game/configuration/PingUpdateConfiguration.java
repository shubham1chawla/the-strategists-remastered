package com.strategists.game.configuration;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.strategists.game.service.UpdateService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@EnableAsync
@EnableScheduling
@Configuration
@ConditionalOnProperty(name = "strategists.configuration.sse-ping.enabled", havingValue = "true")
public class PingUpdateConfiguration {

	@Autowired
	private UpdateService updateService;

	@PostConstruct
	public void setup() {
		log.info("Pinging SSE channels enabled.");
	}

	@Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
	public void ping() {
		log.debug("Sending SSE ping...");
		updateService.sendPing();
	}

}
