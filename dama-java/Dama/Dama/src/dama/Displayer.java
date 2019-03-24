package dama;

/*
__________________________________________
The Displayer class models an object that
displays a dama board to the console
__________________________________________
*/
class Displayer {

    // the board object to be displayed
    private final Board board;
    
    // Costructor
    Displayer(Board board) {
        this.board = board;
    }

    // displays the dama board to the console
    void displayBoard() {
        String boardString = "";
        for(int row=0; row < 8; row++){
            int subRowMax = (row == 7) ? 5 : 4;
            for(int subRow=0; subRow < subRowMax; subRow++){
                for(int col=0; col < 8; col++){ 
                    if(subRow == 0 || subRow == 4){
                       boardString += "* * * * * ";                                                            
                    } else {
                        if(board.isIllegalCell(row, col))
                            boardString += "*         ";
                        else {
                            if(subRow == 2) {
                                String corki = board.isEmptyLegalCell(row, col)
                                        ? "   " : (board.isTop(row, col) 
                                        ? "@@@" : "+++");
                                String leftAuthoritySign = 
                                        board.isKing(row, col) ? "<<" : " <";
                                String rightAuthoritySign = 
                                        board.isKing(row, col) ? ">>" : "> ";                                
                                boardString += ("* "+leftAuthoritySign+
                                        corki+rightAuthoritySign+" ");
                            } else {
                                boardString += "*    "+
                                        board.getUniqueIdentifier(row, col)+"    ";
                            }
                        }
                    } // end of the outer else
                    
                } // end of the inner for loop     
                boardString += "*\n";
            } // end of the outer for loop             
        }
        System.out.println(boardString);
    }
    
    public static void main(String[] args){
        Board b = new Board();
        Displayer d = new Displayer(b);
        d.displayBoard();
    }
}
