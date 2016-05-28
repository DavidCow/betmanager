package backtest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.commons.math3.stat.inference.TTest;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorElement;
import bettingBot.TeamMapping;
import blogaBetHistoricalDataParsing.BlogaBetComparator;
import blogaBetHistoricalDataParsing.BlogaBetElement;
import blogaBetHistoricalDataParsing.BlogaBetParser;
import eastbridgeLiquidityMining.regression.PredictiveModel;
import historicalData.HdpElement;
import historicalData.HistoricalDataComparator;
import historicalData.HistoricalDataElement;
import historicalData.HistoricalDataParser;
import historicalData.OneTwoElement;
import historicalData.TotalElement;
import weka.core.Instance;

public class BlogaBetBacktest {
	
	public static final String BLOGABET_BACKTEST_PATH = "blogaBetBackTestBets.dat";
	public static final String BLOGABET_BACKTEST_RECORD_PATH = "blogaBetBackTestRecords.dat";
	public static final String BLOGABET_BACKTEST_LIQUIDITY_PATH = "blogaBetBackTestLiquidities.dat";
	public static final String BLOGABET_BACKTEST_BESTODDS_PATH = "blogaBetBackTestBestOdds.dat";
	public static final String BLOGABET_BACKTEST_TIP_PATH = "blogaBetBackTestTips.dat";
	
