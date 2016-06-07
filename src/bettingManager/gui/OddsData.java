package bettingManager.gui;

public class OddsData {
	private float greaterThan = -1;
	private float lessThan = Float.MAX_VALUE;
	private float between = -1;
	private float and = Float.MAX_VALUE;
	
	public float getGreaterThan() {
		return greaterThan;
	}
	public void setGreaterThan(float greaterThan) {
		this.greaterThan = greaterThan;
	}
	public float getLessThan() {
		return lessThan;
	}
	public void setLessThan(float lessThan) {
		this.lessThan = lessThan;
	}
	public float getBetween() {
		return between;
	}
	public void setBetween(float between) {
		this.between = between;
	}
	public float getAnd() {
		return and;
	}
	public void setAnd(float and) {
		this.and = and;
	}
	
	@Override
	public String toString() {
		return "Greater:"+ getGreaterThan() + " Less than:"+ getLessThan() + " Between"+getBetween()+" And" + getAnd();
	}
	
	/**
	 * Create the String for "Active Filter:"
	 * @param title
	 * @return
	 */
	public String getAllFiltersLabel(String title) {
		String s = title + " : ";
		boolean emptyString = true;
		if (greaterThan != -1) {
			s += "Greater than " + greaterThan;
			emptyString = false;
			if (lessThan != -1 || (between != -1 && and != -1)) {
				s += "; ";
			}
		}
		if (lessThan != -1 && lessThan != Float.MAX_VALUE) {
			s += "Less than " + lessThan;
			emptyString = false;
			
			if (between != -1 && and != -1) {
				s += "; ";
			}
		}
		if (between != -1 && and != Float.MAX_VALUE) {
			s += "Between " + between + " and " + and;
			emptyString = false;
		}
		
		if (emptyString) return "";
		
		return s;
	}
	
}
