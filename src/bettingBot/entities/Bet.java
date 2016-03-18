package bettingBot.entities;

import java.util.Date;

import com.google.gson.Gson;

public class Bet {

	private String actionMessage;
	private String id;
	private String reqId;
	private double betAmount;
	private int betStatus;
	private double betOdd;
	private int actionStatus;
	private String tipJsonString;
	private long timeOfBet;
	
	/**
	 * @return the timeOfBet
	 */
	public long getTimeOfBet() {
		return timeOfBet;
	}

	/**
	 * @param timeOfBet the timeOfBet to set
	 */
	public void setTimeOfBet(long timeOfBet) {
		this.timeOfBet = timeOfBet;
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

	private String eventJsonString;
	private String recordJsonString;
	private String selection;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Bet [id=" + id + ", reqId=" + reqId + ", betAmount="
				+ betAmount + ", betStatus=" + betStatus + ", betOdd=" + betOdd
				+ ", timeOfBet=" + new Date(timeOfBet) + ", selection=" + selection + "]";
	}
	
	private static Gson gson = new Gson();
	
	/**
	 * Wrapper around Gson.fromJson
	 * 
	 * @param gsonString
	 * @return
	 */
	public static Bet fromJson(String gsonString){
		Bet b = gson.fromJson(gsonString, Bet.class);
		return b;
	}	
	
	/**
	 * @return the actionMessage
	 */
	public String getActionMessage() {
		return actionMessage;
	}
	/**
	 * @param actionMessage the actionMessage to set
	 */
	public void setActionMessage(String actionMessage) {
		this.actionMessage = actionMessage;
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
	 * @return the bestOdd
	 */
	public double getBetOdd() {
		return betOdd;
	}
	/**
	 * @param bestOdd the bestOdd to set
	 */
	public void setBetOdd(double betOdd) {
		this.betOdd = betOdd;
	}
	/**
	 * @return the actionStatus
	 */
	public int getActionStatus() {
		return actionStatus;
	}
	/**
	 * @param actionStatus the actionStatus to set
	 */
	public void setActionStatus(int actionStatus) {
		this.actionStatus = actionStatus;
	}
}
