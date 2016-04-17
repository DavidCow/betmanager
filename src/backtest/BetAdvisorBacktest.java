package backtest;

import historicalData.HdpElement;
import historicalData.HistoricalDataComparator;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.swing.JFrame;

import org.apache.commons.math3.stat.inference.TTest;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import weka.core.Instance;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import bettingBot.TeamMapping;
import eastbridgeLiquidityMining.regression.ArffCreator;
import eastbridgeLiquidityMining.regression.PredictiveModel;

public class BetAdvisorBacktest {
	
	public void runBacktest() throws IOException{

		TreeSet<String> teams = new TreeSet<String>();
		TreeSet<String> leagues = new TreeSet<String>();
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
		int m = 0;
		
		// EVs with lay hedging
		double evLayed = 0;
		
		// If we only take bets with odds higher or equal to what the tipster suggested
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
		
		// Variable for testing worse cases of bestOdds
		double bestOddsFactor = 1;
		
		// Variance calculation
		List<Double> betEvs = new ArrayList<Double>();
		List<Double> hedgedEvs = new ArrayList<Double>();
		
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
		// Odds Ratio
		double oddsRatio = 0;
		
		// Liquidity Calculation
		double evAllPossibleBetsTakenMaxLiquidity = 0;
		List<Double> betEvsMaxLiquidity = new ArrayList<Double>();
		List<Double> betLiquidities = new ArrayList<Double>();
		List<String> liquidityTipsters = new ArrayList<String>(); 
		PredictiveModel repTreeModel = new PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		double averageLiquidity = 0;
		int numberOfLiquidityCalculations = 0;
		
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
			if(y == 2016){
				endI = i;
				break;
			}
		}
		
		//Try to load the historical data from an object stream or load it flom csv files otherwise

		//Full
		List<HistoricalDataElement> historicalDataList_Full = new ArrayList<HistoricalDataElement>();
		File historicalDataFilePending = new File("allFullHistoricalData_Pending.dat");
		File historicalDataFileEarly = new File("allFullHistoricalData_Early.dat");
		File historicalDataFileLive = new File("allFullHistoricalData_Live.dat");
		
		// Half time
		List<HistoricalDataElement> historicalDataList_Half = new ArrayList<HistoricalDataElement>();
		File historicalDataFilePending_Half = new File("allHalfHistoricalData_Pending.dat");
		File historicalDataFileEarly_Half = new File("allHalfHistoricalData_Early.dat");
		
		// Read Full Time Bets
		
