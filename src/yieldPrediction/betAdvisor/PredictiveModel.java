package yieldPrediction.betAdvisor;

import historicalData.HistoricalDataElement;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javafx.util.Pair;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class PredictiveModel {

	private static final int numberOfClusters = 50;
	private static final String[] fileNames = 
		{
		 "yieldModel_50_0.9099999999999999.dat",
		 "yieldModel_50_0.9199999999999999.dat",
		 "yieldModel_50_0.9299999999999999.dat",
		 "yieldModel_50_0.94.dat",
		 "yieldModel_50_0.95.dat", 
		 "yieldModel_50_0.96.dat",
		 "yieldModel_50_0.97.dat",
		 "yieldModel_50_0.98.dat",
		 "yieldModel_50_0.99.dat",
		 "yieldModel_50_1.0.dat"
		 };
	
	private static double[][] yields = new double[fileNames.length][];
	private static void loadYields(){
		for(int i = 0; i < fileNames.length; i++){
            try {
				File f = new File(fileNames[i]);
				if(f.exists()){
		            FileInputStream fileInput = new FileInputStream(f);
		            BufferedInputStream br = new BufferedInputStream(fileInput);
		            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
	    			yields[i] = (double[])objectInputStream.readObject();
	    			objectInputStream.close();
				}
            }catch(Exception e){
            	e.printStackTrace();
            	System.exit(-1);
            }
		}
	}
	
	private static final String winPercentFileName = "winPercentModel_50.dat";
	private static double[] winPercent;
	private static void loadWinProbs(){
        try {
			File f = new File(winPercentFileName);
			if(f.exists()){
	            FileInputStream fileInput = new FileInputStream(f);
	            BufferedInputStream br = new BufferedInputStream(fileInput);
	            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
	            winPercent = (double[])objectInputStream.readObject();
	            objectInputStream.close();
			}
        }catch(Exception e){
        	e.printStackTrace();
        	System.exit(-1);
        }	
	}
	
	private SimpleKMeans model;
	private Instances attribute_structure;
	
	public PredictiveModel(String arff_path, String model_path){
		ArffReader arff;
		try {
			model = (SimpleKMeans) weka.core.SerializationHelper.read(model_path);
			BufferedReader reader = new BufferedReader(new FileReader(arff_path));
			arff = new ArffReader(reader);
			attribute_structure = arff.getStructure();
			attribute_structure.setClassIndex(attribute_structure.numAttributes() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public Instance createWekaInstance(String tipster, String typeOfBet, double odds, double liquidity, double take){
		Instance instance = new Instance(attribute_structure.numAttributes());
		instance.setValue(attribute_structure.attribute(0), tipster);
		instance.setValue(attribute_structure.attribute(1), typeOfBet);
		instance.setValue(attribute_structure.attribute(2), odds);
		if(liquidity > 0)
			instance.setValue(attribute_structure.attribute(3), liquidity);
		else
			instance.setValue(attribute_structure.attribute(3), Instance.missingValue());
		instance.setValue(attribute_structure.attribute(4), take);
		return instance;
	}
	
	public double predictYield(Instance record, double oddsRatio) throws Exception{
		double[] yield = null;
		if(oddsRatio < 0.9)
			return -1;
		if(oddsRatio > 1)
			yield = yields[yields.length - 1];
		else{
			int mIndex = (int)Math.round(100 - 100 * oddsRatio);
			int index = yields.length - 1 - mIndex;
			yield = yields[index];
		}
		
		int cluster = model.clusterInstance(record);
		return yield[cluster];
	}
	
	public double predictWinPercent(Instance record) throws Exception{	
		int cluster = model.clusterInstance(record);
		return winPercent[cluster];
	}
	
	public static void test(List<BetAdvisorElement> betAdvisorList, double oddsRatio) throws Exception{
		
		double normalProfit = 0;
		double normalBets = 0;
		double filteredProfit = 0;
		double filteredBets = 0;
		
		PredictiveModel model = new PredictiveModel("Yield.arff", "yieldCluster.model");
		
		// Liquidity Model
		eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		
		double m = 0;
		// Create each record
		for(int i = 0; i < betAdvisorList.size(); i++){
			try{
				BetAdvisorElement element = betAdvisorList.get(i);
				String tipster = element.getTipster();
				String typeOfBet = element.getTypeOfBet();
				typeOfBet = typeOfBet.toUpperCase();
				typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
				double odds = element.getOdds() * oddsRatio;
				//if(!typeOfBet.equalsIgnoreCase("MATCH ODDS"))
				//	odds++;
				
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
				
				Instance record = model.createWekaInstance(tipster, typeOfBet, odds, liquidity, take);
				double yield = model.predictYield(record, oddsRatio);
//				double winPercent = model.predictWinPercent(record);
//				System.out.println("yield: " + yield);
//				System.out.println("winPercent: " + winPercent);
				
				if(element.getProfit() > 0){
					normalProfit += 100 * odds - 100;
					if(yield > 0){
						filteredProfit += 100 * odds - 100;
						filteredBets++;
					}	
				}
				else if(element.getProfit() < 0){
					normalProfit -= 100;
					if(yield > 0){
						filteredProfit -= 100;
						filteredBets++;
					}		
				}
				normalBets++;
				
				//m = Math.max(m, winPercent);
			}catch(Exception e){
				
			}
		}		
		//System.out.println("Max: " + m);
		System.out.println("Profit: " + normalProfit);
		System.out.println("Yield: " + normalProfit / normalBets);
		System.out.println("Bets: " + normalBets);
		System.out.println("Filtered Profit: " + filteredProfit);
		System.out.println("Filtered Yield: " + filteredProfit / filteredBets);
		System.out.println("Filtered Bets: " + filteredBets);
	}
	
	public static void main(String[] args) throws Exception {
		Pair<List<BetAdvisorElement>, List<BetAdvisorElement>> pair = YieldBackTest.splitTipsterData(0.7);
		StatsCalculation.calculateYields(pair.getKey(), 0.98);
		loadYields();
		test(pair.getValue(), 0.98);
	}
}
