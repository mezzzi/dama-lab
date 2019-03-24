"""
==================================================
THE GameManager CLASS IS A CONTROLLER CLASS THAT
OVERSEES THE INTERACTION BETWEEN THE GUI INTERFACE
AND THE OTHER OBJECTS OF THE DAMA GAME
==================================================
"""
from board import *
from boardgui import *
from player import *
from mastermind import ComputerPlayer

class GameManager:
    def __init__(self, compPlayer, humanPlayer, boardGui):
        """
        initializes the game manager
        """
        self.comp_player = compPlayer
        self.human_player = humanPlayer
        self.board_gui = boardGui
        self.start_cell = None

    def printNumCorkis(self):
        """
        @@@ TEST @@@
        """
        print('Human Corkis:', self.human_player.getNumCorkis())
        print('Comp Corkis:', self.comp_player.getNumCorkis())
        
    def handleClick(self, now_clicked_cell):
        """
        delegates user interaction with the game gui, so appropriate
        modification can be done to the data models that the user doesn't see
        """
                
        ''' undo all highlighting and highlight only the now clicked cell '''
        self.board_gui.unhighlightAll()
        self.board_gui.highlightCell(now_clicked_cell)

        ''' handle start cell click '''
        if not self.start_cell:
            self.handleStartCellClick(now_clicked_cell)
        else:
            ''' check redundant click '''
            is_click_redundant = (self.start_cell == now_clicked_cell)
            if is_click_redundant:
                self.handleStartCellClick(now_clicked_cell)
            else:
                moves_and_eats = self.human_player.movesAndEatsCombined(self.start_cell)
                if now_clicked_cell in moves_and_eats:                   
                    ''' handle destination cell click '''
                    self.handleLandingCellClick(now_clicked_cell)
                else:
                    ''' user has changed its start cell '''
                    self.handleStartCellClick(now_clicked_cell)

    def handleStartCellClick(self, start_cell):
        """
        handles the clicking of the start cell, the cell from which the corki starts its move
        """

        ''' get next legal move and eat cells and enable them '''
        moves_and_eats = self.human_player.movesAndEatsCombined(start_cell)
        for cell in moves_and_eats:
            self.board_gui.highlightCell(cell)
            self.board_gui.enableCell(cell)
        self.start_cell = start_cell

    def handleLandingCellClick(self, landing_cell):
        """
        handles landing cell selection
        """

        ''' disable all '''
        ''' tell gui to un-highlight landing cell '''
        self.board_gui.unhighlightCell(landing_cell)
        ''' tell gui to disable all previously clickable cells '''
        self.board_gui.disableAll()
        
        ''' get the human to play its turn '''
        ''' -------------------------------'''
        is_kingified, did_eat = self.human_player.playOneTurn(self.start_cell, landing_cell)
        ''' tell gui to animate the human move for the user '''
        self.board_gui.animateMove(self.start_cell, landing_cell, did_eat)
        ''' handle more eats '''
        if (not is_kingified) and did_eat:
            more_eats = self.human_player.movesAndEats(landing_cell)[1]
            if len(more_eats) > 0:
                self.start_cell = landing_cell
                for cell in more_eats:
                    self.board_gui.highlightCell(cell)                    
                    self.board_gui.enableCell(cell)
                return
        ''' check if game is over '''
        if (self.comp_player.getNumCorkis() == 0) or (len(self.comp_player.getCanAdvanceCells()) == 0):
            print('GAME OVER, COMP LOST')
            return
        ''' get the computer to play its turn '''
        ''' ----------------------------------'''
        while True:
            is_kingified, did_eat, start_cell, landing_cell = self.comp_player.playOneTurn()
            ''' tell gui to animate the human move for the user '''        
            self.board_gui.animateMove(start_cell, landing_cell, did_eat)
            ''' its turn is over if kingified '''

            if is_kingified:
                break
            ''' check for more eats, if computer had just eaten '''
            if did_eat:
                more_eats = self.comp_player.movesAndEats(landing_cell)[1]
                if len(more_eats) > 0:
                    self.start_cell, landing_cell = landing_cell, more_eats[0]
                else:
                    break
            else:
                break
        ''' check if game is over '''
        if (self.human_player.getNumCorkis() == 0) or (len(self.human_player.getCanAdvanceCells()) == 0):
            print('GAME OVER, HUMAN LOST')
            return            
        ''' prepare the board for the human player to play its turn again'''
        ''' -------------------------------------------------------------'''
        next_clickable_cells = self.human_player.getCanAdvanceCells()
        for cell in next_clickable_cells:
            self.board_gui.enableCell(cell)
        self.start_cell = None

    def enableClickableHumanCells(self):
        """
        enables all clickable human cells
        """
        next_clickable_cells = self.human_player.getCanAdvanceCells()
        for cell in next_clickable_cells:
            self.board_gui.enableCell(cell)
            
    def startGame(self):
        """
        this is where the game kicks off
        """

        ''' enable clickable cells for the human player, so he can start playing '''
        self.enableClickableHumanCells()
        ''' begin the game '''
        self.board_gui.letTheShowBegin()

"""
============
RUN THE GAME
============
"""
if __name__ == "__main__":       
    board = DamaBoard()
    human_player = HumanPlayer(board)
    comp_player = ComputerPlayer(board)
    board_gui = DamaGui(board)
    game_manager = GameManager(comp_player, human_player, board_gui)
    board_gui.setOperator(game_manager)
    game_manager.startGame()

        
