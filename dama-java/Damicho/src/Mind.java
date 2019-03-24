
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;


public class Mind {
	
	protected LegalPos originLegalPos;
	protected Player player;
	protected Board board;
	protected Position[][] positions;
	protected static ArrayList<LegalPos> legalPositions = new ArrayList<LegalPos>();
	

	
	public Mind(Player player) {
		this.player = player;
		board = player.board;
		positions = board.getPositions();
	}


	//returns top legal positions, null if no legal position
	protected ArrayList<Position> getTopLegalPositions(Position pos, int level) {
		
		ArrayList<Position> topLegals = new ArrayList<Position>();
		int newX = pos.X()-level;
		int newY = pos.Y()-level;
		
		if(!(newX<0 || newY<0)) {
			topLegals.add(positions[newX][newY]);

		}
		
		newX = pos.X()+level;
		if(!(newX>board.getNumRows()-1 || newY<0)) {
			topLegals.add(positions[newX][newY]);

		}
		return topLegals;
		
	}
	
	protected Position getTopLegal(Position pos,boolean isRight, int level) {
		
		int newX = pos.X()-level;
		int newY = pos.Y()-level;
		
		if(isRight) {
			newX = pos.X()+level;
			if(!(newX>board.getNumRows()-1 || newY<0)) {
				return positions[newX][newY];

			}
		} else {
			if(!(newX<0 || newY<0)) {
				return positions[newX][newY];

			}
		}
		
		
		
		return null;
		
	}
	
	protected ArrayList<Position> getBottomLegalPositions(Position pos, int level) {
		ArrayList<Position> BotLegals = new ArrayList<Position>();
		int newX = pos.X()-level;
		int newY = pos.Y()+level;
		int yLimit = board.getNumColumns()-1;
		
		if(!(newX<0 || newY>yLimit)) {
			BotLegals.add(positions[newX][newY]);

		}
		
		newX = pos.X()+level;
		if(!(newX>board.getNumRows()-1 || newY>yLimit)) {
			BotLegals.add(positions[newX][newY]);

		}
		return BotLegals;		
	}
	
	protected Position getBotLegal(Position pos, boolean isRight, int level) {

		int newX = pos.X()-level;
		int newY = pos.Y()+level;
		int yLimit = board.getNumColumns()-1;
		
		if(isRight) {
			newX = pos.X()+level;
			if(!(newX>board.getNumRows()-1 || newY>yLimit)) {
				return positions[newX][newY];

			}
		} else {
			if(!(newX<0 || newY>yLimit)) {
				return positions[newX][newY];

			}
			
		}
		
		return null;
		
		
		
	}
	
	
	
	protected ArrayList<Position> getAllLegalPositions(Position pos, int level) {
		
		ArrayList<Position> allLegals = new ArrayList<Position>();
		
		allLegals.addAll(getTopLegalPositions(pos,level));
		allLegals.addAll(getBottomLegalPositions(pos,level));
	
		return allLegals;
		
	}
	
	//assumes that both positions are legal and the first position has Corki but
	//not the second one
	public void move(Position sourcePos, Position destnPos) {
		destnPos.setCorki(sourcePos.getCorki());
		destnPos.getPosGraph().repaint();
		sourcePos.setCorki(null);
		sourcePos.getPosGraph().repaint();
	}
	
	public void eat(Position sourcePos, Position destnPos) {
		move(sourcePos,destnPos);
		removeEatenCorkis(sourcePos,destnPos);
	}
	private void removeEatenCorkis(Position sourcePos, Position destnPos) {
		int x1 = sourcePos.X();
		int y1 = sourcePos.Y();
		int x2 = destnPos.X();
		int y2 = destnPos.Y();
		
		if(x1<x2) {
			if(y1<y2) {
				for(int i = x1; i<x2-1; i++) {
					positions[++x1][++y1].setCorki(null);
					positions[x1][y1].getPosGraph().repaint();
				}
			} else {
				for(int i = x1; i<x2-1; i++) {
					positions[++x1][--y1].setCorki(null);
					positions[x1][y1].getPosGraph().repaint();

				}
			}
				
		} else {
			if(y1<y2) {
				for(int i = y1; i<y2-1; i++) {
					positions[--x1][++y1].setCorki(null);
					positions[x1][y1].getPosGraph().repaint();

				}
			} else {
				for(int i = x1; i>x2+1; i--) {
					positions[--x1][--y1].setCorki(null);
					positions[x1][y1].getPosGraph().repaint();

				}
			}
		}
		
	}
	
