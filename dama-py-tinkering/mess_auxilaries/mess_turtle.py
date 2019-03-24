from turtle import *
from stillmess import *

def drawCell(x, y, fill_color, cellwidth):
    """
    draws a solid cell to represent a board position,
    cell is centered at x, y
    """
    penup(); goto(x - cellwidth / 2, y + cellwidth / 2); pendown()
    color("black", fill_color); setheading(0)
    begin_fill()
    for i in range(4):
        forward(cellwidth); right(90)
    end_fill()
        
def turtBoard(board):
    """
    draws the board in turtle
    """
    cellwidth = 40
    screen_width = screensize()[0]
    screen_height = screensize()[1]
    board_width = board_height = 8 * cellwidth
    offsetx = (-screen_width / 2) + \
              (screen_width - board_width) / 2
    offsety = (screen_height / 2) - \
              (screen_height - board_height) / 2

    tracer(False)
    for i in range(8):
        for j in range(8):
            # draw the cell
            cellcolor = 'white' if board[i][j] == ILLEGAL_CELL \
                        else 'black'
            xcoor = offsetx + (j * cellwidth) + (cellwidth / 2)
            ycoor = offsety - (i * cellwidth) - (cellwidth / 2)
            drawCell(xcoor, ycoor, cellcolor, cellwidth)
            # draw the corki
            if not (board[i][j] in \
                    (ILLEGAL_CELL, EMPTY_LEGAL_CELL)) :
                penup(); goto(xcoor, ycoor); pendown()
                if board[i][j] == TETER_TOP:
                    pencolor("blue")
                elif board[i][j] == TETER_BOTTOM:
                    pencolor("red")
                elif board[i][j] == KING_TOP:
                    pencolor("cyan")
                elif board[i][j] == KING_BOTTOM:
                    pencolor("orange")
                dot(cellwidth / 4); pencolor("black")
            # draw column indexes
            if(i == 0):    
                penup()
                goto(offsetx + (j * cellwidth) + (cellwidth / 2),
                     offsety + (cellwidth / 4))
                write(str(j), align='center',
                      font=('Arial', 10, 'normal'))
            # draw row indexes
            if(j == 0):    
                penup()
                goto(offsetx - cellwidth / 2,
                     offsety - (i * cellwidth) - cellwidth * 0.75)
                write(str(i),align='center',
                      font=('Arial', 10, 'normal'))
    hideturtle()
    tracer(True)

def runGame(board):
    """
    runs game by allowing interaction through cmmand line
    text input as x1,y1,x2,y2 where (x1, y1) is starting cell and
    (x2, y2) is destination cell.
    board should come with the corkis properly layed out on it.
    """
    computersTurn = False
    while True:
        clear(); turtBoard(board) # update turtle board
        if not computersTurn:
            user_input = input("Enter move: ")
            if user_input == 'q': break
            else:
                x1, y1, x2, y2 = eval(user_input)
            moves_and_eats = legalMovesAndEats(board, x1, y1)
            isAnEatMove = (x2, y2) in moves_and_eats[1]
            computersTurn = True
        else:
            start_cell, landing_cell, isAnEatMove = \
                          getComputersMove(board)
            x1, y1, x2, y2 = start_cell[0], start_cell[1], \
                             landing_cell[0], landing_cell[1]
            computersTurn = False
        former_value = board[x1][y1]
        board[x1][y1] = EMPTY_LEGAL_CELL
        if not isAnEatMove: pass
        elif isAnEatMove:
            midX = (x1 + 1) if x2 > x1 else (x1 - 1)
            midY = (y1 + 1) if y2 > y1 else (y1 - 1)
            board[midX][midY] = EMPTY_LEGAL_CELL
            if former_value in (KING_TOP, KING_BOTTOM): pass
            else:
                if former_value == TETER_BOTTOM and x2 == 0:
                    board[x2][y2] = KING_BOTTOM
                    continue
                elif former_value == TETER_TOP and x2 == 7:
                    board[x2][y2] = KING_TOP
                    continue
                else:pass
        board[x2][y2] = former_value
        
def strBoard(board):
    """
    returns formatted string representation of the board,
    helpful for test runs and debugging
    """
    board_string = ""
    for i in range(8):
        board_string += ("\n " + (8 * 8 * "-"))
        for k in range(3):
            board_string += "\n"
            for j in range(8):
                if k == 1 :
                    if( board[i][j] == ILLEGAL_CELL):
                        board_string += "|       "
                    else:
                        # print corki
                        if board[i][j] == EMPTY_LEGAL_CELL:
                            board_string += "|   *   "
                        elif board[i][j] == TETER_TOP:
                            board_string += "|   @   "
                        elif board[i][j] == TETER_BOTTOM:
                            board_string += "|   #   "
                        elif board[i][j] == KING_TOP:
                            board_string += "|   %   "
                        elif board[i][j] == KING_BOTTOM:
                            board_string += "|   &   "      
                else:
                    board_string += "|       "
                if j == 7 :
                    board_string += "|"               
    board_string += ("\n " + (8 * 8 * "-"))
    return board_string

"""
TEST RUNS
"""
if __name__ == "__main__":
    my_board = board()
    addCorkis(my_board)
    print(strBoard(my_board))
    turtBoard(my_board)
    runGame(my_board)
