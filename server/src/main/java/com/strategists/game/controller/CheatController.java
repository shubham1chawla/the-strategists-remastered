package com.strategists.game.controller;

import com.strategists.game.entity.Cheat;
import com.strategists.game.repository.CheatRepository;
import com.strategists.game.request.CreateCheatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cheats")
public class CheatController {

    @Autowired
    private CheatRepository cheatRepository;

    @GetMapping
    public List<Cheat> getCheats() {
        return cheatRepository.findAll();
    }

    @PostMapping
    public Cheat createCheat(@RequestBody CreateCheatRequest request) {
        return cheatRepository.save(Cheat.fromRequest(request));
    }

    @DeleteMapping("/{id}")
    public void deleteCheat(@PathVariable long id) {
        cheatRepository.deleteById(id);
    }
}