	public  ArrayList<Position> getMaBackSeriously(Position sourcePos, Position destnPos) {
		
		ArrayList<Position> backPoses = new ArrayList<Position>();
				
		int x1 = sourcePos.X();
		int y1 = sourcePos.Y();
		int x2 = destnPos.X();
		int y2 = destnPos.Y();
		
		boolean destnTopRight = x2>x1 && y2<y1;
		boolean destnTopLeft = x2<x1 && y2<y1;
		boolean destnBotRight = x2>x1 && y2>y1;
		boolean destnBotLeft = x2<x1 && y2>y1;
		
		boolean stillMoreInMyBack = true;
		int counter = 0;
		Position tempPos = null;
		
		while(stillMoreInMyBack) {
			counter++;
			
			if(destnTopRight) {
				tempPos = getTopLegal(sourcePos,true,counter);
			} else if(destnTopLeft) {
				tempPos = getTopLegal(sourcePos,false,counter);

			} else if(destnBotLeft) {
				tempPos = getBotLegal(sourcePos,false,counter);

			} else if(destnBotRight) {
				tempPos = getBotLegal(sourcePos,true,counter);

			}
			
			if(tempPos != null) {
				backPoses.add(tempPos);
			} else {
				stillMoreInMyBack = false;
			}
		}
		
		
		
		return backPoses;
	
	}
	protected  ArrayList<Position> getMaBack(Position sourcePos, Position destnPos) {
		
		ArrayList<Position> backPoses = new ArrayList<Position>();
		
		backPoses.add(sourcePos);
		
		int x1 = sourcePos.X();
		int y1 = sourcePos.Y();
		int x2 = destnPos.X();
		int y2 = destnPos.Y();
		
		if(x1<x2) {
			if(y1<y2) {
				for(int i = x1; i<x2-1; i++) {
					backPoses.add(positions[++x1][++y1]);
				}
			} else {
				for(int i = x1; i<x2-1; i++) {
					backPoses.add(positions[++x1][--y1]);

				}
			}
				
		} else {
			if(y1<y2) {
				for(int i = y1; i<y2-1; i++) {
					backPoses.add(positions[--x1][++y1]);

				}
			} else {
				for(int i = x1; i>x2+1; i--) {
					backPoses.add(positions[--x1][--y1]);

				}
			}
		}
		
		return backPoses;
	}

	protected Position getEatingLandPos(Position depart, Position destin) {

		int x = 0;
		int y = 0;

		if(destin.X() > depart.X()) {
			x = destin.X()+1;
		} else {
			x = destin.X()-1;

		} 
		
		if(destin.Y() > depart.Y()) {
			y= destin.Y()+1;
		} else {
			y = destin.Y()-1;
		}
		
		//check the bound
		if(x<0 || x >= board.getNumColumns() || y<0 || y>=board.getNumRows()) {

			return null;
		}

		return board.getPositions()[x][y];
	}
	protected  GameLoader.Result doYourTurn() {
		// TODO Auto-generated method stub
		return null;
	}

	//this method returns the next possible moving positions from the corkis
	//position if there are no corkis at destination
	protected ArrayList<Position> getNextPossiblePositions(Corki corki) {
		ArrayList<Position> possiblePositions = new ArrayList<Position>();
		if(player.isTank) {
			if(corki.isKing()) {
				int x = corki.getPos().X();
				int y = corki.getPos().Y();
				//get all level legal positions
				int level = Math.max(Math.max(x-1, board.getNumColumns()-x), Math.max(y-1, board.getNumRows()-y));
				for(int i=1; i<=level; i++) {
					possiblePositions.addAll(getAllLegalPositions(corki.getPos(),i));
				}
			} else {
				possiblePositions.addAll(getAllLegalPositions(corki.getPos(),1));
			}
		} else {
			if(corki.isKing()) {
				possiblePositions.addAll(getAllLegalPositions(corki.getPos(),1));

			} else {
				if(player.isOnTop) {
					possiblePositions.addAll(getBottomLegalPositions(corki.getPos(),1));

				} else {
					possiblePositions.addAll(getTopLegalPositions(corki.getPos(),1));

				}
			}
		}
		return possiblePositions;
	}
	
