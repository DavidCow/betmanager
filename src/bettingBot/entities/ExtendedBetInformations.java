package bettingBot.entities;

public class ExtendedBetInformations {
	private String id;
	private String reqId;
	private double betAmount;
	private double betOdd;
	private int betStatus;
	private String tipJsonString;
	private String eventJsonString;
	private String recordJsonString;
	private String betTicketJsonString;
	private String selection;
	private long timeOfBet;
	
	
	
	public ExtendedBetInformations(String id, String reqId, double betAmount,
			double betOdd, int betStatus, String tipJsonString,
			String eventJsonString, String recordJsonString,
			String betTicketJsonString, String selection, long timeofBet) {
		super();
		this.id = id;
		this.reqId = reqId;
		this.betAmount = betAmount;
		this.betOdd = betOdd;
		this.betStatus = betStatus;
		this.tipJsonString = tipJsonString;
		this.eventJsonString = eventJsonString;
		this.recordJsonString = recordJsonString;
		this.betTicketJsonString = betTicketJsonString;
		this.selection = selection;
		this.timeOfBet = timeofBet;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExtendedBetInformations [id=" + id + ", reqId=" + reqId
				+ ", betAmount=" + betAmount + ", betOdd=" + betOdd
				+ ", betStatus=" + betStatus + ", tipJsonString="
				+ tipJsonString + ", eventJsonString=" + eventJsonString
				+ ", recordJsonString=" + recordJsonString
				+ ", betTicketJsonString=" + betTicketJsonString
				+ ", selection=" + selection + ", timeofBet=" + timeOfBet + "]";
	}



	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}



	/**
	 * @return the reqId
	 */
	public String getReqId() {
		return reqId;
	}



	/**
	 * @param reqId the reqId to set
	 */
	public void setReqId(String reqId) {
		this.reqId = reqId;
	}



	/**
	 * @return the betAmount
	 */
	public double getBetAmount() {
		return betAmount;
	}



	/**
	 * @param betAmount the betAmount to set
	 */
	public void setBetAmount(double betAmount) {
		this.betAmount = betAmount;
	}



	/**
	 * @return the betOdd
	 */
	public double getBetOdd() {
		return betOdd;
	}



	/**
	 * @param betOdd the betOdd to set
	 */
	public void setBetOdd(double betOdd) {
		this.betOdd = betOdd;
	}



	/**
	 * @return the betStatus
	 */
	public int getBetStatus() {
		return betStatus;
	}



	/**
	 * @param betStatus the betStatus to set
	 */
	public void setBetStatus(int betStatus) {
		this.betStatus = betStatus;
	}



	/**
	 * @return the tipJsonString
	 */
	public String getTipJsonString() {
		return tipJsonString;
	}



	/**
	 * @param tipJsonString the tipJsonString to set
	 */
	public void setTipJsonString(String tipJsonString) {
		this.tipJsonString = tipJsonString;
	}



	/**
	 * @return the eventJsonString
	 */
	public String getEventJsonString() {
		return eventJsonString;
	}



	/**
	 * @param eventJsonString the eventJsonString to set
	 */
	public void setEventJsonString(String eventJsonString) {
		this.eventJsonString = eventJsonString;
	}



	/**
	 * @return the recordJsonString
	 */
	public String getRecordJsonString() {
		return recordJsonString;
	}



	/**
	 * @param recordJsonString the recordJsonString to set
	 */
	public void setRecordJsonString(String recordJsonString) {
		this.recordJsonString = recordJsonString;
	}



	/**
	 * @return the betTicketJsonString
	 */
	public String getBetTicketJsonString() {
		return betTicketJsonString;
	}



	/**
	 * @param betTicketJsonString the betTicketJsonString to set
	 */
	public void setBetTicketJsonString(String betTicketJsonString) {
		this.betTicketJsonString = betTicketJsonString;
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
	 * @return the timeofBet
	 */
	public long getTimeofBet() {
		return timeOfBet;
	}



	/**
	 * @param timeofBet the timeofBet to set
	 */
	public void setTimeofBet(long timeofBet) {
		this.timeOfBet = timeofBet;
	}
	
	
}
