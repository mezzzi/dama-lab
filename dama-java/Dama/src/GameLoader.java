
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;




/**
 * @author mzm002
 *
 */
public class GameLoader implements ActionListener{

	public static enum PlayerType {COMP, HUMN, PLAYER1, PLAYER2};
	public static enum Result {KEEP_TURN,VALID_MOVE, WIN, LOSE, DRAW};

	private BoardSettings boardSettings;
	private GameOperator game;
	private BoardGraphics boardGraphics;
	private Window frame;
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
	private LineDrawer.GlassPane glassPane;
	private JPopupMenu popUp;
	private JButton gameTypeMenu;
	private JButton statusMenu;
	private Player fakeUp;
	
	//actions
	private ArrayList<DamaAction> actions;
	private DamaAction exitAction;
	private DamaAction statusAction;
	private DamaAction restartAction;
	private DamaAction newGameAction;
	private DamaAction boardSettingsAction;
	private DamaAction withdrawAction;
	private DamaAction drawAction;
	private DamaAction undoAction;
	private DamaAction historyAction;
	private DamaAction rotateAction;
	private ArrayList<History> historyList = new ArrayList<History>();
	private JCheckBox teterByTeter;
	private ImageIcon imageIcon;
	private ClickHandler clickHandler;
	private DamaAction clearHistoryAction;
	

	

	public JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
		BoxLayout bl = new BoxLayout(tb,BoxLayout.X_AXIS);
        tb.setLayout(bl);
        menuBar.add(createMenu("MenuOptions", actions));

        //create status menus
        gameTypeMenu = new JButton("GameType >>> "+(Tank?"Tankegna":"Egregna"));
        statusMenu = new JButton("ClickToSeeStatus");
        statusMenu.setOpaque(true);
        
        //add them
        gameTypeMenu.setEnabled(false);
        gameTypeMenu.setOpaque(true);
        gameTypeMenu.setBackground(Color.red);
        tb.add(Box.createRigidArea(new Dimension(40,10)));
        tb.add(gameTypeMenu);
        tb.add(Box.createRigidArea(new Dimension(40,10)));
        statusMenu.addActionListener(this);
        statusMenu.setActionCommand("ClickToSeeStatus");
        tb.add(statusMenu);
        
        
        menuBar.setBorder(BorderFactory.createEtchedBorder());
        menuBar.add(tb);
    
