package resultAnalysis;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.recordfetcher.DeltaCrawlerSession;
import mailParsing.GMailReader;
import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;

import com.google.gson.Gson;

public class ResultAnalyser {

	private BettingBotDatabase dataBase = null;	
	private static Gson gson = new Gson();
	
	public ResultAnalyser(){
		try {
			dataBase = new BettingBotDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
			
	public void analyseBets(){
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

		// Initialize mail parsing
		GMailReader reader = new GMailReader();
		
		/* Initialize Classes for JSon deserialisation */
		Class eventClass = null;
		Class recordClass = null;
		
		boolean initialiseGson = true;
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
				initialiseGson = false;
			}
			if(!initialiseGson)
				break;
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int numberOfRunninngBets = 0;
		int numberOfWonBets = 0;
		int numberOfLostBets = 0;
		List<Bet> bets = dataBase.getAllBets();
		for(int i = 0; i < bets.size(); i++){
			Bet bet = bets.get(i);
			Record record = (Record)gson.fromJson(bet.getRecordJsonString(), recordClass);
			SoccerEvent event = (SoccerEvent)gson.fromJson(bet.getEventJsonString(), eventClass);
			if(bet.getBetStatus() == 1){
				numberOfRunninngBets++;
			}
			if(bet.getBetStatus() == 4){
				numberOfWonBets++;
			}
			if(bet.getBetStatus() == 5){
				numberOfLostBets++;
			}
		}
		System.out.println("numberOfRunninngBets: " + numberOfRunninngBets);
		System.out.println("numberOfWonBets: " + numberOfWonBets);
		System.out.println("numberOfLostBets: " + numberOfLostBets);
	}
	
	public static void main(String[] args) {
		ResultAnalyser analyser = new ResultAnalyser();
		analyser.analyseBets();
	}
}
