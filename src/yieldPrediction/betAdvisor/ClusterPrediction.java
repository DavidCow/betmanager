package yieldPrediction.betAdvisor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.TreeSet;

import weka.classifiers.meta.Bagging;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class ClusterPrediction {

	private SimpleKMeans model;
	private Instances attribute_structure;
	
	public ClusterPrediction(String arff_path, String model_path){
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
	
	public int predictCluster(Instance record) throws Exception{
		return model.clusterInstance(record);
	}
}
