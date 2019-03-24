from tkinter import *
from time import sleep

ILLEGAL_CELL = 0 # 0
EMPTY_LEGAL_CELL = 1 # 1
TETER_TOP = 3 # 3
KING_TOP = TETER_TOP + 1 # 4
TETER_BOTTOM = KING_TOP + 4 # 8
KING_BOTTOM = TETER_BOTTOM + 1 # 9

NEIGHBOR_OFFSETS = ((-1, -1), (-1, 1), (1, 1), (1, -1))

BOARD_WIDTH = 576
CELL_WIDTH = 72 # int(BOARD_WIDTH / 8)
CORKI_RAD = 18 # int(cellwidth / 4)
    
CELLS = [[None for i in range(8)] for j in range(8)] # rectangles
CORKIS = [[None for i in range(8)] for j in range(8)] # ovals
REAL_CORKIS = [[None for i in range(8)] for j in range(8)] # ovals


HIGHLIGHTED_CELLS = []
ENABLED_CELLS = []

FIRST_CLICK_DONE = False
prev_selected_cell = first_clicked_cell = None

def board():
    global board
    """
    returns an 8 x 8 board as array of arrays,
    array value 1 mean legal board position,
    while array value 0 mean illegal board position 
    """
    board = [[0 for j in range(8)] for i in range(8)]
    for i in range(8):
        for j in range(8):
            board[i][j] = (i + j) % 2

def addCorkis():
    """
    lays out the cokis on the board; 24 in total, 12 for top
    player and another 12 for the bottom player.
    """
    for i in range(3):
        for j in range(8):
            if (i + j) % 2 :
                board[i][j] = TETER_TOP
                board[i + 5][7 - j] = TETER_BOTTOM
                               
def legalMovesAndEats(x, y):
    """
    returns the cells a given corki@(x, y) can legally
    move to in one step, in Egregna Dama game.
    """
    legal_move_cells = []
    legal_eat_cells = []
    for neighbor_offset in NEIGHBOR_OFFSETS:
        neighborX = x + neighbor_offset[0]
        neighborY = y + neighbor_offset[1]
        if isInsideBoard(neighborX, neighborY):           
            if board[neighborX][neighborY] == EMPTY_LEGAL_CELL:
                if board[x][y] in (KING_TOP, KING_BOTTOM):pass
                else:
                    if (board[x][y] == TETER_TOP) and \
                       (neighborX > x): pass
                    elif (board[x][y] == TETER_BOTTOM) and \
                         (neighborX < x): pass
                    else: continue
                legal_move_cells.append((neighborX, neighborY))
            else:
                if not areTeamMates(board[x][y],
                                board[neighborX][neighborY]): 
                    nextX = neighborX + (neighborX - x)
                    nextY = neighborY + (neighborY - y)
                    if isInsideBoard(nextX, nextY):
                        if board[nextX][nextY] == EMPTY_LEGAL_CELL:
                           if board[x][y] in \
                              (KING_TOP, KING_BOTTOM): pass
                           elif board[neighborX][neighborY] in \
                                (TETER_TOP, TETER_BOTTOM):
                               if (board[x][y] == TETER_TOP)\
                                  and (neighborX > x): pass
                               elif (board[x][y] == TETER_BOTTOM) \
                                    and (neighborX < x): pass
                               else: continue                               
                           else: continue
                           legal_eat_cells.append((nextX, nextY))
    return legal_move_cells, legal_eat_cells


def getCanMoveCells():
    """
    returns cells whose corki can either eat or move,
    player is assumed to be human, not computer
    """
    can_move_cells = []
    for i in range(8):
        for j in range(8):
            if board[i][j] in (TETER_BOTTOM, KING_BOTTOM):
                moves_and_eats = legalMovesAndEats(i, j)
                moves = len(moves_and_eats[0])
                eats = len(moves_and_eats[1])
                if (moves > 0) or (eats > 0):
                    can_move_cells.append((i, j))
    return can_move_cells
                
