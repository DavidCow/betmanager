package mailParsing;

import java.util.Date;


public class BetAdvisorTip implements java.io.Serializable {
	
	@Override
	public String toString(){
		String res = "";
		res += "Event: " + event + ", Tipster: " + tipster + ", bet on: " + betOn + ", no Bet under: " + noBetUnder;
		return res;		
	}
	
	public String event;
	public String host;
	public String guest;
	public String tipster;
	public String typeOfBet;
	public String betOn;
	public double bestOdds;
	public double noBetUnder;
	public double take;
	public Date date;
	public double pivotValue;
	public String pivotBias;
	public Date receivedDate;
	public String fullContent;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(bestOdds);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((betOn == null) ? 0 : betOn.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result
				+ ((fullContent == null) ? 0 : fullContent.hashCode());
		result = prime * result + ((guest == null) ? 0 : guest.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		temp = Double.doubleToLongBits(noBetUnder);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((pivotBias == null) ? 0 : pivotBias.hashCode());
		temp = Double.doubleToLongBits(pivotValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((receivedDate == null) ? 0 : receivedDate.hashCode());
		temp = Double.doubleToLongBits(take);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((tipster == null) ? 0 : tipster.hashCode());
		result = prime * result
				+ ((typeOfBet == null) ? 0 : typeOfBet.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BetAdvisorTip other = (BetAdvisorTip) obj;
		if (Double.doubleToLongBits(bestOdds) != Double
				.doubleToLongBits(other.bestOdds))
			return false;
		if (betOn == null) {
			if (other.betOn != null)
				return false;
		} else if (!betOn.equals(other.betOn))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (fullContent == null) {
			if (other.fullContent != null)
				return false;
		} else if (!fullContent.equals(other.fullContent))
			return false;
		if (guest == null) {
			if (other.guest != null)
				return false;
		} else if (!guest.equals(other.guest))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (Double.doubleToLongBits(noBetUnder) != Double
				.doubleToLongBits(other.noBetUnder))
			return false;
		if (pivotBias == null) {
			if (other.pivotBias != null)
				return false;
		} else if (!pivotBias.equals(other.pivotBias))
			return false;
		if (Double.doubleToLongBits(pivotValue) != Double
				.doubleToLongBits(other.pivotValue))
			return false;
		if (receivedDate == null) {
			if (other.receivedDate != null)
				return false;
		} else if (!receivedDate.equals(other.receivedDate))
			return false;
		if (Double.doubleToLongBits(take) != Double
				.doubleToLongBits(other.take))
			return false;
		if (tipster == null) {
			if (other.tipster != null)
				return false;
		} else if (!tipster.equals(other.tipster))
			return false;
		if (typeOfBet == null) {
			if (other.typeOfBet != null)
				return false;
		} else if (!typeOfBet.equals(other.typeOfBet))
			return false;
		return true;
	}
}
