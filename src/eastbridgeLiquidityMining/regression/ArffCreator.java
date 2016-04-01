package eastbridgeLiquidityMining.regression;

import java.io.File;
import java.io.FileInputStream;
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
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import bettingBot.entities.BetTicket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import eastbridgeLiquidityMining.database.EastbridgeLiquidityDatabase;

public class ArffCreator {

	private EastbridgeLiquidityDatabase database;
	private Instances data;
	private FastVector attributes;
	
	public ArffCreator(){
		try {
			database = new EastbridgeLiquidityDatabase();
			attributes = new FastVector();
			attributes.addElement(new Attribute("ID"));
			attributes.addElement(new Attribute("Host", (FastVector) null));
			attributes.addElement(new Attribute("Guest", (FastVector) null));
			attributes.addElement(new Attribute("PivotType", (FastVector) null));
			attributes.addElement(new Attribute("PivotBias", (FastVector) null));
			attributes.addElement(new Attribute("PivotValue", (FastVector) null));
			attributes.addElement(new Attribute("Liga", (FastVector) null));
			attributes.addElement(new Attribute("OddType", (FastVector) null));
			attributes.addElement(new Attribute("Source", (FastVector) null));
			attributes.addElement(new Attribute("CurrentOdd"));
			attributes.addElement(new Attribute("MaxStake"));
			data = new Instances("Records", attributes, 0);
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		} 
	}
	
	
	public void iterateAllEvents() throws JsonSyntaxException, SQLException{
		int recordCounter = 0;
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
			int eventId = rS.getInt("id");
			String eventJsonString = rS.getString("eventJsonString");
			SoccerEvent event = (SoccerEvent)gson.fromJson(eventJsonString, eventClass);
			
			ResultSet rS2 = database.getRecordsForEvent(eventId);
			List<Record> records = new ArrayList<Record>();
			List<BetTicket> betTickets = new ArrayList<BetTicket>();
			Map<String, Integer> bestOdds = new HashMap<String, Integer>();

			while(rS2.next()){
				String betTicketJsonString = rS2.getString("betTicketJsonString");
				String recordJsonString = rS2.getString("recordJsonString");
				BetTicket betTicket = (BetTicket)gson.fromJson(betTicketJsonString, BetTicket.class);
				Record record = (Record)gson.fromJson(recordJsonString, recordClass);
				betTickets.add(betTicket);
				records.add(record);
				
			}
			for(int i = 0; i < records.size(); i++){
				Record r = records.get(i);
				BetTicket bt = betTickets.get(i);
				String key = r.getPivotType().toString() + r.getPivotValue();
				if(bestOdds.containsKey(key)){
					int idx = bestOdds.get(key);
					BetTicket betTicket_currentBest = betTickets.get(idx);
					if(bt.getCurrentOdd() > betTicket_currentBest.getCurrentOdd())
						bestOdds.put(key, i);
				}
				else{
					bestOdds.put(key, i);
				}
			}
			for(String s : bestOdds.keySet()){
				int idx = bestOdds.get(s);
				Record r = records.get(idx);
				BetTicket bt = betTickets.get(idx);
				double[] vals = new double[data.numAttributes()];
				vals[0] = recordCounter;
				vals[1] = data.attribute(1).addStringValue(event.getHost());
				vals[2] = data.attribute(2).addStringValue(event.getGuest());
				vals[3] = data.attribute(3).addStringValue(r.getPivotType().toString());
				vals[4] = data.attribute(4).addStringValue(r.getPivotBias().toString());
				vals[5] = data.attribute(5).addStringValue(r.getPivotString());
				vals[6] = data.attribute(6).addStringValue(event.getLeague());
				vals[7] = data.attribute(7).addStringValue(r.getOddType().toString());
				vals[8] = data.attribute(8).addStringValue(r.getSource());
				vals[9] = bt.getCurrentOdd();
				vals[10] = bt.getMaxStake();
				data.add(new Instance(1.0, vals));
				recordCounter++;
			}
		}
		
	}
	
	public static void main(String[] args) throws JsonSyntaxException, SQLException {
		ArffCreator analyser = new ArffCreator();
		analyser.iterateAllEvents();
		System.out.println(analyser.data);
	}
}
