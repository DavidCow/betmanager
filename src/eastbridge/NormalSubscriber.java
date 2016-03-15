package eastbridge;

/**
 *
 * @author Praveen
 */

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.datastructure.SoccerEventLiveState;
import jayeson.lib.recordfetcher.DeltaCrawlerSession;
import jayeson.lib.recordfetcher.DeltaFeedConverter;
import jayeson.lib.recordfetcher.RecordConverter;

public class NormalSubscriber {
	
	public static void main(String args[]) throws InterruptedException {

		Properties systemProps = System.getProperties();
		// setup key stores for secure connections
		systemProps.put("javax.net.ssl.trustStore", "conf/client.ts");
		systemProps.put("javax.net.ssl.keyStore", "conf/client.ks");
		systemProps.put("javax.net.ssl.trustStorePassword", "password");
		systemProps.put("javax.net.ssl.keyStorePassword", "password");
		//setup the configuration file
		systemProps.put("deltaCrawlerSessionConfigurationFile", "conf/deltaCrawlerSession.json");

		
		DeltaCrawlerSession cs = new DeltaCrawlerSession();
		// this will enable auto reconnection for your record fetcher in case of connection failure
		cs.connect();
		
		cs.waitConnection();
		
		Logger logger = Logger.getLogger(RecordConverter.class.getName()); 
		logger.setLevel(Level.OFF);
		
		logger = Logger.getLogger(DeltaFeedTracker.class.getName()); 
		logger.setLevel(Level.OFF);
		
		logger = Logger.getLogger(DeltaFeedConverter.class.getName()); 
		logger.setLevel(Level.OFF);
		
		logger = Logger.getLogger(DeltaCrawlerSession.class.getName()); 
		logger.setLevel(Level.OFF);
		
		
		while (true) {
			// this call will block if the connection to the feed fails. 
			// If auto reconnection is enabled, it will automatically reconnect to the feed for you
			cs.waitConnection();
			Collection<SoccerEvent> events = cs.getAllEvents();
			Collection<jayeson.lib.record.Record> records = cs.getAllRecords();
			
			System.out.println("-------------------"+events.size()+" events------------------------------------------------------------------");
			for (SoccerEvent e : events) {
				
				Collection<Record> rs = e.getRecords();	
				
				if (rs.size() == 0) continue;
				
				System.out.println(String.format("Id %s \t-\t Host %s \t-\t Guest %s \t-\t League %s", e.getEventId(), e.getHost(), e.getGuest(), e.getLeague()));
				SoccerEventLiveState state = e.getLiveState();
				
				
				System.out.println(String.format("LiveState Information---- Starttime: %d, Source: %s, Duration: %d, Score %d-%d -", state.getStartTime(), state.getSource(), state.getDuration(), state.getHostPoint(), state.getGuestPoint()));
				System.out.println(state.getSegment());
				
				Map<String, String> oids = e.getAllOriginalEventIds();
				System.out.println("Original event ids");
				for (Entry<String, String> et : oids.entrySet()) {
					System.out.print(et.getKey()+" - "+et.getValue()+" ");
				}
				System.out.println();
				
				Collection<Record> recs = e.getRecords();
				

				System.out.println("\n");
			}
			
			
			try {
				

			} catch (Exception ex) {
				System.out.println("Exception");
				ex.printStackTrace();
			}

			try {
				Thread.sleep(5000);
			} catch (Exception ex) {

			}
		}

		
	}
}
