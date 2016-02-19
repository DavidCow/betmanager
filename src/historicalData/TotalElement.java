package historicalData;

public class TotalElement implements OddsElement{
	private double over;
	private double under;
	private double total;
	private long time;
	
	public TotalElement(double over, double under, double total, long time) {
		this.over = over;
		this.under = under;
		this.total = total;
		this.time = time;
	}

	/**
	 * @return the over
	 */
	public double getOver() {
		return over;
	}

	/**
	 * @return the under
	 */
	public double getUnder() {
		return under;
	}

	/**
	 * @return the total
	 */
	public double getTotal() {
		return total;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	
}
