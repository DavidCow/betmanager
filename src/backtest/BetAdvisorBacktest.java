package backtest;

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
		
		// If we always take the best available odds, even if they are lower as what the tippster suggested
		double evAllPossibleBetsTaken = 0;
		double numberOfAllBets = 0;
		double evAllPossibleBetsTakenMatchOdds = 0;
		double numberOfAllBetsMatchOdds = 0;
		double evAllPossibleBetsTakenOverUnder = 0;
		double numberOfAllBetsOverUnder = 0;
		
		// If we only take bets with odds higher or equal to what the tippster suggested
		double evOnlyGoodOddsTaken = 0;
		double numberOfGoodBets = 0;
		
		// If we only take bets with odds above a certain threshold of what the tipster suggested
		double threshold = 0.95;
		double evThresholdOddsTaken = 0;
		double numberOfThresholdBets = 0;
		
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
				if(date.getMonth() == 2){
					endI = i;
					break;
				}
			}
		}
		
		HistoricalDataParser historicalDataParser = new HistoricalDataParser();
		List<HistoricalDataElement> historicalDataList = historicalDataParser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\pending", "Full");
		
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
			if(!tipp.getTypeOfBet().equals("Match Odds") && !tipp.getTypeOfBet().equals("Over / Under")){
				continue;
			}
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
			double bestOdds = 0;
			for(int j = 0; j < availableBets.size(); j++){
				HistoricalDataElement historicalElement = availableBets.get(j);
				
				// ONE_TWO
				if(tipp.getTypeOfBet().equals("Match Odds")){
					List<OneTwoElement> oneTwoOdds = historicalElement.getOneTwoList();

					for(int oddIndex = 0; oddIndex < oneTwoOdds.size(); oddIndex++){
						OneTwoElement oneTwoElement = oneTwoOdds.get(oddIndex);
						Date oddsDate = new Date(oneTwoElement.getTime() * 1000);
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
						bestOdds = Math.max(bestOdds, odds);
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
							Date oddsDate = new Date(totalElement.getTime() * 1000);
							double odds = 0;
							if(oddsDate.before(tippPublishedDate)){
								if(tippIndex == 0){
									odds = 1 + totalElement.getOver();
								}
								else if(tippIndex == 1){
									odds = 1 + totalElement.getUnder();
								}
							}
							bestOdds = Math.max(bestOdds, odds);		
						}
					}					
				}
				if(bestOdds != 0){
					if(tipp.getTypeOfBet().equals("Match Odds")){
						double take = 100;
						numberOfAllBets++;
						numberOfAllBetsMatchOdds++;
						if(tipp.getProfit() < 0){
							evAllPossibleBetsTaken -= take;
							evAllPossibleBetsTakenMatchOdds -= take;
						}
						else{
							evAllPossibleBetsTaken += take * bestOdds - take;
							evAllPossibleBetsTakenMatchOdds  += take * bestOdds - take;
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
						double take = 100;
						numberOfAllBets++;
						numberOfAllBetsOverUnder++;
						if(tipp.getProfit() < 0){
							evAllPossibleBetsTaken -= take;
							evAllPossibleBetsTakenOverUnder -= take;
						}
						else{
							evAllPossibleBetsTaken += take * bestOdds - take;
							evAllPossibleBetsTakenOverUnder  += take * bestOdds - take;
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
		}	
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
	}
	public static void main(String[] args) throws IOException {
		BetAdvisorBacktest backTest = new BetAdvisorBacktest();
		backTest.runBacktest();
	}
}
