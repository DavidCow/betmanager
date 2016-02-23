package backtest;

import historicalData.HdpElement;
import historicalData.HistoricalDataElement;
import historicalData.HistoricalDataParser;
import historicalData.OneTwoElement;
import historicalData.TotalElement;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import betadvisor.BetAdvisorComparator;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;

public class BetAdvisorBacktest {

	public BetAdvisorBacktest(){
		
	}
	
	public void runBacktest() throws IOException{

		/* Variables for the results */
		double checkedTipps = 0;
		
		// If we always take the best available odds, even if they are lower as what the tippster suggested
		double evAllPossibleBetsTaken = 0;
		double numberOfAllBets = 0;
		double evAllPossibleBetsTakenMatchOdds = 0;
		double numberOfAllBetsMatchOdds = 0;
		double evAllPossibleBetsTakenOverUnder = 0;
		double numberOfAllBetsOverUnder = 0;
		double evAllPossibleBetsTakenHdp = 0;
		double numberOfAllBetsHdp = 0;
		
		// EVs with lay hedging
		double evLayed = 0;
		
		// If we only take bets with odds higher or equal to what the tippster suggested
		double evOnlyGoodOddsTaken = 0;
		double numberOfGoodBets = 0;
		
		// If we only take bets with odds above a certain threshold of what the tipster suggested
		double threshold = 0.95;
		double evThresholdOddsTaken = 0;
		double numberOfThresholdBets = 0;
		
		// Getting "lay" odds for hedging
		double averageLayMovement = 0;
		double numberOfLays = 0;
		
		double takenLayMovement = 0;
		double numberOfTakenLays = 0;
		
		double takenLayMovementThreshold = 0;
		double numberOfTakenLaysThreshold = 0;
		double layThreshold = 1.1;
		
		// Variance calculation
		List<Double> betEvs = new ArrayList<Double>();
		List<Double> hedgedEvs = new ArrayList<Double>();
		
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
		// We set the start and endIndex of considered tipps, according to the historical data that we have
		int startI = 0;
		int endI = 0;
		for(int i = 0; i < betAdvisorList.size(); i++){
			Date date = betAdvisorList.get(i).getGameDate();
			int y = date.getYear() + 1900;
			if(y == 2014){
				startI = i;
				break;
			}
		}
		for(int i = 0; i < betAdvisorList.size(); i++){
			Date date = betAdvisorList.get(i).getGameDate();
			int y = date.getYear() + 1900;
			if(y == 2015){
				if(date.getMonth() == 9){
					endI = i;
					break;
				}
			}
		}
		
		HistoricalDataParser historicalDataParser = new HistoricalDataParser();
		List<HistoricalDataElement> historicalDataList = new ArrayList<HistoricalDataElement>();//historicalDataParser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\pending", "Full");
		historicalDataList.addAll(historicalDataParser.parseFileJayeson("C:\\Users\\Patryk\\Desktop\\pending_2015\\pendingFull_20150201_20150301.xml"));
		
		/* Needed for comparison of Dates */
		DateFormat gmtFormat = new SimpleDateFormat();
		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTime);
		
		// We dont have to loop over all historical data for every tipp, sonce some historical data will be
		// From games before the tipp
		int startJ = 0;
		boolean startJSet = false;
				
