"use strict";

var makeGuiDisplayer = function () {

const cellWidth = 70;

var  boardFrame, board, arbiter,
    highlightedCells = Array.matrix(board.NUM_ROWS, 
        board.NUM_ROWS, 0),
    enabledCells = Array.matrix(board.NUM_ROWS, 
        board.NUM_ROWS, 0),
    eatenCorkis = Array.matrix(4, board.NUM_ROWS, null),
    temporarilyEnabledCells = [],
    movingCorki, movingCorkiX, movingCorkiY,
    isAnimatingEfita,
    efitaCorki,

resetDisplayerStates = function () {

    highlightedCells = Array.matrix(board.NUM_ROWS, 
        board.NUM_ROWS, 0);
    enabledCells = Array.matrix(board.NUM_ROWS, 
        board.NUM_ROWS, 0);
    eatenCorkis = Array.matrix(4, board.NUM_ROWS, null);
    temporarilyEnabledCells = [];

},

canvasClickHandler = function (evt) {

    var row = parseInt((evt.pageY - boardFrame.offsetTop) / 
        cellWidth, 10),
        col = parseInt((evt.pageX - boardFrame.offsetLeft) / 
            cellWidth, 10);

    if (row >= 0 && row < board.NUM_ROWS + 1) {
        if (enabledCells[row][col] ==- 1 && 
        !arbiter.isWaitingForOpponent()) {
            arbiter.handleCellClick([row, col]);
        }                    
    } 

};        

return {

setUP: function (boardObj, arbiterObj) {

    board = boardObj;
    arbiter = arbiterObj;

    if (DAMA.useCanvas){
        DAMA.util.addListener(boardFrame, "click", 
            canvasClickHandler);
    }

},


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
        DAMA.colors.tetrTopEfita : DAMA.colors.tetrTop;
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
        boardFrameWidth = 700,
        painter = boardFrame.getContext("2d");

    // apparently in javascript canvas you have to start 
    // by clearing the canvas screen yourself
    painter.clearRect(0, 0, boardFrameWidth, boardFrameWidth);
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
                painter.fillRect(ycoor, xcoor, cellWidth, 
                    cellWidth);
                // and draw the corkis, if there are any
                if (board.isOccupied(cell)) {                         
                    fill = this.getColor(cell);
                    this.drawCircle(painter, {cell:cell}, fill);                       
                }
            } else {
                fill = i === 0 ? DAMA.colors.topBar : 
                DAMA.colors.botBar;
                painter.fillStyle = fill;
                painter.fillRect(ycoor, xcoor, cellWidth, 
                    cellWidth);
                // what remains is the part where corkis
                // get added to the top and bottom bars
                row = i === 0 ? 0 : 2;
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
            {coors:[movingCorkiX - offset, movingCorkiY - offset]},
                        movingCorki);          
    }      
},

displayMove: function (startCell, landingCell, 
    advancingCorkiVal, eatenCorki) {

    var didEat = eatenCorki[0] !== -1;
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
            this.repaint();         
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
        dy = ((destnRow - startRow)*cellWidth)/numSteps; curStep = 0; 
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

    var animateEfita, count = 0, 
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
            this.displayMove(null, null, -1, efitaInfo);
        } else {
            setTimeout(animateEfita, delay);
        }
        
    };

    setTimeout(animateEfita, delay);
}
 

};

};