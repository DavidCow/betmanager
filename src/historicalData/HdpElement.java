package historicalData;

public class HdpElement implements OddsElement{

	private double host;
	private double guest;
	private double pivot;
	private String bias;
	private long time;
	
	public HdpElement(double host, double guest, double pivot, String bias, long time){
		this.host = host;
		this.guest = guest;
		this.pivot = pivot;
		this.bias = bias;
		this.time = time;
	}

	/**
	 * @return the host
	 */
	public double getHost() {
		return host;
	}

	/**
	 * @return the guest
	 */
	public double getGuest() {
		return guest;
	}

	/**
	 * @return the pivot
	 */
	public double getPivot() {
		return pivot;
	}

	/**
	 * @return the bias
	 */
	public String getBias() {
		return bias;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	
	
}
