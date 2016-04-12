package eastbridgeLiquidityMining.regression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.datastructure.SoccerEventLiveState;
import weka.classifiers.Classifier;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;
import bettingBot.entities.BetTicket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import eastbridgeLiquidityMining.Mappings;
import eastbridgeLiquidityMining.database.EastbridgeLiquidityDatabase;

public class ArffCreator2 {

	private EastbridgeLiquidityDatabase database;
	private Instances data;
	public static FastVector attributes;
	
	static{
		attributes = new FastVector();
		attributes.addElement(new Attribute("Host", (FastVector) null));
		attributes.addElement(new Attribute("Guest", (FastVector) null));
		attributes.addElement(new Attribute("PivotType", (FastVector) null));
		attributes.addElement(new Attribute("PivotBias", (FastVector) null));
		attributes.addElement(new Attribute("Liga", (FastVector) null));
		attributes.addElement(new Attribute("OddType", (FastVector) null));
		attributes.addElement(new Attribute("Source", (FastVector) null));
		attributes.addElement(new Attribute("Country", (FastVector) null));
		attributes.addElement(new Attribute("Women", (FastVector) null));
		attributes.addElement(new Attribute("MajorLeague", (FastVector) null));
		attributes.addElement(new Attribute("Selection", (FastVector) null));
		attributes.addElement(new Attribute("U-Teams", (FastVector) null));
		attributes.addElement(new Attribute("PivotValue"));
		attributes.addElement(new Attribute("TimeBeforeStart"));
		attributes.addElement(new Attribute("CurrentOdd"));
		attributes.addElement(new Attribute("MarketValue"));
		attributes.addElement(new Attribute("LeagueRank"));
		attributes.addElement(new Attribute("MaxStake"));
	}
	
	public ArffCreator2(){
		try {
			database = new EastbridgeLiquidityDatabase();
			data = new Instances("Records", attributes, 0);
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		} 
	}
	
	
	public static String getCleanedNames(String name){
		String s = name.replaceAll("- .*", "");
		s = s.replaceAll(" \\d{2}:.*", "");
		s = s.replaceAll("(.*)( Am)", "$1");
		s = s.replaceAll("\\(.*", "").trim();
		return s;
	}
	
	public static String extractCountryInfo(String league){
		String[] splits = league.split(" ");
		String country = splits[0].replaceAll("-.*", "");
		return country;
	}
	
	public static boolean isIrrelevantEvent(SoccerEvent event){
		String host = event.getHost();
		String league = event.getLeague();
		if(host.contains("Corner") || host.contains("Total Bookings") || host.contains("Home Team") ||
				league.matches(".*\\s-\\s.*"))
			return true;
		else 
			return false;
	}
	
	public double getPivotValue(Record record){
		if(record.getPivotType().toString().equals("ONE_TWO"))
			return Instance.missingValue();
		else
			return record.getPivotValue();
	}
	
	public static boolean isWomenLeague(String league){
		if(league.contains("Women") || league.contains("Womens") || league.contains("(w)"))
			return true;
		else
			return false;
	}
	
	public static boolean isULeague(String league){
		if(league.matches(".*U\\d{2}.*"))
			return true;
		else
			return false;
	}
	
	public static boolean isMajorLeague(String league){
		if(league.contains("English Premier League") || league.contains("Spain Primera Laliga") ||
				league.contains("Italy Serie A") || league.contains("Germany-bundesliga I") || league.contains("France Ligue 1"))
			return true;
		else
			return false;
	}
	
	public static double getMarketValue(String host, String guest){
		float host_value = Mappings.getMarktwert(host);
		float guest_value = Mappings.getMarktwert(guest);
		double result = 0;
		if(host_value>0 && guest_value>0)
			return result += (host_value + guest_value);
		else
			return Instance.missingValue();
	}
	
	public static double capMaxStake(double maxStake){
		if(maxStake > 15000)
			return 15000;
		else 
			return maxStake;
	}
	
