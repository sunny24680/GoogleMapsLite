package cmsc420.stucture.PMQT;

public class PM1QT extends PMQT{
	static Validator v = new PM1Validator();
	
	public PM1QT(int x, int y) {
		super(v, x, y);
	}
	
	private static class PM1Validator implements Validator{

		@Override
		public boolean isValid(blackNode n) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
