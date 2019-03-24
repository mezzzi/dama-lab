
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class encapsulates the general behaviors of a player object
 * @author mzm002
 *
 */
public class Player implements Serializable{


	/**
	 * fields
	 */
	private static final long serialVersionUID = 7790688138582187984L;
	private boolean isOnTop;
	private ArrayList<Corki> corkiList;
	private Board board;
	private boolean isTypeA;
	private boolean efitaEnabled;
	private boolean teterBeTeterFirst;
	private boolean forceToEat;
	private boolean noForcing;
	private String name;
	private GameLoader.PlayerType playerType;
	private boolean isTank;
	private Player opponent;
	private boolean justAte;
	private transient Mind mind;


	public Player(boolean isTank,GameLoader.PlayerType playerType, 
			String name, boolean isOnTop, Board board, boolean isTypeA, 
			ArrayList<Corki> corkList) {
		
		this.name = name;
		this.isOnTop = isOnTop;
		this.board = board;
		
		if(corkList != null) {
			this.corkiList = corkList;
		} else {
			this.corkiList = new ArrayList<Corki>();

		}
		this.isTypeA = isTypeA;
		this.playerType = playerType;
		this.isTank = isTank;
		
		
		//lay out corkis
		if(board != null) {
			layOutCorkis();
		} else {
			out("Null board, corkis not laid out");
		}
		
		this.mind = new Mind(this);
		
	}
	
	
	/**
	 * assign corkis to a respective position & update corkiList
	 */
	private void layOutCorkis() {
		Position[][] positions = board.getPositions();

		if(corkiList.size() == 0) {
			if(isOnTop) {
				for(int i=0; i<3; i++) {
					for(int j=0; j<8; j++) {
						if(positions[j][i].isLegal()) {
							corkiList.add(new Corki(isTypeA,false,positions[j][i]));
						}
					}
				}
			} else {
				for(int i=7; i>4; i--) {
					for(int j=0; j<8; j++) {
						if(positions[j][i].isLegal()) {
							corkiList.add(new Corki(isTypeA,false,positions[j][i]));
						}
					}
				}
			}
		} else {
			if(isOnTop) {
				for(int i=0; i<8; i++) {
					for(int j=0; j<8; j++) {
						if(positions[j][i].isLegal()) {
							for(Corki cork: corkiList) {
								if(cork.getPos().equals(positions[j][i])) {
									positions[j][i].setCorki(cork);
								}
							}
						}
					}
				}
			} else {
				for(int i=7; i>=0; i--) {
					for(int j=0; j<8; j++) {
						if(positions[j][i].isLegal()) {
							for(Corki cork: corkiList) {
								if(cork.getPos().equals(positions[j][i])) {
									positions[j][i].setCorki(cork);
								}
							}
						}
					}
				}
			}
		}
		
	}

	private void out(String string) {
		System.out.println(string);
		
	}
	
	/**
	 * @return the justAte
	 */
	public boolean isJustAte() {
		return justAte;
	}


	/**
	 * @param justAte the justAte to set
	 */
	public void setJustAte(boolean justAte) {
		this.justAte = justAte;
	}



	public String toString() {
		String str = "";
		
		str += name+"\n";
		for(int i =0; i<corkiList.size(); i++) {
			str+=corkiList.get(i).toString()+"\n";
		}
		
		return str;
	}


	public Mind getMind() {
		return mind;
	}
	public GameLoader.Result doYourTurn() {
		return mind.doYourTurn();
	}



	public GameLoader.Result doYourTurn(PositionGraphics firstPosGraph,
			PositionGraphics secondPosGraph) {
		return mind.doYourTurn(firstPosGraph,secondPosGraph);
	}



	public ArrayList<Position> getNextLegalPositions(PositionGraphics firstPosGraph) {
		return mind.getNextLegalsSequentially(firstPosGraph.getPos().getCorki());
	}



	public boolean undoMove() {
		return mind.undoMove();
	}



	public ArrayList<Corki> getMovableCorkis() {
		return mind.getMovableCorkis();
	}
	
	public int getNumTeter() {
		int counter =0;
		for(Corki cork:corkiList) {
			if(!cork.isKing()) {
				counter++;
			}
		}
		
		return counter;
	}
	
	public int getNumKing() {
		int counter =0;
		for(Corki cork:corkiList) {
			if(cork.isKing()) {
				counter++;
			}
		}
		
		return counter;
	}
	
	/**
	 * @return the isOnTop
	 */
	public boolean isOnTop() {
		return isOnTop;
	}

	/**
	 * @param isOnTop the isOnTop to set
	 */
	public void setOnTop(boolean isOnTop) {
		this.isOnTop = isOnTop;
	}

	/**
	 * @return the corkiList
	 */
	public ArrayList<Corki> getCorkiList() {
		return corkiList;
	}

	/**
	 * @param corkiList the corkiList to set
	 */
	public void setCorkiList(ArrayList<Corki> corkiList) {
		this.corkiList = corkiList;
	}

	/**
	 * @return the board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * @param board the board to set
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * @return the isTypeA
	 */
	public boolean isTypeA() {
		return isTypeA;
	}

	/**
	 * @param isTypeA the isTypeA to set
	 */
	public void setTypeA(boolean isTypeA) {
		this.isTypeA = isTypeA;
	}

	/**
	 * @return the efitaEnables
	 */
	public boolean isEfitaEnabled() {
		return efitaEnabled;
	}

	/**
	 * @param efitaEnables the efitaEnables to set
	 */
	public void setEfitaEnabled(boolean efitaEnables) {
		this.efitaEnabled = efitaEnables;
	}

	/**
	 * @return the teterBeTeterFirst
	 */
	public boolean isTeterBeTeterFirst() {
		return teterBeTeterFirst;
	}

	/**
	 * @param teterBeTeterFirst the teterBeTeterFirst to set
	 */
	public void setTeterBeTeterFirst(boolean teterBeTeterFirst) {
		this.teterBeTeterFirst = teterBeTeterFirst;
	}

	/**
	 * @return the forceToEat
	 */
	public boolean isForceToEat() {
		return forceToEat;
	}

	/**
	 * @param forceToEat the forceToEat to set
	 */
	public void setForceToEat(boolean forceToEat) {
		this.forceToEat = forceToEat;
	}

	/**
	 * @return the noForcing
	 */
	public boolean isNoForcing() {
		return noForcing;
	}

	/**
	 * @param noForcing the noForcing to set
	 */
	public void setNoForcing(boolean noForcing) {
		this.noForcing = noForcing;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the playerType
	 */
	public GameLoader.PlayerType getPlayerType() {
		return playerType;
	}

	/**
	 * @param playerType the playerType to set
	 */
	public void setPlayerType(GameLoader.PlayerType playerType) {
		this.playerType = playerType;
	}

	/**
	 * @return the isTank
	 */
	public boolean isTank() {
		return isTank;
	}

	/**
	 * @param mind the mind to set
	 */
	public void setMind(Mind mind) {
		this.mind = mind;
	}
	
	/**
	 * @param isTank the isTank to set
	 */
	public void setTank(boolean isTank) {
		this.isTank = isTank;
	}	
	
	public void setOpponent(Player opp) {
		opponent = opp;
	}
	
	public Player getOpponent() {
		return opponent;
	}




}
