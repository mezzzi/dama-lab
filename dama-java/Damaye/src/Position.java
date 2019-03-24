import java.io.Serializable;


/**
 * This class represents a position object
 * @author mzm002
 *
 */
public class Position implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1125279130832381495L;
	//fields
	private int x_Coor = 0;
	private int y_Coor = 0;
	private boolean IS_LEGAL = false;
	private Corki CORKI;



	private PositionGraphics posGraph ;
	
	/**
	 * @return the posGraph
	 */
	public PositionGraphics getPosGraph() {
		return posGraph;
	}

	/**
	 * @param posGraph the posGraph to set
	 */
	public void setPosGraph(PositionGraphics posGraph) {
		this.posGraph = posGraph;
	}

	/**
	 * 
	 * @param x x_coor
	 * @param y y_coor
	 * @param isLegal true if it is legal for a Corki to sit on the position
	 * @param corki Corki on the position
	 */
	public Position(int x, int y, boolean isLegal) {
		this.x_Coor = x;
		this.y_Coor = y;
		this.IS_LEGAL = isLegal;
		this.CORKI = null;

	}
	
	/**
	 * 
	 * @param pos
	 * @param isLegal
	 */
	public Position(int[] pos, boolean isLegal) {
		this.x_Coor = pos[0];
		this.y_Coor = pos[1];
		this.IS_LEGAL = isLegal;
		this.CORKI = null;

	}
	
	/**
	 * 
	 * @param pos the position to copy
	 */
	public Position(Position pos) {
		this.x_Coor = pos.X();
		this.y_Coor = pos.Y();
		this.IS_LEGAL = pos.isLegal();
		this.CORKI = null;

	}

	/**
	 * Checks if there is a corki at this pos
	 * @return true if there is already a Corki at this pos
	 */
	public boolean hasCorki() {
		return this.CORKI != null;
	}
	/**
	 * @return the Corki that is resting on this position.
	 */
	public Corki getCorki() {
		return CORKI;
	}

	/**
	 * @param corki the Corki to set to this position
	 */
	public void setCorki(Corki corki) {
		CORKI = corki;
		if(corki != null) {
			corki.setPos(this);
		}
	}
	
	/**
	 * removes the corki pointer
	 */
	public void removeCorki() {
		this.CORKI = null;
		
	}
	
	/**
	 * @return the x_Coor
	 */
	public int X() {
		return x_Coor;
	}

	/**
	 * @return the y_Coor
	 */
	public int Y() {
		return y_Coor;
	}

	/**
	 * @return true is position is a legal position for a Corki to sit on.
	 */
	public boolean isLegal() {
		return IS_LEGAL;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object ob) {
		if(!(ob instanceof Position) ) {
			return false;
		} else {
			Position obj = (Position)ob;
			if((obj.X() == this.x_Coor) && (obj.Y() == this.y_Coor) && (obj.isLegal() == this.IS_LEGAL)) {
				return true;
			}
		}
		return false;
	}
	//String representation of the position without the Corki on it
	public String toString() {
		
		String str = "";
		str += "[ (x, y) = ("+this.x_Coor+", "+this.y_Coor+"), " ;
		str += "isLegal: "+this.IS_LEGAL+" ]";
		
		return str;
	}

	//tired of typing
	private static void out(String string) {
		System.out.println(string);
		
	}
	//local testing purpose
	public static void main(String[] args) {

		Position pos = new Position(3,4,true);
		out("pos with null corki: "+pos.toString());
		
		Position pos1 = new Position(5,6,true);
		out("pos1 with null corki: "+pos1.toString());
		
		Corki corki = new Corki(true, true, pos);
		out("\ncorki with position pos: "+corki.toString());
		out("Tsting if pos has a corki: "+pos.hasCorki());
		
		corki.moveToPos(pos1);
		out("\ncorki moved to pos1"+corki.toString());
		out("Tsting if pos has a corki: "+pos.hasCorki());
		out("Tsting if pos1 has a corki: "+pos1.hasCorki());
		
		Position pos2 = new Position(3,4,false);
		out("\npos2 with null corki and illegal pos: "+pos2.toString());
		
		corki.moveToPos(pos2);
		out("corki try to move to pos2, lets if it moved: "+corki.toString());
		
		Position pos3 = new Position(pos2);
		out("\npos3 copy constructed out of pos2: "+pos3.toString());

		if(pos2.equals(pos1)) {
			out("pos3 and pos2 are equal, equals method tested");
		}
	}

	

}
