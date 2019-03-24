import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;


public class Receptionist implements ActionListener {
	
	private boolean humanToHuman;
	private boolean Tank;
	private GameLoader.PlayerType up;
	private GameLoader.PlayerType whoStarts;
	private int level;
	private JDialog initialDialog;
	private boolean dialogAlreadyCreated;
	private String player1;
	private String player2;
	private String playerA;
	private String playerB;
	
	private GameLoader gameLoader;
	private boolean defaultMode;
	private ImageIcon imageIcon;
	

	public Receptionist(GameLoader gameLoader) {
		this.gameLoader = gameLoader;
		imageIcon = new ImageIcon("Picture 017.jpg");

	}

	class WindowHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	private void displayFirstDialog() {
		
		if(!dialogAlreadyCreated) {
			initialDialog = new JDialog();
			initialDialog.addWindowListener(new WindowHandler());
		} else {
			initialDialog.getContentPane().removeAll();
		}
		humanToHuman = false;
		initialDialog.setTitle("Initial Settings");
		initialDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JPanel panel = getFirstDialogPane();
      
		initialDialog.add(panel);        
		initialDialog.pack();
		if(!dialogAlreadyCreated) {
			dialogAlreadyCreated = true;

		}
		GameLoader.resizeToCenter(initialDialog);
		initialDialog.setIconImage(imageIcon.getImage());
		initialDialog.setVisible(true);


	}
	
