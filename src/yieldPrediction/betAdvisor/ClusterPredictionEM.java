package yieldPrediction.betAdvisor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import weka.classifiers.meta.Bagging;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class ClusterPredictionEM {

	private EM model;
	private Instances attribute_structure;
	private double odds_max;
	private double odds_min;
	private double liquidity_max;
	private double liquidity_min;
	
	public ClusterPredictionEM(String arff_path, String model_path){
		ArffReader arff;
		try {
			model = (EM) weka.core.SerializationHelper.read(model_path);
			BufferedReader reader = new BufferedReader(new FileReader(arff_path));
			arff = new ArffReader(reader);
			attribute_structure = arff.getStructure();
			odds_max = arff.getData().attributeStats(1).numericStats.max;
			odds_min = arff.getData().attributeStats(1).numericStats.min;
			liquidity_max = arff.getData().attributeStats(2).numericStats.max;
			liquidity_min = arff.getData().attributeStats(2).numericStats.min;
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
	
	public int predictCluster(Instance record) throws Exception{
		return model.clusterInstance(record);
	}
	
	public static void main(String[] args) throws Exception {
		ClusterPredictionEM em = new ClusterPredictionEM("Yield_noTipster.arff", "yieldNoTipsterEM.model");
		Instance i = em.createWekaInstance("MATCH ODDS", 5, 211);
		System.out.println(em.predictCluster(i));

	}
}
