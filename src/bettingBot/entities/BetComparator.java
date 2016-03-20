package bettingBot.entities;

import java.util.Comparator;

public class BetComparator implements Comparator<Bet>{

	@Override
	public int compare(Bet arg0, Bet arg1) {
		long m0 = arg0.getTimeOfBet();
		long m1 = arg1.getTimeOfBet();
		
		if(m0 < m1)
			return -1;
		if(m1 < m0)
			return 1;
		return 0;
	}
}
