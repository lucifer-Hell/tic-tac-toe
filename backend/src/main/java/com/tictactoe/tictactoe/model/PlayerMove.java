package com.tictactoe.tictactoe.model;

import lombok.Data;

@Data
public class PlayerMove {
    private int x; // The cell index (0-8)
    private int y;
    private String playerId; // "X" or "O"
    // Getters and setters
}
