package eastbridgeLiquidityMining.regression;

import java.io.File;
import java.io.FileInputStream;
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
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
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
	private FastVector attributes;
	
	public ArffCreator2(){
		try {
			database = new EastbridgeLiquidityDatabase();
			attributes = new FastVector();
			attributes.addElement(new Attribute("Host", (FastVector) null));
			attributes.addElement(new Attribute("Guest", (FastVector) null));
			attributes.addElement(new Attribute("PivotType", (FastVector) null));
			attributes.addElement(new Attribute("PivotBias", (FastVector) null));
			attributes.addElement(new Attribute("PivotValue", (FastVector) null));
			attributes.addElement(new Attribute("Liga", (FastVector) null));
			attributes.addElement(new Attribute("OddType", (FastVector) null));
			attributes.addElement(new Attribute("Source", (FastVector) null));
			attributes.addElement(new Attribute("Country", (FastVector) null));
			attributes.addElement(new Attribute("Woman", (FastVector) null));
			attributes.addElement(new Attribute("MajorLeague", (FastVector) null));
			attributes.addElement(new Attribute("Selection", (FastVector) null));
			attributes.addElement(new Attribute("TimeBeforeStart"));
			attributes.addElement(new Attribute("CurrentOdd"));
			attributes.addElement(new Attribute("MarketValue"));
			attributes.addElement(new Attribute("LeagueRank"));
			attributes.addElement(new Attribute("MaxStake"));
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
	
	private String extractCountryInfo(String league){
		String[] splits = league.split(" ");
		String country = splits[0].replaceAll("-.*", "");
		return country;
	}
	
	private boolean isIrrelevantEvent(SoccerEvent event){
		String host = event.getHost();
		if(host.contains("Corner") || host.contains("Total Bookings") || host.contains("Home Team") )
			return true;
		else 
			return false;
	}
	
	private boolean isWomenLeague(String league){
		if(league.contains("Woman") || league.contains("Womans") || league.contains("(w)"))
			return true;
		else
			return false;
	}
	
	private boolean isMajorLeague(String league){
		if(league.contains("English Premier League") || league.contains("Spain Primera Laliga") ||
				league.contains("Italy Serie A") || league.contains("Germany-bundesliga I") || league.contains("France Ligue 1"))
			return true;
		else
			return false;
	}
	
	public static double assignLeagueRank(String league){
		String[] splits = league.split(" ");
		for(int i = 0; i < splits.length; i++){
			String s = splits[i];
			if(s.equalsIgnoreCase("2nd") || s.equalsIgnoreCase("B") || s.equalsIgnoreCase("Two") || 
					s.equalsIgnoreCase("2") || s.equalsIgnoreCase("Segunda"))
				return 2;
			if(s.equalsIgnoreCase("Uefa") || s.contains("World") || s.contains("Copa"))
				return 0;
			if(s.equalsIgnoreCase("3rd") || s.equalsIgnoreCase("C") || 
					s.equalsIgnoreCase("3") || s.equalsIgnoreCase("Regional") ||
					s.contains("North") || s.contains("South") || 
					s.contains("West") || s.contains("East"))
				return 3;
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
				vals[4] = data.attribute(4).addStringValue(record.getPivotString());
				vals[5] = data.attribute(5).addStringValue(league);
				vals[6] = data.attribute(6).addStringValue(record.getOddType().toString());
				vals[7] = data.attribute(7).addStringValue(record.getSource());
				vals[8] = data.attribute(8).addStringValue(extractCountryInfo(league));
				vals[9] = data.attribute(9).addStringValue(Boolean.toString(isWomenLeague(league)));
				vals[10] = data.attribute(10).addStringValue(Boolean.toString(isMajorLeague(league)));
				vals[11] = data.attribute(11).addStringValue(selection);
				vals[12] = (eventStartTime * 1000 - miningTime)/3600000;
				vals[13] = betTicket.getCurrentOdd();

				float host_value = Mappings.getMarktwert(host);
				float guest_value = Mappings.getMarktwert(guest);
				if(host_value>0 && guest_value>0)
					vals[14] = host_value + guest_value;
				else
					vals[14] = Instance.missingValue();
				
				vals[15] = assignLeagueRank(league);
				
				vals[16] = betTicket.getMaxStake();
				data.add(new Instance(1.0, vals));
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
		ArffCreator2 analyser = new ArffCreator2();
		analyser.iterateAllEvents();
		analyser.useStringToNominalFilter(1, 12);
		System.out.println(analyser.data);
		ArffSaver saver = new ArffSaver();
		saver.setInstances(analyser.data);
		try {
			saver.setFile(new File("EastBridge3.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
