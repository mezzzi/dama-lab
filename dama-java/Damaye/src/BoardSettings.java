


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;



/**
 * @author mzm002
 *
 */
public class BoardSettings implements ActionListener, ChangeListener {


	protected enum Parameters {ILLEGAL_POS_COLOR, LEGAL_POS_COLOR, TYPE_A_CORKI_COLOR, TYPE_B_CORKI_COLOR, 
		STAR_COLOR, STROKE_COLOR,BOARDER_COLOR, DIAM_RATIO, INNER_DIAM_RATIO, STROKE_WIDTH, STAR_FILLED, SQUARE_RAISED, ALLOW_STROKE}

	protected Color paramColor = Color.red;
	protected Parameters param = Parameters.ILLEGAL_POS_COLOR;
	protected BoardGraphics boardGraphics;
	
	
	private String[] stringParams = {"Illegal Position", "Legal Position", "Computer's Corki", "Human's Corki", "Star", "Stroke","Border Color",
			 "Size of Corki", "Size of the star", "The width of the stroke","fill star", "raise square", "allow stroke"};
	private double diamRatio;
	private double innerDiamRatio;
	private double strokeWidth;
	private PositionGraphics fakePosGraphics2;
	private PositionGraphics fakePosGraphics1;
	
	//the following 4 fields are associated with change Listener
	private JSpinner strokeWidthSpinner;
	private JSpinner innerDiamWidthSpinner;
	private JSpinner diamWidthSpinner;
	private JColorChooser colorChooser;
	private JCheckBox allowStroke;
	private JCheckBox isSquareRaised;
	private JCheckBox isStarFilled;
	private GameLoader.PlayerType up;
	private String player2;
	private String player1;
	

		/**
	 * @param gameLoader 
		 * @param posGraphics
	 */
	public BoardSettings(BoardGraphics boardGraphics, GameLoader.PlayerType up, String player1, String player2) {
		this.boardGraphics = boardGraphics;
		this.fakePosGraphics1 = new PositionGraphics();
		this.fakePosGraphics2 = new PositionGraphics();
		this.up = up;
		this.player1 = player1;
		this.player2 = player2;
		
	}


	/**
	 * writes default params to a file default.txt
	 */
 private void saveDefaultParams() {
	 
		Color[] colorArray = new Color[7];

		colorArray[0] = PositionGraphics.illegalColor;
		colorArray[1] = PositionGraphics.legalColor;
		colorArray[2] = PositionGraphics.typeAColor;
		colorArray[3] = PositionGraphics.typeBColor;
		colorArray[4] = PositionGraphics.starColor;
		colorArray[5] = PositionGraphics.strokeColor;
		colorArray[6] = PositionGraphics.borderColor;
		
	 FileWriter writer = null;
	 
	 try {
		 writer = new FileWriter("default.txt");
		 for(int i=0; i<colorArray.length; i++) {
			 try {
					writer.write("["+" "+colorArray[i].getRed()+" "+colorArray[i].getGreen()+" "+colorArray[i].getBlue()+" "+colorArray[i].getAlpha()+" ] ");

			 } catch (Exception e) {
				 writer.write("[ 0 0 0 0 ] "); //just write all zero to avoid mismatch.
				 out("error at index "+i);
				 e.printStackTrace();
			 }
		 }
		 writer.write(""+PositionGraphics.diam_Ratio);
		 writer.write(" "+PositionGraphics.innerDiam_Ratio);
		 writer.write(" "+PositionGraphics.stroke_Width);
		 
		 //1 for true and 0 for false;
		 writer.write(" "+(PositionGraphics.isStarFilled ? 1 : 0));
		 writer.write(" "+(PositionGraphics.isSquareRaised ? 1 : 0));
		 writer.write(" "+(PositionGraphics.allowStroke ? 1 : 0));
		 writer.close();
		 


	} catch (IOException e) {
		out("File not written properly");
		e.printStackTrace();
	} 
	
 }

	/** Creates the GUI shown inside the frame's content pane. */
	 public JTabbedPane  getSettingPane() {
	    		   
	        //Create the components.
	        JPanel colorPanel = getColorPanel();
	        JPanel inputPanel= getInputPanel();
	        
	     

	        //Lay them out.
	        Border padding = BorderFactory.createEmptyBorder(20,20,5,20);
	        colorPanel.setBorder(padding);
	        inputPanel.setBorder(padding);

	        JTabbedPane tabbedPane = new JTabbedPane();
	        
	        tabbedPane.addTab("Numeric Parameters", null,
	        		inputPanel,
	                          "Click to edit numeric parameters"); 
	        tabbedPane.addTab("Color Parameters", null,
	        		colorPanel,
	                          "Click to change the color of a given paramter"); 
	       
	        return tabbedPane;
	       
	  
	 }

