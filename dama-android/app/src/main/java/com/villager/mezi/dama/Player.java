package com.villager.mezi.dama;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {

    public static final int[][] NEIGHBOUR_OFFSETS = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};

    protected Board board;
    private boolean isTop;  // player's position
    private String name;
    private static boolean isEgregna = true;
    private static boolean allowEfita = true;
    private int[] lastMove;
    private ArrayList<int[]> couldHaveEatens;

    public Player(Board board, boolean isTop, String name){
        this.board = board;
        this.isTop = isTop;
        setName(name);
    }

    public Player(Board board, boolean isTop){
        this(board, isTop, isTop ? "Top Player" : "Bot Player");
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static void setIsEgregna(boolean isItEgregna){
        Player.isEgregna = isItEgregna;
    }

    public static boolean isEgregna(){return isEgregna; }


    public static boolean isEfitaAllowed(){
        return allowEfita;
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

    public boolean isTop() {
        return  isTop;
    }

    // identifies the legal cells(eats and move destinations) that can be reached out from a given
    // corki and returns them wrapper in an array list of arrays, the array at index 0 containing
    // all the moves and the array at index 1 containing all the eats
    public ArrayList<int[]>[] movesAndEats(int row, int col)  {
        int[] curCell = {row, col};
        ArrayList<int[]>[] movesAndEats = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};
        for(int[] offset: NEIGHBOUR_OFFSETS){
            int[] neighborCell = {row + offset[0], col + offset[1]};
            boolean isTankegnaKing = (!Player.isEgregna) && board.isKing(curCell);
            int[] prevNeighborCell = curCell;
            while(true) {
                if (board.isInsideBoard(neighborCell)) {
                    if (canMove(curCell, neighborCell)) {
                        movesAndEats[0].add(neighborCell);
                    } else {
                        int[] landingCell = isTankegnaKing ? board.getNextCell(prevNeighborCell,
                                neighborCell) : board.getNextCell(curCell, neighborCell);
                        if(landingCell != null) {
                            if (canEat(curCell, landingCell)) {
                                movesAndEats[1].add(landingCell);
                            }
                        }
                    }
                }
                if(!isTankegnaKing){
                    break;
                } else {
                    int temp[] = neighborCell.clone();
                    neighborCell = board.getNextCell(prevNeighborCell, neighborCell);
                    prevNeighborCell = temp;
                    if(neighborCell == null) break;
                }
            }
        }
        return movesAndEats;
    }

    public ArrayList<int[]>[] movesAndEats(int[] cell)  {
        return movesAndEats(cell[0], cell[1]);
    }

    public ArrayList<int[]> advancesFromCell(int[] fromCell)  {
        ArrayList<int[]>[] advances= movesAndEats(fromCell[0], fromCell[1]);
        if((!allowEfita) && advances[1].size() > 0) return advances[1];
        ArrayList<int[]> combined = new ArrayList<>();
        for (int[] cell: advances[0]) {
            combined.add(cell);
        }
        for (int[] cell: advances[1]) {
            combined.add(cell);
        }
        return combined;
    }


    public ArrayList<int[]> getCanAdvanceCells() {
        ArrayList<int[]> canAdvanceCells = new ArrayList<>();
        ArrayList<int[]> canEatCells = new ArrayList<>();

        for (int row = 0; row < Board.NUM_ROWS; row++) {
            for (int col = 0; col < Board.NUM_ROWS; col++) {
                if ((isTop && board.isTop(row, col) || ((!isTop) && board.isBottom(row, col)))) {
                    ArrayList[] movesAndEats = movesAndEats(row, col);
                    int numMoves = movesAndEats[0].size();
                    int numEats = movesAndEats[1].size();
                    if (numMoves > 0 || numEats > 0) {
                        int[] cell = {row, col};
                        canAdvanceCells.add(cell);
                        if (numEats > 0) {
                            canEatCells.add(cell);
                        }
                    }
                }
            }
        }
        if(!allowEfita) {
            if(canEatCells.size() > 0) return canEatCells;
            else return canAdvanceCells;
        }
        else {
            couldHaveEatens = canEatCells;
            return canAdvanceCells;
        }
    }

    public int[] getEfita(){
        if(couldHaveEatens.size() > 0){
            int[] potentialEfita = couldHaveEatens.get(0);
            if(board.isEmptyLegalCell(potentialEfita)) return lastMove;
            else return potentialEfita;
        }
        else return null;
    }

    public void eatEfita(int[] efita){
        board.makeEmpty(efita);
    }

    public boolean[] playTurn(int[] startCell, int[] landingCell, int[] eatenCorki){
        boolean didEat = false, wasKingified = false;
        if(canEat(startCell, landingCell)){
            didEat = true;
            int[] target = (!Player.isEgregna) && board.isKing(startCell) ?
                    board.getTankegnaKingsPrey(startCell, landingCell) :
                    board.getMiddleCell(startCell, landingCell);
            eatenCorki[0] = target[0]; eatenCorki[1] = target[1]; eatenCorki[2] = board.getCellValue(target);
            board.makeEmpty(target);
        }
        lastMove = landingCell;
        board.moveCorki(startCell, landingCell);
        if (board.shouldKingify(landingCell)) { // crown the new king, if there is one
            wasKingified = true;
            board.makeKing(landingCell);
        }
        return new boolean[] {wasKingified, didEat};
    }

    public boolean canMove(int[] fromCell, int[] toCell)  {
        if(!isOwnCorki(fromCell)) {
            System.out.println("NOT YOUR CORKI, CAN'T MOVE");
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
        if(!isOwnCorki(fromCell)) {
            System.out.println("Menesha: "+Arrays.toString(fromCell));
            System.out.println("NOT YOUR CORKI, CAN'T EAT");
            return false;
        }
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
