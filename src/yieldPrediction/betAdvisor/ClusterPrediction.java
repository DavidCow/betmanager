package yieldPrediction.betAdvisor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import weka.classifiers.meta.Bagging;
import weka.clusterers.Clusterer;
import weka.clusterers.EM;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Normalize;

public class ClusterPrediction {

	private Instances attribute_structure;
	private Clusterer clusterer;
	private ArffReader arff;
	double odds_max;
	double odds_min;
	double liquidity_max;
	double liquidity_min;
	
	public ClusterPrediction(String arff_path, String model_path){
		try {
			clusterer = (Clusterer) weka.core.SerializationHelper.read(model_path);
			BufferedReader reader = new BufferedReader(new FileReader(arff_path));
			arff = new ArffReader(reader);
			attribute_structure = arff.getStructure();
			for(int i = 0; i < attribute_structure.numAttributes(); i++){
				if(attribute_structure.attribute(i).name().equals("Odds")){
					odds_max = arff.getData().attributeStats(i).numericStats.max;
					odds_min = arff.getData().attributeStats(i).numericStats.min;
				}
				else if(attribute_structure.attribute(i).name().equals("Liquidity")){
					liquidity_max = arff.getData().attributeStats(i).numericStats.max;
					liquidity_min = arff.getData().attributeStats(i).numericStats.min;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static double normalize(double max, double min, double value){
		return (value - min)/(max - min);
	}
	
	public Instance createWekaInstance(String typeOfBet, double odds, double liquidity){

		Instance instance = new Instance(attribute_structure.numAttributes());
		instance.setValue(attribute_structure.attribute(0), typeOfBet);
		instance.setValue(attribute_structure.attribute(1), normalize(odds_max, odds_min, odds));
		if(liquidity > 0)
			instance.setValue(attribute_structure.attribute(2), normalize(liquidity_max, liquidity_min, liquidity));
		else
			instance.setValue(attribute_structure.attribute(2), Instance.missingValue());
		return instance;
	}
	
	public Instance createWekaInstance(double odds, double liquidity){

		Instance instance = new Instance(attribute_structure.numAttributes());
		instance.setValue(attribute_structure.attribute(0), normalize(odds_max, odds_min, odds));
		if(liquidity > 0)
			instance.setValue(attribute_structure.attribute(1), normalize(liquidity_max, liquidity_min, liquidity));
		else
			instance.setValue(attribute_structure.attribute(1), Instance.missingValue());
		return instance;
	}
	
	public int predictCluster(Instance record) throws Exception{
		return clusterer.clusterInstance(record);
	}
	
	public int getNumberOfClusters() throws Exception{
		return clusterer.numberOfClusters();
	}
	
	public static void main(String[] args) throws Exception {
		ClusterPrediction em = new ClusterPrediction("Yield_noTipster.arff", "yieldNoTipsterEM.model");
		
		Instance i = em.createWekaInstance("MATCH ODDS", 5, 211);
		System.out.println(em.predictCluster(i));
		for(int a = 0; a < em.attribute_structure.numAttributes(); a++){
			System.out.println(em.attribute_structure.attributeStats(a).distinctCount);
		}

	}
}
