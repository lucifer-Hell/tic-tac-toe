package com.tictactoe.tictactoe.service;

import com.tictactoe.tictactoe.dto.JoinGameRequest;
import com.tictactoe.tictactoe.model.Game;
import com.tictactoe.tictactoe.model.GameState;
import com.tictactoe.tictactoe.model.Player;
import com.tictactoe.tictactoe.model.PlayerMove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TicTacToeGameService {
    @Autowired
    PlayerService playerService;
    private static long gamesPlayedTillNow;
    private static final ConcurrentHashMap<String, Game> gameDb=new ConcurrentHashMap<>();
    // new-game
    public Game createNewGame(Player player){
        Game result=new Game(player);
        gameDb.put(result.getGameId(), result);
        return result;
    }
    // add-player
    public Game addPlayerToGame(JoinGameRequest joinGameRequest){
        if(gameDb.containsKey(joinGameRequest.getGameId())) {
            Game game = gameDb.get(joinGameRequest.getGameId());
            if (game.getPlayers().size() > 2) throw new RuntimeException("Cant add more then 2 players ");
            game.getPlayers().add(joinGameRequest.getPlayer());
            // Randomly set the first player’s turn
            Random random = new Random();
            int firstTurnIndex = random.nextInt(2);
            game.setCurrentPlayer(game.getPlayers().get(firstTurnIndex));
            // start running the game
            game.setGameState(GameState.RUNNING);
            // SEND UPDATE EVENT TO GAME
            gameDb.put(game.getGameId(), game);
            return game;
        }else{
            throw new RuntimeException("Game Id not found");
        }
    }
    // handle-move
    public Game handlePlayerMove(PlayerMove move,String gameId){
        Player currentPlayer=playerService.getPlayerId(move.getPlayerId());
        Game game=gameDb.getOrDefault(gameId,null);
        String playerSymbol=getPlayerSymbol(currentPlayer,game);
        if(currentPlayer==null|| game==null) throw new RuntimeException("Invalid player or game id");
        if(isMoveValid(move,game)){
            int x= move.getX();
            int y= move.getY();
            game.getBoard()[x][y]=playerSymbol;
            Player winner=getWinner(game);
            if(winner!=null){
                game.setWinner(winner);
                game.setGameState(GameState.ENDED);
                gamesPlayedTillNow++;
            }
            else if(isDraw(game)){
                game.setGameState(GameState.DRAW);
                gamesPlayedTillNow++;
            }else{
                // Switch the turn to the other player
                Player nextPlayer = game.getPlayers().get(0).equals(currentPlayer) ? game.getPlayers().get(1) : game.getPlayers().get(0);
                game.setCurrentPlayer(nextPlayer);
            }
        }
        if(game.getWinner()!=null){
            gameDb.remove(game.getGameId());
            game.getPlayers().forEach(player -> playerService.remove(player.getId()));
        }
        else gameDb.put(gameId,game);
        return game;
    }
    // Check if the game is a draw
    private boolean isDraw(Game game) {
        for (String[] row : game.getBoard()) {
            for (String cell : row) {
                if (Objects.equals(cell, "-") || cell.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    // Check if there is a winner
    private Player getWinner(Game game) {
        String[][] board = game.getBoard();
        for (int i = 0; i < 3; i++) {
            // Check rows and columns
            if (!Objects.equals(board[i][0], "-") && board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2])) {
                return getPlayerBySymbol(game, board[i][0]);
            }
            if (!Objects.equals(board[0][i], "-") && board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i])) {
                return getPlayerBySymbol(game, board[0][i]);
            }
        }
        // Check diagonals
        if (!Objects.equals(board[0][0], "-") && board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])) {
            return getPlayerBySymbol(game, board[0][0]);
        }
        if (!Objects.equals(board[0][2], "-") && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0])) {
            return getPlayerBySymbol(game, board[0][2]);
        }
        return null;
    }

    // Get player symbol based on the current player
    private String getPlayerSymbol(Player currentPlayer, Game game) {
        return (game.getPlayers().get(0).equals(currentPlayer)) ? "X" : "O";
    }

    // Get the player based on the symbol on the board
    private Player getPlayerBySymbol(Game game, String symbol) {
        return symbol.equals("X") ? game.getPlayers().get(0) : game.getPlayers().get(1);
    }

    // Validate the move
    private boolean isMoveValid(PlayerMove move, Game game) {
        int x = move.getX();
        int y = move.getY();
        return x >= 0 && x < 3 && y >= 0 && y < 3 && game.getBoard()[x][y].equals("-");
    }

    public Long getGamesPlayed(){
        return gamesPlayedTillNow;
    }
}
