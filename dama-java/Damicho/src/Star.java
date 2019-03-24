
import java.awt.Polygon;


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
