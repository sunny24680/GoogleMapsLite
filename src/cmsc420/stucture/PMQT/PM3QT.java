package cmsc420.stucture.PMQT;

import cmsc420.geom.Geometry2D;

public class PM3QT extends PMQT{
	static Validator v = new PM3Validator();
	
	public PM3QT() {
		super(v);
	}
	
	private static class PM3Validator implements Validator{

		@Override
		public boolean isValid(PMQT.blackNode n) {
			int cities = n.numCities();
			int roads = n.numRoads();
			if (cities > 1 || roads < 1)
				return false;
			else 
				return true;
		}
		
	}
}
