package com.tictactoe.tictactoe.controller;

import com.tictactoe.tictactoe.dto.JoinGameRequest;
import com.tictactoe.tictactoe.model.Game;
import com.tictactoe.tictactoe.model.Player;
import com.tictactoe.tictactoe.model.PlayerMove;
import com.tictactoe.tictactoe.service.TicTacToeGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Objects;

@Controller
@RequestMapping("/game/tic-tac-toe/")
public class TicTacGameController {
    @Autowired
    TicTacToeGameService ticTacToeGameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @PostMapping("/new-game")
    ResponseEntity<Game>createNewGame(@RequestBody Player player){
        return ResponseEntity.ok(ticTacToeGameService.createNewGame(player));
    }

    @PostMapping("/join-game")
    ResponseEntity<Game> joinGame(@RequestBody JoinGameRequest joinGameRequest){
        Game result=ticTacToeGameService.addPlayerToGame(joinGameRequest);
        // Broadcast updated game state to all players in this game
        messagingTemplate.convertAndSend("/topic/game-state/" + result.getGameId(), result);

        return ResponseEntity.ok(result);
    }

    @MessageMapping("/move/{gameId}") // Maps to /app/move
    @SendTo("/topic/game-state/{gameId}") // Broadcasts the game state after each move
    public Game handleMove(PlayerMove move, @DestinationVariable String gameId){
        return ticTacToeGameService.handlePlayerMove(move,gameId);
    }

    @GetMapping("/games-played")
    public ResponseEntity<HashMap<String, Object>> getGamesPlayed(){
        return new ResponseEntity<>(
                new HashMap<>(){{
                    put("gamesPlayed",ticTacToeGameService.getGamesPlayed());
                }}, HttpStatus.OK
        );
    }

}
