package com.strategists.game.configuration;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class GameMapConfiguration {

    @Value("classpath:maps")
    private File mapsDirectory;

    @Bean
    public Map<String, File> getGameMapFiles() {
        val map = new HashMap<String, File>();
        for (File json : Objects.requireNonNull(mapsDirectory.listFiles())) {
            val name = json.getName().split("\\.")[0];
            map.put(name, json);
        }
        return map;
    }

}
