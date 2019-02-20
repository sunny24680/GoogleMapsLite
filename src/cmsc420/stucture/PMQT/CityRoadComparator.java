package cmsc420.stucture.PMQT;

import java.util.Comparator;
import cmsc420.structure.*;
import cmsc420.geom.Geometry2D;

public class CityRoadComparator implements Comparator<Geometry2D>{
	
	public int compare(Geometry2D o1, Geometry2D o2) {
		if (o1.getType() == Geometry2D.POINT) {
			if (o2.getType() == Geometry2D.POINT) {
				return ((City) o1).getName().compareTo(((City) o2).getName());
			}
			else 
				return -1;
		} else if (o2.getType() == Geometry2D.POINT)
			return 1; 
		else 
			return -(((Road) o1).compareTo((Road) o2));
		
	}
	
}
