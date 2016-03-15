package mailParsing;

import java.util.Date;


public class BetInformations {
	
	@Override
	public String toString(){
		String res = "";
		res += "Event: " + event + ", Tipster: " + tipster + ", bet on: " + betOn + ", no Bet under: " + noBetUnder;
		return res;		
	}
	
	public String event;
	public String host;
	public String guest;
	public String tipster;
	public String typeOfBet;
	public String betOn;
	public double bestOdds;
	public double noBetUnder;
	public Date date;
	public double pivotValue;
	public String pivotBias;
}