	/**
	 * @return
	 */
	private JPanel getFirstDialogPane() {
		JPanel radioPanel = new JPanel(new GridLayout(0,1));

		JPanel subPanel1 = new JPanel(new GridLayout(0,1));
		
		Border titleBorder1 = BorderFactory.createTitledBorder("Mode");
		Border emptyBorder1 = BorderFactory.createEmptyBorder(5, 5, 20, 5);
		Border compBorder1 = BorderFactory.createCompoundBorder(emptyBorder1, titleBorder1);

		subPanel1.setBorder(compBorder1);
		ButtonGroup group1 = new ButtonGroup();
		addRadioButton("Human To Human", "Human To Human",false, group1,subPanel1);
		addRadioButton("Human To Computer", "Human To Computer", true, group1,subPanel1);

		JPanel subPanel2 = new JPanel(new GridLayout(0,1));
		
		Border titleBorder2 = BorderFactory.createTitledBorder("Game type");
		Border emptyBorder2 = BorderFactory.createEmptyBorder(0, 5, 20, 5);
		Border compBorder2 = BorderFactory.createCompoundBorder(emptyBorder2, titleBorder2);

		subPanel2.setBorder(compBorder2);
		ButtonGroup group2 = new ButtonGroup();
		addRadioButton("Tankegna", "Tankegna", false, group2,subPanel2);
		addRadioButton("Egregna", "Egregna", true, group2,subPanel2);
		
		//add subPanels to radio panel
		radioPanel.add(subPanel1);
		radioPanel.add(subPanel2);
		
        //create JButton
        JButton next =  new JButton("Next");
        next.setActionCommand("firstNext");
        next.addActionListener(this);
        
        //set the next layout
        JPanel bottomPanel = new JPanel(new GridLayout(1,4));
        Dimension d = new Dimension(bottomPanel.getWidth()/5,bottomPanel.getHeight()/5);
        
        bottomPanel.add(Box.createRigidArea(d));
        bottomPanel.add(next);
        bottomPanel.add(Box.createRigidArea(d));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(radioPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
		return panel;
	}
	/**
	 * @param subPanel1 
	 * @return
	 */
	private void addRadioButton(String displayText, String command, boolean isSelected, ButtonGroup group, JPanel subPanel) {
		//create radio button items
	    JRadioButton radioButton = new JRadioButton(displayText);
	    radioButton.addActionListener(this);
	    radioButton.setActionCommand(command);
	    if(isSelected) {
		    radioButton.setSelected(isSelected);
	    }
	    
	    group.add(radioButton);
	    
	  //Put the radio buttons in a column in a panel.
        subPanel.add(radioButton);
	}

	private void displaySecondDialog() {
		
		initialDialog.getContentPane().removeAll();
		
		JPanel panel = getSecondDialogPane();
      
		initialDialog.add(panel);  
		initialDialog.pack();
		GameLoader.resizeToCenter(initialDialog);
		initialDialog.setIconImage(imageIcon.getImage());
		initialDialog.setVisible(true);
	}
	
	private JPanel getSecondDialogPane() {
		JPanel radioPanel = new JPanel(new GridLayout(1,0));

		JPanel subPanel1;
		if(humanToHuman) {
			subPanel1 = getNamePanel();

		} else {
			subPanel1 = new JPanel(new GridLayout(0,1));
			subPanel1.add(getLevelPanel());
			subPanel1.add(getNamePanel());

		}
		
		JPanel subPanel2 = getPositionPanel();
		JPanel subPanel3 = getStarterPanel();
		
		JPanel mergerPane = new JPanel(new GridLayout(0,1));
		mergerPane.add(subPanel2);
		mergerPane.add(subPanel3);
		//add subPanels to radio panel
		radioPanel.add(subPanel1);
		radioPanel.add(mergerPane);


        //create JButton
        JButton next =  new JButton("Next");
        next.setActionCommand("secondNext");
        next.addActionListener(this);
        
        JButton back =  new JButton("Back");
        back.setActionCommand("firstBack");
        back.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel bottomPanel = new JPanel(new GridLayout(1,4));
        Dimension d = new Dimension(bottomPanel.getWidth()/5,bottomPanel.getHeight()/5);
        
        bottomPanel.add(Box.createRigidArea(d));
        bottomPanel.add(back);
        bottomPanel.add(Box.createRigidArea(d));
        bottomPanel.add(next);
        bottomPanel.add(Box.createRigidArea(d));
        
        panel.add(radioPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
		return panel;
	}


	private JPanel getNamePanel() {
		JPanel namePanel = new JPanel(new GridLayout(0,1));

		Border titleBorder = BorderFactory.createTitledBorder("Press enter in the end");
		Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 20, 0);
		Border compBorder = BorderFactory.createCompoundBorder(emptyBorder, titleBorder);

		namePanel.setBorder(compBorder);
		
		JPanel player1 = new JPanel(new FlowLayout());
		
		String player;
		
		if(humanToHuman) {
			player = "Player1";
		} else {
			player = "Your name";
		}
		JLabel label1 = new JLabel(player+"(no space)");
		JTextField field1 = new JTextField(10);
		field1.addActionListener(this);
		field1.setActionCommand("player1_name");
		
		player1.add(label1);
		player1.add(field1);
		
		namePanel.add(player1);

		if(humanToHuman) {
			JPanel player2 = new JPanel(new FlowLayout());
			
			JLabel label2 = new JLabel("Player2(no space)");
			JTextField field2 = new JTextField(10);
			field2.addActionListener(this);
			field2.setActionCommand("player2_name");
			
			player2.add(label2);
			player2.add(field2);
			
			namePanel.add(player2);
		}
        
		
		JPanel mergerPane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		Dimension d = new Dimension(mergerPane.getWidth(),mergerPane.getHeight()/6);
		
		c.gridx = 0;
		c.gridy = 0;
		mergerPane .add(Box.createRigidArea(d),c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		mergerPane .add(namePanel,c);
		c.gridx = 0;
		c.gridy = 4;
        mergerPane.add(Box.createRigidArea(d),c);
		
		
		return mergerPane;
	}

	/**
	 * @return
	 */
	private JPanel getStarterPanel() {
		JPanel subPanel3 = new JPanel(new GridLayout(0,1));

		
		if(humanToHuman) {
			player1 = "Player1";
			player2 = "Player2";
		} else {
			player1 = "You";
			player2 = "Computer";
		}
		Border titleBorder3 = BorderFactory.createTitledBorder("Who will start");
		Border emptyBorder3 = BorderFactory.createEmptyBorder(0, 20, 20, 0);
		Border compBorder3 = BorderFactory.createCompoundBorder(emptyBorder3, titleBorder3);

		subPanel3.setBorder(compBorder3);
		ButtonGroup group3 = new ButtonGroup();
		addRadioButton(player1,player1+"Starts",false, group3,subPanel3);
		addRadioButton(player2, player2+"Starts",true, group3,subPanel3);
		return subPanel3;
	}

	/**
	 * @return
	 */
	private JPanel getPositionPanel() {
		JPanel subPanel2 = new JPanel(new GridLayout(0,1));
		
		Border titleBorder2 = BorderFactory.createTitledBorder("Top position");
		Border emptyBorder2 = BorderFactory.createEmptyBorder(0, 20, 20, 0);
		Border compBorder2 = BorderFactory.createCompoundBorder(emptyBorder2, titleBorder2);

		
		if(humanToHuman) {
			player1 = "Player1";
			player2 = "Player2";
		} else {
			player1 = "You";
			player2 = "Computer";
		}
		
		subPanel2.setBorder(compBorder2);
		ButtonGroup group2 = new ButtonGroup();
		addRadioButton(player1,player1+"Up", false, group2,subPanel2);
		addRadioButton(player2, player2+"Up",true, group2,subPanel2);
		return subPanel2;
	}

	/**
	 * @return
	 */
	private JPanel getLevelPanel() {
		
		JPanel subPanel1 = new JPanel(new GridLayout(1,0));
		
		Border titleBorder1 = BorderFactory.createTitledBorder("Level");
		Border emptyBorder1 = BorderFactory.createEmptyBorder(0, 0, 20, 0);
		Border compBorder1 = BorderFactory.createCompoundBorder(emptyBorder1, titleBorder1);

		subPanel1.setBorder(compBorder1);
		ButtonGroup group = new ButtonGroup();
		addRadioButton("1","1", false, group,subPanel1);
		addRadioButton("2","2", true, group,subPanel1);
		addRadioButton("3","3", false, group,subPanel1);
		addRadioButton("4","4", false, group,subPanel1);
		addRadioButton("5","5", false, group,subPanel1);
		return subPanel1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if(command.equals("Human To Human")) {
			humanToHuman = true;

		} else if(command.equals("Human To Computer")) {
			humanToHuman = false;
		} else if(command.equals("firstNext")) {
			displaySecondDialog();
		} else if(command.equals("secondNext")) {
			setUserLazyOptions();
			updateGameLoader();
			displayThirdDialog();
		} else if(command.equals("firstBack")) {
			displayFirstDialog();
		} else if(command.equals("secondBack")) {
			playerA = null;
			playerB = null;
			up = null;
			whoStarts = null;
			level = 0;
			displaySecondDialog();
		} else if(command.equals("finish")) {
			gameLoader.setStart(true);
			initialDialog.dispose();
		} else if(command.equals("save")) {
			saveDefaults();
			if(new File("dfault1.txt").isFile()) {
				System.out.println("File is file");
			}
		} else if(command.equals("1") || command.equals("2") || command.equals("3")
				|| command.equals("4") || command.equals("5")) {
			level = Integer.parseInt(command);
		} else if(command.equals("Tankegna")) {
			Tank = true;
		} else if(command.equals("Egregna")) {
			Tank = false;
		} else if(command.equals(player1+"Starts") || command.equals(player2+"Starts")) {
			if(getPlayerType(command) != null) {
				whoStarts = getPlayerType(command);
			} else {
				System.out.println("null starter type");
			}
		} else if(command.equals(player1+"Up") || command.equals(player2+"Up")) {
			if(getPlayerType(command) != null) {
				up = getPlayerType(command);
			} else {
				System.out.println("null up type");
			}
		} else if(command.equals("player1_name")) {
			JTextField field = (JTextField) e.getSource();
			playerA = field.getText();
		} else if(command.equals("player2_name")) {
			JTextField field = (JTextField) e.getSource();
			playerB = field.getText();
		} 			
	}

	/**
	 * 
	 */
	private void updateGameLoader() {
		gameLoader.setHumanToHuman(humanToHuman);
		gameLoader.setLevel(level);
		gameLoader.setPlayer1(playerA);
		gameLoader.setPlayer2(playerB);
		gameLoader.setTank(Tank);
		gameLoader.setUp(up);
		gameLoader.setWhoStarts(whoStarts);
	}

	private void saveDefaults() {
		
		FileWriter writer = null;
		 
		 try {
			 writer = new FileWriter("default1.txt");
			 writer.write(""+humanToHuman);
			 writer.write(" "+Tank);
			 writer.write(" "+up.toString());
			 writer.write(" "+whoStarts.toString());
			 writer.write(" "+level);
			 writer.write(" "+playerA);
			 writer.write(" "+playerB);
			 writer.close();
			 
		 } catch (IOException e) {
				System.out.println("File not written properly");
				e.printStackTrace();
		 } 
		
	}

	private void setUserLazyOptions() {
		if(humanToHuman) {

			if(playerA == null) {
				playerA = "Player1";
			}
			if(playerB == null) {
				playerB = "Player2";
			}
			if(up == null) {
				up = GameLoader.PlayerType.PLAYER2;
			}
			if(whoStarts == null) {
				whoStarts = GameLoader.PlayerType.PLAYER2;
			}
		} else {
			if(level==0) {
				level=2;
			}
			if(playerA == null) {
				playerA = "You";
			}
			if(playerB == null) {
				playerB = "Computer";
			}
			if(up == null) {
				up = GameLoader.PlayerType.COMP;
			}
			if(whoStarts == null) {
				whoStarts = GameLoader.PlayerType.COMP;
			}
		}
		
	}

	private void displayThirdDialog() {
		if(defaultMode) {
			initialDialog = new JDialog();
			initialDialog.addWindowListener(new WindowHandler());
			dialogAlreadyCreated = true;
			defaultMode = false;
		} else {
			initialDialog.getContentPane().removeAll();

		}
		
		JPanel panel = getThirdDialogPane();
      
		initialDialog.add(panel);  
		initialDialog.pack();
		GameLoader.resizeToCenter(initialDialog);
		initialDialog.setIconImage(imageIcon.getImage());
		initialDialog.setVisible(true);		
	}

	private JPanel getThirdDialogPane() {
		JPanel panel = new JPanel(new BorderLayout());
		Border emptyBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
		panel.setBorder(emptyBorder);

		
		JPanel labelPanel = new JPanel(new GridLayout(0,1));
		Border titleBorder1 = BorderFactory.createTitledBorder("Chosen Settings");
		Border emptyBorder1 = BorderFactory.createEmptyBorder(0, 20, 20, 20);
		Border compBorder1 = BorderFactory.createCompoundBorder(emptyBorder1, titleBorder1);

		labelPanel.setBorder(compBorder1);
		
		//create labels
		JLabel modeLabel = new JLabel((gameLoader.isHumanToHuman()?"Human To Human":"Human To Computer"));
		JPanel modePanel = getLabelPanel(modeLabel,"Mode : ");
		JLabel typeLabel = new JLabel((gameLoader.isTank()?"Tankegna":"Egregna"));
		JPanel typePanel = getLabelPanel(typeLabel,"GameType : ");
		JLabel playersLabel = new JLabel(gameLoader.getPlayer1()+" Vs "+gameLoader.getPlayer2());
		JPanel playersPanel = getLabelPanel(playersLabel,"Fighters : ");
		JLabel positionLabel = new JLabel(getName(gameLoader.getUp()));
		JPanel positionPanel = getLabelPanel(positionLabel,"Up Position : ");
		JLabel starterLabel = new JLabel(getName(gameLoader.getWhoStarts()));
		JPanel starterPanel = getLabelPanel(starterLabel,"Starter : ");
		JLabel levelLabel = new JLabel(""+gameLoader.getLevel());
		JPanel levelPanel = getLabelPanel(levelLabel,"Level : ");
		//create JButton
        JButton finish =  new JButton("Finish");
        finish.setActionCommand("finish");
        finish.addActionListener(this);
        
        JButton back =  new JButton("Back");
        back.setActionCommand("secondBack");
        back.addActionListener(this);
        
        JButton save =  new JButton("Save Settings");
        save.setActionCommand("save");
        save.addActionListener(this);
        
        JPanel bottomPanel = new JPanel(new FlowLayout());
        Dimension d = new Dimension(bottomPanel.getWidth()/7,bottomPanel.getHeight()/5);
        
        bottomPanel.add(Box.createRigidArea(d));
        bottomPanel.add(back);
        bottomPanel.add(Box.createRigidArea(d));
        bottomPanel.add(save);
        bottomPanel.add(Box.createRigidArea(d));
        bottomPanel.add(Box.createRigidArea(d));
        bottomPanel.add(finish);
        bottomPanel.add(Box.createRigidArea(d));
        
        //add labels and buttons to panel
        labelPanel.add(modePanel);
        labelPanel.add(typePanel);
        labelPanel.add(playersPanel);
        labelPanel.add(positionPanel);
        labelPanel.add(starterPanel);
        if(!gameLoader.isHumanToHuman()) {
        	labelPanel.add(levelPanel);
        }
        
        panel.add(labelPanel,BorderLayout.CENTER);
        panel.add(bottomPanel,BorderLayout.SOUTH);
        
		return panel;
	}

	/**
	 * @param modeLabel
	 * @param labelString 
	 * @return
	 */
	private JPanel getLabelPanel(JLabel modeLabel, String labelString) {
		Font font = new Font(	Font.SERIF,Font.BOLD,16);
		JLabel modeString = new JLabel(labelString);
		modeString.setFont(font);
		JPanel modePanel =new JPanel(new FlowLayout());
		modePanel.add(modeString);
		modePanel.add(modeLabel);
		return modePanel;
	}

	private String getName(GameLoader.PlayerType player) {

		switch(player) {
		
		case PLAYER1 :return gameLoader.getPlayer1(); 
		case PLAYER2 :return gameLoader.getPlayer2();
		case HUMN :return gameLoader.getPlayer1();
		case COMP :return gameLoader.getPlayer2();

		}
		
		
		return "UNKNOWN"; //hmmm
	}

	private GameLoader.PlayerType getPlayerType(String command) {
		
		
		if(command.startsWith("Player1") ) {
			return GameLoader.PlayerType.PLAYER1;
		} else if(command.startsWith("Player2")) {
			return GameLoader.PlayerType.PLAYER2;
		}  else if(command.startsWith("You")) {
			return GameLoader.PlayerType.HUMN;
		} else if (command.startsWith("Computer")) {
			return GameLoader.PlayerType.COMP;
		}
		return null;
	}

	private GameLoader.PlayerType getPlayer(String command) {
		
		
		if(command.equals("PLAYER1")) {
			return GameLoader.PlayerType.PLAYER1;
		} else if(command.equals("PLAYER2")) {
			return GameLoader.PlayerType.PLAYER2;
		}  else if(command.equals("HUMN")) {
			return GameLoader.PlayerType.HUMN;
		} else if (command.equals("COMP")) {
			return GameLoader.PlayerType.COMP;
		}
		return null;
	}

	
	public void run() {
		if(!userLikesDefault()) {
			displayFirstDialog();
		} else {
			try {
				setDefaults();
			} catch (Exception e) {
				displayFirstDialog();
				System.out.println("Something wrong with default file");
				e.printStackTrace();
			}
		}
		
	}

	private void setDefaults() throws Exception {
		Scanner sc = null;
		String[] array = new String[7];
		
			sc = new Scanner(new File("default1.txt"));
			
			for(int i = 0; i<7; i++) {
				array[i]=sc.next();
			}
			if(sc.hasNext()) {
				throw new Exception("items in file greater than 7");
			}
			
			humanToHuman = array[0].equals("true");
			Tank = array[1].equals("true");
			up = getPlayer(array[2]);
			whoStarts = getPlayer(array[3]);
			level = Integer.parseInt(array[4]);
			playerA = array[5];
			playerB = array[6];
			
			defaultMode = true;
			updateGameLoader();
			displayThirdDialog();
							
	}

	private boolean userLikesDefault() {
		int userChoice = JOptionPane.showConfirmDialog(null,"Is it your first time?", 
				null,JOptionPane.YES_NO_OPTION);
		if(userChoice == 0) {
			return false;
		} else {
			userChoice = JOptionPane.showConfirmDialog(null,"Would you like to use saved initial settings?", 
					null,JOptionPane.YES_NO_OPTION);
		}
		return userChoice == 0;
	}

}
