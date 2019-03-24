"use strict";

var DAMA = {
    isEgregna: true,
    allowEfita: true,
    gameSessionID: 1,
    exchangePointUrl: "https://localhost:8000/"
},

damaSetUp = function () {

    var makeBoard = function () {

        const ILLEGAL_CELL = 0,
            EMPTY_LEGAL_CELL = 1,
            TETER_TOP = 3,
            KING_TOP = TETER_TOP + 1,
            TETER_BOTTOM = KING_TOP + 4,
            KING_BOTTOM = TETER_BOTTOM + 1;

        var board = [];

        return {

            NUM_ROWS: 8,
            NUM_HOME_ROWS: 3,

            getIllegalCellValue: function () {
                return ILLEGAL_CELL;
            },

            getLegalCellValue: function () {
                return EMPTY_LEGAL_CELL;
            },

            getTeterTopValue: function () {
                return TETER_TOP;
            },

            getTeterBotValue: function () {
                return TETER_BOTTOM;
            },

            getKingTopValue: function () {
                return KING_TOP;
            },

            getKingBotValue: function () {
                return KING_BOTTOM;
            },
            
            populateBoard: function () {

                var row, col;

                // initialize the boardFrame, set the legal and illegal cells
                for (row = 0; row < this.NUM_ROWS; row += 1) {
                    board[row] = [];
                    for (col = 0; col < this.NUM_ROWS; col += 1) {
                        board[row][col] = (row + col) % 2;
                    }
                }
               
                //l ay out the start cokis on the boardFrame; 24 in total, 12 for top
                // player and another 12 for the bottom player. 
                for (row = 0; row < this.NUM_HOME_ROWS; row += 1) {
                    for (col = 0; col < this.NUM_ROWS; col += 1) {
                        if (board[row][col] === EMPTY_LEGAL_CELL) {
                            board[row][col] = TETER_TOP;
                            board[row + this.NUM_ROWS - this.NUM_HOME_ROWS]
                            [this.NUM_ROWS - 1 - col] = TETER_BOTTOM;
                        }
                    }
                } 

            },

            getBoard: function () {
                return board;
            },

            // checks whether the cell @(row, col) is within 
            // the legal bounds of the boardFrame
            isInsideBoard: function (cell) {

                var row = cell[0], col = cell[1];

                return (0 <= row && row < this.NUM_ROWS) && 
                (0 <= col && col < this.NUM_ROWS);

            },

            // the next position to be encountered
            // when diagonally moving from first cell to second cell
            getNextCell: function (firstCell, secondCell) {

                var nextCell = [], 
                    row1 = firstCell[0], col1 = firstCell[1],
                    row2 = secondCell[0], col2 = secondCell[1];

                nextCell[0] = row2 + (row2 - row1);
                nextCell[1] = col2 + (col2 - col1);
                if (!this.isInsideBoard(nextCell)) {
                    return null;
                }
                return nextCell;

            },

            // yeah, literally returns the coordinates of the middle cell
            getMiddleCell: function (startCell, landingCell) {

                var midCell = [],
                    row1 = startCell[0], col1 = startCell[1], 
                    row2 = landingCell[0], col2 = landingCell[1];

                midCell[0] = (row2 > row1) ? (row1 + 1) : (row1 - 1);
                midCell[1] = (col2 > col1) ? (col1 + 1) : (col1 - 1);
                if (!this.isInsideBoard(midCell)){
                    return null;        
                }
                return midCell;
                
            },

            getCellValue: function (cell) {

                var row = cell[0], col = cell[1];

                return board[row][col];

            },

            setCellValue: function (value, cell) {

                var row = cell[0], col = cell[1];

                board[row][col] = value;

            },   

            isBottom: function (cell) {
                return this.isTeterBottom(cell) || this.isKingBottom(cell);
            },

            isTop: function (cell) {
                return this.isTeterTop(cell) || this.isKingTop(cell);
            },

            isIllegalCell: function (cell) {
                return this.getCellValue(cell) === ILLEGAL_CELL;
            },

            isEmptyLegalCell: function (cell) {
                return this.getCellValue(cell) === EMPTY_LEGAL_CELL;
            },

            isKing: function (cell) {
                var cellValue = this.getCellValue(cell);
                return cellValue === KING_TOP || cellValue === KING_BOTTOM;    
            },

            isTeter: function (cell) {
                var cellValue = this.getCellValue(cell);
                return cellValue === TETER_TOP || cellValue === TETER_BOTTOM;
            },

            isTeterTop: function (cell) {
                return this.getCellValue(cell) === TETER_TOP;
            },

            isTeterBottom: function (cell) {
                return this.getCellValue(cell) === TETER_BOTTOM;
            },

            isKingTop: function (cell) {
                return this.getCellValue(cell) === KING_TOP;
            },

            isKingBottom: function (cell) {
                return this.getCellValue(cell) === KING_BOTTOM;
            },

            makeEmpty: function (cell) {
                this.setCellValue(EMPTY_LEGAL_CELL, cell);
            },

            makeTeterTop: function (cell) {
                this.setCellValue(TETER_TOP, cell);
            },

            makeTeterBottom: function (cell) {
                this.setCellValue(TETER_BOTTOM, cell);
            },

            makeKingTop: function (cell) {
                this.setCellValue(KING_TOP, cell);
            },

            makeKingBottom: function (cell) {
                this.setCellValue(KING_BOTTOM, cell);
            }, 

            makeKing: function (cell) {

                if (this.isTeterTop(cell)) {
                    this.makeKingTop(cell);
                } else {
                    this.makeKingBottom(cell);
                }

            },

            // returns true if corkis at positions cell1 and cell2
            // are on the same team(owned by the same player)
            areTeamMates: function (cell1, cell2) {      

                if (this.isBottom(cell1)) {
                    return this.isBottom(cell2);
                } else if (this.isTop(cell1)){
                    return this.isTop(cell2);
                } else {
                    return false;
                }

            },

            // returns true if row, col is occupied by any corki
            isOccupied: function (cell) {
                return (!(this.isEmptyLegalCell(cell) || 
                    this.isIllegalCell(cell)));
            },

            getTankegnaKingsPrey: function (fromCell, landingCell) {

                var rowDirection = landingCell[0] > fromCell[0] ? 1 : -1,
                    colDirection = landingCell[1] > fromCell[1] ? 1 : -1,
                    curCell = fromCell.slice(0),
                    prey = null;

                while (true) {
                    curCell[0] += rowDirection;
                    curCell[1] += colDirection;
                    if (landingCell.isEqualTo(curCell)) {
                        break;
                    }
                    if (this.isOccupied(curCell)) {
                        if (this.areTeamMates(curCell, fromCell)) {
                            return null;                
                        }
                        if (prey !== null) {
                            return null;
                        } else {
                            prey = curCell.slice(0);                
                        }
                    }            
                }
                return prey;

            },

            areDiagonals: function (firstCell, secondCell, dist) {

                dist = dist || null;

                if (dist){
                    return Math.abs(firstCell[0] - secondCell[0]) === dist && 
                    Math.abs(firstCell[1] - secondCell[1]) === dist;
                } else {
                    return Math.abs(firstCell[0] - secondCell[0]) ===
                        Math.abs(firstCell[1] - secondCell[1]);
                }
                
            },

            // returns true if a teter corki can move in its forward direction from 
            // start cell to landing cell
            isForwardToTeter: function (startCell , ladningCell ) {   

                var row1 = startCell[0], row2 = ladningCell[0];

                if (this.isTeterTop(startCell)) {
                    return row2 > row1;
                } else {
                    return row2 < row1;
                }

            },

            isEmptyInBetween: function (fromCell, landingCell) {

                var rowDirection = landingCell[0] > fromCell[0] ? 1 : -1,
                    colDirection = landingCell[1] > fromCell[1] ? 1 : -1,
                    curCell = fromCell.slice(0);

                while (!landingCell.isEqualTo(curCell)){
                    curCell[0] += rowDirection;
                    curCell[1] += colDirection;
                    if (this.isOccupied(curCell)){
                        return false;         
                    }
                }
                return true;       
                 
            },       

            // returns true if corki at given cell should be made king
            shouldKingify: function (cell) {                       
                var row = cell[0], shouldBeKing = this.isTeter(cell) && 
                    ((row === 0 && this.isTeterBottom(cell)) || 
                    (row === (this.NUM_ROWS - 1) && this.isTeterTop(cell)));

                return shouldBeKing;
            },

            // moves corki from start cell to landing cell
            moveCorki: function (startCell , landingCell ) {    

                this.setCellValue(this.getCellValue(startCell), landingCell);
                this.makeEmpty(startCell);

            }         

        };

    },

    makePlayer = function (board, isTop, name) { 

        const NEIGHBOUR_OFFSETS = [[-1, -1], [-1, 1], [1, 1], [1, -1]];

        var lastMove, couldHaveEatens = []; // 2D array

        name = name || (isTop ? "Top Player" : "Bottom Player");

        return {
            
            setName: function (nameToBe) {
                name = nameToBe;
            },
            
            getName: function () {
                return name;
            },
            
            setIsEgregna: function (isItEgregna) {
                DAMA.isEgregna = isItEgregna;
            },
            
            isItEgregna: function () {
                return DAMA.isEgregna; 
            },
            
            setAllowEfita: function (efitaOk) {
                DAMA.allowEfita = efitaOk;
            },   
            
            isEfitaAllowed: function () {
                return DAMA.allowEfita;
            },
            
            getNumCorkis: function () {

                var numCorkis = 0, row, col;

                for (row = 0; row < board.NUM_ROWS; row += 1) {
                    for (col = 0; col < board.NUM_ROWS; col += 1 ) {
                        if (isTop) {
                            numCorkis += board.isTop([row, col]) ? 1 : 0;
                        } else {
                            numCorkis += board.isBottom([row, col]) ? 1 : 0;
                        }
                    }
                }
                return numCorkis;

            },
            
            // possible advances (eats and moves) from a given occupied cell
            movesAndEats: function (curCell)  {

                var i, offset, moves_and_eats = [[],[]],
                    neighborCell, landingCell, prevNeighborCell,
                    row = curCell[0], col = curCell[1],
                    isTankegnaKing, temp;

                for (i = 0; i < NEIGHBOUR_OFFSETS.length; i += 1) {
                    offset = NEIGHBOUR_OFFSETS[i];
                    neighborCell = [row + offset[0], col + offset[1]];
                    isTankegnaKing = (!this.isItEgregna()) && board.isKing(curCell);
                    prevNeighborCell = curCell;
                    while (true) {
                        if (board.isInsideBoard(neighborCell)) {
                            if (this.canMove(curCell, neighborCell)) {
                                moves_and_eats[0].push(neighborCell);
                            } else {
                                landingCell = isTankegnaKing ? 
                                board.getNextCell(prevNeighborCell, neighborCell) : 
                                board.getNextCell(curCell, neighborCell);
                                if (landingCell !== null) {
                                    if (this.canEat(curCell, landingCell)) {                        
                                        moves_and_eats[1].push(landingCell);
                                    }
                                }
                            }
                        }
                        if (!isTankegnaKing) {
                            break;
                        } else {
                            temp = neighborCell.slice(0);
                            neighborCell = board.getNextCell(prevNeighborCell, neighborCell);
                            prevNeighborCell = temp;
                            if (neighborCell === null) {
                                break;
                            } 
                        }
                    }
                }
                return moves_and_eats;
            },
            
            advancesFromCell: function (fromCell)  {

                var i, advances, combined = []; 

                advances= this.movesAndEats(fromCell);
                if ((!this.isEfitaAllowed()) && advances[1].length > 0) {
                    return advances[1];
                } 
                for (i = 0; i < advances[0].length; i += 1) {
                    combined.push(advances[0][i]);
                }
                for (i = 0; i < advances[1].length; i += 1) {
                    combined.push(advances[1][i]);
                }  
                return combined;

            },   

            
            getCanAdvanceCells: function () {

                var row, col, cell, moves_and_eats, numEats, numMoves,
                    canAdvanceCells = [], canEatCells = [];
                
                for (row = 0; row < board.NUM_ROWS; row += 1) {
                    for (col = 0; col < board.NUM_ROWS; col += 1) {
                        if ( (isTop && board.isTop([row, col])) || 
                            ((!isTop) && board.isBottom([row, col])) ) {
                            moves_and_eats = this.movesAndEats([row, col]);
                            numMoves = moves_and_eats[0].length;
                            numEats = moves_and_eats[1].length;
                            if (numMoves > 0 || numEats > 0) {
                                cell = [row, col];
                                canAdvanceCells.push(cell);
                                if (numEats > 0) {
                                    canEatCells.push(cell);
                                }  
                            }                  
                        }
                    }
                }
                if (!this.isEfitaAllowed()) {
                    if (canEatCells.length > 0) {
                        return canEatCells;
                    } else {
                        return canAdvanceCells;
                    } 
                } else {
                    couldHaveEatens = canEatCells;
                    return canAdvanceCells;
                }

            },    
            
            getEfita: function () {

                var potentialEfita;

                if (couldHaveEatens.length > 0) {
                    potentialEfita = couldHaveEatens[0];
                    if (board.isEmptyLegalCell(potentialEfita)) {
                        return lastMove;
                    } else {
                        return potentialEfita;
                    }
                } else {
                    return null;
                }

            },

            eatEfita: function (efita) {
                board.makeEmpty(efita);
            },
            
            playTurn: function (startCell, landingCell, eatenCorki) {

                var didEat = false, wasKingified = false,
                    target; 

                eatenCorki = eatenCorki || [0, 0, 0];

                if (this.canEat(startCell, landingCell)) {
                    didEat = true; 
                    target = (!this.isItEgregna()) && board.isKing(startCell) ? 
                            board.getTankegnaKingsPrey(startCell, landingCell) : 
                            board.getMiddleCell(startCell, landingCell);
                    eatenCorki[0] = target[0]; 
                    eatenCorki[1] = target[1]; 
                    eatenCorki[2] = board.getCellValue(target);
                    board.makeEmpty(target);
                } 
                lastMove = landingCell;
                board.moveCorki(startCell, landingCell);        
                if (board.shouldKingify(landingCell)) { // crown the new king, if there is one
                    wasKingified = true;
                    board.makeKing(landingCell);
                }        
                return [wasKingified, didEat];      

            },

            canMove: function (fromCell, toCell)  {

                if (!this.isOwnCorki(fromCell)) {
                    window.console.log("NOT YOUR CORKI, CAN'T MOVE");
                    return false;
                }
                if ((!this.isItEgregna()) && board.isKing(fromCell)) {
                    return board.isEmptyInBetween(fromCell, toCell);
                } else {
                    if (!board.areDiagonals(fromCell, toCell, 1)) {
                        return false;
                    }
                    return board.isEmptyLegalCell(toCell) &&
                            (board.isKing(fromCell) ||  
                                board.isForwardToTeter(fromCell, toCell));            
                }
            },

            canEat: function (fromCell, landingCell) {

                var prey, nextCell;

                if (!this.isOwnCorki(fromCell)) {
                    window.console.log("NOT YOUR CORKI, CAN'T EAT");
                    return false;      
                }
                if ((!this.isItEgregna()) && board.isKing(fromCell)) {
                    if (!board.areDiagonals(fromCell, landingCell)) {
                        return false;
                    } else {
                        prey = board.getTankegnaKingsPrey(fromCell, landingCell);
                        return board.isEmptyLegalCell(landingCell) && prey !== null;
                    }            
                } else {
                    if(!board.areDiagonals(fromCell, landingCell, 2)) {
                        return false;
                    } else {
                        nextCell = board.getMiddleCell(fromCell, landingCell);
                        return this.canEatHelper(fromCell, nextCell, landingCell);
                    }            
                }        
                           
            },
            
            canEatHelper: function (fromCell, nextCell, landingCell) {

                var landingCellOk, isEatAllowed;

                if (board.areTeamMates(fromCell, nextCell) || 
                        !board.areDiagonals(fromCell, nextCell, 1)) {
                    return false;
                } else {
                    landingCellOk = landingCell !== null &&
                            board.isEmptyLegalCell(landingCell);
                    if (this.isItEgregna()) {
                        isEatAllowed = (board.isKing(fromCell) && board.isOccupied(nextCell)) ||
                            (board.isTeter(nextCell) && board.isForwardToTeter(fromCell, nextCell));
                    } else { // tankegna Teter
                        isEatAllowed = board.isOccupied(nextCell);
                    }
                    return landingCellOk && isEatAllowed;
                }

            }, 
            
            isOwnCorki: function (cell) {
                return (isTop && board.isTop(cell)) ||
                       ((!isTop) && board.isBottom(cell));
            }

        };
        
    },

    makeArbiter = function () {

        var isCurrentPlayerTop = false, board,
            playerTop, playerBottom, topStarts,
            guiDisplayer, startCell = null,
            potentialLandingCells,
            didEat, wasKingified,
            boardFrame, rotateBoard = false,
            inputIsFromNetwork, isWaitingForOpponent,

            isPotentialLandingCell = function (testCell) {

                var i;

                for (i = 0; i < potentialLandingCells.length; i += 1) {
                    if (testCell.isEqualTo(potentialLandingCells[i])) {
                        return true;
                    }
                }
                return false;

            },

            getCurrentPlayer = function () {
                if (isCurrentPlayerTop) {
                    return playerTop;
                } else {
                    return playerBottom;
                }

            },       

            handleStartCellClick = function (fromCell) {

                var i, cell;

                potentialLandingCells = getCurrentPlayer().advancesFromCell(fromCell);
                for (i = 0; i < potentialLandingCells.length; i += 1) {
                    cell = potentialLandingCells[i];
                    guiDisplayer.highlightCell(cell);
                    guiDisplayer.enableCellTemporarily(cell);
                }            
                startCell = fromCell;

            },

            handleLandingCellClick = function (landingCell) {

                var movingCorki, eatenCorki, advanceInfo;

                guiDisplayer.unHighlightCell(landingCell);
                guiDisplayer.disableAll();
                movingCorki = board.getCellValue(startCell);
                eatenCorki = [-1,-1,-1];
                advanceInfo = getCurrentPlayer().playTurn(startCell, landingCell, eatenCorki);
                wasKingified = advanceInfo[0]; 
                didEat = advanceInfo[1];
                guiDisplayer.displayMove(startCell, landingCell, movingCorki, eatenCorki); 

            };          
        
        return {

            setUp: function () {

                var svgBoard = document.getElementById(DAMA.ids.svgBoardId);

                boardFrame = document.getElementById(DAMA.ids.canvasBoardId);
                board = makeBoard();     
                board.populateBoard();   
                playerTop = makePlayer(board, true);
                playerBottom = makePlayer(board, false);
                guiDisplayer = makeGuiDisplayer(boardFrame, board, this);  
       
                document.getElementById(DAMA.ids.bodyId).removeChild(svgBoard);
                
            },

            restartGame: function () {
                board = makeBoard();        
                playerTop = makePlayer(board, true);
                playerBottom = makePlayer(board, false);
                guiDisplayer = makeGuiDisplayer(boardFrame, board, this);
                isCurrentPlayerTop = topStarts;
                this.enableCanAdvanceCorkis();
            },
            
            shouldRotateBoard: function () {
                return rotateBoard;
            },

            setTopStarts: function (doesTopStart) {
                topStarts = doesTopStart;
                isCurrentPlayerTop = topStarts;
            },
            
            getTopStarts: function () { 
                return topStarts; 
            },
            
            handleCellClick: function (clickedCell) {

                var isClickRedundant;

                inputIsFromNetwork = false;
                guiDisplayer.unHighlightAll();
                guiDisplayer.highlightCell(clickedCell);
                if (startCell === null) {
                    handleStartCellClick(clickedCell);
                } else {
                    guiDisplayer.disableAllTemporarilyEnabledCells();
                    isClickRedundant = startCell.isEqualTo(clickedCell);
                    if(isClickRedundant) {
                        handleStartCellClick(clickedCell);
                    } else {
                        if (isPotentialLandingCell(clickedCell)) {
                            handleLandingCellClick(clickedCell);
                        } else { // another starting cell selected
                            handleStartCellClick(clickedCell);
                        }
                    }            
                }

            },

            processPendingEats: function (landingCell) {

                var moreEats, i, cell;

                if (didEat && !wasKingified){ // handle consecutive eats
                    moreEats = getCurrentPlayer().movesAndEats(landingCell)[1];
                    if (moreEats.length > 0) {
                        startCell = landingCell; 
                        potentialLandingCells = [];
                        for (i = 0; i < moreEats.length; i += 1) {
                            cell = moreEats[i];
                            guiDisplayer.highlightCell(cell);
                            guiDisplayer.enableCell(cell);
                            potentialLandingCells.push(cell);
                        }
                        return true;
                    }
                }      
                return false;

            },
            
            checkForEfita: function () {

                var efita;

                if (getCurrentPlayer().isEfitaAllowed() && !didEat) {
                    efita = getCurrentPlayer().getEfita();
                    if (efita !== null) {
                        guiDisplayer.displayEfita(efita); 
                        return true;
                    }            
                }  
                this.changeTurn(); 
                return false;

            },
            
            changeTurn: function () {     

                if (this.isGameOver()) {
                    return null;
                } 
                isCurrentPlayerTop = !isCurrentPlayerTop; // change turn
                this.enableCanAdvanceCorkis();
                startCell = null;     

            },
            
            getCurrentPlayer: getCurrentPlayer,
            
            getOpponnentPlayer: function () {

                if (isCurrentPlayerTop) {
                    return playerBottom;
                } else {
                    return playerTop;
                }

            },

            getTopPlayer: function () {
                return playerTop;
            },
            
            getBottomPlayer: function () {
                return playerBottom;
            },
            
            isGameOver: function () {

                var currentPlayer, hasCorkis, canAdvance;

                currentPlayer = getCurrentPlayer();
                hasCorkis = currentPlayer.getNumCorkis() > 0;
                if (!hasCorkis) {
                    window.alert("GAME OVER!");
                    window.alert(currentPlayer.getName()+" HAS LOST!");
                    return true;
                }
                canAdvance = currentPlayer.getCanAdvanceCells().length > 0;
                if (!canAdvance){
                    window.alert("GAME OVER!");
                    window.alert(currentPlayer.getName()+" HAS LOST!");
                    return true;           
                }
                return false;

            },
            
            sendMove: function (advance) { // advance[][] ... json

                var unique_id = 1234, strMsg,
                xmlhttp = new XMLHttpRequest();

                // strMsg = JSON.stringify(advance)

                strMsg = "unique_id=" + unique_id + "&" +
                "is_top="+ (isCurrentPlayerTop ? 1 : 0) + "&" +
                "is_pending=" + (advance.length > 2 ? 1 : 0) + "&" +
                "start_row=" + advance[0][0] + "&" +
                "start_col=" + advance[0][1] + "&" +
                "landing_row=" + advance[1][0] + "&" +
                "landing_col=" + advance[1][1];

                xmlhttp.onreadystatechange = function() {
                    if (this.readyState == 4 && this.status == 200) {
                        // success! move is sent, so may wanna start
                        // the listening timer so it can repeatedly call
                        // recieve move
                        // myObj = JSON.parse(this.responseText);
                    } else {
                        // something is wrong resend the message
                    }
                };
                xmlhttp.open("POST", DAMA.send_url, true);
                xmlhttp.setRequestHeader("Content-type", 
                    "application/x-www-form-urlencoded");
                xmlhttp.send(strMsg);

                isWaitingForOpponent = advance.length < 3;

                // you are waiting means, you are trying
                // to recieve moves from the opponent
                if (isWaitingForOpponent) {
                    this.startRecieving();
                }

            },
              
            
            isWaitingForOpponent: function () { 
                return isWaitingForOpponent;
            },  


            // recieve move forever, like until game is over
            // this kind of fakes java's threading haha
            startRecieving: function () {

                var xmlhttp, advance, keepGoing = true,
                    delay = 1000, unique_id = 1234, strMsg;

                var getOpponentsAdvance = function () {

                    strMsg = "unique_id=" + unique_id + "&" +
                    "is_top=" + (isCurrentPlayerTop ? 1 : 0);
                    xmlhttp = new XMLHttpRequest();

                    xmlhttp.onreadystatechange = function() {
                        // this callback represents the old 
                        // recieveMove method that was in place
                        // in the java version
                        if (this.readyState == 4 && this.status == 200) {
                            // success! request is sent, retrieve the 
                            // response now
                            advance = JSON.parse(this.responseText);
                            isWaitingForOpponent = advance.length > 2;
                            startCell = advance[0]; 
                            inputIsFromNetwork = true;
                            if (!isWaitingForOpponent) {
                                keepGoing = false;
                            } else {                                
                                setTimeout(getOpponentsAdvance, delay);
                                // this maight not be necessary, but better
                                /// to have it than not
                                keepGoing = true; 
                            }
                            handleLandingCellClick(advance[1]);
                        } 
                    };
                    xmlhttp.open("POST", DAMA.recieve_url, true);
                    xmlhttp.setRequestHeader("Content-type", 
                        "application/x-www-form-urlencoded");
                    xmlhttp.send(strMsg);

                    if (keepGoing) {
                        setTimeout(getOpponentsAdvance, delay);
                    } 

                };
                setTimeout(getOpponentsAdvance, delay);

            },
      
            enableCanAdvanceCorkis: function () {

                var i, cellsToEnable;

                guiDisplayer.disableAll();
                cellsToEnable = this.getCurrentPlayer().getCanAdvanceCells();
                for (i = 0; i < cellsToEnable.length; i += 1) {
                    guiDisplayer.enableCell(cellsToEnable[i]);
                }   

            },     

            // the place where the game starts
            startGame: function () {

                this.enableCanAdvanceCorkis();
                guiDisplayer.repaint();

                // gotta wait if not a starter
                isWaitingForOpponent = !DAMA.isClientStarter;

                // if not a starter wait for the opponent
                // to take its move
                if (DAMA.isClientStarter) {
                    this.setTopStarts(false);
                    rotateBoard = false;                 
                } else {
                    this.setTopStarts(true);
                    rotateBoard = true;
                    this.startRecieving();
                }

            }

        };

    },

    makeGuiDisplayer = function (boardFrame, board, arbiter) {

        const cellWidth = 60;

        var highlightedCells = Array.matrix(board.NUM_ROWS, 
                board.NUM_ROWS, 0),
            enabledCells = Array.matrix(board.NUM_ROWS, 
                board.NUM_ROWS, 0),
            eatenCorkis = Array.matrix(4, board.NUM_ROWS, null),
            temporarilyEnabledCells = [],
            movingCorki, movingCorkiX, movingCorkiY,
            isAnimatingEfita,
            efitaCorki = null,

        canvasClickHandler = function (evt) {

            var row = parseInt((evt.pageY - boardFrame.offsetTop) / 
                cellWidth, 10),
                col = parseInt((evt.pageX - boardFrame.offsetLeft) / 
                    cellWidth, 10);

            if (row >= 0 && row < board.NUM_ROWS + 1) {
                row -= 1;
                if (enabledCells[row][col] == 1 && 
                !arbiter.isWaitingForOpponent()) {
                    arbiter.handleCellClick([row, col]);
                }                    
            } 

        };    

        if (DAMA.useCanvas){
            DAMA.util.addListener(boardFrame, "click", 
                canvasClickHandler);
        }            

        return {

        setBoard: function (boardObj) {
            board = boardObj;
        },

        setArbiter: function (arbiterObj) {
            arbiter = arbiterObj;
        },

        enableCell: function (cell) {
            enabledCells[cell[0]][cell[1]] = 1;
        },

        enableCellTemporarily: function (cell) {
            enabledCells[cell[0]][cell[1]] = 1;
            temporarilyEnabledCells.push(cell);
        },

        disableAll: function () {
            enabledCells = Array.matrix(board.NUM_ROWS, 
                board.NUM_ROWS, 0);
        },

        disableAllTemporarilyEnabledCells: function () {

            var i, cell;

            for (i = 0; i < temporarilyEnabledCells.length; 
                i += 1) {
                cell = temporarilyEnabledCells[i];
                enabledCells[cell[0]][cell[1]] = 0;
            }
            temporarilyEnabledCells = [];

        },   

        highlightCell: function (cell) {

            highlightedCells[cell[0]][cell[1]] = 1;
            this.repaint();

        },

        unHighlightCell: function (cell) {

            highlightedCells[cell[0]][cell[1]] = 0;
            this.repaint();

        },

        unHighlightAll: function () {
            highlightedCells = Array.matrix(board.NUM_ROWS, 
                board.NUM_ROWS, 0);
        }, 

        getColor: function (cell) {

            var color, useEfitaCorki = isAnimatingEfita && 
                            cell.isEqualTo(efitaCorki);

            if (board.isTeterTop(cell)) {
                color = useEfitaCorki ? 
                DAMA.colors.tetrTopEfita : DAMA.colors.teterTop;
            } else if (board.isTeterBottom(cell)) {
                color = useEfitaCorki ? 
                DAMA.colors.teterBotEfita : DAMA.colors.teterBot;
            } else if (board.isKingTop(cell)) {
                color = useEfitaCorki ?
                 DAMA.colors.kingTopEfita : DAMA.colors.kingTop;
            } else { // king bottom
                color = useEfitaCorki ? 
                DAMA.colors.kingBotEfita : DAMA.colors.kingBot;
            }
            return color;

        },    

        getCircleCoors: function (args) { 
        // args = {cell:[]} or {coors:[]}

            var radiusScale = 0.8, 
                radius = cellWidth * radiusScale / 2,
                xCell, yCell;
                
            if (args.cell) {
                xCell = args.cell[1] * cellWidth;
                yCell = args.cell[0] * cellWidth;
            } else {
                xCell = args.coors[0];
                yCell = args.coors[1];
            }
            
            return {

                r: radius,
                x : xCell + 0.5 * cellWidth,
                y : yCell + 0.5 * cellWidth

            };
            
        },

        drawCircle: function (g, loc, color) {

            var corkiCoors;

            g.fillStyle = color;   
            g.beginPath();
            corkiCoors = this.getCircleCoors(loc);
            g.arc(corkiCoors.x, corkiCoors.y,
                corkiCoors.r, 0, 2*Math.PI);
            g.fill(); 

        },

        repaint: function () {

            var i, row, col, cell,
                xcoor, ycoor, offset, eatenCorkiColor,
                fill = "black",
                boardFrameWidth = 480,
                boardFrameHeight = 600,
                painter = boardFrame.getContext("2d");

            // apparently in javascript canvas you have to start 
            // by clearing the canvas screen yourself
            painter.clearRect(0, 0, boardFrameWidth, boardFrameHeight);
            for (i = 0; i < board.NUM_ROWS + 2; i += 1) {
                for (col = 0; col < board.NUM_ROWS; col += 1) {
                    xcoor = col * cellWidth; 
                    ycoor = i * cellWidth;                
                    if (i > 0 && i < board.NUM_ROWS + 1 ) {
                        row = i - 1;
                        cell = [row, col];
                        if (arbiter.shouldRotateBoard()) {
                            row = (board.NUM_ROWS - row - 1);
                            col = (board.NUM_ROWS - col - 1);
                            cell = [row, col];
                        }                        
                        // draw the legal and illegal cells
                        // they are the cells upon which the corkis sit
                        if (highlightedCells[row][col] === 1) {
                            fill = DAMA.colors.highlight;
                        } else {
                            fill = board.isIllegalCell(cell) ? 
                            DAMA.colors.illegal: DAMA.colors.legal;
                        }
                        painter.fillStyle = fill;
                        painter.fillRect(xcoor, ycoor, cellWidth, 
                            cellWidth);
                        // and draw the corkis, if there are any
                        if (board.isOccupied(cell)) {                         
                            fill = this.getColor(cell);
                            this.drawCircle(painter, {coors:[xcoor, ycoor]}, 
                                fill);                       
                        }
                    } else {
                        fill = i === 0 ? DAMA.colors.topBar : 
                        DAMA.colors.botBar;
                        painter.fillStyle = fill;
                        painter.fillRect(xcoor, ycoor, cellWidth, 
                            cellWidth);
                        // what remains is the part where corkis
                        // get added to the top and bottom bars
                        row = i === 0 ? 0 : 2;
                        if (arbiter.shouldRotateBoard()) {
                            row = (row + 2) % 4;
                        }
                        eatenCorkiColor = eatenCorkis[row][col]; 
                        if (eatenCorkiColor !== null) {
                            // draw the first corki
                            this.drawCircle(painter, {coors:[xcoor, ycoor]},
                                eatenCorkiColor);
                            // draw the second corki if there is one
                            eatenCorkiColor = eatenCorkis[row+1][col]; 
                            offset = cellWidth / 10;
                            if (eatenCorkiColor !== null) {
                                this.drawCircle(painter, 
                                    {coors:[xcoor + offset, ycoor + offset]},
                                eatenCorkiColor);                      
                            }                       
                        }                    
                    }
                }
            } 
            if (movingCorki !== null) {
                // offset is subtracted from movingCorkiX and movingCorkiY
                // to make them coordinate points of the top left corner
                // of the rectangle circumscribing the corki
                offset = cellWidth * 0.5;
                // remove the offset if movingCorkiX and movingCorkiY turn
                // out to be instead the coordinates of a rectangle
                this.drawCircle(painter, 
                    {coors:[movingCorkiX, movingCorkiY]},
                                movingCorki);          
            }      
        },

        displayMove: function (startCell, landingCell, 
            advancingCorkiVal, eatenCorki) {

            var that = this, didEat = eatenCorki[0] !== -1;
            // if there was an eaten corki, then reinstate
            // it so as to facilitate the moving process
            if (didEat) {
                board.setCellValue(eatenCorki[2], 
                [eatenCorki[0], eatenCorki[1]]); 
            }     

            // too many variables, oh my God do we need all of this
            // do seek it if there is some way to reduce them 
            var animateMove, setup,
            landingCellValue, startRow, startCol, destnRow, destnCol, 
            numSteps, dx, dy, curStep, delay,
            advancingCorkiValue,
            initialSetUpDone = false, moveAnimated = false, 
            eatAnimationStarted = false, 
            aboutToAnimateEfitaMove = false, 
            eatenCorkiSotrageLoc;

            animateMove = function () {

                var opponentNumEatenCorkis, isPlayerTop,
                row, col, eatenCorkiDestn, eatenCorkiStorageRow;         


                // Yes initial setup gets done for the second time
                // to move an efita corki or an eaten corki to the
                // basket of the player who just won the corki
                // The first setup was of course done to move the 
                // eating or the simply-moving corki to its destination
                if (!initialSetUpDone) {
                    // set delay to 50 ms and numSteps to 10, also set
                    // the startCell landingCell and advancingCorkiValue
                    setup(50, 10, startCell, landingCell, advancingCorkiVal);
                }

                // The coordinates of the moving corki are not incremented
                // towards its destination when aboutToAnimateEfita is true, 
                // since it implies that the necessary setup initialization
                // for moving the efita corki is not done by the first call
                // to setup ... what the first call to setup did was simply
                // set initialSetUpDone to true, moveAnimated to true and
                // aboutToAnimateEfita to true, and it defered actual initi
                // alizations to the second setup call, the reason for that 
                // is because efita and eaten-corki move are actually moving
                // the corki of not the current player but that of the opponent
                // so some pre-arragnement has to be done to get the animation
                // right before making the animation setups right away     
                if (!aboutToAnimateEfitaMove) {
                    curStep += 1; 
                    movingCorkiX += dx; 
                    movingCorkiY += dy;
                    that.repaint();         
                }
                // the next if clause involves the business of animating
                // the movement of efita-corki or eaten-corki from its
                // origin to its destination, and there are two entries
                // to this if clause:
                // --- the first one is after completing animation of a benign
                // move (in which case aboutToAnimateEfita is set to its
                // default false value, since landing cell is not none) when
                // curStep equals total numSteps
                // --- the second one is when landingCell is null and the 
                // first call to setup returns without doing real initialization
                // but having simply set aboutToAnimateEfita to true
                if (aboutToAnimateEfitaMove || curStep === numSteps) {
                    // the next if clause is entered by the session
                    // where curStep equals numSteps, that is right
                    // after completing a benign move, because a session
                    // which gets here having its aboutToAnimateEfita set to true
                    // would have had its moveAnimated also set to true
                    // --- and what it is doing is undoing what the first
                    // call to setup had done which was emptying the
                    // landing cell till animation of benign move completed
                    if (!moveAnimated) {
                        moveAnimated = true; 
                        board.setCellValue(landingCellValue, landingCell);
                    }              
                    // there are two cases that the next if clause is filtering
                    // 1: the case where there is only a benign move and 
                    // nothing more
                    // 2: the case that has just finished animating the movement
                    // of an efita-corki or an eaten-corki
                    // --- those two cases enter directly into the else
                    // clause
                    // --- but the case that has an efita-corki or 
                    // eaten-corki to animate enters the if clause   
                    if (aboutToAnimateEfitaMove || (didEat && !eatAnimationStarted)) { 
                        // gotta empty the eaten corki, this is the undoing of
                        // the reinstating of the eaten corki for it to
                        // sit there till benign move animation completed
                        board.makeEmpty(eatenCorki);           
                        // the destination of both the efita-corki and the
                        // eaten-corki is the collector's basket or one
                        // of the storage bars (either the top or the bottom)
                        // ---the locations at the storage bar are indexed, so 
                        // to calculate the next right index you first have 
                        // to know how many corkis are eaten so far, also
                        // need to determine whether the sorage bar that is
                        // our target destination is the top one or the bottom one.
                        opponentNumEatenCorkis = 12 - 
                        arbiter.getOpponnentPlayer().getNumCorkis();
                        isPlayerTop = arbiter.getCurrentPlayer() === 
                            arbiter.getTopPlayer();
                        // if animating efita-corki move, the collector
                        // is not the current player, it is the opponent.
                        // so you need to flip the calculations
                        if (aboutToAnimateEfitaMove) {
                            isPlayerTop = !isPlayerTop; 
                            opponentNumEatenCorkis = 12 - 
                            arbiter.getCurrentPlayer().getNumCorkis();
                        }
                        // we don't want the steps within this if clause to be
                        // repeated in the next round of the timer's call
                        // so make the next entry conditions imposssible
                        eatAnimationStarted = true; 
                        aboutToAnimateEfitaMove = false;                  
                        // determine the row of the storage bar, it is either
                        // 0 or 9, but subtract 1 from each and make them -1
                        // and 8 since the call to setup will add 1 to each
                        row = isPlayerTop ? -1 : 8; 
                        col = opponentNumEatenCorkis % board.NUM_ROWS;
                        // so now we have the eaten corki destination
                        // on the boardFrame, they are frame coordinates
                        eatenCorkiDestn = {row, col}; 
                        // now also need to calculate the eaten corki
                        // storage location indexes, this is a logical
                        // data storage array index not a board frame
                        // destination coordinate.
                        eatenCorkiStorageRow = isPlayerTop ? 
                        (opponentNumEatenCorkis / board.NUM_ROWS) : 
                        2 + (opponentNumEatenCorkis / board.NUM_ROWS);
                        eatenCorkiSotrageLoc = [eatenCorkiStorageRow, col];  
                        // finally do the secon call to setup with faster delay
                        // and higher number of steps, so the animation can be
                        // done faster than that of the benign move's animation
                        // since the eaten corki covers a relatively larger distance               
                        setup(20, 20, [eatenCorki[0], eatenCorki[1]], 
                            eatenCorkiDestn, eatenCorki[2]);                                                  
                    } else { 
                        // note didEat is true for two cases, when a 
                        // corki is eaten as efita or as normal eat
                        // since this else clause is entered after 
                        // completing move animation, now set the eaten
                        // corki at its destination permanently so it
                        // can be seen later on when paint does its routine
                        // drawing
                        if (didEat) {
                            eatenCorkis[eatenCorkiSotrageLoc[0]]
                            [eatenCorkiSotrageLoc[1]] = movingCorki;
                        }
                        // set movingCorki null, since move animation is complets, 
                        // and so that repaint can ignore it
                        // and focus only on the static components
                        movingCorki = null; 
                        // --- if efitaCorki is not null, it means animateMove was called
                        // from displayEfita, and that benignMove animation has already
                        // been completed at some earlier time, so what remains is
                        // to eat the efita, change the turn and set the efitaCorki
                        // to null and be done with for now, till next time.   
                        // --- if efitaCorki is however null it means that we just
                        // completed either a benign move animation or a normal
                        // eaten-corki move animation.
                        //  --> in this case if there are pending eats to process
                        // it means that what was just completed was an eaten-corki
                        // move animation so we just send the animated move to the
                        // network and get out, no need to check for efita, since
                        // efita is impossible if something has been eaten
                        //  --> but if there are no pending eats, there is a chance
                        // that we just came from a benign move animation and that
                        // there is an efita waiting to be checked, so we do that
                        //, after which we send the move to the network ... note
                        //  checkForEfita also takes the responsibility of changing
                        // the turn for us     
                        if (efitaCorki === null) {
                            if (!arbiter.processPendingEats(landingCell)) {
                                arbiter.checkForEfita();
                                if (!arbiter.inputIsFromNetwork) {
                                    arbiter.sendMove([startCell,landingCell]);
                                } 
                            } else {
                                if (!arbiter.inputIsFromNetwork) {
                                    // null is added as a third element of the message
                                    // to signal the listener on the other side of 
                                    // the network that there is still a pending
                                    // eat to be done, so should hold on with 
                                    // changing its turn
                                    arbiter.sendMove([startCell, landingCell, null]);
                                } 
                            }
                        } else {
                            arbiter.getCurrentPlayer().eatEfita(efitaCorki);
                            arbiter.changeTurn(); 
                            efitaCorki = null; 
                        }
                        
                    }                                        
                } else {
                    setTimeout(animateMove, delay);
                }
         
            };

            setup = function (speed, steps, startCell, landingCell, 
                advancingCorkiVal) {

                if (!initialSetUpDone) {       
                    initialSetUpDone = true;
                    // so if landing cell is null, it means
                    // we are dealing with efita animation
                    if (landingCell === null) {
                        moveAnimated = true; 
                        aboutToAnimateEfitaMove = true; 
                        return;
                    } else {
                        // landing cell is not null, so we are dealing with
                        // normal move animation, but can't start animating
                        // a move with the target at destination, so gotta
                        // empty it from the destination till animation completes
                        landingCellValue = board.getCellValue(landingCell); 
                        board.makeEmpty(landingCell);
                    }
                }
                // 1 is added to the row values since a cell row value
                // that is say 0 is actually 1 since in the board frame
                // layout the top storage bar takes the row value 0
                // so 1 is added to account for the imbalance created
                // by the insertion of a top storage row
                // the real frame rows range from 0 to 9 (10 in total),
                // 8 rows of the dama board plus the 2 storage rows
                startRow = startCell[0] + 1; 
                startCol = startCell[1]; 
                destnRow = landingCell[0] + 1; 
                destnCol = landingCell[1]; 
                numSteps = steps; 
                movingCorkiX = startCol * cellWidth; 
                movingCorkiY = startRow * cellWidth; 
                advancingCorkiValue = advancingCorkiVal;
                dx = ((destnCol - startCol) * cellWidth)/numSteps; 
                dy = ((destnRow - startRow) * cellWidth)/numSteps; 
                curStep = 0; 
                // choose the color for the advancing corki 
                switch (advancingCorkiValue) {
                    case board.getTeterTopValue(): 
                        movingCorki = DAMA.colors.tetrTop;
                        break;
                    case board.getTeterBotValue():
                        movingCorki = DAMA.colors.teterBot;
                        break;
                    case board.getKingTopValue():
                        movingCorki = DAMA.colors.kingTop;
                        break;   
                    case board.getKingBotValue():
                        movingCorki = DAMA.color.kingBot;
                        break;                      
                }         
                delay =  speed;
                // start the timer
                setTimeout(animateMove, delay);
            };

            animateMove();
        },

        displayEfita: function (efita) {

            var that = this, animateEfita, count = 0, 
            cellValue = board.getCellValue(efita),
            efitaInfo, delay = 500;

            efitaCorki = efita; 
            isAnimatingEfita = true;  

            animateEfita = function () {

                if(board.isEmptyLegalCell(efitaCorki)) {
                    board.setCellValue(cellValue, efitaCorki);
                } else {
                    board.makeEmpty(efitaCorki);
                }
                this.repaint(); 
                count += 1;
                if (count > 4) {
                    isAnimatingEfita = false;
                    efitaInfo = [efitaCorki[0], efitaCorki[1], cellValue];
                    that.displayMove(null, null, -1, efitaInfo);
                } else {
                    setTimeout(animateEfita, delay);
                }
                
            };

            setTimeout(animateEfita, delay);
        }
         

        };
    },

    prepEnv = function () {

        DAMA.DEBUG = true;

        DAMA.useCanvas = true;

        DAMA.isClientStarter = parseInt(
            document.getElementById('isStarter').value);

        DAMA.send_url = "http://localhost:8000/dama/send/";

        DAMA.recieve_url = "http://localhost:8000/dama/recieve/";

        DAMA.ids = {
            svgBoardId: "svgBoard",
            canvasBoardId: "canvasBoard",
            bodyId: "boardBody",
            dummyRectId: "rect1"
        };

        DAMA.colors = {
            legal: "black",
            illegal: "white",
            highlight: "grey",
            teterTop: "blue",
            teterBot: "red",
            kingTop: "cyan",
            kingBot: "orange",
            teterTopEfita: "blue",
            teterBotEfita: "red",
            kingTopEfita: "cyan",
            kingBotEfita: "orange",
            topBar: "blue",
            botBar: "red"           
        };        

        DAMA.util = {
            
            addListener: function (eventTarget, eventType, eventHandler) {

                if (eventTarget.addEventListener) {
                    eventTarget.addEventListener(eventType, eventHandler, false);
                } else if (eventTarget.attachEvent) {
                    eventType = "on" + eventType;
                    eventTarget.attachEvent(eventType, eventHandler);
                } else {
                    eventTarget["on" + eventType] = eventHandler;
                }

            }

        };

        Function.prototype.method = function (name, func) {

            if (!this.prototype[name]){
                this.prototype[name] = func;
                return this;
            }

        };

        // initialized matrix/multi-dimensional array
        Array.matrix = function (m, n, initial) {
            var a, i, j, mat = [];
            for (i = 0; i < m; i += 1) {
                a = [];
                for (j = 0; j < n; j += 1) {
                    a[j] = initial;
                }
                mat[i] = a;
            }
            return mat;
        };        

        Array.method('isEqualTo', function (arr) {

            var i;

            if (!arr) {
                // note this can not be null or undefined
                // since a method can not be called on either
                return this.length === 0;
            }
            if (this.length !== arr.length) {
                return false;
            }
            for (i = 0; i < this.length; i += 1) {
                if (this[i] !== arr[i]) {
                    return false;
                }
            }
            return true;           

        });

    },

    start = function () {

        var arbiter = makeArbiter();
        arbiter.setUp();
        arbiter.startGame();

    };

    prepEnv();

    start();

};
    
window.onload = function() {
    damaSetUp();
};