package bettingBot;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import jayeson.lib.datastructure.PivotType;
import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.recordfetcher.DeltaCrawlerSession;
import mailParsing.BetAdvisorEmailParser;
import mailParsing.BetInformations;
import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;
import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;
import bettingBot.entities.BetTicket;
import bettingBot.gui.BettingBotFrame;
import eastbridge.BettingApi;

public class BettingBot {
	
	private static final int numberOfDaysToCheck = 2;
	private BettingBotFrame mainFrame = new BettingBotFrame();
	private BettingBotDatabase dataBase;
	
	public void run(){
		
		// Initialize GUI
		mainFrame.setVisible(true);
		
		// Initialize Database
		try {
			dataBase = new BettingBotDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
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
	
		// Loop over tips and available events
		while (true) {		
			// Get current funds
			double funds = BettingApi.getUserCreditAsDouble(-1);
			mainFrame.setFunds(funds);
			
			// Emergency stop
			if(funds < 950){
				System.out.println("INSUFFICIENT FUNDS");
				System.exit(-1);
			}
			
			// Get Events
			// this call will block if the connection to the feed fails. 
			// If auto reconnection is enabled, it will automatically reconnect to the feed for you
			cs.waitConnection();
			Collection<SoccerEvent> events = cs.getAllEvents();
			
			// Get parsed mails
			// Date of the oldest mail to check
			Date getMailsSinceDate = new Date(System.currentTimeMillis() - numberOfDaysToCheck * 24 * 60 * 60 * 1000);
			List<ParsedTextMail> mails = reader.read("noreply@betadvisor.com", getMailsSinceDate);
			List<BetInformations> tipps = new ArrayList<BetInformations>();
			for(ParsedTextMail mail : mails){
				if(mail.subject.indexOf("Tip subscription") != -1){
					tipps.add(BetAdvisorEmailParser.parseMail(mail.content));
				}
			}
				
			/* Iterate over all tipps */
			for(int t = 0; t < tipps.size(); t++){
				
				BetInformations tipp = tipps.get(t);
				
				/* The teams of this tipp */
				String tippHost = tipp.host;
				String tippGuest = tipp.guest;
				
				/* The date when the game is starting */
				Date tippStartDate = tipp.date;		
				long tippStartUnixTime = tippStartDate.getTime();
				
				/* We can not bet on events from the past */
				if(tippStartUnixTime < System.currentTimeMillis())
					continue;
				
				/* Check if we have a new tipp */
				if(dataBase.isTippInDatabase(tipp.event, tipp.tipster, tipp.date.getTime())){
					mainFrame.addEvent("Tipp already processed");
					continue;
				}
				mainFrame.addEvent("New Tipp received:\n" + tipp.toString());
				
				/* Inner loop: iterate over currently available events */
				for (SoccerEvent event : events) {
					
					Collection<Record> records = event.getRecords();	
					/* If there are no records for this event, we can not bet on it */
					if (records.size() == 0) 
						continue;
					
					/* Get some event variables */
					String eventHost = event.getHost();
					String eventGuest = event.getGuest();
					
					/* We do not bet on number of corners */
					if(eventHost.indexOf("No.of Corners") != -1)
						continue;
					if(eventGuest.indexOf("No.of Corners") != -1)
						continue;
					/* We do not bet on the nth corner */
					if(eventHost.indexOf(" Corner") != -1)
						continue;
					if(eventGuest.indexOf(" Corner") != -1)
						continue;
					/* We do not bet on "Home Team" */
					if(eventHost.indexOf("Home Team") == 0)
						continue;
										
					/* The date when the event starts */
					Date eventStartDate = new Date(event.getLiveState().getStartTime() * 1000);
					long eventStartUnixTime = eventStartDate.getTime();
					
					/* Check if start Time matches */
					if(tippStartUnixTime == eventStartUnixTime){
											
						/* Check if teams match, using different methods */
						boolean teamsMatch = eventHost.equalsIgnoreCase(tippHost) || eventGuest.equalsIgnoreCase(tippGuest);
						
						if(!teamsMatch)
							teamsMatch = TeamMapping.teamsMatch(eventHost, tippHost) || TeamMapping.teamsMatch(eventGuest, tippGuest);
						
						/*/ Teams match, get the right record and make a bet */
						if(teamsMatch){
							
							// Match Odds tipp, get the best matching record and make a bet
							if(tipp.typeOfBet.equalsIgnoreCase("Match Odds")){
								String betOn = "INVALID";
								if(tipp.betOn.equalsIgnoreCase(tipp.host))
									betOn = "one";
								else if(tipp.betOn.equalsIgnoreCase(tipp.guest))
									betOn = "two";
								else if(tipp.betOn.equalsIgnoreCase("draw"))
									betOn = "draw";
								
								double bestOdd = 0;
								String bestCompany = "";
								String bestMarket = "";
								String bestEventId = "";
								int bestOddId = 0;
								double bestMinStake = 0;
								
								for(Record record : records){
																	
									if(record.getPivotType() == PivotType.ONE_TWO && record.getTimeType().name().equals("FULL_TIME")){
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										BetTicket betTicket = BetTicket.fromJson(betTicketString);
										// Check for best Odds
										if(betTicket.getCurrentOdd() > bestOdd){
											bestOdd = betTicket.getCurrentOdd();
											bestOddId = oddId;
											bestCompany = company;
											bestMarket = market;
											bestEventId = eventId;
											bestMinStake = betTicket.getMinStake();
										}										
									}									
								}
								if(bestOdd > 0){
//									String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, bestMinStake, true, -1, -1);
									System.out.println();
								}
							}
							
							// Over /Under  tipp, get the best matching record and make a bet
							if(tipp.typeOfBet.equalsIgnoreCase("Over / Under")){
								String betOn = "INVALID";
								if(tipp.betOn.indexOf("Over") == 0)
									betOn = "over";
								else if(tipp.betOn.indexOf("Under") == 0)
									betOn = "under";
								
								double bestOdd = 0;
								String bestCompany = "";
								String bestMarket = "";
								String bestEventId = "";
								int bestOddId = 0;
								double bestMinStake = 0;
								
								for(Record record : records){
									
									// Check if the tipp and the record have the same pivot value
									double tippPivotValue = tipp.pivotValue;
									double recordPivotValue = record.getPivotValue();
									if(tippPivotValue != recordPivotValue)
										continue;								
									
									if(record.getPivotType() == PivotType.TOTAL && record.getTimeType().name().equals("FULL_TIME")){
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String res = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										BetTicket betTicket = BetTicket.fromJson(res);
										
										// Check for best odds
										if(betTicket.getCurrentOdd() > bestOdd){
											bestOdd = betTicket.getCurrentOdd();
											bestOddId = oddId;
											bestCompany = company;
											bestMarket = market;
											bestEventId = eventId;
											bestMinStake = betTicket.getMinStake();
										}			
									}
									if(bestOdd > 0){
//										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, bestMinStake, true, -1, -1);
										System.out.println();										
									}	
								}		
							}	
							if(tipp.typeOfBet.equalsIgnoreCase("Asian handicap")){
								String betOn = "INVALID";
								if(tipp.betOn.equals(tipp.host)){
									if(tipp.pivotBias.equals("HOST")){
										betOn = "give";
									}
									if(tipp.pivotBias.equals("GUEST")){
										betOn = "take";
									}
								}
								else if(tipp.betOn.equals(tipp.guest)){
									if(tipp.pivotBias.equals("GUEST")){
										betOn = "give";
									}
									if(tipp.pivotBias.equals("HOST")){
										betOn = "take";
									}
								}
								
								double bestOdd = 0;
								String bestCompany = "";
								String bestMarket = "";
								String bestEventId = "";
								int bestOddId = 0;
								double bestMinStake = 0;
								BetTicket bestBetTicket = null;
								
								for(Record record : records){
									// Check if the tipp and the record have the same pivot value
									double tippPivotValue = tipp.pivotValue;
									double recordPivotValue = record.getPivotValue();
									if(tippPivotValue != recordPivotValue)
										continue;	
																							
									if(record.getPivotType() == PivotType.HDP && record.getTimeType().name().equals("FULL_TIME")){
										String tippPivotBias = tipp.pivotBias;
										String recordPivotBias = record.getPivotBias().name();
										if(!tippPivotBias.equalsIgnoreCase(recordPivotBias))
											continue;
										
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String res = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										BetTicket betTicket = BetTicket.fromJson(res);
										
										// Check for best odds
										if(betTicket.getCurrentOdd() > bestOdd){
											bestBetTicket = betTicket;
											bestOdd = betTicket.getCurrentOdd();
											bestOddId = oddId;
											bestCompany = company;
											bestMarket = market;
											bestEventId = eventId;
											bestMinStake = betTicket.getMinStake();
										}		
									}	
								}
								if(bestOdd > 0 && bestOdd + 1 > tipp.noBetUnder){
									if(bestMinStake < 20){
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, bestMinStake, true, -1, -1);
										Bet bet = Bet.fromJson(betString);
										System.out.println(bestBetTicket);
										try {
											dataBase.addBetInformations(tipp);
											dataBase.addBet(bet);
										} catch (SQLException e) {
											e.printStackTrace();
											System.exit(-1);
										}
										mainFrame.addEvent("Tipp processed:\n" + tipp.toString());
										mainFrame.addEvent("BetTicket Received:\n" + bestBetTicket.toString());
									}							
								}	
							}									
						}
					}
				}				
			}
			
			// Get open Bets
			List<Bet> bets = dataBase.getAllBets();
			String openBets = "Running bets:\n";
			for(int b = 0; b < bets.size(); b++){
				Bet bet = bets.get(b);
				// Update bet status
				if(bet.getBetStatus() == 1){
					String betString = BettingApi.getBetStatus(bet.getId());
					Bet newBet = Bet.fromJson(betString);
					if(newBet.getBetStatus() != 1){
						bet.setBetStatus(newBet.getBetStatus());
						dataBase.updateBet(bet.getId(), newBet.getBetStatus());
					}
				}
				openBets += bet.toString() + "\n";
			}
			mainFrame.setBets(openBets);
			
			// Sleep at the end of while loop
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "UK"));
		BettingBot bot = new BettingBot();
		bot.run();
	}
}
