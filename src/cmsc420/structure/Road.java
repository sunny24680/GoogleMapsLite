package cmsc420.structure;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;

import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;

@SuppressWarnings("serial")
public class Road extends Line2D.Float implements Geometry2D, Comparable<Road>{
	private City startCity, endCity;
	
	public Road(City s, City e) {
		super(s.x, s.y, e.x, e.y);
		startCity = s;
		endCity = e;
	}
	
	public double getDist() {
		return startCity.toPoint2D().distance(endCity);
	}

	@Override
	public int getType() {
		return SEGMENT;
	}

	public City getStartCity() {
		return startCity;
	}

	public void setStartCity(City startCity) {
		this.startCity = startCity;
	}

	public City getEndCity() {
		return endCity;
	}

	public void setEndCity(City endCity) {
		this.endCity = endCity;
	}
	
	public Line2D toLine2D() {
		return new Line2D.Float(startCity.x, startCity.y, endCity.x, endCity.y);
	}
	
	public City findSimilar(Road other) {
		if (other.getStartCity() == startCity)
			return startCity;
		else if (other.getEndCity() == endCity)
			return endCity;
		return null;
	}
	
	@Override
	public int compareTo(Road obj) {
		if (startCity.compareName(((Road) obj).getStartCity()) == 0) {
			return -endCity.compareName(((Road) obj).getEndCity());
		} else 
			return -startCity.compareName(((Road) obj).getStartCity());
	}
	
	public boolean intersects(Circle2D.Float cir) {
		if (ptSegDist(cir.getCenterX(), cir.getCenterY()) <= cir.getRadius())
			return true;
		else 
			return false;
	}
	
	public static class nameComparator implements Comparator<Road>{
		@Override
		public int compare(Road o1, Road o2) {
			//returns reverse asciibetically
			City o1start = o1.getStartCity();
			City o1end = o1.getEndCity();
			City o2start = o2.getStartCity();
			City o2end = o2.getEndCity();
			if (o1.getStartCity().compareName(o1.getEndCity()) < 0) {
				City temp = o1start;
				o1start = o1end;
				o1end = o1start;
			}
			if (o2.getStartCity().compareName(o2.getEndCity()) < 0) {
				City temp = o1start;
				o1start = o1end;
				o1end = o1start;
			}
			if (o1start.compareName(o2start) == 0) {
				return o1end.compareName(o2end);
			} else 
				return o1start.compareName(o2start);
		}
	}
	
	public String toString() {
		return "starts : "+startCity.getName()+" ----- ends : "+endCity.getName();
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Road))
			return false;
		Road other = (Road) obj;
		boolean start = startCity.equals(other.getStartCity()) || startCity.equals(other.endCity);
		boolean end = endCity.equals(other.getStartCity()) || endCity.equals(other.endCity);
		//System.out.println("start city = "+start);
		//System.out.println("end city matches other city "+end);
		return start && end;
	}
	
}
