package com.strategists.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.strategists.game.service.UpdateService;

@RestController
@RequestMapping("/api/updates")
@CrossOrigin("*")
public class UpdateController {

	@Autowired
	private UpdateService updateService;

	@GetMapping("/{username}")
	public SseEmitter getSseEmitter(@PathVariable String username) {
		return updateService.registerEmitter(username);
	}

}
