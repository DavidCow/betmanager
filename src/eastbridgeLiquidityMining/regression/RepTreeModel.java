package eastbridgeLiquidityMining.regression;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

import jayeson.lib.datastructure.PivotType;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class RepTreeModel {
	
	private TreeSet<String> model_leagues;
	private TreeSet<String> model_sources;
	private Instances attribute_structure;
	private REPTree cls;

	public RepTreeModel(String arff_path, String model_path){
		ArffReader arff;
		model_leagues = new TreeSet<String>();
		model_sources = new TreeSet<String>();
		try {
			cls = (REPTree) weka.core.SerializationHelper.read(model_path);
			BufferedReader reader = new BufferedReader(new FileReader(arff_path));
			arff = new ArffReader(reader);
			attribute_structure = arff.getStructure();
			attribute_structure.setClassIndex(attribute_structure.numAttributes() - 1);
			Attribute leagues = attribute_structure.attribute("Liga");
			for(int i = 0; i < leagues.numValues(); i++)
				model_leagues.add(leagues.value(i));
			Attribute sources = attribute_structure.attribute("Source");
			for(int i = 0; i < sources.numValues(); i++)
				model_sources.add(sources.value(i));
			System.out.println();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public Instance createWekaInstance(String league, String source, String selection, PivotType pivotType, 
			double pivotValue, String pivotBias, long timebeforestart, double bestOdd){
		try{
			Instance instance = new Instance(attribute_structure.numAttributes());
			instance.setValue(attribute_structure.attribute(0), pivotType.toString());
			instance.setValue(attribute_structure.attribute(1), pivotBias);
			instance.setValue(attribute_structure.attribute(2), league);
			instance.setValue(attribute_structure.attribute(3), source);
			instance.setValue(attribute_structure.attribute(4), selection);
			instance.setValue(attribute_structure.attribute(5), pivotValue);
			instance.setValue(attribute_structure.attribute(6), timebeforestart);
			instance.setValue(attribute_structure.attribute(7), bestOdd);
			instance.setDataset(attribute_structure);
			return instance;	
		}catch(Exception e){
			return null;
		}
	}
	
	public double classifyInstance(Instance record) throws Exception{
		return cls.classifyInstance(record);
	}	
}
