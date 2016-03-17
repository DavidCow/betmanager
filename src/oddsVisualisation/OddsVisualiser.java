package oddsVisualisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;

import eastbridge.BettingApi;
import oddsVisualisation.gui.OddsVisualisationWindow;
import jayeson.lib.datastructure.PivotType;
import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.recordfetcher.DeltaCrawlerSession;
import mailParsing.BetAdvisorEmailParser;
import mailParsing.BetAdvisorTip;
import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;

public class OddsVisualiser {
	
	private OddsVisualisationWindow window = new OddsVisualisationWindow();
	
	public OddsVisualiser(){
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	public void run(){
			
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
		BetAdvisorEmailParser parser = new BetAdvisorEmailParser();	
	
		// Loop over tipps and available events
		while (true) {			
			// Get Events
			// this call will block if the connection to the feed fails. 
			// If auto reconnection is enabled, it will automatically reconnect to the feed for you
			cs.waitConnection();
			Collection<SoccerEvent> events = cs.getAllEvents();
			
			int index = 0;
			String[] strings = new String[events.size() + 1];
			double credit = BettingApi.getUserCreditAsDouble(-1);
			String s = "User Credit: " + credit + " GBP";
			strings[0] = s;
			index++;
			
			/* Inner loop: iterate over currently available events */
			for (SoccerEvent e : events) {
				s = e.getHost() + " vs " + e.getGuest();
				Collection<Record> rs = e.getRecords();	
				/* If there are no records for this event, we can not bet on it */
				if (rs.size() != 0){
					for(Record r : rs){
						if(r.getPivotType() == PivotType.ONE_TWO){
							// Get bet ticket
							String company = r.getSource().toLowerCase();
							String targetType = r.getPivotType().toString();
							String market = r.getOddType().toString().toLowerCase();
							String eventId = r.getEventId();
							double hostOdds = ((int)(r.getRateOver() * 100)) / 100.0;
							double drawOdds = ((int)(r.getRateEqual() * 100)) / 100.0;
							double guestOdds = ((int)(r.getRateUnder() * 100)) / 100.0;
							
							s += "          Host: " + hostOdds + "     Draw: " + drawOdds + "     Guest: " + guestOdds;
							break;
						}
						if(r.getPivotType() == PivotType.TOTAL){
							// Get bet ticket
							String company = r.getSource().toLowerCase();
							String targetType = r.getPivotType().toString();
							String market = r.getOddType().toString().toLowerCase();
							String eventId = r.getEventId();
							double hostOdds = ((int)(r.getRateOver() * 100)) / 100.0;
							double guestOdds = ((int)(r.getRateUnder() * 100)) / 100.0;
							
							s += "          Over: " + ( 1 + hostOdds) + "     Under: " + (1 + guestOdds);
							break;
						}
						if(r.getPivotType() == PivotType.HDP){
							// Get bet ticket
							String company = r.getSource().toLowerCase();
							String targetType = r.getPivotType().toString();
							String market = r.getOddType().toString().toLowerCase();
							String eventId = r.getEventId();
							double hostOdds = ((int)(r.getRateOver() * 100)) / 100.0;
							double guestOdds = ((int)(r.getRateUnder() * 100)) / 100.0;
							
							s += "          Home: " + (1 + hostOdds) + "     Guest: " + (1 + guestOdds);
							break;
						}	
					}
				}
				strings[index] = s;
				index++;
			}			
			window.setData(strings);
			
			// Sleep at the end of while loop
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		OddsVisualiser visualiser = new OddsVisualiser();
		visualiser.run();
	}
}