	    private JPanel getInputPanel() {
	    	JPanel pane = new JPanel();
	    	pane.setLayout(new GridLayout(1,2));
	    	
	    	
	    	JPanel textPanel = new JPanel();
	    	textPanel.setLayout(new GridLayout(3,1));
	    	
	    
	    	
	    	SpinnerModel diamWidthModel = new SpinnerNumberModel(0.8,0.2,1, 0.1); 
	    	diamWidthSpinner = addLabeledSpinner(textPanel,"Size of the Corki",diamWidthModel);
	    	diamWidthSpinner.addChangeListener(this);
	    	
	    	SpinnerModel innerDiamWidthModel = new SpinnerNumberModel(0.8,0.2,1, 0.1); 
	    	innerDiamWidthSpinner = addLabeledSpinner(textPanel,"Size of the star",innerDiamWidthModel);
	    	innerDiamWidthSpinner.addChangeListener(this);
	    
	    	
	    	SpinnerModel strokeWidthModel = new SpinnerNumberModel(2.0,0,10.0, 1.0); 
	    	strokeWidthSpinner = addLabeledSpinner(textPanel,"StrokeWidth",strokeWidthModel);
	    	strokeWidthSpinner.addChangeListener(this);
	    	
	    	
	    	
	    	textPanel.setBorder(BorderFactory.createTitledBorder("Choose values"));
	    	
	    	JPanel booleanPanel = new JPanel();
	    	booleanPanel.setLayout(new GridLayout(3,1));
	    	
	    	isStarFilled = new JCheckBox("Fill Star", PositionGraphics.isStarFilled);
	    	isStarFilled.addActionListener(this);
	    	isStarFilled.setActionCommand("fill star");
	    	booleanPanel.add(isStarFilled);
	    	
	    	isSquareRaised = new JCheckBox("Raise square",PositionGraphics.isSquareRaised);
	    	isSquareRaised.addActionListener(this);
	    	isSquareRaised.setActionCommand("raise square");
	    	booleanPanel.add(isSquareRaised);
	    	
	    	allowStroke = new JCheckBox("Allow Stroke",PositionGraphics.allowStroke);
	    	allowStroke.addActionListener(this);
	    	allowStroke.setActionCommand("allow stroke");
	    	booleanPanel.add(allowStroke);
	    	
	    	booleanPanel.setBorder(BorderFactory.createTitledBorder("Check values"));

	    	JPanel mergedPane = new JPanel();
	    	mergedPane.setLayout(new GridLayout(2,1));
	    	mergedPane.add(textPanel);
	    	mergedPane.add(booleanPanel);
	    	
	    	
	    	pane.add(getPreviewPanel(fakePosGraphics2));
	    	pane.add(mergedPane);
	    	     
	    	
	    	return pane;
		}


	    private  JSpinner addLabeledSpinner(Container c,String label,SpinnerModel model) {
	    	
	    	JPanel flowPanel = new JPanel();
	    	
	    	flowPanel.setLayout(new FlowLayout());
	    	JLabel l = new JLabel(label);
	    	flowPanel.add(l);

	    	JSpinner spinner = new JSpinner(model);
	    	l.setLabelFor(spinner);
	    	flowPanel.add(spinner);
	    	
	    	c.add(flowPanel);

	    	return spinner;
	    }

