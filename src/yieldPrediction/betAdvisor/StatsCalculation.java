package yieldPrediction.betAdvisor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.clusterers.Clusterer;
import weka.core.Instance;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;

public class StatsCalculation {

	
	public static void calculateYields(List<BetAdvisorElement> betAdvisorList, double oddsRatio) throws Exception{
		
		// Model
		ClusterPredictionKmeans prediction = new ClusterPredictionKmeans("Yield.arff", "yieldCluster.model");
		
		//initialize result arrays
		int numberOfClusters = prediction.getNumberOfClusters();
		double[] res = new double[numberOfClusters];
		double[] totalTake = new double[numberOfClusters];
		int[] numberOfBets = new int[numberOfClusters];
		
		// Liquidity Model
		eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		
		// Create each record
		for(int i = 0; i < betAdvisorList.size(); i++){
			try{
				BetAdvisorElement element = betAdvisorList.get(i);
				String tipster = element.getTipster();
				String typeOfBet = element.getTypeOfBet();
				typeOfBet = typeOfBet.toUpperCase();
				typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
				double odds = element.getOdds();
				
				Instance record2 = liquidityModel.createWekaInstance(element);
				if(record2 == null)
					continue;
				double liquidity = -1;
				try {
					liquidity = liquidityModel.classifyInstance(record2);
				} catch (Exception e) {
					e.printStackTrace();
				}				
				double take = element.getTake();
				
				Instance record = prediction.createWekaInstance(tipster, typeOfBet, odds, liquidity, take);
				int cluster = prediction.predictCluster(record);
				numberOfBets[cluster]++;
				double p = -element.getTake();
				if(element.getProfit() == 0)
					p = 0;
				if(element.getProfit() > 0)
					p = element.getTake() * element.getOdds() * oddsRatio - element.getTake();
				res[cluster] += p;
				totalTake[cluster] += element.getTake();
				System.out.println(cluster);
			} catch(Exception e){
				
			}
		}		
		
		for(int i = 0; i < numberOfClusters; i++){
			res[i] /= totalTake[i];
		}	
		File f = new File("yieldModel_" + numberOfClusters + "_" + oddsRatio + ".dat");
        FileOutputStream fileOutput = new FileOutputStream(f);
        BufferedOutputStream br = new BufferedOutputStream(fileOutput);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(res);
        objectOutputStream.close();
		oddsRatio -= 0.01;	
	}
	
	public static Map<Integer, Double> calculateYieldsNoTipster(List<BetAdvisorElement> betAdvisorList, double oddsRatio) throws Exception{			
		// Model
		ClusterPrediction prediction = new ClusterPrediction("Yield_noTipster.arff", "em_seed100.model");
		
		//initialize result arrays
		int numberOfClusters = prediction.getNumberOfClusters();
		double[] res = new double[numberOfClusters];
		double[] totalTake = new double[numberOfClusters];
		int[] numberOfBets = new int[numberOfClusters];
		
		
		// Liquidity Model
		eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		
		// Create each record
		for(int i = 0; i < betAdvisorList.size(); i++){
			try{
				BetAdvisorElement element = betAdvisorList.get(i);
				String tipster = element.getTipster();
				String typeOfBet = element.getTypeOfBet();
				typeOfBet = typeOfBet.toUpperCase();
				typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
				double odds = element.getOdds();
				
				Instance record2 = liquidityModel.createWekaInstance(element);
				if(record2 == null)
					continue;
				double liquidity = -1;
				try {
					liquidity = liquidityModel.classifyInstance(record2);
				} catch (Exception e) {
					e.printStackTrace();
				}				
		
				Instance record = prediction.createWekaInstance(typeOfBet, odds, liquidity);
				int cluster = prediction.predictCluster(record);
				numberOfBets[cluster]++;
				double p = -1;
				if(element.getProfit() == 0)
					p = 0;
				if(element.getProfit() > 0)
					p = element.getOdds() * oddsRatio - 1;
				res[cluster] += p;
				totalTake[cluster]++;
//				System.out.println(cluster);
			} catch(Exception e){
				
			}
		}		
		
		for(int i = 0; i < numberOfClusters; i++){
			res[i] /= totalTake[i];
		}	
		
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for(int i = 0; i < numberOfClusters; i++){
			map.put(i, res[i]);
		}
		return map;	
	}
	
