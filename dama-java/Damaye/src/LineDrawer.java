import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;

public class LineDrawer {

	public class GlassPane extends JComponent{
		
		
		private static final long serialVersionUID = 1L;
		
		private ArrayList<Point> firstPoints;
		private ArrayList<Point> secondPoints;
		
		public ArrayList<Object[]> arrowList;
		
		private ArrowCalculator arrowCalculator;
		private JPanel panel;
		
		public BasicArrowButton next;
		
		
		public GlassPane(JPanel panel) {
			
			this.panel = panel;
			firstPoints = new ArrayList<Point>();
			secondPoints = new ArrayList<Point>();
			arrowList = new ArrayList<Object[]>();
			arrowCalculator = new ArrowCalculator();

			
			
		}
	
		public void addPoints(Position firstPos, Position secondPos) {
			
			int posWidth = (int)(panel.getWidth()/8.0);
			int posHeight = (int)(panel.getHeight()/8.0);
			
			int x1 = (int)((firstPos.X()+.5)*posWidth);
			int x2 = (int)((secondPos.X()+.5)*posWidth);
			
			int y1 = (int)((firstPos.Y()+.5)*posHeight);
			int y2 = (int)((secondPos.Y()+.5)*posHeight);
			
			firstPoints.add(SwingUtilities.convertPoint(panel, x1, y1, getRootPane()));
			secondPoints.add(SwingUtilities.convertPoint(panel, x2, y2, getRootPane()));

		}
		
		public void clearAll() {
			firstPoints = new ArrayList<Point>();
			secondPoints = new ArrayList<Point>();
			arrowList = new ArrayList<Object[]>();
		}
		
		public void paintComponent(Graphics g) {
			
			setDoubleBuffered(true);
			
			super.paintComponent(g);
			
			Graphics2D g1 = (Graphics2D)g;
			
			//smooth lines
			g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//increase quality
			g1.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
			
			arrowList = new ArrayList<Object[]>();
			for(int i=0; i<firstPoints.size(); i++) {
				arrowList.add(arrowCalculator.getArrow(firstPoints.get(i), secondPoints.get(i)));
			}
			for(int i=0; i<arrowList.size(); i++) {
				g1.setColor(Color.GREEN);
				g1.fillOval(firstPoints.get(i).x-10, firstPoints.get(i).y-10, 20, 20);
				g1.setColor(Color.BLACK);
				g1.setStroke(new BasicStroke(5));
				g1.draw((Line2D.Double) arrowList.get(i)[0]);
				
				g1.setColor(Color.BLUE);
				g1.setStroke(new BasicStroke(1));
				g1.fillPolygon((Polygon) arrowList.get(i)[1]);
			}
			

			
		}

	}
	
	public class ArrowCalculator {
		
		public Object[] getArrow(Point pos1, Point pos2) {
			
			double x1 = pos1.x;
			double y1 = pos1.y;
			double x2 = pos2.x;
			double y2 = pos2.y;
			
			double x_c = Math.min(x1, x2)+Math.abs(x1-x2)/2.0;
			double y_c = Math.min(y1, y2)+Math.abs(y1-y2)/2.0;
			
			x_c = Math.min(x_c, x2)+Math.abs(x_c-x2)/2.0;
			y_c = Math.min(y_c, y2)+Math.abs(y_c-y2)/2.0;
			
			double hl = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))/2.0;
			double qrt = hl/4.0;
			
			double al = Math.atan((y2-y_c)/(x2-x_c))+(Math.PI/8.0);
			double ar = Math.atan((y2-y_c)/(x2-x_c))-(Math.PI/8.0);
			
			double xl = x_c+qrt*Math.cos(al);
			double yl = y_c+qrt*Math.sin(al);
			
			double xr = x_c+qrt*Math.cos(ar);
			double yr = y_c+qrt*Math.sin(ar);
			
			
			Line2D.Double line = new Line2D.Double(x1, y1, x_c, y_c);
			
			int x[]= {(int) x_c,(int) xl,(int) x2,(int) xr};
			int y[]= {(int) y_c,(int) yl,(int) y2,(int) yr};
			
			Polygon arrowHead = new Polygon(x,y,4);
			
			Object[] obj = {line,arrowHead};

			return obj;
		}
	}
	public GlassPane getGlassPane(JPanel panel) {
		return new GlassPane(panel);
	}

}


	

