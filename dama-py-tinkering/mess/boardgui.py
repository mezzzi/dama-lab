'''
===================================================================
THE BoardGui CLASS IS A TKINTER GUI DISPLAY FOR THE DamaBoard CLASS
====================================================================
'''

from tkinter import *
from time import sleep
import pickle

BOARD_WIDTH = 480
CELL_WIDTH = 60 # int(BOARD_WIDTH / 8)
        
class DamaGui:
    """
    a tkinter GUI for a dama board
    """    

    def __init__(self, dama_board, game_operator = None):
        """
        creates a dama graphical interface based on the given dama board object
        """

        ''' initialize fields '''
        self.board = dama_board
        self.operator = game_operator
        self.cells = [[None for col in range(self.board.NUM_ROWS)] for row in range(self.board.NUM_ROWS)] 
        self.corkis = [[None for col in range(self.board.NUM_ROWS)] for row in range(self.board.NUM_ROWS)]
        self.highlights = [] # contains highlighted cells
        self.enableds = [] # contains the list of enabled cells tag bind ids
        ''' set up the root tk '''
        self.root = Tk()
        self.root.title("Dama")
        ''' create the canvas '''
        self.canvas = Canvas(self.root, height = BOARD_WIDTH, width = BOARD_WIDTH, relief = "sunken")
        self.canvas.pack()
        '''@DEBUG@'''
        self.debug = False
        self.current_debug_cell = None
        self.context_menu = Menu(self.root, tearoff = 0)
        self.canvas.bind("<Button-3>", self.popContextMenu) 
        ''' create corki images '''
        self.teter_bottom_photo = PhotoImage(file = "pepsi.png")
        self.teter_top_photo = PhotoImage(file = "coke.png")
        self.king_bottom_photo = PhotoImage(file = "kingpepsi.png")
        self.king_top_photo = PhotoImage(file = "kingcoke.png")
        ''' draw board '''
        self.paintBoard()
        
    def paintBoard(self):
        """
        draws/paints board gui from board object
        """
        for row in range(self.board.NUM_ROWS):
            for col in range(self.board.NUM_ROWS):
                ''' draw a cell '''
                cellcolor = 'white' if self.board.isIllegalCell(row, col) else 'black'
                self.cells[row][col] = self.drawCell(row, col, cellcolor)
                ''' draw a corki '''
                if self.board.isOccupied(row, col):
                    self.corkis[row][col] = self.drawCorki(row, col)

    def letTheShowBegin(self):
        """
        starts accepting input from the user by starting the event loop
        """
        
        ''' display the dama gui by starting the event listener loop '''
        self.root.mainloop()
        
    def setOperator(self, game_operator):
        """
        sets the game operator
        """
        self.operator = game_operator
        #self.test_button['command'] = self.operator.printNumCorkis
       
    def enableCell(self, cell):
        """
        makes a given cell clickable
        """
        row, col = cell[0], cell[1]
        bind_id = self.canvas.tag_bind("cell" + str(row) + str(col), "<ButtonPress-1>", self.cellClicked)
        self.enableds.append(((row, col), bind_id))

    def cellClicked(self, event):
        """
        delegates click event
        """    
        clicked_cell = event.y // CELL_WIDTH, event.x // CELL_WIDTH # row, col of the clicked cell
        
        ''' click is handled differently depending on the debug mode '''
        if not self.debug:
            # let the operator handle it
            self.operator.handleClick(clicked_cell)
        else:
            # undo highlighting
            self.unhighlightAll()
            self.highlightCell(clicked_cell)
            # set current debug cell
            self.current_debug_cell = clicked_cell

    def disableAll(self):
        """
        disables all previously enabled cells
        """
        for cell_bindId in self.enableds: # bind_id is the id returned earlier by tag_bind
            row, col, bind_id = cell_bindId[0][0], cell_bindId[0][1], cell_bindId[1]
            self.canvas.tag_unbind("cell" + str(row) + str(col), "<ButtonPress-1>", str(bind_id))
        self.enableds.clear()

    def highlightCell(self, cell):
        """
        highlights a given cell by filling it with a darker color 
        """
        row, col = cell[0], cell[1]
        self.canvas.itemconfig(self.cells[row][col], fill = "grey")
        self.highlights.append(cell)

    def unhighlightCell(self, cell):
        """
        unhighlights a given cell by filling it with its normal color 
        """
        row, col = cell[0], cell[1]
        self.canvas.itemconfig(self.cells[row][col], fill = "black")
        self.highlights.remove(cell)
        
    def unhighlightAll(self):    
        """
        unhighlights all previously highlighted cells 
        """
        for cell in self.highlights:
            row, col = cell[0], cell[1]
            self.canvas.itemconfig(self.cells[row][col], fill = "black")
        self.highlights.clear()
        
    def drawCell(self, row, col, cellcolor):
        """
        draws dama cell on the canvas @ row, col
        returns a cell id
        """
        xcoor = col * CELL_WIDTH
        ycoor = row * CELL_WIDTH
        cell = self.canvas.create_rectangle(
            xcoor, ycoor, xcoor + CELL_WIDTH, ycoor + CELL_WIDTH,
            fill = cellcolor, tag = "cell" + str(row) + str(col))
        return cell

    def drawCorki(self, row, col):
        """
        draws dama corki on the canvas @ row, col,
        returns a corki id 
        """
        xcoor = col * CELL_WIDTH + (CELL_WIDTH / 2)
        ycoor = row * CELL_WIDTH + (CELL_WIDTH / 2)
        photo = None
        if self.board.isTeterBottom(row, col):
            photo= self.teter_bottom_photo
        elif self.board.isTeterTop(row, col):
            photo= self.teter_top_photo
        elif self.board.isKingTop(row, col):
            photo= self.king_top_photo
        elif self.board.isKingBottom(row, col):
            photo= self.king_bottom_photo
        corki = self.canvas.create_image(xcoor, ycoor, image = photo, tag = "cell" + str(row) + str(col))
        return corki


    def moveCorki(self, start_cell, landing_cell):
        """
        performs a slow gui move of a corki from start cell to its landing cell
        """
        row1, col1, row2, col2 = start_cell[0], start_cell[1], landing_cell[0], landing_cell[1]
        moving_corki = self.corkis[row1][col1]
        steps = 10 # chosen almost at random, or because 10 is cool
        x1 = (col1 * CELL_WIDTH) + CELL_WIDTH / 2
        y1 = (row1 * CELL_WIDTH) + CELL_WIDTH / 2
        x2 = (col2 * CELL_WIDTH) + CELL_WIDTH / 2
        y2 = (row2 * CELL_WIDTH) + CELL_WIDTH / 2
        dx = (x2 - x1) / steps
        dy = (y2 - y1) / steps
        self.canvas.lift(moving_corki) # so it can be seen moving on top of the canvas, and not under some rectangle
        for step in range(steps):
            self.canvas.move(moving_corki, dx, dy)
            self.canvas.after(100) # pause 100 milliseconds
            self.canvas.update()
        ''' update the corki tag, now that it has moved to a different cell '''
        self.canvas.itemconfig(moving_corki, tag = "cell" + str(row2) + str(col2))
        self.corkis[row2][col2] = moving_corki
        self.corkis[row1][col1] = None
        ''' kingify if a teter became a king '''
        if row2 == 0 and self.board.isKingBottom(row2, col2):
            self.canvas.itemconfig(moving_corki, image = self.king_bottom_photo)
        if row2 == 7 and self.board.isKingTop(row2, col2):
            self.canvas.itemconfig(moving_corki, image = self.king_top_photo)
        self.canvas.update()
        ''' wait half a second for the move to be taken in / to be noticed '''
        sleep(0.5)

    def animateMove(self, start_cell, landing_cell, did_eat):
        """
        does what it says; displays the move to the user, wouldn't be
        a cool game to play otherwise
        """

        ''' remove the eaten corki '''        
        if did_eat:
            eaten_row, eaten_col = self.board.getMiddleCell(start_cell, landing_cell)
            self.canvas.delete(self.corkis[eaten_row][eaten_col])
            self.corkis[eaten_row][eaten_col] = None
        ''' move the eating or moving corki to its destination '''
        self.moveCorki(start_cell, landing_cell)
        
    ####################################
    ###     DEBUG RELATED METHODS  #####
    ####################################
        
    def toggleDebugMode(self):
        """
        toggles debug mode, and enables all legal cells when debug mode is on
        """

        ''' toggle debug state '''
        self.debug = not self.debug
        ''' make the appropriate adjustements '''
        if self.debug:
            # enable all legal cells
            for row in range(self.board.NUM_ROWS):
                for col in range(self.board.NUM_ROWS):
                    if not self.board.isIllegalCell(row, col):
                        self.enableCell((row, col))
        else:
            # unhilight all
            self.unhighlightAll()
            # disable all cells
            self.disableAll()
            # enable clickable human cells
            self.operator.enableClickableHumanCells()

    def addCorki(self, corki_type):
        """
        adds a teter bottom corki at the current debug cell
        """
        if self.current_debug_cell != None:
            if corki_type == 'teter_bottom':
                self.board.makeTeterBottom(self.current_debug_cell)
            elif corki_type == 'teter_top':
                self.board.makeTeterTop(self.current_debug_cell)
            elif corki_type == 'king_bottom':
                self.board.makeKingBottom(self.current_debug_cell)
            elif corki_type == 'king_top':
                self.board.makeKingTop(self.current_debug_cell)
            else:
                return
            row, col = self.current_debug_cell[0], self.current_debug_cell[1]
            corki = self.drawCorki(row, col)
            self.corkis[row][col] = corki

    def highlightNextMove(self, move_type):
        """
        adds a teter bottom corki at the current debug cell
        """
        
        if self.current_debug_cell != None:
            ''' get moves and eats at once '''
            cur_cell = self.current_debug_cell
            if self.board.isTop(cur_cell):
                moves_and_eats = self.operator.comp_player.movesAndEats(cur_cell)
            else:
                moves_and_eats = self.operator.human_player.movesAndEats(cur_cell)

            ''' get the target cells, depending on the move type '''
            if move_type == 'move':
                target_cells = moves_and_eats[0]
            elif move_type == 'eat':
                target_cells = moves_and_eats[1]
            else:
                return

            ''' highlight target cells '''
            for cell in target_cells:
                self.highlightCell(cell)

    def removeCorki(self):
        """
        removes the selected corki from the board
        """

        ''' remove from the logic board '''
        self.board.makeEmpty(self.current_debug_cell)

        ''' remove from the canvas '''
        delete_row, delete_col = self.current_debug_cell
        self.canvas.delete(self.corkis[delete_row][delete_col])
        self.corkis[delete_row][delete_col] = None

    def showEatingSpree(self):
        """
        shows the multiple simultaneous eats that a corki at the current cell
        can make.
        """
        eat_paths, tree_depth = self.operator.comp_player.getEatPathsFromANode(self.current_debug_cell)
        if eat_paths:
            for path in eat_paths:
                for i in range(len(path)):
                    self.highlightCell(path[i])
                    self.canvas.after(500)
                    self.canvas.update()
                sleep(1)
                self.unhighlightAll()
                
    def saveBoard(self):
        """
        pickles/serializes the current board obect for ater restoration
        """
        with open('board.pickle', 'wb') as f:
            # Pickle the 'data' dictionary using the highest protocol available.
            pickle.dump(self.board, f, pickle.HIGHEST_PROTOCOL)

    def restoreBoard(self):
        """
        loads/deserializes a saved board object and sets it as the current board object
        """
        with open('board.pickle', 'rb') as f:
            # The protocol version used is detected automatically, so we do not
            # have to specify it.
            self.board = pickle.load(f)
            self.operator.comp_player.board = self.board
            self.operator.human_player.board = self.board
            ''' repaint board gui '''
            for row in range(self.board.NUM_ROWS):
                for col in range(self.board.NUM_ROWS):
                    if self.corkis[row][col]:
                        self.canvas.delete(self.corkis[row][col])
                        self.corkis[row][col] = None
            self.paintBoard()

    def displayCellCoordinates(self):
        """
        displays the (row, col) coordinates using a simple dialog window
        """
        messagebox.showinfo("Cell Coordinates", str(self.current_debug_cell))
        
    def popContextMenu(self, event):
        """
        pops up the right sets of context menu items when the user right clicks on the board
        """
        
        ''' find clicked cell '''
        clicked_cell = event.y // CELL_WIDTH, event.x // CELL_WIDTH # row, col of the clicked cell
        # set clicked cell as current debug cell
        self.current_debug_cell = clicked_cell
        
        ''' clear previous commands, if there are any '''
        self.context_menu.delete(0, 10) # assuming the commands are less than 10 in number
        
        ''' add the appropriate commands to the menu first '''
        self.context_menu.add_command(label='Toggle DEBUG', command=self.toggleDebugMode)
        if self.debug:
            self.context_menu.add_command(label='Show Cell Coordinates', command=self.displayCellCoordinates)                                                         
            # clicked cell is empty
            if self.board.isEmptyLegalCell(clicked_cell):
                self.context_menu.add_command(label='Add Teter Bottom', command=lambda corkitype='teter_bottom': self.addCorki(corkitype))
                self.context_menu.add_command(label='Add Teter Top', command=lambda corkitype='teter_top': self.addCorki(corkitype))
                self.context_menu.add_command(label='Add King Bottom', command=lambda corkitype='king_bottom': self.addCorki(corkitype))
                self.context_menu.add_command(label='Add King Top', command=lambda corkitype='king_top': self.addCorki(corkitype))
                self.context_menu.add_command(label='Save Board', command=self.saveBoard)
                self.context_menu.add_command(label='Restore Board', command=self.restoreBoard)
            else:
                self.context_menu.add_command(label='Show Next Moves', command=lambda movetype='move': self.highlightNextMove(movetype))
                self.context_menu.add_command(label='Show Next Eats', command=lambda movetype='eat': self.highlightNextMove(movetype))
                self.context_menu.add_command(label='Remove Corki', command=self.removeCorki)
                self.context_menu.add_command(label='Show Eating Spree', command=self.showEatingSpree)
                
        ''' post the context menu at the click location '''
        self.context_menu.post(event.x_root, event.y_root)
