    makeDisplayer = function (boardFrame, boardObj) {

        var cellWidth = 70, boardFrameWidth = 560,
            selectedX = -1, selectedY = -1,
            highlighteds = [], firstClickedCell,
            movingCorki,

            getCircleCoors = function (args) { // args = {cell:[]} or {coors:[]}

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

            repaintCanvas = function () {

                var row, col, cell, circleCoors,
                    ctx = boardFrame.getContext("2d"), fill = "black";

                ctx.clearRect(0, 0, boardFrameWidth, boardFrameWidth);
                for (row = 0; row < 8; row += 1){
                    for (col = 0; col < 8; col += 1){
                        cell = [row, col];
                        if (selectedX !== -1 && selectedX === row &&
                            selectedY === col ){
                            fill = DAMA.colors.highlight;
                            selectedX = selectedY = -1;
                        } else if (boardObj.isIllegalCell(cell)){
                            fill = DAMA.colors.illegal;
                        } else {
                            fill = DAMA.colors.legal;
                        }
                        ctx.fillStyle = fill;
                        ctx.fillRect(col * cellWidth, row * cellWidth, cellWidth, cellWidth);
                        if (boardObj.isOccupied(cell)){
                            fill = getColor(cell);
                            ctx.fillStyle = fill;   
                            ctx.beginPath();
                            circleCoors = getCircleCoors({cell:cell});
                            ctx.arc(circleCoors.x, circleCoors.y,
                                circleCoors.r, 0, 2*Math.PI);
                            ctx.fill();                  
                        }
                        
                    }
                }         
                if (movingCorki) {
                    ctx.fillStyle = movingCorki.color;
                    ctx.beginPath();
                    circleCoors = getCircleCoors({coors:[movingCorki.x, movingCorki.y]});
                    ctx.arc(circleCoors.x, circleCoors.y,
                        circleCoors.r, 0, 2*Math.PI);
                    ctx.fill();                      
                }    

            },

            getColor = function (cell) {

                if (boardObj.isTeterTop(cell)) {
                    return DAMA.colors.teterTop;
                } else if (boardObj.isTeterBottom(cell)) {
                    return DAMA.colors.teterBot;
                } else if (boardObj.isKingTop(cell)) {
                    return DAMA.colors.kingTop;
                } else if (boardObj.isKingBottom(cell)) {
                    return DAMA.colors.kingBot;
                }

            },

            animateMove = function (secondClickedCell) {

                var dx, dy, delay = 100, 
                    numSteps = 10, curStep = 1,
                    moveIt;

                dx = (secondClickedCell[1] - firstClickedCell[1]) * cellWidth / numSteps;
                dy = (secondClickedCell[0] - firstClickedCell[0]) * cellWidth / numSteps;
                movingCorki = {
                    x: firstClickedCell[1] * cellWidth,
                    y: firstClickedCell[0] * cellWidth,
                    color: getColor(firstClickedCell)
                };
                moveIt = function () {
                    movingCorki.x += dx;
                    movingCorki.y += dy;
                    repaintCanvas();
                    if (curStep < numSteps) {
                        curStep += 1;
                        setTimeout(moveIt, delay);
                    } else {
                        movingCorki = null;
                    }
                };
                setTimeout(moveIt, delay);

            },

            canvasClickHandler = function (evt) {

                var r = parseInt((evt.pageY - boardFrame.offsetTop) / cellWidth, 10),
                    c = parseInt((evt.pageX - boardFrame.offsetLeft) / cellWidth, 10);

                if (firstClickedCell) {
                    animateMove([r, c]);
                    firstClickedCell = null;
                } else {
                    firstClickedCell = [r, c];
                }
                selectedX = r;
                selectedY = c;
                repaintCanvas();    

            },

            repaintSVG = function () {

                var row, col, fill, cellIndex, cell, cellR, cellC,
                    rectDummy = document.getElementById(DAMA.ids.dummyRectId),
                    rect = rectDummy.cloneNode(true),

                    addClickListener = function (that) {
                        DAMA.util.addListener(that, 'click', function () {
                            that.style.fill = DAMA.colors.highlight;
                            for (cellIndex in highlighteds){
                                if (highlighteds.hasOwnProperty(cellIndex)){
                                    cell = highlighteds[cellIndex];
                                    cellR = parseInt(cell.getAttribute("r"), 10);
                                    cellC = parseInt(cell.getAttribute("c"), 10);
                                    cell.style.fill = (cellR + cellC) % 2 === 0 ? 
                                    DAMA.colors.illegal : DAMA.colors.legal;
                                }
                            }
                            highlighteds = [];
                            highlighteds.push(that);                            
                        });
                    };

                boardFrame.removeChild(rectDummy);
                for (row = 0; row < 8; row += 1){
                    for (col = 0; col < 8; col += 1){
                        fill = boardObj.isIllegalCell([row, col]) ? 
                        DAMA.colors.illegal : DAMA.colors.legal;
                        rect = rect.cloneNode(true);
                        rect.id = "cell"+row+""+col;
                        rect.setAttribute("r", row);
                        rect.setAttribute("c", col);
                        rect.setAttribute("x", col * cellWidth);
                        rect.setAttribute("y", row * cellWidth);    
                        rect.setAttribute("width", cellWidth);
                        rect.setAttribute("height", cellWidth);
                        rect.style.fill = fill;
                        addClickListener(rect);
                        boardFrame.appendChild(rect);
                    }
                }           
                
            };

        if (DAMA.useCanvas){
            DAMA.util.addListener(boardFrame, "click", canvasClickHandler);
        }

        return {

            repaint: function () {

                if (DAMA.useCanvas){
                    repaintCanvas();
                } else {
                    repaintSVG();
                }

            }

        };

    },

    
        resetDisplayerStates = function () {

            highlightedCells = Array.matrix(board.NUM_ROWS, 
                board.NUM_ROWS, 0);
            enabledCells = Array.matrix(board.NUM_ROWS, 
                board.NUM_ROWS, 0);
            eatenCorkis = Array.matrix(4, board.NUM_ROWS, null);
            temporarilyEnabledCells = [];

        },