		/* Itterate over Tipps */
		for(int i = startI; i < endI; i++){
			startJSet = false;
			
			int matches = 0;
			int hostMatches = 0;
			String betAdvisorHost = "";
			String betAdvisorGuest = "";
			
			String historicalDataHost = "";
			String historicalDataGuest = "";
			
			/* The Date of the tipp */
			BetAdvisorElement tipp = betAdvisorList.get(i);
			if(!tipp.getTypeOfBet().equals("Match Odds") && !tipp.getTypeOfBet().equals("Over / Under")&& !tipp.getTypeOfBet().equals("Asian handicap")){
				continue;
			}
			checkedTipps++;
			double suggestedOdds = tipp.getOdds();
			
			Date betAdvisorGameDate = tipp.getGameDate();
			String s0 = betAdvisorGameDate.toGMTString();
			
			/* The index of the tipp */
			String tippTeam = tipp.getSelection();
			int tippIndex = -1;
			
			List<HistoricalDataElement> availableBets = new ArrayList<HistoricalDataElement>();
			
			/* Itterate over games and find the markets that match the tipp */
			for(int j = startJ; j < historicalDataList.size(); j++){
				
				HistoricalDataElement historicalDataElement= historicalDataList.get(j);
				Date historicalDataGameDate = historicalDataElement.getStartDate();
				String s1 = historicalDataGameDate.toGMTString();
				
				// We can break the inner loop if the start Time of the match of the historical
				// data element is later than that of the startTime of the tipped game
				// because both lists are sorted
				if(historicalDataGameDate.after(betAdvisorGameDate)){
					break;
				}			
				
				long t0 = betAdvisorGameDate.getTime();
				long t1 = historicalDataGameDate.getTime();
				
				if(t0 == t1){
					// Set the new startJ
					// it will be the first index j with a date equal to the start of the game of the tipp
					// because both lists are sorted, the relevant index j for the next tipp can not be lower than for the
					// current tipp
					if(!startJSet){
						startJSet = true;
						startJ = j - 10;
					}
					
					String betAdvisorLeague = tipp.getLeague();
					String historicalDataLeague = historicalDataElement.getLeague();
					//System.out.println(betAdvisorLeague + " , " + historicalDataLeague);
					
					betAdvisorHost = BetAdvisorParser.parseHostFromEvent(tipp.getEvent());
					betAdvisorGuest = BetAdvisorParser.parseGuestFromEvent(tipp.getEvent());
					
					historicalDataHost = historicalDataElement.getHost();
					historicalDataGuest = historicalDataElement.getGuest();
					matches++;
					
					//System.out.println(betAdvisorHost + " , " + historicalDataHost);	
					if(betAdvisorHost.equalsIgnoreCase(historicalDataHost)){
						availableBets.add(historicalDataElement);
						if(tipp.getTypeOfBet().equals("Match Odds")){
							if(tippTeam.equalsIgnoreCase("Draw")){
								tippIndex = 2;
							}
							else if(tippTeam.equalsIgnoreCase(betAdvisorHost)){
								tippIndex = 0;
							}
							else if(tippTeam.equalsIgnoreCase(betAdvisorGuest)){
								tippIndex = 1;
							}		
						}
						if(tipp.getTypeOfBet().equals("Over / Under")){
							int totalStart = tipp.getSelection().lastIndexOf("+") + 1;
							String totalString = tipp.getSelection().substring(totalStart);
							double total = Double.parseDouble(totalString);
							List<TotalElement> l = historicalDataElement.getTotalList();
							if(!l.isEmpty()){
								boolean totalOk = false;
								for(int t = 0; t < l.size(); t++){
									if(l.get(t).getTotal() == total){
										totalOk = true;
										break;
									}
								}
								if(totalOk){
									availableBets.add(historicalDataElement);
									if(tippTeam.indexOf("Over") == 0){
										tippIndex = 0;
									}
									else if(tippTeam.indexOf("Under") == 0){
										tippIndex = 1;
									}
								}
							}
						}
						if(tipp.getTypeOfBet().equals("Asian handicap")){
							int pivotStart = tipp.getSelection().lastIndexOf("-") + 1;
							if(pivotStart != 0){
								try{
									String pivotString = tipp.getSelection().substring(pivotStart);
									double pivot = Double.parseDouble(pivotString);
									List<HdpElement> l = historicalDataElement.getHdpList();
									if(!l.isEmpty()){
										boolean pivotOk = false;
										for(int t = 0; t < l.size(); t++){
											if(l.get(t).getPivot() == pivot){
												pivotOk = true;
												break;
											}
										}
										if(pivotOk){
											if(tippTeam.indexOf(betAdvisorHost) != -1){
												tippIndex = 0;
											}
											else if(tippTeam.indexOf(betAdvisorGuest) != -1){
												tippIndex = 1;
											}
										}
									}			
								}catch(Exception e){
									
								}
							}
						}
					}
					else if(betAdvisorGuest.equalsIgnoreCase(historicalDataGuest)){
						availableBets.add(historicalDataElement);
						if(tipp.getTypeOfBet().equals("Match Odds")){
							if(tippTeam.equalsIgnoreCase("Draw")){
								tippIndex = 2;
							}
							else if(tippTeam.equalsIgnoreCase(betAdvisorHost)){
								tippIndex = 0;
							}
							else if(tippTeam.equalsIgnoreCase(betAdvisorGuest)){
								tippIndex = 1;
							}
						}
						if(tipp.getTypeOfBet().equals("Over / Under")){
							int totalStart = tipp.getSelection().lastIndexOf("+") + 1;
							String totalString = tipp.getSelection().substring(totalStart);
							double total = Double.parseDouble(totalString);
							List<TotalElement> l = historicalDataElement.getTotalList();
							if(!l.isEmpty()){
								boolean totalOk = false;
								for(int t = 0; t < l.size(); t++){
									if(l.get(t).getTotal() == total){
										totalOk = true;
										break;
									}
								}
								if(totalOk){
									availableBets.add(historicalDataElement);
									if(tippTeam.indexOf("Over") == 0){
										tippIndex = 0;
									}
									else if(tippTeam.indexOf("Under") == 0){
										tippIndex = 1;
									}
								}
							}
						}	
						if(tipp.getTypeOfBet().equals("Asian handicap")){
							int pivotStart = tipp.getSelection().lastIndexOf("-") + 1;
							if(pivotStart != 0){
								try{
									String pivotString = tipp.getSelection().substring(pivotStart);
									double pivot = Double.parseDouble(pivotString);
									List<HdpElement> l = historicalDataElement.getHdpList();
									if(!l.isEmpty()){
										boolean pivotOk = false;
										for(int t = 0; t < l.size(); t++){
											if(l.get(t).getPivot() == pivot){
												pivotOk = true;
												break;
											}
										}
										if(pivotOk){
											if(tippTeam.indexOf(betAdvisorHost) != -1){
												tippIndex = 0;
											}
											else if(tippTeam.indexOf(betAdvisorGuest) != -1){
												tippIndex = 1;
											}
										}
									}			
								}catch(Exception e){
									
								}
							}
						}
					}
					else if(betAdvisorLeague.equals("International Friendly Games") && tipp.getTypeOfBet().equals("Match Odds")){
						if(betAdvisorHost.equalsIgnoreCase(historicalDataGuest)){
							availableBets.add(historicalDataElement);
							if(tippTeam.equalsIgnoreCase("Draw")){
								tippIndex = 2;
							}
							else if(tippTeam.equalsIgnoreCase(betAdvisorHost)){
								tippIndex = 1;
							}
							else if(tippTeam.equalsIgnoreCase(betAdvisorGuest)){
								tippIndex = 0;
							}
						}
					}
				}
			}
			
			/* Make the bet with the best Odds available at the time */
			Date tippPublishedDate = tipp.getPublicationDate();
			HistoricalDataElement bestSource = null;
			double bestOdds = 0;
			
			for(int j = 0; j < availableBets.size(); j++){
				HistoricalDataElement historicalElement = availableBets.get(j);
				
				// ONE_TWO
				if(tipp.getTypeOfBet().equals("Match Odds")){
					List<OneTwoElement> oneTwoOdds = historicalElement.getOneTwoList();

					for(int oddIndex = 0; oddIndex < oneTwoOdds.size(); oddIndex++){
						OneTwoElement oneTwoElement = oneTwoOdds.get(oddIndex);
						Date oddsDate = new Date(oneTwoElement.getTime());
						double odds = 0;
						if(oddsDate.before(tippPublishedDate)){
							if(tippIndex == 0){
								odds = oneTwoElement.getOne();
							}
							else if(tippIndex == 1){
								odds = oneTwoElement.getTwo();
							}
							else if(tippIndex == 2){
								odds = oneTwoElement.getDraw();
							}
						}
						if(odds > bestOdds){
							bestOdds = odds;
							bestSource = historicalElement;
						}	
					}		
				}
				
				// OVER / UNDER
				if(tipp.getTypeOfBet().equals("Over / Under")){
					List<TotalElement> totalOdds = historicalElement.getTotalList();

					for(int oddIndex = 0; oddIndex < totalOdds.size(); oddIndex++){
						TotalElement totalElement = totalOdds.get(oddIndex);
						
						int totalStart = tipp.getSelection().lastIndexOf("+") + 1;
						String totalString = tipp.getSelection().substring(totalStart);
						double total = Double.parseDouble(totalString);
						
						if(totalElement.getTotal() == total){
							Date oddsDate = new Date(totalElement.getTime());
							double odds = 0;
							if(oddsDate.before(tippPublishedDate)){
								if(tippIndex == 0){
									odds = 1 + totalElement.getOver();
								}
								else if(tippIndex == 1){
									odds = 1 + totalElement.getUnder();
								}
							}
							if(odds > bestOdds){
								bestOdds = odds;
								bestSource = historicalElement;
							}	
						}
					}					
				}
				
				// Asian handicap
				if(tipp.getTypeOfBet().equals("Asian handicap")){
					List<HdpElement> hdpOdds = historicalElement.getHdpList();

					for(int oddIndex = 0; oddIndex < hdpOdds.size(); oddIndex++){
						HdpElement totalElement = hdpOdds.get(oddIndex);
						
						int pivotStart = tipp.getSelection().lastIndexOf("-") + 1;
						if(pivotStart != 0){
							try{
								String pivotString = tipp.getSelection().substring(pivotStart);
								double pivot = Double.parseDouble(pivotString);
								
								if(totalElement.getPivot() == pivot){
									Date oddsDate = new Date(totalElement.getTime());
									double odds = 0;
									if(oddsDate.before(tippPublishedDate)){
										if(tippIndex == 0){
											odds = 1 + totalElement.getHost();
										}
										else if(tippIndex == 1){
											odds = 1 + totalElement.getGuest();
										}
									}
									if(odds > bestOdds){
										bestOdds = odds;
										bestSource = historicalElement;
									}
								}		
							}catch(Exception e){
								
							}
						}
					}					
				}
			}
			if(bestOdds != 0){
				if(tipp.getTypeOfBet().equals("Match Odds")){
					// Check "lay" movement
					double firstLay = 0;
					double lastLay = 0;
					List<OneTwoElement> oneTwoOdds = bestSource.getOneTwoList();
					for(int oddIndex = 0; oddIndex < oneTwoOdds.size(); oddIndex++){
						OneTwoElement oneTwoElement = oneTwoOdds.get(oddIndex);
						Date oddsDate = new Date(oneTwoElement.getTime());

						if(oddsDate.before(tippPublishedDate)){
							if(tippIndex == 0){
								firstLay = 0.5 * (oneTwoElement.getDraw() + oneTwoElement.getTwo());
								lastLay = firstLay;
							}
							else if(tippIndex == 1){
								firstLay = 0.5 * (oneTwoElement.getDraw() + oneTwoElement.getOne());
								lastLay = firstLay;
							}
							else if(tippIndex == 2){
								firstLay = 0.5 * (oneTwoElement.getOne() + oneTwoElement.getTwo());
								lastLay = firstLay;
							}
						}
						else{
							if(oddIndex != 0 && oddIndex == oneTwoOdds.size() -1){
								if(tippIndex == 0){
									firstLay = 0.5 * (oneTwoElement.getDraw() + oneTwoElement.getTwo());
								}
								else if(tippIndex == 1){
									firstLay = 0.5 * (oneTwoElement.getDraw() + oneTwoElement.getOne());
								}
								else if(tippIndex == 2){
									firstLay = 0.5 * (oneTwoElement.getOne() + oneTwoElement.getTwo());
								}						
							}
						}
					}
					double layMovement = lastLay / firstLay;
					averageLayMovement += layMovement;
					numberOfLays++;
					
					if(layMovement > 1){
						numberOfTakenLays++;
						takenLayMovement += layMovement;
					}
					
					if(layMovement > layThreshold){
						numberOfTakenLaysThreshold++;
						takenLayMovementThreshold += layMovement;
					}
					double take = 100;
					numberOfAllBets++;
					numberOfAllBetsMatchOdds++;
					if(tipp.getProfit() < 0){
						evAllPossibleBetsTaken -= take;
						evAllPossibleBetsTakenMatchOdds -= take;
						betEvs.add(-take);
						if(layMovement > layThreshold){
							double lEv = -take;
							lEv += take * lastLay - take;
							hedgedEvs.add(lEv);
							evLayed += take * lastLay - take;
							evLayed -= take;
						}
					}
					else{
						evAllPossibleBetsTaken += take * bestOdds - take;
						evAllPossibleBetsTakenMatchOdds  += take * bestOdds - take;
						betEvs.add(take * bestOdds - take);
						if(layMovement > layThreshold){
							double lEv = -take;
							lEv += take * bestOdds - take;
							hedgedEvs.add(lEv);
							evLayed -= take;
							evLayed += take * bestOdds - take;
						}
					}
					
					if(bestOdds >= suggestedOdds){
						numberOfGoodBets++;
						if(tipp.getProfit() < 0){
							evOnlyGoodOddsTaken -= take;
						}
						else{
							evOnlyGoodOddsTaken += take * bestOdds - take;
						}
					}
					if(bestOdds >= suggestedOdds * threshold){
						numberOfThresholdBets++;
						if(tipp.getProfit() < 0){
							evThresholdOddsTaken -= take;
						}
						else{
							evThresholdOddsTaken += take * bestOdds - take;
						}					
					}
					if(bestOdds < suggestedOdds){
						//System.out.println("Suggested Odds: " +  suggestedOdds + " real Odds: " + bestOdds);
					}		
				}
				if(tipp.getTypeOfBet().equals("Over / Under")){	
					// Check "lay" movement
					double firstLay = 0;
					double lastLay = 0;
					List<TotalElement> totalOdds = bestSource.getTotalList();
					for(int oddIndex = 0; oddIndex < totalOdds.size(); oddIndex++){
						TotalElement totalElement = totalOdds.get(oddIndex);
						Date oddsDate = new Date(totalElement.getTime());

						if(oddsDate.before(tippPublishedDate)){
							if(tippIndex == 0){
								firstLay = 1 + totalElement.getUnder();
								lastLay = firstLay;
							}
							else if(tippIndex == 1){
								firstLay = 1 + totalElement.getOver();
								lastLay = firstLay;
							}
						}
						else{
							if(oddIndex != 0 && oddIndex == totalOdds.size() -1){
								if(tippIndex == 0){
									lastLay = 1 + totalElement.getUnder();
								}
								else if(tippIndex == 1){
									lastLay = 1 + totalElement.getOver();
								}							
							}
						}
					}
					double layMovement = lastLay / firstLay;
					averageLayMovement += layMovement;
					numberOfLays++;
					
					if(layMovement > 1){
						numberOfTakenLays++;
						takenLayMovement += layMovement;
					}
					
					if(layMovement > layThreshold){
						numberOfTakenLaysThreshold++;
						takenLayMovementThreshold += layMovement;
					}
					
					double take = 100;
					numberOfAllBets++;
					numberOfAllBetsOverUnder++;
					if(tipp.getProfit() < 0){
						evAllPossibleBetsTaken -= take;
						evAllPossibleBetsTakenOverUnder -= take;
						betEvs.add(-take);
						if(layMovement > layThreshold){
							double lEv = -take;
							lEv += take * lastLay - take;
							hedgedEvs.add(lEv);
							evLayed += take * lastLay - take;
							evLayed -= take;
						}
					}
					else{
						evAllPossibleBetsTaken += take * bestOdds - take;
						evAllPossibleBetsTakenOverUnder  += take * bestOdds - take;
						betEvs.add(take * bestOdds - take);
						if(layMovement > layThreshold){
							double lEv = -take;
							lEv += take * bestOdds - take;
							hedgedEvs.add(lEv);
							evLayed -= take;
							evLayed += take * bestOdds - take;
						}
					}
					
					if(bestOdds >= suggestedOdds){
						numberOfGoodBets++;
						if(tipp.getProfit() < 0){
							evOnlyGoodOddsTaken -= take;
						}
						else{
							evOnlyGoodOddsTaken += take * bestOdds - take;
						}
					}
					if(bestOdds >= suggestedOdds * threshold){
						numberOfThresholdBets++;
						if(tipp.getProfit() < 0){
							evThresholdOddsTaken -= take;
						}
						else{
							evThresholdOddsTaken += take * bestOdds - take;
						}					
					}
					if(bestOdds < suggestedOdds){
						//System.out.println("Suggested Odds: " +  suggestedOdds + " real Odds: " + bestOdds);
					}						
				}
				if(tipp.getTypeOfBet().equals("Asian handicap")){
					// Check "lay" movement
					double firstLay = 0;
					double lastLay = 0;
					List<HdpElement> hdpOdds = bestSource.getHdpList();
					for(int oddIndex = 0; oddIndex < hdpOdds.size(); oddIndex++){
						HdpElement hdpElement = hdpOdds.get(oddIndex);
						Date oddsDate = new Date(hdpElement.getTime());

						if(oddsDate.before(tippPublishedDate)){
							if(tippIndex == 0){
								firstLay = 1 + hdpElement.getGuest();
								lastLay = firstLay;
							}
							else if(tippIndex == 1){
								firstLay = 1 + hdpElement.getHost();
								lastLay = firstLay;
							}
						}
						else{
							if(oddIndex != 0 && oddIndex == hdpOdds.size() -1){
								if(tippIndex == 0){
									lastLay = 1 + hdpElement.getGuest();
								}
								else if(tippIndex == 1){
									lastLay = 1 + hdpElement.getHost();
								}							
							}
						}
					}
					double layMovement = lastLay / firstLay;
					averageLayMovement += layMovement;
					numberOfLays++;
					
					if(layMovement > 1){
						numberOfTakenLays++;
						takenLayMovement += layMovement;
					}
					
					if(layMovement > layThreshold){
						numberOfTakenLaysThreshold++;
						takenLayMovementThreshold += layMovement;
					}
					
					double take = 100;
					numberOfAllBets++;
					numberOfAllBetsHdp++;
					if(tipp.getProfit() < 0){
						evAllPossibleBetsTaken -= take;
						evAllPossibleBetsTakenHdp -= take;
						betEvs.add(-take);
						if(layMovement > layThreshold){
							double lEv = -take;
							lEv += take * lastLay - take;
							hedgedEvs.add(lEv);
							evLayed += take * lastLay - take;
							evLayed -= take;
						}
					}
					else{
						evAllPossibleBetsTaken += take * bestOdds - take;
						evAllPossibleBetsTakenHdp  += take * bestOdds - take;
						betEvs.add(take * bestOdds - take);
						if(layMovement > layThreshold){
							double lEv = -take;
							lEv += take * bestOdds - take;
							hedgedEvs.add(lEv);
							evLayed += take * bestOdds - take;
							evLayed -= take;
						}
					}
					
					if(bestOdds >= suggestedOdds){
						numberOfGoodBets++;
						if(tipp.getProfit() < 0){
							evOnlyGoodOddsTaken -= take;
						}
						else{
							evOnlyGoodOddsTaken += take * bestOdds - take;
						}
					}
					if(bestOdds >= suggestedOdds * threshold){
						numberOfThresholdBets++;
						if(tipp.getProfit() < 0){
							evThresholdOddsTaken -= take;
						}
						else{
							evThresholdOddsTaken += take * bestOdds - take;
						}					
					}
					if(bestOdds < suggestedOdds){
						//System.out.println("Suggested Odds: " +  suggestedOdds + " real Odds: " + bestOdds);
					}						
				}
			}
		}
		averageLayMovement /= numberOfLays;
		takenLayMovement /= numberOfTakenLays;
		takenLayMovementThreshold /= numberOfTakenLaysThreshold;
		
