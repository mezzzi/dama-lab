"""
==================================================
THE Player IS A PROGRAM MODEL OF A DAMA PLAYER
==================================================
"""

NEIGHBOR_OFFSETS = ((-1, -1), (-1, 1), (1, 1), (1, -1))

class Player:

    # create player object
    def __init__(self, dama_board, is_top):
        self.board = dama_board
        self.is_top = is_top

        self.teter_eat_first = False

        self.teter_eats, self.king_eats = [], []
        self.teter_moves, self.king_moves = [], []

    # returns the number of corkis that a player have left on board
    def getNumCorkis(self):
        num_corkis = 0
        for row in range(self.board.NUM_ROWS):
            for col in range(self.board.NUM_ROWS):
                if self.is_top:
                    num_corkis += (self.is_top and self.board.isTop(row, col))
                else:
                    num_corkis += ((not self.is_top) and self.board.isBottom(row, col))
        return num_corkis

    # checks whether corki at the given cell belongs to the current player
    def isOwnCorki(self, row, col):
        return (self.is_top and self.board.isTop(row, col)) or \
               ((not self.is_top) and self.board.isBottom(row, col))

    '''
    goes through all corkis that belong to the comp-layer checking
    for possible moves and eats, and stores these moves and eats as
    instance variables
    '''
    def markAllMovesAndEats(self):
        teter_moves = []; king_moves = []; teter_eats = []; king_eats = []
        for row in range(self.board.NUM_ROWS):
            for col in range(self.board.NUM_ROWS):
                if self.isOwnCorki(row, col):
                    moves_and_eats = self.movesAndEats(row, col)
                    if len(moves_and_eats[1]) > 0:
                        eats = moves_and_eats[1]
                        if self.board.isTeter(row, col):
                            teter_eats.append(((row, col), eats))
                        else:
                            king_eats.append(((row, col), eats))
                    elif len(moves_and_eats[0]) > 0:
                        moves = moves_and_eats[0]
                        if self.board.isTeter(row, col):
                            teter_moves.append(((row, col), moves))
                        else:
                            king_moves.append(((row, col), moves))
        # set the corresponding instance variables
        self.teter_moves = teter_moves; self.king_moves = king_moves
        self.teter_eats = teter_eats; self.king_eats = king_eats

    # returns the cells a given corki@cur_cell can legally move to in one step, in Egregna Dama game.
    def movesAndEats(self, row, col = None):
        if col is None: cur_cell = row
        else: cur_cell = (row, col)
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

    # returns true if corki at from_cell can move to the to_cell
    def canMove(self, from_cell, to_cell):
        return self.board.isEmptyLegalCell(to_cell) and \
               (self.board.isKing(from_cell) or self.board.isForwardToTeter(from_cell, to_cell))

    # returns true if corki at from_cell can eat corki at to_cell
    def canEat(self, from_cell, next_cell):
        if self.board.areTeamMates(from_cell, next_cell):
            return False
        else:
            landing_cell = self.board.getNextCell(from_cell, next_cell)
            landing_cell_ok = self.board.isInsideBoard(landing_cell) and self.board.isEmptyLegalCell(landing_cell)
            is_eat_allowed = self.board.isKing(from_cell) or \
                              ( self.board.isTeter(from_cell) and self.board.isTeter(next_cell) and
                                self.board.isForwardToTeter(from_cell, next_cell) )
            return landing_cell_ok and is_eat_allowed

    # returns the next legal moves from cell
    def movesAndEatsCombined(self, cell):
        moves_and_eats = self.movesAndEats(cell)
        moves_and_eats[0].extend(moves_and_eats[1])
        return set(moves_and_eats[0])

    # returns cells whose corki can either eat or move
    def getCanAdvanceCells(self):
        can_advance_cells = []
        for row in range(self.board.NUM_ROWS):
            for col in range(self.board.NUM_ROWS):
                if self.isOwnCorki(row, col):
                    moves_and_eats = self.movesAndEats(row, col)
                    num_moves = len(moves_and_eats[0])
                    num_eats = len(moves_and_eats[1])
                    if (num_moves > 0) or (num_eats > 0):
                        can_advance_cells.append((row, col))
        return can_advance_cells

    # performs dama move from start_cell to landing_cell, returns is_kingified
    def playOneStep(self, start_cell, landing_cell, isAnEatMove):
        self.board.moveCorki(start_cell, landing_cell) # perform move
        if isAnEatMove: # eat, if it is an eat move
            mid_cell = self.board.getMiddleCell(start_cell, landing_cell)
            self.board.makeEmpty(mid_cell)
        is_kingified = False
        if self.board.shouldKingify(landing_cell): # kingify if necessary
            self.board.makeKing(landing_cell)
            is_kingified = True
        return is_kingified

class HumanPlayer(Player):
    
    def __init__(self, dama_board, is_top=False):
        super().__init__(dama_board, is_top=is_top)

    # performs dama move from start_cell to landing_cell,
    # returns is_kingified, isAnEatMove, (startcell, endcell)
    def playOneStep(self, start_cell, landing_cell, isAnEatMove=False):
        moves_and_eats = self.movesAndEats(start_cell[0], start_cell[1])
        isAnEatMove = len(moves_and_eats[1]) > 0
        is_kingified = super().playOneStep(start_cell, landing_cell, isAnEatMove)
        return is_kingified, isAnEatMove 
