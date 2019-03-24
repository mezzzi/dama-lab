import java.awt.*;
import java.awt.event.WindowListener;

import javax.swing.*;

public class Message extends JComponent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private Color color;
	private Font font;
	private Dimension d;
	private Graphics2D g1;
	private FontMetrics metrics;
	private double divisor;
	
	public Message(String message, Color color, Dimension d,double divisor) {
		this.message = message;
		this.color = color;
		this.d = d;
		this.divisor = divisor;
	}
	

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		g1 = (Graphics2D) g;
		setFont();
		g1.setFont(font);
		g1.setColor(color);
		g1.drawString(message, getXCoor(), getYCoor());
	}

	private int getXCoor() {
		
		return (getWidth()-g1.getFontMetrics().stringWidth(message))/2;
	}


	private int getYCoor() {

		return (int)(getHeight()-(getHeight()-g1.getFontMetrics().getHeight())/divisor);
	}


	private void setFont() {
		font = new Font(Font.DIALOG,Font.BOLD,15);
		
		boolean bigEnough = false;
		int fontSize = 15;
		
		while(!bigEnough ) {
			fontSize++;
			font = new Font(Font.MONOSPACED,Font.BOLD,fontSize);
			metrics = g1.getFontMetrics(font);

			if(metrics.stringWidth(message)>=d.getWidth() || metrics.getHeight()>=d.getHeight()) {
				bigEnough = true;
			}
			
		}
		font = new Font(Font.MONOSPACED,Font.BOLD,fontSize-4);
	}
	

	public static void main(String[] args) {
		JDialog dialog = new JDialog();
		dialog.setSize(200, 200);
		Dimension d = new Dimension(200,100);
		Dimension d1 = new Dimension(100,40);

		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.cyan);
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setBackground(Color.cyan);
		panel1.setSize(d1);
		Message msg = new Message("Hello",Color.BLUE,d,4);
		Message msg1 = new Message("Mezigebu",Color.RED,d,2);

		panel.add(msg);
		panel1.add(msg1);
		
		JPanel panel3 = new JPanel(new GridLayout(0,1));
		panel3.setSize(d);

		panel3.add(panel);
		
		panel3.add(panel1);

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.add(panel3,BorderLayout.CENTER);
		dialog.addWindowListener((WindowListener) new GameLoader());
		dialog.setVisible(true);
		
	}
}
