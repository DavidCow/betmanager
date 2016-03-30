package blogaBetHistoricalDataParsing;

import java.util.Date;

public class BlogaBetElement {
	
	private Date gameDate;
	private String sport;
	private String competition;
	private String tipster;
	private String selection;
	private String typeOfBet;
	private double bestOdds;
	private String result;
	/**
	 * @return the sport
	 */
	public String getSport() {
		return sport;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BlogaBetElement [gameDate=" + gameDate + ", competition="
				+ competition + ", tipster=" + tipster + ", selection="
				+ selection + ", typeOfBet=" + typeOfBet + ", bestOdds="
				+ bestOdds + ", result=" + result + "]";
	}
	
	public BlogaBetElement(Date gameDate, String sport, String competition,
			String tipster, String selection, String typeOfBet,
			double bestOdds, String result) {
		super();
		this.gameDate = gameDate;
		this.sport = sport;
		this.competition = competition;
		this.tipster = tipster;
		this.selection = selection;
		this.typeOfBet = typeOfBet;
		this.bestOdds = bestOdds;
		this.result = result;
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
