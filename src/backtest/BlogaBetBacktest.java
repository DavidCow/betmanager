package backtest;

import historicalData.HdpElement;
import historicalData.HistoricalDataElement;
import historicalData.HistoricalDataParser;
import historicalData.OneTwoElement;
import historicalData.TotalElement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.math3.stat.inference.TTest;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import betadvisor.BetAdvisorElement;
import bettingBot.TeamMapping;
import blogaBetHistoricalDataParsing.BlogaBetComparator;
import blogaBetHistoricalDataParsing.BlogaBetElement;
import blogaBetHistoricalDataParsing.BlogaBetParser;

public class BlogaBetBacktest {

	public void runBacktest() throws IOException{
		
		bestOddsFactor = 1;
		double oddsratio = 0;
		
		
		BlogaBetParser parser = new BlogaBetParser();
		List<BlogaBetElement> blogaBetList = parser.parseSheets("blogaBetTipsterData/csv");
		Collections.sort(blogaBetList, new BlogaBetComparator());
		
		// Result variables
		double profit = 0;
		int numberOfMatches = 0;
		double averageYield = 0;
		int checkedTipps = 0;
		int oddsFound = 0;
		
		List<Double> betEvs = new ArrayList<Double>();
		
		// We set the start and endIndex of considered tipps, according to the historical data that we have
		int startI = 0;
		int endI = 0;
		for(int i = 0; i < blogaBetList.size(); i++){
			Date date = blogaBetList.get(i).getGameDate();
			int y = date.getYear() + 1900;
			if(y == 2014){
				startI = i;
				break;
			}
		}
		for(int i = 0; i < blogaBetList.size(); i++){
			Date date = blogaBetList.get(i).getGameDate();
			int y = date.getYear() + 1900;
			if(y == 2016){
//				if(date.getMonth() == 8){
					endI = i;
					break;
//				}
			}
		}
		
		//Try to load the historical data from an object stream or load it flom csv files otherwise
		List<HistoricalDataElement> historicalDataList = null;
		File historicalDataFile = new File("allFullHistoricalData.dat");
		if(historicalDataFile.exists()){
            FileInputStream fileInput = new FileInputStream(historicalDataFile);
            BufferedInputStream br = new BufferedInputStream(fileInput);
            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
            try {
				historicalDataList = (List<HistoricalDataElement>)objectInputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
            objectInputStream.close();
            System.out.println("Historical data loaded from ObjectStream");
		}
		else{
			HistoricalDataParser historicalDataParser = new HistoricalDataParser();
			historicalDataList = historicalDataParser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\pending", "Full");
			historicalDataList.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\pending_2015", "Full"));
			historicalDataList.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\pending_2016", "Full"));	
			System.out.println("Historical data loaded from CSV");
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFile);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(historicalDataList);
            objectOutputStream.close();
		}
		
		// We dont have to loop over all historical data for every tipp, sonce some historical data will be
		// From games before the tipp
		int startJ = 0;
		boolean startJSet = false;
		
		/* Itterate over Tipps */
		for(int i = startI; i < endI; i++){
			startJSet = false;
			
			BlogaBetElement tipp = blogaBetList.get(i);
			if(!tipp.getTypeOfBet().equals("Match Odds") && !tipp.getTypeOfBet().equals("Over Under") && !tipp.getTypeOfBet().equals("Asian Handicap")){
				continue;
			}
			checkedTipps++;
			double suggestedOdds = tipp.getBestOdds();
			
			int matches = 0;
			int hostMatches = 0;
			String blogaBetHost = tipp.getHost();
			String blogaBetGuest = tipp.getGuest();
			
			String historicalDataHost = "";
			String historicalDataGuest = "";
			
			Date blogaBetGameDate = tipp.getGameDate();
			String s0 = blogaBetGameDate.toGMTString();
			
			/* The index of the tipp */
			String tippTeam = tipp.getTipTeam();
			int tippIndex = -1;
			
			List<HistoricalDataElement> availableBets = new ArrayList<HistoricalDataElement>();
			
			/* Itterate over games and find the markets that match the tipp */
			for(int j = startJ; j < historicalDataList.size(); j++){
				
				HistoricalDataElement historicalDataElement = historicalDataList.get(j);
				Date historicalDataGameDate = historicalDataElement.getStartDate();
				String s1 = historicalDataGameDate.toGMTString();
				
				historicalDataHost = historicalDataElement.getHost();
				historicalDataGuest = historicalDataElement.getGuest();
				
				// We can break the inner loop if the start Time of the match of the historical
				// data element is later than that of the startTime of the tipped game
				// because both lists are sorted
				// We can break the inner loop if the start Time of the match of the historical
				// data element is later than that of the startTime of the tipped game
				// because both lists are sorted		
				
				long t0 = blogaBetGameDate.getTime();
				long t1 = historicalDataGameDate.getTime();
				
				if(t1 > t0 + 60 * 60 * 1000){
					break;
				}	
				
				if(Math.abs(t0 - t1) < 10 * 60 * 1000){
					// Set the new startJ
					// it will be the first index j with a date equal to the start of the game of the tipp
					// because both lists are sorted, the relevant index j for the next tipp can not be lower than for the
					// current tipp
					if(!startJSet){
						startJSet = true;
						startJ = j;
					}
					if(TeamMapping.teamsMatch(historicalDataHost, blogaBetHost) || TeamMapping.teamsMatch(historicalDataGuest, blogaBetGuest)){
						
						if(tipp.getTypeOfBet().equals("Match Odds")){
							if(tippTeam.equalsIgnoreCase("Draw")){
								tippIndex = 2;
							}
							else if(tippTeam.equalsIgnoreCase(blogaBetHost) || TeamMapping.teamsMatch(tippTeam, blogaBetHost)){
								tippIndex = 0;
							}
							else if(tippTeam.equalsIgnoreCase(blogaBetGuest) || TeamMapping.teamsMatch(tippTeam, blogaBetGuest)){
								tippIndex = 1;
							}		
						}
						if(tipp.getTypeOfBet().equals("Over Under")){
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
						if(tipp.getTypeOfBet().equals("Asian Handicap")){
							double pivot = tipp.getPivotValue();
							if(pivot == -10)
								continue;
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
									if(tippTeam.indexOf(blogaBetHost) != -1){
										tippIndex = 0;
									}
									else if(tippTeam.indexOf(blogaBetGuest) != -1){
										tippIndex = 1;
									}
								}
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

					double odds = 0;
					for(int oddIndex = 0; oddIndex < oneTwoOdds.size(); oddIndex++){
						OneTwoElement oneTwoElement = oneTwoOdds.get(oddIndex);
						Date oddsDate = new Date(oneTwoElement.getTime());
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
					}	
					if(odds > bestOdds){
						bestOdds = odds;
						bestSource = historicalElement;
					}	
				}
				
				// OVER / UNDER
				if(tipp.getTypeOfBet().equals("Over Under")){
					List<TotalElement> totalOdds = historicalElement.getTotalList();

					double odds = 0;
					for(int oddIndex = 0; oddIndex < totalOdds.size(); oddIndex++){
						TotalElement totalElement = totalOdds.get(oddIndex);
						
						int totalStart = tipp.getSelection().lastIndexOf("+") + 1;
						String totalString = tipp.getSelection().substring(totalStart);
						double total = Double.parseDouble(totalString);
						
						if(totalElement.getTotal() == total){
							Date oddsDate = new Date(totalElement.getTime());
							if(oddsDate.before(tippPublishedDate)){
								if(tippIndex == 0){
									odds = 1 + totalElement.getOver();
								}
								else if(tippIndex == 1){
									odds = 1 + totalElement.getUnder();
								}
							}
						}
					}	
					if(odds > bestOdds){
						bestOdds = odds;
						bestSource = historicalElement;
					}	
				}
				
				// Asian handicap
				if(tipp.getTypeOfBet().equals("Asian handicap")){
					List<HdpElement> hdpOdds = historicalElement.getHdpList();

					double odds = 0;
					for(int oddIndex = 0; oddIndex < hdpOdds.size(); oddIndex++){
						HdpElement totalElement = hdpOdds.get(oddIndex);
						
						int pivotStart = tipp.getSelection().lastIndexOf("-") + 1;
						if(pivotStart != 0){
							try{
								String pivotString = tipp.getSelection().substring(pivotStart);
								double pivot = Double.parseDouble(pivotString);
								
								if(totalElement.getPivot() == pivot){
									Date oddsDate = new Date(totalElement.getTime());
									if(oddsDate.before(tippPublishedDate)){
										if(tippIndex == 0){
											odds = 1 + totalElement.getHost();
										}
										else if(tippIndex == 1){
											odds = 1 + totalElement.getGuest();
										}
									}
								}		
							}catch(Exception e){
								
							}
						}
					}	
					if(odds > bestOdds){
						bestOdds = odds;
						bestSource = historicalElement;
					}
				}
			}
			if(bestOdds != 0){
				oddsFound++;
				if(bestOdds > tipp.getBestOdds())
					bestOdds = tipp.getBestOdds();
				
				bestOdds *= bestOddsFactor;
				oddsRatio += bestOdds / tipp.getOdds();
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
									lastLay = 0.5 * (oneTwoElement.getDraw() + oneTwoElement.getTwo());
								}
								else if(tippIndex == 1){
									lastLay = 0.5 * (oneTwoElement.getDraw() + oneTwoElement.getOne());
								}
								else if(tippIndex == 2){
									lastLay = 0.5 * (oneTwoElement.getOne() + oneTwoElement.getTwo());
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
					if(bestOdds > 10){
						System.out.println();
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
		averageYield /= numberOfMatches;

		System.out.println();
		System.out.println("Matches: " + numberOfMatches);
		System.out.println("Percentage of matched Bets: " + numberOfMatches * 100.0 / checkedTipps);
		System.out.println("Profit: " + profit);
		System.out.println("Yield: " + averageYield);
		
		System.out.println();
		double[] bEv = new double[betEvs.size()];
		for(int e = 0; e < betEvs.size(); e++){
			bEv[e] = betEvs.get(e);
		}
		System.out.println("\nSignificance tests:");
		TTest test = new TTest();
		double significance = test.tTest(0, bEv);
		System.out.println("Significance level of < 0: " + significance / 2);
		significance = test.tTest(1, bEv);
		System.out.println("Significance level of < 1: " + significance / 2);
		significance = test.tTest(2, bEv);
		System.out.println("Significance level of < 2: " + significance / 2);
		significance = test.tTest(3, bEv);
		System.out.println("Significance level of < 3: " + significance / 2);
		significance = test.tTest(4, bEv);
		System.out.println("Significance level of < 4: " + significance / 2);
		significance = test.tTest(5, bEv);
		System.out.println("Significance level of < 5: " + significance / 2);
		significance = test.tTest(6, bEv);
		System.out.println("Significance level of < 6: " + significance / 2);
		significance = test.tTest(7, bEv);
		System.out.println("Significance level of < 7: " + significance / 2);
		System.out.println();
		
		// Chart
		XYSeries series = new XYSeries("Profit");
		XYDataset xyDataset = new XYSeriesCollection(series);
		double totalProfit = 0;
		for(int i = 0; i < betEvs.size(); i++){
			series.add(i, totalProfit);
			totalProfit += betEvs.get(i);
		}
		final JFreeChart chart = ChartFactory.createXYLineChart("Profit", "Bets", "Profit", xyDataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Backtest");
        frame.setContentPane(chartPanel);
        frame.setSize(600, 400);
        frame.setVisible(true);
	}
	
	public static void main(String[] args) throws IOException {
		BlogaBetBacktest backtest = new BlogaBetBacktest();
		backtest.runBacktest();
	}
}
