package eastbridgeLiquidityMining;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import jayeson.lib.datastructure.PivotType;
import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.recordfetcher.DeltaCrawlerSession;

import com.google.gson.Gson;

import eastbridge.BettingApi;
import eastbridgeLiquidityMining.database.EastbridgeLiquidityDatabase;

public class EastbridgeLiquidityMiner {

	private EastbridgeLiquidityDatabase database;
	
	public EastbridgeLiquidityMiner(){
		try {
			database = new EastbridgeLiquidityDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void run(){
		// Initialize gson
		Gson gson = new Gson();
		
		// Initialize Properties for Data Crawler
		Properties systemProps = System.getProperties();
		// setup key stores for secure connections
		systemProps.put("javax.net.ssl.trustStore", "conf/client.ts");
		systemProps.put("javax.net.ssl.keyStore", "conf/client.ks");
		systemProps.put("javax.net.ssl.trustStorePassword", "password");
		systemProps.put("javax.net.ssl.keyStorePassword", "password");
		//setup the configuration file
		systemProps.put("deltaCrawlerSessionConfigurationFile", "conf/deltaCrawlerSession.json");
		
		// Initialize Data Crawler
		DeltaCrawlerSession cs = new DeltaCrawlerSession();
		// this will enable auto reconnection for your record fetcher in case of connection failure
		cs.connect();
		
		/* Initialize Classes for JSon deserialisation */
		Class eventClass = null;
		Class recordClass = null;
		
		boolean initialiseGson = true;
		
		File f0 = new File("event.dat");
		File f1 = new File("record.dat");
		if(f0.isFile() && f0.canRead() && f1.isFile() && f1.canRead()){
			initialiseGson = false;
			try{
				FileInputStream in0 = new FileInputStream(f0);
				ObjectInputStream inO0 = new ObjectInputStream(in0);
				eventClass = (Class)inO0.readObject();
				
				FileInputStream in1 = new FileInputStream(f1);
				ObjectInputStream inO1 = new ObjectInputStream(in1);
				recordClass = (Class)inO1.readObject();
				inO0.close();
				inO1.close();
				initialiseGson = false;
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println("Objects loaded from Inputstream");
		}
		
		while(initialiseGson){
			System.out.println("Initialising Gson");
			Collection<SoccerEvent> events = cs.getAllEvents();
			for (SoccerEvent event : events) {	
				
				Collection<Record> records = event.getRecords();	
				/* If there are no records for this event, we can not bet on it */
				if (records.size() == 0) 
					continue;
				
				Record record = records.iterator().next(); 
				eventClass = event.getClass();
				recordClass = record.getClass();	
				// Save Objects
				try{
					FileOutputStream out = new FileOutputStream("event.dat");
			        ObjectOutputStream oout = new ObjectOutputStream(out);
			        oout.writeObject(event.getClass());
					FileOutputStream out2 = new FileOutputStream("record.dat");
			        ObjectOutputStream oout2 = new ObjectOutputStream(out2);
			        oout2.writeObject(record.getClass());
			        oout.close();
			        oout2.close();
					initialiseGson = false;	
					System.out.println("Objects saved");
					break;
				}catch(Exception e){
					e.printStackTrace();
					System.exit(-1);
				}
				initialiseGson = false;
				break;
			}
			if(!initialiseGson)
				break;
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Loop over tips and available events
		while (true) {		
			// Get Events
			// this call will block if the connection to the feed fails. 
			// If auto reconnection is enabled, it will automatically reconnect to the feed for you
			cs.waitConnection();
			Collection<SoccerEvent> events = cs.getAllEvents();
			if(events.isEmpty())
				continue;
			
			/* Get time so time for all events in database will be the same */
			long time = System.currentTimeMillis();
			
			/* Iterate over currently available events */
			for (SoccerEvent event : events) {					
				String eventJsonString = gson.toJson(event);
				int eventId = database.addEvent(time, eventJsonString);	
				
				Collection<Record> records = event.getRecords();	
				for(Record record : records){
					String company = record.getSource().toLowerCase();
					String market = record.getOddType().toString().toLowerCase();
					String recordEventId = record.getEventId();
					int oddId = record.getOddId();
					
					String recordJsonString = gson.toJson(record);
					
					if(record.getPivotType() == PivotType.HDP){
						String betTicketJsonString = BettingApi.getBetTicket(company, "give", market, recordEventId, oddId, -1, -1);
						database.addRecord(time, recordJsonString, betTicketJsonString, eventId);
						betTicketJsonString = BettingApi.getBetTicket(company, "take", market, recordEventId, oddId, -1, -1);
						database.addRecord(time, recordJsonString, betTicketJsonString, eventId);
					}
					if(record.getPivotType() == PivotType.TOTAL){
						String betTicketJsonString = BettingApi.getBetTicket(company, "under", market, recordEventId, oddId, -1, -1);
						database.addRecord(time, recordJsonString, betTicketJsonString, eventId);
						betTicketJsonString = BettingApi.getBetTicket(company, "over", market, recordEventId, oddId, -1, -1);
						database.addRecord(time, recordJsonString, betTicketJsonString, eventId);				
					}
					if(record.getPivotType() == PivotType.ONE_TWO){
						String betTicketJsonString = BettingApi.getBetTicket(company, "one", market, recordEventId, oddId, -1, -1);
						database.addRecord(time, recordJsonString, betTicketJsonString, eventId);
						betTicketJsonString = BettingApi.getBetTicket(company, "two", market, recordEventId, oddId, -1, -1);
						database.addRecord(time, recordJsonString, betTicketJsonString, eventId);
						betTicketJsonString = BettingApi.getBetTicket(company, "draw", market, recordEventId, oddId, -1, -1);
						database.addRecord(time, recordJsonString, betTicketJsonString, eventId);
					}
				}
			}		
			
			try {
				Thread.sleep(10 * 60 * 1000);
		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		EastbridgeLiquidityMiner miner = new EastbridgeLiquidityMiner();
		miner.run();
	}
}
