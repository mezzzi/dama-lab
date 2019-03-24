package com.villager.mezi.dama;

import java.util.ArrayList;
import java.util.Arrays;

public class Arbiter {
    private boolean isCurrentPlayerTop = false;
    private Board board;
    private Player playerTop;
    private Player playerBottom;
    private boolean topStarts;

    private  GuiDisplayer guiDisplayer;
    private int[] startCell = null;
    ArrayList<int[]> potentialLandingCells;
    boolean didEat, wasKingified;

    private Player winner = null;

    public Arbiter(){
        board = new Board();
        playerTop = new Player(board, true);
        playerBottom = new Player(board, false);
        Player.setIsEgregna(false);
    }

    public void setDisplayer (GuiDisplayer displayer) {
        guiDisplayer = displayer;
    }

    public Board getBoard() {
        return board;
    }

    public void restartGame(){
        board = new Board();
        playerTop = new Player(board, true);
        playerBottom = new Player(board, false);
        guiDisplayer.setBoard(board);
        isCurrentPlayerTop = topStarts;
        guiDisplayer.resetDisplayerStates();
        enableCanAdvanceCorkis();
    }

    public void enableCanAdvanceCorkis(){
        guiDisplayer.disableAll();
        for (int[] cell: getCurrentPlayer().getCanAdvanceCells()){
            guiDisplayer.enableCell(cell);
        }
    }

    public void startGame(){
        enableCanAdvanceCorkis();
        guiDisplayer.showBoard();
    }


    // for outsiders use
    public boolean isTheGameOver() {
        Player currentPlayer = getOpponnentPlayer();
        boolean hasCorkis = currentPlayer.getNumCorkis() > 0;
        if(!hasCorkis){
            winner = getCurrentPlayer();
            return true;
        }
        boolean canAdvance = currentPlayer.getCanAdvanceCells().size() > 0;
        if(!canAdvance){
            winner = getCurrentPlayer();
            return true;
        }
        return false;
    }

    public Player getWinner() {
        return winner;
    }

    void handleCellClick(int[] clickedCell) {
        guiDisplayer.unHighlightAll();
        guiDisplayer.highlightCell(clickedCell);
        if(Arrays.equals(startCell, null)) handleStartCellClick(clickedCell);
        else {
            guiDisplayer.disableAllTemporarilyEnabledCells();
            boolean isClickRedundant = Arrays.equals(startCell, clickedCell);
            if(isClickRedundant)handleStartCellClick(clickedCell);
            else {
                if(isPotentialLandingCell(clickedCell)){
                    handleLandingCellClick(clickedCell);
                } else { // another starting cell selected
                    handleStartCellClick(clickedCell);
                }
            }
        }
    }

    private boolean isPotentialLandingCell(int[] testCell){
        for (int[] cell: potentialLandingCells) {
            if (Arrays.equals(cell, testCell)) {
                return true;
            }
        }
        return false;
    }

    private void handleStartCellClick(int[] fromCell){
        potentialLandingCells = getCurrentPlayer().advancesFromCell(fromCell);
        for (int[] cell: potentialLandingCells) {
            guiDisplayer.highlightCell(cell);
            guiDisplayer.enableCellTemporarily(cell);
        }
        this.startCell = fromCell;
    }

    private void handleLandingCellClick(int[] landingCell){
        guiDisplayer.unHighlightCell(landingCell);
        guiDisplayer.disableAll();
        int movingCorki = board.getCellValue(startCell);
        int[] eatenCorki = {-1,-1,-1};
        boolean[] advanceInfo = getCurrentPlayer().playTurn(startCell, landingCell, eatenCorki);
        wasKingified = advanceInfo[0]; didEat = advanceInfo[1];
        // sendMove(new Message(board.getBoardArray(), startCell, landingCell, eatenCorki, movingCorki));
        guiDisplayer.displayMove(startCell, landingCell, movingCorki, eatenCorki);
    }


    public boolean processPendingEats(int[] landingCell){
        if(didEat && !wasKingified){ // handle consecutive eats
            ArrayList<int[]> moreEats = getCurrentPlayer().movesAndEats(landingCell)[1];
            if(moreEats.size() > 0){
                startCell = landingCell;
                for (int[] cell: moreEats)  {
                    guiDisplayer.highlightCell(cell);
                    guiDisplayer.enableCell(cell);
                }
                potentialLandingCells = new ArrayList<>();
                for (int[] cell: moreEats)  {
                    potentialLandingCells.add(cell);
                }
                return true;
            }
        }
        return false;
    }

    public void checkForEfita(){
        if(Player.isEfitaAllowed() && !didEat){
            int[] efita = getCurrentPlayer().getEfita();
            if(efita != null) { guiDisplayer.displayEfita(efita); return;}
        }
        changeTurn();
    }

    public void changeTurn(){
        if(isGameOver()) return;
        isCurrentPlayerTop = !isCurrentPlayerTop; // change turn
        enableCanAdvanceCorkis();
        startCell = null;
    }

    public Player getCurrentPlayer(){
        if(isCurrentPlayerTop) return playerTop;
        else return playerBottom;
    }

    public Player getOpponnentPlayer(){
        if(isCurrentPlayerTop) return playerBottom;
        else return playerTop;
    }
    public Player getTopPlayer(){return playerTop;}

    public boolean isGameOver(){
        Player currentPlayer = getCurrentPlayer();
        boolean hasCorkis = currentPlayer.getNumCorkis() > 0;
        if(!hasCorkis){
            winner = getOpponnentPlayer();
            return true;
        }
        boolean canAdvance = currentPlayer.getCanAdvanceCells().size() > 0;
        if(!canAdvance){
            winner = getOpponnentPlayer();
            return true;
        }
        return false;
    }
}

