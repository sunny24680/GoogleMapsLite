package cmsc420.structure.PRQT;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import cmsc420.structure.*;
import cmsc420.meeshquest.part2.*;

@SuppressWarnings("serial")
public class blackNode extends Node {
	public City city;
	private Rectangle2D.Float dimension;
	
	public blackNode(City newCity, Rectangle2D.Float dimensions) {
		//System.out.println("Creating black node"+newCity.toString() + "  "+dimensions);
		city = newCity;
		dimension = dimensions;
		MeeshQuest.canvas.addPoint(newCity.getName(), newCity.x, newCity.y, Color.BLACK);
	} 
	
	public Node addCity(City newCity, Rectangle2D.Float dimensions) {
		// TODO Auto-generated method stub
		MeeshQuest.canvas.removePoint(city.getName(), city.x, city.y, Color.BLACK);
		Node g = new grayNode(city, dimensions);
		return g.addCity(newCity, dimensions);
	}

	public Node removeCity(City remove) {
		// TODO Auto-generated method stub
		if (remove.equals(city)) {
			MeeshQuest.canvas.removePoint(city.getName(), city.x, city.y, Color.BLACK);
			return PRQT.white;
		} else {
			//System.out.println("Looked at wrong city");
			return this;
		}
	}

	public City getCity() {
		return city;
	}
	
	public Rectangle2D.Float getDimensions(){
		return dimension;
	}
	
	public ArrayList<Node> getCities() {
		ArrayList<Node> t = new ArrayList<Node>();
		t.add(this);
		return t;
	}
	
	public ArrayList<Node> distanceFrom(Point2D.Float loc, int radius) {
		ArrayList<Node> t = new ArrayList<Node>();
		double dis = (Math.pow((loc.x-city.x), 2) + Math.pow((loc.y-city.y), 2));
		dis = Math.sqrt(dis);
		if (dis <= radius) {
			t.add(this);
		}
		return t;
	}
	
	public boolean contains(City contain) {
		return city.equals(contain);
	}
	
	public String toString() {
		return "City = "+city.getName()+" @ : "+city.getX()+", "+city.getY();
	}
}
