from player import Player
from copy import deepcopy
from queue import Queue

"""
=========================================================================
The ComputerPlayer class represents the object that does the thinking job for
the comp-player object. It basically determines what the next should best
move should be as calculated by some winning strategy.
==========================================================================
"""

class ComputerPlayer(Player):

    def __init__(self, target_board):
        """
        initializes the MasterMind; target_board is a board object with its
        current status(position of corkis) kept intact.
        """
        super().__init__(target_board, is_top = True)
        self.TETER_EAT_SCORE = 4
        self.KING_EAT_SCORE = 6
        self.KINGIFY_SCORE = 4
        self.BFS_DEPTH = 6

    '''
        returns all possible eat paths as something like:[[0, 2], [0, 1, 4], [0, 1, 6], [0, 3, 7], [0, 3, 8], [0, 1, 5, 9]]
        and also returns the maximum number of eats(which in the code below is stored in the variable called tree_depth).
        This method in a nutshell is a recursive method that traces a dama-board's eat tree recursively. But here it is implemented
        using while loop ( by tracing each level of the tree at a time) so as to save time and resource.
    '''
    def getEatPathsFromANode(self, start_node):
        eat_paths = [[start_node]] # e.g [[0, 2], [0, 1, 4], [0, 1, 6], [0, 3, 7], [0, 3, 8], [0, 1, 5, 9]]
        current_from_nodes = {start_node} # making it a set prevents duplication
        tree_depth = 0
        eating_corki = self.board.getCellValue(start_node) # the eating_corki
        while True:
            next_from_nodes = set() # making it a set prevents duplication
            last_depth_reached = [True] # suppose that last depth is reached, it is wrapped by a list so it can be passed-by-reference
            for from_node in current_from_nodes:
                self.board.setCellValue(eating_corki, from_node) # place the corki at this node first
                # check if the from_node is parent to some children
                eatable_children = self.movesAndEats(from_node)[1] # eatable children
                if eatable_children: # if we have got children, there is work to be done
                    self.addPathsLeadingToTheChildren(eat_paths, from_node, eatable_children, last_depth_reached)
                    next_from_nodes.update(eatable_children)                    
                self.board.makeEmpty(from_node) # remove the corki from this node
            if last_depth_reached[0]:
                self.board.setCellValue(eating_corki, start_node) #place the eating corki back to the start node
                return eat_paths, tree_depth
            tree_depth += 1
            current_from_nodes = deepcopy(next_from_nodes)

    '''
    Say there is a path [1, 2, 3]. Now 3 is the path's last node. Now if this node is found
    to have two children say 4 and 6. Then this function deletes the old path [1 2 3] and
    adds to the path list the new paths [1, 2, 3, 4] and [1, 2, 3, 6].
    '''
    def addPathsLeadingToTheChildren(self, eat_paths, from_node, eatable_children, last_depth_reached):
        # loop through the paths in the path list, to find the path that ends with the from_node 
        old_eat_paths = deepcopy(eat_paths) # so that new-path addition and old-path removals can safely be done to eat_paths
        for i in range(len(old_eat_paths)):
            ith_eat_path = deepcopy(old_eat_paths[i])
            # check if current path ends with the from_node 
            if ith_eat_path[-1] == from_node: 
                old_path_extended = False # suppose old path extends to no children
                eaten_corkis = self.getEatenCorkis(ith_eat_path) # get corkis eaten so far
                # add new path leading to each new children
                for child in eatable_children:
                    almost_eaten_corki = self.board.getMiddleCell(from_node, child) # corki about to be eaten
                    # if child has already been eaten, don't append it to an eat-path 
                    if almost_eaten_corki not in eaten_corkis: 
                        last_depth_reached[0] = False # because at least one non-redundant eatable-child is found
                        new_path = deepcopy(ith_eat_path)
                        new_path.append(child)
                        eat_paths.append(new_path)
                        old_path_extended = True # yes, old path has just been extended
                # remove old path, if it has been extended
                if old_path_extended:
                    eat_paths.remove(ith_eat_path) # remove old path, because it is extended
        return last_depth_reached

    # returns corkis(as list of [row, col] cells) eaten up to the given node
    def getEatenCorkis(self, eat_path):
        eaten_corkis = []
        for i in range(len(eat_path) - 1):
            eaten_corkis.append(self.board.getMiddleCell(eat_path[i], eat_path[i + 1]))
        return eaten_corkis

    # well this is it man, good luck
    def damaBFS(self, max_level):
        main_board, self.board  = self.board, deepcopy(self.board) # persist board state
        queue, dummy_root_node = Queue(), AdvancingNode() # start with a dummy node as a root
        queue.put(dummy_root_node) # put the dummy node into the bfs queue
        while not queue.empty(): # terminate when queue is empty or when the desired level is reached
            old_board, cur_node = deepcopy(self.board), queue.get()
            if (cur_node.level + 1) == max_level: break # break when max level is reached
            if cur_node.parent is not None: self.playPath(cur_node) # modify self.board to reflect the state of current node
            self.is_top = not cur_node.played_by_comp # adjust position appropriately
            self.markAllMovesAndEats() # mark moves and eats
            if self.teter_eat_first and self.teter_eats: # check for teter eats first
                self.addEatsToQueue(queue, self.teter_eats, cur_node)
            else: # be sure to consider efita
                self.addEatsToQueue(queue, self.king_eats, cur_node)
                self.addEatsToQueue(queue, self.teter_eats, cur_node)
                self.addMovesToQueue(queue, cur_node)
            self.board = old_board # return board to its former state, i.e undo the changes
        self.board, self.is_top = main_board, True # restore main board
        return dummy_root_node # there should be an only children

    # adds moves to bfs queue
    def addMovesToQueue(self, queue, parent):
        for moves in [self.teter_moves, self.king_moves]:
            for move in moves:
                for destn_cell in move[1]: # add move advances to the bfs queue
                    self.addNodeToQueue(queue=queue, parent=parent, start_cell=move[0], destn_cell=destn_cell,
                                   score=0, is_eat=False, spree_path=None, played_by_comp=self.is_top)

    # adds eats of the corki at the given cell to the given queue
    def addEatsToQueue(self, queue, eats, parent):
        for eat in eats:
            cell = eat[0]
            spree_paths_and_scores = self.spreePathsAndScores(cell)
            for path_and_score in spree_paths_and_scores:
                spree_path, cur_score = path_and_score
                self.addNodeToQueue(queue=queue, parent=parent, start_cell=cell,
                               destn_cell=spree_path[-1], score=cur_score, is_eat=True,
                               spree_path=spree_path, played_by_comp=self.is_top)

    # chooses or marks the best possible path by taking into account the opponent's smart
    # counter moves
    def markBestPath(self, node):
        if node.score_fixed or not node.children: # leaf node
            return node.score
        else: # choose the max of the children
            max_score, max_node = None, None
            coeff = -1 if node.played_by_comp else 1
            for child in node.children:
                score = self.markBestPath(child)
                if max_score is None:
                    max_score, max_node = score, child
                elif coeff*score > coeff*max_score:
                    max_score, max_node = score, child
            node.children = [max_node] # make max node the only child
            node.score += max_score # update node score
            node.score_fixed = True
            return node.score

    # gets the spree paths and their associated scores
    def spreePathsAndScores(self, start_cell):
        eat_paths = self.getEatPathsFromANode(start_cell)[0]
        is_teter, paths_and_scores = self.board.isTeter(start_cell), []
        if eat_paths:
            for eat_path in eat_paths:
                path_score = 0
                for i in range(len(eat_path) - 1):
                    eaten_cell = self.board.getMiddleCell(eat_path[i], eat_path[i + 1])
                    path_score = (path_score + self.TETER_EAT_SCORE) if self.board.isTeter(eaten_cell) else (path_score + self.KING_EAT_SCORE)
                paths_and_scores.append([eat_path, path_score])
        return paths_and_scores

    # plays path from root to current node on the current self.board
    def playPath(self, node, animator=None):
        # get the path from the root up to cur_node
        path = []
        while node.parent is not None:
            path.append(node)
            node = node.parent
        path.reverse() # reverse path so as to start from the root
        # bring the board to the state of the current node by running the path
        for i in range(len(path)):
            node = path[i]
            self.is_top = node.played_by_comp # adjust position appropriately
            if node.spree_path is None:
                self.playOneStep(node.start_cell, node.destn_cell, node.is_eat)
                if animator: animator.animateMove(node.start_cell, node.destn_cell, node.is_eat)
            else:
                spree_path = node.spree_path
                for j in range(len(spree_path) - 1):
                    self.playOneStep(spree_path[j], spree_path[j+1], True)
                    if animator: animator.animateMove(spree_path[j], spree_path[j+1], True)

    # plays the computer's turn and returns a list of the form [is_eat, start_cell, destn_cell]
    def playTurn(self, debug=True):
        advances = []
        num_corkis = self.getNumCorkis()
        self.BFS_DEPTH -= (num_corkis // 5)
        root_node = self.damaBFS(4)
        if debug: self.printFullTree(root_node)
        self.markBestPath(root_node)
        if debug: self.printFinalTree(root_node)
        best_next_node = root_node.children[0]
        if best_next_node.is_eat:
            spree_path = best_next_node.spree_path
            for j in range(len(spree_path) - 1):
                advances.append([True, spree_path[j], spree_path[j+1]])
        else:
            advances.append([False, best_next_node.start_cell, best_next_node.destn_cell])
        return advances

    # adds plain node(unscored) to a queue
    def addPlainNodeToQueue(self, queue=None, parent=None, start_cell=None, destn_cell=None, score=0, is_eat=False,
                       spree_path=None, played_by_comp=True):
        node = AdvancingNode(parent=parent, level=parent.level+1, score=score,
                             start_cell=start_cell, destn_cell=destn_cell, is_eat=is_eat,
                             spree_path=spree_path, played_by_comp=played_by_comp)
        parent.children.append(node)
        queue.put(node) # add node to the bfs queue for further processing dow the tree
        return node

    # adds node to a queue
    def addNodeToQueue(self, queue=None, parent=None, start_cell=None, destn_cell=None, score=0, is_eat=False,
                       spree_path=None, played_by_comp=True):
        if self.board.shouldCrown(start_cell, destn_cell):
            score += self.KINGIFY_SCORE # add score for kingification
        if not played_by_comp: score *= -1
        node = AdvancingNode(parent=parent, level=parent.level+1, score=score,
                             start_cell=start_cell, destn_cell=destn_cell, is_eat=is_eat,
                             spree_path=spree_path, played_by_comp=played_by_comp)
        parent.children.append(node)
        queue.put(node) # add node to the bfs queue for further processing dow the tree
        return node

    # checks whether the corki at the given cell has escaped into a region from where it
    # can easily become a king.
    def canEasilyAttainKingship(self, cell):
        if not self.board.isTeter(cell): return False # only teter can attain kingship
        if (not self.isOutsideHomeTerritory(cell))  and self.isOpponentsBackDefenceFull(cell):
            return False
        else:
            return self.isTherePathToTheCrown(cell)

    # checks whether corki at the given cell is close
    # to the opponent's back defence
    def isOutsideHomeTerritory(self, cell):
        row = cell[0]
        if self.board.isTop(cell): return row > 2
        else: return row < (self.board.NUM_ROWS - 3)

    # checks if the opponents back defence is full
    def isOpponentsBackDefenceFull(self, cell):
        rowToCheck = (self.board.NUM_ROWS - 1) if self.board.isTop(cell) else 0
        for col in range(self.board.NUM_ROWS):
            cellToCheck = [rowToCheck, col]
            if (not self.board.isOccupied(cellToCheck)) or \
                    (not self.board.areTeamMates(cell, cellToCheck)):
                return False
        return True

    # checks if there is a path between current cell and the king's thrones
    def isTherePathToTheCrown(self, cell):
        originalBoard, queue, isMoverTop = deepcopy(self.board), Queue(), self.is_top
        dummyRootNode = AdvancingNode(start_cell=cell, destn_cell=cell)
        queue.put(dummyRootNode)
        while not queue.empty(): # break when target corki gets crowned, gets eaten or is stuck
            old_board, cur_node = deepcopy(self.board), queue.get()
            if cur_node.parent is not None: self.playPath(cur_node) # modify self.board to reflect the state of current node
            self.is_top = not cur_node.played_by_comp # adjust position appropriately
            if self.is_top == isMoverTop: # you only add advances from the current node
                isCrowned = [False]
                targetNode = cur_node if cur_node.parent is None else cur_node.parent
                self.addSingleCorkiMovesToQueue(queue, targetNode.destn_cell, cur_node, isCrowned)
                if isCrowned[0]: return True
                self.addSingleCorkiEatsToQueue(queue, targetNode.destn_cell, cur_node, isCrowned)
                if isCrowned[0]: return True
            else:
                threateningGuards = self.getThreateningGuards(cur_node.destn_cell)
                for guard in threateningGuards:
                    self.addSingleCorkiMovesToQueue(queue, guard, cur_node)
                    self.addSingleCorkiEatsToQueue(queue, guard, cur_node)
            self.board = old_board # return board to its former state, i.e undo the changes
        self.board, self.is_top = originalBoard, True # restore main board
        return False

    # adds moves to bfs queue
    def addSingleCorkiMovesToQueue(self, queue, startCell, parent, isCrowned=None):
        moves = self.movesAndEats(startCell)[0]
        for move in moves:
            for destnCell in move[1]: # add move advances to the bfs queue
                if isCrowned is not None and self.board.shouldCrown(startCell, destnCell):
                    isCrowned[0] = True
                    return
                self.addPlainNodeToQueue(queue=queue, parent=parent, start_cell=startCell, destn_cell=destnCell,
                               is_eat=False, spree_path=None, played_by_comp=self.is_top)

    # adds eats of the corki at the given cell to the given queue
    def addSingleCorkiEatsToQueue(self, queue, startCell, parent, isCrowned=None):
        eatPaths = self.getEatPathsFromANode(startCell)[0]
        for path in eatPaths:
            destnCell = path[-1]
            if isCrowned is not None and self.board.shouldCrown(startCell, destnCell):
                isCrowned[0] = True
                return
            self.addPlainNodeToQueue(queue=queue, parent=parent, start_cell=startCell,
                           destn_cell=path[-1], is_eat=True,
                           spree_path=path, played_by_comp=self.is_top)

    # returns all the corkis that have a chance of tackling the
    # corki at the given cell
    def getThreateningGuards(self, cell):
        scaryGuards = []
        rowStart, colStart = cell
        rowRange = range(cell[0]+1, self.board.NUM_ROWS) if self.board.isTop(cell) else range(0, cell[0])
        for row in rowRange:
            for col in range(self.board.NUM_ROWS):
                if self.board.areOpponents(row, col):
                    if col == colStart: scaryGuards.append([row, col]) # vertical slope
                    else: # otherwise slope should be > 1
                        slope = (colStart - col) / (rowStart - row)
                        if abs(slope) > 1:
                            scaryGuards.append([row, col])
        return scaryGuards

    # prints the finalized eat tree, for debugging purpose
    @staticmethod
    def printFinalTree(chosen_node):
        node = chosen_node
        while node.children:
            if len(node.children) != 1:
                print("%s>1 children Node", node)
                break
            else:
                child = node.children[0]
                print("%sChild at level %d: %s " % ("-" * (2*node.level), child.level, child))
                node = child

    # prints full tree
    def printFullTree(self, node):
        print(node)
        if node.children:
            if len(node.children) < 1:
                print(node, "Weird Node with None children")
            else:
                for child in node.children:
                    self.printFullTree(child)


class AdvancingNode:
    def __init__(self, parent=None, children=None, level=-1, score=-1, start_cell=(-1,-1), destn_cell=(-1,-1),
                 is_eat=False, spree_path=None, played_by_comp=False, score_fixed=False):
        self.parent = parent
        self.children = children if children else []
        self.level = level
        self.score = score
        self.start_cell = start_cell
        self.destn_cell = destn_cell
        self.is_eat = is_eat
        self.spree_path = spree_path
        self.played_by_comp = played_by_comp
        self.score_fixed = score_fixed # this really may not be necessary

    def __str__(self):
        pad = "-"*(2*(self.level + 1))
        return "%sStart: %s Destn: %s \n%sIsEat: %s Level: %s Score: %s \n%sPlayedByComp: %s SpreePath: %s" % \
        (pad, self.start_cell, self.destn_cell, pad, self.is_eat, self.level, self.score,
         pad, self.played_by_comp, self.spree_path)


