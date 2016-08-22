package bettingManager.statsCalculation;

import java.util.ArrayList;
import java.util.List;

public class StatsRow {
	public String groupBy;
	public double invested = 0;
	public double averageYield = 0;
	public double averageOdds = 0;
	public double numberOfBets = 0;
	public double numberOfTips = 0;
	public double percentWeGet = 0;
	public double percentOver95 = 0;
	public double averageLiquidity = 0;
	public double percentOfTipsFound = 0;
	public double flatStakeYield = 0;
	public double probabilityRatio = 0;
	public double flatStakeYieldEv = 0;
	public double averageYieldEv = 0;
	
	public List<BettingManagerBet> bets = new ArrayList<BettingManagerBet>();
	
	public List<BettingManagerBet> getBets() {
		return bets;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public double getInvested() {
		return invested;
	}
	public double getAverageYield() {
		return averageYield;
	}
	public double getAverageOdds() {
		return averageOdds;
	}
	public double getNumberOfBets() {
		return numberOfBets;
	}
	public double getNumberOfTips() {
		return numberOfTips;
	}
	public double getPercentWeGet() {
		return percentWeGet;
	}
	public double getPercentOver95() {
		return percentOver95;
	}
	public double getAverageLiquidity() {
		return averageLiquidity;
	}
	public double getPercentOfTipsFound() {
		return percentOfTipsFound;
	}
	public double getFlatStakeYield() {
		return flatStakeYield;
	}
}
