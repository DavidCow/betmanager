package yieldPrediction.betAdvisor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import weka.core.Instance;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;

public class StatsCalculation {

	private static final int numberOfClusters = 50;
	
	public static void calculateYields() throws Exception{
		double oddsRatio = 1;
		
		while(oddsRatio >= 0.9){
			double[] res = new double[numberOfClusters];
			double[] totalTake = new double[numberOfClusters];
			int[] numberOfBets = new int[numberOfClusters];
			
			// Model
			ClusterPrediction prediction = new ClusterPrediction("Yield.arff", "yieldCluster.model");
			
			// Load historical Data
			BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
			List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
			
			// Create each record
			for(int i = 0; i < betAdvisorList.size(); i++){
				BetAdvisorElement element = betAdvisorList.get(i);
				String tipster = element.getTipster();
				String typeOfBet = element.getTypeOfBet();
				typeOfBet = typeOfBet.toUpperCase();
				typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
				double odds = element.getOdds();
				if(!typeOfBet.equalsIgnoreCase("MATCH ODDS"))
					odds++;
				
				Instance record = prediction.createWekaInstance(tipster, typeOfBet, odds);
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
	}
	
	public static void calculateWinPercent() throws Exception{
		double[] wins = new double[numberOfClusters];
		int[] numberOfBets = new int[numberOfClusters];
		
		// Model
		ClusterPrediction prediction = new ClusterPrediction("Yield.arff", "yieldCluster.model");
		
		// Load historical Data
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
		// Create each record
		for(int i = 0; i < betAdvisorList.size(); i++){
			BetAdvisorElement element = betAdvisorList.get(i);
			String tipster = element.getTipster();
			String typeOfBet = element.getTypeOfBet();
			typeOfBet = typeOfBet.toUpperCase();
			typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
			double odds = element.getOdds();
			if(!typeOfBet.equalsIgnoreCase("MATCH ODDS"))
				odds++;
			
			Instance record = prediction.createWekaInstance(tipster, typeOfBet, odds);
			int cluster = prediction.predictCluster(record);
			numberOfBets[cluster]++;
			double w = 0;
			if(element.getProfit() == 0)
				w = 0.5;
			if(element.getProfit() > 0)
				w = 1;
			wins[cluster] += w;
			System.out.println(cluster);
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
		calculateWinPercent();
		calculateYields();
	}
}