		// Evs
		System.out.println();
		System.out.println("Results:");
		System.out.println("All Bets taken: " + numberOfAllBets + " EV if all bets taken: " + evAllPossibleBetsTaken + " EV per Bet: " + evAllPossibleBetsTaken / numberOfAllBets);
		System.out.println("Only good Bets taken: " + numberOfGoodBets + " EV if good bets taken: " + evOnlyGoodOddsTaken + " EV per Bet: " + evOnlyGoodOddsTaken / numberOfGoodBets);
		System.out.println("Only threshold Bets taken: " + numberOfThresholdBets + " EV if threshold bets taken: " + evThresholdOddsTaken + " EV per Bet: " + evThresholdOddsTaken / numberOfThresholdBets);
		System.out.println("Percentage of good odds: " + numberOfGoodBets / numberOfAllBets); 
		System.out.println("Percentage of threshold odds: " + numberOfThresholdBets / numberOfAllBets); 
		System.out.println("Match Odds:");
		System.out.println("All Bets taken Match Odds: " + numberOfAllBetsMatchOdds + " EV if all bets taken: " + evAllPossibleBetsTakenMatchOdds + " EV per Bet: " + evAllPossibleBetsTakenMatchOdds / numberOfAllBetsMatchOdds);
		System.out.println("Over / Under");
		System.out.println("All Bets taken Over / Under: " + numberOfAllBetsOverUnder + " EV if all bets taken: " + evAllPossibleBetsTakenOverUnder + " EV per Bet: " + evAllPossibleBetsTakenOverUnder / numberOfAllBetsOverUnder);
		System.out.println("Asian handicap");
		System.out.println("All Bets taken Asian handicap: " + numberOfAllBetsHdp + " EV if all bets taken: " + evAllPossibleBetsTakenHdp + " EV per Bet: " + evAllPossibleBetsTakenHdp / numberOfAllBetsHdp);
		
