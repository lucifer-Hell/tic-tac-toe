package com.tictactoe.tictactoe.service;

import com.tictactoe.tictactoe.model.Player;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PlayerService {
    private static final HashMap<String,Player> playerDb = new HashMap<>();
    // register player
    public Player addPlayer(Player player){
        if (!isUserNameValid(player.getPlayerName())){
            throw new RuntimeException("Invalid player name or player name already taken");
        }
        Player result =new Player(player.getPlayerName());
        playerDb.put(result.getId(),result);
        return result;
    }

    // validate player name
    public boolean isUserNameValid(String userName){
        for(Player player: playerDb.values())
            if(player.getPlayerName().equals(userName))
                return false;
        return true;
    };

    public Player getPlayerId(String id){
        return playerDb.getOrDefault(id,null);
    }

    public void remove(String id){
        playerDb.remove(id);
    }

}