	public static Map<String, Map<Integer, Double>> calculateYieldsNoTipsterSeparatedTypes(List<BetAdvisorElement> betAdvisorList, double oddsRatio) throws Exception{			
		// Model
		ClusterPrediction prediction1X2 = new ClusterPrediction("yield_1X2_EM.arff", "yield_1X2_EM.model");
		ClusterPrediction predictionAH = new ClusterPrediction("yield_AH_EM.arff", "yield_AH_EM.model");
		ClusterPrediction predictionOU = new ClusterPrediction("yield_OU_EM.arff", "yield_OU_EM.model");
		
		//initialize result arrays
		int numberOfClusters1X2 = prediction1X2.getNumberOfClusters();
		double[] res1X2 = new double[numberOfClusters1X2];
		double[] totalTake1X2 = new double[numberOfClusters1X2];
		int[] numberOfBets1X2 = new int[numberOfClusters1X2];
		
		int numberOfClustersAH = predictionAH.getNumberOfClusters();
		double[] resAH = new double[numberOfClustersAH];
		double[] totalTakeAH = new double[numberOfClustersAH];
		int[] numberOfBetsAH = new int[numberOfClustersAH];
		
		int numberOfClustersOU = predictionOU.getNumberOfClusters();
		double[] resOU = new double[numberOfClustersOU];
		double[] totalTakeOU = new double[numberOfClustersOU];
		int[] numberOfBetsOU = new int[numberOfClustersOU];
		
		
		// Liquidity Model
		eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		
		// Create each record
		for(int i = 0; i < betAdvisorList.size(); i++){
			try{
				BetAdvisorElement element = betAdvisorList.get(i);
				String tipster = element.getTipster();
				String typeOfBet = element.getTypeOfBet();
				typeOfBet = typeOfBet.toUpperCase();
				typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
				double odds = element.getOdds();
				
				Instance record2 = liquidityModel.createWekaInstance(element);
				if(record2 == null)
					continue;
				double liquidity = -1;
				try {
					liquidity = liquidityModel.classifyInstance(record2);
				} catch (Exception e) {
					e.printStackTrace();
				}				
		
				if(typeOfBet.equalsIgnoreCase("ASIAN HANDICAP")){
					Instance record = predictionAH.createWekaInstance(odds, liquidity);
					int cluster = predictionAH.predictCluster(record);
					numberOfBetsAH[cluster]++;
					double p = -1;
					if(element.getProfit() == 0)
						p = 0;
					if(element.getProfit() > 0)
						p = element.getOdds() * oddsRatio - 1;
					resAH[cluster] += p;
					totalTakeAH[cluster]++;
				}
				else if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
					Instance record = prediction1X2.createWekaInstance(odds, liquidity);
					int cluster = prediction1X2.predictCluster(record);
					numberOfBets1X2[cluster]++;
					double p = -1;
					if(element.getProfit() == 0)
						p = 0;
					if(element.getProfit() > 0)
						p = element.getOdds() * oddsRatio - 1;
					res1X2[cluster] += p;
					totalTake1X2[cluster]++;
				}
				else if(typeOfBet.equalsIgnoreCase("OVER / UNDER")){
					Instance record = predictionOU.createWekaInstance(odds, liquidity);
					int cluster = predictionOU.predictCluster(record);
					numberOfBetsOU[cluster]++;
					double p = -1;
					if(element.getProfit() == 0)
						p = 0;
					if(element.getProfit() > 0)
						p = element.getOdds() * oddsRatio - 1;
					resOU[cluster] += p;
					totalTakeOU[cluster]++;
				}
//				System.out.println(cluster);
			} catch(Exception e){
				
			}
		}		
		
		for(int i = 0; i < numberOfClusters1X2; i++){
			res1X2[i] /= totalTake1X2[i];
		}	
		for(int i = 0; i < numberOfClustersAH; i++){
			resAH[i] /= totalTakeAH[i];
		}	
		for(int i = 0; i < numberOfClustersOU; i++){
			resOU[i] /= totalTakeOU[i];
		}	
		
		Map<Integer, Double> map1X2 = new HashMap<Integer, Double>();
		for(int i = 0; i < numberOfClusters1X2; i++){
			map1X2.put(i, res1X2[i]);
		}
		Map<Integer, Double> mapAH = new HashMap<Integer, Double>();
		for(int i = 0; i < numberOfClustersAH; i++){
			mapAH.put(i, resAH[i]);
		}
		Map<Integer, Double> mapOU = new HashMap<Integer, Double>();
		for(int i = 0; i < numberOfClustersOU; i++){
			mapOU.put(i, resOU[i]);
		}

		Map<String, Map<Integer, Double>> map = new HashMap<String, Map<Integer,Double>>();
		map.put("MATCH ODDS", map1X2);
		map.put("ASIAN HANDICAP", mapAH);
		map.put("OVER / UNDER", mapOU);
		return map;	
	}
	
	public static void calculateWinPercent() throws Exception{		
		// Model
		ClusterPredictionKmeans prediction = new ClusterPredictionKmeans("Yield.arff", "yieldCluster.model");
		
		int numberOfClusters = prediction.getNumberOfClusters();
		double[] wins = new double[numberOfClusters];
		int[] numberOfBets = new int[numberOfClusters];
		
		// Liquidity Model
		eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		
		// Load historical Data
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
		// Create each record
		for(int i = 0; i < betAdvisorList.size(); i++){
			try{
				BetAdvisorElement element = betAdvisorList.get(i);
				String tipster = element.getTipster();
				String typeOfBet = element.getTypeOfBet();
				typeOfBet = typeOfBet.toUpperCase();
				typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
				double odds = element.getOdds();
				if(!typeOfBet.equalsIgnoreCase("MATCH ODDS"))
					odds++;
				
				Instance record2 = liquidityModel.createWekaInstance(element);
				if(record2 == null)
					continue;
				double liquidity = -1;
				try {
					liquidity = liquidityModel.classifyInstance(record2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				double take = element.getTake();
				
				Instance record = prediction.createWekaInstance(tipster, typeOfBet, odds, liquidity, take);
				int cluster = prediction.predictCluster(record);
				numberOfBets[cluster]++;
				double w = 0;
				if(element.getProfit() == 0)
					w = 0.5;
				if(element.getProfit() > 0)
					w = 1;
				wins[cluster] += w;
				System.out.println(cluster);
			} catch(Exception e){
				
			}
		}		
		
		for(int i = 0; i < numberOfClusters; i++){
			wins[i] /= numberOfBets[i];
		}	
		File f = new File("winPercentModel_" + numberOfClusters + ".dat");
        FileOutputStream fileOutput = new FileOutputStream(f);
        BufferedOutputStream br = new BufferedOutputStream(fileOutput);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(wins);
        objectOutputStream.close();
	}
	
	public static void main(String[] args) throws Exception {

	}
}
