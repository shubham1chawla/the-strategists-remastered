package com.strategists.game.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.strategists.game.entity.Land;
import com.strategists.game.repository.LandRepository;
import com.strategists.game.service.LandService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class LandServiceImpl implements LandService {

	@Autowired
	private LandRepository landRepository;

	/**
	 * Caching the count of lands to avoid DB calls on runtime. We can't cache the
	 * entire list of lands as many of its fields are transient and subject to
	 * change.
	 */
	private Integer count = null;

	@Override
	public List<Land> getLands() {
		return landRepository.findAll();
	}

	@Override
	public int getCount() {
		if (Objects.isNull(count)) {
			log.info("Caching count of lands...");
			count = (int) landRepository.count();
		}
		return count;
	}

}
