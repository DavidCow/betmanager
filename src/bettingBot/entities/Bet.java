package bettingBot.entities;

import com.google.gson.Gson;

public class Bet {

	private String actionMessage;
	private String id;
	private String reqId;
	private double betAmount;
	private int betStatus;
	private double betOdd;
	private int actionStatus;
	
	@Override
	public String toString(){
		String res = "";
		res += "id: " + id + ", reqId: " + reqId + ", betAmount: " + betAmount + ", betOdd: " + betOdd + ", betStatus: " + betStatus; 
		return res;
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
