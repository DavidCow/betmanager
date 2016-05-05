package yieldPrediction.betAdvisor;

import java.util.Map;

public class TipsterStats {
	
	String tipster;
	double numBets;
	double avgYield;
//	Map<String, Double> avgYield_types;
	Map<Double, Double> avgYield_stakes;
//	Map<String, Integer> type_frequency;
	Map<Double, Double> stake_frequency;
	double minStake;
	double maxStake;
	double avgStake;
	double maxStakeDeviation;
	
	public TipsterStats(String name, double numBets, double yield, Map<Double, Double> stakes_yield, Map<Double, Double> stake_freq) {
		this.tipster = name;
		this.numBets = numBets;
		this.avgYield = yield;
		this.avgYield_stakes = stakes_yield;
		this.stake_frequency = stake_freq;
		this.minStake = (double) stakes_yield.keySet().toArray()[0];
		this.maxStake = (double) stakes_yield.keySet().toArray()[stakes_yield.size()-1];
		this.avgStake = calculateAvgStake();
		this.maxStakeDeviation = Math.max(Math.abs(minStake - avgStake), Math.abs(maxStake - avgStake));
	}
	
	private double calculateAvgStake(){
		double total = 0;
		for(Double d : stake_frequency.keySet()){
			total += d * stake_frequency.get(d);
		}
		return total/numBets;
	}
	
	public double calculateNormalizedStakeDeviation(double stake){
		return (stake - avgStake)/maxStakeDeviation;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(tipster).append(" ").append(numBets).append(" ").append(avgYield).append("\n");
		sb.append("AvgStake: ").append(calculateAvgStake()).append("\n");
		for(Double d : avgYield_stakes.keySet()){
			sb.append(Math.round(d * 100.0) / 100.0).append("\t").append(Math.round(stake_frequency.get(d) * 100.0) / 100.0)
			.append("\t").append(Math.round(avgYield_stakes.get(d) * 100.0) / 100.0).append("\n");
		}
		return sb.toString();
		
	}
	
	

}
