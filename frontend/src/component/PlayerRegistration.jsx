import React, { useEffect, useState } from "react";
import {
  getGamesPlayed,
  registerPlayer,
  validateUsername,
} from "../services/api";

function PlayerRegistration({ onRegister }) {
  const [playerName, setPlayerName] = useState("");
  const [isValid, setIsValid] = useState(true);
  const [gamesPlayed, setGamesPlayed] = useState(0); // State to store games played count

  useEffect(() => {
    getGamesPlayed()
      .then((count) => setGamesPlayed(count))
      .catch((err) => console.log(err));
  }, []);

  const handleValidation = async () => {
    const { isValid } = await validateUsername(playerName);
    setIsValid(isValid);
  };

  const handleRegister = async () => {
    const { isValid } = await validateUsername(playerName);
    setIsValid(isValid);
    if (isValid) {
      const player = await registerPlayer(playerName); // `registerPlayer` now expects a name only
      onRegister(player); // Pass the `Player` object with `id` and `playerName`
    }
  };

  return (
    <div>
      <input
        type="text"
        value={playerName}
        onChange={(e) => setPlayerName(e.target.value)}
        onBlur={handleValidation}
        placeholder="Enter your name"
      />
      {!isValid && <p>Username already taken. Try another.</p>}
      <button onClick={handleRegister} disabled={!isValid}>
        Register
      </button>
      {(gamesPlayed) ? <div>Total games played: {gamesPlayed}</div> : ""}
    </div>
  );
}

export default PlayerRegistration;