	public static double assignLeagueRank(String league){
		String[] splits = league.split(" ");
		for(int i = 0; i < splits.length; i++){
			String s = splits[i];
			if(s.equalsIgnoreCase("3rd") || s.equalsIgnoreCase("C") || s.equalsIgnoreCase("Lega") || 
					s.equalsIgnoreCase("3") || s.equalsIgnoreCase("Regional") ||
					s.contains("North") || s.contains("South") || 
					s.contains("West") || s.contains("East"))
				return 3;
			if(s.equalsIgnoreCase("2nd") || s.equalsIgnoreCase("B") || s.equalsIgnoreCase("Two") || 
					s.equalsIgnoreCase("2") || s.equalsIgnoreCase("Segunda"))
				return 2;
			if(s.equalsIgnoreCase("Uefa") || s.equalsIgnoreCase("AFC") || s.contains("World") 
					|| s.contains("Copa") || s.contains("Fifa"))
				return 0;
			if(s.equalsIgnoreCase("Premier") || s.equalsIgnoreCase("A") || s.equalsIgnoreCase("First") || 
					s.equalsIgnoreCase("1") || s.equalsIgnoreCase("1st") || s.equalsIgnoreCase("Pro") || 
					s.equalsIgnoreCase("I") || s.equalsIgnoreCase("Primera") ||
					s.equalsIgnoreCase("Super") || s.equalsIgnoreCase("Allsvenskan") ||
					s.equalsIgnoreCase("Major") || s.equalsIgnoreCase("Tippeligaen"))
				return 1;
		}
		return Instance.missingValue();
	}
	
	
	public void iterateAllEvents() throws JsonSyntaxException, SQLException{
		int recordCounter = 0;
		int eventCounter = 0;
		Gson gson = new Gson();
		Class recordClass = null;
		Class eventClass = null;
		File f0 = new File("event.dat");
		File f1 = new File("record.dat");
		if(f0.isFile() && f0.canRead() && f1.isFile() && f1.canRead()){
			try{
				FileInputStream in0 = new FileInputStream(f0);
				ObjectInputStream inO0 = new ObjectInputStream(in0);
				eventClass = (Class)inO0.readObject();
				
				FileInputStream in1 = new FileInputStream(f1);
				ObjectInputStream inO1 = new ObjectInputStream(in1);
				recordClass = (Class)inO1.readObject();
				inO0.close();
				inO1.close();
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println("Objects loaded from Inputstream");
		}	
		else{
			System.out.println("FILES NOT FOUND");
			return;
		}
		ResultSet rS = database.getAllEvents();
		while(rS.next()){
			eventCounter++;
			if(eventCounter%100 == 0)
				System.out.println(eventCounter);
			int eventId = rS.getInt("id");
			String eventJsonString = rS.getString("eventJsonString");
			SoccerEvent event = (SoccerEvent)gson.fromJson(eventJsonString, eventClass);
			if(isIrrelevantEvent(event))
				continue;
			
			String host = getCleanedNames(event.getHost());
			String guest = getCleanedNames(event.getGuest());
			String league = getCleanedNames(event.getLeague());
			
			ResultSet rS2 = database.getRecordsForEvent(eventId);


			while(rS2.next()){
				String betTicketJsonString = rS2.getString("betTicketJsonString");
				String recordJsonString = rS2.getString("recordJsonString");
				String selection = rS2.getString("selection");
				long eventStartTime = rS2.getLong("eventstarttime");
				long miningTime = rS2.getLong("time");
				BetTicket betTicket = (BetTicket)gson.fromJson(betTicketJsonString, BetTicket.class);
				Record record = (Record)gson.fromJson(recordJsonString, recordClass);
				
				if(betTicket.getMaxStake() == 0)
					continue;
				
				double[] vals = new double[data.numAttributes()];
				vals[0] = data.attribute(0).addStringValue(host);
				vals[1] = data.attribute(1).addStringValue(guest);
				vals[2] = data.attribute(2).addStringValue(record.getPivotType().toString());
				vals[3] = data.attribute(3).addStringValue(record.getPivotBias().toString());
				vals[4] = data.attribute(4).addStringValue(league);
				vals[5] = data.attribute(5).addStringValue(record.getOddType().toString());
				vals[6] = data.attribute(6).addStringValue(record.getSource());
				vals[7] = data.attribute(7).addStringValue(extractCountryInfo(league));
				vals[8] = data.attribute(8).addStringValue(Boolean.toString(isWomenLeague(league)));
				vals[9] = data.attribute(9).addStringValue(Boolean.toString(isMajorLeague(league)));
				vals[10] = data.attribute(10).addStringValue(selection);
				vals[11] = data.attribute(11).addStringValue(Boolean.toString(isULeague(league)));
				vals[12] = getPivotValue(record);
				vals[13] = (eventStartTime * 1000 - miningTime)/3600000;
				vals[14] = betTicket.getCurrentOdd();
				vals[15] = getMarketValue(host, guest);
				vals[16] = assignLeagueRank(league);
				
				vals[17] = capMaxStake(betTicket.getMaxStake());
				data.add(new Instance(1.0, vals));
				
//				if(getPivotValue(record)>20)
//					System.out.println();
				recordCounter++;
				
//				if(betTicket.getMaxStake() < 100){
//					System.out.println(host);
//					System.out.println(guest);
//					System.out.println(league);
//					System.out.println(betTicket.getMaxStake());
//					System.out.println(record.getOddType().toString());
//					System.out.println("****************************************************************");
//				}
				
			}

		}
		System.out.println(recordCounter);
		
	}
	
	private void useStringToNominalFilter(int start, int end){
		StringToNominal stn=new StringToNominal();
		stn.setAttributeRange(start + "-" + end);
		try {
			stn.setInputFormat(data);
			data=Filter.useFilter(data,stn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws JsonSyntaxException, SQLException {
//		ArffCreator2 analyser = new ArffCreator2();
//		analyser.iterateAllEvents();
//		analyser.useStringToNominalFilter(1, 12);
//		System.out.println(analyser.data);
//		ArffSaver saver = new ArffSaver();
//		saver.setInstances(analyser.data);
//		try {
//			saver.setFile(new File("EastBridge4.arff"));
//			saver.writeBatch();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		ArffReader arff;
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader("EastBridge4Backtest.arff"));
//			arff = new ArffReader(reader);
//			Instances data = arff.getStructure();
//			HashSet<String> set = new HashSet<String>();
//			Attribute leagues = data.attribute("Liga");
//			for(int i = 0; i < leagues.numValues(); i++)
//				set.add(leagues.value(i));
//			System.out.println();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader("EastBridge4BackTest.arff"));
			ArffReader arff = new ArffReader(reader);
			Instances struct = arff.getStructure();
			struct.setClassIndex(struct.numAttributes()-1);
			REPTree cls = (REPTree) weka.core.SerializationHelper.read("reptree2.model");

			Instance instance = new Instance(9);
			instance.setValue(struct.attribute(0), "HDP");
			instance.setValue(struct.attribute(1), "HOST");
			instance.setValue(struct.attribute(2), "Italy Serie A");
			instance.setValue(struct.attribute(3), "PIN");
			instance.setValue(struct.attribute(4), "take");
			instance.setValue(struct.attribute(5), 0.7);
			instance.setValue(struct.attribute(6), 14);
			instance.setValue(struct.attribute(7), 1.68);
			instance.setDataset(struct);
			
			double x = cls.classifyInstance(instance);
			System.out.println(x);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