	    /**
	     * 
	     * @return the color chooser panel
	     */
		private JPanel getColorPanel() {
			
			JPanel pane = new JPanel();
			pane.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			String[] colorStrings = { "Illegal Position", "Legal Position", "Computer's Corki", "Human's Corki", "Star", "Stroke", "Border Color"};
			 
	
			
	        //create and add ComboBox to pane
	        JComboBox colorList = new JComboBox();
	        colorList.addItem(colorStrings[0]);
	        colorList.addItem(colorStrings[1]);
	        colorList.addItem(getUpPlayer());
	        colorList.addItem(getDownPlayer());
	        colorList.addItem(colorStrings[4]);
	        colorList.addItem(colorStrings[5]);
	        colorList.addItem(colorStrings[6]);


	        colorList.setSelectedIndex(0);
	        colorList.addActionListener(this);
	        colorList.setActionCommand("colorCombBox");
	        colorList.setBorder(BorderFactory.createTitledBorder("Choose Parameter"));
	        
	        //change the preview pane
	        JPanel previewPane = getPreviewPanel(fakePosGraphics1);
	       
	        //add the color chooser to pane
	        colorChooser = new JColorChooser();
	        colorChooser.setPreviewPanel(new JPanel());
	        colorChooser.getSelectionModel().addChangeListener(this);
	        colorChooser.setBorder(BorderFactory.createTitledBorder("Choose color"));
	        
	        JPanel savePanel = new JPanel(new FlowLayout());
	        JButton saveButton = new JButton("Save As Default");
	        saveButton.addActionListener(this);
	        saveButton.setActionCommand("save");
	        
	        JButton revertButton = new JButton("revertToOriginal");
	        revertButton.addActionListener(this);
	        revertButton.setActionCommand("revert");
	        savePanel.add(revertButton);
	        savePanel.add(saveButton);
	        
	        //constraints, hard to make meaning out of this numbers, kinda trial and error
	        c.gridx = 1;
	        c.gridy = 0;
	        c.gridwidth = 1;
	        c.weightx = 0.0;
	        c.weighty = 0.5;
	        c.fill = GridBagConstraints.BOTH;
	        pane.add(colorChooser,c);
	        
	        c.gridx = 0;
	        c.gridy = 1;
	        c.weightx = 0.1;
	        c.weighty = 0.1;
	        c.gridwidth = 1;
	        c.fill = GridBagConstraints.BOTH;
	        pane.add(colorList,c);
	        
	        c.gridx = 1;
	        c.gridy = 1;
	        c.weightx = 0.1;
	        c.weighty = 0.1;
	        c.gridwidth = 1;
	        c.fill = GridBagConstraints.BOTH;
	        pane.add(savePanel,c);
	        
	        c.gridx = 0;
	        c.gridy = 0;
	        c.weightx = 0.5;
	        c.weighty = 0.2;
	        c.gridwidth = 1;
	        c.fill = GridBagConstraints.BOTH;
	        pane.add(previewPane,c);
	        
	        
	        
			return pane;
		}




		private String getDownPlayer() {
			switch(up) {
			
			case PLAYER1 :return player2+"'s Corki"; 
			case PLAYER2 :return player1+"'s Corki";
			case HUMN :return "Computer's Corki";
			case COMP :return "Your Corki";

			}
			return null;
		}


		private String getUpPlayer() {
			switch(up) {
			
			case PLAYER1 :return player1+"'s Corki"; 
			case PLAYER2 :return player2+"'s Corki";
			case HUMN :return "Your Corki";
			case COMP :return "Computer's Corki";

			}			
			return null;
		}


		/**
		 * @return
		 */
		private JPanel getPreviewPanel(PositionGraphics fakePosGraph) {
			
	        JPanel previewPane = new JPanel();
	        previewPane.setLayout(new GridBagLayout());
	        GridBagConstraints c1 = new GridBagConstraints();
	        c1.fill = GridBagConstraints.BOTH;
	        c1.gridx = 0;
	        c1.gridy = 0;
	        c1.weightx = 0.5;
	        c1.weighty = 0.5;
	        
	        fakePosGraph.setPos(getFakePosition());
	        previewPane.add(fakePosGraph, c1);

	        previewPane.setBorder(BorderFactory.createTitledBorder("Preview"));
			return previewPane;
		}

		//matches string to proper parameter
	    protected void setParam(String paramString) {
	    	int index = 0;
	    	for(int i=0; i<stringParams.length; i++) {
	    		if(paramString.equals(stringParams[i])) {
	    			index = i;
	    			break;
	    		}
	    	}
			param = Parameters.values()[index];
			
		}



	    //dumb helper
	    private void out(String string) {
			System.out.println(string);
			
		}

