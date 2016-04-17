package eastbridgeLiquidityMining.regression;

import historicalData.HistoricalDataElement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.TreeSet;

import jayeson.lib.datastructure.PivotType;
import weka.classifiers.meta.Bagging;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import bettingBot.LeagueMapping;
import blogaBetHistoricalDataParsing.BlogaBetElement;

public class PredictiveModel {
	
	private TreeSet<String> model_leagues;
	private TreeSet<String> model_sources;
	private Instances attribute_structure;
	private Bagging cls;
	private HashMap<String,String> league_mapping;

	public PredictiveModel(String arff_path, String model_path){
		ArffReader arff;
		model_leagues = new TreeSet<String>();
		model_sources = new TreeSet<String>();
		league_mapping = new HashMap<String, String>();
		try {
			cls = (Bagging) weka.core.SerializationHelper.read(model_path);
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
	
	public Instance createWekaInstance(HistoricalDataElement bestSource, BlogaBetElement tip, double bestOdd){
		Instance instance = null;
		String league = ArffCreator.getCleanedNames(bestSource.getLeague());
		String source = bestSource.getSource();
		long timebeforestart = (tip.getGameDate().getTime() - tip.getPublicationDate().getTime())/3600000;
		String selection = "";
		PivotType pivotType = null;
		double pivotValue = Instance.missingValue();
		String pivotBias = tip.getPivotBias();
		String host = tip.getHost();
		String guest = tip.getGuest();
		if(tip.getTypeOfBet().indexOf("Match Odds") == 0){
			pivotBias = "NEUTRAL";
			pivotType = PivotType.ONE_TWO;
			if(tip.getTipTeam().equalsIgnoreCase("Draw"))
				selection = "draw";
			else{
				String h = tip.getHost();
				String g = tip.getGuest();
				
				if(tip.getTipTeam().equals(h))
					selection = "one";	
				if(tip.getTipTeam().equals(g))
					selection = "two";
			}		
		}
		if(tip.getTypeOfBet().indexOf("Over Under") == 0){
			pivotType = PivotType.TOTAL;
			if(tip.getTipTeam().toUpperCase().indexOf("OVER") != -1)
				selection = "over";
			if(tip.getTipTeam().toUpperCase().indexOf("UNDER") != -1)
				selection = "under";	
			pivotValue = tip.getPivotValue();
			pivotBias = "NEUTRAL";
		}
		if(tip.getTypeOfBet().indexOf("Asian Handicap") == 0){
			pivotType = PivotType.HDP;
			if(tip.getSelection().indexOf("+") != -1)
				selection = "take";
			else
				selection = "give";	
			pivotValue = tip.getPivotValue();
		}
		if(selection.isEmpty()){
			System.out.println();
		}
		instance = createWekaInstance(league, source, selection, pivotType, pivotValue, pivotBias, timebeforestart, bestOdd);
		return instance;				
	}
	
	public Instance createWekaInstance(HistoricalDataElement bestSource, BetAdvisorElement tip, double bestOdd){
		Instance instance = null;
		String league = ArffCreator.getCleanedNames(bestSource.getLeague());
		String source = bestSource.getSource();
		long timebeforestart = (tip.getGameDate().getTime() - tip.getPublicationDate().getTime())/3600000;
		String selection = "";
		PivotType pivotType = null;
		double pivotValue = Instance.missingValue();
		String pivotBias = "";
		String host = BetAdvisorParser.parseHostFromEvent(tip.getEvent());
		String guest = BetAdvisorParser.parseGuestFromEvent(tip.getEvent());
		if(tip.getTypeOfBet().indexOf("Match Odds") == 0){
			String selectionString = tip.getSelection();
			selectionString = selectionString.replaceAll(" Half time", "");
			pivotBias = "NEUTRAL";
			pivotType = PivotType.ONE_TWO;
			if(selectionString.equalsIgnoreCase("draw"))
				selection = "draw";
			else{
				String h = BetAdvisorParser.parseHostFromEvent(tip.getEvent());
				String g = BetAdvisorParser.parseGuestFromEvent(tip.getEvent());
				
				if(selectionString.equals(h))
					selection = "one";	
				if(selectionString.equals(g))
					selection = "two";
			}		
		}
		if(tip.getTypeOfBet().indexOf("Over / Under") == 0){
			String selectionString = tip.getSelection();
			selectionString = selectionString.replaceAll(" Half time", "");
			pivotType = PivotType.TOTAL;
			if(selectionString.indexOf("Over") == 0)
				selection = "over";
			if(selectionString.indexOf("Under") == 0)
				selection = "under";	
			int totalStart = selectionString.lastIndexOf("+") + 1;
			String totalString = selectionString.substring(totalStart);
			pivotValue = Double.parseDouble(totalString);
			pivotBias = "NEUTRAL";
		}
		if(tip.getTypeOfBet().indexOf("Asian handicap") == 0 || tip.getTypeOfBet().indexOf("Asian Handicap") == 0){
			pivotType = PivotType.HDP;
			if(tip.getSelection().indexOf("+") != -1)
				selection = "take";
			else
				selection = "give";	
			String selectionString = tip.getSelection();
			selectionString = selectionString.replace(" Half time", "");
			int pivotStart = selectionString.lastIndexOf("-") + 1;
			if(pivotStart != 0){
				try{
					String pivotString = selectionString.substring(pivotStart);
					pivotValue = Double.parseDouble(pivotString);
					if(selectionString.contains(host)){
						pivotBias = "HOST";
					}
					else if(selectionString.contains(guest)){
						pivotBias = "GUEST";
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			else{
				pivotStart = selectionString.lastIndexOf("+") + 1;
				if(pivotStart != -1){
					try{
						String pivotString = selectionString.substring(pivotStart);
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
		if(selection.isEmpty()){
			System.out.println();
		}
		instance = createWekaInstance(league, source, selection, pivotType, pivotValue, pivotBias, timebeforestart, bestOdd);
		return instance;
	}
	
	private void addLeagueMapping(String league){
		if(!league_mapping.containsKey(league)){
			if(model_leagues.contains(league))
				league_mapping.put(league, league);
			else{
				boolean foundMapping = false;
				for(String modelLeague : model_leagues){
					if(LeagueMapping.leaguesMatch(league, modelLeague)){
						league_mapping.put(league, modelLeague);
						foundMapping = true;
						break;
					}		
				}
				if(!foundMapping)
					league_mapping.put(league, "");
			}
		}
	}
	
	public Instance createWekaInstance(String league, String source, String selection, PivotType pivotType, 
			double pivotValue, String pivotBias, long timebeforestart, double bestOdd){
		try{
			Instance instance = new Instance(attribute_structure.numAttributes());
			instance.setValue(attribute_structure.attribute(0), pivotType.toString());
			if(pivotBias.equals("?"))
				instance.setValue(attribute_structure.attribute(1), Instance.missingValue());
			else
				instance.setValue(attribute_structure.attribute(1), pivotBias);
			
			addLeagueMapping(league);
			String mapped_league = league_mapping.get(league);
			if(mapped_league.equalsIgnoreCase(""))
				instance.setValue(attribute_structure.attribute(2), Instance.missingValue());
			else{
				instance.setValue(attribute_structure.attribute(2), mapped_league);	
			}
			
			if(model_sources.contains(source))
				instance.setValue(attribute_structure.attribute(3), source);
			else
				instance.setValue(attribute_structure.attribute(3), Instance.missingValue());
			
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
