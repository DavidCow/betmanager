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

	private static final int numberOfClusters = 50;
	
	public static void calculateYields(List<BetAdvisorElement> betAdvisorList, double oddsRatio) throws Exception{
		double[] res = new double[numberOfClusters];
		double[] totalTake = new double[numberOfClusters];
		int[] numberOfBets = new int[numberOfClusters];
		
		// Model
		ClusterPredictionKmeans prediction = new ClusterPredictionKmeans("Yield.arff", "yieldCluster.model");
		
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
	
	public static Map<Integer, Double> calculateYieldsNoTipster(List<BetAdvisorElement> betAdvisorList, double oddsRatio, String arffPath, String modelPath) throws Exception{	
		double[] res = new double[numberOfClusters];
		double[] totalTake = new double[numberOfClusters];
		int[] numberOfBets = new int[numberOfClusters];
		
		// Model
		ClusterPrediction prediction = new ClusterPrediction("Yield_noTipster.arff", "yieldNoTipsterEM.model");
		
		
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
	
	public static void calculateWinPercent() throws Exception{
		double[] wins = new double[numberOfClusters];
		int[] numberOfBets = new int[numberOfClusters];
		
		// Model
		ClusterPredictionKmeans prediction = new ClusterPredictionKmeans("Yield.arff", "yieldCluster.model");
		
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
