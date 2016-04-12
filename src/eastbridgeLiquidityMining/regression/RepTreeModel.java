package eastbridgeLiquidityMining.regression;

import historicalData.HistoricalDataElement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

import jayeson.lib.datastructure.PivotType;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;

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
	
	public Instance createWekaInstance(HistoricalDataElement bestSource, BetAdvisorElement tip, double bestOdd){
		Instance instance = null;
		String league = ArffCreator.getCleanedNames(bestSource.getLeague());
		String source = bestSource.getSource();
		long timebeforestart = (tip.getGameDate().getTime() - tip.getPublicationDate().getTime())/3600000;
		String selection = "";
		PivotType pivotType = null;
		double pivotValue = Double.NEGATIVE_INFINITY;
		String pivotBias = "";
		String host = BetAdvisorParser.parseHostFromEvent(tip.getEvent());
		String guest = BetAdvisorParser.parseGuestFromEvent(tip.getEvent());
		if(tip.getTypeOfBet().equals("Match Odds")){
			pivotBias = "NEUTRAL";
			pivotType = PivotType.ONE_TWO;
			if(tip.getSelection().equalsIgnoreCase("draw"))
				selection = "draw";
			else{
				String h = BetAdvisorParser.parseHostFromEvent(tip.getEvent());
				String g = BetAdvisorParser.parseGuestFromEvent(tip.getEvent());
				
				if(tip.getSelection().equals(h))
					selection = "one";	
				if(tip.getSelection().equals(g))
					selection = "two";
			}		
		}
		if(tip.getTypeOfBet().equals("Over / Under")){
			pivotType = PivotType.TOTAL;
			if(tip.getSelection().indexOf("Over") == 0)
				selection = "over";
			if(tip.getSelection().indexOf("Under") == 0)
				selection = "under";	
			int totalStart = tip.getSelection().lastIndexOf("+") + 1;
			String totalString = tip.getSelection().substring(totalStart);
			pivotValue = Double.parseDouble(totalString);
			pivotBias = "NEUTRAL";
		}
		if(tip.getTypeOfBet().equals("Asian handicap")){
			pivotType = PivotType.HDP;
			if(tip.getSelection().indexOf("+") != -1)
				selection = "take";
			else
				selection = "give";	
			int pivotStart = tip.getSelection().lastIndexOf("-") + 1;
			if(pivotStart != -1){
				try{
					String pivotString = tip.getSelection().substring(pivotStart);
					pivotValue = Double.parseDouble(pivotString);
					if(tip.getSelection().contains(host)){
						pivotBias = "HOST";
					}
					else if(tip.getSelection().contains(guest)){
						pivotBias = "GUEST";
					}
				}catch(Exception e){
					
				}
			}
			else{
				pivotStart = tip.getSelection().lastIndexOf("+") + 1;
				if(pivotStart != -1){
					try{
						String pivotString = tip.getSelection().substring(pivotStart);
						pivotValue = Double.parseDouble(pivotString);
						if(tip.getSelection().contains(host)){
							pivotBias = "GUEST";
						}
						else if(tip.getSelection().contains(guest)){
							pivotBias = "HOST";
						}
					}catch(Exception e){
						
					}		
				}
			}
		}
		instance = createWekaInstance(league, source, selection, pivotType, pivotValue, pivotBias, timebeforestart, bestOdd);
		return instance;
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