		// Pending
		if(historicalDataFilePending.exists()){
			List<HistoricalDataElement> historicalDataList_Pending = new ArrayList<HistoricalDataElement>();
            FileInputStream fileInput = new FileInputStream(historicalDataFilePending);
            BufferedInputStream br = new BufferedInputStream(fileInput);
            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
            try {
    			historicalDataList_Pending.addAll((List<HistoricalDataElement>)objectInputStream.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
            objectInputStream.close();
            historicalDataList_Full.addAll(historicalDataList_Pending);
            System.out.println("Historical data pending full loaded from ObjectStream");
		}
		else{
			HistoricalDataParser historicalDataParser = new HistoricalDataParser();
			List<HistoricalDataElement> historicalDataList_Pending = new ArrayList<HistoricalDataElement>();
			historicalDataList_Pending.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\pending", "Full"));
			historicalDataList_Pending.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\pending_2015", "Full"));
			historicalDataList_Pending.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\pending_2016", "Full"));
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFilePending);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(historicalDataList_Pending);
            objectOutputStream.close();
			historicalDataList_Full.addAll(historicalDataList_Pending);
			System.out.println("Historical data pending full loaded from CSV");
		}

		
		// Early
		if(historicalDataFileEarly.exists()){
            FileInputStream fileInput = new FileInputStream(historicalDataFileEarly);
            List<HistoricalDataElement> historicalDataList_Early = new ArrayList<HistoricalDataElement>();
            BufferedInputStream br = new BufferedInputStream(fileInput);
            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
            try {
            	historicalDataList_Early.addAll((List<HistoricalDataElement>)objectInputStream.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
            objectInputStream.close();
            historicalDataList_Full.addAll(historicalDataList_Early);
            System.out.println("Historical data early full loaded from ObjectStream");
		}
		else{
			HistoricalDataParser historicalDataParser = new HistoricalDataParser();
			List<HistoricalDataElement> historicalDataList_Early = new ArrayList<HistoricalDataElement>();
			historicalDataList_Early.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\early", "Full"));
			historicalDataList_Early.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\early_2015", "Full"));
			historicalDataList_Early.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\early_2016", "Full"));	
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFileEarly);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(historicalDataList_Early);
            objectOutputStream.close();
            historicalDataList_Full.addAll(historicalDataList_Early);
			System.out.println("Historical data early full loaded from CSV");
		}
		
		//Live 
		if(historicalDataFileLive.exists()){
            FileInputStream fileInput = new FileInputStream(historicalDataFileLive);
            List<HistoricalDataElement> historicalDataList_Live = new ArrayList<HistoricalDataElement>();
            BufferedInputStream br = new BufferedInputStream(fileInput);
            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
            try {
            	historicalDataList_Live.addAll((List<HistoricalDataElement>)objectInputStream.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
            objectInputStream.close();
            historicalDataList_Full.addAll(historicalDataList_Live);
            System.out.println("Historical data live full loaded from ObjectStream");
		}
		else{
			HistoricalDataParser historicalDataParser = new HistoricalDataParser();
			List<HistoricalDataElement> historicalDataList_Live = new ArrayList<HistoricalDataElement>();
			historicalDataList_Live.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\live", "Full"));
//			historicalDataList_Early.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\live_2015", "Full"));
//			historicalDataList_Early.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\live_2016", "Full"));	
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFileLive);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(historicalDataList_Live);
            objectOutputStream.close();
            historicalDataList_Full.addAll(historicalDataList_Live);
			System.out.println("Historical data early full loaded from CSV");
		}		
		
		Collections.sort(historicalDataList_Full, new HistoricalDataComparator());
		
		
		// Read half Time Bets
		if(historicalDataFilePending_Half.exists()){
			List<HistoricalDataElement> HistoricalDataList_Pending_Half = new ArrayList<HistoricalDataElement>();
            FileInputStream fileInput = new FileInputStream(historicalDataFilePending_Half);
            BufferedInputStream br = new BufferedInputStream(fileInput);
            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
            try {
            	HistoricalDataList_Pending_Half.addAll((List<HistoricalDataElement>)objectInputStream.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
            objectInputStream.close();
            historicalDataList_Half.addAll(HistoricalDataList_Pending_Half);
            System.out.println("Historical data pending half loaded from ObjectStream");
		}
		else{
			HistoricalDataParser historicalDataParser = new HistoricalDataParser();
			List<HistoricalDataElement> HistoricalDataList_Pending_Half = new ArrayList<HistoricalDataElement>();
			HistoricalDataList_Pending_Half.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\pending_half", "Half"));
			HistoricalDataList_Pending_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\pending_half_2015", "Half"));
			HistoricalDataList_Pending_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\pending_half_2016", "Half"));	
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFilePending_Half);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(HistoricalDataList_Pending_Half);
            objectOutputStream.close();
            historicalDataList_Half.addAll(HistoricalDataList_Pending_Half);
			System.out.println("Historical Pending Halfdata loaded from CSV");
		}
		if(historicalDataFileEarly_Half.exists()){
			List<HistoricalDataElement> historicalDataList_Early_Half = new ArrayList<HistoricalDataElement>();
            FileInputStream fileInput = new FileInputStream(historicalDataFileEarly_Half);
            BufferedInputStream br = new BufferedInputStream(fileInput);
            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
            try {
            	historicalDataList_Early_Half.addAll((List<HistoricalDataElement>)objectInputStream.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
            objectInputStream.close();
            historicalDataList_Half.addAll(historicalDataList_Early_Half);
            System.out.println("Historical data early half loaded from ObjectStream");
		}
		else{
			HistoricalDataParser historicalDataParser = new HistoricalDataParser();
			List<HistoricalDataElement> HistoricalDataList_Early_Half = new ArrayList<HistoricalDataElement>();
			HistoricalDataList_Early_Half.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\early_half", "Half"));
			HistoricalDataList_Early_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\early_half_2015", "Half"));
			HistoricalDataList_Early_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Patryk\\Desktop\\early_half_2016", "Half"));	
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFileEarly_Half);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(HistoricalDataList_Early_Half);
            objectOutputStream.close();
            historicalDataList_Half.addAll(HistoricalDataList_Early_Half);
			System.out.println("Historical data early half loaded from CSV");
		}
		Collections.sort(historicalDataList_Half, new HistoricalDataComparator());
		
		/* Needed for comparison of Dates */
		DateFormat gmtFormat = new SimpleDateFormat();
		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTime);
		
		// We dont have to loop over all historical data for every tipp, since some historical data will be
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
			if(!tipp.getTypeOfBet().equals("Match Odds") && !tipp.getTypeOfBet().equals("Over / Under") && !tipp.getTypeOfBet().equals("Asian handicap")){
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
			for(int j = startJ; j < historicalDataList_Full.size(); j++){
				
				HistoricalDataElement historicalDataElement= historicalDataList_Full.get(j);
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
				
				if(Math.abs(t0 - t1) < 10 * 60 * 1000){
					// Set the new startJ
					// it will be the first index j with a date equal to the start of the game of the tipp
					// because both lists are sorted, the relevant index j for the next tipp can not be lower than for the
					// current tipp
					if(!startJSet){
						startJSet = true;
						startJ = j;
					}
					
					String betAdvisorLeague = tipp.getLeague();
					String historicalDataLeague = ArffCreator.getCleanedNames(historicalDataElement.getLeague());

					//System.out.println(betAdvisorLeague + " , " + historicalDataLeague);
					
					betAdvisorHost = BetAdvisorParser.parseHostFromEvent(tipp.getEvent());
					betAdvisorGuest = BetAdvisorParser.parseGuestFromEvent(tipp.getEvent());
					
					historicalDataHost = historicalDataElement.getHost();
					historicalDataGuest = historicalDataElement.getGuest();
					matches++;
					
					//System.out.println(betAdvisorHost + " , " + historicalDataHost);	
					if(TeamMapping.teamsMatch(historicalDataHost, betAdvisorHost) || TeamMapping.teamsMatch(historicalDataGuest, betAdvisorGuest)){
						teams.add(historicalDataHost);
						teams.add(historicalDataGuest);
						leagues.add(historicalDataLeague);
						availableBets.add(historicalDataElement);
						if(tipp.getTypeOfBet().equals("Match Odds")){
							if(tippTeam.equalsIgnoreCase("Draw")){
								tippIndex = 2;
							}
							else if(tippTeam.equalsIgnoreCase(betAdvisorHost) || TeamMapping.teamsMatch(tippTeam, betAdvisorHost)){
								tippIndex = 0;
							}
							else if(tippTeam.equalsIgnoreCase(betAdvisorGuest) || TeamMapping.teamsMatch(tippTeam, betAdvisorGuest)){
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
							else{
								pivotStart = tipp.getSelection().lastIndexOf("+") + 1;
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
													tippIndex = 1;
												}
												else if(tippTeam.indexOf(betAdvisorGuest) != -1){
													tippIndex = 0;
												}
											}
										}			
									}catch(Exception e){
										
									}
								}								
							}
						}
					}
					else if(betAdvisorLeague.equals("International Friendly Games") && tipp.getTypeOfBet().equals("Match Odds")){
						if(betAdvisorHost.equalsIgnoreCase(historicalDataGuest)){
							teams.add(historicalDataHost);
							teams.add(historicalDataGuest);
							leagues.add(historicalDataLeague);
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
				if(tipp.getTypeOfBet().equals("Over / Under")){
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
						else{
							pivotStart = tipp.getSelection().lastIndexOf("+") + 1;
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
					}	
					if(odds > bestOdds){
						bestOdds = odds;
						bestSource = historicalElement;
					}
				}
			}
			if(bestOdds != 0){
				
				if(bestOdds > tipp.getOdds())
					bestOdds = tipp.getOdds();
				
				// Calculate Liquidity
				double liquidity = 0;			
				Instance record = repTreeModel.createWekaInstance(bestSource, tipp, bestOdds);
				if(record != null){
					try {
						liquidity = repTreeModel.classifyInstance(record);
						averageLiquidity += liquidity;
						numberOfLiquidityCalculations++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				double take = 100;			
				
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
					numberOfAllBets++;
					numberOfAllBetsMatchOdds++;
					if(take > 0){
						int a = 12;
						int b = a;
					}
					if(tipp.getProfit() < 0){
						evAllPossibleBetsTaken -= take;
						evAllPossibleBetsTakenMatchOdds -= take;
						betEvs.add(-take);
						
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
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
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
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
					
					numberOfAllBets++;
					numberOfAllBetsOverUnder++;
					if(tipp.getProfit() < 0){
						evAllPossibleBetsTaken -= take;
						evAllPossibleBetsTakenOverUnder -= take;
						betEvs.add(-take);
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
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
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
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
					
					numberOfAllBets++;
					numberOfAllBetsHdp++;
					if(tipp.getProfit() < 0){
						evAllPossibleBetsTaken -= take;
						evAllPossibleBetsTakenHdp -= take;
						betEvs.add(-take);
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
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
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
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
		
		// HalfTime bets
		// We dont have to loop over all historical data for every tipp, since some historical data will be
		// From games before the tipp
		startJ = 0;
		startJSet = false;
				
		/* Itterate over Tipps */
		for(int i = startI; i < i; i++){
			startJSet = false;
			
			int matches = 0;
			int hostMatches = 0;
			String betAdvisorHost = "";
			String betAdvisorGuest = "";
			
			String historicalDataHost = "";
			String historicalDataGuest = "";
			
			/* The Date of the tipp */
			BetAdvisorElement tipp = betAdvisorList.get(i);
			if(!tipp.getTypeOfBet().equals("Match Odds 1st Half") && !tipp.getTypeOfBet().equals("Over / Under 1st Half") && !tipp.getTypeOfBet().equals("Asian Handicap 1st Half")){
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
			for(int j = startJ; j < historicalDataList_Half.size(); j++){
				
				HistoricalDataElement historicalDataElement= historicalDataList_Half.get(j);
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
				
				if(Math.abs(t0 - t1) < 10 * 60 * 1000){
					// Set the new startJ
					// it will be the first index j with a date equal to the start of the game of the tipp
					// because both lists are sorted, the relevant index j for the next tipp can not be lower than for the
					// current tipp
					if(!startJSet){
						startJSet = true;
						startJ = j;
					}
					
					String betAdvisorLeague = tipp.getLeague();
					String historicalDataLeague = ArffCreator.getCleanedNames(historicalDataElement.getLeague());

					//System.out.println(betAdvisorLeague + " , " + historicalDataLeague);
					
					betAdvisorHost = BetAdvisorParser.parseHostFromEvent(tipp.getEvent());
					betAdvisorGuest = BetAdvisorParser.parseGuestFromEvent(tipp.getEvent());
					
					historicalDataHost = historicalDataElement.getHost();
					historicalDataGuest = historicalDataElement.getGuest();
					matches++;
					
					//System.out.println(betAdvisorHost + " , " + historicalDataHost);	
					if(TeamMapping.teamsMatch(historicalDataHost, betAdvisorHost) || TeamMapping.teamsMatch(historicalDataGuest, betAdvisorGuest)){
						
						m++;
						
						teams.add(historicalDataHost);
						teams.add(historicalDataGuest);
						leagues.add(historicalDataLeague);
						availableBets.add(historicalDataElement);
						String cleanedTipTeam = tipp.getSelection();
						cleanedTipTeam = cleanedTipTeam.replaceAll(" Half time", "");
						
						if(tipp.getTypeOfBet().equals("Match Odds 1st Half")){
							if(tippTeam.equalsIgnoreCase("Draw")){
								tippIndex = 2;
							}
							else if(cleanedTipTeam.equalsIgnoreCase(betAdvisorHost) || TeamMapping.teamsMatch(cleanedTipTeam, betAdvisorHost)){
								tippIndex = 0;
							}
							else if(cleanedTipTeam.equalsIgnoreCase(betAdvisorGuest) || TeamMapping.teamsMatch(cleanedTipTeam, betAdvisorGuest)){
								tippIndex = 1;
							}		
						}
						if(tipp.getTypeOfBet().equals("Over / Under 1st Half")){
							String selectionLine = tipp.getSelection();
							selectionLine = selectionLine.replaceAll(" Half time", "");
							int totalStart = selectionLine.lastIndexOf("+") + 1;
							String totalString = selectionLine.substring(totalStart);
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
									else{
										System.out.println();
									}
								}
							}
						}
						if(tipp.getTypeOfBet().equals("Asian Handicap 1st Half")){
							String selectionString = tipp.getSelection();
							selectionString = selectionString.replace(" Half time", "");
							int pivotStart = selectionString.lastIndexOf("-") + 1;
							if(pivotStart != 0){
								try{
									String pivotString = selectionString.substring(pivotStart);
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
											else{
												System.out.println();
											}
										}
									}			
								}catch(Exception e){
									
								}
							}
							else{
								pivotStart = selectionString.lastIndexOf("+") + 1;
								if(pivotStart != 0){
									try{
										String pivotString = selectionString.substring(pivotStart);
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
													tippIndex = 1;
												}
												else if(tippTeam.indexOf(betAdvisorGuest) != -1){
													tippIndex = 0;
												}
												else{
													System.out.println();
												}
											}
										}			
									}catch(Exception e){
										
									}
								}		
							}
						}
					}
					else if(betAdvisorLeague.equals("International Friendly Games") && tipp.getTypeOfBet().equals("Match Odds 1st Half")){
						if(betAdvisorHost.equalsIgnoreCase(historicalDataGuest)){
							teams.add(historicalDataHost);
							teams.add(historicalDataGuest);
							leagues.add(historicalDataLeague);
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
				if(tipp.getTypeOfBet().equals("Match Odds 1st Half")){
					List<OneTwoElement> oneTwoOdds = historicalElement.getOneTwoList();

					double odds = 0;
					for(int oddIndex = 0; oddIndex < oneTwoOdds.size(); oddIndex++){
						OneTwoElement oneTwoElement = oneTwoOdds.get(oddIndex);
						Date oddsDate = new Date(oneTwoElement.getTime());
						if(oddsDate.before(tippPublishedDate) || bestOdds == 0){
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
				if(tipp.getTypeOfBet().equals("Over / Under 1st Half")){
					List<TotalElement> totalOdds = historicalElement.getTotalList();

					double odds = 0;
					for(int oddIndex = 0; oddIndex < totalOdds.size(); oddIndex++){
						TotalElement totalElement = totalOdds.get(oddIndex);
						
						String selectionString = tipp.getSelection();
						selectionString = selectionString.replace(" Half time", "");
						int totalStart = selectionString.lastIndexOf("+") + 1;
						String totalString = selectionString.substring(totalStart);
						double total = Double.parseDouble(totalString);
						
						if(totalElement.getTotal() == total){
							Date oddsDate = new Date(totalElement.getTime());
							if(oddsDate.before(tippPublishedDate) || bestOdds == 0){
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
				if(tipp.getTypeOfBet().equals("Asian Handicap 1st Half")){
					String selectionString = tipp.getSelection();
					selectionString = selectionString.replace(" Half time", "");
					List<HdpElement> hdpOdds = historicalElement.getHdpList();

					double odds = 0;
					for(int oddIndex = 0; oddIndex < hdpOdds.size(); oddIndex++){
						HdpElement totalElement = hdpOdds.get(oddIndex);
						
						int pivotStart = selectionString.lastIndexOf("-") + 1;
						if(pivotStart != 0){
							try{
								String pivotString = selectionString.substring(pivotStart);
								double pivot = Double.parseDouble(pivotString);
								
								if(totalElement.getPivot() == pivot){
									Date oddsDate = new Date(totalElement.getTime());
									if(oddsDate.before(tippPublishedDate) || bestOdds == 0){
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
						else{
							pivotStart = selectionString.lastIndexOf("+") + 1;
							if(pivotStart != 0){
								try{
									String pivotString = selectionString.substring(pivotStart);
									double pivot = Double.parseDouble(pivotString);
									
									if(totalElement.getPivot() == pivot){
										Date oddsDate = new Date(totalElement.getTime());
										if(oddsDate.before(tippPublishedDate) || bestOdds == 0){
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
					}	
					if(odds > bestOdds){
						bestOdds = odds;
						bestSource = historicalElement;
					}
				}
			}
			if(bestOdds == 0){
				System.out.println();
			}
			if(bestOdds != 0){
				
				if(bestOdds > tipp.getOdds())
					bestOdds = tipp.getOdds();
				
				// Calculate Liquidity
				double liquidity = 0;			
				Instance record = repTreeModel.createWekaInstance(bestSource, tipp, bestOdds);
				if(record != null){
					try {
						liquidity = repTreeModel.classifyInstance(record);
						averageLiquidity += liquidity;
						numberOfLiquidityCalculations++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				double take = 100;
				if(liquidity > 13000){
					System.out.println();
				}
				
				
				bestOdds *= bestOddsFactor;
				oddsRatio += bestOdds / tipp.getOdds();
				if(tipp.getTypeOfBet().equals("Match Odds 1st Half")){
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
					numberOfAllBets++;
					numberOfAllBetsMatchOdds++;
					if(take > 0){
						int a = 12;
						int b = a;
					}
					if(tipp.getProfit() < 0){
						evAllPossibleBetsTaken -= take;
						evAllPossibleBetsTakenMatchOdds -= take;
						betEvs.add(-take);
						
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
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
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
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
				if(tipp.getTypeOfBet().equals("Over / Under 1st Half")){	
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
					
					numberOfAllBets++;
					numberOfAllBetsOverUnder++;
					if(tipp.getProfit() < 0){
						evAllPossibleBetsTaken -= take;
						evAllPossibleBetsTakenOverUnder -= take;
						betEvs.add(-take);
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
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
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
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
				if(tipp.getTypeOfBet().equals("Asian Handicap 1st Half")){
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
					
					numberOfAllBets++;
					numberOfAllBetsHdp++;
					if(tipp.getProfit() < 0){
						evAllPossibleBetsTaken -= take;
						evAllPossibleBetsTakenHdp -= take;
						betEvs.add(-take);
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
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
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
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
			betVariance += Math.pow((betEvs.get(i) - evPerBet), 2);
		}
		betVariance /= betEvs.size() - 1;		
		System.out.println("Normal variance per Bet: " + betVariance);
		betVariance /= betEvs.size();	
		double betSigma = Math.pow(betVariance, 0.5);
		
		double betVarianceHedged = 0;
		double evPerBetHedged = evLayed / numberOfTakenLaysThreshold;
		for(int i = 0; i < hedgedEvs.size(); i++){
			betVarianceHedged += Math.pow((hedgedEvs.get(i) - evPerBetHedged), 2);
		}
		betVarianceHedged /= hedgedEvs.size() - 1;
		System.out.println("Hedged variance per Bet: " + betVarianceHedged);
		betVarianceHedged /= hedgedEvs.size();
		double betSigmaHedged = Math.pow(betVarianceHedged, 0.5);
		
		System.out.println("Normal variance of mean: " + betVariance);
		System.out.println("Normal sigma of mean: " + betSigma);
		System.out.println("Hedged variance of mean: " + betVarianceHedged);
		System.out.println("Hedged sigma of mean: " + betSigmaHedged);
		
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
		
		// Odds ratio
		oddsRatio /= numberOfAllBets;
		System.out.println("Odds Ratio: " + oddsRatio);
		
		//print teams and leagues
//		for(String s : leagues)
//			System.out.println(s);
//		System.out.println(leagues.size());
		
		// Liquidity
		System.out.println();
		averageLiquidity /= numberOfLiquidityCalculations;
		System.out.println("Number Of Calculated Liquidities: " +  numberOfLiquidityCalculations);
		System.out.println("Average Liquidity: " + averageLiquidity);
		System.out.println("Profit for Max Stake: " + evAllPossibleBetsTakenMaxLiquidity);
		
		double minLiquidity = Double.POSITIVE_INFINITY;
		double maxLiquidity = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < betLiquidities.size(); i++){
			Double l = betLiquidities.get(i);
			maxLiquidity = Math.max(maxLiquidity, l);
			minLiquidity = Math.min(minLiquidity, l);
		}
		
		int numberOfStakes = 10;
		double[] yieldByStake = new double[numberOfStakes];
		int[] numberOfBetsByStake = new int[numberOfStakes];
		
		double stakeLevel = (maxLiquidity - minLiquidity) / numberOfStakes;
		System.out.println("maxStake: " + maxLiquidity);
		System.out.println("minStake: " + minLiquidity);
		
		for(int i = 0; i < betLiquidities.size(); i++){
			int stakeIndex = -1;
			for(int j = 0; j < numberOfStakes; j++){
				double maxStake = betLiquidities.get(i);
				if(maxStake > minLiquidity + (j + 1) * stakeLevel + 0.01)
					continue;
				numberOfBetsByStake[j]++;
				stakeIndex = j;
				break;
			}
			double l = betLiquidities.get(i);
			double ev = betEvsMaxLiquidity.get(i);
			double p = ev / l;
			yieldByStake[stakeIndex] += p;
		}
		
		for(int i = 0; i < yieldByStake.length; i++){
			yieldByStake[i] /= Math.max(1, numberOfBetsByStake[i]);
		}
		System.out.println("Stakes:");
		for(int i = 0; i < numberOfStakes; i++){
			double stakeStart = minLiquidity + i * stakeLevel;
			double stakeEnd = stakeStart + stakeLevel;
			System.out.println(i + ": " + stakeStart + " - " + stakeEnd);
		
		}
		System.out.println("bets per Stake: " + Arrays.toString(numberOfBetsByStake));
		for(int i = 0; i < yieldByStake.length; i++){
			System.out.println(yieldByStake[i]);
		}
		
		// By Tipster
		Map<String, Double> liquidityByTipster = new HashMap<String, Double>();
		Map<String, Double> profitByTipster = new HashMap<String, Double>();
		Map<String, Double> numberOfBestByTipster = new HashMap<String, Double>();
		Map<String, Double> yieldByTipster = new HashMap<String, Double>();
		
		for(int i = 0; i < betEvsMaxLiquidity.size(); i++){
			double p = betEvsMaxLiquidity.get(i);
			double l = betLiquidities.get(i);
			double y = p / l;
			String tipster = liquidityTipsters.get(i);
			if(numberOfBestByTipster.containsKey(tipster)){
				numberOfBestByTipster.put(tipster, numberOfBestByTipster.get(tipster) + 1.0);
			}
			else{
				numberOfBestByTipster.put(tipster, 1.0);
			}
			if(liquidityByTipster.containsKey(tipster)){
				liquidityByTipster.put(tipster, liquidityByTipster.get(tipster) + l);
			}
			else{
				liquidityByTipster.put(tipster, l);
			}
			if(profitByTipster.containsKey(tipster)){
				profitByTipster.put(tipster, profitByTipster.get(tipster) + p);
			}
			else{
				profitByTipster.put(tipster, p);
			}
			if(yieldByTipster.containsKey(tipster)){
				yieldByTipster.put(tipster, yieldByTipster.get(tipster) + y);
			}
			else{
				yieldByTipster.put(tipster, y);
			}
		}
		
		for(String tipster : liquidityByTipster.keySet()){
			liquidityByTipster.put(tipster, liquidityByTipster.get(tipster) / numberOfBestByTipster.get(tipster));
		}
		for(String tipster : yieldByTipster.keySet()){
			yieldByTipster.put(tipster, yieldByTipster.get(tipster) / numberOfBestByTipster.get(tipster));
		}
		
		System.out.println();
		System.out.println("TIPSTERS:");
		for(String tipster : numberOfBestByTipster.keySet()){
			double n = numberOfBestByTipster.get(tipster);
			double y = yieldByTipster.get(tipster);
			double p = profitByTipster.get(tipster);
			double l = liquidityByTipster.get(tipster);
			System.out.println(tipster + " - number of bets: " + n + ", profit: " + p + ", yield: " + y  + ", roi: " + p / n + ", average liquidity: " + l);
		}
		
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
        
		// Chart Max Liquidity
		XYSeries seriesMax = new XYSeries("Profit Max Stake");
		XYDataset xyDatasetMax = new XYSeriesCollection(seriesMax);
		double totalProfitMax = 0;
		for(int i = 0; i < betEvsMaxLiquidity.size(); i++){
			seriesMax.add(i, totalProfitMax);
			totalProfitMax += betEvsMaxLiquidity.get(i);
		}
		final JFreeChart chartMax = ChartFactory.createXYLineChart("Profit", "Bets", "Profit", xyDatasetMax);
        final ChartPanel chartPanelMax = new ChartPanel(chartMax);
        JFrame frameMax = new JFrame("Backtest Max Stake");
        frameMax.setContentPane(chartPanelMax);
        frameMax.setSize(600, 400);
        frameMax.setVisible(true);
	}
	public static void main(String[] args) throws IOException {
		BetAdvisorBacktest backTest = new BetAdvisorBacktest();
		backTest.runBacktest();
	}
}
