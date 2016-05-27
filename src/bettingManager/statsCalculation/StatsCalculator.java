package bettingManager.statsCalculation;

import historicalData.HdpElement;
import historicalData.HistoricalDataElement;
import historicalData.OneTwoElement;
import historicalData.TotalElement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.List;

import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import mailParsing.BetAdvisorTip;
import mailParsing.BlogaBetTip;
import backtest.BetAdvisorBacktest;
import backtest.BlogaBetBacktest;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import bettingBot.entities.Bet;
import bettingBot.entities.BetTicket;
import blogaBetHistoricalDataParsing.BlogaBetElement;
import blogaBetHistoricalDataParsing.BlogaBetParser;

import com.google.gson.Gson;

/**
 * 
 * This Class provides Methods for calculating Stats for each report
 * The for the Backtest analysis must be calculated in the Classes "BetAdvisorBacktest" and "BlogaBetBacktest"
 * 
 * The real betting results and received tips must be created in the Class "Datacreation"
 */
public class StatsCalculator {
	
	// Filters
	public boolean historical = true;
	public boolean real = true;
	public boolean betAdvisor = true;
	public boolean blogaBet = true;
	public boolean asianHandicap = true;
	public boolean overUnder = true;
	public boolean oneTwoResult = true;
	public boolean xResult = true;
	
	public double minLiquidity = 0;
	public double maxLiquidity = Double.MAX_VALUE;
	
	public Date startdate = new Date(0);
	public Date endDate = new Date(Long.MAX_VALUE);
	
	public double minOdds = 0;
	public double maxOdds = Double.MAX_VALUE;
	
	// Classes for Gson
	private Class recordClass;
	private Class eventClass;
	
	// Historical Tipster Data
	private List<BetAdvisorElement> betAdvisorList;
	private List<BlogaBetElement> blogaBetList;
	
	// Backtest Results
	private List<HistoricalDataElement> betAdvisorHistorical;
	private List<Double> betAdvisorBacktestLiquidity;
	private List<BetAdvisorElement> betAdvisorBacktestBets;
	
	private List<HistoricalDataElement> blogaBetHistorical;
	private List<Double> blogaBetBacktestLiquidity;
	private List<BlogaBetElement> blogaBetBacktestBets;
	
	// Real Bets
	private List<Bet> betAdvisorBets;
	private List<Bet> blogaBetBets;
	
	/**
	 * Creates the StatsCalculator and loads all data from the project folder
	 * 
	 * If any Exceptions are thrown, the software will exit
	 */
	
