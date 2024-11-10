package com.tictactoe.tictactoe.model;

import lombok.Data;

import java.util.Random;
import java.util.UUID;

@Data
public class Player {
    String id;
    String playerName;
    public Player(String playerName){
        this.playerName=playerName;
        this.id= String.format("%04d", new Random().nextInt(10000));
    }
    public Player(String playerName,String id){
        this.playerName=playerName;
        this.id= id;
    }
}
