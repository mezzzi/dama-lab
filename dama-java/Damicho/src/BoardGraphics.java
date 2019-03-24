

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;


/**
 * This class represents the graphics look and dimension of dama board
 * @author mzm002
 *
 */
public class BoardGraphics extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Position[][] positions;
	private PositionGraphics[][] allPosGraphics;

	/**
	 * @param board
	 */
	public BoardGraphics(Board board, MouseListener l) {
		
				//set layout and create a constraint object 
				GridLayout gb = new GridLayout(8,8);
				setLayout(gb);
				
				//create a compound border and set it as a border to the panel
				setPanelBoarder();
				
				//get the positions from the board and also instantiate the buttons
				
				positions = board.getPositions();
				int numRows = board.getNumRows();
				int numColumns = board.getNumColumns();
				allPosGraphics = new PositionGraphics[numRows][numColumns];

				
				for(int i=0; i<numColumns; i++) {
					for(int j=0; j<numRows; j++) {
						
					
							allPosGraphics[j][i] = new PositionGraphics(positions[j][i]);
							positions[j][i].setPosGraph(allPosGraphics[j][i]);
							allPosGraphics[j][i].addMouseListener(l);
							allPosGraphics[j][i].setEnabled(false);
							add(allPosGraphics[j][i]);
					
					}
				}
				
				repaint();
				
	}

	/**
	 * I did this so that I can repaint the boards boarder
	 */
	private void setPanelBoarder() {
		Border compound,raisedbevel, loweredbevel, line;
		
        raisedbevel = BorderFactory.createRaisedBevelBorder();
        loweredbevel = BorderFactory.createLoweredBevelBorder();
		line = BorderFactory.createLineBorder(PositionGraphics.borderColor);
		 
        compound = BorderFactory.createCompoundBorder(
                                  raisedbevel, loweredbevel);
 
        compound = BorderFactory.createCompoundBorder(
                                  compound, line);
        
        compound = BorderFactory.createCompoundBorder(
                line, compound);
        
        compound = BorderFactory.createCompoundBorder(
                compound, compound);
        
        
        setBorder(compound);
	}
	

	public void setEnabled(ArrayList<Position> posList, boolean b) {
		for(int i=0; i<posList.size(); i++) {
			posList.get(i).getPosGraph().setEnabled(b);
		}
		
	}
	
	public void setCorkisEnabled(ArrayList<Corki> corkiList, boolean b) {
		for(int i=0; i<corkiList.size(); i++) {
			corkiList.get(i).getPos().getPosGraph().setEnabled(b);
		}
		
	}
	
	public void enableALL() {
		
		for(int i=0; i<positions[1].length; i++) {
			for(int j=0; j<positions[0].length; j++) {
				positions[j][i].getPosGraph().setEnabled(true);
			}
		}
	}
	public void performGraphicMove(PositionGraphics firstPosGraph,
			PositionGraphics secondPosGraph) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		
		JDialog dialog = new JDialog();
		dialog.setSize(300, 300);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		BoardGraphics bg = new BoardGraphics(new Board(), null);
		dialog.add(bg);
		
		dialog.setVisible(true);
	}
	

	

}
