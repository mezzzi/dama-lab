from copy import deepcopy


fake_tree = {0:[1, 2, 3], 1:[4, 5, 6], 3:[7, 8], 5:[9]}
    
def getEatList(level, from_cell, to_cell, eat_paths):
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
                                return new_entry
                            eat_list.append(new_entry)
                        break
        mothers = next_mothers
        level_count += 1

def getEatLists(level, from_cell, eat_paths):
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

def getEats(from_cell):
    """
    a helper method that gets the list of corkis eaten from from_cell upto this to_cell
    """
    eat_paths, level = path, 3
    eat_lists = getEatLists(level, from_cell, eat_paths)
    eats = []
    for path in eat_lists:
        for cell in path:
            if not cell in eats:
                eats.append(cell)
    return eats
    


def getEatPathsFake(start_node):
    """
    returns all possible eat paths as something like:[[0, 2], [0, 1, 4], [0, 1, 6], [0, 3, 7], [0, 3, 8], [0, 1, 5, 9]]
    and also returns the maximum number of eats(which in the code below is stored in the variable called tree_depth)
    """
    eat_paths = [[start_node]] # e.g [[0, 2], [0, 1, 4], [0, 1, 6], [0, 3, 7], [0, 3, 8], [0, 1, 5, 9]]
    crrent_from_nodes = {start_node} # making it a set prevents duplication
    tree_depth = 0 
    while True:
        next_from_nodes = set() # making it a set prevents duplication
        last_depth_reached = True
        for from_node in crrent_from_nodes:
            ''' check if the from_node is parent to some children'''
            ''' technically, you would call some function here to get the real children '''            
            if from_node in fake_tree.keys():
                # fake_tree is declared at the top of the page)
                children = fake_tree[from_node] # get the children
            else:
                children = [] # no children
            ''' if we have got children, there is work to be done '''
            if children:
                # not done yet, because the children may in turn have children
                last_depth_reached = False 
                addPathsLeadingToNewChildren(eat_paths, from_node, children) # also update the eating-paths
                # accumulate the current depth nodes for later checking(children-inspection)
                next_from_nodes.update(children)
        ''' if last_depth_reached if still true after the foor loop, it means we are done '''
        if last_depth_reached:
            return eat_paths, tree_depth
        tree_depth += 1
        crrent_from_nodes = next_from_nodes
        
def addPathsLeadingToNewChildren(eat_paths, from_node, new_children):
    """
    Say there is a path [1, 2, 3]. Now 3 is the path's last node. Now if this node is found
    to have two children say 4 and 6. Then this function deletes the old path [1 2 3] and
    adds to the path list the new paths [1, 2, 3, 4] and [1, 2, 3, 6].
    """

    '''loop through the paths in the path list, to find the path that
    ends with the from_node '''
    for i in range(len(eat_paths)):
        ith_eat_path = deepcopy(eat_paths[i]) 
        ''' check if current path ends with the from_node '''
        if ith_eat_path[-1] == from_node: 
            eat_paths.remove(ith_eat_path) # remove old path
            # add new path leading to each new children
            for child in new_children:
                new_path = deepcopy(ith_eat_path)
                new_path.append(child)
                eat_paths.append(new_path)    

def getEatPathsFromANode(self, start_node):
    """
    returns all possible eat paths as something like:[[0, 2], [0, 1, 4], [0, 1, 6], [0, 3, 7], [0, 3, 8], [0, 1, 5, 9]]
    and also returns the maximum number of eats(which in the code below is stored in the variable called tree_depth)
    """
    eat_paths = [[start_node]] # e.g [[0, 2], [0, 1, 4], [0, 1, 6], [0, 3, 7], [0, 3, 8], [0, 1, 5, 9]]
    crrent_from_nodes = {start_node} # making it a set prevents duplication
    tree_depth = 0 
    while True:
        next_from_nodes = set() # making it a set prevents duplication
        last_depth_reached = True
        for from_node in crrent_from_nodes:
            ''' check if the from_node is parent to some children '''
            eatable_children = self.movesAndEats(from_node)[1] # eatable children
            ''' if we have got children, there is work to be done '''                    
            if eatable_children:
                last_depth_reached = False
                addPathsLeadingToTheChildren(eat_paths, from_node, eatable_children)
                next_from_nodes.update(eatable_children)
        if last_depth_reached:
            return eat_paths, tree_depth
        tree_depth += 1
        crrent_from_nodes = next_from_nodes

def addPathsLeadingToTheChildren(eat_paths, from_node, new_children):
    """
    Say there is a path [1, 2, 3]. Now 3 is the path's last node. Now if this node is found
    to have two children say 4 and 6. Then this function deletes the old path [1 2 3] and
    adds to the path list the new paths [1, 2, 3, 4] and [1, 2, 3, 6].
    """

    '''loop through the paths in the path list, to find the path that
    ends with the from_node '''
    for i in range(len(eat_paths)):
        ith_eat_path = deepcopy(eat_paths[i]) 
        ''' check if current path ends with the from_node '''
        if ith_eat_path[-1] == from_node: 
            eat_paths.remove(ith_eat_path) # remove old path
            eaten_corkis = getEatenCorkis(ith_eat_path) # get corkis eaten so far
            # add new path leading to each new children
            for child in new_children:
                almost_eaten_corki = self.board.getMiddleCell(from_node, child) # corki about to be eaten
                ''' if corki has been eaten, don't add child to eat path '''
                if almost_eaten_corki in eaten_corkis:
                    continue # child was leading to a redundant-eat                
                new_path = deepcopy(ith_eat_path)
                new_path.append(child)
                eat_paths.append(new_path)

def getEatenCorkis(self, eat_path):
    """
    returns corkis(as list of [row, col] cells) eaten upto the given node
    """
    eaten_corkis = []
    for i in range(len(eat_path) - 1):
        eaten_corkis.append(self.board.getMiddleCell(eat_path[i], eat_path[i + 1]))
    return eaten_corkis
                        
        
