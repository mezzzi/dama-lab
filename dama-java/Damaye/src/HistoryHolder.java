import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class HistoryHolder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6898481374242123196L;
	private ArrayList<History> historyList;
	
	public HistoryHolder() {
		historyList = new ArrayList<History>();
	}
	/**
	 * @return the historyList
	 */
	public ArrayList<History> getHistoryList() {
		return historyList;
	}

	/**
	 * @param historyList the historyList to set
	 */
	public void setHistoryList(ArrayList<History> historyList) {
		this.historyList = historyList;
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
		nh.setP2Status("bababbaba");
		
		ArrayList<History> historyList = new ArrayList<History>();
		historyList.add(nh);
		
		HistoryHolder holder = new HistoryHolder();
		holder.setHistoryList(historyList);
		
		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("myHistHold.ser"));
			output.writeObject(holder);
			System.out.println("written sucessfully");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
