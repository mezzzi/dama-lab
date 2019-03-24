"use strict";

var DAMA = {
    isEgregna: false,
    allowEfita: true,
    gameSessionID: 1,
    exchangePointUrl: "https://localhost:8000/",
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
                        board[row][col] = Math.floor((row + col) % 2);
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

                var row1 = startCell[0], col1 = startCell[1], row2 = ladningCell[0];

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

        var lastMove, couldHaveEatens; // 2D array

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
            boardFrame,
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

                var movingCorkiV, eatenCorki, advanceInfo;

                guiDisplayer.unHighlightCell(landingCell);
                guiDisplayer.disableAll();
                movingCorkiV = board.getCellValue(startCell);
                eatenCorki = [-1,-1,-1];
                advanceInfo = getCurrentPlayer().playTurn(startCell, landingCell, eatenCorki);
                wasKingified = advanceInfo[0]; 
                didEat = advanceInfo[1];
                guiDisplayer.displayMove(startCell, landingCell, movingCorkiV, eatenCorki); 

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
                            // success! request is sent, retrieve the response now
                            advance = JSON.parse(this.responseText);
                            isWaitingForOpponent = advance.length > 2;
                            startCell = advance[0]; 
                            inputIsFromNetwork = true;
                            handleLandingCellClick(advance[1]);
                        } 
                    };
                    xmlhttp.open("POST", DAMA.recieve_url, true);
                    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
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

                isWaitingForOpponent = !topStarts;

            },
            
            getDisplayer : function () {
                return guiDisplayer;
            }

        };

    },

    makeGuiDisplayer = function (boardFrame, board, arbiter) {

        const cellWidth = 70;

        var highlightedCells = Array.matrix(board.NUM_ROWS, 
                board.NUM_ROWS, 0),
            enabledCells = Array.matrix(board.NUM_ROWS, 
                board.NUM_ROWS, 0),
            eatenCorkis = Array.matrix(4, board.NUM_ROWS, null),
            temporarilyEnabledCells = [],
            movingCorki = null, movingCorkiX, movingCorkiY,
            isAnimatingEfita,
            efitaCorki = null,

        canvasClickHandler = function (evt) {

            var row = parseInt((evt.pageY - boardFrame.offsetTop) / 
                cellWidth, 10),
                col = parseInt((evt.pageX - boardFrame.offsetLeft) / 
                    cellWidth, 10);

            if (row >= 0 && row < board.NUM_ROWS + 1) {
                row -= 1;
                if (enabledCells[row][col] == 1) {
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
                DAMA.colors.teterTopEfita : DAMA.colors.teterTop;
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
            
        getCorki: function (cell) {

            var corki, useEfitaCorki = isAnimatingEfita && 
                            cell.isEqualTo(efitaCorki);

            if (board.isTeterTop(cell)) {
                corki = useEfitaCorki ? 
                DAMA.CORKIS.teterTopEfita : DAMA.CORKIS.teterTopCorki;
            } else if (board.isTeterBottom(cell)) {
                corki = useEfitaCorki ? 
                DAMA.CORKIS.teterBottomEfita : DAMA.CORKIS.teterBottomCorki;
            } else if (board.isKingTop(cell)) {
                corki = useEfitaCorki ?
                 DAMA.CORKIS.kingTopEfita : DAMA.CORKIS.kingTopCorki;
            } else { // king bottom
                corki = useEfitaCorki ? 
                DAMA.CORKIS.kingBottomEfita : DAMA.CORKIS.kingBottomCorki;
            }
            return corki;

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
        
        corkiCoordinates: function(corki, xcoor, ycoor) {
            var width = parseInt(corki['width']);
            var height = parseInt(corki['height']);
            return [xcoor + (cellWidth - width) / 2, ycoor + (cellWidth - height) / 2];
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
                corki, fill, corkiCoors,
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
                            corki = this.getCorki(cell);
                            corkiCoors = this.corkiCoordinates(corki, xcoor, ycoor);
                            painter.drawImage(corki, corkiCoors[0], corkiCoors[1]);                            
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
                        corki = eatenCorkis[row][col]; 
                        if (corki !== null) {
                            // draw the first corki
                            corkiCoors = this.corkiCoordinates(corki, xcoor, ycoor);
                            painter.drawImage(corki, corkiCoors[0], corkiCoors[1]);
                            // draw the second corki if there is one
                            corki = eatenCorkis[row+1][col]; 
                            offset = cellWidth / 10;
                            if (corki !== null) {
                                corkiCoors = this.corkiCoordinates(corki, xcoor, ycoor);
                            }
                        }                    
                    }
                }
            } 
            if (movingCorki !== null) {
                painter.drawImage(movingCorki, movingCorkiX, movingCorkiY);     
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
            numSteps, dx, dy, curStep, timer,
            advancingCorki,
            initialSetUpDone = false, hasMoved = false, hasEaten = false,
            isMovingEfita = false, 
            eatenCorkiSotrageLoc;

            animateMove = function () {

                var opponentNumEatenCorkis, isPlayerTop,
                row, col, eatenCorkiDestn, eatenCorkiStorageRow;         

                if (!initialSetUpDone) {
                    // set delay to 50 ms and numSteps to 10, also set
                    // the startCell landingCell and advancingCorkiValue
                    setup(50, 10, startCell, landingCell, advancingCorkiVal);
                }
                if (!isMovingEfita) {
                    curStep += 1; 
                    movingCorkiX += dx; 
                    movingCorkiY += dy;
                }
                if (isMovingEfita || curStep === numSteps) {
                    if (!hasMoved) {
                        hasMoved = true; 
                        board.setCellValue(landingCellValue, landingCell);
                    }              
                    if (isMovingEfita || (eatenCorki[0] !== -1 && !hasEaten)) {          
                        opponentNumEatenCorkis = 12 - 
                        arbiter.getOpponnentPlayer().getNumCorkis();
                        isPlayerTop = arbiter.getCurrentPlayer() === 
                            arbiter.getTopPlayer();
                        if (isMovingEfita) {
                            isPlayerTop = !isPlayerTop; 
                            opponentNumEatenCorkis = 12 - 
                            arbiter.getCurrentPlayer().getNumCorkis();
                        }
                        hasEaten = true; 
                        isMovingEfita = false;                                
                        
                        row = isPlayerTop ? -1 : 8; 
                        col = Math.floor(opponentNumEatenCorkis % board.NUM_ROWS);
                        eatenCorkiDestn = [row, col]; 
                        board.makeEmpty([eatenCorki[0], eatenCorki[1]]); 
                        setup(30, 20, [eatenCorki[0], eatenCorki[1]], eatenCorkiDestn, eatenCorki[2]);
                        eatenCorkiStorageRow = isPlayerTop ? 
                        Math.floor((opponentNumEatenCorkis / board.NUM_ROWS)) : 
                        2 + Math.floor((opponentNumEatenCorkis / board.NUM_ROWS));
                        eatenCorkiSotrageLoc = [eatenCorkiStorageRow, col];    
                    } else { 
                        if (eatenCorki[0] !== -1) {
                            eatenCorkis[eatenCorkiSotrageLoc[0]]
                            [eatenCorkiSotrageLoc[1]] = movingCorki;
                        }
                        movingCorki = null; clearInterval(timer);
                        if (efitaCorki === null) {
                            if (!arbiter.processPendingEats(landingCell)) {
                                arbiter.checkForEfita();
                            } 
                        } else {
                            arbiter.getCurrentPlayer().eatEfita(efitaCorki);
                            arbiter.changeTurn(); 
                            efitaCorki = null; 
                        }
                        
                    }                                        
                } 
                that.repaint();
                    
         
            };

            setup = function (speed, steps, startCell, landingCell, 
                advancingCorkiVal) {
                var useEfitaCorki, corkiCoors;

                if (!initialSetUpDone) {       
                    initialSetUpDone = true;

                    if (landingCell === null) {
                        hasMoved = true; 
                        isMovingEfita = true; 
                        return;
                    } else {

                        landingCellValue = board.getCellValue(landingCell); 
                        board.makeEmpty(landingCell);
                    }
                }

                startRow = startCell[0] + 1; 
                startCol = startCell[1]; 
                destnRow = landingCell[0] + 1; 
                destnCol = landingCell[1]; 
                numSteps = steps; 
                advancingCorki = advancingCorkiVal;
                dx = ((destnCol - startCol) * cellWidth)/numSteps; 
                dy = ((destnRow - startRow) * cellWidth)/numSteps; 
                curStep = 0; 
                useEfitaCorki = efitaCorki !== null;
                // choose the color for the advancing corki 
                switch (advancingCorki) {
        
                    case board.getTeterTopValue(): 
                        movingCorki = useEfitaCorki ? 
                        DAMA.CORKIS.teterTopEfita : DAMA.CORKIS.teterTopCorki;
                        break;
                    case board.getTeterBotValue():
                        movingCorki = useEfitaCorki ? 
                        DAMA.CORKIS.teterBottomEfita : DAMA.CORKIS.teterBottomCorki;
                        break;
                    case board.getKingTopValue():
                        movingCorki = useEfitaCorki ?
                         DAMA.CORKIS.kingTopEfita : DAMA.CORKIS.kingTopCorki;
                        break;
                    case board.getKingBotValue():
                        movingCorki = useEfitaCorki ? 
                        DAMA.CORKIS.kingBottomEfita : DAMA.CORKIS.kingBottomCorki;
                        break;

                }      
                corkiCoors = that.corkiCoordinates(movingCorki, startCol * cellWidth, startRow * cellWidth);
                movingCorkiX = corkiCoors[0]; 
                movingCorkiY = corkiCoors[1];                                 
                // start the timer
                clearInterval(timer);
                timer = setInterval(animateMove, speed);
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
                that.repaint(); 
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

        DAMA.send_url = "https://localhost:8000/dama/send";

        DAMA.recieve_url = "https://localhost:8000/dama/recieve";

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
            topBar: "#795548",
            botBar: "#795548"           
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

            },
            
            scaleCorkis : function(factor) {
                for (var corki in DAMA.CORKIS) {
                    DAMA.CORKIS[corki].style.transform = "scale(' + factor + ', ' + factor + ')'";
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
        DAMA.util.scaleCorkis(0.5);
        arbiter.getDisplayer().repaint();
    };

    DAMA.CORKIS = {
        gameOver : document.getElementById('gameoverbot'),
        teterTopCorki : document.getElementById("coke"),
        teterBottomCorki : document.getElementById("pepsi"),
        kingTopCorki : document.getElementById("kingcoke"),
        kingBottomCorki : document.getElementById("kingpepsi"),      
        teterTopEfita : document.getElementById("cokeefita"),
        teterBottomEfita : document.getElementById("pepsiefita"),
        kingTopEfita : document.getElementById("kingcokeefita"),
        kingBottomEfita : document.getElementById("kingpepsiefita")       
    };
    
    prepEnv();
    start();

};
    
window.onload = function() {
    damaSetUp();
};