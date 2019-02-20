package cmsc420.structure.PRQT;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import cmsc420.structure.*;
import cmsc420.meeshquest.part2.*;

@SuppressWarnings("serial")
public class grayNode extends Node {
	private Node [] direction = new Node[4];
	//0 = NW
	//1 = NE 
	//2 = SW
	//3 = SE
	private Point2D.Float center;
	private Rectangle2D.Float dimension;
	
	public grayNode(City city, Rectangle2D.Float dimensions) {
		//have to do the math for the quad tree
		//System.out.println("Creating gray node with " + city + "  "+ dimensions);
		center = new Point2D.Float((float) dimensions.getCenterX(), (float) dimensions.getCenterY());
		Arrays.fill(direction, PRQT.white);
		dimension = dimensions;
		MeeshQuest.canvas.addRectangle(dimension.x, dimension.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
		MeeshQuest.canvas.addRectangle(center.x, dimension.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
		MeeshQuest.canvas.addRectangle(dimension.x, center.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
		MeeshQuest.canvas.addRectangle(center.x, center.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
		insert(city);
	}
	
	public double getX() {
		return center.getX();
	}
	
	public double getY() {
		return center.getY();
	}
	
	public Rectangle2D.Float getDimension(){
		return dimension;
	}
	
	public Node insert(City newCity) {
		//System.out.println("adding new city to gray node "+newCity);
		Rectangle2D.Float d = new Rectangle2D.Float(dimension.x, dimension.y, dimension.height/2, dimension.width/2);
		if (newCity.x < center.x) {
			//on the left side of the quad tree
			if (newCity.y < center.y) {
				//SW side
				if (direction[2] == PRQT.white) {
					direction[2] = direction[2].addCity(newCity, d);
					return this;
				} else {
					direction[2] = direction[2].addCity(newCity, d);
					return this;
				}
			} else {
				d.y = center.y;
				//NW
				if (direction[0] == PRQT.white) {
					direction[0] = direction[0].addCity(newCity, d);
					return this;
				} else {
					direction[0] = direction[0].addCity(newCity, d);
					return this;
				}
			}	
		} else {
			//on the Right side of the quad tree
			d.x = center.x;
			if (newCity.y < center.y) {
				//SE side
				if (direction[3] == PRQT.white) {
					direction[3] = direction[3].addCity(newCity, d);
					return this;
				} else {
					direction[3] = direction[3].addCity(newCity, d);
					return this;
				}
			} else {
				d.y = center.y;
				//NE
				if (direction[1] == PRQT.white) {
					direction[1] = direction[1].addCity(newCity, d);
					return this;
				} else {
					direction[1] = direction[1].addCity(newCity, d);
					return this;
				}
			}
		}
	}
	
	public int numCities() {
		int c = 0;
		for(int x = 0; x < direction.length; x++) {
			if (direction[x] != PRQT.white)
				c++;
		}
		return c;
	}
	
	@Override
	public Node addCity(City newCity, Rectangle2D.Float dimensions) {
		// TODO Auto-generated method stub
		return insert(newCity);
	}

	public Node condense() {
		if (numCities() == 0) {
			return PRQT.white;
		}
		
		if (numCities() == 1)
			for(int x = 0; x < direction.length; x++) {
				MeeshQuest.canvas.removeRectangle(dimension.x, dimension.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
				MeeshQuest.canvas.removeRectangle(center.x, dimension.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
				MeeshQuest.canvas.removeRectangle(dimension.x, center.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
				MeeshQuest.canvas.removeRectangle(center.x, center.y, dimension.getWidth()/2, dimension.getHeight()/2, Color.GRAY, false);
				if (direction[x] instanceof blackNode)
					return new blackNode(((blackNode) direction[x]).getCity(), dimension);
			}
		
		return this;
	}
	
	@Override
	public Node removeCity(City city) {
		// TODO Auto-generated method stub
		for (int x = 0; x < direction.length; x++) {
			if (direction[x].contains(city)) {
				direction[x] = direction[x].removeCity(city);
			}
		}
		
		return condense();
	}

	public Node getChild(int x) {
		return direction[x];
	}
	
	public ArrayList<Node> getCities(){
		ArrayList<Node> t = new ArrayList<Node>();
//		for (int x = 0; x < 4; x++) {
//			System.out.println("direction # "+x+" = "+direction[x]);
//		}
		t.add(this);
		t.addAll(direction[0].getCities());
		t.addAll(direction[1].getCities());
		t.addAll(direction[2].getCities());
		t.addAll(direction[3].getCities());
		return t;
	}
	
	public ArrayList<Node> distanceFrom(Point2D.Float loc, int radius) {
		ArrayList<Node> t = new ArrayList<Node>();
		t.addAll(direction[0].distanceFrom(loc, radius));
		t.addAll(direction[1].distanceFrom(loc, radius));
		t.addAll(direction[2].distanceFrom(loc, radius));
		t.addAll(direction[3].distanceFrom(loc, radius));
		return t;
	}
	
	public boolean contains(City contain) {
		for (int x = 0; x < direction.length; x++) {
			if (direction[x].contains(contain))
				return true;
		}
		return false;
	}
	
	public String toString() {
		return "Gray node with center at "+center.toString();
	}
}
