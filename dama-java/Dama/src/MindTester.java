import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;

public class MindTester {

	private static JFrame dialog;
	private static LineDrawer.GlassPane glassPane;
	private static BoardGraphics boardGraphics;
	private static Player player1;
	private static Player player2;
	private static BasicArrowButton arbut;
	
	
	public MindTester() {
		
	}
	
	public ActionListener getSelectionHandler(ActionListener methodListener) {
		return new SelectionHandler((MethodEventHandler) methodListener);
	}
	
	private class SelectionHandler implements ActionListener {
		
		private MethodEventHandler methodListener;
		
		public SelectionHandler(MethodEventHandler methodListener) {
			this.methodListener = methodListener;
		}
		public void actionPerformed(ActionEvent e) {
			
			PositionGraphics posGraph = (PositionGraphics) e.getSource();
			methodListener.setCurrentPos(posGraph.getPos());
						
			int size =  methodListener.getSelectedPosGraphs().size();
			if(size >0) {
				for(int i = 0; i< size; i++) {
					methodListener.getSelectedPosGraphs().get(i).brighten(true);
				}
			
				methodListener.getSelectedPosGraphs().
				removeAll(methodListener.getSelectedPosGraphs());

			}
			
			posGraph.brighten(false);
			methodListener.getSelectedPosGraphs().add(posGraph);
		}
	}
	
	private static void createandShowGUI() {

		MindTester mindTester = new MindTester();
		
		
		//create board, players and board graphics
	
		Board board = new Board();
		
		
		int userInput = JOptionPane.showConfirmDialog(null, "Would you like to use saved setting?","Check",JOptionPane.YES_NO_OPTION);
		
		if(userInput == JOptionPane.YES_OPTION) {
			try {
				ObjectInputStream input = new ObjectInputStream(new FileInputStream("player1.ser"));
				try {
					Player playerA = (Player) input.readObject();
					player1 = new Player
					(playerA.isTank(),GameLoader.PlayerType.HUMN,"me",playerA.isOnTop(),board,playerA.isTypeA(),playerA.getCorkiList());

			
					
				} catch (ClassNotFoundException e1) {
					player1 = new Player
					(false,GameLoader.PlayerType.HUMN,"me",false,board,true,null);
			
					JOptionPane.showMessageDialog(null, "couldn't read player1");
					e1.printStackTrace();
				}
				
				input = new ObjectInputStream(new FileInputStream("player2.ser"));
				try {
					Player playerB = (Player) input.readObject();
					player2 = new Player
					(playerB.isTank(),GameLoader.PlayerType.HUMN,"you",playerB.isOnTop(),board,playerB.isTypeA(),playerB.getCorkiList());
			
			
				} catch (ClassNotFoundException e1) {
					
					player2 = new Player
					(false,GameLoader.PlayerType.HUMN,"you",true,board,false,null);
			
			
					JOptionPane.showMessageDialog(null, "couldn't read player2");
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "File not Found while reading");
				e1.printStackTrace();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "IOexception while reading");
				e1.printStackTrace();
			}
		} else {
			player1 = new Player
			(false,GameLoader.PlayerType.HUMN,"me",false,board,true,null);
			player2 = new Player
			(false,GameLoader.PlayerType.HUMN,"you",true,board,false,null);
	
	
	
		}
		
		//opponent
		player1.setOpponent(player2);
		player2.setOpponent(player1);
		
		JButton gameType = new JButton("GT: "+(player1.isTank()?"Tankegna":"Egregna"));
		//set up the dama board
		dialog = new JFrame();
		
        JFrame.setDefaultLookAndFeelDecorated(true);


        ActionListener methodListener = mindTester.getMethodListener(player1, player2, gameType);
		boardGraphics = new BoardGraphics(board, mindTester.getSelectionHandler(methodListener),null);
		boardGraphics.enableALL();

