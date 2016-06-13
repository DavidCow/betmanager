package bettingManager.statsCalculation;

import java.util.Comparator;

public class BettingManagerBetComparator implements Comparator<BettingManagerBet>{

	public int compare(BettingManagerBet b0, BettingManagerBet b1){
		if(b0.betDate.after(b1.betDate))
			return 1;
		else if(b1.betDate.after(b0.betDate)){
			return -1;
		}
		return 0;
	}
}
