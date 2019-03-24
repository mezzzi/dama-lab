
import java.util.ArrayList;

/**
 * This class encapsulates the general behaviors of a player object
 * @author mzm002
 *
 */
public class Player {


	protected boolean isOnTop;
	protected ArrayList<Corki> corkiList;
	protected Board board;
	protected boolean isTypeA;
	protected String name;
	protected GameLoader.PlayerType playerType;
	protected boolean isTank;
	/**
	 * @param isTank the isTank to set
	 */
	public void setTank(boolean isTank) {
		this.isTank = isTank;
	}



	private Mind mind;
	
	
	protected Player(boolean isTank,GameLoader.PlayerType playerType, String name, boolean isOnTop, Board board, boolean isTypeA) {
		
		this.name = name;
		this.isOnTop = isOnTop;
		this.board = board;
		this.corkiList = new ArrayList<Corki>();
		this.isTypeA = isTypeA;
		this.playerType = playerType;
		this.isTank = isTank;
		
		
		//lay out corkis
		if(board != null) {
			layOutCorkis();
		} else {
			out("Null board, corkis not laid out");
		}
		
		if(isTank) {
			this.mind = new TankegnaMind(this);
		} else {
			this.mind = new EgregnaMind(this);
		}
		
	}
	
	

	/**
	 * assign corkis to a respective position & update corkiList
	 */
	private void layOutCorkis() {
		Position[][] positions = board.getPositions();
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
	}

	private void out(String string) {
		System.out.println(string);
		
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
		return mind.getNextLegalPositions(firstPosGraph,null,true);
	}



	public void undoMove() {
		mind.undoMove();
	}



	public ArrayList<Corki> getMovableCorkis() {
		return mind.getMovableCorkis();
	}
	


}
