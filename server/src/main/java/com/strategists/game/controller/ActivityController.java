package com.strategists.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Activity;
import com.strategists.game.repository.ActivityRepository;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

	@Autowired
	private ActivityRepository activityRepository;

	@GetMapping
	public List<Activity> getActivities() {
		return activityRepository.findByOrderByIdDesc();
	}

}