def getComputersMove():
    """
    returns the computers move as ((x1, y1), (x2, y2)), bool
    where (x1, y1) is starting cell, (x2, y2) is
    destination cell, and bool is True if the move is an eat
    move, False if the move is a non eat move.
    It is assumed that the computer takes the top position
    """
    max_moves = max_eats = 0
    best_move = best_eat = None
    for i in range(8):
        for j in range(8):
            if board[i][j] in (TETER_TOP, KING_TOP):
                moves_and_eats = legalMovesAndEats(i, j)
                moves = len(moves_and_eats[0])
                eats = len(moves_and_eats[1])
                # prefer max num moves
                if moves > max_moves:
                    max_moves = moves
                    best_move = [(i, j), moves_and_eats[0]]
                # prefer max num eats
                if eats > max_eats:
                    max_eats = eats
                    best_eat = [(i, j), moves_and_eats[1]]
    # prefer eat move over non eat move
    if not best_eat is None:
        return best_eat[0], best_eat[1][0], True
    elif not best_move is None:
        return best_move[0], best_move[1][0], False
    else:
        return None

def areTeamMates(corki1, corki2):
    """
    returns true if both corkis are either top or bottom
    """
    return abs(corki1 - corki2) <= 1

def riskIfUnmoved(x, y):
    """
    returns the maximum number of corki values that can
    be lost by not moving the corki@(x,y).
    Computer's position is assumed to be top
    """
    value_lost = 0
    # get attacker cells
    attacker_cells = []
    cur_cell = board[x][y]
    for next_offset in NEIGHBOR_OFFSETS:
        nextX, nextY = x + next_offset[0], Y + next_offset[1]
        if isInsideBoard(nextX, nextY):
            if ((cur_cell == TETER_TOP) and
                (next_offset[0] == -1)) or \
                ((next_cell == TETER_BOTTOM) and \
                 (cur_cell == KING_TOP)): break
            next_cell = board[nextX][nextY]
            landingX, landingY = x + (x - nextX), y + (y - nextY)
            canAttack = \
            (not(next_cell == EMPTY_LEGAL_CELL)) and \
            (not areTeamMates(cur_cell, next_cell)) and \
            isInsideBoard(landingX, landingY) and \
            (board[landingX][landingY] == EMPTY_LEGAL_CELL)
            if canAttack: attacker_cells.append((nextX, nextY))
               
def isInsideBoard(x, y):
    """
    returns true if (x, y) lies with in an 8x8 board
    """
    return (x in range(8)) and (y in range(8))

def playOneMove(start_cell = None, landing_cell = None,
                is_computer_playing = False):
    """ 
    performs dama move from start_cell to landing_cell,
    returns isAnEatMove, (startcell, endcell)
    """
    if is_computer_playing:
        start_cell, landing_cell, isAnEatMove = getComputersMove()
    else:
        moves_and_eats = legalMovesAndEats(start_cell[0],
                                           start_cell[1])
        isAnEatMove = len(moves_and_eats[1]) > 0
    x1, y1, x2, y2 = start_cell[0], start_cell[1],\
                     landing_cell[0], landing_cell[1]
    former_value = board[x1][y1]
    board[x1][y1] = EMPTY_LEGAL_CELL
    board[x2][y2] = former_value
    if isAnEatMove:
        midX, midY = getMiddleCell((x1, y1),(x2, y2))
        board[midX][midY] = EMPTY_LEGAL_CELL
    if former_value in (TETER_TOP, TETER_BOTTOM):
        if former_value == TETER_BOTTOM and x2 == 0:
            board[x2][y2] = KING_BOTTOM
        elif former_value == TETER_TOP and x2 == 7:
            board[x2][y2] = KING_TOP
    return isAnEatMove, ((x1, y1), (x2, y2))

def getMiddleCell(start_cell, landing_cell):
    """
    yeah, literally returns the coordinates of the middle cell
    """
    x1, y1, x2, y2 = start_cell[0], start_cell[1], \
                      landing_cell[0], landing_cell[1]    
    mid_x = (x1 + 1) if x2 > x1 else (x1 - 1)
    mid_y = (y1 + 1) if y2 > y1 else (y1 - 1)
    return (mid_x, mid_y)