	protected ArrayList<Position> getMoreEatables(Position landingPos,Corki corka) {

			Corki cork = new Corki(corka.isTypeA(),corka.isKing(),new Position(landingPos.X(),landingPos.Y(),true));

			ArrayList<Position> morePoses = getNextLegalPositions(null,cork,false);
			
			ArrayList<Position> eatables = new ArrayList<Position>();
			
			//get only the eatable.
			if(morePoses.size() >0) {
				for(int i=0; i<morePoses.size(); i++) {
					
							boolean isEatable = isEatable(morePoses.get(i));
					
							boolean previousPos = ((corka.getPos().X() == morePoses.get(i).X()) && (corka.getPos().Y() == morePoses.get(i).Y()));

							
							if(isEatable && (!previousPos)) {

								eatables.add(morePoses.get(i));
							} else {
							
								LegalPos legPos = getLegalPos(morePoses.get(i));

								if(!isEatable) {
									
									legalPositions.remove(legPos);

								} 
							}
						
				}
				
				if(eatables.size()>0) {

					ArrayList<Position> moreEatables = new ArrayList<Position>();
					ArrayList<Position> holder = new ArrayList<Position>();

					for(int i=0; i<eatables.size(); i++) {


						holder = getMoreEatables(eatables.get(i),cork);
						
						if(holder.size()>0) {
							for(int j=0; j<holder.size(); j++) {
								if(moreEatables.contains(holder.get(j))) {
									holder.remove(j);
								}

							}
							moreEatables.addAll(holder);
						}
					}
					
					if(moreEatables.size()>0) {
						eatables.addAll(moreEatables);
					}
				}

			}
			


		return eatables;
	}
	

	private LegalPos getLegalPos(Position posi) {
		
		for(int i=0; i<legalPositions.size(); i++) {
			LegalPos legPos = null;
			try {
				 legPos = legalPositions.get(i);

			} catch (Exception e) {
				continue;
			}
			
			if(legPos != null) {
				Position pos = legPos.getPos();
				if((pos.X() == posi.X()) && (pos.Y() == posi.Y())) {
					
					return legPos;
				}
			}
				
			
		}

		return null;
	}


	private boolean isEatable(Position posi) {
		for(int i=0; i<legalPositions.size(); i++) {
			LegalPos legPos = legalPositions.get(i);
			if(legPos.eatable) {
				Position pos = legPos.getPos();
				if((pos.X() == posi.X()) && (pos.Y() == posi.Y())) {
					return true;
				}
			}
		}
		return false;
	}


	@SuppressWarnings("serial")
	public class LineDrawer extends JComponent{
		
		private JPanel panel;
		private ArrayList<Integer> x_1;
		private ArrayList<Integer> x_2;
		private ArrayList<Integer> y_1;
		private ArrayList<Integer> y_2;



		
		public LineDrawer(JPanel panel) {
			this.panel = panel;
		}
		
		public void drawLine(Position firstPos, Position secondPos) {
			
			int posWidth = (int)(panel.getWidth()/8.0);
			int posHeight = (int)(panel.getHeight()/8.0);
			
			int x1 = 10+(int)(firstPos.X()*posWidth+(posWidth/2.0));
			int x2 = 10+(int)(secondPos.X()*posWidth+(posWidth/2.0));
			
			int y1 = 10+(int)(firstPos.Y()*posHeight+(posHeight/2.0));
			int y2 = 10+(int)(secondPos.Y()*posHeight+(posHeight/2.0));
			
			x_1.add(x1);
			x_2.add(x2);
			y_1.add(y1);
			y_2.add(y2);
						
		}
		
		public void paintComponent(Graphics g) {
						
			Graphics2D g1 = (Graphics2D)g;
			
			for(int i=0; i<x_1.size(); i++) {
				g1.drawLine(x_1.get(i), x_2.get(i), y_1.get(i), y_2.get(i));

			}
		}
	}
	
	@SuppressWarnings("unused")
	private class Runner implements Runnable {
		
		private LineDrawer lineDrawer;
		private Position firstPos;
		private Position secondPos;
		
		public Runner(LineDrawer lineDrawer, Position firstPos, Position secondPos) {
			this.lineDrawer = lineDrawer;
			this.firstPos = firstPos;
			this.secondPos = secondPos;
		}
		public void run() {
			lineDrawer.drawLine(firstPos, secondPos);
			lineDrawer.repaint();
		}
	}