	   /**
	    * 
	    * @return fake positions for preview
	    */
		private Position getFakePosition() {
			
	
			Position legalPos7 = new Position(0,0,true);
			Position legalPos8 = new Position(0,0,true);
			@SuppressWarnings("unused")
			Corki compsCorki = new Corki(true,false,legalPos7);
			Corki humnsCorki = new Corki(false,false,legalPos8);
			
			if(param != null) {
				switch(param) {
					case ILLEGAL_POS_COLOR : return new Position(0,0,false);
					case LEGAL_POS_COLOR : return new Position(0,0,true);
					case TYPE_A_CORKI_COLOR : return legalPos7;
					case TYPE_B_CORKI_COLOR : return legalPos8;
					case STAR_COLOR : Position legalPos2 = new Position(0,0,true); humnsCorki.setAsKing(true); legalPos2.setCorki(humnsCorki); return legalPos2;
					case STROKE_COLOR : Position legalPos3 = new Position(0,0,true); humnsCorki.setAsKing(true); legalPos3.setCorki(humnsCorki); return legalPos3;
					case DIAM_RATIO: Position legalPos4 = new Position(0,0,true); humnsCorki.setAsKing(true); legalPos4.setCorki(humnsCorki); return legalPos4;
					case INNER_DIAM_RATIO: Position legalPos5 = new Position(0,0,true); humnsCorki.setAsKing(true); legalPos5.setCorki(humnsCorki); return legalPos5;
					case STROKE_WIDTH: Position legalPos6 = new Position(0,0,true); humnsCorki.setAsKing(true); legalPos6.setCorki(humnsCorki); return legalPos6;
					case STAR_FILLED: Position legalPos9 = new Position(0,0,true); humnsCorki.setAsKing(true); legalPos9.setCorki(humnsCorki); return legalPos9;
					case SQUARE_RAISED: Position legalPos10 = new Position(0,0,true); humnsCorki.setAsKing(true); legalPos10.setCorki(humnsCorki); return legalPos10;
					case ALLOW_STROKE: Position legalPos11 = new Position(0,0,true); humnsCorki.setAsKing(true); legalPos11.setCorki(humnsCorki); return legalPos11;

				}
			}
			
			return new Position(0,0,false);
		}

		/**
		 * update the graphics position parameters
		 * @param posGraphics
		 */
		protected void updateGraphics(PositionGraphics posGraphics) {

			switch(param) {
				case ILLEGAL_POS_COLOR : PositionGraphics.illegalColor = paramColor; break;
				case LEGAL_POS_COLOR : PositionGraphics.legalColor = paramColor; break;
				case TYPE_A_CORKI_COLOR : PositionGraphics.typeAColor = paramColor; break;
				case TYPE_B_CORKI_COLOR : PositionGraphics.typeBColor = paramColor; break;
				case STAR_COLOR : PositionGraphics.starColor = paramColor; break;
				case STROKE_COLOR : PositionGraphics.strokeColor = paramColor; break;
				case DIAM_RATIO: PositionGraphics.diam_Ratio = diamRatio; break;
				case INNER_DIAM_RATIO: PositionGraphics.innerDiam_Ratio = innerDiamRatio; break;
				case STROKE_WIDTH: PositionGraphics.stroke_Width = strokeWidth; break;
				case STAR_FILLED: break;
				case SQUARE_RAISED: break;
				case ALLOW_STROKE: break;
				case BOARDER_COLOR: if(boardGraphics != null) PositionGraphics.borderColor = paramColor;
			}
			posGraphics.repaint();
			
		}

		//general helper method
		public static double[] getLoc(Dimension screen, Dimension frame, double minOffset) {
			
			double x_coor = (screen.getWidth()-frame.getWidth())/2.0;
			double y_coor = (screen.getHeight()-frame.getHeight())/2.0;
			double[] loc = {x_coor,y_coor};
			
			return loc;
			
		}


	    private void updateBoardAndPreview(PositionGraphics fakePosGraphics) {
			fakePosGraphics.setPos(getFakePosition());
			updateGraphics(fakePosGraphics);
			if(boardGraphics != null) {
				 boardGraphics.repaintCompletely();
			 }
		}


