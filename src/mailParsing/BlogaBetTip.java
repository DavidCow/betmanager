package mailParsing;

import java.util.Date;

public class BlogaBetTip implements java.io.Serializable {
	
	@Override
	public String toString(){
		String res = "";
		res += "Event: " + event + ", Tipster: " + tipster + ", bet on: " + selection + ", no Bet under: " + odds * 0.95;
		return res;		
	}
	
	public String host;
	public String guest;
	public String pivotBias;
	public String pivotType;
	public String event;
	public double pivotValue;
	public double odds;
	public double stake;
	public String source;
	public String sport;
	public String country;
	public Date startDate;
	public Date receivedDate;
	public Date publishDate;
	public String tipster;
	public String selection;
	public String fullContent;

}
