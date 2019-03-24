import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class LineDrawer {

	private class GlassPane extends JComponent {
		
		
		private static final long serialVersionUID = 1L;
		
		private ArrayList<Integer> x_1;
		private ArrayList<Integer> x_2;
		private ArrayList<Integer> y_1;
		private ArrayList<Integer> y_2;
		
		private boolean blocking;
		private MouseEvent e;
		
		public GlassPane() {
			x_1 = new ArrayList<Integer>();
			y_1 = new ArrayList<Integer>();
			x_2 = new ArrayList<Integer>();
			y_2 = new ArrayList<Integer>();
			
			
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e1) {
					blocking = true;
					e = e1;
					repaint();
				}
			});
			
		}
		
		
		public  void addPoint(Point p, boolean firstPoint) {
			if(firstPoint) {
				x_1.add(p.x);
				y_1.add(p.y);
			} else {
				x_2.add(p.x);
				y_2.add(p.y);
			}
		}
		
		public void paintComponent(Graphics g) {
			
			super.paintComponent(g);
			
			Graphics2D g1 = (Graphics2D)g;
			
			if(blocking) {
				
				g1.drawString("can't paint while i am visible",e.getPoint().x,e.getPoint().y);

			} else if(x_1.size() > 0) {
				
				for(int i=0; i<x_1.size()-1; i++) {
					g1.setStroke(new BasicStroke(1));
					g1.setColor(Color.RED);
					g1.fillOval(x_1.get(i)-5, y_1.get(i)-5, 10, 10);
					g1.setColor(Color.GREEN);
					g1.fillOval(x_2.get(i)-5, y_2.get(i)-5, 10, 10);
					g1.setColor(Color.BLUE);
					g1.setStroke(new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
					g1.drawLine(x_1.get(i), y_1.get(i), x_2.get(i), y_2.get(i));
					
					g1.drawLine(x_2.get(i), y_2.get(i), x_1.get(i+1), y_1.get(i+1));

				}
				
			}
		}
	}
	
	private class ContentPane extends JPanel {
		
		
		private static final long serialVersionUID = 1L;
		
		private GlassPane glassPane;
		private ArrayList<Point> pointList;
		

		public ContentPane(GlassPane glassPane) {
			pointList = new ArrayList<Point>();
			this.glassPane = glassPane;
			setPreferredSize(new Dimension(300,300));
			addMouseListener(new MouseHandler());
		}
	
		
		public void paintComponent(Graphics g) {
			
			super.paintComponent(g);
			
			Graphics2D g1 = (Graphics2D)g;
			
			g1.setColor(Color.BLACK);
			g1.setStroke(new BasicStroke(1));
			
			if(pointList.size()>1){
				for(int i=0; i<pointList.size()-1; i++) {
					g1.fillOval(pointList.get(0).x-3, pointList.get(0).y-3, 6, 6);
					g1.drawLine(pointList.get(i).x, pointList.get(i).y, pointList.get(i+1).x, pointList.get(i+1).y);

				}
			} else {
				g1.drawString("DRAW ON ME", getWidth()/2-20, getHeight()/2);
			}
			
		}
		private class MouseHandler extends MouseAdapter {
			
			private boolean firstPoint = true;
			
			public void mouseClicked(MouseEvent e) {
				
				Point p = e.getPoint();
				
				pointList.add(p);
				
				glassPane.addPoint(p, firstPoint);
				
				if(firstPoint) {
					firstPoint = false;
				} else {
					firstPoint = true;
				}
				
				repaint();
			}
		}
		
	}
	
	public JComponent getGlassPane() {
		return new GlassPane();
	}
	
	public JPanel getContentPanel(JComponent glassPane) {
		return new ContentPane((GlassPane) glassPane);
	}
	
	private static void createAndShowGui() {
		
		JFrame frame = new JFrame("glassPaneFun");
		
		frame.setState(Frame.ICONIFIED);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LineDrawer drawer = new LineDrawer();
		
		JComponent glassPane = drawer.getGlassPane();
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(drawer.getContentPanel(glassPane),BorderLayout.CENTER);
		
		JButton donePlaying = new JButton("ToggleGlassPaneVisibility");
		donePlaying.addActionListener(drawer.getActionHandler(glassPane));
		
		container.add(donePlaying,BorderLayout.SOUTH);
		
		frame.add(container);
		frame.setGlassPane(glassPane);
		frame.getGlassPane().setVisible(false);
		frame.pack();
		frame.setVisible(true);
	}
	private ActionListener getActionHandler(JComponent glassPane) {
		return new ActionHandler(glassPane);
	}
	
	private class ActionHandler implements ActionListener {
		
		private JComponent glassPane;
		
		public ActionHandler(JComponent glassPane) {
			this.glassPane = glassPane;
		}
		public void actionPerformed(ActionEvent e) {
				glassPane.setVisible(!glassPane.isVisible());
			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater( new Runnable () {
			public void run () {
				createAndShowGui();
			}
		});
		
	}

}


	

