import java.io.Serializable;




/**
 * 
 */

/**
 * This class represents a dama board object
 * @author mzm002
 *
 */
public class Board implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3604760326318176472L;
	private Position[][] positions;
	private int numRows;
	private int numColumns;
	
	/**
	 * @param positions
	 * @param numRows
	 * @param numColumns
	 */
	public Board(int numRows, int numColumns) {
		this.numRows = numRows;
		this.numColumns = numColumns;
		this.positions = new Position[numRows][numColumns];	
		setPositions();
	}
	
	/**
	 * default
	 */
	public Board() {
		this.numRows = 8;
		this.numColumns = 8;
		this.positions = new Position[numRows][numColumns];	
		setPositions();
	}

	/**
	 * instantiates the position objects
	 */
	public void setPositions() {
		
		//not j is x coordinate
		for(int i=0; i<numColumns; i++) {
			
			for(int j=0; j<numRows; j++) {
				//watch out for the condition
				if((j%2 ==1 && i%2==0) || (j%2==0 && i%2 ==1)) {
					positions[j][i] = new Position(j,i,true);
				} else {
					positions[j][i] = new Position(j,i,false);
				}
			}
		}
	}

	/**
	 * 
	 * @return the collection of position objects
	 */
	public Position[][] getPositions() {
		return positions;
	}
	
	public int getNumColumns() {
		return numColumns;
		
	}
	
	public int getNumRows() {
		return numRows;
		
	}
	
	public String toString() {
		String str = "";
		for(int i=0; i<numColumns; i++) {
			for(int j=0; j<numRows; j++) {
				str += positions[j][i].toString();
				if(j==numRows-1) {
					str += "\n";
				}
			}
		}
		
		return str;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Board brd = new Board();
		out(brd.toString());

	}

	private static void out(String string) {
		System.out.println(string);
		
	}



}
