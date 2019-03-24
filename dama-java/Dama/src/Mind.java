
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;





public class Mind {
	
	private Player player;
	private Board board;
	private Position[][] positions;
	private static ArrayList<LegalPos> legalPositions = new ArrayList<LegalPos>();
	private LineDrawer.GlassPane glassPane;
	private GameLoader.Result result;
	private ArrayList<ArrayList<moveHistory>> undoList;
	private moveHistory justMoved;
	private int undoIndex = -1;
	private LegalPos originLegalPos;
	
	/**
	 * @return the glassPane
	 */
	public LineDrawer.GlassPane getGlassPane() {
		return glassPane;
	}


	/**
	 * @param glassPane the glassPane to set
	 */
	public void setGlassPane(LineDrawer.GlassPane glassPane) {
		this.glassPane = glassPane;
	}



	
	/**
	 * @return the undoIndex
	 */
	public int getUndoIndex() {
		return undoIndex;
	}


	/**
	 * @param undoIndex the undoIndex to set
	 */
	public void setUndoIndex(int undoIndex) {
		this.undoIndex = undoIndex;
	}


	/**
	 * @return the undoList
	 */
	public ArrayList<ArrayList<moveHistory>> getUndoList() {
		return undoList;
	}


	/**
	 * @param undoList the undoList to set
	 */
	public void setUndoList(ArrayList<ArrayList<moveHistory>> undoList) {
		this.undoList = undoList;
	}


	public Mind(Player player) {
		this.player = player;
		board = player.getBoard();
		positions = board.getPositions();
	}


	/**
	 * @return the justMoved
	 */
	public moveHistory getJustMoved() {
		return justMoved;
	}


