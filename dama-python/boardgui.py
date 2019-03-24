"""
===================================================================
THE BoardGui CLASS IS A TKINTER GUI DISPLAY FOR THE DamaBoard CLASS
====================================================================
"""

import pickle
import tkinter.messagebox as messagebox
from time import sleep
from tkinter import *

from mastermind import AdvancingNode
from queue import Queue

BOARD_WIDTH = 640
CELL_WIDTH = 80 # int(BOARD_WIDTH / 8)
        
class DamaGui:
    """
    a tkinter GUI for a dama board
    """    

    def __init__(self, dama_board, game_operator = None):
        """
        creates a dama graphical interface based on the given dama board object
        """
        # initialize fields
        self.board = dama_board
        self.operator = game_operator
        self.cells = [[None for row in range(self.board.NUM_ROWS)] for col in range(self.board.NUM_ROWS)]
        self.corkis = [[None for row in range(self.board.NUM_ROWS)] for col in range(self.board.NUM_ROWS)]
        self.highlights = [] # contains highlighted cells
        self.enableds = [] # contains the list of enabled cells tag bind ids
        self.tempEnableds = []
        # set up the root tk
        self.root = Tk()
        self.root.title("Dama")
        # create the canvas
        self.canvas = Canvas(self.root, height = BOARD_WIDTH, width = BOARD_WIDTH, relief = "sunken")
        self.canvas.pack()
        # @DEBUG@
        self.debug = False
        self.current_debug_cell = None
        self.test_destn_cell = None
        self.test_cur_node = AdvancingNode()
        self.test_max_node_wrapper = [self.test_cur_node]
        self.test_bfs_queue = Queue()
        self.context_menu = Menu(self.root, tearoff = 0)
        self.canvas.bind("<Button-3>", self.popContextMenu) 
        # create corki images
        self.teter_bottom_photo = PhotoImage(file = "images/pepsi.png")
        self.teter_top_photo = PhotoImage(file = "images/coke.png")
        self.king_bottom_photo = PhotoImage(file = "images/kingpepsi.png")
        self.king_top_photo = PhotoImage(file = "images/kingcoke.png")
        # draw board
        self.paintBoard()

    # draws/paints board gui from board object
    def paintBoard(self):
        for row in range(self.board.NUM_ROWS):
            for col in range(self.board.NUM_ROWS):
                # draw a cell
                cellcolor = 'brown' if self.board.isIllegalCell(row, col) else 'black'
                self.cells[row][col] = self.drawCell(row, col, cellcolor)
                # draw a corki
                if self.board.isOccupied(row, col):
                    self.corkis[row][col] = self.drawCorki(row, col)

    # starts accepting input from the user by starting the event loop
    def letTheShowBegin(self):
        # display the dama gui by starting the event listener loop
        self.root.mainloop()

    # sets the game operator
    def setOperator(self, game_operator):
        self.operator = game_operator

    # makes a given cell clickable
    def enableCell(self, cell):
        row, col = cell[0], cell[1]
        bind_id = self.canvas.tag_bind("cell" + str(row) + str(col), "<ButtonPress-1>", self.cellClicked)
        self.enableds.append(((row, col), bind_id))

    def enableCellTemporarily(self, cell):
        row, col = cell[0], cell[1]
        bind_id = self.canvas.tag_bind("cell" + str(row) + str(col), "<ButtonPress-1>", self.cellClicked)
        self.tempEnableds.append(((row, col), bind_id))

    # disables all previously enabled cells
    def disableAllTemporaries(self):
        for cell_bindId in self.tempEnableds: # bind_id is the id returned earlier by tag_bind
            row, col, bind_id = cell_bindId[0][0], cell_bindId[0][1], cell_bindId[1]
            self.canvas.tag_unbind("cell" + str(row) + str(col), "<ButtonPress-1>", str(bind_id))
        self.tempEnableds.clear()

    # delegates click event
    def cellClicked(self, event):
        clicked_cell = event.y // CELL_WIDTH, event.x // CELL_WIDTH # row, col of the clicked cell
        # click is handled differently depending on the debug mode
        if not self.debug:
            # let the operator handle it
            self.operator.handleClick(clicked_cell)
        else:
            # undo highlighting
            self.unhighlightAll()
            self.highlightCell(clicked_cell)
            # set current debug cell
            self.current_debug_cell = clicked_cell

    # disables all previously enabled cells
    def disableAll(self):
        for cell_bindId in self.enableds: # bind_id is the id returned earlier by tag_bind
            row, col, bind_id = cell_bindId[0][0], cell_bindId[0][1], cell_bindId[1]
            self.canvas.tag_unbind("cell" + str(row) + str(col), "<ButtonPress-1>", str(bind_id))
        self.enableds.clear()

    # highlights a given cell by filling it with a darker color
    def highlightCell(self, cell):
        row, col = cell[0], cell[1]
        self.canvas.itemconfig(self.cells[row][col], fill = "grey")
        self.highlights.append(cell)

    # un highlights a given cell by filling it with its normal color
    def unhighlightCell(self, cell):
        row, col = cell[0], cell[1]
        self.canvas.itemconfig(self.cells[row][col], fill = "black")
        self.highlights.remove(cell)

    # un highlights all previously highlighted cells
    def unhighlightAll(self):
        for cell in self.highlights:
            row, col = cell[0], cell[1]
            self.canvas.itemconfig(self.cells[row][col], fill = "black")
        self.highlights.clear()

    # draws dama cell on the canvas @ row, col
    def drawCell(self, row, col, cellcolor):
        xcoor = col * CELL_WIDTH
        ycoor = row * CELL_WIDTH
        cell = self.canvas.create_rectangle(
            xcoor, ycoor, xcoor + CELL_WIDTH, ycoor + CELL_WIDTH,
            fill = cellcolor, tag = "cell" + str(row) + str(col))
        return cell

    # draws dama corki on the canvas @ row, col,
    # returns a corki id
    def drawCorki(self, row, col):
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

    # performs a slow gui move of a corki from start cell to its landing cell
    def moveCorki(self, start_cell, landing_cell):
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
        # update the corki tag, now that it has moved to a different cell
        self.canvas.itemconfig(moving_corki, tag = "cell" + str(row2) + str(col2))
        self.corkis[row2][col2] = moving_corki
        self.corkis[row1][col1] = None
        # kingify if a teter became a king
        if row2 == 0 and self.board.isKingBottom(row2, col2):
            self.canvas.itemconfig(moving_corki, image = self.king_bottom_photo)
        if row2 == 7 and self.board.isKingTop(row2, col2):
            self.canvas.itemconfig(moving_corki, image = self.king_top_photo)
        self.canvas.update()
        # wait half a second for the move to be taken in / to be noticed
        sleep(0.5)

    """
    does what it says; displays the move to the user, wouldn't be
    a cool game to play otherwise
    """
    def animateMove(self, start_cell, landing_cell, did_eat):
        # remove the eaten corki #        
        if did_eat:
            eaten_row, eaten_col = self.board.getMiddleCell(start_cell, landing_cell)
            self.canvas.delete(self.corkis[eaten_row][eaten_col])
            self.corkis[eaten_row][eaten_col] = None
        # move the eating or moving corki to its destination
        self.moveCorki(start_cell, landing_cell)
        
    ####################################
    ###     DEBUG RELATED METHODS  #####
    ####################################

    # toggles debug mode, and enables all legal cells when debug mode is on
    def toggleDebugMode(self):
        # toggle debug state
        self.debug = not self.debug
        # make the appropriate adjustements
        if self.debug:
            # enable all legal cells
            for row in range(self.board.NUM_ROWS):
                for col in range(self.board.NUM_ROWS):
                    if not self.board.isIllegalCell(row, col):
                        self.enableCell((row, col))
        else:
            # un highlight all
            self.unhighlightAll()
            # disable all cells
            self.disableAll()
            # enable clickable human cells
            self.operator.enableClickableHumanCells()

    # adds a teter bottom corki at the current debug cell
    def addCorki(self, corki_type):
        if self.current_debug_cell is not None:
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

    # adds a teter bottom corki at the current debug cell
    def highlightNextMove(self, move_type):
        if self.current_debug_cell is not None:
            # get moves and eats at once
            cur_cell = self.current_debug_cell
            if self.board.isTop(cur_cell):
                moves_and_eats = self.operator.comp_player.movesAndEats(cur_cell)
            else:
                moves_and_eats = self.operator.human_player.movesAndEats(cur_cell)
            # get the target cells, depending on the move type
            if move_type == 'move':
                target_cells = moves_and_eats[0]
            elif move_type == 'eat':
                target_cells = moves_and_eats[1]
            else:
                return
            # highlight target cells
            for cell in target_cells:
                self.highlightCell(cell)

    # removes the selected corki from the board
    def removeCorki(self):
        # remove from the logic board
        self.board.makeEmpty(self.current_debug_cell)
        # remove from the canvas
        delete_row, delete_col = self.current_debug_cell
        self.canvas.delete(self.corkis[delete_row][delete_col])
        self.corkis[delete_row][delete_col] = None

    # shows the multiple simultaneous eats that a corki at the current cell can make.
    def showEatingSpree(self):
        paths_and_scores = self.operator.comp_player.spreePathsAndScores(self.current_debug_cell)
        if paths_and_scores:
            for path_and_score in paths_and_scores:
                path, score = path_and_score
                print("Path with score",score, ":", path)
                for i in range(len(path)):
                    self.highlightCell(path[i])
                    self.canvas.after(500)
                    self.canvas.update()
                sleep(1)
                self.unhighlightAll()

    # pickles/serializes the current board object for after restoration
    def saveBoard(self):
        with open('board.pickle', 'wb') as f:
            # Pickle the 'data' dictionary using the highest protocol available.
            pickle.dump(self.board, f, pickle.HIGHEST_PROTOCOL)

    # loads/deserializes a saved board object and sets it as the current board object
    def restoreBoard(self):
        with open('board.pickle', 'rb') as f:
            # The protocol version used is detected automatically, so we do not
            # have to specify it.
            self.board = pickle.load(f)
            self.operator.comp_player.board = self.board
            self.operator.human_player.board = self.board
            # repaint board gui
            for row in range(self.board.NUM_ROWS):
                for col in range(self.board.NUM_ROWS):
                    if self.corkis[row][col]:
                        self.canvas.delete(self.corkis[row][col])
                        self.corkis[row][col] = None
            self.paintBoard()

    # displays the (row, col) coordinates using a simple dialog window
    def displayCellCoordinates(self):
        messagebox.showinfo("Cell Coordinates", str(self.current_debug_cell))

    # displays move tree
    def displayMoveTree(self):
        self.operator.comp_player.atLastConfronted()

    def setTestDestnCell(self):
        self.test_destn_cell = self.current_debug_cell

    # adds node to queue
    def addNodeToQueue(self):
        cur_node = self.test_cur_node
        played_by_comp = self.board.isTop(self.current_debug_cell)
        spree_paths_and_scores = self.operator.comp_player.spreePathsAndScores(self.current_debug_cell)
        spree_path, score, is_eat = None, 1, False
        if spree_paths_and_scores:
            for path_and_score in spree_paths_and_scores:
                path, spree_score = path_and_score
                if path[-1] == self.test_destn_cell:
                    spree_path, score, is_eat = path, spree_score, True
                    break
        if not played_by_comp: score *= -1
        self.test_cur_node = self.operator.comp_player.addNodeToQueue(
            queue=self.test_bfs_queue, parent=self.test_cur_node, start_cell=self.current_debug_cell,
            destn_cell=self.test_destn_cell, score=score, is_eat=is_eat,spree_path=spree_path,played_by_comp=played_by_comp)
        print("Added Node:\n", self.test_cur_node)
        print("-"*50)

    def playFinalPath(self):
        self.operator.comp_player.playPath(self.test_cur_node, self)

    # pops up the right sets of context menu items when the user right clicks on the board
    def popContextMenu(self, event):
        # find clicked cell
        clicked_cell = event.y // CELL_WIDTH, event.x // CELL_WIDTH # row, col of the clicked cell
        # set clicked cell as current debug cell
        self.current_debug_cell = clicked_cell
        # clear previous commands, if there are any
        self.context_menu.delete(0, 10) # assuming the commands are less than 10 in number
        # add the appropriate commands to the menu first
        self.context_menu.add_command(label='Toggle DEBUG', command=self.toggleDebugMode)
        if self.debug:
            self.context_menu.add_command(label='Show Cell Coordinates', command=self.displayCellCoordinates)
            self.context_menu.add_command(label='Build Move Tree', command=self.displayMoveTree)
            if self.board.isEmptyLegalCell(clicked_cell): # clicked cell is empty
                self.context_menu.add_command(label='Add Teter Bottom',
                                              command=lambda corkitype='teter_bottom': self.addCorki(corkitype))
                self.context_menu.add_command(label='Add Teter Top',
                                              command=lambda corkitype='teter_top': self.addCorki(corkitype))
                self.context_menu.add_command(label='Add King Bottom',
                                              command=lambda corkitype='king_bottom': self.addCorki(corkitype))
                self.context_menu.add_command(label='Add King Top',
                                              command=lambda corkitype='king_top': self.addCorki(corkitype))
                self.context_menu.add_command(label='Save Board', command=self.saveBoard)
                self.context_menu.add_command(label='Restore Board', command=self.restoreBoard)
                self.context_menu.add_command(label='Set As Destn Cell', command=self.setTestDestnCell)
                self.context_menu.add_command(label='Play Final Path', command=self.playFinalPath)
            else: # clicked cell has corki
                self.context_menu.add_command(label='Show Next Moves',
                                              command=lambda movetype='move': self.highlightNextMove(movetype))
                self.context_menu.add_command(label='Show Next Eats',
                                              command=lambda movetype='eat': self.highlightNextMove(movetype))
                self.context_menu.add_command(label='Remove Corki', command=self.removeCorki)
                self.context_menu.add_command(label='Show Eating Spree', command=self.showEatingSpree)
                self.context_menu.add_command(label='Add Node To Queue', command=self.addNodeToQueue)
        # post the context menu at the click location
        self.context_menu.post(event.x_root, event.y_root)
