package dama;

import java.util.Arrays;

/*
__________________________________________
The Board class models a dama board object
__________________________________________
*/
public class Board {

    // total number of rows in the dama board(8x8 board in this case), 
    // which is also equal with number of columnsapt install default-jre
    public static final int NUM_ROWS = 8;
    // the number of home rows for a given player when the game begins, 
    // it is 3 for an 8x8 dama board
    public static final int NUM_HOME_ROWS = 3;

    // Corki values
    public static final int ILLEGAL_CELL = 0; // 0
    public static final int EMPTY_LEGAL_CELL = 1; // 1
    public static final int TETER_TOP = 3; // 3
    public static final int KING_TOP = TETER_TOP + 1; // 4
    public static final int TETER_BOTTOM = KING_TOP + 4; // 8
    public static final int KING_BOTTOM = TETER_BOTTOM + 1; // 9

    // 2D int arrays serving as a data model of the board.
    private final int[][] board = new int[NUM_ROWS][NUM_ROWS];
    private final char[][] cellValues = new char[NUM_ROWS][NUM_ROWS];

    // Constructor of the Board class 
    public Board() {
        // instantiates the board
        populateBoard();        
        // assign unique cell values
        assignUniqueCellValues();
        
    } // end of constructor

    final public void populateBoard(){
        // initialize the board, set the legal and illegal cells
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_ROWS; col++) {
                this.board[row][col] = (row + col) % 2;
            }
        }
        
        /*
        lay out the start cokis on the board; 24 in total, 12 for top
      	player and another 12 for the bottom player. 
         */
        for (int row = 0; row < NUM_HOME_ROWS; row++) {
            for (int col = 0; col < NUM_ROWS; col++) {
                if (this.board[row][col] == EMPTY_LEGAL_CELL) {
                        this.board[row][col] = TETER_TOP;
                        this.board[row + NUM_ROWS - NUM_HOME_ROWS]
                                [NUM_ROWS - 1 - col] = TETER_BOTTOM;
                }
            }
        }         
    }
    
    /**
     * assigns unique values to legal board cells, useful
     * in a multi player mode
     */
    final void assignUniqueCellValues(){
        char lastCorkiValue = 'a';
        char lastEmptyValue = '1';
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_ROWS; col++) { 
                if(isOccupied(row, col)) {
                    cellValues[row][col] = lastCorkiValue++;
                } else if (isEmptyLegalCell(row, col)) {
                    cellValues[row][col] = lastEmptyValue++;
                }
            }
        } // end of for loop         
    }
    
    /*
    =================================================
    ========= HELPER METHOD =========================
    =================================================
     */
    
    // returns true if (row, col) lies with in an 8x8 board
    public boolean isInsideBoard(int row, int col) {
        return (0 <= row && row < NUM_ROWS) && (0 <= col && col < NUM_ROWS);
    }

    public boolean isInsideBoard(int[] cell) {
        return this.isInsideBoard(cell[0], cell[1]);
    }

    /*
    the next position to be encountered
    when diagonally moving from first cell to second cell
    */
    public int[] getNextCell(int[] first_cell, int[] second_cell) {
        int[] next_cell = new int[2];
        int row1 = first_cell[0], col1 = first_cell[1], 
                row2 = second_cell[0], col2 = second_cell[1];
        next_cell[0] = row2 + (row2 - row1);
        next_cell[1] = col2 + (col2 - col1);
        if(!isInsideBoard(next_cell))
            return null;
        return next_cell;
    }
    
    public int[] getTankegnaKingsPrey(int[] fromCell, int[] landingCell){
        int rowDirection = landingCell[0] > fromCell[0] ? 1 : -1;
        int colDirection = landingCell[1] > fromCell[1] ? 1 : -1;
        int[] curCell = fromCell.clone();
        int[] prey = null;
        while(true){
            curCell[0] += rowDirection;
            curCell[1] += colDirection;
            if(Arrays.equals(landingCell, curCell))
                break;
            if(isOccupied(curCell)) {
                if(areTeamMates(curCell, fromCell)) 
                    return null;                
                if(prey != null) 
                    return null;
                else 
                    prey = curCell.clone();                
            }            
        }
        return prey;
    }
    
    public boolean isEmptyInBetween(int[] fromCell, int[] landingCell){
        int rowDirection = landingCell[0] > fromCell[0] ? 1 : -1;
        int colDirection = landingCell[1] > fromCell[1] ? 1 : -1;
        int[] curCell = fromCell.clone();
        while(!Arrays.equals(landingCell, curCell)){
            curCell[0] += rowDirection;
            curCell[1] += colDirection;
            if(isOccupied(curCell))
                return false;         
        }
        return true;        
    }
    
    public boolean areDiagonals(int[] firstCell, int[] secondCell, int dist){
        return Math.abs(firstCell[0] - secondCell[0]) == 
                dist && Math.abs(firstCell[1] - secondCell[1]) == dist;
    }
    
    boolean areDiagonals(int[] firstCell, int[] secondCell) {
        return Math.abs(firstCell[0] - secondCell[0]) == 
                Math.abs(firstCell[1] - secondCell[1]);
    }    

    // yeah, literally returns the coordinates of the middle cell
    public int[] getMiddleCell(int[] start_cell, int[] landing_cell) {
        int[] mid_cell = new int[2]; 
        int row1 = start_cell[0], col1 = start_cell[1], 
                row2 = landing_cell[0], col2 = landing_cell[1];
        mid_cell[0] = ((row2 > row1) ? (row1 + 1) : (row1 - 1));
        mid_cell[1] = ((col2 > col1) ? (col1 + 1) : (col1 - 1));
        if(!isInsideBoard(mid_cell))
            return null;        
        return mid_cell;
    }

    /*
     returns true if a teter corki can move in its forward direction from 
     start cell to landing cell
     */
    public boolean isForwardToTeter(int[] start_cell, int[] landing_cell) {      
        int row1 = start_cell[0], col1 = start_cell[1], row2 = landing_cell[0];
        if (this.isTeterTop(row1, col1)) {
            return row2 > row1;
        } else {
            return row2 < row1;
        }
    }

    /*
     returns true if corkis at positions cell1 and cell2
     are on the same team(owned by the same player)
     */
    public boolean areTeamMates(int[] cell1, int[] cell2) {      
        int row1 = cell1[0], col1 = cell1[1], row2 = cell2[0], col2 = cell2[1];
        if (this.isBottom(row1, col1)) {
            return this.isBottom(row2, col2);
        } else if (this.isTop(row1, col1)){
            return this.isTop(row2, col2);
        } else {
            return false;
        }
    }

    // returns true if row, col is occupied by any corki
    public boolean isOccupied(int row, int col) {
        return (!(this.isEmptyLegalCell(row, col) || this.isIllegalCell(row, col)));
    }

    public boolean isOccupied(int[] cell) {
        return this.isOccupied(cell[0], cell[1]);
    }

    // returns true if corki at given cell should be made king
    public boolean shouldKingify(int row, int col) {                       
        boolean should_be_king = this.isTeter(row, col) && 
                ((row == 0 && this.isTeterBottom(row, col))
                || (row == (NUM_ROWS - 1) && this.isTeterTop(row, col)));
        return should_be_king;
    }

    public boolean shouldKingify(int[] cell) {
        return this.shouldKingify(cell[0], cell[1]);
    }

    // moves corki from start cell to landing cell
    public void moveCorki(int[] start_cell, int[] landing_cell) {      
        int row1 = start_cell[0], col1 = start_cell[1], 
                row2 = landing_cell[0], col2 = landing_cell[1];
        this.board[row2][col2] = this.board[row1][col1];
        this.makeEmpty(row1, col1);
    }

    /*
    =======================================
    ===========  setters ==================
    =======================================
     */

    public char getUniqueIdentifier(int row, int col){
        return cellValues[row][col];
    }
    
    public char getUniqueIdentifier(int[] cell){
        return cellValues[cell[0]][cell[1]];
    }         
    
    // gets the cell coordinates corresponding to the given unique identifier
    public int[] getCellCoordinates(char identifier){
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_ROWS; col++) {
                if(cellValues[row][col] == identifier){
                    return new int[] {row, col};
                }
            }
        }        
        return null;
    }
    
    public int getCellValue(int row, int col) {
        return this.board[row][col];
    }

    public int getCellValue(int[] cell) {
        return this.getCellValue(cell[0], cell[1]);
    }

    public boolean isBottom(int row, int col) {
        return this.isTeterBottom(row, col) || this.isKingBottom(row, col);
    }

    public boolean isBottom(int[] cell) {
        return this.isBottom(cell[0], cell[1]);
    }

    public boolean isTop(int row, int col) {
        return this.isTeterTop(row, col) || this.isKingTop(row, col);
    }

    public boolean isTop(int[] cell) {
        return this.isTop(cell[0], cell[1]);
    }

    public boolean isIllegalCell(int row, int col) {
        return this.getCellValue(row, col) == ILLEGAL_CELL;
    }

    public boolean isIllegalCell(int[] cell) {
        return this.isIllegalCell(cell[0], cell[1]);
    }

    public boolean isEmptyLegalCell(int row, int col) {
        return this.getCellValue(row, col) == EMPTY_LEGAL_CELL;
    }

    public boolean isEmptyLegalCell(int[] cell) {
        return this.isEmptyLegalCell(cell[0], cell[1]);
    }

    public boolean isKing(int row, int col) {
        int cell_value = this.getCellValue(row, col);
        return cell_value == KING_TOP || cell_value == KING_BOTTOM;
    }

    public boolean isKing(int[] cell) {
        return this.isKing(cell[0], cell[1]);
    }

    public boolean isTeter(int row, int col) {
        int cell_value = this.getCellValue(row, col);
        return cell_value == TETER_TOP || cell_value == TETER_BOTTOM;
    }

    public boolean isTeter(int[] cell) {
        return this.isTeter(cell[0], cell[1]);
    }

    public boolean isTeterTop(int row, int col) {
        return this.getCellValue(row, col) == TETER_TOP;
    }

    public boolean isTeterTop(int[] cell) {
        return this.isTeterTop(cell[0], cell[1]);
    }

    public boolean isTeterBottom(int row, int col) {
        return this.getCellValue(row, col) == TETER_BOTTOM;
    }

    public boolean isTeterBottom(int[] cell) {
        return this.isTeterBottom(cell[0], cell[1]);
    }

    public boolean isKingTop(int row, int col) {
        return this.getCellValue(row, col) == KING_TOP;
    }

    public boolean isKingTop(int[] cell) {
        return this.isKingTop(cell[0], cell[1]);
    }

    public boolean isKingBottom(int row, int col) {
        return this.getCellValue(row, col) == KING_BOTTOM;
    }

    public boolean isKingBottom(int[] cell) {
        return this.isKingBottom(cell[0], cell[1]);
    }

    /*
    =======================================
    ===========  getters ==================
    =======================================
     */

    public void setCellValue(int value, int row, int col) {
        this.board[row][col] = value;
    }

    public void setCellValue(int value, int[] cell) {
        this.setCellValue(value, cell[0], cell[1]);
    }

    public void makeEmpty(int row, int col) {
        this.setCellValue(EMPTY_LEGAL_CELL, row, col);
    }

    public void makeEmpty(int[] cell) {
        this.makeEmpty(cell[0], cell[1]);
    }

    public void makeTeterTop(int row, int col) {
        this.setCellValue(TETER_TOP, row, col);
    }

    public void makeTeterTop(int[] cell) {
        this.makeTeterTop(cell[0], cell[1]);
    }

    public void makeTeterBottom(int row, int col) {
        this.setCellValue(TETER_BOTTOM, row, col);
    }

    public void makeTeterBottom(int[] cell) {
        this.makeTeterBottom(cell[0], cell[1]);
    }

    public void makeKingTop(int row, int col) {
        this.setCellValue(KING_TOP, row, col);
    }

    public void makeKingTop(int[] cell) {
        this.makeKingTop(cell[0], cell[1]);
    }
    
    public void makeKingBottom(int row, int col) {
        this.setCellValue(KING_BOTTOM, row, col);
    }

    public void makeKingBottom(int[] cell) {
        this.makeKingBottom(cell[0], cell[1]);
    }

    public void makeKing(int row, int col) {
        if (this.isTeterTop(row, col)) {
            this.makeKingTop(row, col);
        } else {
            this.makeKingBottom(row, col);
        }
    }

    public void makeKing(int[] cell) {
        this.makeKing(cell[0], cell[1]);
    }

} // end of class

