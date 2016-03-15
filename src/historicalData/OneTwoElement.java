package historicalData;

public class OneTwoElement implements OddsElement, java.io.Serializable{
	private double one;
	private double two;
	private double draw;
	private long time;
	
	public OneTwoElement(double one, double two, double draw, long time) {
		this.one = one;
		this.two = two;
		this.draw = draw;
		this.time = time;
	}

	/**
	 * @return the one
	 */
	public double getOne() {
		return one;
	}

	/**
	 * @return the two
	 */
	public double getTwo() {
		return two;
	}

	/**
	 * @return the draw
	 */
	public double getDraw() {
		return draw;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	
		
}
