package yieldPrediction.betAdvisor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;
import mailParsing.BetAdvisorTip;
import weka.core.Instance;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import bettingBot.entities.BetTicket;

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
		ClusterPrediction em = new ClusterPrediction("Yield_noTipster.arff", "em_seed100.model");
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
			if(combinedAvgYield > 0.01){
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
	
	public static void runFlatYieldTestSeparatedTypes(List<BetAdvisorElement> data, Map<String, Map<Integer, Double>> avgYieldMap, Map<String, TipsterStats> tipsterStats, double oddsratio) throws Exception{
		double result = 0;
		double numBets = 0;
		double resultFiltered = 0;
		double numBetsFiltered = 0;
		
		//load models
		ClusterPrediction prediction1X2 = new ClusterPrediction("yield_1X2_EM.arff", "yield_1X2_EM.model");
		ClusterPrediction predictionAH = new ClusterPrediction("yield_AH_EM.arff", "yield_AH_EM.model");
		ClusterPrediction predictionOU = new ClusterPrediction("yield_OU_EM.arff", "yield_OU_EM.model");
		eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		
		for(BetAdvisorElement element : data){
			double odds = element.getOdds() * oddsratio;
			double profit = element.getProfit();
			String tipster = element.getTipster();
			String typeOfBet = element.getTypeOfBet();
			typeOfBet = typeOfBet.toUpperCase();
			typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
			
			if(!activeTipsters.contains(tipster)){
				System.out.println(tipster);
				continue;
			}
			
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
			
			double clusterAvgYield = 0;
			if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
				Instance i = prediction1X2.createWekaInstance(element.getOdds(), liquidity);
				int cluster = prediction1X2.predictCluster(i);
				clusterAvgYield = avgYieldMap.get("MATCH ODDS").get(cluster);
			}
			else if(typeOfBet.equalsIgnoreCase("ASIAN HANDICAP")){
				Instance i = predictionAH.createWekaInstance(element.getOdds(), liquidity);
				int cluster = predictionAH.predictCluster(i);
				clusterAvgYield = avgYieldMap.get("ASIAN HANDICAP").get(cluster);
			}
			else if(typeOfBet.equalsIgnoreCase("OVER / UNDER")){
				Instance i = predictionOU.createWekaInstance(element.getOdds(), liquidity);
				int cluster = predictionOU.predictCluster(i);
				clusterAvgYield = avgYieldMap.get("OVER / UNDER").get(cluster);
			}
			else
				continue;
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
	
	public static double predictYield(BetAdvisorTip tip, BetTicket betTicket) throws Exception{
		//load models
		ClusterPrediction em = new ClusterPrediction("Yield_noTipster.arff", "em_seed100.model");	
		
		// Load historical Data
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
		// Predict cluster
		Instance i = em.createWekaInstance(tip.typeOfBet, tip.bestOdds, betTicket.getMaxStake());
		int cluster = em.predictCluster(i);

		// Calculate average yield
		double betOdds = betTicket.getCurrentOdd();
		if(tip.typeOfBet.indexOf("Over / Under") == 0 || tip.typeOfBet.indexOf("Asian handicap") == 0 || tip.typeOfBet.indexOf("Asian Handicap") == 0){
			betOdds++;
		}
		double oddsRatio = Math.min(1,  betOdds / tip.bestOdds);
		Map<Integer, Double> yieldMap = StatsCalculation.calculateYieldsNoTipster(betAdvisorList, oddsRatio);
		
		double yield = yieldMap.get(cluster);
		return yield;
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