def tkinterBoard():
    """
    displays dama GUI for the given board, using tkinter
    """
    global canvas
    # set up the root tk
    root = Tk()
    root.title("Dama")
    root.overrideredirect(1)
    
    # create canvas
    canvas = Canvas(root, height = BOARD_WIDTH,
                    width = BOARD_WIDTH, relief = "sunken")
    canvas.grid(column = 0, row = 0, sticky=(E, W, N, S))

    for i in range(8):
        for j in range(8):
            # draw the cell
            cellcolor = 'white' if board[i][j] == ILLEGAL_CELL \
                        else 'black'
            cell = drawCell((i, j), cellcolor)
            CELLS[i][j] = cell
            # draw the corki
            if not (board[i][j] in \
                    (ILLEGAL_CELL, EMPTY_LEGAL_CELL)):
                corki = drawCorki((i, j))
                CORKIS[i][j] = corki
            # bind possible next move corkis to click event
            if i == 5 and j % 2 == 0:
                canvas.tag_bind("cell" + str(i) + str(j),
                            "<ButtonPress-1>", cellClicked)

def drawCell(position, cellcolor):
    """
    draws dama cell on the given canvas at the given position,
    returns rectangle
    """
    i, j = position[0], position[1]
    xcoor = j * CELL_WIDTH
    ycoor = i * CELL_WIDTH
    cell = canvas.create_rectangle(xcoor, ycoor,
        xcoor + CELL_WIDTH, ycoor + CELL_WIDTH,
        fill = cellcolor, tag = "cell" + str(i) + str(j))
    return cell

'''
def drawCorki(position):
    """
    draws dama corki on the given canvas at the given position,
    returns corki 
    """
    i, j = position[0], position[1]
    xcoor = j * CELL_WIDTH
    ycoor = i * CELL_WIDTH
    corki_color = "yellow"
    if board[i][j] == TETER_TOP:
        corki_color = "blue"
    elif board[i][j] == TETER_BOTTOM:
        corki_color = "red"
    elif board[i][j] == KING_TOP:
        corki_color = "cyan"
    elif board[i][j] == KING_BOTTOM: 
        corki_color = "orange"
    corki = canvas.create_oval(
        xcoor + CORKI_RAD, ycoor + CORKI_RAD,
        xcoor + (3 * CORKI_RAD),
        ycoor + (3 * CORKI_RAD),
        fill = corki_color,
        tag = "cell" + str(i) + str(j)) 
    return corki
'''


def drawCorki(pos):
    """
    draws dama corki on the canvas @ row, col,
    returns a corki id 
    """
    i, j = pos[0], pos[1]
    xcoor = j * CELL_WIDTH + (CELL_WIDTH / 2)
    ycoor = i * CELL_WIDTH + (CELL_WIDTH / 2)
    if board[i][j] in (TETER_TOP, KING_TOP):
        REAL_CORKIS[i][j] = PhotoImage(file="coke.png")
        corki = canvas.create_image(xcoor, ycoor, image=REAL_CORKIS[i][j], tag = "cell" + str(i) + str(j))     
    else:
        REAL_CORKIS[i][j] = PhotoImage(file="pepsi.png")
        corki = canvas.create_image(xcoor, ycoor, image=REAL_CORKIS[i][j], tag = "cell" + str(i) + str(j))  
    return corki