	public ArrayList<Position> getTankEatablesRecursively(ArrayList<Position> pastEatables, ArrayList<Corki> origins) {
		
		ArrayList<Position> eatablesList = new ArrayList<Position>();
		
		ArrayList<Position> investigatedList = new ArrayList<Position>();

		ArrayList<Position> tempoList = new ArrayList<Position>();
		
		ArrayList<Corki> corkList = new ArrayList<Corki>();
		
		
	
			for(int j=0;j<pastEatables.size();j++) {
				
				Position pastPos = pastEatables.get(j);
		

				
				investigatedList.add(pastPos);
				
				LegalPos oldPos = getLegalPos(pastPos);
				
				ArrayList<Position> maBackPoses = getMaBackSeriously(pastPos, origins.get(j).getPos());

				Corki cork = new Corki(origins.get(j).isTypeA(),origins.get(j).isKing(), 
						new Position(pastPos.X(), pastPos.Y(),true));
				
				ArrayList<ArrayList<Position>> newEatableStore = getEatables(cork);

				for(int k=0; k<newEatableStore.size(); k++) {
					
					for(int r=0; r<newEatableStore.get(k).size(); r++) {
						
						Position adisPos = newEatableStore.get(k).get(r);
						if((!maBackPoses.contains(adisPos))) {
												
							LegalPos newPos = new LegalPos(adisPos,true);
							
								
								oldPos.getNext().add(newPos);
								newPos.getPrevious().add(oldPos);
								
								LegalPos oldi = getLegalPos(adisPos);
								if(oldi != null) {

									assimilate(oldi,newPos);

								} else {
									
									legalPositions.add(newPos);
								}
								
								tempoList.add(adisPos);
								corkList.add(cork);
								
							
							
						}
					}
				}
				
			
			}
		
		if(investigatedList.size() >0) {
			eatablesList.addAll(investigatedList);

		}
			
		if(tempoList.size() >0) {

				
				ArrayList<Position> moreGotten = getTankEatablesRecursively(tempoList,corkList);
				if(moreGotten.size() > 0) {
					eatablesList.addAll(moreGotten);

				}
			
		}
		
		
		return eatablesList;
		
	}
	
	public ArrayList<Position> getTankEatablesRecursively(Corki corki) {
		
		ArrayList<Position> pastEatables = new ArrayList<Position>();
		ArrayList<Corki> origins = new ArrayList<Corki>();

		ArrayList<ArrayList<Position>> etbleStore = getEatables(corki);
		
		originLegalPos = new LegalPos(corki.getPos(),true);
		
		for(int i=0; i<4; i++) {
			
			ArrayList<Position> diagoEatbles = etbleStore.get(i);
			
			for(int j=0;j<diagoEatbles.size();j++) {
				
				Position pastPos = diagoEatbles.get(j);
				LegalPos oldPos = new LegalPos(pastPos,true);
				originLegalPos.getNext().add(oldPos);
				oldPos.getPrevious().add(originLegalPos);
				
				legalPositions.add(oldPos);
				
				pastEatables.add(pastPos);
				origins.add(corki);
			}
		}
			
		return getTankEatablesRecursively(pastEatables,origins);
	}
	
