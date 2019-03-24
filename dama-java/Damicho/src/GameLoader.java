
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * @author mzm002
 *
 */
public class GameLoader implements ActionListener {

	public static enum PlayerType {COMP, HUMN, PLAYER1, PLAYER2};
	public static enum Result {VALID_MOVE, INVALID_MOVE, WIN, LOSE, DRAW};

	private static double minOffset = Toolkit.getDefaultToolkit().getScreenSize().getHeight()/20.0;
	private BoardSettings boardSettings;
	private Game game;
	private BoardGraphics boardGraphics;
	private JDialog frame;
	
	private boolean humanToHuman;
	private boolean Tank;
	private PlayerType up;
	private PlayerType whoStarts;
	private int level;
	private boolean start;
	private String player1;
	private String player2;
	private boolean newGame;
	private PlayerType currentPlayer;
	private PositionGraphics firstPosGraph;
	private boolean firstClickDone;
	private PositionGraphics secondPosGraph;
	private Result result;
	private JDialog finalDialog;



	
	/**
	 * 
	 */
	public GameLoader() {
		boardSettings = null;
		game = null;
		boardGraphics = null;
		frame = null;
		humanToHuman = false;
		Tank = false;
		up = null;
		whoStarts = null;
		level = 0;
		start = false;
		player1 = null;
		player2 = null;
		newGame = false;
		currentPlayer = null;
		firstPosGraph = null;
		firstClickDone = false;
		secondPosGraph = null;
		result = null;
		finalDialog = null;
	}

