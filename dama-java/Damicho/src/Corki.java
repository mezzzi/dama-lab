/**
 * 
 */


/**
 * This class encapsulates a Corki object
 * @author mzm002
 *
 */
public class Corki {

	/**
	 * @param pOSITION the pOSITION to set
	 */
	public void setPos(Position pos) {
		this.POSITION = pos;
	}

	//instance fields
	private boolean IS_KING = false;
	private boolean IS_TYPE_A = false;
	private Position POSITION;
	
	//constructor
	public Corki(boolean isTypeA, boolean isKing, Position pos) {
		this.IS_KING = isKing;
		this.IS_TYPE_A = isTypeA;
		this.POSITION = pos;
		POSITION.setCorki(this); //make sure the position refers back to the corki
	}
	
	//Copy constructor, this really may not be needed at all.
	public Corki(Corki corki) {
		this.IS_KING = corki.isKing();
		this.IS_TYPE_A = corki.isTypeA();
		this.POSITION = new Position(corki.getPos());
		POSITION.setCorki(this);
	}

	/**
	 * @return the current position of the Corki
	 */
	public Position getPos() {
		return POSITION;
	}

	/**
	 * 
	 * @param pos
	 * @return true is pos is set properly, false if setting pos fails
	 */
	public boolean moveToPos(Position pos) {
		
		//remove the corki of current position
		POSITION.removeCorki();
		
		//makes sure a position that is illegal or already has a corki is not assigned
		if(pos.isLegal() && !pos.hasCorki()) {
			POSITION = pos;
			POSITION.setCorki(this); //also make the new pos refer to this corki
			return true;
		} 
		
		//error message
		if(pos.hasCorki()) {
			out("Cant assign corki to position that hs a corki: "+pos.getCorki().toString());
		} else {
			out("Cant assign corki to illegal position: "+pos.toString());
		}
		
		return false;
	}



	/**
	 * @return true if the Corki is King
	 */
	public boolean isKing() {
		return IS_KING;
	}

	/**
	 * @param if true the Corki will be king
	 */
	public void setAsKing(boolean isKing) {
		IS_KING = isKing;
	}

	/**
	 * @return true if Corki belongs to Computer player
	 */
	public boolean isTypeA() {
		return IS_TYPE_A;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object ob) {
		
		if(!(ob instanceof Corki) ) {
			return false;
		} else {
			Corki obj = (Corki)ob;
			
			//for equality position's has to be identical
			if((obj.isTypeA() == this.IS_TYPE_A) && (obj.isKing() == this.IS_KING) && (obj.getPos().equals(this.POSITION))) {
				return true;
			}
		}
		return false;
		
	}
	
	@Override
	public String toString() {
		
		String str = "";
		
		if(IS_KING) {
			str += "[Status:King, ";
		} else {
			str += "[Status:Ordinary, ";
		}
		
		
		str += "Position:"+POSITION.toString()+"]";
		
		return str;
		
	}
	
	//tired of typing
	private void out(String string) {
		System.out.println(string);
		
	}

}
