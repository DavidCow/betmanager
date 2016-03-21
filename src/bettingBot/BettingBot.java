package bettingBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import mailParsing.BetAdvisorTip;
import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;
import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;
import bettingBot.entities.BetTicket;
import bettingBot.gui.BettingBotFrame;

import com.google.gson.Gson;

import eastbridge.BettingApi;

public class BettingBot {
	
	private static final int numberOfMessagesToCheck = 100;
	private BettingBotFrame mainFrame = new BettingBotFrame();
	private BettingBotDatabase dataBase;
	
	public void run(){
		
		// Initialize gson
		Gson gson = new Gson();
		
		// ArrayList which holds Tips, that will not be printed anymore
		ArrayList<BetAdvisorTip> seenTips = new ArrayList<BetAdvisorTip>();
		
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		// Loop over tips and available events
		while (true) {		
			// Get current funds
			double funds = BettingApi.getUserCreditAsDouble(-1);
			mainFrame.setFunds(funds);
			
			// Emergency stop
			if(funds < 800){
				System.out.println("INSUFFICIENT FUNDS");
				System.exit(-1);
			}
			
			// Get Events
			// this call will block if the connection to the feed fails. 
			// If auto reconnection is enabled, it will automatically reconnect to the feed for you
			cs.waitConnection();
			Collection<SoccerEvent> events = cs.getAllEvents();
			
			// Get parsed mails
			List<ParsedTextMail> mails = reader.read("noreply@betadvisor.com", numberOfMessagesToCheck);
			List<BetAdvisorTip> tips = new ArrayList<BetAdvisorTip>();
			for(ParsedTextMail mail : mails){
				if(mail.subject.indexOf("Tip subscription") != -1){
					tips.add(BetAdvisorEmailParser.parseTip(mail));
				}
			}
				
			/* Iterate over all tips */
			for(int t = 0; t < tips.size(); t++){
				
				BetAdvisorTip tip = tips.get(t);
				
				/* The teams of this tip */
				String tipHost = tip.host;
				String tipGuest = tip.guest;
				
				/* The date when the game is starting */
				Date tipStartDate = tip.date;		
				long tipStartUnixTime = tipStartDate.getTime();
				
				/* We can not bet on events from the past */
				if(tipStartUnixTime < System.currentTimeMillis())
					continue;
				
				/* Check if we have a new tip */
				if(dataBase.isTipInDatabase(tip.event, tip.tipster, tip.date.getTime())){
					continue;
				}

				if(!seenTips.contains(tip)){
					mainFrame.addEvent("New Tip received:\n" + tip.toString());
					seenTips.add(tip);
				}
				
				
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
					
					if(TeamMapping.teamsMatch(eventHost, tipHost) || TeamMapping.teamsMatch(eventGuest, tipGuest)){
//						System.out.println("Teams Match: " + eventHost + "  " + tipHost);
					}
					
					/* Check if start Time matches 
					 * 
					 * Do it with a 5 minute tolerance, because the dates from Eastbridge are sometimes a little inacurate
					 */
					if(Math.abs(tipStartUnixTime - eventStartUnixTime) < 5 * 60 * 1000){
											
						/* Check if teams match, using different methods */
						boolean teamsMatch = eventHost.equalsIgnoreCase(tipHost) || eventGuest.equalsIgnoreCase(tipGuest);
						
						if(!teamsMatch)
							teamsMatch = TeamMapping.teamsMatch(eventHost, tipHost) || TeamMapping.teamsMatch(eventGuest, tipGuest);
						
						/*/ Teams match, get the right record and make a bet */
						if(teamsMatch){
							
							// Match Odds tip, get the best matching record and make a bet
							if(tip.typeOfBet.indexOf("Match Odds") == 0 || tip.typeOfBet.indexOf("Match odds") == 0){
								String tipTimeType = "";
								if(tip.typeOfBet.equals("Match odds")){
									tipTimeType = "FULL_TIME";
								}
								else if(tip.typeOfBet.equals("Match Odds 1st Half")){
									tipTimeType = "HALF_TIME";
								}
								if(tipTimeType.isEmpty()){
									System.out.println("WRONG TIME TYPE");
									System.exit(-1);
								}
								
								String betOn = "INVALID";
								if(tip.betOn.equalsIgnoreCase(tip.host))
									betOn = "one";
								else if(tip.betOn.equalsIgnoreCase(tip.guest))
									betOn = "two";
								else if(tip.betOn.equalsIgnoreCase("draw"))
									betOn = "draw";
								
								double bestOdd = 0;
								String bestCompany = "";
								String bestMarket = "";
								String bestEventId = "";
								int bestOddId = 0;
								double bestMinStake = 0;
								BetTicket bestBetTicket = null;
								Record bestRecord = null;
								String bestBetTicketJsonString = "";
								
								for(Record record : records){
																	
									if(record.getPivotType() == PivotType.ONE_TWO && record.getTimeType().name().equals(tipTimeType)){
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										BetTicket betTicket = BetTicket.fromJson(betTicketString);
										// Check for best Odds
										if(betTicket.getCurrentOdd() > bestOdd){
											bestBetTicket = betTicket;
											bestOdd = betTicket.getCurrentOdd();
											bestOddId = oddId;
											bestCompany = company;
											bestMarket = market;
											bestEventId = eventId;
											bestMinStake = betTicket.getMinStake();
											bestRecord = record;
											bestBetTicketJsonString = betTicketString;
										}										
									}									
								}
								if(bestOdd > 0 && bestOdd > tip.noBetUnder){
									if(bestMinStake < 20){
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, bestMinStake, true, -1, -1);
										Bet bet = Bet.fromJson(betString);
										if(bet.getActionStatus() == 0){
											System.out.println(bestBetTicket);
											try {
												dataBase.addProcessedTip(tip);
												String tipJsonString = gson.toJson(tip);
												String eventJsonString = gson.toJson(event);
												String recordJsonString = gson.toJson(bestRecord);
												bet.setTipJsonString(tipJsonString);
												bet.setEventJsonString(eventJsonString);
												bet.setRecordJsonString(recordJsonString);
												bet.setSelection(betOn);
												bet.setTimeOfBet(System.currentTimeMillis());
												bet.setBetTicketJsonString(bestBetTicketJsonString);
												dataBase.addBet(bet);
											} catch (SQLException e) {
												e.printStackTrace();
												System.exit(-1);
											}
											mainFrame.addEvent("Tip processed:\n" + tip.toString());
											mainFrame.addEvent("BetTicket Received:\n" + bestBetTicket.toString());	
											mainFrame.addEvent("Bet Placed:\n" + bet.toString());	
										}
									}							
								}	
							}
							
							// Over /Under  tip, get the best matching record and make a bet
							else if(tip.typeOfBet.indexOf("Over / Under") == 0 && tip.typeOfBet.indexOf("Team") == -1){
								String tipTimeType = "";
								if(tip.typeOfBet.equals("Over / Under")){
									tipTimeType = "FULL_TIME";
								}
								else if(tip.typeOfBet.equals("Over / Under 1st Half")){
									tipTimeType = "HALF_TIME";
								}
								if(tipTimeType.isEmpty()){
									System.out.println("WRONG TIME TYPE");
									System.exit(-1);
								}
								
								String betOn = "INVALID";
								if(tip.betOn.indexOf("Over") == 0)
									betOn = "over";
								else if(tip.betOn.indexOf("Under") == 0)
									betOn = "under";
								
								double bestOdd = 0;
								String bestCompany = "";
								String bestMarket = "";
								String bestEventId = "";
								int bestOddId = 0;
								double bestMinStake = 0;
								BetTicket bestBetTicket = null;
								Record bestRecord = null;
								String bestBetTicketJsonString = "";
								
								for(Record record : records){
									
									// Check if the tip and the record have the same pivot value
									double tipPivotValue = tip.pivotValue;
									double recordPivotValue = record.getPivotValue();
									if(tipPivotValue != recordPivotValue)
										continue;								
									
									if(record.getPivotType() == PivotType.TOTAL && record.getTimeType().name().equals(tipTimeType)){
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										BetTicket betTicket = BetTicket.fromJson(betTicketString);
										
										// Check for best odds
										if(betTicket.getCurrentOdd() > bestOdd){
											bestBetTicket = betTicket;
											bestOdd = betTicket.getCurrentOdd();
											bestOddId = oddId;
											bestCompany = company;
											bestMarket = market;
											bestEventId = eventId;
											bestMinStake = betTicket.getMinStake();
											bestRecord = record;
											bestBetTicketJsonString = betTicketString;
										}			
									}
								}
								if(bestOdd > 0 && bestOdd + 1 > tip.noBetUnder){
									if(bestMinStake < 20){
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, bestMinStake, true, -1, -1);
										Bet bet = Bet.fromJson(betString);
										if(bet.getActionStatus() == 0){
											System.out.println(bestBetTicket);
											try {
												dataBase.addProcessedTip(tip);
												String tipJsonString = gson.toJson(tip);
												String eventJsonString = gson.toJson(event);
												String recordJsonString = gson.toJson(bestRecord);
												bet.setTipJsonString(tipJsonString);
												bet.setEventJsonString(eventJsonString);
												bet.setRecordJsonString(recordJsonString);
												bet.setSelection(betOn);
												bet.setTimeOfBet(System.currentTimeMillis());
												bet.setBetTicketJsonString(bestBetTicketJsonString);
												dataBase.addBet(bet);
											} catch (SQLException e) {
												e.printStackTrace();
												System.exit(-1);
											}
											mainFrame.addEvent("Tip processed:\n" + tip.toString());
											mainFrame.addEvent("BetTicket Received:\n" + bestBetTicket.toString());		
											mainFrame.addEvent("Bet Placed:\n" + bet.toString());	
										}
									}							
								}	
							}		
							else if(tip.typeOfBet.indexOf("Asian handicap") == 0 || tip.typeOfBet.indexOf("Asian Handicap") == 0){
								String tipTimeType = "";
								if(tip.typeOfBet.equals("Asian handicap")){
									tipTimeType = "FULL_TIME";
								}
								else if(tip.typeOfBet.equals("Asian Handicap 1st Half")){
									tipTimeType = "HALF_TIME";
								}
								if(tipTimeType.isEmpty()){
									System.out.println("WRONG TIME TYPE");
									System.exit(-1);
								}
								
								String betOn = "INVALID";
								if(tip.betOn.equals(tip.host)){
									if(tip.pivotBias.equals("HOST")){
										betOn = "give";
									}
									if(tip.pivotBias.equals("GUEST")){
										betOn = "take";
									}
								}
								else if(tip.betOn.equals(tip.guest)){
									if(tip.pivotBias.equals("GUEST")){
										betOn = "give";
									}
									if(tip.pivotBias.equals("HOST")){
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
								Record bestRecord = null;
								String bestBetTicketJsonString = "";
								
								for(Record record : records){
									// Check if the tip and the record have the same pivot value
									double tipPivotValue = tip.pivotValue;
									double recordPivotValue = record.getPivotValue();
									if(tipPivotValue != recordPivotValue)
										continue;	
																							
									if(record.getPivotType() == PivotType.HDP && record.getTimeType().name().equals(tipTimeType)){
										String tipPivotBias = tip.pivotBias;
										String recordPivotBias = record.getPivotBias().name();
										if(!tipPivotBias.equalsIgnoreCase(recordPivotBias))
											continue;
										
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										BetTicket betTicket = BetTicket.fromJson(betTicketString);
										
										// Check for best odds
										if(betTicket.getCurrentOdd() > bestOdd){
											bestBetTicket = betTicket;
											bestOdd = betTicket.getCurrentOdd();
											bestOddId = oddId;
											bestCompany = company;
											bestMarket = market;
											bestEventId = eventId;
											bestMinStake = betTicket.getMinStake();
											bestRecord = record;
											bestBetTicketJsonString = betTicketString;
										}		
									}	
								}
								if(bestOdd > 0 && bestOdd + 1 > tip.noBetUnder){
									if(bestMinStake < 20){
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, bestMinStake, true, -1, -1);
										Bet bet = Bet.fromJson(betString);
										if(bet.getActionStatus() == 0){
											System.out.println(bestBetTicket);
											try {
												dataBase.addProcessedTip(tip);
												String tipJsonString = gson.toJson(tip);
												String eventJsonString = gson.toJson(event);
												String recordJsonString = gson.toJson(bestRecord);
												bet.setTipJsonString(tipJsonString);
												bet.setEventJsonString(eventJsonString);
												bet.setRecordJsonString(recordJsonString);
												bet.setSelection(betOn);
												bet.setTimeOfBet(System.currentTimeMillis());
												bet.setBetTicketJsonString(bestBetTicketJsonString);
												dataBase.addBet(bet);
											} catch (SQLException e) {
												e.printStackTrace();
												System.exit(-1);
											}
											mainFrame.addEvent("Tip processed:\n" + tip.toString());
											mainFrame.addEvent("BetTicket Received:\n" + bestBetTicket.toString());		
											mainFrame.addEvent("Bet Placed:\n" + bet.toString());		
										}
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
			double currentlyInvested = 0;
			for(int b = 0; b < bets.size(); b++){
				Bet bet = bets.get(b);
				// Update bet status
				if(bet.getBetStatus() == 1){
					String betString = BettingApi.getBetStatus(bet.getId());
					Bet newBet = Bet.fromJson(betString);
					if(newBet.getBetStatus() != 1){
						bet.setBetStatus(newBet.getBetStatus());
						dataBase.updateBet(bet.getId(), newBet.getBetStatus());
						mainFrame.addEvent("Bet Status changed: " + bet);
					}
					else{
						// Currently invested
						currentlyInvested += bet.getBetAmount();
						
						// Infos about the event
						SoccerEvent event = null;
						Record record = null;
						if(eventClass != null && recordClass != null){
							event = (SoccerEvent)gson.fromJson(bet.getEventJsonString(), eventClass);	
							record = (Record)gson.fromJson(bet.getRecordJsonString(), recordClass);	
						}
						if(event != null){
							openBets += event.getHost() + " vs " + event.getGuest() +  "  " + record.getPivotString() + " " + bet.getSelection() + "\n";
						}
						openBets += bet.toString() + "\n\n";
					}
				}
			}
			mainFrame.setBets(openBets);
			mainFrame.setInvested(currentlyInvested);
			
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