def cellClicked(event):
    """
    handles dama board interaction through tkinter
    """
    global FIRST_CLICK_DONE, first_clicked_cell, \
           prev_selected_cell
    now_selected_cell = event.y // CELL_WIDTH, \
                        event.x // CELL_WIDTH  
    is_repeat_click = False 
    legal_moves = getLegalMoves(now_selected_cell)
    if not (prev_selected_cell is None):
        if prev_selected_cell == now_selected_cell :
            is_repeat_click = True   
    prev_selected_cell = now_selected_cell

    # undo highlighting
    for highlighted_cell in HIGHLIGHTED_CELLS:
        x, y = highlighted_cell[0], highlighted_cell[1]
        canvas.itemconfig(CELLS[x][y], fill = "black")
    HIGHLIGHTED_CELLS.clear()

    # highlight now clicked cell
    i, j = now_selected_cell[0], now_selected_cell[1]
    canvas.itemconfig(CELLS[i][j], fill = "grey")
    HIGHLIGHTED_CELLS.append(now_selected_cell)

    # handle first click
    if (not FIRST_CLICK_DONE) or \
       is_repeat_click or \
       not (now_selected_cell in getLegalMoves(first_clicked_cell)):
        # get legal move cells and enable them
        for cell in legal_moves:
            canvas.itemconfig(
                CELLS[ cell[0] ][ cell[1] ], fill = "grey")
            funcid = canvas.tag_bind(
                "cell" + str(cell[0]) + str(cell[1]),
                "<ButtonPress-1>", cellClicked)
            ENABLED_CELLS.append((cell, funcid))
            HIGHLIGHTED_CELLS.append(cell)
        first_clicked_cell = now_selected_cell
        FIRST_CLICK_DONE = True
    else:
        FIRST_CLICK_DONE = False
        # play the human's movedef cellClicked(event):

        did_eat = playOneMove(first_clicked_cell,
                              now_selected_cell)[0]
        sleep(0.5) # so user can see the change
        # unhighlight landing_cell now that the move is made
        x, y = now_selected_cell[0], now_selected_cell[1]
        canvas.itemconfig(CELLS[x][y], fill = "black")
        HIGHLIGHTED_CELLS.remove(now_selected_cell)
        # disable all
        for cell in ENABLED_CELLS:
            canvas.tag_unbind(
                "cell" + str(cell[0][0]) + str(cell[0][1]),
                "<ButtonPress-1>", str(cell[1]))
        ENABLED_CELLS.clear()
        # update board GUI
        updateBoardGUI(board, first_clicked_cell,
                       now_selected_cell, did_eat)   
        # play the computer's move
        did_eat, moves = playOneMove(is_computer_playing = True)
        # update board GUI here too
        updateBoardGUI(board, moves[0], moves[1], did_eat)
        next_legal_click_cells = getCanMoveCells()
        for cell in next_legal_click_cells:
            funcid = canvas.tag_bind(
                "cell" + str(cell[0]) + str(cell[1]),
                "<ButtonPress-1>", cellClicked)
            ENABLED_CELLS.append((cell, funcid))

def getLegalMoves(position):
    """
    returns the next legal moves from position
    """
    moves_and_eats = legalMovesAndEats(position[0], position[1])
    legal_moves = moves_and_eats[0]
    legal_moves.extend(moves_and_eats[1])
    return legal_moves

def moveCorki(start_cell, end_cell, animate=False):
    x1, y1, x2, y2 = start_cell[0], start_cell[1], end_cell[0], \
                     end_cell[1]
    moving_corki = CORKIS[x1][y1]
    if animate:
        steps = 10
        x_1 = (y1 * CELL_WIDTH) + CELL_WIDTH/2
        y_1 = (x1 * CELL_WIDTH) + CELL_WIDTH/2
        x_2 = (y2 * CELL_WIDTH) + CELL_WIDTH/2
        y_2 = (x2 * CELL_WIDTH) + CELL_WIDTH/2
        dx = (x_2 - x_1) / steps
        dy = (y_2 - y_1) / steps
        canvas.lift(moving_corki)
        for i in range(10):
            canvas.move(moving_corki, dx, dy)
            canvas.after(100) # after 100 milliseconds
            canvas.update()
        canvas.itemconfig(moving_corki,
                      tag = "cell" + str(x2) + str(y2))
        CORKIS[x2][y2] = moving_corki
        CORKIS[x1][y1] = None
        # adjust color, kingify
        if x2 == 0 and board[x2][y2] == KING_BOTTOM:
            canvas.itemconfig(moving_corki, fill = "orange")
        if x2 == 7  and board[x2][y2] == KING_TOP:
            canvas.itemconfig(moving_corki, fill = "cyan")
    else:
        # remove corki from start cell
        canvas.delete(moving_corki)
        CORKIS[x1][y1] = None
        # draw corki on landing cell
        drawCorki(end_cell)
    sleep(0.5)

def updateBoardGUI(board, start_cell, landing_cell, did_eat):
    """
    does what it says
    """

    if did_eat:
        # remove corki from middle cell
        i, j = getMiddleCell(start_cell, landing_cell)
        canvas.delete(CORKIS[i][j])
        CORKIS[i][j] = None
    moveCorki(start_cell, landing_cell, True)

    
            
"""
TEST RUNS
"""
if __name__ == "__main__":
    board()
    addCorkis()
    tkinterBoard()

