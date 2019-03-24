

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
	private int numRows;
	private int numColumns;
	private Point[][] location;

	/**
	 * @param board
	 * @param ph 
	 */
	public BoardGraphics(Board board, ActionListener l,MouseListener ph) {
						
				//set layout and create a constraint object 
				GridLayout gb = new GridLayout(8,8);
				setLayout(gb);
				
				
				//get the positions from the board and also instantiate the buttons
				
				positions = board.getPositions();
				numRows = board.getNumRows();
				numColumns = board.getNumColumns();
				allPosGraphics = new PositionGraphics[numRows][numColumns];

				
				for(int i=0; i<numColumns; i++) {
					for(int j=0; j<numRows; j++) {
						
					
							allPosGraphics[j][i] = new PositionGraphics(positions[j][i]);
							positions[j][i].setPosGraph(allPosGraphics[j][i]);
							allPosGraphics[j][i].addActionListener(l);
							if(ph != null) {
								allPosGraphics[j][i].addMouseListener(ph);
								
							}
							allPosGraphics[j][i].setEnabled(false);
							add(allPosGraphics[j][i]);
					
					}
				}
				

				//create a compound border and set it as a border to the panel
				setPanelBoarder();
				
	}
	
	public  void rotate() {
		
		removeAll();
		location = new Point[8][8];
		
		for(int i=numColumns-1; i>=0;i--) {
			for(int j=numRows-1;j>=0; j-- ) {
		
				location[7-j][7-i]=allPosGraphics[j][i].getLocation();

			}
		}
		
		for(int i=0; i<numColumns; i++) {
			for(int j=0; j<numRows; j++) {
				
			
					allPosGraphics[j][i].setLocation(location[j][i]);
					add(allPosGraphics[j][i]);
			
			}
		
		}
		
		repaint();
		
	}

	public PositionGraphics[][] getAllPosGraphics() {
		return allPosGraphics;
	}

	public Dimension getPreferredSize() {
		int width = (int)(Toolkit.getDefaultToolkit().getScreenSize().height*0.8);
		return new Dimension(width,width);
	}
	
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public Dimension getMaximumSize() {
		return getPreferredSize();
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
	

	public void setEnabled(ArrayList<Position> posList, boolean enable,boolean brighten) {
		for(int i=0; i<posList.size(); i++) {
			posList.get(i).getPosGraph().setEnabled(enable);
			posList.get(i).getPosGraph().brighten(brighten);

		}
		
	}
	
	public void setCorkisEnabled(ArrayList<Corki> corkiList, boolean enable,boolean brighten) {
		for(int i=0; i<corkiList.size(); i++) {
			corkiList.get(i).getPos().getPosGraph().setEnabled(enable);
			corkiList.get(i).getPos().getPosGraph().brighten(brighten);
		}
		
	}
	
	public void setCorkisEnabled(ArrayList<Corki> corkiList, boolean enable) {
		for(int i=0; i<corkiList.size(); i++) {
			corkiList.get(i).getPos().getPosGraph().setEnabled(enable);
		}
		
	}
	
	public void enableALL() {
		
		for(int i=0; i<positions[1].length; i++) {
			for(int j=0; j<positions[0].length; j++) {
				positions[j][i].getPosGraph().setEnabled(true);
			}
		}
	}
	
	public void disableALL() {
		
		for(int i=0; i<positions[1].length; i++) {
			for(int j=0; j<positions[0].length; j++) {
				positions[j][i].getPosGraph().setEnabled(false);
			}
		}
	}

	
	public static void main(String[] args) {
		
		JDialog dialog = new JDialog();
		dialog.setSize(300, 300);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		BoardGraphics bg = new BoardGraphics(new Board(), null,null);
		dialog.add(bg);
		dialog.pack();
		
		dialog.setVisible(true);
	}

	//make sure private backgrounds are set to null
	public void repaintCompletely() {
		
		for(int i=0; i<allPosGraphics.length; i++) {
			for(int j=0; j<allPosGraphics[i].length; j++) {
				allPosGraphics[i][j].setBackground(null);
			}
		}
		
		setPanelBoarder();//update border color too
		repaint();
		
	}
	

	

}