	public JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createMenu("File", new String[]{"Board Settings","New Game","History","Exit"}));

        return menuBar;
    }

    public JMenu createMenu(String title, String[] itemNames) {
    	
        JMenu menu = new JMenu(title);  
        
        for(int i =0; i<itemNames.length; i++) {
        	JMenuItem item = new JMenuItem(itemNames[i]);
            item.setActionCommand(itemNames[i]);
            item.addActionListener(this);
            menu.add(item);
        }
        
        
        return menu;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public void run() throws Exception {

    		while(!start) {
    		}
    		frame = new JDialog(); //it should be JFrame, i used JDialog temporarily
            resizeToCenter(frame, minOffset);
            frame.addWindowListener(new windowHandler());
            frame.addComponentListener(new componentHandler());
           
            //instantiate game class
            game = new Game(this);
            
            //the following two lines should be deleted later
            game.getPlayer1().corkiList.get(4).setAsKing(true);
            game.getPlayer2().corkiList.get(4).setAsKing(true);


            //add the board graphics
            boardGraphics = new BoardGraphics(game.getBoard(),new ClickHandler());
            frame.setLayout(new BorderLayout());
            frame.add(getToolBar(),BorderLayout.SOUTH);
            frame.add(boardGraphics,BorderLayout.CENTER);
            //instantiate the board settings class.
            boardSettings = new BoardSettings(boardGraphics,up,player1,player2);
            
            //set the menu bar
            frame.setJMenuBar(getMenuBar());
            
            //Display the window.
            frame.setVisible(true);
    	      
            //start the game
        	currentPlayer = whoStarts;
            if(currentPlayer == PlayerType.COMP) {
            	result = game.doYourTurn(currentPlayer);
            	respondToMove(result,null,null);
            	invert(currentPlayer);
            } else {
	        	boardGraphics.setCorkisEnabled(game.getMovableCorkis(currentPlayer),false);
            }
    }


	private void invert(PlayerType player) {
		
		if(player == PlayerType.HUMN) {
			currentPlayer = PlayerType.COMP;
		} else if(player == PlayerType.COMP) {
			currentPlayer = PlayerType.HUMN;
		} else if(player == PlayerType.PLAYER1) {
			currentPlayer = PlayerType.PLAYER2;
		} else if(player == PlayerType.PLAYER2) {
			currentPlayer = PlayerType.PLAYER1;
		}
	}

	private JToolBar getToolBar() {
		
		JToolBar toolBar = new JToolBar();
		
		addButtonToToolBar("Restart",toolBar);
		addButtonToToolBar("Withdraw",toolBar);
		addButtonToToolBar("Draw",toolBar);
		addButtonToToolBar("UndoMove",toolBar);
		addButtonToToolBar("History",toolBar);

		
		return toolBar;
	}

	private void addButtonToToolBar(String name, JToolBar toolBar) {
		JButton button = new JButton(name);
		button.addActionListener(this);
		button.setActionCommand(name);
		toolBar.addSeparator();
		toolBar.add(button);
		
	}

	public static void main(String[] args) {
		
		GameLoader gl;
		Receptionist r;
		boolean newGame = true;
		
		while(newGame) {
			
		   gl = new GameLoader();
		   r = new Receptionist(gl);
		   r.run();
		   try {
			gl.run();
		} catch (Exception e) {
			System.out.println("exception caught");
			e.printStackTrace();
		}
		   
		   while(!gl.isNewGame()) {}
		   newGame = true;
		}
    }
    
	private boolean isNewGame() {
		return newGame;
	}
	

	public static void resizeToCenter(JDialog frame, double minOffset2) {
    	 //you can clarify this if you want, its just to center the frame in the screen
        double[] locAndSize = getLocAndSize(Toolkit.getDefaultToolkit().getScreenSize(), minOffset2);
        frame.setLocation((int)locAndSize[0], (int)locAndSize[1]);
        frame.setSize((int)locAndSize[2], (int)locAndSize[2]);
		
	}
	//general helper for centering window method
	public static double[] getLocAndSize(Dimension d, double minOffset) {
		
		double size = Math.min(d.getWidth(), d.getHeight())-2*minOffset;
		double x_coor = (d.getWidth()-size)/2.0;
		double y_coor = (d.getHeight()-size)/2.0;
		double[] locAndSize = {x_coor,y_coor,size};
		
		return locAndSize;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Board Settings")) {
			JDialog dialog = new JDialog(frame);
			dialog.setSize(750, 360);
			boardSettings.moreSettings(dialog.getContentPane());
			resizeToCenter(dialog);
			dialog.setVisible(true);
		} else if(command.equals("Restart")) {
			frame.dispose();
			try {
				run();
			} catch (Exception e1) {
				out("exception caught");
				e1.printStackTrace();
			}
		} else if(command.equals("New Game")) {
			out("event detected");
			newGame = true;
			frame.dispose();
		} else if(command.equals("History")) {
			showHistory();
		} else if(command.equals("Withdraw")) {
			result = Result.LOSE;
			respondToMove(result,null,null);
		} else if(command.equals("Draw")) {
			result = Result.DRAW;
			respondToMove(result,null,null);
		} else if(command.equals("UndoMove")) {
				game.undoMove(currentPlayer);
		} else if(command.equals("Exit")) {
			System.exit(0);
		} 
			
		

	}



	private void respondToMove(Result result, PositionGraphics firstPosGraph,
			PositionGraphics secondPosGraph) {
		if(result != Result.INVALID_MOVE && result != Result.VALID_MOVE && result != null) {
			handleFareWell(result);
		} else {
			if(result == Result.INVALID_MOVE) {
				JOptionPane.showMessageDialog(frame, "Oh oh you can't do like that");
			} else if(result == Result.INVALID_MOVE) {
				boardGraphics.performGraphicMove(firstPosGraph,secondPosGraph);
			}
		}
		
	}

	/**
	 * @param result
	 */
	private void handleFareWell(Result result) {
		if(result == Result.DRAW ) {
			if(fakeInvert(currentPlayer) == PlayerType.COMP || currentPlayer == PlayerType.COMP) {
				JOptionPane.showMessageDialog(frame, "Oh no you can't draw against the computer");
				
			} else {
				int agreed = JOptionPane.showConfirmDialog(frame, game.getPlayer(fakeInvert(currentPlayer)).name+
						" do you want to draw too?",null, JOptionPane.YES_NO_OPTION);
				if(agreed == 0) {
					displayFareWell(result);

				}
			}
		} else {
			if(currentPlayer == PlayerType.COMP) {
				invert(currentPlayer);
				displayFareWell(result);
				invert(currentPlayer);

			} else {
				displayFareWell(result);

			}

		}
	}

	private PlayerType fakeInvert(PlayerType currentPlayer2) {
		PlayerType fakePlayer = currentPlayer;
		invert(fakePlayer);
		return fakePlayer;
	}

	private void displayFareWell(Result result) {
		
		finalDialog = new JDialog(frame);
		Dimension d = new Dimension((int)(frame.getWidth()*0.75),(int)(frame.getHeight()*0.5));
		
		JPanel subPanel1 = new JPanel(new BorderLayout());
		subPanel1.setBackground(Color.cyan);
		JPanel subPanel2 = new JPanel(new BorderLayout());
		subPanel2.setBackground(Color.cyan);
		
		
		Dimension d1 = new Dimension((int)d.getWidth(),(int)d.getHeight()/2);
		
		String text =getfirstMessage(result);
		Message message = new Message(text.endsWith(" YOU")?text.substring(0, text.length()-3)+"BUDDY":text,Color.BLUE,d1,2);		
		
		Message message2 = new Message(getSecondMessage(result),Color.RED,d1,1);
		
		subPanel1.add(message);
		subPanel2.add(message2);
		
		if(finalDialog != null) {
			finalDialog.dispose();
		}
		JPanel panel = new JPanel(new GridLayout(0,1));
		
		Border border = BorderFactory.createLineBorder(Color.RED);
		Border border1 = BorderFactory.createLoweredBevelBorder();
		Border compound1 = new CompoundBorder(border1,border);
		Border border2 = BorderFactory.createLineBorder(Color.RED);
		Border compound = new CompoundBorder(border2,compound1);

		panel.setBorder(compound);
		
		panel.setSize(d);
		finalDialog.setSize(d);
		resizeToCenter(finalDialog);
		panel.add(subPanel1);
		panel.add(subPanel2);
		
		finalDialog.setContentPane(panel);
		finalDialog.setVisible(true);
		
	}

	private String getfirstMessage(Result result) {

		switch (result) {
		case WIN: return "CONGRATULATIONS "+game.getPlayer(currentPlayer).name.toUpperCase();
		case LOSE: return "SORRY "+game.getPlayer(currentPlayer).name.toUpperCase();
		case DRAW: return "NOBODY WON";

		}
		return null;
	}

	private String getSecondMessage(Result result) {
		
		switch (result) {
		
		case WIN: return "YOU JUST WON!";
		case LOSE: return "YOU JUST LOST!";
		case DRAW: return "THIS ROUND!";

		}
		return null;
	}

	private void showHistory() {
		//to do the real stuff
		JOptionPane.showMessageDialog(frame, "No history made yet");
		
	}

	
	class componentHandler extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			JDialog frame = (JDialog) e.getSource();
			
			frame.setSize(Math.min(frame.getWidth(), frame.getHeight()), Math.min(frame.getWidth(), frame.getHeight()));	
		}
	}
	
	private void out(String string) {
		System.out.println(string);		
	}

	/**
	 * @return the tank
	 */
	public boolean isTank() {
		return Tank;
	}

	/**
	 * @param tank the tank to set
	 */
	public void setTank(boolean tank) {
		Tank = tank;
	}

	/**
	 * @return the humanToHuman
	 */
	public boolean isHumanToHuman() {
		return humanToHuman;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the start
	 */
	public boolean isStart() {
		return start;
	}

	/**
	 * @param up the up to set
	 */
	public void setUp(PlayerType up) {
		this.up = up;
	}

	/**
	 * @param whoStarts the whoStarts to set
	 */
	public void setWhoStarts(PlayerType whoStarts) {
		this.whoStarts = whoStarts;
	}

	/**
	 * @param player1 the player1 to set
	 */
	public void setPlayer1(String player1) {
		this.player1 = player1;
	}

	/**
	 * @param player2 the player2 to set
	 */
	public void setPlayer2(String player2) {
		this.player2 = player2;
	}

	public PlayerType getWhoStarts() {
		return whoStarts;
	}

	public PlayerType getUp() {
		return up;
	}

	/**
	 * @return the player1
	 */
	public String getPlayer1() {
		return player1;
	}

	/**
	 * @return the player2
	 */
	public String getPlayer2() {
		return player2;
	}

	public void setHumanToHuman(boolean humanToHuman) {
		this.humanToHuman = humanToHuman;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public static void resizeToCenter(Container dialog) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x_coor = (int) ((d.getWidth()-dialog.getWidth())/2.0);
		int y_coor = (int) ((d.getHeight()-dialog.getHeight())/2.0);
		dialog.setLocation(x_coor, y_coor);
		
	}

	class windowHandler extends WindowAdapter {
		public void windowClosed() {
			System.exit(0);
		}
	}
	
	class ClickHandler extends MouseAdapter {
		 public void mouseClicked(MouseEvent e) {
			 
					ArrayList<Position> legalPositions = null;
					
					if(!firstClickDone) {
						
						firstPosGraph = (PositionGraphics)e.getSource();
						firstPosGraph.brighten(true);
						firstClickDone = true;
			        	boardGraphics.setCorkisEnabled(game.getMovableCorkis(currentPlayer),false);
			        	legalPositions = game.getNextLegalPositions(currentPlayer,firstPosGraph);
			        	boardGraphics.setEnabled(legalPositions,true);

					} else {
						
						secondPosGraph = (PositionGraphics)e.getSource();
						firstClickDone = false;
						boardGraphics.setEnabled(legalPositions,false);
						secondPosGraph.brighten(true);
						result = game.doYourTurn(currentPlayer,firstPosGraph,secondPosGraph);
						respondToMove(result,firstPosGraph,secondPosGraph);
			        	invert(currentPlayer);
			        	
			        	if(currentPlayer == PlayerType.COMP) {	
			        		
			        		result = game.doYourTurn(currentPlayer);
			            	respondToMove(result,null,null);
			            	invert(currentPlayer);
			            	
			        	}

						
					}
			 }
		 
	}
	/**
	 * @param newGame the newGame to set
	 */
	public void setNewGame(boolean newGame) {
		this.newGame = newGame;
	}



}