		//set up the JCombobox
		final String[] methodList = 
		{ "ChooseMethod","setAsOrdinary","setAsKing","setAsFirstPos",
		"setAsSecondPos","deleteCorki","addTeter","addKing","doYourTurn","getAllLandings",
		"getNextAdjacents","getNextLegalsSequentially",
		"TopRight","TopLeft","BottomRight","BottomLeft","getTopPossLegals",
		"getBottomPossLegals","getAllPossLegals","getPossibleLegals","getSequencesRecursively","getMovableCorkis"
		,"EatingLandPos","drawTree","getEfita",
		"getNextTankingLegals","getMiddlePoses","getMaBackSeriously","move",
		"eat","getIndexFromUser","InvertGameType","ToogleGP"};
        final JComboBox methodBox = new JComboBox(methodList);
        methodBox.setSelectedIndex(0);
        
        methodBox.addActionListener(methodListener);


		glassPane = new LineDrawer().getGlassPane(boardGraphics);
		player1.getMind().setGlassPane(glassPane);
		player2.getMind().setGlassPane(glassPane);
		
		gameType.setOpaque(true);
		gameType.setBackground(Color.GREEN);
		gameType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				methodBox.setSelectedIndex(methodList.length-2);
			}
		});

		JButton saveBut = new JButton("SaveBoard");
		saveBut.setOpaque(true);
		saveBut.setBackground(Color.RED);
		saveBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
	
					ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("player1.ser"));
					output.writeObject(player1);
					output = new ObjectOutputStream(new FileOutputStream("player2.ser"));
					output.writeObject(player2);
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "FileNotFoundException while serializing");
					e1.printStackTrace();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Ioexception while serializing");
					e1.printStackTrace();
				}
			}
		});
		

		methodBox.setOpaque(true);
		methodBox.setBackground(Color.YELLOW);
		
		arbut = new BasicArrowButton(SwingUtilities.EAST);
		arbut.setOpaque(true);
		arbut.setBackground(Color.GREEN);
		
		glassPane.next = arbut;
		
		arbut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showConfirmDialog(arbut, "Next","Hiii", JOptionPane.OK_OPTION);
			}
		});
		
		BasicArrowButton toggle = new BasicArrowButton(SwingUtilities.WEST);
		toggle.setOpaque(true);
		toggle.setBackground(Color.RED);

		
		toggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				glassPane.setVisible(!glassPane.isVisible());
				if(!glassPane.isVisible())
					glassPane.clearAll();
			}
		});
		

				
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(toggle);
		toolBar.add(gameType);
		toolBar.add(methodBox);
		toolBar.add(saveBut);
		toolBar.add(arbut);
		
		//test some posGraphics operations
		boardGraphics.getAllPosGraphics()[0][1].setText("2");
		boardGraphics.getAllPosGraphics()[0][1].setDrawText(true);
		boardGraphics.getAllPosGraphics()[0][1].setCircled(true);

		
		dialog.add(boardGraphics,BorderLayout.CENTER);
		dialog.add(toolBar,BorderLayout.NORTH);
		dialog.setGlassPane(glassPane);
		dialog.getGlassPane().setVisible(true);
		dialog.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				JFrame frame = (JFrame) e.getSource();
				
				boardGraphics.setSize(Math.min(boardGraphics.getWidth(), 
						boardGraphics.getHeight()), Math.min(boardGraphics.
								getWidth(), boardGraphics.getHeight()));	
				frame.pack();
			}
		});
		
		dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		dialog.pack();
		GameLoader.resizeToCenter(dialog);


		dialog.setVisible(true);
	

	}
	
	public ActionListener getMethodListener(Player player1, Player player2, JButton gameType) {
		return new MethodEventHandler(player1,player2,gameType);
	}

	private class MethodEventHandler implements ActionListener {

		private Position currentPos;
		private Position firstPos;
		private Position secondPos;
		private ArrayList<PositionGraphics> selectedPosGraphs;
		
		private Player player1;
		private Player player2;
		private JButton gameTypeLabel;
		private JComboBox cb;
		
		public MethodEventHandler(Player player1, Player player2, JButton gameType) {
			
			this.currentPos = null;
			this.firstPos = null;
			this.secondPos = null;
			this.selectedPosGraphs = new ArrayList<PositionGraphics>();
			
			this.player1 = player1;
			this.player2 = player2;
			this.gameTypeLabel = gameType;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
				cb = (JComboBox)e.getSource();
			} catch (ClassCastException e1) {

				JOptionPane.showMessageDialog(null, "Class cast");
				return;
				
			}
			
	        String method = (String)cb.getSelectedItem();
	        
	        if(method.equalsIgnoreCase("setAsKing")) {
	        	currentPos.getCorki().setAsKing(true);
	        	currentPos.getPosGraph().repaint();
	        } else if(method.equalsIgnoreCase("setAsOrdinary")) {
	        	currentPos.getCorki().setAsKing(false);
	        	currentPos.getPosGraph().repaint();
	        }  else if(method.equalsIgnoreCase("deleteCorki")) {
	        	getPlayerMatch
    			(currentPos.getCorki(), player1, player2).getCorkiList().remove
    			(currentPos.getCorki());
	        	currentPos.setCorki(null);
	        	currentPos.getPosGraph().repaint();
	        }  else if(method.equalsIgnoreCase("addTeter")) {
	        	Player p = getPlayerMatch
    			(firstPos.getCorki(), player1, player2);
	        	if(currentPos.hasCorki()) {
	        		currentPos.setCorki(null);
	        	}
	        	Corki cork = new Corki(p.isTypeA(),false,currentPos);
	        	p.getCorkiList().add(cork);
	        	currentPos.getPosGraph().repaint();
	        }   else if(method.equalsIgnoreCase("addKing")) {
	        	Player p = getPlayerMatch
    			(firstPos.getCorki(), player1, player2);
	        	if(currentPos.hasCorki()) {
	        		currentPos.setCorki(null);
	        	}
	        	Corki cork = new Corki(p.isTypeA(),true,currentPos);
	        	p.getCorkiList().add(cork);
	        	currentPos.getPosGraph().repaint();
	        } else if(method.equalsIgnoreCase("setAsFirstPos")) {
	        	firstPos = currentPos;
	        } else if(method.equalsIgnoreCase("setAsSecondPos")) {
	        	secondPos = currentPos;
	        } else if(method.equalsIgnoreCase("getTopPossLegals")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	ArrayList<Position> topLegals = player1.getMind().getTopLegalPositions(currentPos, Integer.parseInt(level));
	        	for(int i=0; i<topLegals.size(); i++) {
	        		topLegals.get(i).getPosGraph().brighten(false);
	        		selectedPosGraphs.add(topLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getBottomPossLegals")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	ArrayList<Position> botLegals = player2.getMind().getBottomLegalPositions(currentPos, Integer.parseInt(level));
	        	for(int i=0; i<botLegals.size(); i++) {
	        		botLegals.get(i).getPosGraph().brighten(false);
	        		selectedPosGraphs.add(botLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getAllPossLegals")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	ArrayList<Position> allLegals = player1.getMind().getAllLegalPositions(currentPos, Integer.parseInt(level));
	        	for(int i=0; i<allLegals.size(); i++) {
	        		allLegals.get(i).getPosGraph().brighten(false);
	        		selectedPosGraphs.add(allLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getPossibleLegals")) {
	        	ArrayList<Position> possLegals = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getNextPossiblePositions
	        			(currentPos.getCorki());
	        	for(int i=0; i<possLegals.size(); i++) {
	        		possLegals.get(i).getPosGraph().brighten(false);
	        		selectedPosGraphs.add(possLegals.get(i).getPosGraph());
	        	}
	        } 
	        else if(method.equalsIgnoreCase("getEfita")) {
	        	ArrayList<Corki> adjacents = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getEfita();
	        	for(int i=0; i<adjacents.size(); i++) {
	        		adjacents.get(i).getPos().getPosGraph().brighten(false);
	        		selectedPosGraphs.add(adjacents.get(i).getPos().getPosGraph());
	        	}
	        }
	        else if(method.equalsIgnoreCase("getNextAdjacents")) {
	        	ArrayList<Position> adjacents = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getNextAdjacents(currentPos.getCorki());
	        	for(int i=0; i<adjacents.size(); i++) {
	        		adjacents.get(i).getPosGraph().brighten(false);
	        		selectedPosGraphs.add(adjacents.get(i).getPosGraph());
	        	}
	        }
	        if(method.equalsIgnoreCase("drawTree")) {
	        	getPlayerMatch
    			(currentPos.getCorki(), player1, player2)
    			.getMind().drawTreeBasedOnLegalPos(currentPos.getCorki());
	        } 
	        else if(method.equalsIgnoreCase("getNextLegalsSequentially")) {
	        	
	        	long start = System.nanoTime();
	        	ArrayList<Position> nextLegals = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getNextLegalsSequentially(currentPos.getCorki());
	        	long end = System.nanoTime();
	        	
	        	JOptionPane.showMessageDialog(null, "Time elapsed: "+(end-start));
	        	
	        	for(int i=0; i<nextLegals.size(); i++) {
	        		nextLegals.get(i).getPosGraph().brighten(false);
	        		selectedPosGraphs.add(nextLegals.get(i).getPosGraph());
	        	}
	        			        			        	
	        }

	        else if(method.equalsIgnoreCase("getMovableCorkis")) {
	        	ArrayList<Corki> movables = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getMovableCorkis
	        			();
	        	for(int i=0; i<movables.size(); i++) {
	        		movables.get(i).getPos().getPosGraph().brighten(false);
	        		selectedPosGraphs.add(movables.get(i).getPos().getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("move")) {
	        	player1.getMind().move(firstPos, secondPos,null);
	        } else if(method.equalsIgnoreCase("eat")) {
	        	player2.getMind().eat(firstPos, secondPos,null);

	        }  else if(method.equalsIgnoreCase("InvertGameType")) {
	        	player1.setTank(!player1.isTank());
	        	player2.setTank(!player2.isTank());
	    		gameTypeLabel.setText("GT: "+(player1.isTank()?"Tankegna":"Egregna"));
	    		gameTypeLabel.repaint();		        	
	        }  else if(method.equalsIgnoreCase("EatingLandPos")) {
	        	PositionGraphics posGraphi = player1.getMind().getEatingLandPos(firstPos, secondPos).getPosGraph();
	        	if(posGraphi != null) {
	        		
	        	posGraphi.brighten(false);
        		selectedPosGraphs.add(posGraphi);
	        	}
	        }  else if(method.equalsIgnoreCase("TopRight")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	Position pos = player2.getMind().getTopLegal(currentPos, true,Integer.parseInt(level));
	        	pos.getPosGraph().brighten(false);
	        	selectedPosGraphs.add(pos.getPosGraph());
	        	
	        } else if(method.equalsIgnoreCase("TopLeft")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	Position pos = player2.getMind().getTopLegal(currentPos, false,Integer.parseInt(level));
	        	pos.getPosGraph().brighten(false);
	        	selectedPosGraphs.add(pos.getPosGraph());
	        	
	        }else if(method.equalsIgnoreCase("BottomRight")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	Position pos = player2.getMind().getBotLegal(currentPos, true,Integer.parseInt(level));
	        	pos.getPosGraph().brighten(false);
	        	selectedPosGraphs.add(pos.getPosGraph());
	        	
	        }else if(method.equalsIgnoreCase("BottomLeft")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	Position pos = player2.getMind().getBotLegal(currentPos, false,Integer.parseInt(level));
	        	pos.getPosGraph().brighten(false);
	        	selectedPosGraphs.add(pos.getPosGraph());
	        	
	        } else if(method.equalsIgnoreCase("getNextTankingLegals")) {
	        	ArrayList<Position> nextLegals = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getNextTanKingLegals
	        			(currentPos.getCorki(),null);
	        	for(int i=0; i<nextLegals.size(); i++) {
	        		nextLegals.get(i).getPosGraph().brighten(false);
	        		selectedPosGraphs.add(nextLegals.get(i).getPosGraph());
	        	}
	        }
            else if(method.equalsIgnoreCase("doYourTurn")) {
	        	
	        	glassPane.setVisible(false);
	        	Mind mind = getPlayerMatch
    			(firstPos.getCorki(), player1, player2)
    			.getMind();
	        	mind.doYourTurn(firstPos, secondPos);
	        	
	        }
	        else if(method.equalsIgnoreCase("getIndexFromUser")) {
	        	
	        	glassPane.setVisible(false);
	        	Mind mind = getPlayerMatch
    			(firstPos.getCorki(), player1, player2)
    			.getMind();
	        	ArrayList<ArrayList<Position>> big = mind.getAllLandingPoses(firstPos.getCorki(),secondPos);
	        	mind.getIndexFromUser(big);
	        	
	        }
	        else if(method.equalsIgnoreCase("getAllLandings")) {
	        	long start = System.nanoTime();
	        	ArrayList<ArrayList<Position>> big = getPlayerMatch
	        			(firstPos.getCorki(), player1, player2)
	        			.getMind().getAllLandingPoses(firstPos.getCorki(),secondPos);
    	        long end = System.nanoTime();
    	        JOptionPane.showMessageDialog(null, "Time taken: "+(end-start));	
	        	int choice = 1;
	        	if(big.size() >0) {
	        		String input = JOptionPane.showInputDialog("you got "+big.size()+" enter the path you wanna see.");
		        	choice = Integer.parseInt(input);
		        	
		        	glassPane.clearAll();
		        	
		        	for(int i=0; i<big.get(choice-1).size(); i++) {
		        		if(!glassPane.isVisible()) {
		        			if(i!=big.get(choice-1).size()-1) {
		        				glassPane.addPoints(big.get(choice-1).get(i)
		        				, big.get(choice-1).get(i+1));
		        			}
			        		
			        	}
		        		big.get(choice-1).get(i).getPosGraph().brighten(false);
		        		selectedPosGraphs.add(big.get(choice-1).get(i).getPosGraph());
		        	}
	        	} 
	        	
	        	
	        }
	        else if(method.equalsIgnoreCase("getMiddlePoses")) {
	        	ArrayList<Position> nextLegals = getPlayerMatch
	        			(firstPos.getCorki(), player1, player2)
	        			.getMind().getMiddlePoses
	        			(firstPos,secondPos);
	        	for(int i=0; i<nextLegals.size(); i++) {
	        		nextLegals.get(i).getPosGraph().brighten(false);
	        		selectedPosGraphs.add(nextLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getMaBackSeriously")) {
	        	ArrayList<Position> nextLegals = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getMaBackSeriously
	        			(firstPos,secondPos);
	        	for(int i=0; i<nextLegals.size(); i++) {
	        		nextLegals.get(i).getPosGraph().brighten(false);
	        		selectedPosGraphs.add(nextLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getTankEatables")) {
	        	ArrayList<ArrayList<Position>> eatables = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getEatables
	        			(currentPos.getCorki());
	        	for(int i=0; i<eatables.size(); i++) {
	        		for(int j=0; j<eatables.get(i).size(); j++) {
	        				eatables.get(i).get(j).getPosGraph().brighten(false);
			        		selectedPosGraphs.add(eatables.get(i).get(j).getPosGraph());
	        			
		        	}
	        	}
	        } else if(method.equalsIgnoreCase("ToogleGP")) {
				dialog.getGlassPane().setVisible(!dialog.getGlassPane().isVisible());
				if(!dialog.getGlassPane().isVisible()) {
					((LineDrawer.GlassPane) dialog.getGlassPane()).clearAll();
				}

	        }
    
	        cb.setSelectedIndex(0);

		}
		
			private Player getPlayerMatch(Corki corki, Player player1, Player player2) {
				return (corki.isTypeA() == player1.isTypeA() ? player1: player2);
				
			}


			/**
			 * @param currentPos the currentPos to set
			 */
			public void setCurrentPos(Position currentPos) {
				this.currentPos = currentPos;
			}



			/**
			 * @return the selectedPosGraphs
			 */
			public ArrayList<PositionGraphics> getSelectedPosGraphs() {
				return selectedPosGraphs;
			}


	}
	//for testing purpose
	public static void main(String[] args     	) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createandShowGUI();
			}
		});

		
	}

}
