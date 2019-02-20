package cmsc420.structure.PRQT;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import cmsc420.structure.*;

@SuppressWarnings("serial")
public abstract class Node extends Rectangle2D.Float {
	public abstract Node addCity(City newCity, Rectangle2D.Float dimensions);
	public abstract Node removeCity(City city);
	public abstract boolean contains(City city);
	public abstract ArrayList<Node> getCities();
	public abstract ArrayList<Node> distanceFrom(Point2D.Float loc, int radius);
	public abstract String toString();
}
