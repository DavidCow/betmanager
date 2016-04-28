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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import jayeson.lib.datastructure.PivotType;
import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.recordfetcher.DeltaCrawlerSession;
import mailParsing.BetAdvisorEmailParser;
import mailParsing.BetAdvisorTip;
import mailParsing.BlogaBetEmailParser;
import mailParsing.BlogaBetTip;
import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;
import moneyManagement.StakeCalculation;
import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;
import bettingBot.entities.BetTicket;
import bettingBot.entities.ExtendedBetInformations;
import bettingBot.gui.BettingBotFrame;

import com.google.gson.Gson;
import com.google.protobuf.TextFormat.ParseException;

import eastbridge.BettingApi;

public class BettingBot {
	
	private static final int numberOfMessagesToCheck = 20;
	private BettingBotFrame mainFrame = new BettingBotFrame();
	private BettingBotDatabase dataBase;
	private static final int MAX_STAKE = 200;
	
	public void run(){
		
		// Initialize gson
		Gson gson = new Gson();
		
		// ArrayList which holds Tips, that will not be printed anymore
		Map<BetAdvisorTip, Integer> seenTips = new HashMap<BetAdvisorTip, Integer>();
		Map<BlogaBetTip, Integer> seenTipsBlogaBet = new HashMap<BlogaBetTip, Integer>();
		
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
		GMailReader reader = new GMailReader("vicentbet90@gmail.com", "bmw735tdi2");
		GMailReader readerBlogaBet = new GMailReader("blogabetcaptcha@gmail.com", "bmw735tdi");
		
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
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Loop over tips and available events
		while (true) {		
			// Get current funds
			double funds = BettingApi.getUserCreditAsDouble(-1);
			mainFrame.setFunds(funds);
			
			// Emergency stop
			if(funds < 55000 && funds != -1){
				System.out.println("INSUFFICIENT FUNDS");
				System.exit(-1);
			}
			
			// Get Events
			// this call will block if the connection to the feed fails. 
			// If auto reconnection is enabled, it will automatically reconnect to the feed for you
			cs.waitConnection();
			Collection<SoccerEvent> events = cs.getAllEvents();
			if(events.isEmpty())
				continue;
			
			// Get parsed mails
			List<ParsedTextMail> mails = new ArrayList<ParsedTextMail>();
			try{
				mails = reader.read("noreply@betadvisor.com", numberOfMessagesToCheck);
			} catch(Exception e){
				e.printStackTrace();
			}
			List<BetAdvisorTip> tips = new ArrayList<BetAdvisorTip>();
			for(ParsedTextMail mail : mails){
				if(mail.subject.indexOf("Tip subscription") != -1){
					try{
						tips.add(BetAdvisorEmailParser.parseTip(mail));
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			}
				
			/* Iterate over all tips */
			for(int t = 0; t < t; t++){
				
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
				
				boolean secondBetForTip = false;
				double betAmountForTip = 0;
				/* Check if we have a new tip  or if we have not bet as much as we wanted yet*/
				if(dataBase.isTipInDatabase(tip)){
					continue;
//					List<ExtendedBetInformations> betInformations = dataBase.getBetsForTip(tip.event, tip.tipster, tip.date.getTime());
//					for(int b = 0; b < betInformations.size(); b++){
//						double betAmount = betInformations.get(b).getBetAmount();
//						betAmountForTip += betAmount;
//					}
//					if(betAmountForTip >= MAX_STAKE){
//						continue;
//					}
//					secondBetForTip = true;
				}

				// Some variables for logging
				boolean newTip = false;
				boolean timeFoundAlreadyPrinted = false;

				/* Tip is not in database, we log once that we received it and add it to a List
				 * So it gets ignored in later prints
				 */
				if(!seenTips.containsKey(tip)){
					mainFrame.addEvent("New Tip received:\n" + tip.toString());
					seenTips.put(tip, 0);
					newTip = true;
				}
				else{
					int v = seenTips.get(tip);
					if(v < 5){
						newTip = true;
						seenTips.put(tip, seenTips.get(tip) + 1);	
					}
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
					
					/* Check if start Time matches 
					 * 
					 * Do it with a 5 minute tolerance, because the dates from Eastbridge are sometimes a little inaccurate
					 */
					if(Math.abs(tipStartUnixTime - eventStartUnixTime) < 5 * 60 * 1000){
						/* Log that we found a matching game date */
						if(newTip && !timeFoundAlreadyPrinted){
							timeFoundAlreadyPrinted = true;
							seenTips.put(tip, 5);	
							mainFrame.addEvent("Matching Game Date found:\n" + eventStartDate.toString());
						}
											
						/* Check if teams match, using different methods */
						boolean teamsMatch = eventHost.equalsIgnoreCase(tipHost) || eventGuest.equalsIgnoreCase(tipGuest);
						
						if(!teamsMatch)
							teamsMatch = TeamMapping.teamsMatch(eventHost, tipHost) || TeamMapping.teamsMatch(eventGuest, tipGuest);
						
						/*/ Teams match, get the right record and make a bet */
						if(teamsMatch){
							/* Log that we found matching teams */
							if(newTip){
								mainFrame.addEvent("Matching Teams found:\n" + 
								"Tip Host: " + tipHost + " Event Host: " + eventHost + "\n" + 
								"Tip Guest: " + tipGuest + " Event Guest: " + eventGuest);
							}
							
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
								else if(TeamMapping.teamsMatch(tip.betOn, tip.host) && !TeamMapping.teamsMatch(tip.betOn, tip.guest)){
									betOn = "one";
								}
								else if(!TeamMapping.teamsMatch(tip.betOn, tip.host) && TeamMapping.teamsMatch(tip.betOn, tip.guest)){
									betOn = "two";
								}
								else if(tip.betOn.equalsIgnoreCase("draw"))
									betOn = "draw";
								
								if(betOn.equals("INVALID")){
									System.out.println("INVALID SELECTION");
									System.exit(-1);
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
																	
									if(record.getPivotType() == PivotType.ONE_TWO && record.getTimeType().name().equals(tipTimeType)){
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										if(betTicketString != null){
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
								}
								if(bestOdd > 0 && bestOdd > tip.noBetUnder && bestOdd > tip.bestOdds * 0.95){
									if(bestMinStake <= MAX_STAKE){
										if(betAmountForTip > 0){
											mainFrame.addEvent("Additional betting attempt for tip, invested so far: " + betAmountForTip);	
										}
										double stakeLeftForTip = MAX_STAKE * StakeCalculation.betAdvisorPercent(tip.take) - betAmountForTip;
										double betAmount = Math.min(stakeLeftForTip, bestBetTicket.getMaxStake());
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, betAmount, true, -1, -1);
										if(betString != null){
											Bet bet = Bet.fromJson(betString);
											if(bet.getBetStatus() == 1){
												System.out.println(bestBetTicket);
												try {
													if(!secondBetForTip)
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
									else{
										/* Log */
										if(newTip){
											mainFrame.addEvent("Min Stake too high: " + bestMinStake);
										}
									}
								}	
								else{
									/* Log */
									if(newTip){
										if(bestOdd == 0){
											mainFrame.addEvent("No matching Record found");
										}
										else if(bestOdd <= tip.noBetUnder){
											mainFrame.addEvent("Insufficient Odds");
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
								
								if(betOn.equals("INVALID")){
									System.out.println("INVALID SELECTION");
									System.exit(-1);
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
									
									if(record.getPivotType() == PivotType.TOTAL && record.getTimeType().name().equals(tipTimeType)){
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										if(betTicketString != null){
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
								}
								if(bestOdd > 0 && bestOdd + 1 > tip.noBetUnder && (bestOdd + 1) > tip.bestOdds * 0.95){
									if(bestMinStake <= MAX_STAKE){
										if(betAmountForTip > 0){
											mainFrame.addEvent("Additional betting attempt for tip, invested so far: " + betAmountForTip);	
										}
										double stakeLeftForTip = MAX_STAKE * StakeCalculation.betAdvisorPercent(tip.take) - betAmountForTip;
										double betAmount = Math.min(stakeLeftForTip, bestBetTicket.getMaxStake());
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, betAmount, true, -1, -1);
										if(betString != null){
											Bet bet = Bet.fromJson(betString);
											if(bet.getBetStatus() == 1){
												System.out.println(bestBetTicket);
												try {
													if(!secondBetForTip)
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
									else{
										/* Log */
										if(newTip){
											mainFrame.addEvent("Min Stake too high: " + bestMinStake);
										}
									}						
								}	
								else{
									/* Log */
									if(newTip){
										if(bestOdd == 0){
											mainFrame.addEvent("No matching Record found");
										}
										else if(bestOdd <= tip.noBetUnder){
											mainFrame.addEvent("Insufficient Odds");
										}	
									}
								}
							}	
							// Over /Under Team tip, get the best matching record and make a bet
							else if(tip.typeOfBet.indexOf("Over / Under") == 0 && tip.typeOfBet.indexOf("Team") != -1){
								boolean tipTeamMatches = false;
								tipTeamMatches = TeamMapping.teamsMatch(eventHost, tip.pivotBias) && TeamMapping.teamsMatch(eventGuest, tip.pivotBias);
								
								if(!tipTeamMatches){
									continue;
								}
								
								String tipTimeType = "";
								if(tip.typeOfBet.equals("Over / Under Team")){
									tipTimeType = "FULL_TIME";
								}
								else if(tip.typeOfBet.equals("Over / Under Team 1st Half")){
									tipTimeType = "HALF_TIME";
								}
								if(tipTimeType.isEmpty()){
									System.out.println("WRONG TIME TYPE");
									System.exit(-1);
								}
								
								// Over under Team has different semantics than normal over under
								String betOn = "INVALID";
								if(tip.betOn.indexOf("Over") != -1){
									betOn = "over";
								}
								else if(tip.betOn.indexOf("Under") == 0){
									betOn = "under";
								}
								
								if(betOn.equals("INVALID")){
									System.out.println("INVALID SELECTION");
									System.exit(-1);
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
									
									if(record.getPivotType() == PivotType.TOTAL && record.getTimeType().name().equals(tipTimeType)){
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										if(betTicketString != null){
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
								}
								if(bestOdd > 0 && bestOdd + 1 > tip.noBetUnder && (bestOdd + 1) > tip.bestOdds * 0.95){
									if(bestMinStake <= MAX_STAKE){
										double stakeLeftForTip = MAX_STAKE * StakeCalculation.betAdvisorPercent(tip.take) - betAmountForTip;
										double betAmount = Math.min(stakeLeftForTip, bestBetTicket.getMaxStake());
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, betAmount, true, -1, -1);
										if(betString != null){
											Bet bet = Bet.fromJson(betString);
											if(bet.getBetStatus() == 1){
												System.out.println(bestBetTicket);
												try {
													if(!secondBetForTip)
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
									else{
										/* Log */
										if(newTip){
											mainFrame.addEvent("Min Stake too high: " + bestMinStake);
										}
									}						
								}	
								else{
									/* Log */
									if(newTip){
										if(bestOdd == 0){
											mainFrame.addEvent("No matching Record found");
										}
										else if(bestOdd <= tip.noBetUnder){
											mainFrame.addEvent("Insufficient Odds");
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
								else if(TeamMapping.teamsMatch(tip.betOn, tip.host) && !TeamMapping.teamsMatch(tip.betOn, tip.guest)){
									if(tip.pivotBias.equals("HOST")){
										betOn = "give";
									}
									if(tip.pivotBias.equals("GUEST")){
										betOn = "take";
									}
								}
								else if(!TeamMapping.teamsMatch(tip.betOn, tip.host) && TeamMapping.teamsMatch(tip.betOn, tip.guest)){
									if(tip.pivotBias.equals("GUEST")){
										betOn = "give";
									}
									if(tip.pivotBias.equals("HOST")){
										betOn = "take";
									}
								}
								
								if(betOn.equals("INVALID")){
									System.out.println("INVALID SELECTION");
									System.exit(-1);
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
										if(betTicketString != null){
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
								}
								if(bestOdd > 0 && bestOdd + 1 > tip.noBetUnder && (bestOdd + 1) > tip.bestOdds * 0.95){
									if(bestMinStake <= MAX_STAKE){
										if(betAmountForTip > 0){
											mainFrame.addEvent("Additional betting attempt for tip, invested so far: " + betAmountForTip);	
										}
										double stakeLeftForTip = MAX_STAKE - betAmountForTip;
										double betAmount = Math.min(stakeLeftForTip, bestBetTicket.getMaxStake());
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, betAmount, true, -1, -1);
										if(betString != null){
											Bet bet = Bet.fromJson(betString);
											if(bet.getBetStatus() == 1){
												System.out.println(bestBetTicket);
												try {
													if(!secondBetForTip)
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
									else{
										/* Log */
										if(newTip){
											mainFrame.addEvent("Min Stake too high: " + bestMinStake);
										}
									}
								}	
								else{
									/* Log */
									if(newTip){
										if(bestOdd == 0){
											mainFrame.addEvent("No matching Record found");
										}
										else if(bestOdd <= tip.noBetUnder){
											mainFrame.addEvent("Insufficient Odds");
										}	
									}
								}
							}
							else{
								/* Log that the Bet Type was not found */
								if(newTip){
									mainFrame.addEvent("Bet Type not recognized:\n" + tip.typeOfBet);		
								}
							}
						}
					}
				}				
			}
			
			// BlogaBet
			// Get parsed mails
			List<ParsedTextMail> mailsBlogaBet = new ArrayList<ParsedTextMail>();
			try{
				mailsBlogaBet = readerBlogaBet.read("vicentbet90@gmail.com", numberOfMessagesToCheck);
			} catch(Exception e){
				e.printStackTrace();
			}
			List<BlogaBetTip> tipsBlogaBet = new ArrayList<BlogaBetTip>();
			for(ParsedTextMail mail : mailsBlogaBet){
				try{
					tipsBlogaBet.add(BlogaBetEmailParser.parseEmail(mail));
				} catch(RuntimeException e){
					e.printStackTrace();
				}
			}
				
			/* Iterate over all tips */
			for(int t = 0; t < tipsBlogaBet.size(); t++){
				BlogaBetTip tip = tipsBlogaBet.get(t);
				
				/* The teams of this tip */
				String tipHost = tip.host;
				String tipGuest = tip.guest;
				
				/* The date when the game is starting */
				Date tipStartDate = tip.startDate;		
				long tipStartUnixTime = tipStartDate.getTime();
				
				/* We can not bet on events from the past */
				if(tipStartUnixTime < System.currentTimeMillis())
					continue;
				
				boolean secondBetForTip = false;
				double betAmountForTip = 0;
				/* Check if we have a new tip  or if we have not bet as much as we wanted yet*/
				if(dataBase.isTipInDatabase(tip)){
					continue;
//					List<ExtendedBetInformations> betInformations = dataBase.getBetsForTip(tip.event, tip.tipster, tip.startDate.getTime());
//					for(int b = 0; b < betInformations.size(); b++){
//						double betAmount = betInformations.get(b).getBetAmount();
//						betAmountForTip += betAmount;
//					}
//					if(betAmountForTip >= MAX_STAKE){
//						continue;
//					}
//					secondBetForTip = true;
				}

				// Some variables for logging
				boolean newTip = false;
				boolean timeFoundAlreadyPrinted = false;

				/* Tip is not in database, we log once that we received it and add it to a List
				 * So it gets ignored in later prints
				 */
				if(!seenTipsBlogaBet.containsKey(tip)){
					mainFrame.addEvent("New Tip received:\n" + tip.toString());
					seenTipsBlogaBet.put(tip, 0);
					newTip = true;
				}
				else{
					int v = seenTipsBlogaBet.get(tip);
					if(v < 5){
						newTip = true;
						seenTipsBlogaBet.put(tip, seenTips.get(tip) + 1);	
					}
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
					
					/* Check if start Time matches 
					 * 
					 * Do it with a 5 minute tolerance, because the dates from Eastbridge are sometimes a little inaccurate
					 */
					if(Math.abs(tipStartUnixTime - eventStartUnixTime) < 5 * 60 * 1000){
						/* Log that we found a matching game date */
						if(newTip && !timeFoundAlreadyPrinted){
							timeFoundAlreadyPrinted = true;
							seenTipsBlogaBet.put(tip, 5);	
							mainFrame.addEvent("Matching Game Date found:\n" + eventStartDate.toString());
						}
											
						/* Check if teams match, using different methods */
						boolean teamsMatch = eventHost.equalsIgnoreCase(tipHost) || eventGuest.equalsIgnoreCase(tipGuest);
						
						if(!teamsMatch)
							teamsMatch = TeamMapping.teamsMatch(eventHost, tipHost) || TeamMapping.teamsMatch(eventGuest, tipGuest);
						
						/*/ Teams match, get the right record and make a bet */
						if(teamsMatch){
							/* Log that we found matching teams */
							if(newTip){
								mainFrame.addEvent("Matching Teams found:\n" + 
								"Tip Host: " + tipHost + " Event Host: " + eventHost + "\n" + 
								"Tip Guest: " + tipGuest + " Event Guest: " + eventGuest);
							}
							
							// Match Odds tip, get the best matching record and make a bet
							if(tip.pivotType.equalsIgnoreCase("Match Odds") || tip.pivotType.equalsIgnoreCase("Match Odds 1st Half")){
								String tipTimeType = "";
								if(tip.pivotType.equalsIgnoreCase("Match odds")){
									tipTimeType = "FULL_TIME";
								}
								else if(tip.pivotType.equalsIgnoreCase("Match Odds 1st Half")){
									tipTimeType = "HALF_TIME";
								}
								if(tipTimeType.isEmpty()){
									System.out.println("WRONG TIME TYPE");
									System.exit(-1);
								}
								
								String betOn = "INVALID";
								if(tip.selection.equalsIgnoreCase("HOME"))
									betOn = "one";
								else if(tip.selection.equalsIgnoreCase("AWAY"))
									betOn = "two";
								else if(tip.selection.equalsIgnoreCase("DRAW"))
									betOn = "draw";
								
								if(betOn.equals("INVALID")){
									System.out.println("INVALID SELECTION");
									System.exit(-1);
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
																	
									if(record.getPivotType() == PivotType.ONE_TWO && record.getTimeType().name().equals(tipTimeType)){
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										if(betTicketString != null){
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
								}
								if(bestOdd > 0 && bestOdd > tip.odds * 0.95){
									if(bestMinStake <= MAX_STAKE * 1.5){
										if(betAmountForTip > 0){
											mainFrame.addEvent("Additional betting attempt for tip, invested so far: " + betAmountForTip);	
										}
										double stakeLeftForTip = MAX_STAKE * StakeCalculation.blogaBetPercent(tip.stake) - betAmountForTip;
										double betAmount = Math.min(stakeLeftForTip, bestBetTicket.getMaxStake());
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, betAmount, true, -1, -1);
										if(betString != null){
											Bet bet = Bet.fromJson(betString);
											if(bet.getBetStatus() == 1){
												System.out.println(bestBetTicket);
												try {
													if(!secondBetForTip)
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
													dataBase.addBetBlogaBet(bet);
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
									else{
										/* Log */
										if(newTip){
											mainFrame.addEvent("Min Stake too high: " + bestMinStake);
										}
									}
								}	
								else{
									/* Log */
									if(newTip){
										if(bestOdd == 0){
											mainFrame.addEvent("No matching Record found");
										}
										else if(bestOdd <= tip.odds * 0.95){
											mainFrame.addEvent("Insufficient Odds");
										}	
									}
								}
							}
							
							// Over /Under  tip, get the best matching record and make a bet
							else if(tip.pivotType.equalsIgnoreCase("Over / Under") || tip.pivotType.equalsIgnoreCase("Over / Under 1st Half")){
								String tipTimeType = "";
								if(tip.pivotType.equalsIgnoreCase("Over / Under")){
									tipTimeType = "FULL_TIME";
								}
								else if(tip.pivotType.equalsIgnoreCase("Over / Under 1st Half")){
									tipTimeType = "HALF_TIME";
								}
								if(tipTimeType.isEmpty()){
									System.out.println("WRONG TIME TYPE");
									System.exit(-1);
								}
								
								String betOn = "INVALID";
								if(tip.selection.indexOf("OVER") == 0)
									betOn = "over";
								else if(tip.selection.indexOf("UNDER") == 0)
									betOn = "under";
								
								if(betOn.equals("INVALID")){
									System.out.println("INVALID SELECTION");
									System.exit(-1);
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
									
									if(record.getPivotType() == PivotType.TOTAL && record.getTimeType().name().equals(tipTimeType)){
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										if(betTicketString != null){
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
								}
								if(bestOdd > 0 && (bestOdd + 1) > tip.odds * 0.95){
									if(bestMinStake <= MAX_STAKE * 1.5){
										if(betAmountForTip > 0){
											mainFrame.addEvent("Additional betting attempt for tip, invested so far: " + betAmountForTip);	
										}
										double stakeLeftForTip = MAX_STAKE * StakeCalculation.blogaBetPercent(tip.stake) - betAmountForTip;
										double betAmount = Math.min(stakeLeftForTip, bestBetTicket.getMaxStake());
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, betAmount, true, -1, -1);
										if(betString != null){
											Bet bet = Bet.fromJson(betString);
											if(bet.getBetStatus() == 1){
												System.out.println(bestBetTicket);
												try {
													if(!secondBetForTip)
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
													dataBase.addBetBlogaBet(bet);
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
									else{
										/* Log */
										if(newTip){
											mainFrame.addEvent("Min Stake too high: " + bestMinStake);
										}
									}						
								}	
								else{
									/* Log */
									if(newTip){
										if(bestOdd == 0){
											mainFrame.addEvent("No matching Record found");
										}
										else if(bestOdd + 1 <= tip.odds * 0.95){
											mainFrame.addEvent("Insufficient Odds");
										}	
									}
								}
							}	
							else if((tip.pivotType.equalsIgnoreCase("Asian handicap") || tip.pivotType.equalsIgnoreCase("Asian handicap 1st Half")) && tip.pivotValue != 0){
								String tipTimeType = "";
								if(tip.pivotType.equalsIgnoreCase("Asian handicap")){
									tipTimeType = "FULL_TIME";
								}
								else if(tip.pivotType.equalsIgnoreCase("Asian handicap 1st Half")){
									tipTimeType = "HALF_TIME";
								}
								if(tipTimeType.isEmpty()){
									System.out.println("WRONG TIME TYPE");
									System.exit(-1);
								}
								
								String betOn = "INVALID";
								if(tip.selection.equals("HOME")){
									if(tip.pivotBias.equals("HOME")){
										betOn = "give";
									}
									if(tip.pivotBias.equals("AWAY")){
										betOn = "take";
									}
								}
								else if(tip.selection.equals("AWAY")){
									if(tip.pivotBias.equals("AWAY")){
										betOn = "give";
									}
									if(tip.pivotBias.equals("HOME")){
										betOn = "take";
									}
								}
								
								if(betOn.equals("INVALID")){
									System.out.println("INVALID SELECTION");
									System.exit(-1);
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
										if(tipPivotBias.equals("HOME")){
											tipPivotBias = "HOST";
										}
										else if(tipPivotBias.equals("AWAY")){
											tipPivotBias = "GUEST";
										}
										String recordPivotBias = record.getPivotBias().name();
										if(!tipPivotBias.equalsIgnoreCase(recordPivotBias))
											continue;
										
										// Get bet ticket
										String company = record.getSource().toLowerCase();
										String market = record.getOddType().toString().toLowerCase();
										String eventId = record.getEventId();
										int oddId = record.getOddId();
										String betTicketString = BettingApi.getBetTicket(company, betOn, market, eventId, oddId, -1, -1);
										if(betTicketString != null){
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
								}
								if(bestOdd > 0 && (bestOdd + 1) > tip.odds * 0.95){
									if(bestMinStake <= MAX_STAKE * 1.5){
										if(betAmountForTip > 0){
											mainFrame.addEvent("Additional betting attempt for tip, invested so far: " + betAmountForTip);	
										}
										double stakeLeftForTip = MAX_STAKE * StakeCalculation.blogaBetPercent(tip.stake) - betAmountForTip;
										double betAmount = Math.min(stakeLeftForTip, bestBetTicket.getMaxStake());
										String betString = BettingApi.placeBet(bestCompany, betOn, bestMarket, bestEventId, bestOddId, bestOdd, betAmount, true, -1, -1);
										if(betString != null){
											Bet bet = Bet.fromJson(betString);
											if(bet.getBetStatus() == 1){
												System.out.println(bestBetTicket);
												try {
													if(!secondBetForTip)
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
													dataBase.addBetBlogaBet(bet);
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
									else{
										/* Log */
										if(newTip){
											mainFrame.addEvent("Min Stake too high: " + bestMinStake);
										}
									}
								}	
								else{
									/* Log */
									if(newTip){
										if(bestOdd == 0){
											mainFrame.addEvent("No matching Record found");
										}
										else if(bestOdd + 1 <= tip.odds * 0.95){
											mainFrame.addEvent("Insufficient Odds");
										}	
									}
								}
							}
							else{
								/* Log that the Bet Type was not found */
								if(newTip){
									mainFrame.addEvent("Bet Type not recognized:\n" + tip.pivotType);		
								}
							}
						}
					}
				}				
			}
			
			// Get open Bets
			String openBets = "Running bets:\n";
			double currentlyInvested = 0;
			
			List<Bet> bets = dataBase.getAllBets();
			for(int b = 0; b < bets.size(); b++){
				Bet bet = bets.get(b);
				// Update bet status
				if(bet.getBetStatus() == 1){
					String betString = BettingApi.getBetStatus(bet.getId());
					if(betString != null){
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
			}
			
			List<Bet> betsBlogaBet = dataBase.getAllBetsBlogaBet();
			for(int b = 0; b < betsBlogaBet.size(); b++){
				Bet bet = betsBlogaBet.get(b);
				// Update bet status
				if(bet.getBetStatus() == 1){
					String betString = BettingApi.getBetStatus(bet.getId());
					if(betString != null){
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
