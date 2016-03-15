package bettingBot.entities;

import com.google.gson.Gson;

public class BetTicket {
	private String actionMessage;
	private double minStake;
	private double maxStake;
	private double currentOdd;
	private String reqId;
	private int actionStatus;
	
	@Override
	public String toString(){
		String res = "";
		res += "minStake: " + minStake + ", maxStake: " + maxStake + ", currentOdd: " + currentOdd + ", reqId: " + reqId + ", actionStatus: " + actionStatus;
		return res;
	}
	
	private static Gson gson = new Gson();
	
	/**
	 * Wrapper around Gson.fromJson
	 * 
	 * @param gsonString
	 * @return
	 */
	public static BetTicket fromJson(String gsonString){
		BetTicket bT = gson.fromJson(gsonString, BetTicket.class);
		return bT;
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
	 * @return the minStake
	 */
	public double getMinStake() {
		return minStake;
	}
	/**
	 * @param minStake the minStake to set
	 */
	public void setMinStake(double minStake) {
		this.minStake = minStake;
	}
	/**
	 * @return the maxStake
	 */
	public double getMaxStake() {
		return maxStake;
	}
	/**
	 * @param maxStake the maxStake to set
	 */
	public void setMaxStake(double maxStake) {
		this.maxStake = maxStake;
	}
	/**
	 * @return the currentOdd
	 */
	public double getCurrentOdd() {
		return currentOdd;
	}
	/**
	 * @param currentOdd the currentOdd to set
	 */
	public void setCurrentOdd(double currentOdd) {
		this.currentOdd = currentOdd;
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
