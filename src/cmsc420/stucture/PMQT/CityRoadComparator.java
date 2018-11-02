package cmsc420.stucture.PMQT;

import java.util.Comparator;

import cmsc420.geom.Geometry2D;

public class CityRoadComparator implements Comparator<Geometry2D>{
	
	public int compare(Geometry2D o1, Geometry2D o2) {
		if (o1.getType() == 0) {
			if (o2.getType() == 0)
				return 0;
			else 
				return -1;
		} else 
			return 1;
		
	}
	
}
