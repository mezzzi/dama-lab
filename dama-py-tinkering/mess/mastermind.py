from player import Player
from copy import deepcopy

'''
=========================================================================
The ComputerPlayer class represents the object that does the thinking job for
the comp-player object. It basically determines what the next should best
move should be as calculated by some winning strategy.
==========================================================================
'''

class ComputerPlayer(Player):
    def __init__(self, target_board):
        """
        initializes the MasterMind; target_board is a board object with its
        current status(position of corkis) kept intact.
        """
        super().__init__(target_board, is_top = True)
        
    def getComputersMove(self):
        """
        returns the computers move as ((row1, col1), (row2, col2)), bool
        where (row1, col1) is starting cell, (row2, col2) is
        destination cell, and bool is True if the move is an eat
        move, False if the move is a non-eat move.
        It is assumed that the computer takes the top position
        """
        self.markAllMovesAndEats()
        ''' check for kingifing advance '''
        kingfing_advance = self.getKingfingAdvance()
        if kingfing_advance != None:
            return kingfing_advance
        ''' A simplified prioritizing rule: '''
        ''' ------------------------------- '''
        ''' first teter-eats, then king-eats, then king-moves, finally tete-moves '''
        if len(self.teter_eats) > 0: # teter eats first
            return self.teter_eats[0][0], self.teter_eats[0][1], True
        elif len(self.king_eats) > 0: # king eats second
            return self.king_eats[0][0], self.king_eats[0][1], True
        elif len(self.king_moves) > 0: # king moves third
            return self.king_moves[0][0], self.king_moves[0][1], False
        elif len(self.teter_moves) > 0: # teter moves fourth
            return self.teter_moves[0][0], self.teter_moves[0][1], False
        else:
            return None

    def getKingfingAdvance(self):
        """
        Identifies the favorable advance that turns a teter corki into a king.
        returns None if no kingfing advance is found, otherwise returns the
        found advance as ((row1, col1), (row2, col2)), bool
        where (row1, col1) is starting cell, (row2, col2) is
        destination cell, and bool is True if the move is an eat
        move, False if the move is a non-eat move.
        """

        ''' check the teter eats first '''
        for eat in self.teter_eats:
            row, col = eat[1] # destination cell
            if row == (self.board.NUM_ROWS - 1):
                return eat[0], eat[1], True
        ''' check teter moves second '''
        for move in self.teter_moves:
            row, col = move[1] # destination cell
            if row == (self.board.NUM_ROWS - 1):
                return move[0], move[1], False
        ''' return None, no kingifying advance found '''
        return None
        
        
    def playOneTurn(self):
        """ 
        performs dama move from start_cell to landing_cell,
        returns is_kingified, isAnEatMove, (startcell, endcell)
        """
        start_cell, landing_cell, isAnEatMove = self.getComputersMove()
        is_kingified = super().playOneTurn(start_cell, landing_cell, isAnEatMove)
        return is_kingified, isAnEatMove, start_cell, landing_cell        
    
    def markAllMovesAndEats(self):
        """
        goes through all corkis that belong to the comp-layer checking
        for possible moves and eats, and stores these moves and eats as
        instance variables
        """
        teter_moves = []; king_moves = []; teter_eats = []; king_eats = []
        for row in range(self.board.NUM_ROWS):
            for col in range(self.board.NUM_ROWS):
                if self.board.isTop(row, col):
                    moves_and_eats = self.movesAndEats(row, col)
                    if len(moves_and_eats[1]) > 0:
                        if self.board.isTeter(row, col):
                            teter_eats.append(((row, col), moves_and_eats[1][0]))
                        else:
                            king_eats.append(((row, col), moves_and_eats[1][0]))
                    elif len(moves_and_eats[0]) > 0:
                        if self.board.isTeter(row, col):
                            teter_moves.append(((row, col), moves_and_eats[0][0]))
                        else:
                            king_moves.append(((row, col), moves_and_eats[0][0]))
        ''' set the corresponding instance variables '''
        self.teter_moves = teter_moves; self.king_moves = king_moves;
        self.teter_eats = teter_eats; self.king_eats = king_eats;

    def getEatPathsFromANode(self, start_node):
        """
        returns all possible eat paths as something like:[[0, 2], [0, 1, 4], [0, 1, 6], [0, 3, 7], [0, 3, 8], [0, 1, 5, 9]]
        and also returns the maximum number of eats(which in the code below is stored in the variable called tree_depth).
        This method in a nutshell is a recursive method that traces a dama-board's eat tree recursively. But here it is implemented
        using while loop ( by tracing each level of the tree at a time) so as to save time and resource.
        """
        eat_paths = [[start_node]] # e.g [[0, 2], [0, 1, 4], [0, 1, 6], [0, 3, 7], [0, 3, 8], [0, 1, 5, 9]]
        crrent_from_nodes = {start_node} # making it a set prevents duplication
        tree_depth = 0
        eating_corki = self.board.getCellValue(start_node) # the eating_corki
        while True:
            next_from_nodes = set() # making it a set prevents duplication
            last_depth_reached = [True] # suppose that last depth is reached, it is wrapped by a list so it can be passed-by-reference
            for from_node in crrent_from_nodes:
                ''' place the corki at this node first '''
                self.board.setCellValue(eating_corki, from_node)
                ''' check if the from_node is parent to some children '''
                eatable_children = self.movesAndEats(from_node)[1] # eatable children
                ''' if we have got children, there is work to be done '''                    
                if eatable_children:
                    self.addPathsLeadingToTheChildren(eat_paths, from_node, eatable_children, last_depth_reached)
                    next_from_nodes.update(eatable_children)                    
                ''' remove the corki from this node '''
                self.board.makeEmpty(from_node)
            if last_depth_reached[0]:
                ''' place the eating corki back to the start node '''
                self.board.setCellValue(eating_corki, start_node)
                return eat_paths, tree_depth
            tree_depth += 1
            crrent_from_nodes = deepcopy(next_from_nodes)

    def addPathsLeadingToTheChildren(self, eat_paths, from_node, eatable_children, last_depth_reached):
        """
        Say there is a path [1, 2, 3]. Now 3 is the path's last node. Now if this node is found
        to have two children say 4 and 6. Then this function deletes the old path [1 2 3] and
        adds to the path list the new paths [1, 2, 3, 4] and [1, 2, 3, 6].
        """

        '''loop through the paths in the path list, to find the path that
        ends with the from_node '''
        old_eat_paths = deepcopy(eat_paths) # so that new-path addition and old-path removals can safely be done to eat_paths
        for i in range(len(old_eat_paths)):
            ith_eat_path = deepcopy(old_eat_paths[i])
            ''' check if current path ends with the from_node '''
            if ith_eat_path[-1] == from_node: 
                old_path_extended = False # suppose old path extends to no children
                eaten_corkis = self.getEatenCorkis(ith_eat_path) # get corkis eaten so far
                # add new path leading to each new children
                for child in eatable_children:
                    almost_eaten_corki = self.board.getMiddleCell(from_node, child) # corki about to be eaten
                    ''' if child has already been eaten, don't append it to an eat-path '''
                    if almost_eaten_corki not in eaten_corkis: 
                        last_depth_reached[0] = False # because at least one unredundant eatable-child is found
                        new_path = deepcopy(ith_eat_path)
                        new_path.append(child)
                        eat_paths.append(new_path)
                        old_path_extended = True # yes, old path has just been extended
                # remove old path, if it has been extended
                if old_path_extended:
                    eat_paths.remove(ith_eat_path) # remove old path, because it is extended
        ''' remove redundant eats once done with the updating '''          
        return last_depth_reached

    def getEatenCorkis(self, eat_path):
        """
        returns corkis(as list of [row, col] cells) eaten upto the given node
        """
        eaten_corkis = []
        for i in range(len(eat_path) - 1):
            eaten_corkis.append(self.board.getMiddleCell(eat_path[i], eat_path[i + 1]))
        return eaten_corkis