	private void assimilate(LegalPos oldPos, LegalPos newPos) {
		
		for(LegalPos legs: newPos.getNext()) {
			if(!oldPos.getNext().contains(legs)) {
				oldPos.getNext().add(legs);
				legs.getPrevious().remove(newPos);
				legs.getPrevious().add(oldPos);

			}
		}
		
		for(LegalPos legs: newPos.getPrevious()) {
			if(!oldPos.getPrevious().contains(legs)) {
				oldPos.getPrevious().add(legs);
				legs.getNext().remove(newPos);
				legs.getNext().add(oldPos);

			}
		}
		
		if(newPos.eatable) {
			oldPos.setEatable(true);
		}
	}
	protected ArrayList<Position> getNextLegalPositions(PositionGraphics firstPosGraph,Corki corka,boolean firstCall) {
		
		//reset legalPositions
		
		if(firstCall) {
			legalPositions = new ArrayList<LegalPos>();

		}
		
		Corki corki = null;
		if(corka != null) {
			corki = corka;
		} else {
			corki = firstPosGraph.getPos().getCorki();
		}
		if(corki == null ) {
			JOptionPane.showMessageDialog(null, "Null corki");
			return null;
		}
		ArrayList<Position> legalPoses = new ArrayList<Position>();
		if(player.isTank && corki.isKing()) {
			
			ArrayList<Position> tankLegals = getNextTanKingLegals(corki,null);

			//there is no need to fear repitition
			legalPoses.addAll(tankLegals);
			
			tankLegals = getTankEatablesRecursively(corki);
			

			for(Position legPos: tankLegals) {
				if(!legalPoses.contains(legPos)) {
					legalPoses.add(legPos);
				}

			}			
		} else {
			ArrayList<Position> possibles = getNextPossiblePositions(corki);
			
			LegalPos originPos;
			if(firstCall) {
				originPos = new LegalPos(corki.getPos(),true); //care about true
				originLegalPos = originPos;
			} else {
				originPos = getLegalPos(corki.getPos());

			}
			
			
			for(int i= 0; i<possibles.size(); i++) {
				if(!possibles.get(i).hasCorki()) {
					legalPoses.add(possibles.get(i));
					legalPositions.add(new LegalPos(possibles.get(i),false));

				} else {

					if(!(corki.isTypeA() == possibles.get(i).getCorki().isTypeA())) {

						Position landingPos = getEatingLandPos(corki.getPos(),possibles.get(i));
						if(landingPos != null) {
							if(!landingPos.hasCorki()) {

								if(possibles.get(i).getCorki().isKing()) {

									//ordinary can't eat king in EGregna
									if((corki.isKing() || player.isTank) && (landingPos != null)) {
										LegalPos newPos = new LegalPos(landingPos,true);
										originPos.getNext().add(newPos);
										newPos.getPrevious().add(originPos);
										
										LegalPos oldPos = getLegalPos(landingPos);
										
										if( oldPos != null) {
											assimilate(oldPos,newPos);
										} else {
											legalPoses.add(landingPos);
											legalPositions.add(newPos);
										}
										
										
										if(firstCall) {
											legalPoses.addAll(getMoreEatables(landingPos,corki));

										}
									}
								} else {
									if(landingPos != null) {
										
										LegalPos newPos = new LegalPos(landingPos,true);
										
										originPos.getNext().add(newPos);
										newPos.getPrevious().add(originPos);
										LegalPos oldPos = getLegalPos(landingPos);
										
										if( oldPos != null) {
											assimilate(oldPos,newPos);
										} else {
											legalPoses.add(landingPos);
											legalPositions.add(newPos);
										}
										
										if(firstCall) {
											legalPoses.addAll(getMoreEatables(landingPos,corki));

										}
									} 
									
									
								}
							}
						}
						
						
					}
				}
			}
		}
		return legalPoses;
	}
	public ArrayList<Position> getEatableSequence(Position firstPos, Position secondPos) {
		
		ArrayList<Position> sequence = new ArrayList<Position>();
				
		getNextLegalPositions(null, firstPos.getCorki(),true);

		LegalPos destnPos = getLegalPos(secondPos);
		
		LegalPos prevPos = null;

		
		if(destnPos != null) {
			

			sequence.add(destnPos.getPos());
			
			boolean morePrev = true;
			while(morePrev) {
				
				if(destnPos.getPrevious() != null) {

					prevPos = destnPos.getPrevious().get(0);
					if(prevPos == null) {
						morePrev = false;
					} else {
						
						sequence.add(prevPos.getPos());
						
						if(prevPos.getPos().equals(firstPos)) {
							return sequence;
						}
						destnPos = prevPos;

					}

				} else {
					morePrev = false;

				}
				
			
				
			}
		} 
		

		
		return sequence;
	}
	
	public ArrayList<ArrayList<Position>> getEatbleSequence(Position firstPos, Position secondPos) {
		
		getNextLegalPositions(null, firstPos.getCorki(),true);

		ArrayList<ArrayList<Position>> big = new ArrayList<ArrayList<Position>>();
		ArrayList<Position> singleSeq = new ArrayList<Position>();
				

		
		LegalPos destnPos ;
		if(firstPos.equals(secondPos)) {
			destnPos = originLegalPos.getNext().get(0);
		} else {
			destnPos = getLegalPos(secondPos);
		}
		
		if(destnPos == null) {
			JOptionPane.showMessageDialog(null, "destnPos null");
			return null;
		}
		
		singleSeq.add(destnPos.getPos());
		
		
		getSequencesRecursively(big,singleSeq,destnPos,firstPos);
		
		//loop eat
		if(firstPos.equals(secondPos)) {
			for(ArrayList<Position> seq: big) {
				if(seq.size() == 2) {
					big.remove(seq);
				} else {
					seq.add(0, firstPos);
				}
			}
		}
		return big;
		
				
	}
	public void getSequencesRecursively(ArrayList<ArrayList<Position>> big, ArrayList<Position> singleSeq, LegalPos lastLeg, Position firstPos ) {
		

		ArrayList<LegalPos> prevList = lastLeg.getPrevious();
		
		for(LegalPos legs:prevList ) {
			
			if(legs.getPos().equals(firstPos)) {
				
					ArrayList<Position> seq = new ArrayList<Position>() ;
					seq.addAll(singleSeq);
					seq.add(legs.getPos());
					big.add(seq);
				
					
			} else {

				
				if(!singleSeq.contains(legs.getPos())) {
					
					ArrayList<Position> seq1 = new ArrayList<Position>() ;
					seq1.addAll(singleSeq);
					seq1.add(legs.getPos());
					getSequencesRecursively(big,seq1,legs,firstPos);
					
				}
				
			}
		}
	}
	