		//Lay
		System.out.println("Tipps taken: " + numberOfAllBets + " out of " + betAdvisorList.size() + " - Percent of tipps taken: " + numberOfAllBets * 100.0 / checkedTipps + "%");
		System.out.println("Average lay movement: " + averageLayMovement + " number of Lays: " + numberOfLays);
		System.out.println("Average taken lay movement: " + takenLayMovement + " number of Lays: " + numberOfTakenLays);
		System.out.println("Average taken lay movement with threshold: " + takenLayMovementThreshold + " number of Lays: " + numberOfTakenLaysThreshold);
		System.out.println("Lay bets taken with threshold: " + numberOfTakenLaysThreshold + " EV with lay: " + evLayed  + " EV per Bet: " + evLayed / numberOfTakenLaysThreshold);
		
		//Variance
		double betVariance = 0;
		double evPerBet = evAllPossibleBetsTaken / numberOfAllBets;
		for(int i = 0; i < betEvs.size(); i++){
			betVariance +=Math.pow((betEvs.get(i) - evPerBet), 2);
		}
		betVariance /= betEvs.size();
		
		double betVarianceHedged = 0;
		double evPerBetHedged = evLayed / numberOfTakenLaysThreshold;
		for(int i = 0; i < hedgedEvs.size(); i++){
			betVarianceHedged +=Math.pow((hedgedEvs.get(i) - evPerBetHedged), 2);
		}
		betVarianceHedged /= hedgedEvs.size();
		
		System.out.println("Normal variance: " + betVariance);
		System.out.println("Hedged variance: " + betVarianceHedged);
	}
	public static void main(String[] args) throws IOException {
		BetAdvisorBacktest backTest = new BetAdvisorBacktest();
		backTest.runBacktest();
	}
}
