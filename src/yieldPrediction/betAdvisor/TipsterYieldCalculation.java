package yieldPrediction.betAdvisor;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;

public class TipsterYieldCalculation {

	public static Map<String, TipsterStats> createTipsterStats(List<BetAdvisorElement> betAdvisorList) throws Exception {
		HashMap<String, Double> winningsMap = new HashMap<String, Double>();
		HashMap<String, Double> counterMap = new HashMap<String, Double>();
		HashMap<String, TreeMap<Double, Double>> stake_frequency = new HashMap<String, TreeMap<Double,Double>>();
		HashMap<String, TreeMap<Double, Double>> stake_winnings = new HashMap<String, TreeMap<Double,Double>>();

		// iterate
		for (BetAdvisorElement element : betAdvisorList) {
			String tipster = element.getTipster();
			double odds = element.getOdds();
			if(odds < 1)
				System.out.println("NO");
			double profit = element.getProfit();
			double stake = element.getTake();
			String type = element.getTypeOfBet().replaceAll(" 1ST HALF", "");
			double profit_one_unit = 0;
			if(profit < 0)
				profit_one_unit = -1;
			else if(profit > 0)
				profit_one_unit = odds - 1;
			
			if (winningsMap.containsKey(tipster)) {
				counterMap.put(tipster, counterMap.get(tipster) + 1);
				winningsMap.put(tipster, winningsMap.get(tipster) + profit_one_unit);
				TreeMap<Double, Double> stakeFreq = stake_frequency.get(tipster);
				TreeMap<Double, Double> stakeWins = stake_winnings.get(tipster);
				if(stakeFreq.containsKey(stake)){
					stakeFreq.put(stake, stakeFreq.get(stake) + 1);
					stakeWins.put(stake, stakeWins.get(stake) + profit_one_unit);
				}
				else{
					stakeFreq.put(stake, 1.0);
					stakeWins.put(stake, profit_one_unit);
				}
			} else {
				counterMap.put(tipster, 1.0);
				winningsMap.put(tipster, profit_one_unit);
				TreeMap<Double, Double> stakeFreq = new TreeMap<Double, Double>();
				stakeFreq.put(stake, 1.0);
				stake_frequency.put(tipster, stakeFreq);
				TreeMap<Double, Double> stakeWins = new TreeMap<Double, Double>();
				stakeWins.put(stake, profit_one_unit);
				stake_winnings.put(tipster, stakeWins);

			}
		}
		
		//calculate avg yields
		Map<String, TipsterStats> result = new HashMap<String, TipsterStats>();
		for (String s : winningsMap.keySet()) {
			winningsMap.put(s, winningsMap.get(s) / counterMap.get(s));
			TreeMap<Double, Double> stakeWins = stake_winnings.get(s);
			TreeMap<Double, Double> stakeFreq = stake_frequency.get(s);
			for(Double d : stakeWins.keySet()){
				stakeWins.put(d, stakeWins.get(d) / stakeFreq.get(d));
			}
			result.put(s, new TipsterStats(s, counterMap.get(s), winningsMap.get(s), stakeWins, stakeFreq));
		}
		return result;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Map.Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

		return result;
	}


}
