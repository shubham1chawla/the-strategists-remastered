package com.strategists.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Trend;
import com.strategists.game.repository.TrendRepository;

@RestController
@RequestMapping("/api/trends")
public class TrendController {

	@Autowired
	private TrendRepository trendRepository;

	@GetMapping
	public List<Trend> getTrends() {
		return trendRepository.findByOrderByIdAsc();
	}

}
