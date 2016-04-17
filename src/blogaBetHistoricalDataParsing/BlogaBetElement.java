package blogaBetHistoricalDataParsing;

import java.util.Date;

public class BlogaBetElement {
	
	private Date gameDate;
	private Date publicationDate;
	private String sport;
	private String competition;
	private String event;
	private String tipster;
	private String selection;
	private String typeOfBet;
	private double bestOdds;
	private String result;
	private String host;
	private String guest;
	private double pivotValue;
	private String tipTeam;
	private String pivotBias;
	

	
	public BlogaBetElement(Date gameDate, Date publicationDate, String sport,
			String competition, String event, String tipster, String selection,
			String typeOfBet, double bestOdds, String result, String host,
			String guest, double pivotValue, String tipTeam, String pivotBias) {
		super();
		this.gameDate = gameDate;
		this.publicationDate = publicationDate;
		this.sport = sport;
		this.competition = competition;
		this.event = event;
		this.tipster = tipster;
		this.selection = selection;
		this.typeOfBet = typeOfBet;
		this.bestOdds = bestOdds;
		this.result = result;
		this.host = host;
		this.guest = guest;
		this.pivotValue = pivotValue;
		this.tipTeam = tipTeam;
		this.pivotBias = pivotBias;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BlogaBetElement [gameDate=" + gameDate + ", publicationDate="
				+ publicationDate + ", sport=" + sport + ", competition="
				+ competition + ", event=" + event + ", tipster=" + tipster
				+ ", selection=" + selection + ", typeOfBet=" + typeOfBet
				+ ", bestOdds=" + bestOdds + ", result=" + result + ", host="
				+ host + ", guest=" + guest + ", pivotValue=" + pivotValue
				+ ", tipTeam=" + tipTeam + ", pivotBias=" + pivotBias + "]";
	}
	
	



	/**
	 * @return the pivotBias
	 */
	public String getPivotBias() {
		return pivotBias;
	}



	/**
	 * @param pivotBias the pivotBias to set
	 */
	public void setPivotBias(String pivotBias) {
		this.pivotBias = pivotBias;
	}



	/**
	 * @return the tipTeam
	 */
	public String getTipTeam() {
		return tipTeam;
	}

	/**
	 * @param tipTeam the tipTeam to set
	 */
	public void setTipTeam(String tipTeam) {
		this.tipTeam = tipTeam;
	}

	/**
	 * @return the pivotValue
	 */
	public double getPivotValue() {
		return pivotValue;
	}

	/**
	 * @param pivotValue the pivotValue to set
	 */
	public void setPivotValue(double pivotValue) {
		this.pivotValue = pivotValue;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the guest
	 */
	public String getGuest() {
		return guest;
	}

	/**
	 * @param guest the guest to set
	 */
	public void setGuest(String guest) {
		this.guest = guest;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}
	/**
	 * @param event the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}
	/**
	 * @return the sport
	 */
	public String getSport() {
		return sport;
	}
	/**
	 * @return the publicationDate
	 */
	public Date getPublicationDate() {
		return publicationDate;
	}	
	/**
	 * @param publicationDate the publicationDate to set
	 */
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}
	/**
	 * @param sport the sport to set
	 */
	public void setSport(String sport) {
		this.sport = sport;
	}
	/**
	 * @return the gameDate
	 */
	public Date getGameDate() {
		return gameDate;
	}
	/**
	 * @param gameDate the gameDate to set
	 */
	public void setGameDate(Date gameDate) {
		this.gameDate = gameDate;
	}
	/**
	 * @return the competition
	 */
	public String getCompetition() {
		return competition;
	}
	/**
	 * @param competition the competition to set
	 */
	public void setCompetition(String competition) {
		this.competition = competition;
	}
	/**
	 * @return the tipster
	 */
	public String getTipster() {
		return tipster;
	}
	/**
	 * @param tipster the tipster to set
	 */
	public void setTipster(String tipster) {
		this.tipster = tipster;
	}
	/**
	 * @return the selection
	 */
	public String getSelection() {
		return selection;
	}
	/**
	 * @param selection the selection to set
	 */
	public void setSelection(String selection) {
		this.selection = selection;
	}
	/**
	 * @return the typeOfBet
	 */
	public String getTypeOfBet() {
		return typeOfBet;
	}
	/**
	 * @param typeOfBet the typeOfBet to set
	 */
	public void setTypeOfBet(String typeOfBet) {
		this.typeOfBet = typeOfBet;
	}
	/**
	 * @return the bestOdds
	 */
	public double getBestOdds() {
		return bestOdds;
	}
	/**
	 * @param bestOdds the bestOdds to set
	 */
	public void setBestOdds(double bestOdds) {
		this.bestOdds = bestOdds;
	}
	
	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
}
