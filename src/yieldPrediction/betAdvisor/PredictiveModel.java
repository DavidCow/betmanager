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
	static{
		for(int i = 0; i < fileNames.length; i++){
            try {
				File f = new File(fileNames[i]);
	            FileInputStream fileInput = new FileInputStream(f);
	            BufferedInputStream br = new BufferedInputStream(fileInput);
	            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
    			yields[i] = (double[])objectInputStream.readObject();
    			objectInputStream.close();
            }catch(Exception e){
            	e.printStackTrace();
            	System.exit(-1);
            }
		}
	}
	
	private static final String winPercentFileName = "winPercentModel_50.dat";
	private static double[] winPercent;
	static{
        try {
			File f = new File(winPercentFileName);
            FileInputStream fileInput = new FileInputStream(f);
            BufferedInputStream br = new BufferedInputStream(fileInput);
            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
            winPercent = (double[])objectInputStream.readObject();
            objectInputStream.close();
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
	
	public Instance createWekaInstance(String tipster, String typeOfBet, double odds){
		Instance instance = new Instance(attribute_structure.numAttributes());
		instance.setValue(attribute_structure.attribute(0), tipster);
		instance.setValue(attribute_structure.attribute(1), typeOfBet);
		instance.setValue(attribute_structure.attribute(2), odds);
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
	
	public static void test() throws Exception{
		PredictiveModel model = new PredictiveModel("Yield.arff", "yieldCluster.model");
		// Load historical Data
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
		double m = 0;
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
			
			Instance record = model.createWekaInstance(tipster, typeOfBet, odds);
			double yield = model.predictYield(record, 1.1);
			double winPercent = model.predictWinPercent(record);
			//System.out.println("yield: " + yield);
			System.out.println("winPercent: " + winPercent);
			m = Math.max(m, winPercent);
		}		
		System.out.println("Max: " + m);
	}
	
	public static void main(String[] args) throws Exception {
		test();
	}
}