		@Override
		public void stateChanged(ChangeEvent e) {
			
			if(e.getSource().equals(diamWidthSpinner)) {
				JSpinner sp = (JSpinner) e.getSource();
				param = Parameters.DIAM_RATIO;
				diamRatio = (Double) sp.getValue();
				updateBoardAndPreview(fakePosGraphics2);
			} else if(e.getSource().equals(innerDiamWidthSpinner)) {
				JSpinner sp = (JSpinner) e.getSource();
				param = Parameters.INNER_DIAM_RATIO;
				innerDiamRatio = (Double) sp.getValue();
				updateBoardAndPreview(fakePosGraphics2);
			} else if(e.getSource().equals(strokeWidthSpinner)) {
				JSpinner sp = (JSpinner) e.getSource();
				param = Parameters.STROKE_WIDTH;
				strokeWidth = (Double) sp.getValue();
				updateBoardAndPreview(fakePosGraphics2);
			} else if(e.getSource().equals(colorChooser.getSelectionModel())) {
				
				//this loop takes care of some glitch with updating illegal position color
				for(int i=stringParams.length-6; i<stringParams.length; i++) {
					if (param.equals(Parameters.values()[i]) ){
						param = Parameters.ILLEGAL_POS_COLOR;
					}
				}
				paramColor = colorChooser.getColor();	
				updateBoardAndPreview(fakePosGraphics1);
			}
				
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if(command.equals("colorCombBox")) {
				JComboBox cb = (JComboBox)e.getSource();
		        String paramString = (String)cb.getSelectedItem();
		        if(paramString.equals(getUpPlayer())) {
		        	paramString = "Computer's Corki";
		        } else if(paramString.equals(getDownPlayer())) {
		        	paramString = "Human's Corki";
		        }
		        setParam(paramString);
		        updateBoardAndPreview(fakePosGraphics1);
			} else if(command.equals("fill star")) {
				
					setParam(command);
					if(PositionGraphics.isStarFilled) {
						PositionGraphics.isStarFilled = false;
					} else {
						PositionGraphics.isStarFilled = true;
					}
			        updateBoardAndPreview(fakePosGraphics2);
				
		        
			} else if(command.equals("raise square")) {
				setParam(command);
				if(PositionGraphics.isSquareRaised) {
					PositionGraphics.isSquareRaised = false;
				} else {
					PositionGraphics.isSquareRaised = true;
				}
		        updateBoardAndPreview(fakePosGraphics2);
			} else if(command.equals("allow stroke")) {
				 if(PositionGraphics.allowStroke) {
					 PositionGraphics.allowStroke = false;
					} else {
						 PositionGraphics.allowStroke = true;
				}
				setParam(command);
		        updateBoardAndPreview(fakePosGraphics2);
			} else if(command.equals("save")) {
				saveDefaultParams();
			} else if(command.equals("revert")) {
				try {
					setDefaultsBack();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				boardGraphics.repaintCompletely();
			}

		}

		private void setDefaultsBack() {
			Scanner sc = null;
			String next = "";
			
			Color[] colorArray = new Color[7];
			
				try {
					sc = new Scanner(new File("default.txt"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				for(int i=0; i<colorArray.length; i++) {
					next = sc.next();
					if(next.equals("[")) {
						colorArray[i] = new Color(sc.nextInt(),sc.nextInt(),sc.nextInt(),sc.nextInt());
					} else {
						out("wrong file format");
					}
					next = sc.next();
					if(!next.equals("]")) {
						out("wrong file format");
					}
				}
				
				PositionGraphics.illegalColor = colorArray[0];
				PositionGraphics.legalColor = colorArray[1];
				PositionGraphics.typeAColor =  colorArray[2];
				PositionGraphics.typeBColor =  colorArray[3];
				PositionGraphics.starColor =  colorArray[4];
				PositionGraphics.strokeColor =  colorArray[5];
				PositionGraphics.borderColor =  colorArray[6];
				PositionGraphics.diam_Ratio = sc.nextDouble();
				PositionGraphics.innerDiam_Ratio = sc.nextDouble();
				PositionGraphics.stroke_Width = sc.nextDouble();
				PositionGraphics.isStarFilled = (sc.nextInt() ==1);
				PositionGraphics.isSquareRaised = (sc.nextInt() ==1);
				PositionGraphics.allowStroke = (sc.nextInt() ==1);

			
		}


		private static void createAndShowGUI() {
			
			JFrame frame = new JFrame("BoardSetting");
			frame.setSize(800, 400);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			Board brd = new Board();
			BoardGraphics bg = new BoardGraphics(brd,null,null);
			BoardSettings bset = new BoardSettings(bg,GameLoader.PlayerType.HUMN,"me","you");
			frame.add(bset.getSettingPane());
			
			frame.setVisible(true);
		}
		
		public static void main(String[] args) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI();
				}
			});
		}

}
