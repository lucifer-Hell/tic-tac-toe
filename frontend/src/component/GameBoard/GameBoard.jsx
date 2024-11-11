import React, { useState, useEffect } from "react";
import { createGame, joinGame } from "../../services/api";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import "./GameBoard.css";

const WEBSOCKET_URL = "http://54.145.214.185/api/tic-tac-toe"; // WebSocket endpoint

function GameBoard({ player }) {
  const [game, setGame] = useState(null);
  const [board, setBoard] = useState(Array(3).fill(Array(3).fill("")));
  const [currentTurn, setCurrentTurn] = useState(null);
  const [stompClient, setStompClient] = useState(null);
  const [isJoiningGame, setIsJoiningGame] = useState(false); // Track if we are in the "join game" mode
  const [gameIdInput, setGameIdInput] = useState(""); // Store the Game ID input
  const [isWebSocketConnected, setIsWebSocketConnected] = useState(false);

  useEffect(() => {
    const socket = new SockJS(WEBSOCKET_URL);
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log("Connected to WebSocket");
        setStompClient(client);
        setIsWebSocketConnected(true); // Mark WebSocket as connected
      },
      onDisconnect: () => {
        setIsWebSocketConnected(false); // Mark WebSocket as disconnected
      },
    });

    client.activate();

    return () => {
      if (client) {
        client.deactivate();
      }
    };
  }, []);

  // const startNewGame = async () => {
  //   const newGame = await createGame(player);
  //   setGame(newGame);
  //   setCurrentTurn(newGame.currentPlayer);

  //   stompClient.subscribe(`/topic/game-state/${newGame.gameId}`, (message) => {
  //     const updatedGame = JSON.parse(message.body);
  //     setGame(updatedGame);
  //     setBoard(updatedGame.board);
  //     setCurrentTurn(updatedGame.currentPlayer);
  //   });
  // };

  const startNewGame = () => {
    if (!isWebSocketConnected) return; // Only start if WebSocket is connected
    createGame(player)
      .then((newGame) => {
        // Set the game state and current turn
        setGame(newGame);
        setCurrentTurn(newGame.currentPlayer);
        return newGame; // Pass newGame to the next .then() block
      })
      .then((newGame) => {
        if (newGame) {
          // Subscribe to the game state updates after the state is set
          stompClient.subscribe(
            `/topic/game-state/${newGame.gameId}`,
            (message) => {
              const updatedGame = JSON.parse(message.body);
              setGame(updatedGame);
              setBoard(updatedGame.board);
              setCurrentTurn(updatedGame.currentPlayer);
            }
          );
        }
      })
      .catch((error) => {
        console.error("Error starting new game:", error);
      });
  };

  // const joinExistingGame = async () => {
  //   if (!gameIdInput) return; // Only join if a game ID is provided
  //   const joinedGame = await joinGame({ gameId: gameIdInput, player });
  //   setGame(joinedGame);
  //   setCurrentTurn(joinedGame.currentPlayer);

  //   stompClient.subscribe(`/topic/game-state/${gameIdInput}`, (message) => {
  //     const updatedGame = JSON.parse(message.body);
  //     console.log("updated game ", updatedGame);
  //     setGame(updatedGame);
  //     setBoard(updatedGame.board);
  //     setCurrentTurn(updatedGame.currentPlayer);
  //   });
  // };

  const joinExistingGame = () => {
    if (!isWebSocketConnected) return; // Only start if WebSocket is connected
    if (!gameIdInput) return; // Only join if a game ID is provided

    joinGame({ gameId: gameIdInput, player })
      .then((joinedGame) => {
        // Set the game state and current turn
        setGame(joinedGame);
        setCurrentTurn(joinedGame.currentPlayer);
        return joinedGame; // Pass joinedGame to the next .then() block
      })
      .then((joinedGame) => {
        if (joinedGame) {
          // Subscribe to the game state updates after the state is set
          stompClient.subscribe(
            `/topic/game-state/${joinedGame.gameId}`,
            (message) => {
              const updatedGame = JSON.parse(message.body);
              console.log("updated game ", updatedGame);
              setGame(updatedGame);
              setBoard(updatedGame.board);
              setCurrentTurn(updatedGame.currentPlayer);
            }
          );
        }
      })
      .catch((error) => {
        console.error("Error joining existing game:", error);
      });
  };

  const handleMove = (x, y) => {
    if (
      currentTurn == null ||
      currentTurn.id !== player.id ||
      board[x][y] !== "-"
    )
      return;

    const playerMove = { x, y, playerId: player.id };

    stompClient.publish({
      destination: `/app/move/${game.gameId}`,
      body: JSON.stringify(playerMove),
    });
  };

  useEffect(() => {
    if (game) {
      setBoard(game.board);
    }
  }, [game]);

  return (
    <div className="game-container">
      <div className="game-info">
        <p>
          Player: {player.playerName} (ID: {player.id})
        </p>
        {!game ? (
          <div className="button-group">
            {isWebSocketConnected ? (
              <>
                <button onClick={startNewGame}>Start New Game</button>
                <button onClick={() => setIsJoiningGame(true)}>
                  Join Game
                </button>
              </>
            ) : (
              "Waiting for connection"
            )}
          </div>
        ) : (
          <div>
            <b>Game Id: </b> {game.gameId}
            {!currentTurn ? (
              <>
                <p>Share above game id to others for joining game</p>
                <p>Minimum two players required to start game</p>
              </>
            ) : (
              ""
            )}
            <div className="turn-indicator">
              {currentTurn && player && currentTurn?.id === player.id
                ? "Your turn"
                : currentTurn
                ? `${currentTurn?.playerName}'s turn`
                : ""}
            </div>
          </div>
        )}
      </div>

      {isJoiningGame && !game && (
        <div className="join-game-container">
          <input
            type="text"
            placeholder="Enter Game ID"
            value={gameIdInput}
            onChange={(e) => setGameIdInput(e.target.value)}
            className="game-id-input"
          />
          <button onClick={joinExistingGame} className="join-game-button">
            Join Game
          </button>
        </div>
      )}

      {game ? (
        <div className="board">
          {board.map((row, x) =>
            row.map((cell, y) => (
              <button key={`${x}-${y}`} onClick={() => handleMove(x, y)}>
                {cell || "-"}
              </button>
            ))
          )}
        </div>
      ) : null}

      {game && game.winner && (
        <p className="winner-message">
          Winner is: {game.winner.playerName}, hit refresh to start another game
        </p>
      )}
      {game && game.gameState === "DRAW" && (
        <p className="draw-message">
          Game ended in a draw! , hit refresh to start another game
        </p>
      )}
    </div>
  );
}

export default GameBoard;
