package cmsc420.stucture.PMQT;

import java.awt.geom.Rectangle2D;
import cmsc420.geom.Geometry2D;

public abstract class Node extends Rectangle2D.Float {

	public abstract Node add(Geometry2D obj, Rectangle2D.Float dimensions);
	public abstract Node remove(Geometry2D obj);
	
}