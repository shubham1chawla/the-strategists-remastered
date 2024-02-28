package com.strategists.game.entity;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.val;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
public class GameMap {

	private String id;
	private String name;
	private String description;
	private Double playerBaseCash;
	private List<Land> lands;

	public static GameMap from(File json) {
		val mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, GameMap.class);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return null;
		}
	}

}
