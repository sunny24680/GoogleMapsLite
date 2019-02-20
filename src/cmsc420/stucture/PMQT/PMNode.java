package cmsc420.stucture.PMQT;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.TreeSet;

import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.structure.*;

@SuppressWarnings("serial")
public abstract class PMNode extends Rectangle2D.Float {

	public abstract PMNode add(Geometry2D obj, Rectangle2D.Float dimensions);
	public abstract PMNode remove(Geometry2D obj);
	public abstract ArrayList<PMNode> getMap();
	public abstract TreeSet<City> citiesIn(Circle2D.Float cir);
	public abstract TreeSet<Road> roadsIn(Circle2D.Float cir);
	public abstract boolean containsCity(City c);
	public abstract boolean containsRoad(Road r);
	
}