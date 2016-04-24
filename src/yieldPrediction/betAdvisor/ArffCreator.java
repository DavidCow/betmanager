package yieldPrediction.betAdvisor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;

public class ArffCreator {

	private Instances data = new Instances("Records", attributes, 0);;
	public static FastVector attributes = new FastVector();
	
	static{
		attributes = new FastVector();
		attributes.addElement(new Attribute("Tipster", (FastVector) null));
		attributes.addElement(new Attribute("TypeOfBet", (FastVector) null));
		attributes.addElement(new Attribute("Odds"));
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
			
			double[] vals = new double[data.numAttributes()];
			vals[0] = data.attribute(0).addStringValue(tipster);
			vals[1] = data.attribute(1).addStringValue(typeOfBet);
			vals[2] = odds;
			data.add(new Instance(1.0, vals));
		}
	}
	
	public static void main(String[] args) throws IOException {
		ArffCreator analyser = new ArffCreator();
		analyser.create();
		analyser.useStringToNominalFilter(1, 2);
		System.out.println(analyser.data);
		ArffSaver saver = new ArffSaver();
		saver.setInstances(analyser.data);
		try {
			saver.setFile(new File("Yield.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
