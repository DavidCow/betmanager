package bettingManager.gui;

import java.util.Date;

public class DateRangeMessage {
	/**
	 * Finals
	 */
	public static final int MONTH = 0;
	public static final int DAY = 1;
	public static final int BEFORE = 2;
	public static final int AFTER = 3;
	public static final int BETWEEN = 4;
	public static final int LAST = 5;
	
	public final int LAST_STATE_TIPS = 100;
	
	/**
	 * States
	 */
	private int state = 0;
	private int last_state = 0;
	
	
	/**
	 * Variables
	 */
	private Date d1;
	private Date d2;
	private int last_state_value;
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Date getD1() {
		return d1;
	}
	public void setD1(Date d1) {
		this.d1 = d1;
	}
	public Date getD2() {
		return d2;
	}
	public void setD2(Date d2) {
		this.d2 = d2;
	}
	public int getLast_state() {
		return last_state;
	}
	public void setLast_state(int last_state) {
		this.last_state = last_state;
	}
	public int getLast_state_value() {
		return last_state_value;
	}
	public void setLast_state_value(int last_state_value) {
		this.last_state_value = last_state_value;
	}
	
	
}
