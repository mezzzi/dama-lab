
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Scanner;
import javax.swing.*;

/**
 * This class represents the graphical look and dimension of position object and the 
 * item on it.
 * @author mzm002
 *
 */
public class PositionGraphics extends JButton{

	/**
	 * whatever
	 */
	private static final long serialVersionUID = 1L;
	
	//non static field
	private Position pos;
	private Color backGroundColor;
	private boolean drawText;
	private String text;
	private boolean isCircled;

	public static Font textFont;
	public static Component superParent;
	public static boolean superNeedRepaint;

	/**
	 * @param backGroundColor the backGroundColor to set
	 */
	public void setBackGroundColor(Color backGroundColor) {
		this.backGroundColor = backGroundColor;
	}


	//static fields
	public static boolean isSquareRaised;
	public static Color illegalColor, legalColor, typeAColor, typeBColor, starColor, strokeColor, borderColor;
	
	public static boolean isStarFilled;
	public static double diam_Ratio;
	public static double innerDiam_Ratio;
	public static double stroke_Width;
	public static boolean allowStroke;
	public static Point numberLocation;
	public static Point textLocation;
	
	
	/**
	 * The real constructor
	 * @param pos
	 */
	public PositionGraphics(Position pos) {
		this.pos = pos;
		try {
			setDefaultParams();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setDoubleBuffered(true);
	}  
	
	//set fields to default, except the pos field
	public PositionGraphics() {
		//in case reading from a file fails, have your own reserve defaults
		try {
			setDefaultParams();
		} catch (Exception e) {
			illegalColor = Color.orange;
			legalColor = Color.gray;
			typeAColor = Color.magenta;
			typeBColor = Color.red;
			starColor = Color.black;
			borderColor =Color.green;
			diam_Ratio = 3.0/5.0;
			stroke_Width = 0;
			innerDiam_Ratio = 0.8;
			strokeColor = Color.black;
			isStarFilled = true;
			allowStroke = true;
			isSquareRaised = true;
			out("something wrong with the default file");
			e.printStackTrace();
		}
		

	}
	


	/**
	 * Strictly assumes the format is strictly followed.
	 */
	public void setDefaultParams() throws Exception {
		Scanner sc = null;
		String next = "";
		
		Color[] colorArray = new Color[7];
		
			sc = new Scanner(new File("default.txt"));
			
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
			
			illegalColor = colorArray[0];
			legalColor = colorArray[1];
			typeAColor =  colorArray[2];
			typeBColor =  colorArray[3];
			starColor =  colorArray[4];
			strokeColor =  colorArray[5];
			borderColor =  colorArray[6];
			diam_Ratio = sc.nextDouble();
			innerDiam_Ratio = sc.nextDouble();
			stroke_Width = sc.nextDouble();
			isStarFilled = (sc.nextInt() ==1);
			isSquareRaised = (sc.nextInt() ==1);
			allowStroke = (sc.nextInt() ==1);
			backGroundColor = null;


		
		
	}

	private void out(String string) {
		System.out.println(string);
		
	}


	


	/**
	 * @return the isCircled
	 */
	public boolean isCircled() {
		return isCircled;
	}

	/**
	 * @param isCircled the isCircled to set
	 */
	public void setCircled(boolean isCircled) {
		this.isCircled = isCircled;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}


	/**
	 * @param drawText the drawText to set
	 */
	public void setDrawText(boolean drawText) {
		this.drawText = drawText;
	}

	//paint position appropriately
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g1 = (Graphics2D)g;
		
		//smooth lines
		g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//increase quality
		g1.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		Stroke stroke = new BasicStroke((float) stroke_Width,BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		
		//containers dimension
		double frameWidth = getWidth();
		double frameHeight = getHeight();
		
		//Calculate oval diameter
		double diam = diam_Ratio*Math.min(frameWidth, frameHeight);
		
		//calculate top left corner position so that the oval will be centered
		double x_coor = (frameWidth-diam)/2.0;
		double y_coor = (frameHeight-diam)/2.0;
		
		//create the oval
		Ellipse2D.Double corki = new Ellipse2D.Double(x_coor,y_coor, diam, diam);
		
		//create the main rectangle /square
		Rectangle2D.Double backGround = new Rectangle2D.Double(0,0, frameWidth, frameHeight);
		
		if(!pos.isLegal()) {
			
			//draw the illegal positions background
			
			g1.setColor((backGroundColor == null) ? illegalColor : backGroundColor);
			if(isSquareRaised) {
				g1.fill3DRect((int)backGround.x, (int)backGround.y, (int)backGround.width, (int) backGround.height, true);

			} else {
				g1.fill(backGround);

			}
			

			
		} else {
			
			//draw the legal positions background
			g1.setColor((backGroundColor == null) ? legalColor : backGroundColor);
			if(isSquareRaised) {
				g1.fill3DRect((int)backGround.x, (int)backGround.y, (int)backGround.width, (int) backGround.height, true);

			} else {
				g1.fill(backGround);


			}
			
			
			if(pos.hasCorki()) {
				
				if(pos.getCorki().isTypeA()) {
					
					//fill ordinary Comps corki on the to
					g1.setColor(typeAColor);
					fillStar(g1, stroke, frameWidth, frameHeight, diam, corki); 
					
				} else {
					
					//fill ordinary Humns corki on the top
					g1.setColor(typeBColor);
					fillStar(g1, stroke, frameWidth, frameHeight, diam, corki); 
				}
			}
			
		}
		

		if(drawText) {
			if(textLocation == null) {
				setTextLocation(g1,backGround,diam);
			}
			
			Color color = pos.getCorki().isTypeA() ? typeAColor:typeBColor;
			
			g1.setColor(color.brighter().brighter().brighter());
			g1.setFont(textFont);
			g1.drawString(text, textLocation.x, textLocation.y);
		}
		
		if(isCircled) {
			double offset = diam/50.0;
			g1.setStroke(new BasicStroke((float) (2*offset)));
			Color color = pos.getCorki().isTypeA() ? typeAColor:typeBColor;
			g1.setColor(color.brighter().brighter().brighter().brighter());
			Ellipse2D.Double outerCircle = new Ellipse2D.Double(x_coor-offset, y_coor-offset, diam+2*offset, diam+2*offset);
			g1.draw(outerCircle);
		}
			
		if(superNeedRepaint && superParent != null) {
			superParent.repaint();
		}
	}




	private void setTextLocation(Graphics2D g1, Rectangle2D.Double backGround, double diam) {
			
			String style  = Font.MONOSPACED;
			textFont = new Font(style,Font.BOLD,15);
			
			boolean bigEnough = false;
			int fontSize = 15;
			FontMetrics metrics = null;
			
			while(!bigEnough ) {
				fontSize++;
				textFont = new Font(style,Font.BOLD,fontSize);
				g1.setFont(textFont);
				metrics = g1.getFontMetrics(textFont);

				if(metrics.stringWidth(text)>=diam || 
						metrics.getAscent()>=diam) {
					bigEnough = true;
				}
				
			}
			textFont = new Font(style,Font.BOLD,fontSize-4);
			metrics = g1.getFontMetrics(textFont);
			
			int height = metrics.getAscent();
			int width = metrics.stringWidth(text);
			
			//I added some random offsets
			int x= (int) (((this.getWidth()-width)/2));
			int y = (int) ((this.getHeight()-height)/2+height*0.8);
			
			textLocation = new Point(x,y);
			
			
		
		
	}

	/**
	 * @param g1
	 * @param stroke
	 * @param frameWidth
	 * @param frameHeight
	 * @param diam
	 * @param corki
	 */
	private void fillStar(Graphics2D g1, Stroke stroke, double frameWidth,
			double frameHeight, double diam, Ellipse2D.Double corki) {
		
		
		g1.fill(corki);
		drawStroke(g1,stroke,corki);
		

		if(pos.getCorki().isKing()) {
			
		
			Polygon star = new Star(frameWidth/2, frameHeight/2, diam*innerDiam_Ratio).getStar();
			drawStroke(g1,stroke,star);
			if(isStarFilled) { 
				g1.setColor(starColor);
				g1.fill(star);
				
			}
			
		}
	}

	/**
	 * draws stroke
	 * @param g1
	 * @param stroke
	 * @param s
	 */
	public void drawStroke(Graphics2D g1,Stroke stroke, Shape s ) {
		if(allowStroke) {
			g1.setColor(strokeColor);
			g1.setStroke(stroke);	
			g1.draw(s);
		}
	}
	/**
	 * @return the pos
	 */
	public Position getPos() {
		return pos;
	}

	/**
	 * @param pos the pos to set
	 */
	public void setPos(Position pos) {
		this.pos = pos;
	}

	

	/**
	 * for testing purpose
	 * @param args
	 */
	public static  void main(String[] args) {
		
        //Create and set up the window.  
        JFrame frame = new JFrame("FreshDamaSpot");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Position pos = new Position(3,4, true);
        Corki corcki = new Corki(true, true, pos);
        
        System.out.println(corcki.toString());
        
        PositionGraphics graph = new PositionGraphics(pos);
        
        PositionGraphics.innerDiam_Ratio = 0.8;
        PositionGraphics.stroke_Width = 0;
        PositionGraphics.allowStroke = false;
        PositionGraphics.diam_Ratio = (4/5.0);
        PositionGraphics.starColor = Color.RED;
        
        graph.setDrawText(true);
        graph.setText("EFITA");
        graph.setCircled(true);
        
        graph.repaint();
 
        JButton button = new JButton();
        button.add(graph);
        
        frame.add(graph);
        //Display the window.
        frame.setVisible(true);
    }

	
	public void brighten(boolean b) {
		
		if(backGroundColor == null) {
			if(pos.isLegal()) {
				backGroundColor = legalColor;
			} else {
				backGroundColor = illegalColor;

			}
		}
		
		if(b) {
			
			backGroundColor = backGroundColor.darker();

		} else {
			backGroundColor = backGroundColor.brighter();

		}
		
		repaint();
	}


	/**
	 * This class constructs a star object.
	 * @author mzm002
	 *
	 */
	public class Star {

		//fields
		private double x_Center;
		private double y_Center;
		private double inner_Radius;
		private double outer_Radius;
		
		private final double FIBNOCI_FACTOR = 5.0/13.0; //haha it took a while to find this factor
		

		//constructor
		public Star(double x_Center, double y_Center, double outer_Diam) {
			this.x_Center = x_Center;
			this.y_Center = y_Center;
			this.inner_Radius = FIBNOCI_FACTOR*outer_Diam/2;
			this.outer_Radius = outer_Diam/2;
		}

		//gets inner points on the inner circle
		private int[][] getInnerPoints() {
			int[][] innerPoints = new int[2][5];
			double angle = 0.0;
			for (int i=0; i<5; i++) {
				innerPoints[0][i] = (int) (x_Center+inner_Radius*Math.cos(angle));
				innerPoints[1][i] = (int) (y_Center-inner_Radius*Math.sin(angle));
				angle += (2*Math.PI/5);
			}
			

			return innerPoints;
		}
		
		//gets outer points on the outer circle
		private int[][] getOuterPoints() {
			
			int[][] outerPoints = new int[2][5];
			double angle = Math.PI/5;
			for (int i=0; i<5; i++) {
				outerPoints[0][i] = (int) (x_Center+outer_Radius*Math.cos(angle));
				outerPoints[1][i] = (int) (y_Center-outer_Radius*Math.sin(angle));
				angle += (2*Math.PI/5);
			}
			
			return outerPoints;
		}
		
		//merge inner and outer points
		public int[][] getMergedPoints( ) {
			
			int[][] mergedPoints = new int[2][10];
			
			int[][] innerPoints = getInnerPoints();
			int[][] outerPoints = getOuterPoints();
			
			int mergeIndex = 0;
			
			if(innerPoints.length != outerPoints.length) {
				out("lengths of inner and outer points doesn't match");
			}
			
			for (int i=0; i<5; i++) {
				
				mergedPoints[0][mergeIndex] = innerPoints[0][i];
				mergedPoints[1][mergeIndex] = innerPoints[1][i];
				
				mergedPoints[0][++mergeIndex] = outerPoints[0][i];
				mergedPoints[1][mergeIndex] = outerPoints[1][i];
				
				mergeIndex++;
				
			}
			return mergedPoints;
		}
		
		//returns the star as polygon
		public Polygon getStar() {
			
			int[][] mergedPoints = getMergedPoints( );
			Polygon poly = new Polygon(mergedPoints[0], mergedPoints[1], 10);
			
			return poly;
		}
		

		//helper
		private void out(String string) {
			System.out.println(string);
			
		}


	}

}
