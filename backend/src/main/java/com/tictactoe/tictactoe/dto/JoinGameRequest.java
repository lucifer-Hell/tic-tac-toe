package com.tictactoe.tictactoe.dto;

import com.tictactoe.tictactoe.model.Player;
import lombok.Data;

@Data
public class JoinGameRequest {
    Player player;
    String gameId;
}
