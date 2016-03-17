package bettingBot.database.jsonTests;

import java.util.Collection;
import java.util.Properties;

import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.recordfetcher.DeltaCrawlerSession;

import com.google.gson.Gson;

public class JsonTestSimple {
	
	public static void main(String[] args) {	
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
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Collection<SoccerEvent> events = cs.getAllEvents();
		for(SoccerEvent event : events){
			Collection<Record> records = event.getRecords();
			if(records.isEmpty())
				continue;
			for(Record record : records){
				String gsonString = gson.toJson(record);
				System.out.println(gsonString);
				Record newRecord = gson.fromJson(gsonString, Record.class);
				System.out.println();
				break;
			}			
			break;
		}
	}
}
