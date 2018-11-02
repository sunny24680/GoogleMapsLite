package cmsc420.structure;

import java.awt.geom.Point2D;
import java.util.Comparator;

import cmsc420.geom.Geometry2D;

//As of Part 0 City is immutable
@SuppressWarnings("serial")
public class City extends Point2D.Float implements Geometry2D{

	private int radius;
	private String color, name;
	private boolean isolated;
	
	public City() {
		super(0.0f,0.0f); 
		radius = 0;
		color = "black";
		name = "Dummy City";
	}
	
	public City(String name, float x, float y, int r, String color, boolean iso) {
		super(x,y);
		this.name = name;
		radius = r;
		this.color = color;
		isolated = iso;
	}
	
	//getters for the city object
	
	public String getName() {
		return name;
	}
	
	public String getColor() {
		return color;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public boolean isIsolated() {
		return isolated;
	}
	
	public Point2D toPoint2D() {
		return new Point2D.Float((float) this.getX(), (float) this.getY());		
	}
	
	public double distanceTo(Point2D.Float loc) {
		double dis = (Math.pow((loc.x-x), 2) + Math.pow((loc.y-y), 2));
		dis = Math.sqrt(dis);
		return dis;
	}
	
	public int compareName(City other) {
		return -name.compareTo(other.getName());
	}
	
	public boolean equals(City obj) {
		if ((obj.x == x) && (obj.y == y)) {
			//System.out.println("Same position on the Map");
			if (obj.getName().equals(name) && obj.getRadius() == radius)
				return true;
			else {
				//System.out.println("Different name or radius");
				return false;
			}
		} else 
			return false;
	}
	
	@Override
	public int getType() {
		return POINT;
	}
	
	public String toString() {
		return name+" @ "+super.toString();
	}
	
	public static class nameComparator implements Comparator<String>{
		@Override
		public int compare(String o1, String o2) {
			//returns reverse asciibetically
			return -o1.compareTo(o2);
		}
	}
	
	public static class Point2DComparator implements Comparator<City>{
		@Override
		public int compare(City o1, City o2) {
			//sorts on y axis then x axis
			if(o1.getY() < o2.getY())
				return -1;
			else if (o1.getY() > o2.getY())
				return 1;
			else {
				//if the y values are the same
				if (o1.getX() < o2.getX())
					return -1;
				else if (o1.getX() > o2.getX())
					return 1;
				else 
					return 0;
			}
		}
	}

}
