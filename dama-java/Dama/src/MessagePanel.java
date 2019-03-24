import java.awt.*;
import javax.swing.*;

public class MessagePanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String firstMessage;
	private String secondMessage;
	private Color firstColor;
	private Color secondColor;
	private Font font;
	private Dimension d;
	private Graphics2D g1;
	private FontMetrics metrics;
	private boolean needOffset;
	
	public MessagePanel(String firstMessage, String secondMessage,
			Color firstColor,Color secondColor,Dimension d,boolean needOffset) {
		
		this.firstMessage = firstMessage;
		this.secondMessage = secondMessage;
		this.firstColor = firstColor;
		this.secondColor = secondColor;
		this.d = d;
		this.needOffset = needOffset;
		
		setSize(d);
		setDoubleBuffered(true);
		
	}
	



	public Dimension getPreferredSize() {
		return d;
	}
	
	public Dimension getMinimumSize() {
		return d;
	}
	
	public Dimension getMaximumSize() {
		return d;
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponents(g);
				
		g1 = (Graphics2D) g;
		
		//smooth lines
		g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//increase quality
		g1.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		setFont();
		g1.setFont(font);
		g1.setColor(firstColor);
		int[] coors = getCoors();
		g1.drawString(firstMessage, coors[0], coors[1]);
		g1.setColor(secondColor);
		g1.drawString(secondMessage, coors[2], coors[3]);
	}


	/**
	 * computes the coordinates of the first and second string
	 * @return
	 */
	private int[] getCoors(){
		
		FontMetrics metrics = g1.getFontMetrics(font);
		int firstWidth = metrics.stringWidth(firstMessage);
		int secondWidth = metrics.stringWidth(secondMessage);
		boolean isFirstMax = firstWidth > secondWidth;
		int width = isFirstMax?firstWidth:secondWidth;
		int height = metrics.getHeight()*2;
		
		int x = (int) ((d.getWidth()-width)/2.0);
		int y = (int) ((d.getHeight()-height)/2.0);
		
		int xOffset = Math.abs(firstWidth-secondWidth)/2;
		int randOffset = (int) (d.getHeight()/16);
		int x1 = isFirstMax?x:(x+xOffset);
		int y1 = y+height/2;
		int x2 = isFirstMax?(x+xOffset):x;
		int y2 = y+height;
		
		if(needOffset) {
			y1 -= randOffset;
			y2 -= randOffset;
		}
		
		return new int[] {x1,y1,x2,y2};
		
	}

	private void setFont() {
		
		String style  = Font.MONOSPACED;
		font = new Font(style,Font.BOLD,15);
		
		boolean bigEnough = false;
		int fontSize = 15;
		
		while(!bigEnough ) {
			fontSize++;
			font = new Font(style,Font.BOLD,fontSize);
			metrics = g1.getFontMetrics(font);

			if(Math.max(metrics.stringWidth(firstMessage), 
					metrics.stringWidth(secondMessage))>=d.getWidth() || 
					(2*metrics.getHeight())>=d.getHeight()) {
				bigEnough = true;
			}
			
		}
		font = new Font(style,Font.BOLD,fontSize-10);
	}
	

	public static void main(String[] args) {
		JDialog dialog = new JDialog();
		dialog.setSize(200, 200);
		Dimension d = new Dimension(200,100);

		MessagePanel msg = new MessagePanel("Hello","Abebe",Color.RED,Color.BLUE,d,false);
		
		dialog.add(msg,BorderLayout.CENTER);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		
	}
}
