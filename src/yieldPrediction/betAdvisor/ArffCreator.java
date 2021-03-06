package yieldPrediction.betAdvisor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import blogaBetHistoricalDataParsing.BlogaBetElement;
import blogaBetHistoricalDataParsing.BlogaBetParser;

public class ArffCreator {

	private Instances data = new Instances("Records", attributes_noTipster, 0);;
	public static FastVector attributes = new FastVector();
	public static FastVector attributes_noTipster = new FastVector();
	
	static{
		attributes = new FastVector();
		attributes.addElement(new Attribute("Tipster", (FastVector) null));
		attributes.addElement(new Attribute("TypeOfBet", (FastVector) null));
		attributes.addElement(new Attribute("Odds"));
		attributes.addElement(new Attribute("Liquidity"));
		attributes.addElement(new Attribute("Take"));
		
		attributes_noTipster = new FastVector();
		attributes_noTipster.addElement(new Attribute("TypeOfBet", (FastVector) null));
		attributes_noTipster.addElement(new Attribute("Odds"));
		attributes_noTipster.addElement(new Attribute("Liquidity"));
	}
	
	private static Set<String> activeTipsters = new HashSet<String>(); 
	static{
		activeTipsters.add("Chris Tomas");
		activeTipsters.add("Gabriel Elias");
		activeTipsters.add("Jorge Aragundy");
		activeTipsters.add("Ivan Kacic");
		activeTipsters.add("Owen Garth");
		activeTipsters.add("Rosario Barone");
		activeTipsters.add("Don Sorensen");
		activeTipsters.add("Amar Sidran");
		activeTipsters.add("Robert Volkan");
		activeTipsters.add("Simeone Tassini");
		activeTipsters.add("Daniel Larsen");
		activeTipsters.add("Darijo Belic");
	}
	
	private static Set<String> activeTipstersBlogaBet = new HashSet<String>(); 
	static{
		activeTipsters.add("Adirondack");
		activeTipsters.add("batigol");
		activeTipsters.add("Borja");
		activeTipsters.add("Brazzookka");
		activeTipsters.add("LupoAlberto81");
		activeTipsters.add("onlydraw1");
		activeTipsters.add("rooney27");
		activeTipsters.add("Tsubasa10");
		activeTipsters.add("Mrbetting85");
		activeTipsters.add("anniruby");
		activeTipsters.add("atpkokkinakis");
		activeTipsters.add("Ksasha");
		activeTipsters.add("BrandonPark");
		activeTipsters.add("bettingspaintips");
		activeTipsters.add("BierBaron192");
		activeTipsters.add("XLNC");
		activeTipsters.add("claptone");
		activeTipsters.add("Taivo");
		activeTipsters.add("atpkokkinakis");
		activeTipsters.add("Brasil");
		activeTipsters.add("Kneipe");
		activeTipsters.add("bobic");
		activeTipsters.add("Editor");
		activeTipsters.add("atpkokkinakis");
		activeTipsters.add("inittowinit");
		activeTipsters.add("stefaan10");
	}
	
	private void useStringToNominalFilter(int start, int end){
		StringToNominal stn = new StringToNominal();
		stn.setAttributeRange(start + "-" + end);
		try {
			stn.setInputFormat(data);
			data=Filter.useFilter(data,stn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void create() throws IOException{
		
		// Load historical Data
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
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
				if(!typeOfBet.equalsIgnoreCase("MATCH ODDS"))
					odds++;
				Instance record = liquidityModel.createWekaInstance(element);
				if(record == null)
					continue;
				double liquidity = -1;
				try {
					liquidity = liquidityModel.classifyInstance(record);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				double[] vals = new double[data.numAttributes()];
				vals[0] = data.attribute(0).addStringValue(tipster);
				vals[1] = data.attribute(1).addStringValue(typeOfBet);
				vals[2] = odds;
				if(liquidity > 0)
					vals[3] = liquidity;
				else
					vals[3] = Instance.missingValue();
				vals[4] = element.getTake();
				data.add(new Instance(1.0, vals));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void createWithoutTipsterInformation() throws IOException{
		
		// Load historical Data
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
		// Liquidity Model
		eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		
		// Create each record
		for(int i = 0; i < betAdvisorList.size(); i++){
			try{
				BetAdvisorElement element = betAdvisorList.get(i);
				String tipster = element.getTipster();
				if(!activeTipsters.contains(tipster))
					continue;
				String typeOfBet = element.getTypeOfBet();
				typeOfBet = typeOfBet.toUpperCase();
				typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
				double odds = element.getOdds();
				Instance record = liquidityModel.createWekaInstance(element);
				if(record == null)
					continue;
				double liquidity = -1;
				try {
					liquidity = liquidityModel.classifyInstance(record);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				double[] vals = new double[data.numAttributes()];
				vals[0] = data.attribute(0).addStringValue(typeOfBet);
				vals[1] = odds;
				if(liquidity > 0)
					vals[2] = liquidity;
				else
					vals[2] = Instance.missingValue();
				data.add(new Instance(1.0, vals));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void createWithoutTipsterInformationBB() throws IOException{
		
		// Load historical Data
		BlogaBetParser blogaBetParser = new BlogaBetParser();
		List<BlogaBetElement> blogaBetList = blogaBetParser.parseSheets("blogaBetTipsterData/csv");
		
		// Liquidity Model
		eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		
		// Create each record
		for(int i = 0; i < blogaBetList.size(); i++){
			try{
				BlogaBetElement element = blogaBetList.get(i);
				String tipster = element.getTipster();
//				if(!activeTipsters.contains(tipster))
//					continue;
				String typeOfBet = element.getTypeOfBet();
				typeOfBet = typeOfBet.toUpperCase();
				if(typeOfBet.contains("CORNER") || element.getBestOdds()>15)
					continue;
				typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
				typeOfBet = typeOfBet.replaceAll(" HALF TIME", "");
				typeOfBet = typeOfBet.replaceAll(" TEAM", "");
				double odds = element.getBestOdds();
				Instance record = liquidityModel.createWekaInstance(element);
				if(record == null)
					continue;
				double liquidity = -1;
				try {
					liquidity = liquidityModel.classifyInstance(record);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				double[] vals = new double[data.numAttributes()];
				vals[0] = data.attribute(0).addStringValue(typeOfBet);
				vals[1] = odds;
				if(liquidity > 0)
					vals[2] = liquidity;
				else
					vals[2] = Instance.missingValue();
				data.add(new Instance(1.0, vals));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	
	public static void main(String[] args) throws IOException {
		ArffCreator analyser = new ArffCreator();
		analyser.createWithoutTipsterInformationBB();
		analyser.useStringToNominalFilter(1, 1);
		System.out.println(analyser.data);
		ArffSaver saver = new ArffSaver();
		saver.setInstances(analyser.data);
		try {
			saver.setFile(new File("Yield_noTipsterBB.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
