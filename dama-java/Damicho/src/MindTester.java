import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MindTester {

	public MindTester() {
		
	}
	
	public MouseAdapter getSelectionHandler(ActionListener methodListener) {
		return new SelectionHandler((MethodEventHandler) methodListener);
	}
	
	private class SelectionHandler extends MouseAdapter {
		
		private MethodEventHandler methodListener;
		
		public SelectionHandler(MethodEventHandler methodListener) {
			this.methodListener = methodListener;
		}
		public void mouseClicked(MouseEvent e) {
			
			PositionGraphics posGraph = (PositionGraphics) e.getSource();
			methodListener.setCurrentPos(posGraph.getPos());
			posGraph.brighten(true);
						
			int size =  methodListener.getSelectedPosGraphs().size();
			if(size >0) {
				for(int i = 0; i< size; i++) {
					methodListener.getSelectedPosGraphs().get(i).brighten(false);
				}
			
				methodListener.getSelectedPosGraphs().
				removeAll(methodListener.getSelectedPosGraphs());

			}
			
			
			methodListener.getSelectedPosGraphs().add(posGraph);
		}
	}
	
	private static void createandShowGUI() {
		
		MindTester mindTester = new MindTester();
		
		
		//create board, players and board graphics
	
		Board board = new Board();
		
		
		Player player1 = new Player
				(false,GameLoader.PlayerType.HUMN,"me",false,board,true);
		Player player2 = new Player
				(false,GameLoader.PlayerType.HUMN,"you",true,board,false);
		
		
		JLabel gameType = new JLabel("Game Type: "+(player1.isTank?"Tankegna":"Egregna"));

		//set up the dama board
		JDialog dialog = new JDialog();
		int width = (int)(Toolkit.getDefaultToolkit().getScreenSize().height*0.8);
		dialog.setSize(width,width );
		GameLoader.resizeToCenter(dialog);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		//set up the JCombobox
		String[] methodList = { "ChooseMethod","setAsOrdinary","setAsKing","setAsFirstPos",
				"setAsSecondPos","deleteCorki","TopRight","TopLeft","BottomRight","BottomLeft","getTopPossLegals",
				"getBottomPossLegals","getAllPossLegals","getPossibleLegals",
				"getNextLegals","getEatableSequence","getSequencesRecursively",
				"getMovableCorkis","getTankEatables","getEatablesRecursively","EatingLandPos",
				"getUncomplexTakingLeg","getMaBack","getMaBackSeriously","move","eat","InvertGameType"};
        JComboBox methodBox = new JComboBox(methodList);
        methodBox.setSelectedIndex(0);
        
        ActionListener methodListener = mindTester.getMethodListener(player1, player2, gameType);
        methodBox.addActionListener(methodListener);

		BoardGraphics boardGraphics = new BoardGraphics(board, mindTester.getSelectionHandler(methodListener));

		dialog.add(boardGraphics,BorderLayout.CENTER);
		dialog.add(methodBox, BorderLayout.SOUTH);
		dialog.add(gameType,BorderLayout.NORTH);
		boardGraphics.enableALL();
		dialog.setVisible(true);


	}
	
	public ActionListener getMethodListener(Player player1, Player player2, JLabel gameType) {
		return new MethodEventHandler(player1,player2,gameType);
	}

	private class MethodEventHandler implements ActionListener {

		private Position currentPos;
		private Position firstPos;
		private Position secondPos;
		private ArrayList<PositionGraphics> selectedPosGraphs;
		
		private Player player1;
		private Player player2;
		private JLabel gameTypeLabel;
		
		public MethodEventHandler(Player player1, Player player2, JLabel gameType) {
			
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
			
			JComboBox cb = (JComboBox)e.getSource();
	        String method = (String)cb.getSelectedItem();
	        
	        if(method.equalsIgnoreCase("setAsKing")) {
	        	currentPos.getCorki().setAsKing(true);
	        	currentPos.getPosGraph().repaint();
	        } else if(method.equalsIgnoreCase("setAsOrdinary")) {
	        	currentPos.getCorki().setAsKing(false);
	        	currentPos.getPosGraph().repaint();
	        }  else if(method.equalsIgnoreCase("deleteCorki")) {
	        	getPlayerMatch
    			(currentPos.getCorki(), player1, player2).corkiList.remove
    			(currentPos.getCorki());
	        	currentPos.setCorki(null);
	        	currentPos.getPosGraph().repaint();
	        } else if(method.equalsIgnoreCase("setAsFirstPos")) {
	        	firstPos = currentPos;
	        } else if(method.equalsIgnoreCase("setAsSecondPos")) {
	        	secondPos = currentPos;
	        } else if(method.equalsIgnoreCase("getTopPossLegals")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	ArrayList<Position> topLegals = player1.getMind().getTopLegalPositions(currentPos, Integer.parseInt(level));
	        	for(int i=0; i<topLegals.size(); i++) {
	        		topLegals.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(topLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getBottomPossLegals")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	ArrayList<Position> botLegals = player2.getMind().getBottomLegalPositions(currentPos, Integer.parseInt(level));
	        	for(int i=0; i<botLegals.size(); i++) {
	        		botLegals.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(botLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getAllPossLegals")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	ArrayList<Position> allLegals = player1.getMind().getAllLegalPositions(currentPos, Integer.parseInt(level));
	        	for(int i=0; i<allLegals.size(); i++) {
	        		allLegals.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(allLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getPossibleLegals")) {
	        	ArrayList<Position> possLegals = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getNextPossiblePositions
	        			(currentPos.getCorki());
	        	for(int i=0; i<possLegals.size(); i++) {
	        		possLegals.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(possLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getNextLegals")) {
	        	ArrayList<Position> nextLegals = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getNextLegalPositions
	        			(currentPos.getPosGraph(),null,true);
	        	for(int i=0; i<nextLegals.size(); i++) {
	        		nextLegals.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(nextLegals.get(i).getPosGraph());
	        	}
	        			        			        	
	        } else if(method.equalsIgnoreCase("getMovableCorkis")) {
	        	ArrayList<Corki> movables = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getMovableCorkis
	        			();
	        	for(int i=0; i<movables.size(); i++) {
	        		movables.get(i).getPos().getPosGraph().brighten(true);
	        		selectedPosGraphs.add(movables.get(i).getPos().getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("move")) {
	        	player1.getMind().move(firstPos, secondPos);
	        } else if(method.equalsIgnoreCase("eat")) {
	        	player2.getMind().eat(firstPos, secondPos);

	        }  else if(method.equalsIgnoreCase("InvertGameType")) {
	        	player1.setTank(!player1.isTank);
	        	player2.setTank(!player2.isTank);
	    		gameTypeLabel.setText("Game Type: "+(player1.isTank?"Tankegna":"Egregna"));
	    		gameTypeLabel.repaint();		        	
	        }  else if(method.equalsIgnoreCase("EatingLandPos")) {
	        	PositionGraphics posGraphi = player1.getMind().getEatingLandPos(firstPos, secondPos).getPosGraph();
	        	if(posGraphi != null) {
	        		
	        	posGraphi.brighten(true);
        		selectedPosGraphs.add(posGraphi);
	        	}
	        }  else if(method.equalsIgnoreCase("TopRight")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	Position pos = player2.getMind().getTopLegal(currentPos, true,Integer.parseInt(level));
	        	pos.getPosGraph().brighten(true);
	        	selectedPosGraphs.add(pos.getPosGraph());
	        	
	        } else if(method.equalsIgnoreCase("TopLeft")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	Position pos = player2.getMind().getTopLegal(currentPos, false,Integer.parseInt(level));
	        	pos.getPosGraph().brighten(true);
	        	selectedPosGraphs.add(pos.getPosGraph());
	        	
	        }else if(method.equalsIgnoreCase("BottomRight")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	Position pos = player2.getMind().getBotLegal(currentPos, true,Integer.parseInt(level));
	        	pos.getPosGraph().brighten(true);
	        	selectedPosGraphs.add(pos.getPosGraph());
	        	
	        }else if(method.equalsIgnoreCase("BottomLeft")) {
	        	String level = JOptionPane.showInputDialog("Enter valid level:");
	        	Position pos = player2.getMind().getBotLegal(currentPos, false,Integer.parseInt(level));
	        	pos.getPosGraph().brighten(true);
	        	selectedPosGraphs.add(pos.getPosGraph());
	        	
	        } else if(method.equalsIgnoreCase("getUncomplexTakingLeg")) {
	        	ArrayList<Position> nextLegals = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getNextTanKingLegals
	        			(currentPos.getCorki(),null);
	        	for(int i=0; i<nextLegals.size(); i++) {
	        		nextLegals.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(nextLegals.get(i).getPosGraph());
	        	}
	        }  else if(method.equalsIgnoreCase("getEatableSequence")) {
	        	ArrayList<Position> eatables = getPlayerMatch
	        			(firstPos.getCorki(), player1, player2)
	        			.getMind().getEatableSequence
	        			(firstPos,secondPos);
	        	for(int i=0; i<eatables.size(); i++) {
	        		eatables.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(eatables.get(i).getPosGraph());
	        	}
	        }  else if(method.equalsIgnoreCase("getSequencesRecursively")) {
	        	ArrayList<ArrayList<Position>> big = getPlayerMatch
	        			(firstPos.getCorki(), player1, player2)
	        			.getMind().getEatbleSequence
	        			(firstPos,secondPos);
	        	int choice = 1;
	        	if(big.size() >1) {
	        		String input = JOptionPane.showInputDialog("you got "+big.size()+" enter the path you wanna see.");
		        	choice = Integer.parseInt(input);
	        	} 
	        	
	        	for(int i=0; i<big.get(choice-1).size(); i++) {
	        		big.get(choice-1).get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(big.get(choice-1).get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getMaBack")) {
	        	ArrayList<Position> nextLegals = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getMaBack
	        			(firstPos,secondPos);
	        	for(int i=0; i<nextLegals.size(); i++) {
	        		nextLegals.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(nextLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getMaBackSeriously")) {
	        	ArrayList<Position> nextLegals = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getMaBackSeriously
	        			(firstPos,secondPos);
	        	for(int i=0; i<nextLegals.size(); i++) {
	        		nextLegals.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(nextLegals.get(i).getPosGraph());
	        	}
	        } else if(method.equalsIgnoreCase("getTankEatables")) {
	        	ArrayList<ArrayList<Position>> eatables = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getEatables
	        			(currentPos.getCorki());
	        	for(int i=0; i<eatables.size(); i++) {
	        		for(int j=0; j<eatables.get(i).size(); j++) {
	        				eatables.get(i).get(j).getPosGraph().brighten(true);
			        		selectedPosGraphs.add(eatables.get(i).get(j).getPosGraph());
	        			
		        	}
	        	}
	        } else if(method.equalsIgnoreCase("getEatablesRecursively")) {
	        	ArrayList<Position> eatables = getPlayerMatch
	        			(currentPos.getCorki(), player1, player2)
	        			.getMind().getTankEatablesRecursively
	        			(currentPos.getCorki());
	        	for(int i=0; i<eatables.size(); i++) {
	        		eatables.get(i).getPosGraph().brighten(true);
	        		selectedPosGraphs.add(eatables.get(i).getPosGraph());
	        	}
	        }
	        cb.setSelectedIndex(0);

		}
		
			private Player getPlayerMatch(Corki corki, Player player1, Player player2) {
				return (corki.isTypeA() == player1.isTypeA ? player1: player2);
				
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
