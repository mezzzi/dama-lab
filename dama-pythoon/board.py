"""
==================================================
THE BOARD CLASS IS A PROGRAM MODEL OF A DAMA BOARD
==================================================
"""

NUM_ROWS = 8  # total number of rows in the dama board(8x8 board in this case), which is also equal with num cols
NUM_HOME_ROWS = 3 # the number of home rows for a given player when the game begins, it is 3 for an 8x8 dama board

ILLEGAL_CELL = 0 # 0
EMPTY_LEGAL_CELL = 1 # 1
TETER_TOP = 3 # 3
KING_TOP = TETER_TOP + 1 # 4
TETER_BOTTOM = KING_TOP + 4 # 8
KING_BOTTOM = TETER_BOTTOM + 1 # 9

class DamaBoard:

    ###############################################
    ####### THE CONSTRUCTOR #######################
    ###############################################
    
    def __init__(self):
        """
        creates an 8 x 8 dama board as list of lists,
        list value 1 mean legal board position,
        while array value 0 mean illegal board position 
        """
        
        self.NUM_ROWS = NUM_ROWS # repeated simply for convenience
        ''' initialize the board, set the legal and illegal cells '''
        self.__board = [[0 for col in range(NUM_ROWS)] for row in range(NUM_ROWS)]
        for row in range(NUM_ROWS):
            for col in range(NUM_ROWS):
                self.__board[row][col] = (row + col) % 2
                
        '''
        lays out the corkis on the board; 24 in total, 12 for top
        player and another 12 for the bottom player.
        '''       
        for row in range(NUM_HOME_ROWS):
            for col in range(NUM_ROWS):
                if (row + col) % 2 :
                    self.__board[row][col] = TETER_TOP
                    self.__board[row + NUM_ROWS - NUM_HOME_ROWS][NUM_ROWS - 1 - col] = TETER_BOTTOM
                    

    ###############################################
    ####### HELPER METHODS ########################
    ###############################################

    def printOccupieds(self):
        """ @@@ THIS IS FOR TEST PURPOSE @@@ """
        print('\n' + '-' * 16)
        for row in range(NUM_ROWS):
            print('\n' + str(row), end = ' ')
            for col in range(NUM_ROWS):
                if self.isOccupied(row, col):
                    val = 'T' if self.isTop(row, col) else 'B'
                    print(str(col) + val, end = ', ')

    # returns true if (row, col) lies with in an 8x8 board
    @staticmethod
    def isInsideBoard(row, col = None):
        if col is None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return (row in range(NUM_ROWS)) and (col in range(NUM_ROWS))

    """
    returns the next position to be encountered
    when diagonally moving from first cell to second cell
    """
    @staticmethod
    def getNextCell(first_cell, second_cell):
        row1, col1, row2, col2 = first_cell[0], first_cell[1], second_cell[0], second_cell[1]
        return row2 + (row2 - row1), col2 + (col2 - col1)

    # yeah, literally returns the coordinates of the middle cell
    @staticmethod
    def getMiddleCell(start_cell, landing_cell):
        row1, col1, row2, col2 = start_cell[0], start_cell[1], landing_cell[0], landing_cell[1]    
        mid_row = (row1 + 1) if row2 > row1 else (row1 - 1)
        mid_col = (col1 + 1) if col2 > col1 else (col1 - 1)
        return mid_row, mid_col

    # returns true if a teter corki can move in its forward direction from start cell to landing cell
    def isForwardToTeter(self, start_cell, landing_cell):
        row1, col1, row2, col2 = start_cell[0], start_cell[1], landing_cell[0], landing_cell[1]
        if self.isTeterTop(row1, col1):
            return row2 > row1
        else:
            return row2 < row1

    """
    returns true if corkis at positions cell1 and cell2
    are on the same team(owned by the same player)
    """
    def areTeamMates(self, cell1, cell2):
        row1, col1, row2, col2 = cell1[0], cell1[1], cell2[0], cell2[1]
        
        if self.isBottom(row1, col1):
            return self.isBottom(row2, col2)
        elif self.isTop(row1, col1):
            return self.isTop(row2, col2)
        else:
            return False

    # returns true only if both cells have corkis and the corkis are opponents
    def areOpponents(self, cell1, cell2):
        
        row1, col1, row2, col2 = cell1[0], cell1[1], cell2[0], cell2[1]
        if self.isBottom(row1, col1):
            return self.isTop(row2, col2)
        elif self.isTop(row1, col1):
            return self.isBottom(row2, col2)
        else:
            return False

    # returns true if row, col is occupied by any corki
    def isOccupied(self, row, col = None):
        return not (self.isEmptyLegalCell(row, col) or self.isIllegalCell(row, col))

    # returns true if corki at given cell should be made king
    def shouldKingify(self, row, col = None):
        if col is None: # row itself is [row, col]
            row, col = row[0], row[1]          
        should_be_king = self.isTeter(row, col) and ((row == 0 and self.isTeterBottom(row, col)) or
                         (row == (self.NUM_ROWS - 1) and self.isTeterTop(row, col)))
        return should_be_king

     # returns true if corki at start cell should be made king when landing on destination cell
    def shouldCrown(self, startCell, destnCell):
        row, col = destnCell
        should_be_king = self.isTeter(startCell) and ((row == 0 and self.isTeterBottom(startCell)) or
                         (row == (self.NUM_ROWS - 1) and self.isTeterTop(startCell)))
        return should_be_king

    # moves corki from start cell to landing cell
    def moveCorki(self, start_cell, landing_cell):
        row1, col1, row2, col2 = start_cell[0], start_cell[1], landing_cell[0], landing_cell[1]
        self.__board[row2][col2] = self.__board[row1][col1]
        self.makeEmpty(row1, col1)               

    ###############################################
    ####### SETTERS AND GETTERS ###################
    ###############################################

    '''==================================='''
    ''' getters =========================='''
    '''==================================='''

    def getCellValue(self, row, col = None):
        if col is None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col]

    def isBottom(self, row, col = None):
        return self.isTeterBottom(row, col) or self.isKingBottom(row, col)

    def isTop(self, row, col = None):
        return self.isTeterTop(row, col) or self.isKingTop(row, col)

    def isIllegalCell(self, row, col = None):
        return self.getCellValue(row, col) == ILLEGAL_CELL

    def isEmptyLegalCell(self, row, col = None):
        return self.getCellValue(row, col) == EMPTY_LEGAL_CELL

    def isKing(self, row, col = None):
        return self.getCellValue(row, col) in (KING_TOP, KING_BOTTOM)

    def isTeter(self, row, col = None):
        return self.getCellValue(row, col) in (TETER_TOP, TETER_BOTTOM)

    def isTeterTop(self, row, col = None):
        return self.getCellValue(row, col) == TETER_TOP

    def isTeterBottom(self, row, col = None):
        return self.getCellValue(row, col) == TETER_BOTTOM

    def isKingTop(self, row, col = None):
        return self.getCellValue(row, col) == KING_TOP

    def isKingBottom(self, row, col = None):
        return self.getCellValue(row, col) == KING_BOTTOM

    
    '''==================================='''
    ''' setters =========================='''
    '''==================================='''

    def setCellValue(self, value, row, col = None):
        if col is None: # row itself is [row, col]
            row, col = row[0], row[1]        
        self.__board[row][col] = value

    def makeEmpty(self, row, col = None):
        self.setCellValue(EMPTY_LEGAL_CELL, row, col)       

    def makeTeterTop(self, row, col = None):
        self.setCellValue(TETER_TOP, row, col) 

    def makeTeterBottom(self, row, col = None):
        self.setCellValue(TETER_BOTTOM, row, col)  

    def makeKingTop(self, row, col = None):
        self.setCellValue(KING_TOP, row, col)  

    def makeKingBottom(self, row, col = None):
        self.setCellValue(KING_BOTTOM, row, col)  

    def makeKing(self, row, col = None):
        if self.isTeterTop(row, col):
            self.makeKingTop(row, col)
        else:
            self.makeKingBottom(row, col)
