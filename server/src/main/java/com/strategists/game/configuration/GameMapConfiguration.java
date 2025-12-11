package com.strategists.game.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class GameMapConfiguration {

    public static final String GAME_MAP_FILES = "gameMapFiles";

    @Value("classpath:maps")
    private File mapsDirectory;

    @Bean(GAME_MAP_FILES)
    public Map<String, File> getGameMapFiles() {
        final var map = new HashMap<String, File>();
        for (var json : Objects.requireNonNull(mapsDirectory.listFiles())) {
            map.put(json.getName(), json);
        }
        return map;
    }

}
