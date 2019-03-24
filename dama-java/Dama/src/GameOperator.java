import java.util.ArrayList;

/**
 * @author mzm002
 *
 */
public class GameOperator {

	private Board board;
	private Player player1;
	private Player player2;
	private GameLoader gameLoader;
	
	
	private ArrayList<ArrayList<Mind.moveHistory>> undoList =
		new ArrayList<ArrayList<Mind.moveHistory>>();

	public GameOperator(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		this.board = new Board();
		createPlayers();
	}

	private void createPlayers() {
		if(gameLoader.isHumanToHuman()) {
			player1 = new Player(gameLoader.isTank(),GameLoader.PlayerType.PLAYER1,gameLoader.getPlayer1(),
					gameLoader.getUp().equals(GameLoader.PlayerType.PLAYER1),
					board,true,null);
			player2 = new Player(gameLoader.isTank(),GameLoader.PlayerType.PLAYER2,gameLoader.getPlayer2(),
					gameLoader.getUp().equals(GameLoader.PlayerType.PLAYER2),
					board,false,null);
			
			player1.getMind().setUndoList(undoList);
			player2.getMind().setUndoList(undoList);
			player1.setOpponent(player2);
			player2.setOpponent(player1);
			
			
			
		} else {
			player1 = new Player(gameLoader.isTank(),GameLoader.PlayerType.HUMN,gameLoader.getPlayer1(),
					gameLoader.getUp().equals(GameLoader.PlayerType.HUMN),
					board,true,null);
			player2 = new Player(gameLoader.isTank(),GameLoader.PlayerType.COMP,gameLoader.getPlayer2(),
					gameLoader.getUp().equals(GameLoader.PlayerType.COMP),
					board,false,null);
		}
	}


	/**
	 * @param teterByTeter the teterByTeter to set
	 */
	public void setTeterByTeter(boolean teterByTeter) {
		player1.setTeterBeTeterFirst(teterByTeter);
		player2.setTeterBeTeterFirst(teterByTeter);

	}



	/**
	 * @param forceToEat the forceToEat to set
	 */
	public void setForceToEat(boolean forceToEat) {
		player1.setForceToEat(forceToEat);
		player2.setForceToEat(forceToEat);

	}



	/**
	 * @param noForcing the noForcing to set
	 */
	public void setNoForcing(boolean noForcing) {
		player1.setNoForcing(noForcing);
		player2.setNoForcing(noForcing);

	}



	/**
	 * @param enableEfita the enableEfita to set
	 */
	public void setEnableEfita(boolean enableEfita) {
		player1.setEfitaEnabled(enableEfita);
		player2.setEfitaEnabled(enableEfita);

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

	public boolean undoMove(GameLoader.PlayerType currentPlayer) {
		return getPlayer(currentPlayer).undoMove();
	}


}
