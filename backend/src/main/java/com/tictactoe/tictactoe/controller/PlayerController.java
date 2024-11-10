package com.tictactoe.tictactoe.controller;

import com.tictactoe.tictactoe.model.Player;
import com.tictactoe.tictactoe.service.PlayerService;
import lombok.Getter;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@RequestMapping("/player")
public class PlayerController {
    @Autowired
    PlayerService playerService;
    @PostMapping
    ResponseEntity<Player> createNewPlayer(@RequestBody Player player){
        return new ResponseEntity<>(playerService.addPlayer(player), HttpStatus.CREATED);
//        return ResponseEntity.ok(playerService.addPlayer(player));
    }
    @PostMapping("/validate-username")
    ResponseEntity<HashMap<String,Object>> validateUserName(@RequestBody HashMap<String ,Object> request){
        String playerName= (String) request.getOrDefault("playerName",null);
        if(playerName==null) throw new RuntimeException("Invalid player name");
        boolean isValid= playerService.isUserNameValid(playerName);
        HashMap<String,Object> response=new HashMap<>(){{
            put("isValid",isValid);
        }};
        return ResponseEntity.ok(response);
    }
}
