package bettingManager.gui;

import java.util.Calendar;
import java.util.Date;

public class DateRangeMessage {
	/**
	 * Selected by user in "Date Range" button
	 */
	public static final int ALL = 0;
	public static final int MONTH = 1;
	public static final int DAY = 2;
	public static final int BEFORE = 3;
	public static final int AFTER = 4;
	public static final int BETWEEN = 5;
	public static final int LAST = 6;
	
	/**
	 * Additionally set, for example "Last Tips"
	 */
	public static final int LAST_STATE_TIPS = 100;
	
	/**
	 * The "state" variable is either (Variable represents what the user has selected as Date Range filter)
	 * MONTH  0
	 * DAY	1
	 * BEFORE	2 
	 * AFTER 	3
	 * BETWEEN 	4
	 * LAST	5
	 */
	private int state = 0;
	
	
	/**
	 * The "last_state" variable is so far only
	 * LAST_STATE_TIPS	100
	 * If the user has selected Last Tips, then this value is set to the final int variable LAST_STATE_TIPS (see above)
	 */
	private int last_state = 0;
	
	
	/**
	 * Variables
	 */
	private Date d1;		// If user has selected Date Range "Month", "Day", "Before", "After" -> the date is stored in d1
	private Date d2;		// If user has selected Date Range "Between" -> the value for Between Date1 and Date2 is stored in d1 and d2.
	private int last_state_value;	//If user has selected "Last Tips" -> last_state_value has the value, for example 5000 "Last Tips"
	
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
	
	public String hourOrMinuteString(int value) {
		if (value < 10) return "0"+value;
		return ""+value;
	}
	
	
	
	/**
	 * String for "Active Filters:"
	 * @return
	 */
	public String getAllFiltersLabel() {
		Calendar cal = Calendar.getInstance();
		String timeString = "";
		String d1String = "";
		if (d1 != null) {
			cal.setTime(d1);
			timeString = hourOrMinuteString(cal.get(Calendar.HOUR_OF_DAY)) + ":" + hourOrMinuteString(cal.get(Calendar.MINUTE));
			d1String = cal.get(Calendar.DAY_OF_MONTH) + " " + OptionsDateRangeController.theMonth(cal.get(Calendar.MONTH)) + " " + cal.get(Calendar.YEAR);
		}
		
		if (state == ALL) {
			return "All bets";
		} else if (state == MONTH) {
			return "Month: " + OptionsDateRangeController.theMonth(cal.get(Calendar.MONTH)) + " " + cal.get(Calendar.YEAR);
		} else if (state == DAY) {
			return "Day: " + d1String;
		}  else if (state == BEFORE) {
			return "Before: " + timeString + " " + d1String;
		}  else if (state == AFTER) {
			return "After: " + timeString + " " + d1String;
		}  else if (state == BETWEEN) {
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(d2);
			String timeString2 = hourOrMinuteString(cal2.get(Calendar.HOUR_OF_DAY)) + ":" + hourOrMinuteString(cal2.get(Calendar.MINUTE));
			return "Between: " + timeString + " " + d1String + " and " + timeString2 + " " + cal2.get(Calendar.DAY_OF_MONTH) + " " + OptionsDateRangeController.theMonth(cal2.get(Calendar.MONTH)) + " " + cal2.get(Calendar.YEAR);
		}  else if (state == LAST) {
			if (last_state == LAST_STATE_TIPS) {
				return "Last " + last_state_value + " Tips";
			}
			/**
			 * ADD MORE LAST values 
			 */
		} 
		return "Should never be returned - DateRangeMessage";
	}
	
	
}
