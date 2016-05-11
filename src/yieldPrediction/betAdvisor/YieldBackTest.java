package yieldPrediction.betAdvisor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;
import weka.core.Instance;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;

public class YieldBackTest {
	
	private static Set<String> activeTipsters = new HashSet<String>(); 
	static{
		activeTipsters.add("Chris Tomas");
		activeTipsters.add("Gabriel Elias");
		activeTipsters.add("Jorge Aragundy");
		activeTipsters.add("Ivan Kacic");
		activeTipsters.add("Owen Garth");
		activeTipsters.add("Rosario Barone");
		activeTipsters.add("Don Sorensen");
		activeTipsters.add("Amar Sidran");
		activeTipsters.add("Robert Volkan");
		activeTipsters.add("Simeone Tassini");
		activeTipsters.add("Daniel Larsen");
		activeTipsters.add("Darijo Belic");
	}
	
	/**
	 * group all tipster data by tipster name
	 * @return
	 * @throws Exception
	 */
	private static Map<String, List<BetAdvisorElement>> groupTipsterDate() throws Exception{
		Map<String, List<BetAdvisorElement>> result = new HashMap<String, List<BetAdvisorElement>>(); 
		
		// Load historical Data
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
		for(BetAdvisorElement be : betAdvisorList){
			String tipster = be.getTipster();
			if(result.containsKey(tipster)){
				result.get(tipster).add(be);
			}
			else{
				List<BetAdvisorElement> array = new ArrayList<BetAdvisorElement>();
				array.add(be);
				result.put(tipster, array);
			}
		}
		return result;
	}
	
	/**
	 * splits all data into test and training sets given split percentage
	 * @param splitPercentage
	 * @return
	 * @throws Exception
	 */
	public static Pair<List<BetAdvisorElement>, List<BetAdvisorElement>> splitTipsterData(double splitPercentage) throws Exception{
		ArrayList<BetAdvisorElement> trainingSet = new ArrayList<BetAdvisorElement>();
		ArrayList<BetAdvisorElement> testSet = new ArrayList<BetAdvisorElement>();
		
		Map<String, List<BetAdvisorElement>> tipsterMap = groupTipsterDate();
		for(String tipster : tipsterMap.keySet()){
			if(!activeTipsters.contains(tipster)){
				System.out.println(tipster);
				continue;
			}
			List<BetAdvisorElement> list = tipsterMap.get(tipster);
			int listSize = list.size();
			int splitIndex = (int) (listSize * splitPercentage);
			trainingSet.addAll(list.subList(0, splitIndex));
			testSet.addAll(list.subList(splitIndex, listSize));
		}
			
		Pair<List<BetAdvisorElement>, List<BetAdvisorElement>> res = new Pair<List<BetAdvisorElement>, List<BetAdvisorElement>>(trainingSet, testSet);
		return res;
	}
	
	public static void runFlatYieldTest(List<BetAdvisorElement> data, Map<Integer, Double> avgYieldMap, Map<String, TipsterStats> tipsterStats, double oddsratio) throws Exception{
		double result = 0;
		double numBets = 0;
		double resultFiltered = 0;
		double numBetsFiltered = 0;
		
		//load models
		ClusterPrediction em = new ClusterPrediction("Yield_noTipster.arff", "yieldNoTipsterEM.model");
		eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		
		for(BetAdvisorElement element : data){
			double odds = element.getOdds() * oddsratio;
			double profit = element.getProfit();
			String tipster = element.getTipster();
			if(!activeTipsters.contains(tipster)){
				System.out.println(tipster);
				continue;
			}
			String typeOfBet = element.getTypeOfBet();
			typeOfBet = typeOfBet.toUpperCase();
			typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
			
			//filter -ev bets
			Instance record2 = liquidityModel.createWekaInstance(element);
			if(record2 == null)
				continue;
			double liquidity = -1;
			try {
				liquidity = liquidityModel.classifyInstance(record2);
			} catch (Exception e) {
				e.printStackTrace();
			}	
			double p = 0;
			if(profit > 0)
				p = odds - 1;
			else if(profit < 0)
				p = -1;
			result += p;
			numBets++;
			Instance i = em.createWekaInstance(typeOfBet, element.getOdds(), liquidity);
			int cluster = em.predictCluster(i);
			double clusterAvgYield = avgYieldMap.get(cluster);
			double tipsterAvgYield = tipsterStats.get(tipster).avgYield;
			
			//calculate linear combination of both avg yield estimates
			double combinedAvgYield = 1 * clusterAvgYield + 0 * tipsterAvgYield;
			if(combinedAvgYield > 0){
				resultFiltered += p;
				numBetsFiltered++;
			}
			else{
				System.out.println(tipster);
			}
		}
		System.out.println("Num Bets: " + numBets);
		System.out.println("Num Bets Filtered: " + numBetsFiltered);
		System.out.println("Total Flat Winnings: " + result);
		System.out.println("Total Flat Winnings Filtered: " + resultFiltered);
		System.out.println("Yield per Bet: " + result/numBets);
		System.out.println("Yield per Bet Filtered: " + resultFiltered/numBetsFiltered);
	}

	public static void main(String[] args) throws Exception {
		Pair<List<BetAdvisorElement>, List<BetAdvisorElement>> pair = YieldBackTest.splitTipsterData(0.7);
		Map<String, TipsterStats> tipsterStatsMap = TipsterYieldCalculation.createTipsterStats(pair.getKey());
		Map<Integer, Double> yieldMap = StatsCalculation.calculateYieldsNoTipster(pair.getKey(), 0.98);
		runFlatYieldTest(pair.getValue(), yieldMap, tipsterStatsMap, 0.98);
//		for(Integer i : map.keySet())
//			System.out.println(i + " " + map.get(i));

	}

}
