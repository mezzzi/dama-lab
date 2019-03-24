package dama;

import java.util.Scanner;

/*
___________________________________________
The Refree class models a moderator object
that makes sure the dama game runs smoothly
between the two players.
___________________________________________
 */
public class Refree {
    private boolean isCurrentPlayerTop;
    private final Board board;
    private final Player playerTop;
    private final Player playerBottom;
    private final Displayer displayer;
    private static final Scanner scanner = new Scanner(System.in);
    
    // instantiates the Refree object
    public Refree(){
        board = new Board();
        System.out.print("Enter 0 to play Egregna or 1 to play Tankegna: ");
        int choice = scanner.nextInt();
        Player.setIsEgregna(0 == choice);
        playerTop = new Player(board, true);
        playerBottom = new Player(board, false);
        displayer = new Displayer(board);
    }
    
    // starts and runs the game
    public void runGame(){
        displayer.displayBoard();
        showInstructions();
        while (true){
            int[][] currentMove = getInput();
            if (isMoveValid(currentMove)){
                getCurrentPlayer().playTurn(currentMove);
                System.out.println("\n");
                displayer.displayBoard();
                isCurrentPlayerTop = !isCurrentPlayerTop;                
                if (isGameOver()){break;}
            } else{
                System.out.println("Invalid Move!");
            }
        }
    }
    
    // show instructions
    public void showInstructions(){
        System.out.println("_____________________________________");
        System.out.println("Ente your moves in the form m5 \n"+
                           "where m is the starting position and\n" +
                           "5 is the landing position.");
        System.out.println("_____________________________________");
    }
    
    
    // get user inputs
    public int[][] getInput(){
        Player currentPlayer = getCurrentPlayer();
        int[][] advance = new int[2][2];
        while(true){
            System.out.print("\nEnter Move["+currentPlayer.getName()+"] : ");
            String input = scanner.next();
            if(input.length() != 2){
                System.out.println("Invalid Move, Input length should be two");
            } else {         
                int[] startCell = board.getCellCoordinates(input.charAt(0));
                if(startCell == null) {
                    System.out.println("Unrecognized character "+input.charAt(0));
                    continue;
                } else {
                    advance[0] = startCell;
                }
                int[] ladningCell = board.getCellCoordinates(input.charAt(1));
                if(ladningCell == null) {
                    System.out.println("Unrecognized character "+input.charAt(1));
                } else {
                    advance[1] = ladningCell;
                    break;
                }              
            }
        }
        return advance;
    }
    
    // checks whether a given move is valid or not
    public boolean isMoveValid(int[][] move){
        int[] startCell = move[0];
        int[] landingCell = move[1];
        Player currentPlayer = getCurrentPlayer();
        return currentPlayer.canMove(startCell, landingCell) || 
                currentPlayer.canEat(startCell, landingCell);        
    }
    
    
    
    // returns the player who is playing now
    private Player getCurrentPlayer(){
        if(isCurrentPlayerTop){
            return playerTop;
        } else {
            return playerBottom;
        }
    }
    
    // checks whether game is over or not
    private boolean isGameOver(){
        Player currentPlayer = getCurrentPlayer();
        boolean hasCorkis = currentPlayer.getNumCorkis() > 0;
        if(!hasCorkis){
            System.out.println("GAME OVER!");
            System.out.println(currentPlayer.getName()+" HAS LOST!");
            return true;
        }
        boolean canAdvance = currentPlayer.getCanAdvanceCells().size() > 0;
        if(!canAdvance){
            System.out.println("GAME OVER!");
            System.out.println(currentPlayer.getName()+" HAS LOST!");
            return true;           
        }
        return false;
    }
    
    public static void main(String[] args){
        Refree ref = new Refree();
        ref.runGame();
    }
}
