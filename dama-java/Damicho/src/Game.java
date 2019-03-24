import java.util.ArrayList;

/**
 * @author mzm002
 *
 */
public class Game {

	private Board board;
	private Player player1;
	private Player player2;
	private GameLoader gameLoader;

	public Game(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		this.board = new Board();
		createPlayers();
	}

	private void createPlayers() {
		if(gameLoader.isHumanToHuman()) {
			player1 = new Player(gameLoader.isTank(),GameLoader.PlayerType.PLAYER1,gameLoader.getPlayer1(),
					gameLoader.getUp().equals(GameLoader.PlayerType.PLAYER1),
					board,true);
			player2 = new Player(gameLoader.isTank(),GameLoader.PlayerType.PLAYER2,gameLoader.getPlayer2(),
					gameLoader.getUp().equals(GameLoader.PlayerType.PLAYER2),
					board,false);
		} else {
			player1 = new Player(gameLoader.isTank(),GameLoader.PlayerType.HUMN,gameLoader.getPlayer1(),
					gameLoader.getUp().equals(GameLoader.PlayerType.HUMN),
					board,true);
			player2 = new Player(gameLoader.isTank(),GameLoader.PlayerType.COMP,gameLoader.getPlayer2(),
					gameLoader.getUp().equals(GameLoader.PlayerType.COMP),
					board,false);
		}
	}

	public Board getBoard() {
		return board;
	}

	/**
	 * @return the player1
	 */
	public Player getPlayer1() {
		return player1;
	}

	/**
	 * @param player1 the player1 to set
	 */
	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	/**
	 * @return the player2
	 */
	public Player getPlayer2() {
		return player2;
	}

	/**
	 * @param player2 the player2 to set
	 */
	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	/**
	 * @param board the board to set
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	public GameLoader.Result doYourTurn(GameLoader.PlayerType currentPlayer) {

		return getPlayer(currentPlayer).doYourTurn();
		
	}

	public Player getPlayer(GameLoader.PlayerType player) {
		
		if(player == GameLoader.PlayerType.HUMN || player == GameLoader.PlayerType.PLAYER1) {
			return player1;
		} else if(player == GameLoader.PlayerType.COMP || player == GameLoader.PlayerType.PLAYER2) {
			return player2;
		}
		
		return null;
		
	}

	public ArrayList<Position> getNextLegalPositions(GameLoader.PlayerType currentPlayer,
			PositionGraphics firstPosGraph) {
		return getPlayer(currentPlayer).getNextLegalPositions(firstPosGraph);
	}

	public GameLoader.Result doYourTurn(GameLoader.PlayerType currentPlayer,
			PositionGraphics firstPosGraph, PositionGraphics secondPosGraph) {
				return getPlayer(currentPlayer).doYourTurn(firstPosGraph,secondPosGraph);
		
	}

	public ArrayList<Corki> getMovableCorkis(GameLoader.PlayerType currentPlayer) {
		return getPlayer(currentPlayer).getMovableCorkis();
	}

	public void undoMove(GameLoader.PlayerType currentPlayer) {
		getPlayer(currentPlayer).undoMove();
	}


}
