    def getComputersMove(self):
        """
        returns the computers move as ((row1, col1), (row2, col2)), bool
        where (row1, col1) is starting cell, (row2, col2) is
        destination cell, and bool is True if the move is an eat
        move, False if the move is a non eat move.
        It is assumed that the computer takes the top position
        """
        max_moves = max_eats = 0
        best_move = best_eat = None
        for row in range(self.board.NUM_ROWS):
            for col in range(self.board.NUM_ROWS):
                if self.board.isTop(row, col):
                    moves_and_eats = self.movesAndEats(row, col)
                    moves = len(moves_and_eats[0])
                    eats = len(moves_and_eats[1])
                    ''' prefer max num moves '''
                    if moves > max_moves:
                        max_moves = moves
                        best_move = [(row, col), moves_and_eats[0]]
                    ''' prefer max num eats '''
                    if eats > max_eats:
                        max_eats = eats
                        best_eat = [(row, col), moves_and_eats[1]]
        ''' prefer eat move over non eat move '''
        if best_eat != None:
            return best_eat[0], best_eat[1][0], True
        elif best_move != None:
            return best_move[0], best_move[1][0], False
        else:
            return None
        
    def getEats(self, from_cell):
        """
        a helper method that gets the list of corkis eaten from from_cell upto this to_cell
        """
        eat_paths, level = self.getEatPaths(from_cell)
        eat_lists = self.getEatLists(level, from_cell, eat_paths)
        eats = []
        for path in eat_lists:
            for cell in path:
                if not cell in eats:
                    eats.append(cell)
        return eats                         

    def getEatPaths(self, start_cell):
        """
        attempts to get all eat paths from from_cell
        """
        eat_paths = {}        
        from_cells = [start_cell]
        level = 0
        while True:
            next_from_cells = []
            more_coming = False
            for from_cell in from_cells:
                immediate_eats = self.immediateLegalMovesAndEats(from_cell)[1]
                for cell in immediate_eats:
                    eaten_corkis = self.getEatList(level, from_cell, cell, eat_paths)
                    eaten_corki = self.board.getMiddleCell(from_cell, cell)
                    if eaten_corki in eaten_corkis:
                        immediate_eats.remove(cell)
                if len(immediate_eats) > 0:
                    more_coming = True
                    eat_paths[from_cell] = self.getEatLandingPairs(from_cell, immediate_eats)
                    next_from_cells.extend(immediate_eats)
            if not more_coming:
                return eat_paths, level
            level += 1
            from_cells = next_from_cells


    def getEatLists(self, level, from_cell, eat_paths):
        """
        a helper method that gets the list of corkis eaten from from_cell upto this to_cell
        """
        eat_list = []
        level_count = 0
        mothers = [from_cell]
        eat_list.append(mothers)
        while level_count < level:
            next_mothers = []
            for mother in mothers:
                if mother in eat_paths.keys():
                    children = eat_paths[mother]
                    next_mothers.extend(children)
                    for i in range(len(eat_list)):
                        ith_eat_list = deepcopy(eat_list[i])
                        last_index = len(ith_eat_list)-1
                        if ith_eat_list[last_index] == mother:
                            eat_list.remove(ith_eat_list)
                            for child in children:
                                new_entry = deepcopy(ith_eat_list)
                                new_entry.append(child)
                                eat_list.append(new_entry)
                            break
            mothers = next_mothers
            level_count += 1
        return eat_list
    

    def getEatList(self, level, from_cell, to_cell, eat_paths):
        """
        a helper method that gets the list of corkis eaten from from_cell upto this to_cell
        """
        eat_list = []
        level_count = 0
        mothers = [from_cell]
        eat_list.append(mothers)
        while level_count < level:
            next_mothers = []
            for mother in mothers:
                if mother in eat_paths.keys():
                    children = eat_paths[mother]
                    next_mothers.extend(children)
                    for i in range(len(eat_list)):
                        ith_eat_list = deepcopy(eat_list[i])
                        last_index = len(ith_eat_list)-1
                        if ith_eat_list[last_index] == mother:
                            eat_list.remove(ith_eat_list)
                            for child in children:
                                new_entry = deepcopy(ith_eat_list)
                                new_entry.append(child)
                                if child == to_cell and level_count == level - 1:
                                    eaten_corkis = []
                                    for entry in new_entry:
                                        eaten_corkis.append(entry[1])
                                    return eaten_corkis
                                eat_list.append(new_entry)
                            break
            mothers = next_mothers
            level_count += 1
                
            
        
    def getEatLandingPairs(self, from_cell, landing_cells):
        """
        a helper method that adds eaten corkis to landing cells list
        """
        corki_landing_pairs = []
        for landing in landing_cells:
            corki_landing_pairs.append((landing, self.board.getMiddleCell(from_cell, landing)))
        return corki_landing_pairs
                            

