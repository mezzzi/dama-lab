'''
==================================================
THE BOARD CLASS IS A PROGRAM MODEL OF A DAMA BOARD
==================================================
'''

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
        
        self.NUM_ROWS = NUM_ROWS # repeated simply for convinence
        ''' initialize the board, set the legal and illegal cells '''
        self.__board = [[0 for col in range(NUM_ROWS)] for row in range(NUM_ROWS)]
        for row in range(NUM_ROWS):
            for col in range(NUM_ROWS):
                self.__board[row][col] = (row + col) % 2
        '''
        lays out the cokis on the board; 24 in total, 12 for top
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
        
    def isInsideBoard(self, row, col = None):
        """
        returns true if (row, col) lies with in an 8x8 board
        """
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return (row in range(NUM_ROWS)) and (col in range(NUM_ROWS))

    def validateCell(self, row, col = None):
        """
        raises an exception if cell is not inside board
        """
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]          
        if self.isInsideBoard(row, col):
            return True
        else:
            raise Exception('Invalid Position @ ', row, col)
        
    def getNextCell(self, first_cell, second_cell):
        """
        returns the next position to be encountered
        when diagonally moving from first cell to second cell
        """
        ''' validate cells '''
        self.validateCell(first_cell)
        self.validateCell(second_cell)
        ''' do do do '''
        row1, col1, row2, col2 = first_cell[0], first_cell[1], second_cell[0], second_cell[1]
        return row2 + (row2 - row1), col2 + (col2 - col1)

    def getMiddleCell(self, start_cell, landing_cell):
        """
        yeah, literally returns the coordinates of the middle cell
        """
        ''' validate cells '''
        self.validateCell(start_cell)
        self.validateCell(landing_cell)
        ''' do do do '''        
        row1, col1, row2, col2 = start_cell[0], start_cell[1], landing_cell[0], landing_cell[1]    
        mid_row = (row1 + 1) if row2 > row1 else (row1 - 1)
        mid_col = (col1 + 1) if col2 > col1 else (col1 - 1)
        ''' validate cell '''
        self.validateCell(mid_row, mid_col)
        return (mid_row, mid_col)      

    def isForwardToTeter(self, start_cell, landing_cell):
        """
        returns true if a teter corki can move in its forward direction from start cell to landing cell
        """
        ''' validate cells '''
        self.validateCell(start_cell)
        self.validateCell(landing_cell)
        ''' do do do '''         
        row1, col1, row2, col2 = start_cell[0], start_cell[1], landing_cell[0], landing_cell[1]
        if self.isTeterTop(row1, col1):
            return row2 > row1
        else:
            return row2 < row1

    def areTeamMates(self, cell1, cell2):
        """
        returns true if corkis at positions cell1 and cell2
        are on the same team(owned by the same player)
        """
        ''' validate cells '''
        self.validateCell(cell1)
        self.validateCell(cell2)
        ''' do do do '''         
        row1, col1, row2, col2 = cell1[0], cell1[1], cell2[0], cell2[1]
        if self.isBottom(row1, col1):
            return self.isBottom(row2, col2)
        else:
            return self.isTop(row2, col2)

    def isOccupied(self, row, col = None):
        """
        returns true if row, col is occupied by any corki
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return (not (self.isEmptyLegalCell(row, col) or self.isIllegalCell(row, col)))

    def shouldKingify(self, row, col = None):
        """
        returns true if corki at given cell should be made king
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''         
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]          
        should_be_king = self.isTeter(row, col) and ((row == 0 and self.isTeterBottom(row, col)) or \
                         (row == (self.NUM_ROWS - 1) and self.isTeterTop(row, col)))
        return should_be_king
    
    def moveCorki(self, start_cell, landing_cell):
        """
        moves corki from start cell to landing cell
        """
        ''' validate cells '''
        self.validateCell(start_cell)
        self.validateCell(landing_cell)
        ''' do do do '''         
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
        """
        returns the raw value of the given cell, please do not use
        this often, it is meant mainly for debugging purposes
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col]
                     
    def isBottom(self, row, col = None):
        """
        returns true if a corki @row, col belongs to the bottom player
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]
        return self.isTeterBottom(row, col) or self.isKingBottom(row, col)

    def isTop(self, row, col = None):
        """
        returns true if a corki @row, col belongs to the top player
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.isTeterTop(row, col) or self.isKingTop(row, col)
    
    def isIllegalCell(self, row, col = None):
        """
        returns true if cell @ (row, col) is an illegal cell for any corki
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col] == ILLEGAL_CELL
    
    def isEmptyLegalCell(self, row, col = None):
        """
        returns true if cell @ (row, col) is unoccupied legal cell
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col] == EMPTY_LEGAL_CELL
        
    def isKing(self, row, col = None):
        """
        returns true if there is a king corki at row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col] in (KING_TOP, KING_BOTTOM)

    def isTeter(self, row, col = None):
        """
        returns true if there is a teter corki at row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col] in (TETER_TOP, TETER_BOTTOM)

    def isTeterTop(self, row, col = None):
        """
        returns true if there is a TETER_TOP corki at row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col] == TETER_TOP

    def isTeterBottom(self, row, col = None):
        """
        returns true if there is a TETER_BOTTOM corki at row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col] == TETER_BOTTOM
    
    def isKingTop(self, row, col = None):
        """
        returns true if there is a KING_TOP corki at row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col] == KING_TOP

    def isKingBottom(self, row, col = None):
        """
        returns true if there is a KING_BOTTOM corki at row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        return self.__board[row][col] == KING_BOTTOM

    
    '''==================================='''
    ''' setters =========================='''
    '''==================================='''
    def setCellValue(self, value, row, col = None):
        """
        sets the raw value of the given cell, please do not use
        this often, it is meant mainly for debugging purposes
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        self.__board[row][col] = value
    
    def makeEmpty(self, row, col = None):
        """
        sets cell at row, col to an empty legal cell
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        self.__board[row][col] = EMPTY_LEGAL_CELL      
        
    def makeTeterTop(self, row, col = None):
        """
        adds/sets a TETER_TOP corki @ row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        self.__board[row][col] = TETER_TOP

    def makeTeterBottom(self, row, col = None):
        """
        adds/sets a TETER_BOTTOM corki @ row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        self.__board[row][col] = TETER_BOTTOM

    def makeKingTop(self, row, col = None):
        """
        adds/sets a KING_TOP corki @ row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        self.__board[row][col] = KING_TOP

    def makeKingBottom(self, row, col = None):
        """
        adds/sets a KING_BOTTOM corki @ row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]        
        self.__board[row][col] = KING_BOTTOM

    def makeKing(self, row, col = None):
        """
        adds/sets as king corki @ row, col
        """
        ''' validate cell '''
        self.validateCell(row, col)
        ''' do do do '''          
        if col == None: # row itself is [row, col]
            row, col = row[0], row[1]
        if self.isTeterTop(row, col):
            self.makeKingTop(row, col)
        else:
            self.makeKingBottom(row, col)
