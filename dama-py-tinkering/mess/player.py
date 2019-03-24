"""
==================================================
THE Player IS A PROGRAM MODEL OF A DAMA PLAYER
==================================================
"""

NEIGHBOR_OFFSETS = ((-1, -1), (-1, 1), (1, 1), (1, -1))

class Player:
        
    def __init__(self, dama_board, is_top):
        """ creates a player object """
        self.board = dama_board
        self.is_top = is_top
        
    def getNumCorkis(self):
        """
        returns the number of corkis that a player have left on board
        """
        num_corkis = 0
        for row in range(self.board.NUM_ROWS):
            for col in range(self.board.NUM_ROWS):
                if self.is_top:
                    num_corkis += (self.is_top and self.board.isTop(row, col))
                else:
                    num_corkis += ((not self.is_top) and self.board.isBottom(row, col))
        return num_corkis
    
    def movesAndEats(self, row, col = None):
        """
        returns the cells a given corki@cur_cell can legally
        move to in one step, in Egregna Dama game.
        """
        if col is None: # row itself is [row, col]
            cur_cell = row            
        else:
            cur_cell = (row, col)
        legal_move_cells, legal_eat_cells = [], []
        for offset in NEIGHBOR_OFFSETS:
            neighbor_cell = cur_cell[0] + offset[0], cur_cell[1] + offset[1]
            if self.board.isInsideBoard(neighbor_cell):           
                if self.canMove(cur_cell, neighbor_cell):
                    legal_move_cells.append(neighbor_cell)
                else:
                    if self.canEat(cur_cell, neighbor_cell):
                        landing_cell = self.board.getNextCell(cur_cell, neighbor_cell)                      
                        legal_eat_cells.append(landing_cell)
        return legal_move_cells, legal_eat_cells

    def canMove(self, from_cell, to_cell):
        """
        returns true if corki at from_cell can move to the to_cell
        """
        return self.board.isEmptyLegalCell(to_cell) and \
               (self.board.isKing(from_cell) or
                self.board.isForwardToTeter(from_cell, to_cell))

    def canEat(self, from_cell, next_cell):
        """
        returns true if corki at from_cell can eat corki at to_cell
        """
        if self.board.areTeamMates(from_cell, next_cell):
            return False
        else:
            landing_cell = self.board.getNextCell(from_cell, next_cell)
            landing_cell_ok = self.board.isInsideBoard(landing_cell) and \
                              self.board.isEmptyLegalCell(landing_cell)
            is_eat_allowed = self.board.isKing(from_cell) or \
                              ( self.board.isTeter(from_cell) and
                                self.board.isForwardToTeter(from_cell, next_cell) )
            return landing_cell_ok and is_eat_allowed

    def movesAndEatsCombined(self, cell):
        """
        returns the next legal moves from cell
        """
        moves_and_eats = self.movesAndEats(cell)
        moves_and_eats[0].extend(moves_and_eats[1])
        return set(moves_and_eats[0])

    def getCanAdvanceCells(self):
        """
        returns cells whose corki can either eat or move
        """
        can_advance_cells = []
        for row in range(self.board.NUM_ROWS):
            for col in range(self.board.NUM_ROWS):
                if (self.is_top and self.board.isTop(row, col)) or \
                        ((not self.is_top) and self.board.isBottom(row, col)):
                    moves_and_eats = self.movesAndEats(row, col)
                    num_moves = len(moves_and_eats[0])
                    num_eats = len(moves_and_eats[1])
                    if (num_moves > 0) or (num_eats > 0):
                        can_advance_cells.append((row, col))
        return can_advance_cells
    
    def playOneTurn(self, start_cell, landing_cell, isAnEatMove):
        """ 
        performs dama move from start_cell to landing_cell,
        returns is_kingified
        """
        ''' perform move '''
        self.board.moveCorki(start_cell, landing_cell)
        ''' eat, if it is an eat move '''
        if isAnEatMove:
            mid_cell = self.board.getMiddleCell(start_cell, landing_cell)
            self.board.makeEmpty(mid_cell)
        ''' kingify if necessary '''
        is_kingified = False
        if self.board.shouldKingify(landing_cell):
            self.board.makeKing(landing_cell)
            is_kingified = True
        return is_kingified

class HumanPlayer(Player):
    
    def __init__(self, dama_board):
        super().__init__(dama_board, is_top = False)

    def playOneTurn(self, start_cell, landing_cell, isAnEatMove=False):
        """ 
        performs dama move from start_cell to landing_cell,
        returns is_kingified, isAnEatMove, (startcell, endcell)
        """ 
        moves_and_eats = self.movesAndEats(start_cell[0], start_cell[1])
        isAnEatMove = len(moves_and_eats[1]) > 0
        is_kingified = super().playOneTurn(start_cell, landing_cell, isAnEatMove)
        return is_kingified, isAnEatMove 
