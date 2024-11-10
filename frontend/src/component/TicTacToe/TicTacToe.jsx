import React, { useState } from "react";
import PlayerRegistration from "../PlayerRegistration";
import GameBoard from "../GameBoard/GameBoard";
import './TicTacToe.css';

function TicTacToe() {
  const [player, setPlayer] = useState(null);

  const handleRegister = (registeredPlayer) => {
    setPlayer(registeredPlayer);
  };

  return (
    <div className="tictactoe-container">
      {!player ? (
        <div className="registration">
          <PlayerRegistration onRegister={handleRegister} />
        </div>
      ) : (
        <div className="game-board-container">
          <GameBoard player={player} />
        </div>
      )}
    </div>
  );
}

export default TicTacToe;
