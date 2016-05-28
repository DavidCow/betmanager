package bettingManager.statsCalculation;

import historicalData.HistoricalDataElement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import mailParsing.BetAdvisorTip;
import mailParsing.BlogaBetTip;
import backtest.BetAdvisorBacktest;
import backtest.BlogaBetBacktest;
import betadvisor.BetAdvisorElement;
import bettingBot.entities.Bet;
import bettingBot.entities.BetTicket;
import blogaBetHistoricalDataParsing.BlogaBetElement;

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
	public boolean real = false;
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
	
	// This Set should contain all the names of the tipsters, that are currently active
	// Tipsters not in the set will be ignored
	public Set<String> activeTipsters;
	
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
	private List<Double> betAdvisorBacktestBestOddsList;
	
	private List<HistoricalDataElement> blogaBetHistorical;
	private List<Double> blogaBetBacktestLiquidity;
	private List<BlogaBetElement> blogaBetBacktestBets;
	private List<Double> blogaBetBacktestBestOddsList;
	
	// Received Tips
	private List<BetAdvisorTip> betAdvisorTips;
	private List<BlogaBetTip> blogaBetTips;
	
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
		try {			
			FileInputStream fileInput = new FileInputStream(BetAdvisorBacktest.BETADVISOR_BACKTEST_TIP_PATH);
	        BufferedInputStream br = new BufferedInputStream(fileInput);
	        ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			betAdvisorList = (List<BetAdvisorElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			System.out.println("Error reading BetAdvisor Historical Tipster Data");
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			FileInputStream fileInput = new FileInputStream(BlogaBetBacktest.BLOGABET_BACKTEST_TIP_PATH);
	        BufferedInputStream br = new BufferedInputStream(fileInput);
	        ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			blogaBetList = (List<BlogaBetElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			System.out.println("Error reading BlogaBet Historical Tipster Data");
			e.printStackTrace();
			System.exit(-1);
		}

		// Read backtest results
		try {
			FileInputStream fileInput = new FileInputStream(BetAdvisorBacktest.BETADVISOR_BACKTEST_RECORD_PATH);
	        BufferedInputStream br = new BufferedInputStream(fileInput);
	        ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			betAdvisorHistorical = (List<HistoricalDataElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			FileInputStream fileInput = new FileInputStream(BetAdvisorBacktest.BETADVISOR_BACKTEST_LIQUIDITY_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			betAdvisorBacktestLiquidity = (List<Double>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			FileInputStream fileInput = new FileInputStream(BetAdvisorBacktest.BETADVISOR_BACKTEST_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			betAdvisorBacktestBets = (List<BetAdvisorElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			FileInputStream fileInput = new FileInputStream(BetAdvisorBacktest.BETADVISOR_BACKTEST_BESTODDS_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			betAdvisorBacktestBestOddsList = (List<Double>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
        try {
        	FileInputStream fileInput = new FileInputStream(BlogaBetBacktest.BLOGABET_BACKTEST_RECORD_PATH);
	        BufferedInputStream br = new BufferedInputStream(fileInput);
	        ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			blogaBetHistorical = (List<HistoricalDataElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			FileInputStream fileInput = new FileInputStream(BlogaBetBacktest.BLOGABET_BACKTEST_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			blogaBetBacktestBets = (List<BlogaBetElement>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			FileInputStream fileInput = new FileInputStream(BlogaBetBacktest.BLOGABET_BACKTEST_BESTODDS_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);	
			blogaBetBacktestBestOddsList = (List<Double>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			FileInputStream fileInput = new FileInputStream(BlogaBetBacktest.BLOGABET_BACKTEST_LIQUIDITY_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			blogaBetBacktestLiquidity= (List<Double>)objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Read received Tips
		try {
			FileInputStream fileInput = new FileInputStream(DataCreation.BETADVISOR_TIP_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			betAdvisorTips = (List<BetAdvisorTip>)objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			FileInputStream fileInput = new FileInputStream(DataCreation.BLOGABET_TIP_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			blogaBetTips = (List<BlogaBetTip>)objectInputStream.readObject();	
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}	
		
		// Read real bets
		try {
			FileInputStream fileInput = new FileInputStream(DataCreation.BETADVISOR_RESULT_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			betAdvisorBets = (List<Bet>)objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			FileInputStream fileInput = new FileInputStream(DataCreation.BLOGABET_RESULT_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			blogaBetBets = (List<Bet>)objectInputStream.readObject();	
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}			
	}
	
	/**
	 * This method returns the names of all tipsters found in the historical and real data with a suffix, representing their site
	 * The (BA) suffix means, that it's a BetAdvisor tipster, (BB) is a BlogaBet tipster
	 * @return
	 */
	public Set<String> getAllTipsters(){
		Set<String> result = new HashSet<String>();
		
		// BA historical
		for(int i = 0; i < betAdvisorBacktestBets.size(); i++){
			BetAdvisorElement element = betAdvisorBacktestBets.get(i);
			String tipster = element.getTipster();
			tipster += " (BA)";
			if(!result.contains(tipster)){
				result.add(tipster);
			}
		}
		// BB historical
		for(int i = 0; i < blogaBetBacktestBets.size(); i++){
			BlogaBetElement element = blogaBetBacktestBets.get(i);
			String tipster = element.getTipster();
			tipster += " (BB)";
			if(!result.contains(tipster)){
				result.add(tipster);
			}
		}
		// BA real
		for(int i = 0; i < betAdvisorTips.size(); i++){
			BetAdvisorTip tip = betAdvisorTips.get(i);
			String tipster = tip.tipster;
			tipster += " (BA)";
			if(!result.contains(tipster)){
				result.add(tipster);
			}
		}
		// BB real
		for(int i = 0; i < blogaBetTips.size(); i++){
			BlogaBetTip tip = blogaBetTips.get(i);
			String tipster = tip.tipster;
			tipster += " (BB)";
			if(!result.contains(tipster)){
				result.add(tipster);
			}
		}
		
		return result;
	}
	
	public List<StatsRow> getKoBStats(){
		
		Gson gson = new Gson();
		
		List<StatsRow> result = new ArrayList<StatsRow>();
		StatsRow oneTwoRow = new StatsRow();
		oneTwoRow.groupBy = "One Two Result";
		StatsRow xRow = new StatsRow();
		xRow.groupBy = "X result";
		StatsRow asianHandicapRow = new StatsRow();
		asianHandicapRow.groupBy = "Asian Handicap";
		StatsRow overUnderRow = new StatsRow();
		overUnderRow.groupBy = "Over Under";
		result.add(oneTwoRow);
		result.add(xRow);
		result.add(asianHandicapRow);
		result.add(overUnderRow);
		
		// BetAdvisor Backtest
		if(betAdvisor && historical){
			for(int i = 0; i < betAdvisorBacktestBets.size(); i++){
				
				// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
				BetAdvisorElement element = betAdvisorBacktestBets.get(i);
				HistoricalDataElement historicalElement = betAdvisorHistorical.get(i);
				double liquidity = betAdvisorBacktestLiquidity.get(i);
				double bestOdds = betAdvisorBacktestBestOddsList.get(i);
								
				Date gameDate = element.getGameDate();
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && bestOdds >= minOdds && bestOdds <= maxOdds){
					StatsRow row = null;
					String typeOfBet = element.getTypeOfBet();
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(element.getSelection().equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
							row = xRow;
						}
						else{
							if(!oneTwoResult)
								continue;
							row = oneTwoRow;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
						row = overUnderRow;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
						row = asianHandicapRow;
					}
					row.numberOfBets++;
					row.averageLiquidity += liquidity;
					row.averageOdds += element.getOdds();
					if(element.getProfit() > 0){
						row.averageYield += bestOdds * element.getTake() - element.getTake();
						row.flatStakeYield += bestOdds * 100 - 100;
						
					}
					if(element.getProfit() < 0){
						row.averageYield -= element.getTake();
						row.flatStakeYield -= 100;				
					}
					row.percentOfTipsFound++;
					if(bestOdds / element.getOdds() > 0.95){
						row.percentOver95++;
					}
					row.percentWeGet += bestOdds / element.getOdds();
				}				
			}
		}
		
		// BlogaBet Backtest
		if(blogaBet && historical){
			for(int i = 0; i < blogaBetBacktestBets.size(); i++){
				
				// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
				BlogaBetElement element = blogaBetBacktestBets.get(i);
				HistoricalDataElement historicalElement = blogaBetHistorical.get(i);
				double liquidity = blogaBetBacktestLiquidity.get(i);
				double bestOdds = blogaBetBacktestBestOddsList.get(i);
				
				Date gameDate = element.getGameDate();
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && bestOdds >= minOdds && bestOdds <= maxOdds){
					StatsRow row = null;
					String typeOfBet = element.getTypeOfBet();
					typeOfBet = typeOfBet.replace(" Half Time", "");
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(element.getSelection().equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
							row = xRow;
						}
						else{
							if(!oneTwoResult)
								continue;
							row = oneTwoRow;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over Under")){
						if(!overUnder)
							continue;
						row = overUnderRow;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
						row = asianHandicapRow;
					}
					row.numberOfBets++;
					row.averageLiquidity += liquidity;
					row.averageOdds += element.getBestOdds();
					if(element.getResult().equalsIgnoreCase("WIN")){
						row.averageYield += bestOdds * element.getStake() * 100 - element.getStake() * 100;
						row.flatStakeYield += bestOdds * 100 - 100;
						
					}
					if(element.getResult().equalsIgnoreCase("LOST")){
						row.averageYield -= element.getStake() * 100;
						row.flatStakeYield -= 100;				
					}
					row.percentOfTipsFound++;
					if(bestOdds / element.getBestOdds() > 0.95){
						row.percentOver95++;
					}
					row.percentWeGet += bestOdds / element.getBestOdds();
				}				
			}		
		}
		
		// Bet Advisor real results
		if(betAdvisor && real){
			// Get tips
			for(int i = 0; i < betAdvisorTips.size(); i++){
				BetAdvisorTip tip = betAdvisorTips.get(i);
				Date gameDate = tip.date;
				
			}
			
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
		
		// Compute averages
		for(int i = 0; i < result.size(); i++){
			StatsRow row = result.get(i);
			row.averageLiquidity /= row.numberOfBets;
			row.averageOdds /= row.numberOfBets;
			row.averageYield /= row.numberOfBets;
			row.flatStakeYield /= row.numberOfBets;
			row.percentOver95 /= row.numberOfBets;
			row.percentWeGet /= row.numberOfBets;
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		StatsCalculator calculator = new StatsCalculator();
		Set<String> res = calculator.getAllTipsters();
		System.out.println(res);
		List<StatsRow> row = calculator.getKoBStats();
		System.out.println();
	}
}
