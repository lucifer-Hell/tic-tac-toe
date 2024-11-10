package com.tictactoe.tictactoe.model;

import lombok.Data;

import java.util.*;

@Data
public class Game {
    private String[][] board; // 3x3 board status
    private Player currentPlayer; // "X" or "O"
    private Player winner; // "X", "O", or "Draw" when the game ends
    private List<Player>players;
    private GameState gameState;
    private String gameId;
    private String [] winMove;
    public Game(Player player){
        players=new ArrayList<>();
        players.add(player);
        gameState=GameState.WAITING_FOR_OTHERS_TO_JOIN;
        gameId= String.format("%04d", new Random().nextInt(10000));
        board=new String[3][3];
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                board[i][j]="-";
        winMove=new String[3];
    }
    // Getters and setters
}

