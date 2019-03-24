import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class History implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1241913959222801140L;
	//fields
	private String date;
	private String players;
	private String winner;
	private String gameType;
	private String p1Status;
	private String p2Status;
	
	public History() {
		date = null;
		players = null;
		winner = null;
		gameType = null;
		p1Status = null;
		p2Status = null;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the players
	 */
	public String getPlayers() {
		return players;
	}
	/**
	 * @param players the players to set
	 */
	public void setPlayers(String players) {
		this.players = players;
	}
	/**
	 * @return the winner
	 */
	public String getWinner() {
		return winner;
	}
	/**
	 * @param winner the winner to set
	 */
	public void setWinner(String winner) {
		this.winner = winner;
	}
	/**
	 * @return the gameType
	 */
	public String getGameType() {
		return gameType;
	}
	/**
	 * @param gameType the gameType to set
	 */
	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	/**
	 * @return the p1Status
	 */
	public String getP1Status() {
		return p1Status;
	}
	/**
	 * @param p1Status the p1Status to set
	 */
	public void setP1Status(String p1Status) {
		this.p1Status = p1Status;
	}
	/**
	 * @return the p2Status
	 */
	public String getP2Status() {
		return p2Status;
	}
	/**
	 * @param p2Status the p2Status to set
	 */
	public void setP2Status(String p2Status) {
		this.p2Status = p2Status;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		History nh = new History();
		nh.setDate("Today");
		nh.setGameType("Egregana");
		nh.setPlayers("UsAll");
		nh.setWinner("Me");
		nh.setP1Status("loser");
		nh.setP2Status("winner");
		
		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("myHist.ser"));
			output.writeObject(nh);
			output.close();
			System.out.println("written sucessfully");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