	public void runBacktest() throws IOException{
			
		BlogaBetParser parser = new BlogaBetParser();
		List<BlogaBetElement> blogaBetList = parser.parseSheets("blogaBetTipsterData/csv");
		Collections.sort(blogaBetList, new BlogaBetComparator());
		
		// Result variables
		double profit = 0;
		double averageYield = 0;
		
		// How many tips were checked
		int checkedTipps = 0;
		int checkedTippsMatchOdds = 0;
		int checkedTippsAsianHandicap = 0;
		int checkedTippsOverUnder = 0;		
		
		int goodOddsFound = 0;
		
		// Odds statistics
		double averageSuggestedOdds = 0;
		double averageRealOdds = 0;
		double averageSuggestedOddsMatchOdds = 0;
		double averageRealOddsMatchOdds = 0;
		double averageSuggestedOddsAsianHandicap = 0;
		double averageRealOddsAsianHandicap = 0;
		double averageSuggestedOddsOverUnder = 0;
		double averageRealOddsOverUnder = 0;
		
		double bestOddsFactor = 1;
		List<Double> realOddsList = new ArrayList<Double>();
		List<Double> suggestedOddsList = new ArrayList<Double>();
		List<Double> realOddsListMatchOdds = new ArrayList<Double>();
		List<Double> suggestedOddsListMatchOdds = new ArrayList<Double>();
		List<Double> realOddsListAsianHandicap = new ArrayList<Double>();
		List<Double> suggestedOddsListAsianHandicap = new ArrayList<Double>();
		List<Double> realOddsListOverUnder = new ArrayList<Double>();
		List<Double> suggestedOddsListOverUnder = new ArrayList<Double>();

		double oddsRatio = 0;	
		int oddsFound = 0;
		double oddsRatioMatchOdds = 0;
		int oddsFoundMatchOdds = 0;
		double oddsRatioAsianHandicap = 0;
		int oddsFoundAsianHandicap = 0;	
		double oddsRatioOverUnder = 0;
		int oddsFoundOverUnder = 0;
		
		// This list holds all the bets, that we have done in the backtest
		List<BlogaBetElement> bets = new ArrayList<BlogaBetElement>();
		// This list holds all the records for bets, that we have done in the backtest
		List<HistoricalDataElement> records = new ArrayList<HistoricalDataElement>();
		// This list holds all the liquidities for the bets, the indexes correspond to the bets that they were calculated for
		List<Double> liquidities = new ArrayList<Double>();
		// This list holds all the best Odds for the bets, the indexes correspond to the bets that they were calculated for
		List<Double> bestOddsList = new ArrayList<Double>();
		
		// Liquidity Calculation
		double evAllPossibleBetsTakenMaxLiquidity = 0;
		List<Double> betEvsMaxLiquidity = new ArrayList<Double>();
		List<Double> betLiquidities = new ArrayList<Double>();
		List<String> liquidityTipsters = new ArrayList<String>(); 
		PredictiveModel repTreeModel = new PredictiveModel("EastBridge6BackTest.arff", "bagging.model");
		double averageLiquidity = 0;
		int numberOfLiquidityCalculations = 0;
		
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

		//Full
		List<HistoricalDataElement> historicalDataList_Full = new ArrayList<HistoricalDataElement>();
		File historicalDataFilePending = new File("allFullHistoricalData_Pending.dat");
		File historicalDataFileEarly = new File("allFullHistoricalData_Early.dat");
		File historicalDataFileLive = new File("allFullHistoricalData_Live.dat");
		
		// Half time
		List<HistoricalDataElement> historicalDataList_Half = new ArrayList<HistoricalDataElement>();
		File historicalDataFilePending_Half = new File("allHalfHistoricalData_Pending.dat");
		File historicalDataFileEarly_Half = new File("allHalfHistoricalData_Early.dat");
		File historicalDataFileLive_Half = new File("allHalfHistoricalData_Live.dat");
		
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
			historicalDataList_Pending.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Administrator\\Desktop\\pending", "Full"));
			historicalDataList_Pending.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\pending_2015", "Full"));
			historicalDataList_Pending.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\pending_2016", "Full"));
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
			historicalDataList_Early.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Administrator\\Desktop\\early", "Full"));
			historicalDataList_Early.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\early_2015", "Full"));
			historicalDataList_Early.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\early_2016", "Full"));	
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
			historicalDataList_Live.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Administrator\\Desktop\\live", "Full"));
			historicalDataList_Live.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\live_2015", "Full"));
			historicalDataList_Live.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\live_2016", "Full"));	
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFileLive);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(historicalDataList_Live);
            objectOutputStream.close();
            historicalDataList_Full.addAll(historicalDataList_Live);
			System.out.println("Historical data live full loaded from CSV");
		}		
		
		Collections.sort(historicalDataList_Full, new HistoricalDataComparator());
		
		
		// Read half Time Bets
		
		// Pending
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
			HistoricalDataList_Pending_Half.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Administrator\\Desktop\\pending_half", "Half"));
			HistoricalDataList_Pending_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\pending_half_2015", "Half"));
			HistoricalDataList_Pending_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\pending_half_2016", "Half"));	
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFilePending_Half);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(HistoricalDataList_Pending_Half);
            objectOutputStream.close();
            historicalDataList_Half.addAll(HistoricalDataList_Pending_Half);
			System.out.println("Historical Pending Halfdata loaded from CSV");
		}
		
		// Early
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
			HistoricalDataList_Early_Half.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Administrator\\Desktop\\early_half", "Half"));
			HistoricalDataList_Early_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\early_half_2015", "Half"));
			HistoricalDataList_Early_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\early_half_2016", "Half"));	
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFileEarly_Half);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(HistoricalDataList_Early_Half);
            objectOutputStream.close();
            historicalDataList_Half.addAll(HistoricalDataList_Early_Half);
			System.out.println("Historical data early half loaded from CSV");
		}
		
		// Live
		if(historicalDataFileLive_Half.exists()){
			List<HistoricalDataElement> historicalDataList_Live_Half = new ArrayList<HistoricalDataElement>();
            FileInputStream fileInput = new FileInputStream(historicalDataFileLive_Half);
            BufferedInputStream br = new BufferedInputStream(fileInput);
            ObjectInputStream objectInputStream = new ObjectInputStream(br);	
            try {
            	historicalDataList_Live_Half.addAll((List<HistoricalDataElement>)objectInputStream.readObject());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
            objectInputStream.close();
            historicalDataList_Half.addAll(historicalDataList_Live_Half);
            System.out.println("Historical data live half loaded from ObjectStream");
		}
		else{
			HistoricalDataParser historicalDataParser = new HistoricalDataParser();
			List<HistoricalDataElement> HistoricalDataList_Live_Half = new ArrayList<HistoricalDataElement>();
			HistoricalDataList_Live_Half.addAll(historicalDataParser.parseFilesInFolder("C:\\Users\\Administrator\\Desktop\\early_half", "Half"));
			HistoricalDataList_Live_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\early_half_2015", "Half"));
			HistoricalDataList_Live_Half.addAll(historicalDataParser.parseFilesInFolderJayeson("C:\\Users\\Administrator\\Desktop\\early_half_2016", "Half"));	
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFileLive_Half);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(HistoricalDataList_Live_Half);
            objectOutputStream.close();
            historicalDataList_Half.addAll(HistoricalDataList_Live_Half);
			System.out.println("Historical data live half loaded from CSV");
		}
		
		Collections.sort(historicalDataList_Half, new HistoricalDataComparator());
		
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
			if(!tipp.getSport().equals("SOC") && !tipp.getSport().equals("Livebet")){
				continue;
			}
			double pivot = tipp.getPivotValue();
			if(pivot == -10){
				continue;
			}
			checkedTipps++;
			if(tipp.getTypeOfBet().equals("Match Odds")){
				checkedTippsMatchOdds++;
			}
			if(tipp.getTypeOfBet().equals("Asian Handicap")){
				checkedTippsAsianHandicap++;
			}
			if(tipp.getTypeOfBet().equals("Over Under")){
				checkedTippsOverUnder++;
			}
			if(tipp.getBestOdds() > 15){
				continue;
			}
			
			double suggestedOdds = tipp.getBestOdds();
			
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
			for(int j = startJ; j < historicalDataList_Full.size(); j++){
				
				HistoricalDataElement historicalDataElement = historicalDataList_Full.get(j);
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
					boolean teamsMatch = TeamMapping.teamsMatch(historicalDataHost, blogaBetHost) || TeamMapping.teamsMatch(historicalDataGuest, blogaBetGuest);
					if(!teamsMatch)
						teamsMatch =TeamMapping.teamsMatch(historicalDataHost, blogaBetHost, historicalDataGuest, blogaBetGuest);
					//System.out.println(betAdvisorHost + " , " + historicalDataHost);	
					if(teamsMatch){
						availableBets.add(historicalDataElement);
						
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
							double total = tipp.getPivotValue();
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
							pivot = tipp.getPivotValue();
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
				
				if(tippIndex == -1 && tipp.getTypeOfBet().equals("Match Odds")){
					System.out.println();
				}
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
						
						double total = tipp.getPivotValue();
						
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
				if(tipp.getTypeOfBet().equals("Asian Handicap")){
					List<HdpElement> hdpOdds = historicalElement.getHdpList();

					double odds = 0;
					for(int oddIndex = 0; oddIndex < hdpOdds.size(); oddIndex++){
						HdpElement totalElement = hdpOdds.get(oddIndex);
						pivot = tipp.getPivotValue();
						
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
				if(bestOdds > 0.90 * tipp.getBestOdds()){
					goodOddsFound++;
				}
				else{
					continue;
				}
			
				if(tipp.getBestOdds() > bestOdds * 1.5){
					System.out.println();
				}
				realOddsList.add(bestOdds);
				suggestedOddsList.add(tipp.getBestOdds());
				averageSuggestedOdds += tipp.getBestOdds();
				averageRealOdds += bestOdds;
				
				if(tipp.getTypeOfBet().equals("Match Odds")){
					averageSuggestedOddsMatchOdds += tipp.getBestOdds();
					averageRealOddsMatchOdds += bestOdds;
					oddsRatioMatchOdds += bestOdds / tipp.getBestOdds();
					oddsFoundMatchOdds++;
					realOddsListMatchOdds.add(bestOdds);
					suggestedOddsListMatchOdds.add(tipp.getBestOdds());
				}
				if(tipp.getTypeOfBet().equals("Asian Handicap")){
					averageSuggestedOddsAsianHandicap += tipp.getBestOdds();
					averageRealOddsAsianHandicap += bestOdds;
					oddsRatioAsianHandicap += bestOdds / tipp.getBestOdds();
					oddsFoundAsianHandicap++;
					realOddsListAsianHandicap.add(bestOdds);
					suggestedOddsListAsianHandicap.add(tipp.getBestOdds());
				}
				if(tipp.getTypeOfBet().equals("Over Under")){
					averageSuggestedOddsOverUnder += tipp.getBestOdds();
					averageRealOddsOverUnder += bestOdds;
					oddsRatioOverUnder += bestOdds / tipp.getBestOdds();
					oddsFoundOverUnder++;
					realOddsListOverUnder.add(bestOdds);
					suggestedOddsListOverUnder.add(tipp.getBestOdds());
				}
				
				double liquidity = 0;			
				Instance record = repTreeModel.createWekaInstance(bestSource, tipp, bestOdds);
				if(record != null){
					try {
						liquidity = repTreeModel.classifyInstance(record);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(liquidity != 0){
					numberOfLiquidityCalculations++;
					averageLiquidity += liquidity;
				}
				double take = 100;
				if(liquidity > 13000){
					System.out.println();
				}
				
				bets.add(tipp);
				records.add(bestSource);
				liquidities.add(liquidity);
				bestOddsList.add(bestOdds);
				
				bestOdds *= bestOddsFactor;
				oddsRatio += bestOdds / suggestedOdds;
				if(tipp.getTypeOfBet().equals("Match Odds")){
					take = 100;
					if(tipp.getResult().equalsIgnoreCase("LOST")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
						betEvs.add(-take);
						profit -= take;
					}
					else if(tipp.getResult().equalsIgnoreCase("WIN")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
						betEvs.add(take * bestOdds - take);
						profit += take * bestOdds - take;					
					}
					else{
						betLiquidities.add(liquidity);
						liquidityTipsters.add(tipp.getTipster());
						betEvsMaxLiquidity.add(0.0);
					}
					if(bestOdds < suggestedOdds){
						//System.out.println("Suggested Odds: " +  suggestedOdds + " real Odds: " + bestOdds);
					}		
				}
				if(tipp.getTypeOfBet().equals("Over Under")){	
					if(tipp.getResult().equalsIgnoreCase("LOST")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
						betEvs.add(-take);
						profit -= take;
					}
					else if(tipp.getResult().equalsIgnoreCase("WIN")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
						betEvs.add(take * bestOdds - take);
						profit += take * bestOdds - take;					
					}
					else{
						betLiquidities.add(liquidity);
						liquidityTipsters.add(tipp.getTipster());
						betEvsMaxLiquidity.add(0.0);
					}
					if(bestOdds < suggestedOdds){
//						System.out.println("Suggested Odds: " +  suggestedOdds + " real Odds: " + bestOdds);
					}						
				}
				if(tipp.getTypeOfBet().equals("Asian Handicap")){
					if(tipp.getResult().equalsIgnoreCase("LOST")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
						betEvs.add(-take);
						profit -= take;
					}
					else if(tipp.getResult().equalsIgnoreCase("WIN")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
						betEvs.add(take * bestOdds - take);
						profit += take * bestOdds - take;					
					}
					else{
						betLiquidities.add(liquidity);
						liquidityTipsters.add(tipp.getTipster());
						betEvsMaxLiquidity.add(0.0);
					}
					if(bestOdds < suggestedOdds){
//						System.out.println("Suggested Odds: " +  suggestedOdds + " real Odds: " + bestOdds);
					}						
				}
			}			
		}
		// We dont have to loop over all historical data for every tipp, sonce some historical data will be
		// From games before the tipp
		startJ = 0;
		startJSet = false;
	
		/* Itterate over Tipps */
		for(int i = startI; i < endI; i++){
			startJSet = false;
			
			BlogaBetElement tipp = blogaBetList.get(i);
			if(!tipp.getTypeOfBet().equals("Match Odds Half Time") && !tipp.getTypeOfBet().equals("Over Under Half Time") && !tipp.getTypeOfBet().equals("Asian Handicap Half Time")){
				continue;
			}
			if(!tipp.getSport().equals("SOC") && !tipp.getSport().equals("Livebet")){
				continue;
			}
			double pivot = tipp.getPivotValue();
			if(pivot == -10){
				continue;
			}
			if(tipp.getBestOdds() > 15){
				continue;
			}
			
			if(tipp.getTypeOfBet().equals("Match Odds Half Time")){
				checkedTippsMatchOdds++;
			}
			if(tipp.getTypeOfBet().equals("Asian Handicap Half Time")){
				checkedTippsAsianHandicap++;
			}
			if(tipp.getTypeOfBet().equals("Over Under Half Time")){
				checkedTippsOverUnder++;
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
			for(int j = startJ; j < historicalDataList_Half.size(); j++){
				
				HistoricalDataElement historicalDataElement = historicalDataList_Half.get(j);
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
					boolean teamsMatch = TeamMapping.teamsMatch(historicalDataHost, blogaBetHost) || TeamMapping.teamsMatch(historicalDataGuest, blogaBetGuest);
					if(!teamsMatch)
						teamsMatch =TeamMapping.teamsMatch(historicalDataHost, blogaBetHost, historicalDataGuest, blogaBetGuest);
					//System.out.println(betAdvisorHost + " , " + historicalDataHost);	
					if(teamsMatch){
						availableBets.add(historicalDataElement);
						
						if(tipp.getTypeOfBet().equals("Match Odds Half Time")){
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
						if(tipp.getTypeOfBet().equals("Over Under Half Time")){
							double total = tipp.getPivotValue();
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
									if(tippTeam.indexOf("Over") == 0){
										tippIndex = 0;
									}
									else if(tippTeam.indexOf("Under") == 0){
										tippIndex = 1;
									}
								}
							}
						}
						if(tipp.getTypeOfBet().equals("Asian Handicap Half Time")){
							pivot = tipp.getPivotValue();
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
				
				if(tippIndex == -1 && tipp.getTypeOfBet().equals("Match Odds Half Time")){
					System.out.println();
				}
				HistoricalDataElement historicalElement = availableBets.get(j);
				
				// ONE_TWO
				if(tipp.getTypeOfBet().equals("Match Odds Half Time")){
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
				if(tipp.getTypeOfBet().equals("Over Under Half Time")){
					List<TotalElement> totalOdds = historicalElement.getTotalList();

					double odds = 0;
					for(int oddIndex = 0; oddIndex < totalOdds.size(); oddIndex++){
						TotalElement totalElement = totalOdds.get(oddIndex);
						
						double total = tipp.getPivotValue();
						
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
				if(tipp.getTypeOfBet().equals("Asian Handicap Half Time")){
					List<HdpElement> hdpOdds = historicalElement.getHdpList();

					double odds = 0;
					for(int oddIndex = 0; oddIndex < hdpOdds.size(); oddIndex++){
						HdpElement totalElement = hdpOdds.get(oddIndex);
						pivot = tipp.getPivotValue();
						
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
				if(bestOdds > 0.90 * tipp.getBestOdds()){
					goodOddsFound++;
				}
				else{
					continue;
				}
				if(tipp.getBestOdds() > bestOdds * 1.5){
					System.out.println();
				}
				realOddsList.add(bestOdds);
				suggestedOddsList.add(tipp.getBestOdds());
				averageSuggestedOdds += tipp.getBestOdds();
				averageRealOdds += bestOdds;
				
				if(tipp.getTypeOfBet().equals("Match Odds Half Time")){
					averageSuggestedOddsMatchOdds += tipp.getBestOdds();
					averageRealOddsMatchOdds += bestOdds;
					oddsRatioMatchOdds += bestOdds / tipp.getBestOdds();
					oddsFoundMatchOdds++;
					realOddsListMatchOdds.add(bestOdds);
					suggestedOddsListMatchOdds.add(tipp.getBestOdds());
				}
				if(tipp.getTypeOfBet().equals("Asian Handicap Half Time")){
					averageSuggestedOddsAsianHandicap += tipp.getBestOdds();
					averageRealOddsAsianHandicap += bestOdds;
					oddsRatioAsianHandicap += bestOdds / tipp.getBestOdds();
					oddsFoundAsianHandicap++;
					realOddsListAsianHandicap.add(bestOdds);
					suggestedOddsListAsianHandicap.add(tipp.getBestOdds());
				}
				if(tipp.getTypeOfBet().equals("Over Under Half Time")){
					averageSuggestedOddsOverUnder += tipp.getBestOdds();
					averageRealOddsOverUnder += bestOdds;
					oddsRatioOverUnder += bestOdds / tipp.getBestOdds();
					oddsFoundOverUnder++;
					realOddsListOverUnder.add(bestOdds);
					suggestedOddsListOverUnder.add(tipp.getBestOdds());
				}
				
				double liquidity = 0;			
				Instance record = repTreeModel.createWekaInstance(bestSource, tipp, bestOdds);
				if(record != null){
					try {
						liquidity = repTreeModel.classifyInstance(record);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(liquidity != 0){
					numberOfLiquidityCalculations++;
					averageLiquidity += liquidity;
				}
				double take = 100;
				if(liquidity > 13000){
					System.out.println();
				}
				
				bets.add(tipp);
				records.add(bestSource);
				liquidities.add(liquidity);
				bestOddsList.add(bestOdds);
				
				bestOdds *= bestOddsFactor;
				oddsRatio += bestOdds / suggestedOdds;
				if(tipp.getTypeOfBet().equals("Match Odds Half Time")){
					take = 100;
					if(tipp.getResult().equalsIgnoreCase("LOST")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
						betEvs.add(-take);
						profit -= take;
					}
					else if(tipp.getResult().equalsIgnoreCase("WIN")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
						betEvs.add(take * bestOdds - take);
						profit += take * bestOdds - take;					
					}
					else{
						betLiquidities.add(liquidity);
						liquidityTipsters.add(tipp.getTipster());
						betEvsMaxLiquidity.add(0.0);
						betEvs.add(0.0);
					}
					if(bestOdds < suggestedOdds){
						//System.out.println("Suggested Odds: " +  suggestedOdds + " real Odds: " + bestOdds);
					}		
				}
				if(tipp.getTypeOfBet().equals("Over Under Half Time")){	
					if(tipp.getResult().equalsIgnoreCase("LOST")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
						betEvs.add(-take);
						profit -= take;
					}
					else if(tipp.getResult().equalsIgnoreCase("WIN")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
						betEvs.add(take * bestOdds - take);
						profit += take * bestOdds - take;					
					}
					else{
						betLiquidities.add(liquidity);
						liquidityTipsters.add(tipp.getTipster());
						betEvsMaxLiquidity.add(0.0);
						betEvs.add(0.0);
					}
					if(bestOdds < suggestedOdds){
//								System.out.println("Suggested Odds: " +  suggestedOdds + " real Odds: " + bestOdds);
					}						
				}
				if(tipp.getTypeOfBet().equals("Asian Handicap Half Time")){
					if(tipp.getResult().equalsIgnoreCase("LOST")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity -= liquidity;
							betEvsMaxLiquidity.add(-liquidity);
						}
						betEvs.add(-take);
						profit -= take;
					}
					else if(tipp.getResult().equalsIgnoreCase("WIN")){
						if(liquidity > 0){
							betLiquidities.add(liquidity);
							liquidityTipsters.add(tipp.getTipster());
							evAllPossibleBetsTakenMaxLiquidity += liquidity * bestOdds - liquidity;
							betEvsMaxLiquidity.add(liquidity * bestOdds - liquidity);
						}
						betEvs.add(take * bestOdds - take);
						profit += take * bestOdds - take;					
					}
					else{
						betLiquidities.add(liquidity);
						liquidityTipsters.add(tipp.getTipster());
						betEvsMaxLiquidity.add(0.0);
						betEvs.add(0.0);
					}
					if(bestOdds < suggestedOdds){
//								System.out.println("Suggested Odds: " +  suggestedOdds + " real Odds: " + bestOdds);
					}						
				}
			}			
		}
	
		averageYield /= oddsFound;
		averageYield = profit / oddsFound;

		System.out.println();
		System.out.println("Number of Tips: " + blogaBetList.size());
        System.out.println("Number of Valid Tips: " + checkedTipps);
		System.out.println("Number of Odds Found: " + oddsFound);
		System.out.println("Number of good Odds: " + goodOddsFound);
		System.out.println("Percentage of matched Bets: " + oddsFound * 100.0 / blogaBetList.size());
		System.out.println("Percentage of matched Supported Bets: " + oddsFound * 100.0 / checkedTipps);
		System.out.println("Percentage of matched Bets Match Odds: " + oddsFoundMatchOdds * 100.0 / checkedTippsMatchOdds);
		System.out.println("Percentage of matched Bets Asian Handicap: " + oddsFoundAsianHandicap * 100.0 / checkedTippsAsianHandicap);
		System.out.println("Percentage of matched Bets Over Under: " + oddsFoundOverUnder * 100.0 / checkedTippsOverUnder);
		System.out.println("Percentage of good Odds: " + goodOddsFound * 100.0 / oddsFound);
		System.out.println("Profit: " + profit);
		System.out.println("Yield: " + averageYield);
		
        // Odds
		averageSuggestedOdds /= oddsFound;
		averageRealOdds /= oddsFound;
		averageSuggestedOddsMatchOdds /= oddsFoundMatchOdds;
		averageRealOddsMatchOdds /= oddsFoundMatchOdds;
		averageSuggestedOddsAsianHandicap /= oddsFoundAsianHandicap;
		averageRealOddsAsianHandicap /= oddsFoundAsianHandicap;
		averageSuggestedOddsOverUnder /= oddsFoundOverUnder;
		averageRealOddsOverUnder /= oddsFoundOverUnder;
		System.out.println("Average suggested Odds: " + averageSuggestedOdds);
		System.out.println("Average real Odds: " + averageRealOdds);
		System.out.println("Average suggested Odds MatchOdds: " + averageSuggestedOddsMatchOdds);
		System.out.println("Average real Odds MatchOdds: " + averageRealOddsMatchOdds);
		System.out.println("Average suggested Odds Asian Handicap: " + averageSuggestedOddsAsianHandicap);
		System.out.println("Average real Odds Asian Handicap: " + averageRealOddsAsianHandicap);
		System.out.println("Average suggested Odds Over Under: " + averageSuggestedOddsOverUnder);
		System.out.println("Average real Odds Over Under: " + averageRealOddsOverUnder);
		
		oddsRatio /= oddsFound;
        oddsRatioMatchOdds /= oddsFoundMatchOdds;
        oddsRatioAsianHandicap /= oddsFoundAsianHandicap;
        oddsRatioOverUnder /= oddsFoundOverUnder;
        System.out.println();
		System.out.println("Odds Ratio: " + oddsRatio);
		System.out.println("Odds Ratio Match Odds: " + oddsRatioMatchOdds);
		System.out.println("Odds Ratio Asian Handicap: " + oddsRatioAsianHandicap);
		System.out.println("Odds Ratio Over Under: " + oddsRatioOverUnder);
		
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
		if(averageYield > 4){
			significance = test.tTest(4, bEv);
			System.out.println("Significance level of < 4: " + significance / 2);
		}
		if(averageYield > 5){
			significance = test.tTest(5, bEv);
			System.out.println("Significance level of < 5: " + significance / 2);
		}
		if(averageYield > 6){
			significance = test.tTest(6, bEv);
			System.out.println("Significance level of < 6: " + significance / 2);
		}
		if(averageYield > 7){
			significance = test.tTest(7, bEv);
			System.out.println("Significance level of < 7: " + significance / 2);
		}
		System.out.println();
		
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
        
		// Chart Odds
        final String series1 = "Real Odds";
        final String series2 = "Tipster Odds";
        
        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for(int i = 0; i < realOddsList.size(); i++){
            dataset.addValue(realOddsList.get(i), series1, i + "");
            dataset.addValue(suggestedOddsList.get(i), series2, i + "");
            
            if(i != 0 && i % 100 == 0){
                // create the chart...
                final JFreeChart chartOdds = ChartFactory.createBarChart(
                    "Odds Comparison",         	// chart title
                    "Tip",                    	// domain axis label
                    "Odds",                  	// range axis label
                    dataset,                  	// data
                    PlotOrientation.VERTICAL, 	// orientation
                    true,                     	// include legend
                    true,                     	// tooltips?
                    false                     	// URLs?
                );

                JFrame oddsChart = new JFrame();
                ChartPanel chartPanelOdds = new ChartPanel(chartOdds);
                oddsChart.setContentPane(chartPanelOdds);
                oddsChart.setVisible(true);	
                oddsChart.setSize(600, 400);
                dataset = new DefaultCategoryDataset();
            }
            if(i == realOddsList.size() - 1){
                final JFreeChart chartOdds = ChartFactory.createBarChart(
                        "Odds Comparison",         	// chart title
                        "Tip",                    	// domain axis label
                        "Odds",                  	// range axis label
                        dataset,                  	// data
                        PlotOrientation.VERTICAL, 	// orientation
                        true,                     	// include legend
                        true,                     	// tooltips?
                        false                     	// URLs?
                );

                JFrame oddsChart = new JFrame();
                ChartPanel chartPanelOdds = new ChartPanel(chartOdds);
                oddsChart.setContentPane(chartPanelOdds);
                oddsChart.setVisible(true);	          	
            }
        }
        
        dataset = new DefaultCategoryDataset();
        for(int i = 0; i < realOddsListMatchOdds.size(); i++){
            dataset.addValue(realOddsListMatchOdds.get(i), series1, i + "");
            dataset.addValue(suggestedOddsListMatchOdds.get(i), series2, i + "");
            
            if(i != 0 && i % 100 == 0){
                // create the chart...
                final JFreeChart chartOdds = ChartFactory.createBarChart(
                    "Odds Comparison Match Odds",         	// chart title
                    "Tip",                    		// domain axis label
                    "Odds",                  		// range axis label
                    dataset,                  		// data
                    PlotOrientation.VERTICAL, 		// orientation
                    true,                     		// include legend
                    true,                     		// tooltips?
                    false                     		// URLs?
                );

                JFrame oddsChart = new JFrame();
                ChartPanel chartPanelOdds = new ChartPanel(chartOdds);
                oddsChart.setContentPane(chartPanelOdds);
                oddsChart.setVisible(true);	
                oddsChart.setSize(600, 400);
                dataset = new DefaultCategoryDataset();
            }
        }
        // Save result lists
        FileOutputStream fileOutput = new FileOutputStream(BLOGABET_BACKTEST_PATH);
        BufferedOutputStream br = new BufferedOutputStream(fileOutput);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(bets);
        objectOutputStream.close();
        fileOutput = new FileOutputStream(BLOGABET_BACKTEST_RECORD_PATH);
        br = new BufferedOutputStream(fileOutput);
        objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(records);
        objectOutputStream.close();
        fileOutput = new FileOutputStream(BLOGABET_BACKTEST_LIQUIDITY_PATH);
        br = new BufferedOutputStream(fileOutput);
        objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(liquidities);
        objectOutputStream.close();
        fileOutput = new FileOutputStream(BLOGABET_BACKTEST_BESTODDS_PATH);
        br = new BufferedOutputStream(fileOutput);
        objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(bestOddsList);
        objectOutputStream.close();
        fileOutput = new FileOutputStream(BLOGABET_BACKTEST_TIP_PATH);
        br = new BufferedOutputStream(fileOutput);
        objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(blogaBetList);
        objectOutputStream.close();
	}
	
	public static void main(String[] args) throws IOException {
		BlogaBetBacktest backtest = new BlogaBetBacktest();
		backtest.runBacktest();
	}
}