	public ArrayList<ArrayList<Position>> getEatables(Corki corki) {
		

		ArrayList<ArrayList<Position>> eatableStore = new ArrayList<ArrayList<Position>>();
		
		for(int i=0; i<4; i++) {
			eatableStore.add(new ArrayList<Position>());
		}
		getNextTanKingLegals(corki,eatableStore);
		

		
		return eatableStore;
	}
	public ArrayList<Position> getNextTanKingLegals(Corki corki, ArrayList<ArrayList<Position>> eatableStore) {
		
		ArrayList<Position> legalPoses = new ArrayList<Position> ();
		
		for(int i=1; i<=4; i++) {
			boolean adjacentEmpty = true;
			Position nextPos = null;
			int counter = 0;
			
			while(adjacentEmpty) {
				counter++;
				switch(i) {
				case 1: nextPos = getTopLegal(corki.getPos(),true,counter);break;
				case 2: nextPos = getTopLegal(corki.getPos(),false,counter);break;
				case 3: nextPos = getBotLegal(corki.getPos(),true,counter);break;
				case 4: nextPos = getBotLegal(corki.getPos(),false,counter);break;
				}
				
				if(nextPos == null) {
					adjacentEmpty = false;
				} else {
					if(nextPos.hasCorki()) {
						adjacentEmpty = false;
					} else {
						legalPoses.add(nextPos);
						
						if(eatableStore == null) {
							
							LegalPos leg = new LegalPos(nextPos,false);
							
							legalPositions.add(leg);
							
						}
						
					}
				}
			}
			

			if((nextPos != null) && (eatableStore != null)) {
				if(corki.isTypeA() != nextPos.getCorki().isTypeA()) {
					boolean moreLandingPos = true;
					
					while(moreLandingPos) {
						Position landingPos = getEatingLandPos(corki.getPos(),nextPos); 

						if((landingPos != null) && (!landingPos.hasCorki())) {
								
									eatableStore.get(i-1).add(landingPos);


							nextPos = landingPos;
							} else {
								moreLandingPos = false;
							}
					}	
				}
		}
			
			
			
				
	}
		return legalPoses;	
	}
		
						
				
	


	protected void undoMove() {
		// TODO Auto-generated method stub
		
	}

	protected ArrayList<Corki> getMovableCorkis() {
		
		ArrayList<Corki> movableCorkis = new ArrayList<Corki>();
		
		for(Corki cork: player.corkiList) {
			
			if(getNextLegalPositions(null,cork,true).size() >0) {
				movableCorkis.add(cork);
			}
		}
		
		return movableCorkis;
	}

	protected GameLoader.Result doYourTurn(PositionGraphics firstPosGraph,
			PositionGraphics secondPosGraph) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class LegalPos {
		private boolean eatable;
		private Position pos;
		private ArrayList<LegalPos> next;
		private ArrayList<LegalPos> previous;

		
		public LegalPos(Position pos, boolean eatable) {
			this.pos = pos;
			this.eatable = eatable;
			this.next = new ArrayList<LegalPos>();
			this.previous = new ArrayList<LegalPos>();

		}
		
		public Position getPos() {
			return pos;
		}
		
		public ArrayList<LegalPos> getNext() {
			return next;
		}
		
		public ArrayList<LegalPos> getPrevious() {
			return previous;
		}
		
		public boolean isEatable() {
			return eatable;
		}
		
		public void setEatable(boolean eatble) {
			eatable = eatble;
		}
	}
	

}
