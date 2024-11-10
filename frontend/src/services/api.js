import axios from 'axios';

const API_URL = 'http://34.203.209.206/backend'; // Replace with your backend URL
// const API_URL = "http://backend:8080"; // Use 'backend' as the hostname

// Register a new player
export const registerPlayer = async (playerName) => {
    const response = await axios.post(`${API_URL}/player`, { playerName,id:null });
    return response.data; // Returns { id, playerName }
};

// Create a new game
export const createGame = async (player) => {
    const response = await axios.post(`${API_URL}/game/tic-tac-toe/new-game`, player);
    return response.data;
};

// Join an existing game
export const joinGame = async (joinGameRequest) => {
    const response = await axios.post(`${API_URL}/game/tic-tac-toe/join-game`, joinGameRequest);
    return response.data;
};

export const getGamesPlayed = async () => {
    const response = await axios.get(`${API_URL}/game/tic-tac-toe/games-played`);
    return response.data?.gamesPlayed;
  };

// Validate username
export const validateUsername = async (playerName) => {
    const response = await axios.post(`${API_URL}/player/validate-username`, { playerName });
    return response.data;
};
