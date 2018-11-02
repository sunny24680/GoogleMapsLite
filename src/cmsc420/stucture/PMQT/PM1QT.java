package cmsc420.stucture.PMQT;

public class PM1QT extends PMQT{
	static Validator v = new PM1Validator();
	
	public PM1QT() {
		super(v);
	}
	
	private static class PM1Validator implements Validator{

		@Override
		public boolean isValid(Node n) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
