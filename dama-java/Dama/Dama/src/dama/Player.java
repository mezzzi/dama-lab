package dama;

import java.util.ArrayList;

public class Player {

    public static final int[][] NEIGHBOUR_OFFSETS = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};

    protected Board board;  // the game board that the player is playing on
    private final boolean isTop;  // player's position
    private final String name; // the name of the player
    private static boolean isEgregna = true; // Egregna 

    // constructor
    public Player(Board board, boolean isTop, String name){
        this.board = board;
        this.isTop = isTop;
        this.name = name;
    }
    
    // constructor
    public Player(Board board, boolean isTop){
        this(board, isTop, isTop ? "Top Player" : "Bottom Player");
    }
    
    public String getName(){
        return name;
    }
    
    public static void setIsEgregna(boolean isItEgregna){
        isEgregna = isItEgregna;
    }
    
    
    // calculates the number of corkis that a player has left
    // and returns  the number of corkis that a player has left on the board
    public int getNumCorkis() {
        int numCorkis = 0;
        for (int row = 0; row < Board.NUM_ROWS; row++) {
            for (int col = 0; col < Board.NUM_ROWS; col++ ) {
                if (isTop) {
                    numCorkis += board.isTop(row, col) ? 1 : 0;
                } else {
                    numCorkis += board.isBottom(row, col) ? 1 : 0;
                }
            }
        }
        return numCorkis;
    }
    
    // identifies the legal cells(eats and move destinations) that can be reached out from a given
    // corki and returns them wrappen in an array list of arrays, the array at index 0 containing
    // all the moves and the array at index 1 containing all the eats
    public ArrayList<int[]>[] movesAndEats(int row, int col)  {
        int[] curCell = {row, col};
        ArrayList<int[]>[] movesAndEats = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
        for(int[] offset: NEIGHBOUR_OFFSETS){
            int[] neighborCell = {row + offset[0], col + offset[1]};
            if (board.isInsideBoard(neighborCell)) {
                if (canMove(curCell, neighborCell)) {
                    movesAndEats[0].add(neighborCell);
                } else {
                    if (canEat(curCell, neighborCell)) {
                        int[] landingCell = board.getNextCell(curCell, neighborCell);
                        movesAndEats[1].add(landingCell);
                    }
                }
            }
        }
        return movesAndEats;
    }

    public ArrayList<int[]>[] movesAndEats(int[] cell)  {
        return movesAndEats(cell[0], cell[1]);
    }

    public ArrayList<int[]> getCanAdvanceCells() {
        ArrayList<int[]> canAdvanceCells = new ArrayList();
        for (int row = 0; row < Board.NUM_ROWS; row++) {
            for (int col = 0; col < Board.NUM_ROWS; col++) {
                if ((isTop && board.isTop(row, col) || ((!isTop) && board.isBottom(row, col)))) {
                    ArrayList[] movesAndEats = movesAndEats(row, col);
                    int numMoves = movesAndEats[0].size();
                    int numEats = movesAndEats[1].size();
                    if (numMoves > 0 || numEats > 0) {
                        int[] curCell = {row, col};
                        canAdvanceCells.add(curCell);
                    }
                }
            }
        }
        return canAdvanceCells;
    }

    void playOneTurn(int[] startCell, int[] landingCell, boolean isAnEatMove)  {
        if(isAnEatMove){           
            int[] target = (!Player.isEgregna) && board.isKing(startCell) ? 
                    board.getTankegnaKingsPrey(startCell, landingCell) : 
                    board.getMiddleCell(startCell, landingCell);
            board.makeEmpty(target);
        }
        board.moveCorki(startCell, landingCell);        
        if (board.shouldKingify(landingCell)) { // crown the new king, if there is one
            board.makeKing(landingCell);
        }
    }
    
    // decides 
    void playTurn(int[][] advance){
        int[] fromCell = advance[0]; int[] landingCell = advance[1];
        if(canEat(fromCell, landingCell)){
            playOneTurn(fromCell, landingCell, true);            
        } else {
            playOneTurn(fromCell, landingCell, false);
        }
    }


    public boolean canMove(int[] fromCell, int[] toCell)  {
        if(!isOwnCorki(fromCell)) {
            System.out.println("NOT YOUR CORKI");
            return false;
        }
        if((!Player.isEgregna) && board.isKing(fromCell)) {
            return board.isEmptyInBetween(fromCell, toCell);
        } else {
            if(!board.areDiagonals(fromCell, toCell, 1)) {
                return false;
            }
            return board.isEmptyLegalCell(toCell) &&
                    (board.isKing(fromCell) || 
                    board.isForwardToTeter(fromCell, toCell));            
        }
    }

    
    public boolean canEat(int[] fromCell, int[] landingCell){
        if(!isOwnCorki(fromCell))
            return false;        
        if((!Player.isEgregna) && board.isKing(fromCell)) {
            if(!board.areDiagonals(fromCell, landingCell))
                return false;
            else {
                int[] prey = board.getTankegnaKingsPrey(fromCell, landingCell);
                return board.isEmptyLegalCell(landingCell) && prey != null;
            }            
        } else {
            if(!board.areDiagonals(fromCell, landingCell, 2))
                return false;
            else {
                int[] nextCell = board.getMiddleCell(fromCell, landingCell);
                return canEatHelper(fromCell, nextCell, landingCell);
            }            
        }        
                   
    }
    
    public boolean canEatHelper(int[] fromCell, int[] nextCell, int[] landingCell)  {
        if (board.areTeamMates(fromCell, nextCell) || 
                !board.areDiagonals(fromCell, nextCell, 1)) {
            return false;
        } else {
            boolean landingCellOk = landingCell != null &&
                    board.isEmptyLegalCell(landingCell);
            boolean isEatAllowed;
            if (Player.isEgregna) {
                isEatAllowed = (board.isKing(fromCell) && board.isOccupied(nextCell)) ||
                    (board.isTeter(nextCell) && board.isForwardToTeter(fromCell, nextCell));
            } else { // tankegna Teter
                isEatAllowed = board.isOccupied(nextCell);
            }
            return landingCellOk && isEatAllowed;
        }
    }    
    
    public boolean isOwnCorki(int row, int col){
        return (isTop && board.isTop(row, col)) ||
               ((! isTop) && board.isBottom(row, col));
    }
    
    public boolean isOwnCorki(int[] cell){
        return isOwnCorki(cell[0], cell[1]);
    }    
    
}