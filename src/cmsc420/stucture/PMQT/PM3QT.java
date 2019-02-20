package cmsc420.stucture.PMQT;

public class PM3QT extends PMQT{
	static Validator v = new PM3Validator();
	
	public PM3QT(int x, int y) {
		super(v, x, y);
	}
	
	private static class PM3Validator implements Validator{

		@Override
		public boolean isValid(PMQT.blackNode n) {
			int cities = n.numCities();
			if (cities > 1) {
				return false;
			}
			else 
				return true;
		}
		
	}
}
