import logo from "./logo.svg";
import "./App.css";
import TicTacToe from "./component/TicTacToe/TicTacToe";

function App() {
  return (
    <div className="app-container">
      <h1 className="app-title">Tic-Tac-Toe Game</h1>
      <TicTacToe />
    </div>
  );
}

export default App;
