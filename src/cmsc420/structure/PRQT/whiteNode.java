package cmsc420.structure.PRQT;

import cmsc420.structure.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class whiteNode extends Node{

	public Node addCity(City newCity, Rectangle2D.Float dimensions) {
		return new blackNode(newCity, dimensions);
	}

	public Node removeCity(City city) {
		//System.out.println("should not remove white ever");
		return this;
	}

	public boolean contains(City city) {
		return false;
	}
	
	public ArrayList<Node> distanceFrom(Point2D.Float loc, int radius) {
		return new ArrayList<Node>();
	}
	
	public ArrayList<Node> getCities() {
		ArrayList<Node> t = new ArrayList<Node>();
		t.add(PRQT.white);
		return t;
	}
	
	public String toString() {
		return "White node extender";
	}

}