	//returns top legal positions, null if no legal position
	public ArrayList<Position> getTopLegalPositions(Position pos, int level) {
		
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
	
	public Position getTopLegal(Position pos,boolean isRight, int level) {
		
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
	
	public ArrayList<Position> getBottomLegalPositions(Position pos, int level) {
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
	
	public Position getBotLegal(Position pos, boolean isRight, int level) {

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
	
	
	
	public ArrayList<Position> getAllLegalPositions(Position pos, int level) {
		
		ArrayList<Position> allLegals = new ArrayList<Position>();
		
		allLegals.addAll(getTopLegalPositions(pos,level));
		allLegals.addAll(getBottomLegalPositions(pos,level));
	
		return allLegals;
		
	}
	
	/**
	 * assumes that both positions are legal and the first position has Cork but
	 * not the second one
	 */
	
	public void move(Position sourcePos, Position destnPos, ArrayList<moveHistory> newHistory) {
		
		moveHistory mhist = null;
		//set up new history move
		if(newHistory != null) {
			mhist = new moveHistory(sourcePos.getCorki());
			mhist.setPastPos(sourcePos);
			mhist.setRemoved(false);
		}
		
		
		destnPos.setCorki(sourcePos.getCorki());
		destnPos.getPosGraph().repaint();
		sourcePos.setCorki(null);
		sourcePos.getPosGraph().repaint();
		
		//kingfy the non-king
		if(!destnPos.getCorki().isKing()) {
			if(player.isOnTop() && destnPos.Y()==board.getNumColumns()-1) {
				destnPos.getCorki().setAsKing(true);
				if(newHistory != null) {
					mhist.setKingfied(true);

				}
			} else if(!player.isOnTop() && destnPos.Y()==0) {
				destnPos.getCorki().setAsKing(true);
				if(newHistory != null) {
					mhist.setKingfied(true);

				}
			}
		}
		
		//add new move history to new history list
		if(newHistory != null) {
			newHistory.add(mhist);

		}
		
	}
	
	public void eat(Position sourcePos, Position destnPos, ArrayList<moveHistory> newHistory) {
		move(sourcePos,destnPos,null);
		removeEatenCorkis(sourcePos,destnPos,newHistory);
	}
	private void removeEatenCorkis(Position sourcePos, Position destnPos, ArrayList<moveHistory> newHistory) {
		int x1 = sourcePos.X();
		int y1 = sourcePos.Y();
		int x2 = destnPos.X();
		int y2 = destnPos.Y();
				
		
		if(x1<x2) {
			if(y1<y2) {
				for(int i = x1; i<x2-1; i++) {
					++x1; ++y1;
					player.getOpponent().getCorkiList().remove(positions[x1][y1].getCorki());
					//register to history
					if(newHistory != null) {
						moveHistory mhist = new moveHistory(positions[x1][y1].getCorki());
						mhist.setRemoved(true);
						mhist.setPastPos(positions[x1][y1]);
						newHistory.add(mhist);
					}
					
					positions[x1][y1].setCorki(null);
					positions[x1][y1].getPosGraph().repaint();
				}
			} else {
				for(int i = x1; i<x2-1; i++) {
					++x1; --y1;
					player.getOpponent().getCorkiList().remove(positions[x1][y1].getCorki());
					//register to history
					if(newHistory != null) {
						moveHistory mhist = new moveHistory(positions[x1][y1].getCorki());
						mhist.setRemoved(true);
						mhist.setPastPos(positions[x1][y1]);
						newHistory.add(mhist);
					}
					positions[x1][y1].setCorki(null);
					positions[x1][y1].getPosGraph().repaint();

				}
			}
				
		} else {
			if(y1<y2) {
				for(int i = y1; i<y2-1; i++) {
					--x1; ++y1;
					player.getOpponent().getCorkiList().remove(positions[x1][y1].getCorki());
					//register to history
					if(newHistory != null) {
						moveHistory mhist = new moveHistory(positions[x1][y1].getCorki());
						mhist.setRemoved(true);
						mhist.setPastPos(positions[x1][y1]);
						newHistory.add(mhist);
					}
					positions[x1][y1].setCorki(null);
					positions[x1][y1].getPosGraph().repaint();

				}
			} else {
				for(int i = x1; i>x2+1; i--) {
				
					--x1;--y1;
					player.getOpponent().getCorkiList().remove(positions[x1][y1].getCorki());
					//register to history
					if(newHistory != null) {
						moveHistory mhist = new moveHistory(positions[x1][y1].getCorki());
						mhist.setRemoved(true);
						mhist.setPastPos(positions[x1][y1]);
						newHistory.add(mhist);
					}
					positions[x1][y1].setCorki(null);
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
	public  ArrayList<Position> getMiddlePoses(Position sourcePos, Position destnPos) {
		
		ArrayList<Position> backPoses = new ArrayList<Position>();
				
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

	public Position getEatingLandPos(Position depart, Position destin) {

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
	
	public  GameLoader.Result doYourTurn() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ArrayList<Position> getNextPossiblePositions(Corki corki) {
		ArrayList<Position> possiblePositions = new ArrayList<Position>();
		if(player.isTank()) {
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
				if(player.isOnTop()) {
					possiblePositions.addAll(getBottomLegalPositions(corki.getPos(),1));

				} else {
					possiblePositions.addAll(getTopLegalPositions(corki.getPos(),1));

				}
			}
		}
		return possiblePositions;
	}
	
	

	@SuppressWarnings("unused")
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


	
	private void repaintGlassPane() {
		glassPane.repaint();	
		glassPane.next.doClick();	
	}


	
	/**
	 * This method returns the list that contains the legal sequence
	 * of eating threads from a given position item
	 * @param corki
	 * @param secondPos 
	 * @return
	 */
	public ArrayList<ArrayList<Position>>  getAllLandingPoses(Corki corki, Position secondPos) {
		
		//initialize parameters and essential fields
		glassPane.arrowList = new ArrayList<Object[]>();
		ArrayList<Position> singleSeq = new ArrayList<Position>();
		ArrayList<Position> eatenList = new ArrayList<Position>();
		ArrayList<ArrayList<Position>> big = new ArrayList<ArrayList<Position>>();
		
		//add the first position to singleSeq
		singleSeq.add(corki.getPos());

		//just one last thing clear first item since it is moving
		final Position firstPos = corki.getPos();
		Corki fakeCorki = new Corki(corki.isTypeA(),corki.isKing(),
				new Position(corki.getPos().X(),corki.getPos().Y(),true));
		corki.getPos().setCorki(null);
		
		//make the call to fill big
		getEatablesSequentially(corki.getPos(),fakeCorki,singleSeq,eatenList,big,secondPos);


		//finally restore item's position
		firstPos.setCorki(corki);
		
		return big;
	}
	/**
	 * This method finds the edibles sequentially.
	 * @param oldBase
	 * @param newBase
	 * @param singleSeq
	 * @param eatenList
	 * @param secondPos 
	 * @param big 
	 * @param big
	 */
	public void getEatablesSequentially(Position oldBase, Corki newBase, 
			ArrayList<Position> singleSeq, ArrayList<Position> eatenList,
			ArrayList<ArrayList<Position>> big, Position secondPos) {
		
		//get the first level edibles
		ArrayList<Position> eatables = getNextEatables(newBase);
		
		//if edibles size is zero then add the singleSeq to big
		if(eatables.size() > 0){
			
			//edibles size not zero more work to do.
			boolean previous, repeated;
			Position eatenPos;
			Position nPos;
			
			for(int i=0; i<eatables.size(); i++) {
				
				nPos = eatables.get(i);
				eatenPos = getEatenPos(newBase.getPos(),nPos);
				
				previous = getMaBackSeriously(newBase.getPos(),oldBase).contains(nPos);
				repeated = eatenList.contains(eatenPos);
				
				//check terminating conditions
				if(!(previous || repeated)) {
					
					//glassPane repaint
					if(glassPane.isVisible()) {
						glassPane.addPoints(newBase.getPos(), nPos);
						repaintGlassPane();
					}
					//update eaten list
					ArrayList<Position> nEatenList = new ArrayList<Position>();
					nEatenList.addAll(eatenList);
					nEatenList.add(eatenPos);
					
					//update singleSeq
					ArrayList<Position> nSingleSeq = new ArrayList<Position>();
					nSingleSeq.addAll(singleSeq);
					nSingleSeq.add(nPos);
					
					//check for secondPos
					if((nPos.X() == secondPos.X()) && (nPos.Y() == secondPos.Y())) {
						big.add(nSingleSeq);
					}
					
					
					//do the recursive call with fake item
					Corki nNewBase = new Corki(newBase.isTypeA(),newBase.isKing(),
								new Position(nPos.X(),nPos.Y(),true));
						
					getEatablesSequentially(newBase.getPos(),nNewBase,nSingleSeq,nEatenList,big,secondPos);
			
						
					
					
				} 
				
			}
			
		}
		
		
	}
	private Position getEatenPos(Position p1, Position p2) {
		
		ArrayList<Position> middlePoses = getMiddlePoses(p1,p2);
		
		for(Position p: middlePoses) {
			if(p.hasCorki()) {
				return p;
			}
		}
		
		return null;
	}

	/**
	 * This method gets next level edibles
	 * @param corki
	 * @return
	 */
	private ArrayList<Position> getNextEatables(Corki corki) {

		//item can not be null
		if(corki == null ) {
			JOptionPane.showMessageDialog(null, "Null corki");
			return null;
		}
		
		//initialize container
		ArrayList<Position> nextEatables = new ArrayList<Position>();
		
		//Handle the king and tank case separately
		if(player.isTank() && corki.isKing()) {

			
			//getEatables which in turn calls getNextTankingLegals does the job for you.
			ArrayList<ArrayList<Position>> big = getEatables(corki);
			
			if(big.size() > 0) {
				for(int i=0; i<big.size(); i++) {
					
					//make sure inner list is not null
					if(big.get(i) != null) {
						
						for(int j=0; j<big.get(i).size(); j++) {
							nextEatables.add(big.get(i).get(j));

						}
					}
					
				}
			}
			
					
		} else {
			
			//get all next possible landing positions
			ArrayList<Position> possibles = getNextPossiblePositions(corki);
			
			
			//iterate through the possibles to get to the edible
			for(int i= 0; i<possibles.size(); i++) {
				
				
				Position posibItem = possibles.get(i);
				
				//if possible position doesn't have item skip it
				if((possibles.get(i).hasCorki()) ) {

					//make sure the item is of different kind
					if(corki.isTypeA() != posibItem.getCorki().isTypeA()) {


						//get the landing position
						Position landingPos = getEatingLandPos(corki.getPos(),posibItem);
						
						if(landingPos != null) {
							
							//make sure that landing position has no item
							if(!landingPos.hasCorki()) {


								if(posibItem.getCorki().isKing()) {

									//ordinary can't eat king in EGregna
									if(corki.isKing() || player.isTank()) {
			
										nextEatables.add(landingPos);
									}

								} else {
										

									nextEatables.add(landingPos);
									
								}
							}
						}
						
						
					}
				}
			}
		}
		return nextEatables;
	}
	
	public void drawTreeBasedOnLegalPos(Corki cork) {
		
		//first do the get next legals sequentially
		glassPane.setVisible(false);
		getNextLegalsSequentially(cork);
		
		glassPane.clearAll();
		glassPane.setVisible(true);
		drawTree(originLegalPos);
	}
	
	/**
	 * draws the legals tree solely fromlegalPositions list
	 * @param legPos
	 */
	public void drawTree(LegalPos legPos) {
		
		if(legPos.getNext().size()>0) {
			for(LegalPos pos:legPos.getNext()) {
				glassPane.addPoints(legPos.getPos(), pos.getPos());
				glassPane.repaint();
				JOptionPane.showMessageDialog(null, "ami ok?");
				drawTree(pos);
				
			}
			
		}
	
	
	}
	
	

	
	
	/**
	 * This method returns the next legal positions but does no linking
	 * it make indirect call to get all landing poses
	 * @param base
	 * @return list of next legal positions.
	 */
	public ArrayList<Position> getNextLegalsSequentially(Corki base) {
		
		//create new legalPositions list
		legalPositions = new ArrayList<LegalPos>();
		
		//declare legal list container
		ArrayList<Position> legalList = new ArrayList<Position>();
		
		ArrayList<Position> forceEatDestin = null;
		ArrayList<Position> teterByTeterRoute = null;

		
		//if it is force eat return only edibles
		if(player.isForceToEat()) {
			
			forceEatDestin = new ArrayList<Position>();
			
			//if non king by non king is first have a special list ready
			if(player.isTeterBeTeterFirst()) {
				teterByTeterRoute = new ArrayList<Position>();

			}
		}
		
		
		//get immediate adjacent if available
		ArrayList<Position> immAdj = getNextAdjacents(base);

		//add available adjacent to legal list
		if(immAdj.size()>0) {
			
			//add immAdj to legalPositions
			LegalPos legPos;
			for(Position p: immAdj) {
				
				legPos = new LegalPos(p,false);
				legalPositions.add(legPos);
				
			}
			
			legalList.addAll(immAdj);

		}
		
		ArrayList<Position> eatenList = new ArrayList<Position>();
		ArrayList<Position> eatables = getEatablesSequentially(base,eatenList);
		
        if(eatables.size()>0) {
        	
        	//add edibles to eat destination list
			for(Position p: eatables) {
				
				
				if (forceEatDestin != null) {
					forceEatDestin.add(p);
				
					if(teterByTeterRoute != null) {
						//gett the right landing
						for(LegalPos leg:originLegalPos.getNext()) {
							if(!leg.getImmediatePrey().getCorki().isKing()) {
							
								if(!teterByTeterRoute.contains(leg.getPos())) {
									teterByTeterRoute.add(leg.getPos());//add the immediate next

								}
								getForwardRoute(leg,teterByTeterRoute);
							}
						}
						
					}
				
			}
        }
			
        legalList.addAll(eatables);
        
        }



       if(teterByTeterRoute != null && teterByTeterRoute.size()>0) {
        	return teterByTeterRoute;
        } else  if(forceEatDestin != null && forceEatDestin.size()>0) {
        	return forceEatDestin;
        }
		return legalList;
	}
	
	
	/**
	 * a recursive method that adds next route to a storage
	 * @param leg
	 * @param storage
	 */
	private void getForwardRoute(LegalPos leg, ArrayList<Position> storage) {
		
		if(leg.getNext().size()>0) {
			for(LegalPos pos:leg.getNext()) {
				if(!storage.contains(pos.getPos())) {
					storage.add(pos.getPos());
					getForwardRoute(pos,storage);
				}
			
			}
		}
		
	}


	/**
	 * This method is similar to get all landing poses
	 * but simply returns list of all possible edible destinations
	 * @param pos
	 * @param base
	 * @param eatenList
	 * @return
	 */
	public ArrayList<Position> getEatablesSequentially(
			Corki base, ArrayList<Position> eatenList) {
				
			//initialize parameters and essential fields
			glassPane.arrowList = new ArrayList<Object[]>();
			ArrayList<Position> edibles = new ArrayList<Position>();
			
			//initialize origin LegalPos with the real position not fake.
			originLegalPos = new LegalPos(base.getPos(),false);

			//just one last thing clear first item since it is moving
			final Position firstPos = base.getPos();
			Corki fakeCorki = new Corki(base.isTypeA(),base.isKing(),
					new Position(base.getPos().X(),base.getPos().Y(),true));
			
			//do not repaint though.
			base.getPos().setCorki(null);
			
			//make the call to fill edibles
			getEdiblesSequentially(originLegalPos,fakeCorki,eatenList,edibles);


			//finally restore item's position
			firstPos.setCorki(base);
			
		    return edibles;
	}


	/**
	 * This method is similar to getEatablesSequentially except
	 * that it cares only in adding the legal edibles to 
	 * edibles list
	 * @param oldBase
	 * @param newBase
	 * @param eatenList
	 * @param edibles
	 */
	public void getEdiblesSequentially(LegalPos pastLegPos, Corki newBase,
			ArrayList<Position> eatenList, ArrayList<Position> edibles) {

		//get the first level edibles
		ArrayList<Position> eatables = getNextEatables(newBase);
		
		//if edibles size is zero then add the singleSeq to big
		if(eatables.size() > 0){
			
			//edibles size not zero more work to do.
			boolean previous, repeated;
			Position eatenPos;
			Position nPos;
			LegalPos newLegPos;
			
			for(int i=0; i<eatables.size(); i++) {
				nPos = eatables.get(i);
				eatenPos = getEatenPos(newBase.getPos(),nPos);
				
				previous = getMaBackSeriously(newBase.getPos(),pastLegPos.getPos()).contains(nPos);
				repeated = eatenList.contains(eatenPos);
				
				//check terminating conditions
				if(!(previous || repeated)) {
					
					//update legalPositions
					newLegPos = new LegalPos(nPos,true);
					pastLegPos.getNext().add(newLegPos);
					newLegPos.getPrevious().add(pastLegPos);
					newLegPos.setImmediatePrey(eatenPos);
					legalPositions.add(newLegPos);
					
					//add new position (if not repeated) in the edibles list
					if((edibles.size() == 0) || (!edibles.contains(nPos))) {
						edibles.add(nPos);
					}
					//glassPane repaint
					if(glassPane.isVisible()) {
						glassPane.addPoints(newBase.getPos(), nPos);
						repaintGlassPane();
					}
					//update eaten list
					ArrayList<Position> nEatenList = new ArrayList<Position>();
					nEatenList.addAll(eatenList);
					nEatenList.add(eatenPos);				
					
					
					//do the recursive call with fake item
					Corki nNewBase = new Corki(newBase.isTypeA(),newBase.isKing(),
								new Position(nPos.X(),nPos.Y(),true));
						
					getEdiblesSequentially(newLegPos,nNewBase,nEatenList,edibles);
	
				} 
				
			}
			
		}
		
		
	}


	/**
	 * @return the legalPositions
	 */
	public static ArrayList<LegalPos> getLegalPositions() {
		return legalPositions;
	}


	/**
	 * @return the originLegalPos
	 */
	public LegalPos getOriginLegalPos() {
		return originLegalPos;
	}


	/**
	 * This method gets the immediate adjacent of any item
	 * @param base
	 * @return
	 */
	public ArrayList<Position> getNextAdjacents(Corki base) {
		//Declare container for immediate adjacent
		ArrayList<Position> adjacents = new ArrayList<Position>();
		
		//if item is tank king handle differently
		if(player.isTank() && base.isKing() ) {
			
			//iterate through the legal positions in the 4 directions
			//pick only empty adjacent
			for(int i=1; i<=4; i++) {
				
				boolean adjacentEmpty = true;
				Position nextPos = null;
				int counter = 0;
				
				while(adjacentEmpty) {
					
					counter++;
					switch(i) {
					case 1: nextPos = getTopLegal(base.getPos(),true,counter);break;
					case 2: nextPos = getTopLegal(base.getPos(),false,counter);break;
					case 3: nextPos = getBotLegal(base.getPos(),true,counter);break;
					case 4: nextPos = getBotLegal(base.getPos(),false,counter);break;
					}
					
					if(nextPos == null) {
						adjacentEmpty = false;
					} else {
						
						if(nextPos.hasCorki()) {
							adjacentEmpty = false;
						} else {
							adjacents.add(nextPos);
							
						}
					}
				}
				
		    }
			
		} else { //handle the rest types
			
			//get immediate possibles
			ArrayList<Position> possibles = null;
			
			if(player.isTank() && !base.isKing()) {
				if(player.isOnTop()) {
					possibles = getBottomLegalPositions(base.getPos(),1);

				} else {
					possibles = getTopLegalPositions(base.getPos(),1);

				}
			} else {
	            possibles = getNextPossiblePositions(base);

			}
			
            //pick only those that don't have an item
			for(int i= 0; i<possibles.size(); i++) {
				
				if((!possibles.get(i).hasCorki()) ) {

						adjacents.add(possibles.get(i));
				} 
			}
		}
		
		return adjacents;
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
		

	
	@SuppressWarnings("unused")
	private Player getPlayerMatch(Corki corki, Player player1, Player player2) {
		return (corki.isTypeA() == player1.isTypeA() ? player1: player2);
		
	}
	
	public boolean undoMove() {
		
		if(undoIndex >= 0) {
			ArrayList<moveHistory> histList = undoList.get(undoIndex);
			
			//iterate over the list and undo
			for(int i = histList.size()-1; i >= 0; i--) {
				moveHistory move = histList.get(i);
				if(!move.isRemoved){
					move.getCork().getPos().setCorki(null);
					move.getCork().getPos().getPosGraph().repaint();
					if(move.isKingfied) {
						move.getCork().setAsKing(false);
					}
					move.getPastPos().setCorki(move.getCork());
					move.getPastPos().getPosGraph().repaint();
					
				} else {
					
					//note that the undo is done for the opponent last move
					if(move.isEfita) {
						player.getOpponent().getCorkiList().add(move.getCork());

					} else {
						player.getCorkiList().add(move.getCork());

					}
					move.getPastPos().setCorki(move.getCork());
					move.getPastPos().getPosGraph().repaint();
				}
				
				
			}
			
			undoList.remove(histList);
			undoIndex--;
			player.getOpponent().getMind().setUndoIndex(undoIndex);
			
		return true;//as long as index is not negative
		}
		
		return false;
		
	}

	public ArrayList<Corki> getMovableCorkis() {
		
		
		ArrayList<Corki> movableCorkis = new ArrayList<Corki>();
		
		ArrayList<Corki> predatorCorkis = null;
		ArrayList<Corki> teterByTeter = null;
		ArrayList<Corki> trulyTeterByTeter = null;
		ArrayList<Position> legals = null;
		boolean forceEat = player.isForceToEat();
		boolean tByT = player.isTeterBeTeterFirst();
				
		if(forceEat) {
			
			predatorCorkis = new ArrayList<Corki>();
		}
		
		if(tByT) {
			teterByTeter = new ArrayList<Corki>();
			trulyTeterByTeter = new ArrayList<Corki>();

		}
		
		
		
		for(Corki cork: player.getCorkiList()) {
			
			legals = getNextLegalsSequentially(cork);

			if(legals.size()>0) {  
				if(forceEat) {
					
					if(hasEatable()) {
						
						//add non king eater item
						if(!cork.isKing() && tByT ) {
							teterByTeter.add(cork);
							
							//add non king item that has non king item as edible
							if(originLegalPos.getNext().size()>0) {
								for(LegalPos leg: originLegalPos.getNext()) {
									if(!leg.getImmediatePrey().getCorki().isKing()) {
										trulyTeterByTeter.add(cork);
										break;
									}
								}
							}
						}
						predatorCorkis.add(cork);
					}

			    } 
				
				movableCorkis.add(cork);
		    
			
		   }
			
	    }
		
		if(tByT &&  trulyTeterByTeter.size()>0) {
			return trulyTeterByTeter;
		
		} else if(tByT &&  teterByTeter.size()>0) {
			return teterByTeter;
		
		} else if((predatorCorkis != null) && predatorCorkis.size()>0) {
			return predatorCorkis;
		} else {
			return movableCorkis;

		}
	}

	/**
	 * This method checks opponents items one by one to check
	 * for efita
	 */
	public void checkForEfita() {
		
		ArrayList<Corki> efitaList = new ArrayList<Corki>();
		
        
		//iterate through opponets corkis
		Player p = player.getOpponent();
		//put back just moved
		moveHistory oppsJustMoved = p.getMind().getJustMoved();
		Position currentPos = oppsJustMoved.getCork().getPos();
		currentPos.setCorki(null);
		oppsJustMoved.getPastPos().setCorki(oppsJustMoved.getCork());
		oppsJustMoved.setPastPos(currentPos);
		
		if(oppsJustMoved.isKingfied()) {
			oppsJustMoved.getCork().setAsKing(false);
		}
		
		for(Corki cork:p.getCorkiList()) {

			p.getMind().getNextLegalsSequentially(cork);
			
			//make sure there is at least one eatable
			if(p.getMind().hasEatable()) {

				if(player.isTank()) {

					efitaList.add(cork);
				} else {
					
					//special case if cork is king, 
					if(cork.isKing()) {
						

						//check that it has king eatable
						for(LegalPos leg:p.getMind().getOriginLegalPos().getNext()) {
							if(leg.getImmediatePrey().getCorki().isKing()) {
								efitaList.add(cork);
								break;
							}
						}
					} else {

						efitaList.add(cork);
					}
				}
				

			}
			
			
		}
		
		//now that you got the efita list, set back the just moved
		if(oppsJustMoved.isKingfied()) {
			oppsJustMoved.getCork().setAsKing(true);
		}
		Position currentPosi = oppsJustMoved.getCork().getPos();
		currentPosi.setCorki(null);
		oppsJustMoved.getPastPos().setCorki(oppsJustMoved.getCork());
		
		//get ready for eating the efita
		Corki finalEatable = null;
		
		if(!(efitaList.size()<=0)) {
			
			if(efitaList.size() == 1) {
				finalEatable = efitaList.get(0);
			} else if(efitaList.size()>0){
				finalEatable = getUserEfitaChoice(efitaList);
			}
			
		}
		
		//finalStep
		if(finalEatable != null) {
			eatEfitaSlowly(finalEatable);
		}
		
	}
	
	private void eatEfitaSlowly(Corki finalEatable) {
		//get pos graph
		PositionGraphics graph = finalEatable.getPos().getPosGraph();
		graph.setDrawText(true);
		graph.setText("EFITA");
		graph.repaint();
		
		int delay = 500;
		int numRepeats = 5;
		
		Timer t = null;
		EfitaHandler eh = new EfitaHandler(graph,numRepeats);
		t = new Timer(delay,eh);
		t.setRepeats(true);
		eh.setT(t);
		t.start();
		
	}
	
	class EfitaHandler implements ActionListener {
		
		//fields
		private PositionGraphics graph;
		private int counter;
		private int numRepeats;
		private Timer t;
		
		public EfitaHandler(PositionGraphics graph,int numRepeats) {
			this.graph = graph;
			this.numRepeats = numRepeats;
			this.counter = 0;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			counter++;
			if(counter>=numRepeats) {
				t.stop();
				PositionGraphics.textFont = null;
				PositionGraphics.textLocation = null;
				graph.setDrawText(false);
				graph.setText(null);
				graph.setCircled(false);
				
				//addto history as removed
				moveHistory mhist = new moveHistory(graph.getPos().getCorki());
				mhist.setPastPos(graph.getPos());
				mhist.setRemoved(true);
				mhist.setEfita(true);
				undoList.get(undoIndex).add(mhist);
				player.getOpponent().
				getCorkiList().remove(graph.getPos().getCorki());
				graph.getPos().setCorki(null);
				graph.repaint();
			} else {
				if(graph.isCircled()) {
					graph.setCircled(false);
				} else {
					graph.setCircled(true);
				}
				
				graph.repaint();

			}
			
		}

		/**
		 * @param t the t to set
		 */
		public void setT(Timer t) {
			this.t = t;
		}
	}


	private Corki getUserEfitaChoice(ArrayList<Corki> efitaList) {

		PositionGraphics posGraph = null;
		ArrayList<PositionGraphics> graphList = new ArrayList<PositionGraphics>();
		// first put a number on the corkis
		for(int i=0; i<efitaList.size();i++) {
			posGraph = efitaList.get(i).getPos().getPosGraph();
			graphList.add(posGraph);
			posGraph.setDrawText(true);
			posGraph.setText(""+i);
			posGraph.setCircled(true);
			posGraph.repaint();
		}
				
		int index = 0;
		
		JOptionPane pane = new JOptionPane("Enter the number of corki \n" +
				"you want to take as Efita");
		pane.setWantsInput(true);
		JDialog dialog = pane.createDialog("Efita Options");
		dialog.setLocation(0, 0);
		dialog.setVisible(true);
		String value = (String) pane.getInputValue();
		
		//check for error
		index = Integer.parseInt(value);
		boolean done = false;
		
		do {
			String in = null;
			if(!(index>=0 &&index<efitaList.size())) {
				in = JOptionPane.showInputDialog("Enter numbers between 0 and "+efitaList.size());
				index=Integer.parseInt(in);
			} else  {
				done= true;
			}

		}while(!done);
		
		//set posGraphs back;
		PositionGraphics.textFont = null;
		PositionGraphics.textLocation = null;
		for(PositionGraphics graph:graphList) {
			graph.setText(null);
			graph.setDrawText(false);
			graph.setCircled(false);
			graph.repaint();
		}
		return efitaList.get(index);
		
	}


	public boolean hasEatable() {
		for(LegalPos leg:originLegalPos.getNext()) {
			if(leg.isEatable()) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<Corki> getEfita() {
		
		ArrayList<Corki> efitaList = new ArrayList<Corki>();
		
		//iterate through opponets corkis
		Player p = player.getOpponent();
		for(Corki cork:p.getCorkiList()) {

			p.getMind().getNextLegalsSequentially(cork);
			
			//make sure there is at least one eatable
			if(p.getMind().hasEatable()) {

				if(player.isTank()) {

					efitaList.add(cork);
				} else {
					
					//special case if cork is king, 
					if(cork.isKing()) {
						

						//check that it has king eatable
						for(LegalPos leg:p.getMind().getOriginLegalPos().getNext()) {
							if(leg.getImmediatePrey().getCorki().isKing()) {
								efitaList.add(cork);
								break;
							}
						}
					} else {

						efitaList.add(cork);
					}
				}
				

			}
			
			
		}
		
		return efitaList;
	}


	public GameLoader.Result doYourTurn(PositionGraphics firstPosGraph,
			PositionGraphics secondPosGraph) {
		doYourTurn(firstPosGraph.getPos(),secondPosGraph.getPos());
		if(player.getOpponent().getCorkiList().size() == 0 || 
				player.getOpponent().getMovableCorkis().size() == 0) {
			result = GameLoader.Result.WIN;
		}
		return result;
	}
	
	/**
	 * This method performs the appropriate move based on 
	 * initial and last positions
	 * @param sourcePos the starting position
	 * @param destinPos landing position
	 */
	public void doYourTurn(Position sourcePos, Position destinPos) {
		
		//create a new moveHistory
		ArrayList<moveHistory> newHistory = new ArrayList<moveHistory>();
		
		//get the edible list
		ArrayList<Position> legals  = getNextLegalsSequentially(sourcePos.getCorki());

		
		//validate the parameters, may not be necessary under some circumstances
		boolean limitingCondition = sourcePos.hasCorki() && 
				player.getCorkiList().contains(sourcePos.getCorki())&&
				legals.contains(destinPos);
			
		if(limitingCondition) {
			
			//check if destinPos is edible
			if(!isEatable(destinPos)) {
				player.setJustAte(false);//forefitachecking
				
				//set just moved
				justMoved = new moveHistory(sourcePos.getCorki());
				justMoved.setPastPos(sourcePos);
				justMoved.setRemoved(false);
				
				boolean isKingdom = sourcePos.getCorki().isKing();
				//do the move
				move(sourcePos,destinPos,newHistory);
				
				if(!isKingdom && destinPos.getCorki().isKing()) {
					justMoved.setKingfied(true);
				}
				

			} else {
				
				//now get edible sequences
				ArrayList<ArrayList<Position>> big = getAllLandingPoses(sourcePos.getCorki(),destinPos);
		        int index = 0;
				
		        //if big's size is greater than one then let the user choose the one.
		        if(big.size() >1) {		        	
		        	index = getIndexFromUser(big);			        
		        } 
		        
		        if(index==-1) {
		        	result = GameLoader.Result.KEEP_TURN;
		        	return;
		        }
		        //now it is time to eat
		        ArrayList<Position> edibleList = big.get(index);
		        
		        //add new history move
		        Corki movingCork = edibleList.get(0).getCorki();
		        moveHistory mhist = new moveHistory(movingCork);
		        mhist.setPastPos(edibleList.get(0));
		        
		        boolean isKing = movingCork.isKing();

		        
		        for(int i=0; i<edibleList.size()-1; i++) {
		        	
		        	//do efita helper
		        	player.setJustAte(true);
		        	
		        	//eat
		        	eat(edibleList.get(i),edibleList.get(i+1),newHistory);
		        }
		        

		        if(!isKing) {
		        	if(movingCork.isKing()) {
		        		mhist.setKingfied(true);
		        	}
		        }
		        
		        newHistory.add(mhist);
		        
		       
		        
		        
			}
			//add new history to undo list
			if(newHistory.size()>0){
				undoList.add(newHistory);
				undoIndex = undoList.size()-1;
				player.getOpponent().getMind().setUndoIndex(undoIndex);
			}
		
			//set valid move result
	        result = GameLoader.Result.VALID_MOVE;
			
		}
			
		
	}


	/**
	 * @param big
	 */
	public int getIndexFromUser(ArrayList<ArrayList<Position>> big) {
		//declare variables
		JOptionPane pane;
		String options[];
		int index = 0;	
		boolean userDecided = false;
		
		glassPane.setVisible(true);//make sure glassPane is visible

		do{
			
			//clear previous points and repaint
			glassPane.clearAll();
			glassPane.repaint();
			
			//add points sequentially to glassPane
		    for(int i=0; i<big.get(index).size(); i++) {
		    	
		    	//stop one before last index
				if(i!=(big.get(index).size()-1)) {
					glassPane.addPoints(big.get(index).get(i)
					, big.get(index).get(i+1));
				} 		
		       
		    }
		    
		    //repaint
		    glassPane.repaint();
		    
		  //initialize options appropriately based on the value of index
			if(index==0) {
				options = new String[3];
				options[0] = "OK";
				options[1] = "SeeNextOption";
				options[2] = "Cancel";

			} else if(index==(big.size()-1)) {
				options = new String[3];
				options[0] = "BACK";
				options[1] = "OK";
				options[2] = "Cancel";

			} else {
				options = new String[4];
				options[0] = "BACK";
				options[1] = "OK";
				options[2] = "SeeNextOption";
				options[3] = "Cancel";

			}
			
			//create the pane based on the options
			pane = new JOptionPane("Do you want to eat this way?"+("\n option:"+index),
					JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION,
					null,options);
			pane.setOpaque(true);
			pane.setBackground(Color.RED);
			
			//show the pane
			JDialog dialog = pane.createDialog(null,
					"MultipleOptions");
			dialog.setLocation(0, 0);
			dialog.setVisible(true);
			
			//get selected value
			String selectedValue = (String)pane.getValue();
			
			//adjust the value of index appropriately based on selected
			//value
			if(selectedValue == null) {
				//clear previous points and repaint
				glassPane.clearAll();
				glassPane.repaint();
				glassPane.setVisible(false);

				return -1;
			}
			if(selectedValue.equalsIgnoreCase("OK")) {
				userDecided = true;
			} else if(selectedValue.equalsIgnoreCase("BACK")) {
				index--;
			} else if(selectedValue.equalsIgnoreCase("SeeNextOption")) {
				index++;
			}  else if(selectedValue.equalsIgnoreCase("Cancel")) {
				//clear previous points and repaint
				glassPane.clearAll();
				glassPane.repaint();
				glassPane.setVisible(false);

				return -1;
			}
		    
		} while(!userDecided);
		
		//clear previous points and repaint
		glassPane.clearAll();
		glassPane.repaint();
		glassPane.setVisible(false);
		
		return index;
	}
	
	public class LegalPos {
		
		private boolean eatable;
		private Position pos;
		private ArrayList<LegalPos> next;
		private ArrayList<LegalPos> previous;
		private Position immediatePrey; 

		
		public LegalPos(Position pos, boolean eatable) {
			this.pos = pos;
			this.eatable = eatable;
			this.next = new ArrayList<LegalPos>();
			this.previous = new ArrayList<LegalPos>();

		}
		
		/**
		 * @return the immediatePrey
		 */
		public Position getImmediatePrey() {
			return immediatePrey;
		}

		/**
		 * @param immediatePrey the immediatePrey to set
		 */
		public void setImmediatePrey(Position immediatePrey) {
			this.immediatePrey = immediatePrey;
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
	
	/**
	 * for undo purpose
	 * @author me
	 *
	 */
	class moveHistory {
		
		//fields
		private Corki cork;
		private boolean isRemoved;
		private boolean isKingfied;
		private boolean isEfita;
		/**
		 * @return the isEfita
		 */
		public boolean isEfita() {
			return isEfita;
		}

		/**
		 * @param isEfita the isEfita to set
		 */
		public void setEfita(boolean isEfita) {
			this.isEfita = isEfita;
		}

		private Position pastPos;
		
		public moveHistory(Corki corki) {
			cork = corki;
		}

		/**
		 * @return the cork
		 */
		public Corki getCork() {
			return cork;
		}

		/**
		 * @param cork the cork to set
		 */
		public void setCork(Corki cork) {
			this.cork = cork;
		}

		/**
		 * @return the isRemoved
		 */
		public boolean isRemoved() {
			return isRemoved;
		}

		/**
		 * @param isRemoved the isRemoved to set
		 */
		public void setRemoved(boolean isRemoved) {
			this.isRemoved = isRemoved;
		}

		/**
		 * @return the isKingfied
		 */
		public boolean isKingfied() {
			return isKingfied;
		}

		/**
		 * @param isKingfied the isKingfied to set
		 */
		public void setKingfied(boolean isKingfied) {
			this.isKingfied = isKingfied;
		}

		/**
		 * @return the pastPos
		 */
		public Position getPastPos() {
			return pastPos;
		}

		/**
		 * @param pastPos the pastPos to set
		 */
		public void setPastPos(Position pastPos) {
			this.pastPos = pastPos;
		}
	}

}