	public StatsCalculator(){
		File f0 = new File("event.dat");
		File f1 = new File("record.dat");
		if(f0.isFile() && f0.canRead() && f1.isFile() && f1.canRead()){
			try{
				FileInputStream in0 = new FileInputStream(f0);
				ObjectInputStream inO0 = new ObjectInputStream(in0);
				eventClass = (Class)inO0.readObject();
				
				FileInputStream in1 = new FileInputStream(f1);
				ObjectInputStream inO1 = new ObjectInputStream(in1);
				recordClass = (Class)inO1.readObject();
				inO0.close();
				inO1.close();
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
		}	
		else{
			System.out.println("Event or Record Class not found! Program will exit!");
			System.exit(-1);
		}
		// Read historical Tipster Data
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		try {
			betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		} catch (IOException e) {
			System.out.println("Error reading BetAdvisor Historical Tipster Data");
			e.printStackTrace();
			System.exit(-1);
		}
		BlogaBetParser parser = new BlogaBetParser();
		try {
			blogaBetList = parser.parseSheets("blogaBetTipsterData/csv");
		} catch (IOException e) {
			System.out.println("Error reading BlogaBet Historical Tipster Data");
			e.printStackTrace();
		}

		// Read backtest results
        FileInputStream fileInput;
		try {
			fileInput = new FileInputStream(BetAdvisorBacktest.BETADVISOR_BACKTEST_RECORD_PATH);
	        BufferedInputStream br = new BufferedInputStream(fileInput);
	        ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			betAdvisorHistorical = (List<HistoricalDataElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			fileInput = new FileInputStream(BetAdvisorBacktest.BETADVISOR_BACKTEST_LIQUIDITY_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			betAdvisorBacktestLiquidity = (List<Double>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			fileInput = new FileInputStream(BetAdvisorBacktest.BETADVISOR_BACKTEST_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			betAdvisorBacktestBets = (List<BetAdvisorElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
        try {
			fileInput = new FileInputStream(BlogaBetBacktest.BLOGABET_BACKTEST_RECORD_PATH);
	        BufferedInputStream br = new BufferedInputStream(fileInput);
	        ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			blogaBetHistorical = (List<HistoricalDataElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			fileInput = new FileInputStream(BlogaBetBacktest.BLOGABET_BACKTEST_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			blogaBetBacktestBets = (List<BlogaBetElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			fileInput = new FileInputStream(BlogaBetBacktest.BLOGABET_BACKTEST_LIQUIDITY_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			blogaBetBacktestLiquidity= (List<Double>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Read real bets
		try {
			fileInput = new FileInputStream(DataCreation.BETADVISOR_RESULT_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			betAdvisorBets = (List<Bet>)objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			fileInput = new FileInputStream(DataCreation.BLOGABET_RESULT_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			blogaBetBets = (List<Bet>)objectInputStream.readObject();	
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}			
	}
	
	public void getKoBStats(){
		
		Gson gson = new Gson();
		
		double averageYield = 0;
		double averageOdds = 0;
		double numberOfBets = 0;
		double percentWeGet = 0;
		double percentOver95 = 0;
		double averageLiquidity = 0;
		double percentOfTipsFound = 0;
		double flatStakeYield = 0;	
		
		// BetAdvisor Backtest
		if(betAdvisor && historical){
			for(int i = 0; i < betAdvisorBacktestBets.size(); i++){
				
				// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
				BetAdvisorElement element = betAdvisorBacktestBets.get(i);
				HistoricalDataElement historicalElement = betAdvisorHistorical.get(i);
				double liquidity = betAdvisorBacktestLiquidity.get(i);
				
				
				Date gameDate = element.getGameDate();
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity){
					numberOfBets++;
					averageYield += element.getProfit();	
					averageOdds += element.getOdds();
				}				
			}
		}
		
		// BlogaBet Backtest
		if(blogaBet && historical){
			for(int i = 0; i < blogaBetBacktestBets.size(); i++){
				
				// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
				BlogaBetElement element = blogaBetBacktestBets.get(i);
				HistoricalDataElement historicalElement = betAdvisorHistorical.get(i);
				double liquidity = betAdvisorBacktestLiquidity.get(i);
			}		
		}
		
		// Bet Advisor real results
		if(betAdvisor && real){
			for(int i = 0; i < betAdvisorBets.size(); i++){
				Bet bet = betAdvisorBets.get(i);
				
				// Some objects were saved to our SQL database as JSON string
				// We have to convert them to objects again
				Record record = (Record)gson.fromJson(bet.getRecordJsonString(), recordClass);
				SoccerEvent event = (SoccerEvent)gson.fromJson(bet.getEventJsonString(), eventClass);
				
				// The tip, its a different class than a Blogabet tip
				// Some variables also have difefrent names and possible values
				BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);
				
				BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);
				
				Date gameDate = tip.date;
				double liquidity = betTicket.getMaxStake();
				double tipOdds = tip.bestOdds;
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && tipOdds >= minOdds && tipOdds <= maxOdds){


				}	
			}
		}
		
		// BlogaBet real results
		if(blogaBet && real){
			for(int i = 0; i < blogaBetBets.size(); i++){
				Bet bet = blogaBetBets.get(i);
				
				// Some objects were saved to our SQL database as JSON string
				// We have to convert them to objects again
				Record record = (Record)gson.fromJson(bet.getRecordJsonString(), recordClass);
				SoccerEvent event = (SoccerEvent)gson.fromJson(bet.getEventJsonString(), eventClass);
				
				// The tip, its a different class than a betAdvisor tip
				// Some variables also have difefrent names and possible values
				// Conversion from String to double
				String tipJsonString = bet.getTipJsonString();
				int startStake = tipJsonString.indexOf("\"stake\"") + 9;
				int stakeEnd = tipJsonString.indexOf("\"", startStake);
				String stakeString = tipJsonString.substring(startStake, stakeEnd);
				int splitPoint = stakeString.indexOf("/");
				String a = stakeString.substring(0, splitPoint);
				String b = stakeString.substring(splitPoint + 1);
				double stake = Double.parseDouble(a) / Double.parseDouble(b);
				tipJsonString = tipJsonString.replace(stakeString, stake + "");
				
				BlogaBetTip tip = (BlogaBetTip)gson.fromJson(tipJsonString, BlogaBetTip.class);
				
				BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);
				
				Date gameDate = tip.startDate;
				double liquidity = betTicket.getMaxStake();
				double tipOdds = tip.odds;
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && tipOdds >= minOdds && tipOdds <= maxOdds){


				}
			}		
		}
	}
	
	public static void main(String[] args) {
		StatsCalculator calculator = new StatsCalculator();
	}
}