        return menuBar;
    }

    public JMenu createMenu(String title, ArrayList<DamaAction> actions) {
    	
        JMenu menu = new JMenu(title);  
        
        for(DamaAction action: actions) {
        	menu.add(action);
        	menu.addSeparator();
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
    		
    		//set Icon
    		imageIcon = new ImageIcon("Picture 017.jpg");
    		
    		//instantiate game class
            game = new GameOperator(this);
            
    		//set eating moods
    		setEatingMoods();
    		//determine users choice of window
    		int userChoice = JOptionPane.showConfirmDialog(null, "Would you like a title bar?","Check",JOptionPane.YES_NO_OPTION);
    		
    		//initiate actions
    		initiateActions();
    		
    		
    		if(userChoice == JOptionPane.YES_OPTION) {
    			frame = new JFrame("Dama");
        		frame.addWindowListener(new windowHandler());
        		((JFrame) frame).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.addComponentListener(new componentHandler());
    		} else {
    			//using JWindow
        		frame = new JWindow();
    		}
    		
    		
			frame.setIconImage(imageIcon.getImage());
    		
    		updatePopUp();
    		
            //add the board graphics
            JPanel boardPanel = new JPanel();
            boardPanel.setLayout(new BorderLayout());
            popupHandler ph = new popupHandler();
            clickHandler = new ClickHandler();
            boardGraphics = new BoardGraphics(game.getBoard(),clickHandler,ph);
            
            if(frame instanceof JFrame) {
                
                 //set border
                 setBorder(boardPanel);
                 boardPanel.add(getToolBar(),BorderLayout.SOUTH);
            	 boardPanel.add(getMenuBar(),BorderLayout.NORTH);
            }
           
            boardPanel.add(boardGraphics,BorderLayout.CENTER);
            

            //add to window
            frame.add(boardPanel);
            
            //glassPane
            glassPane = new LineDrawer().getGlassPane(boardGraphics);
            
            game.getPlayer(PlayerType.PLAYER1).getMind().setGlassPane(glassPane);
    		game.getPlayer(PlayerType.PLAYER2).getMind().setGlassPane(glassPane);
    		if(frame instanceof JWindow) {
        		((JWindow) frame).setGlassPane(glassPane);

    		} else {
        		((JFrame) frame).setGlassPane(glassPane);

    		}
    		//instantiate the board settings class.
            boardSettings = new BoardSettings(boardGraphics,up,player1,player2);
            
            //Display the window.
            frame.pack();
            resizeToCenter(frame);
            frame.setVisible(true);
         

            //start the game
        	currentPlayer = whoStarts;
        	

            if(currentPlayer == PlayerType.COMP) {
            	result = game.doYourTurn(currentPlayer);
            	invert(currentPlayer);
            } else {
	        	boardGraphics.setCorkisEnabled(game.getMovableCorkis(currentPlayer),true);
            }
            
    }


	private void setEatingMoods() {
		
		
		JPanel eatMoodPanel = new JPanel();
		BoxLayout bl = new BoxLayout(eatMoodPanel,BoxLayout.Y_AXIS);
		eatMoodPanel.setLayout(bl);
		
		JRadioButton forceEat = new JRadioButton("ForceToEatInEveryCase");
		forceEat.addActionListener(this);
		JRadioButton noForcing = new JRadioButton("NoForcingNoEfita");
		noForcing.addActionListener(this);
		JRadioButton enableEfita = new JRadioButton("NoForcingButEfita");
		enableEfita.addActionListener(this);

		ButtonGroup group = new ButtonGroup();
		group.add(enableEfita);
		group.add(noForcing);
		group.add(forceEat);
		
		eatMoodPanel.add(noForcing);
		eatMoodPanel.add(forceEat);
		eatMoodPanel.add(enableEfita);
		
		JPanel eatPanel = new JPanel();
		bl = new BoxLayout(eatPanel,BoxLayout.Y_AXIS);
		eatPanel.setLayout(bl);
		
		teterByTeter = new JCheckBox("TeterByTeterFirst");
		teterByTeter.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {

					game.setTeterByTeter(true);
				} else {

					game.setTeterByTeter(false);

				}
				
				
			}
		});
		teterByTeter.setEnabled(false);
		
		eatMoodPanel.setBorder(BorderFactory.createEtchedBorder());
		
		eatPanel.add(eatMoodPanel);
		eatPanel.add(teterByTeter);
		
		eatPanel.setBorder(BorderFactory.createEtchedBorder());

		
		JOptionPane pane = new JOptionPane(eatPanel);
		JDialog dialog = pane.createDialog("Eating options");
		dialog.setIconImage(imageIcon.getImage());
		dialog.setVisible(true);
		
		
		
	}

	private void setBorder(JPanel panel) {
		Border compound,raisedbevel, loweredbevel, line;
		
        raisedbevel = BorderFactory.createRaisedBevelBorder();
        loweredbevel = BorderFactory.createLoweredBevelBorder();
		line = BorderFactory.createLineBorder(PositionGraphics.borderColor);
		 
        compound = BorderFactory.createCompoundBorder(
                                  raisedbevel, loweredbevel);
 
        compound = BorderFactory.createCompoundBorder(
                                  compound, line);

        panel.setBorder(compound);
		
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

	private void updatePopUp() {
		
		popUp = new JPopupMenu();
		
		for(DamaAction action:actions) {
			popUp.add(action);
			popUp.addSeparator();
		}
		
		popUp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
	}

	private JMenuBar getToolBar() {
		
		JMenuBar toolBar = new JMenuBar();
		toolBar.setBorder(BorderFactory.createEtchedBorder());
		JToolBar toolPane = new JToolBar();
		BoxLayout bl = new BoxLayout(toolPane,BoxLayout.X_AXIS);
		toolPane.setLayout(bl);
		toolPane.setFloatable(false);

		toolPane.addSeparator();
		toolPane.add(boardSettingsAction);
		toolPane.addSeparator();

		toolPane.add(restartAction);
		toolPane.addSeparator();

		toolPane.add(withdrawAction);
		toolPane.addSeparator();

		toolPane.add(drawAction);
		toolPane.addSeparator();

		toolPane.add(undoAction);
		toolPane.addSeparator();

		toolPane.add(rotateAction);

		toolBar.add(toolPane);
		
		return toolBar;
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
	



	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		

		if(command.equals("BoardSettings")) {
			JDialog dialog = new JDialog();
			dialog.setSize(800, 400);
			dialog.setIconImage(imageIcon.getImage());
			dialog.add(boardSettings.getSettingPane());
			resizeToCenter(dialog);
			dialog.setVisible(true);
		} else if(command.equals("Restart")) {
			frame.dispose();
			try {
				run();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		else if(command.equals("NewGame")) {
			newGame = true;
			frame.dispose();
		}else if(command.equals("ClearHistory")) {
			clearHistoryList();
		}
		else if(command.equals("ClickToSeeStatus")) {
				showStatus();
		} 
		else if(command.equals("History")) {
			showHistory();
		} else if(command.equals("Withdraw")) {
			result = Result.LOSE;
			handleFareWell(result);
		} else if(command.equals("Draw")) {
			result = Result.DRAW;
			handleFareWell(result);
		} else if(command.equals("UndoMove")) {
			
				boolean succeded = game.undoMove(currentPlayer);
				if(succeded){
					clickHandler.clearSelection();
					invert(currentPlayer);
					boardGraphics.disableALL();
					boardGraphics.setCorkisEnabled(game.getPlayer(currentPlayer).getMovableCorkis()
							, true);
				}
			
		}  else if(command.equals("Rotate")) {
			
			boardGraphics.rotate();
			handleTopPosUpdate();
			
		}else if(command.equals("Exit")) {
			System.exit(0);
		} else if(command.equals("ForceToEatInEveryCase")) {
			teterByTeter.setEnabled(true);
			game.setForceToEat(true);
			game.setNoForcing(false);
			game.setEnableEfita(false);
		} else if(command.equals("NoForcingNoEfita")) {
			teterByTeter.setEnabled(false);
			game.setForceToEat(false);
			game.setNoForcing(true);
			game.setEnableEfita(false);
			game.setTeterByTeter(false);
		} else if(command.equals("NoForcingButEfita")) {
			teterByTeter.setEnabled(false);
			game.setForceToEat(false);
			game.setNoForcing(false);
			game.setEnableEfita(true);
			game.setTeterByTeter(false);
		} 
			
		

	}

	private void showStatus() {
		//create two players
		Player p1 = game.getPlayer(currentPlayer);
		Player p2 = p1.getOpponent();
		
		//add labels to JPanel
		JPanel labelsPanel = new JPanel();
		BoxLayout bl = new BoxLayout(labelsPanel,BoxLayout.Y_AXIS);
		labelsPanel.setLayout(bl);
		
		
		//create labels
		String value = (Tank?"Tankegna":"Egregna");
		
		addStatusToPanel(labelsPanel, "GAME TYPE: ", value);
		addStatusToPanel(labelsPanel, "PLAYERS: ", p1.getName()+" Vs "+p2.getName());
		addStatusToPanel(labelsPanel,"CURRENT TURN: ", game.getPlayer(currentPlayer).getName());
		value = ((fakeUp == null)?game.getPlayer(up).getName():fakeUp.getName());
		addStatusToPanel(labelsPanel, "CURRENTLY TOP POSITION: ", value);
		addStatusToPanel(labelsPanel, p1.getName().toUpperCase()+" STATUS >>> ", 
				"numTeter: "+p1.getNumTeter()+"  numKing: "+p1.getNumKing());
		addStatusToPanel(labelsPanel, p2.getName().toUpperCase()+" STATUS >>> ", 
				"numTeter: "+p2.getNumTeter()+"  numKing: "+p2.getNumKing());

		Border inner = BorderFactory.createEmptyBorder(5,5,5,5);
		Border cmp = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),inner);
		
		labelsPanel.setBorder(cmp);
		
		//wrap the panel in JOptionPane
		JOptionPane statusPane = new JOptionPane(labelsPanel);
		JDialog dialog = statusPane.createDialog("Game Status");
		dialog.setIconImage(imageIcon.getImage());
		dialog.setVisible(true);



		
	}

	/**
	 * @param labelsPanel
	 * @param title
	 * @param value
	 */
	public void addStatusToPanel(JPanel labelsPanel, String title, String value) {
		JLabel titLabel;
		JPanel linePanel;
		titLabel = new JLabel(title);
		titLabel.setOpaque(true);
		titLabel.setBackground(Color.GREEN);
		JLabel gameTypeLabel = new JLabel(value);
		gameTypeLabel.setOpaque(true);
		gameTypeLabel.setBackground(Color.RED);
		linePanel = new JPanel(new FlowLayout());
		linePanel.add(titLabel);
		linePanel.add(gameTypeLabel);
		labelsPanel.add(linePanel);
	}

	/**
	 * 
	 */
	public void handleTopPosUpdate() {
		if(fakeUp == null) {
			if(game.getPlayer(currentPlayer).isOnTop()) {
				fakeUp = game.getPlayer(currentPlayer);
			} else {
				fakeUp = game.getPlayer(currentPlayer).getOpponent();
			}
			
		} 
		fakeUp = fakeUp.getOpponent();
			
	}




	/**
	 * @param result
	 */
	private void handleFareWell(Result result) {
		if(result == Result.DRAW ) {
			if(fakeInvert(currentPlayer) == PlayerType.COMP || currentPlayer == PlayerType.COMP) {
				JOptionPane.showMessageDialog(frame, "Oh no you can't draw against the computer");
				
			} else {
				int agreed = JOptionPane.showConfirmDialog(frame, game.getPlayer(fakeInvert(currentPlayer)).getName()+
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

	private PlayerType fakeInvert(PlayerType pt) {
		switch(pt) {
		case HUMN : return PlayerType.COMP;
		case COMP : return PlayerType.HUMN;
		case PLAYER1 : return PlayerType.PLAYER2;
		case PLAYER2 : return PlayerType.PLAYER1;
		}
		return null;
	}


	private void displayFareWell(Result result) {
		
		//disable clicking
		boardGraphics.disableALL();
		//write history
		writeHistory(result);
		
		//disable appropriate actions
		ArrayList<DamaAction> enablds = new ArrayList<DamaAction>();
		enablds.add(restartAction);
		enablds.add(newGameAction);
		enablds.add(historyAction);
		enablds.add(clearHistoryAction);
		enablds.add(exitAction);
		
		for(DamaAction action:actions) {
			if(!enablds.contains(action)) {
				action.setEnabled(false);
			}
		}
		
				
		String text =getfirstMessage(result);
		String firstMessage = text.endsWith(" YOU")?text.substring(0, text.length()-3)+"BUDDY":text;
		String secondMessage = getSecondMessage(result);
		
		Dimension d = boardGraphics.getSize();
		boolean needOffset = frame instanceof JWindow;
		MessagePanel message = new MessagePanel(firstMessage,secondMessage,Color.BLUE,Color.RED,d,needOffset);

		frame.add(message,0);
		frame.repaint();
		

		
	}

	/**
	 * write history to history.ser
	 * @param result 
	 * @param result
	 */
	private void writeHistory(Result result) {
		
		instantiateHistoryList();//incase

		
		Player p1 = game.getPlayer(currentPlayer);
		Player p2 = p1.getOpponent();
		
		//determine winner
		Player winner = null;
		if(result == Result.WIN) {
			winner = p1;
		} else if(result == Result.LOSE) {
			winner = p2;
		}
		//create new History
		History newHistory = new History();
		
		//get formatted date
		Date today;
		String dateOut;
		DateFormat dateFormatter;

		dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT,
							   Locale.US);
		today = new Date();
		dateOut = dateFormatter.format(today);
		
		//add date and the rest of the fields
		newHistory.setDate(dateOut.toString());
		newHistory.setGameType(Tank?"Tankegna":"Egregna");
		newHistory.setPlayers(p1.getName()+" Vs "+p2.getName());
		newHistory.setWinner((winner==null)?"NoBody":winner.getName());
		newHistory.setP1Status(p1.getName()+">>>"+p1.getNumTeter()+" Teters, "+
				p1.getNumKing()+" Kings");
		newHistory.setP2Status(p2.getName()+">>>"+p2.getNumTeter()+" Teters, "+
				p2.getNumKing()+" Kings");
		
		//history list
		HistoryHolder holder = new HistoryHolder();
		
		
		//create objectOutputStream
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream("history3.ser"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//handle the case of writing history for the first time
		//instantiate history list, incase
		historyList.add(newHistory);
		holder.setHistoryList(historyList);
		
		//write the new holder
		try {
			output.writeObject(holder);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
		
	}

	private String getfirstMessage(Result result) {

		switch (result) {
		case WIN: return "WOW "+game.getPlayer(currentPlayer).getName().toUpperCase();
		case LOSE: return "SORRY "+game.getPlayer(currentPlayer).getName().toUpperCase();
		case DRAW: return "NOBODY WON";

		}
		return null;
	}

	private String getSecondMessage(Result result) {
		
		switch (result) {
		
		case WIN: return "YOU WON";
		case LOSE: return "YOU LOST";
		case DRAW: return "THIS ROUND";

		}
		return null;
	}

	/**
	 * Show the damn history
	 */
	private void showHistory() {
		
	    instantiateHistoryList();

		if(historyList.size() < 1) {
			JOptionPane.showMessageDialog(frame, "No history made yet");

		} else {
			
				DefaultTableModel historyTable = new DefaultTableModel();
				
				historyTable.addColumn("Date");
				historyTable.addColumn("Players");
				historyTable.addColumn("GameType");
				historyTable.addColumn("Winner");
				historyTable.addColumn("player1Status");
				historyTable.addColumn("player2Status");
				
				History history = null;
				
				for(int i=0; i<historyList.size(); i++) {
					history = historyList.get(i);
					
					String[] historyLabel = {history.getDate(),
							history.getPlayers(), history.getGameType(),
							history.getWinner(),history.getP1Status(), history.getP2Status()};
					
					historyTable.addRow(historyLabel);

				}
				
				JTable table = new JTable(historyTable);
				
				table.getTableHeader().setOpaque(true);
				table.getTableHeader().setBackground(Color.RED);
				

				TableColumn column = null;
				int width[] = {100,120,90,90,200,200};
				for (int i = 0; i < 6; i++) {
				    column = table.getColumnModel().getColumn(i);
				        column.setPreferredWidth(width[i]); //third column is bigger
				  
				}
							
				table.setBackground(Color.CYAN);
				table.setDragEnabled(false);
				table.setEnabled(false);
				table.setSize(800, 300);

				JPanel historyPanel = new JPanel(new BorderLayout());
				historyPanel.add(new JScrollPane(table),BorderLayout.CENTER);
				JOptionPane pane = new JOptionPane(historyPanel);
				JDialog dialog = pane.createDialog("History");
				dialog.setSize(800, 300);
				dialog.setIconImage(imageIcon.getImage());
				JDialog historyDialog = dialog;
				resizeToCenter(historyDialog);
				historyDialog.setVisible(true);

			}
			
		
			
	}
	/**
	 * 
	 */
	public void clearHistoryList() {
		
		//history list
		HistoryHolder holder = new HistoryHolder();
		
		
		//create objectOutputStream
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream("history3.ser"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		//write the new holder
		try {
			output.writeObject(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
		
		
	}
	/**
	 * 
	 */
	public void instantiateHistoryList() {
		
		historyList = new ArrayList<History>();//in case instantiate
		
		//create objectInputStream
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new FileInputStream("history3.ser"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HistoryHolder holder = null;
		
		if(input != null) {
			try {
				holder = (HistoryHolder)input.readObject();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			//get the list
			if(holder != null) {
				historyList = holder.getHistoryList();

			}
		}
		
		
		
	}

	
	class componentHandler extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {			
			boardGraphics.setSize(Math.max(boardGraphics.getWidth(), 
					boardGraphics.getHeight()), Math.max(boardGraphics.
							getWidth(), boardGraphics.getHeight()));	
			frame.pack();
			frame.repaint();
		}
	}
	
	class popupHandler extends MouseAdapter {
		public void mousePressed(MouseEvent event) {
			checkForTriggerEvent(event);
		}
		public void mouseReleased(MouseEvent event) {
			checkForTriggerEvent(event);
		}
		private void checkForTriggerEvent(MouseEvent event) {
			if(event.isPopupTrigger()) {
				
				if(!(frame instanceof JFrame && glassPane.isVisible())) {
					popUp.show(event.getComponent(),event.getX(),event.getY());

				}
					
			}
			
		}
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

	public void resizeToCenter() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x_coor = (int) ((d.getWidth()-boardGraphics.getWidth())/2.0);
		int y_coor = (int) ((d.getHeight()-boardGraphics.getHeight())/2.0);
		frame.setLocation(x_coor, y_coor);
		
	}
	
	public static void resizeToCenter(Container dialog) {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x_coor = (int) ((d.getWidth()-dialog.getWidth())/2.0);
		int y_coor = (int) ((d.getHeight()-dialog.getHeight())/2.0);
		dialog.setLocation(x_coor, y_coor);
		
	}

	class windowHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
		
	}
	
	//initiate actions
	public void initiateActions() {
		
		actions = new ArrayList<DamaAction>();
		
		exitAction = new DamaAction("Exit");
		actions.add(exitAction);
		
		statusAction = new DamaAction("ClickToSeeStatus");
		actions.add(statusAction);
		
		restartAction = new DamaAction("Restart");
		actions.add(restartAction);

		newGameAction = new DamaAction("NewGame");
		actions.add(newGameAction);

		boardSettingsAction = new DamaAction("BoardSettings");
		actions.add(boardSettingsAction);

		withdrawAction = new DamaAction("Withdraw");
		actions.add(withdrawAction);

		drawAction = new DamaAction("Draw");
		actions.add(drawAction);

		undoAction = new DamaAction("UndoMove");
		actions.add(undoAction);

		historyAction = new DamaAction("History");
		actions.add(historyAction);

		rotateAction = new DamaAction("Rotate");
		actions.add(rotateAction);
		
		clearHistoryAction = new DamaAction("ClearHistory");
		actions.add(clearHistoryAction);

	}
	/*
	 * Abstract actions
	 */
	class DamaAction extends AbstractAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DamaAction(String name) {
			putValue(Action.NAME, name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GameLoader.this.actionPerformed(e);
			
		}
	}
	
	class ClickHandler implements ActionListener {
		private ArrayList<PositionGraphics> selectedPosGraphs;
		private PositionGraphics posGraph;
		private PositionGraphics posGraphi;
		private boolean repeat;
		private ArrayList<Position> legalPositions;
		 
		public ClickHandler() {
			selectedPosGraphs = new ArrayList<PositionGraphics>();
		}

		public void actionPerformed(ActionEvent e) {
			 
					boardGraphics.disableALL();					
					posGraphi = (PositionGraphics) e.getSource();
					repeat = posGraph!=null && posGraph.getPos().equals(posGraphi.getPos());
					posGraph = posGraphi;
					
					clearSelection();
					
					posGraph.brighten(false);
					selectedPosGraphs.add(posGraph);
					
					if(!firstClickDone) {
						
						handleFirstClick();
			        

					} else {
						
						firstClickDone = false;

						if((repeat&&legalPositions.contains(posGraph.getPos())) ||
								!game.getPlayer(currentPlayer).getCorkiList().contains(posGraph.getPos().getCorki())) {
							
							handleSecondClick();
							
							//deselect destination once the move is performed.
							posGraph.brighten(true);
							selectedPosGraphs.remove(posGraph);
														

						} else {
							handleFirstClick();
							
						}
						
						
					}
			 }

		/**
		 * 
		 */
		public void clearSelection() {
			int size =  selectedPosGraphs.size();
			if(size >0) {
				for(int i = 0; i< size; i++) {
					selectedPosGraphs.get(i).brighten(true);
				}
			
				selectedPosGraphs.removeAll(selectedPosGraphs);

			}
		}


		/**
		 * 
		 */
		public void handleSecondClick() {
			secondPosGraph = posGraph;
			result = game.doYourTurn(currentPlayer,firstPosGraph,secondPosGraph);
			if(result == Result.WIN || result == Result.LOSE ||
					result == Result.DRAW) {
				PositionGraphics.superNeedRepaint = true;
				PositionGraphics.superParent = frame;
				
				handleFareWell(result);
			}
			
			if(result != Result.KEEP_TURN) {
				invert(currentPlayer);
				Player player = game.getPlayer(currentPlayer);

				if(player.isEfitaEnabled()) {
					if(!player.getOpponent().isJustAte()) {
						player.getMind().checkForEfita();

					}
				}

			}
			
			boardGraphics.setCorkisEnabled(game.getMovableCorkis(currentPlayer),true);

			
			if(currentPlayer == PlayerType.COMP) {	
				
				result = game.doYourTurn(currentPlayer);
				invert(currentPlayer);
				
			}
		}

		/**
		 * 
		 */
		public void handleFirstClick() {
			firstPosGraph = posGraph;
			firstClickDone = true;
			boardGraphics.setCorkisEnabled(game.getMovableCorkis(currentPlayer),true);
			legalPositions = game.getNextLegalPositions(currentPlayer,firstPosGraph);
			for(Position pos: legalPositions) {
				selectedPosGraphs.add(pos.getPosGraph());
			}
			boardGraphics.setEnabled(legalPositions,true,false);
		}
		 
	}
	/**
	 * @param newGame the newGame to set
	 */
	public void setNewGame(boolean newGame) {
		this.newGame = newGame;
	}
	
	


}
