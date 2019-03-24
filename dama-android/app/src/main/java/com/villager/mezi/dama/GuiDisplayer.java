package com.villager.mezi.dama;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import static com.villager.mezi.dama.Board.NUM_ROWS;
import static com.villager.mezi.dama.Board.TETER_BOTTOM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class GuiDisplayer extends View {

    private Board board;
    private Arbiter arbiter;

    private Paint painter = null;

    private final int cellWidth;
    private int[][] highlightedCells = new int[NUM_ROWS][NUM_ROWS];
    private int[][] enabledCells = new int[NUM_ROWS][NUM_ROWS];
    private Drawable[][] eatenCorkis = new Drawable[4][NUM_ROWS];
    private ArrayList<int[]> temporarilyEnabledCells = new ArrayList<>();

    private Drawable movingCorki;
    private int movingCorkiX, movingCorkiY;

    private boolean isAnimatingEfita;
    private boolean isGameOver = false;
    private int[] efitaCorki;

    Drawable teterTopCorki, teterBottomCorki;
    Drawable kingTopCorki, kingBottomCorki;
    Drawable teterTopEfita, teterBottomEfita;
    Drawable kingTopEfita, kingBottomEfita;

    Context context;

    float corkiWidth;
    int ycoorOffset;

    public GuiDisplayer(Context context, Board board, Arbiter arbiter, int width, int height) {
        super(context);
        this.context = context;
        cellWidth = width / NUM_ROWS;
        corkiWidth = cellWidth * 0.75f;
        setMinimumWidth(width);
        setMinimumHeight(height);
        ycoorOffset = (height - (width + 2 * cellWidth)) / 2;
        this.board = board; this.arbiter = arbiter;
        setUP();
    }
    private void handleTouch(float x, float y){
        y = y - ycoorOffset;
        int row = (int)((y / cellWidth) - 1), col = (int)(x / cellWidth);
        if(row >= 0 && row < NUM_ROWS) {
            if(enabledCells[row][col] == 1) // can interact only with enabled cells
                arbiter.handleCellClick(new int[] {row, col});
        }
    }

    public void showBoard() {
        setVisibility(VISIBLE);
    }

    @SuppressLint("NewApi")
    private void setUP(){

        arbiter.setDisplayer(this);

        setOnTouchListener(new TrackingTouchListener());
        teterTopCorki = context.getDrawable(R.drawable.coke);
        teterBottomCorki = context.getDrawable(R.drawable.pepsi);
        kingTopCorki = context.getDrawable(R.drawable.kingcoke);
        kingBottomCorki = context.getDrawable(R.drawable.kingpepsi);
        teterTopEfita = context.getDrawable(R.drawable.cokeefita);
        teterBottomEfita = context.getDrawable(R.drawable.pepsiefita);
        kingTopEfita = context.getDrawable(R.drawable.kingcokeefita);
        kingBottomEfita = context.getDrawable(R.drawable.kingpepsiefita);

    }

    public void resetDisplayerStates(){
        highlightedCells = new int[NUM_ROWS][NUM_ROWS];
        enabledCells = new int[NUM_ROWS][NUM_ROWS];
        temporarilyEnabledCells = new ArrayList<>();
        eatenCorkis = new Drawable[4][NUM_ROWS];
        isGameOver = false;
        postInvalidate();
    }

    public void setBoard(Board board){this.board = board;}

    public void enableCell(int[] cell){enabledCells[cell[0]][cell[1]] = 1;}

    public void enableCellTemporarily(int[] cell){
        enabledCells[cell[0]][cell[1]] = 1;
        temporarilyEnabledCells.add(cell);
    }

    public void disableAll(){enabledCells = new int[NUM_ROWS][NUM_ROWS];}

    public void disableAllTemporarilyEnabledCells(){
        for (int[] cell: temporarilyEnabledCells) {
            enabledCells[cell[0]][cell[1]] = 0;
        }
        temporarilyEnabledCells.clear();
    }

    public void highlightCell(int[] cell){
        highlightedCells[cell[0]][cell[1]] = 1;
        postInvalidate();
    }

    public void unHighlightCell(int[] cell){
        highlightedCells[cell[0]][cell[1]] = 0;
        postInvalidate();
    }

    public void unHighlightAll(){highlightedCells = new int[NUM_ROWS][NUM_ROWS];}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                getSuggestedMinimumWidth(),
                getSuggestedMinimumHeight());
    }
    @Override
    protected void onDraw(Canvas canvas) {
        painter = new Paint();
        painter.setStyle(Paint.Style.FILL);
        int row, xcoor, ycoor, offset;
        boolean useEfitaCorki; Drawable corki; Rect corkiBound;
        for (int i = 0; i < NUM_ROWS + 2; i++) {
            for (int col = 0; col < NUM_ROWS; col++) {
                xcoor = col * cellWidth; ycoor = ycoorOffset + i * cellWidth;
                Rect cell = new Rect(xcoor, ycoor, xcoor + cellWidth, ycoor + cellWidth);
                if(i > 0 && i < NUM_ROWS + 1 ) {
                    row = i - 1;
                    if (highlightedCells[row][col] == 1) painter.setColor(Color.LTGRAY);
                    else painter.setColor(board.isIllegalCell(row, col) ? Color.WHITE : Color.BLACK);
                    canvas.drawRect(cell, painter);
                    if (board.isOccupied(row, col)) {
                        useEfitaCorki = isAnimatingEfita && Arrays.equals(new int[]{row, col}, efitaCorki);
                        if(board.isTeterTop(row, col)) corki = useEfitaCorki ? teterTopEfita : teterTopCorki;
                        else if (board.isTeterBottom(row, col)) corki = useEfitaCorki ? teterBottomEfita : teterBottomCorki;
                        else if (board.isKingTop(row, col)) corki = useEfitaCorki ? kingTopEfita : kingTopCorki;
                        else corki = useEfitaCorki ? kingBottomEfita : kingBottomCorki;
                        corkiBound = corkiBounds(xcoor, ycoor);
                        corki.setBounds(corkiBound);
                        corki.draw(canvas);
                    }
                } else {
                    painter.setColor(Color.DKGRAY);
                    if ( i == 0 ) {
                        canvas.drawRect(cell.left, 0, cell.right, cell.bottom, painter);
                    } else {
                        canvas.drawRect(cell.left, cell.top, cell.right, getHeight() , painter);
                    }
                    row = i == 0 ? 0 : 2;
                    corki = eatenCorkis[row][col];
                    if(corki != null) {
                        corkiBound = corkiBounds(xcoor, ycoor);
                        corki.setBounds(corkiBound);
                        corki.draw(canvas);
                        corki = eatenCorkis[row+1][col]; offset = cellWidth / 10;
                        if(corki != null) {
                            corkiBound = corkiBoundsOff(xcoor, ycoor, offset);
                            corki.setBounds(corkiBound);
                            corki.draw(canvas);                        }
                    }
                }
            }
        }
        if (movingCorki != null) {
            corkiBound = corkiBounds(movingCorkiX, movingCorkiY);
            movingCorki.setBounds(corkiBound);
            movingCorki.draw(canvas);
        }
        if (isGameOver) {
            int r, g, b;
            r = Color.red(Color.CYAN); g = Color.green(Color.CYAN); b = Color.blue(Color.CYAN);
            painter.setColor(Color.argb(180, r, g, b));
            Typeface font = Typeface.create(Typeface.SERIF, Typeface.BOLD);
            painter.setTypeface(font);
            painter.setTextSize(70);

            canvas.drawRect(getOverlayBox(false), painter);

            String topMsg = "Game Over!";
            Player winner = arbiter.getWinner();
            String botMsg = winner.getName() + " WON";
            Rect topMsgBound = new Rect();
            Rect botMsgBound = new Rect();

            painter.getTextBounds(topMsg, 0, topMsg.length(), topMsgBound);
            painter.getTextBounds(botMsg, 0, botMsg.length(), botMsgBound);

            painter.setColor( winner.isTop() ? Color.RED : Color.BLUE);

            centeredMessageBox(topMsgBound, -0.2f);
            centeredMessageBox(botMsgBound, 1.8f);

            canvas.drawText(topMsg, topMsgBound.left, topMsgBound.top, painter);
            canvas.drawText(botMsg, botMsgBound.left, botMsgBound.top, painter);
        }
    }

    private void centeredMessageBox(Rect msgBox, float heightOffset) {
        int width, height, xcoor, ycoor;
        width = cellWidth * NUM_ROWS;
        height = (NUM_ROWS + 2) * cellWidth;
        xcoor = (width - msgBox.width()) / 2;
        ycoor = ycoorOffset + (int)(heightOffset * 0.75 * msgBox.height()) + (height - msgBox.height()) / 2;
        msgBox.set(xcoor, ycoor, xcoor + msgBox.width(), ycoor + msgBox.height());
    }

    private Rect getOverlayBox(boolean isPartial) {
        int width, height, xcoor, ycoor;
        width = cellWidth * NUM_ROWS;
        height = (NUM_ROWS + 2) * cellWidth;
        xcoor = (int) (0.125 * width);
        ycoor = ycoorOffset + (int) (0.2 * height);
        if (isPartial) {
            return new Rect(xcoor, ycoor, xcoor + (int)(width * 0.75), ycoor + (int)(height * 0.6));
        } else {
            return new Rect(0, ycoorOffset, width, ycoorOffset + height);
        }
    }

    private void updateView() {
        postInvalidate();
    }

    private Rect corkiBounds(int xcoor, int ycoor) {
        int offset = (int) ((cellWidth - corkiWidth) / 2);
        return new Rect(xcoor + offset, ycoor + offset, xcoor + offset + (int)corkiWidth ,
                ycoor + offset + (int)corkiWidth);
    }

    private Rect corkiBoundsOff(int xcoor, int ycoor, int off) {
        int offset = off + (int) ((cellWidth - corkiWidth) / 2);
        return new Rect(xcoor + offset, ycoor + offset, xcoor + offset + (int)corkiWidth ,
                ycoor + offset + (int)corkiWidth);
    }
    public void displayMove(final int[] startCell, final int[] landingCell, final int advancingCorkiVal, final int[] eatenCorki) {
        if(eatenCorki[0] != -1) board.setCellValue(eatenCorki[2], eatenCorki[0], eatenCorki[1]);
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            int landingCellValue, startRow, startCol, destnRow, destnCol, numSteps, dx;
            int dy, curStep, xcoor, ycoor, advancingCorki;
            boolean initialSetUpDone = false, hasMoved = false, hasEaten = false, isMovingEfita = false;
            int[] eatenCorkiSotrageLoc;

            @Override
            public void run() {
                if(!initialSetUpDone){ setup(10, startCell, landingCell, advancingCorkiVal);}
                if(!isMovingEfita){ curStep++; movingCorkiX += dx; movingCorkiY += dy;}
                if (isMovingEfita || curStep == numSteps) {
                    if(!hasMoved){hasMoved = true; board.setCellValue(landingCellValue, landingCell);}
                    if(isMovingEfita || (eatenCorki[0] != -1 && !hasEaten)){
                        int opponentNumEatenCorkis = 12 - arbiter.getOpponnentPlayer().getNumCorkis();
                        boolean isPlayerTop = arbiter.getCurrentPlayer().equals(arbiter.getTopPlayer());
                        if(isMovingEfita){
                            isPlayerTop = !isPlayerTop; opponentNumEatenCorkis = 12 - arbiter.getCurrentPlayer().getNumCorkis();
                        }
                        int row = isPlayerTop ? -1 : 8; int col = opponentNumEatenCorkis % NUM_ROWS;
                        hasEaten = true; int[] eatenCorkiDestn = {row, col}; isMovingEfita = false;
                        board.makeEmpty(eatenCorki[0], eatenCorki[1]);
                        setup(20, new int[] {eatenCorki[0], eatenCorki[1]}, eatenCorkiDestn, eatenCorki[2]);
                        int eatenCorkiStorageRow = isPlayerTop ? (opponentNumEatenCorkis / NUM_ROWS) : 2 + (opponentNumEatenCorkis / NUM_ROWS);
                        eatenCorkiSotrageLoc = new int[] {eatenCorkiStorageRow, col};
                    } else {
                        if(eatenCorki[0] != -1) eatenCorkis[eatenCorkiSotrageLoc[0]][eatenCorkiSotrageLoc[1]] = movingCorki;
                        movingCorki = null;
                        isGameOver = arbiter.isTheGameOver();
                        timer.cancel();
                        if(efitaCorki == null){
                            if(!arbiter.processPendingEats(landingCell)) arbiter.checkForEfita();
                        } else {
                            arbiter.getCurrentPlayer().eatEfita(efitaCorki);
                            arbiter.changeTurn(); efitaCorki = null;
                        }
                    }
                }
                updateView();
            }
            void setup(int steps, int[] startCell, int[] landingCell, int advancingCorkiValue){
                if(!initialSetUpDone){
                    initialSetUpDone = true;
                    if(landingCell == null) {hasMoved = true; isMovingEfita = true; return;}
                    else {landingCellValue = board.getCellValue(landingCell); board.makeEmpty(landingCell);}
                }
                startRow = startCell[0] + 1; startCol = startCell[1];
                destnRow = landingCell[0] + 1; destnCol = landingCell[1];
                numSteps = steps; xcoor = startCol * cellWidth;
                ycoor = ycoorOffset + startRow * cellWidth; advancingCorki = advancingCorkiValue;
                dx = ((destnCol - startCol) * cellWidth)/numSteps;
                dy = ((destnRow - startRow) * cellWidth)/numSteps; curStep = 0;
                switch(advancingCorki){
                    case Board.TETER_TOP:movingCorki = efitaCorki != null ? teterTopEfita: teterTopCorki;break;
                    case TETER_BOTTOM:movingCorki = efitaCorki != null ? teterBottomEfita : teterBottomCorki;break;
                    case Board.KING_TOP:movingCorki = efitaCorki != null ? kingTopEfita : kingTopCorki;break;
                    case Board.KING_BOTTOM:movingCorki = efitaCorki != null ? kingBottomEfita : kingBottomCorki;break;
                }
                movingCorkiX = xcoor; movingCorkiY = ycoor;

            }
        }, 0, 50);
    }

    public void displayEfita(final int[] efita) {
        efitaCorki = efita; isAnimatingEfita = true;
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int count = 0, cellValue = board.getCellValue(efita);
            @Override
            public void run() {
                if(board.isEmptyLegalCell(efitaCorki)) board.setCellValue(cellValue, efitaCorki);
                else board.makeEmpty(efitaCorki);
                updateView(); count++;
                if(count > 4) {
                    timer.cancel(); isAnimatingEfita = false;
                    int[] efitaInfo = new int[] {efitaCorki[0], efitaCorki[1], cellValue};
                    displayMove(null, null, -1, efitaInfo);
                }
            }
        }, 0, 300);
    }

    private  final class TrackingTouchListener
            implements View.OnTouchListener
    {
        @Override public boolean onTouch(View v, MotionEvent evt) {
            switch (evt.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handleTouch(evt.getX(), evt.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    //handleTouch(evt.getX(), evt.getY());
                    break;
                default:
                    return false;
            }
            return true;
        }

    }

}
