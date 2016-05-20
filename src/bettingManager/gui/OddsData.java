package bettingManager.gui;

public class OddsData {
	private float greaterThan = -1;
	private float lessThan = -1;
	private float between = -1;
	private float and = -1;
	
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
	
}
