package bettingManager.statsCalculation;

import java.util.Date;

public class BettingManagerBet {

	public Date betDate;
	public Date gameDate;
	public String koB;
	public String tipster;
	public String event;
	public String selection;
	public String netWon;
	public double odds;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BettingManagerBet [betDate=" + betDate + ", gameDate="
				+ gameDate + ", koB=" + koB + ", tipster=" + tipster
				+ ", event=" + event + ", selection=" + selection + ", netWon="
				+ netWon + ", odds=" + odds + "]";
	}
}
