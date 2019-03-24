
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Scanner;
import javax.swing.*;

/**
 * This class represents the graphical look and dimension of position object
 * @author mzm002
 *
 */
public class PositionGraphics extends JComponent{

	/**
	 * whatever
	 */
	private static final long serialVersionUID = 1L;
	
	//non static field
	private Position pos;

	private Color backGroundColor;

	//static fields
	public static boolean isSquareRaised;
	public static Color illegalColor, legalColor, typeAColor, typeBColor, starColor, strokeColor, borderColor;
	
	public static boolean isStarFilled;
	public static double diam_Ratio;
	public static double innerDiam_Ratio;
	public static double stroke_Width;
	public static boolean allowStroke;
	
	
	/**
	 * The real constructor
	 * @param pos
	 */
	public PositionGraphics(Position pos) {
		this.pos = pos;
		try {
			setDefaultParams();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	private void setDefaultParams() throws Exception {
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
			
			// prevent resource leak
			sc.close();


		
		
	}

	private void out(String string) {
		System.out.println(string);
		
	}


	
	//paint position appropriately
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g1 = (Graphics2D)g;
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
			
			backGroundColor = backGroundColor.brighter().brighter();
			
		} else {
			backGroundColor = backGroundColor.darker().darker();
		
		}
		
		repaint();
	}



}
