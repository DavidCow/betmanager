package bettingManager.statsCalculation;

import historicalData.HistoricalDataElement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;
import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import mailParsing.BetAdvisorTip;
import mailParsing.BlogaBetTip;
import moneyManagement.StakeCalculation;
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
	private static Gson gson = new Gson();
	
	// Filters
	public boolean historical = false;
	public boolean real = true;
	public boolean betAdvisor = false;
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
	
	public double minOddsOfTheTip = 0;
	public double maxOddsOfTheTip = Double.MAX_VALUE;
	
	// This Set should contain all the names of the tipsters, that are currently active
	// Tipsters not in this set, or in the alias set, will be ignored
	public Map<String, Boolean> activeTipsters;
	// The alias List
	public List<Alias> aliasList;
	
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
			for(int i = 0; i < betAdvisorBets.size(); i++){
				Bet bet = betAdvisorBets.get(i);
				BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);		
				Date date = tip.date;
				if(date == null){
					betAdvisorBets.remove(i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			FileInputStream fileInput = new FileInputStream(DataCreation.BLOGABET_RESULT_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			blogaBetBets = (List<Bet>)objectInputStream.readObject();	
			for(int i = 0; i < blogaBetBets.size(); i++){
				Bet bet = blogaBetBets.get(i);
				// Conversion from String to double
				String tipJsonString = bet.getTipJsonString();
				int startStake = tipJsonString.indexOf("\"stake\"") + 9;
				int stakeEnd = tipJsonString.indexOf("\"", startStake);
				String stakeString = tipJsonString.substring(startStake, stakeEnd);
				int splitPoint = stakeString.indexOf("/");
				if(splitPoint != -1){
					String a = stakeString.substring(0, splitPoint);
					String b = stakeString.substring(splitPoint + 1);
					double stake = Double.parseDouble(a) / Double.parseDouble(b);
					tipJsonString = tipJsonString.replace(stakeString, stake + "");
					bet.setTipJsonString(tipJsonString);
				}
			}
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

	public List<StatsRow> getMonthlyStats(){
		int rowIndex = 0;
		Map<String, Integer> rowMap = new HashMap<String, Integer>();
		List<StatsRow> rows = new ArrayList<StatsRow>();
		
		int[] mapping = new int[betAdvisorBacktestBets.size() + blogaBetBacktestBets.size() + betAdvisorBets.size() + blogaBetBets.size()];
		int mapIndex = 0;
		for (int i = 0; i < betAdvisorBacktestBets.size(); i++) {
			BetAdvisorElement element = betAdvisorBacktestBets.get(i);
			Date gameDate = element.getGameDate();
			int month = gameDate.getMonth();
			int year = gameDate.getYear() + 1900;
			String dateString = month + " " + year;
			
			if(betAdvisor && historical){
				if(!rowMap.keySet().contains(dateString)){
					rowMap.put(dateString, rowIndex);
					StatsRow row = new StatsRow();
					row.groupBy = dateString;
					rows.add(row);
					rowIndex++;
				}	
				mapping[mapIndex] = rowMap.get(dateString);	
			}
			mapIndex++;
		}
		// BlogaBet Backtest
		for (int i = 0; i < blogaBetBacktestBets.size(); i++) {
			BlogaBetElement element = blogaBetBacktestBets.get(i);
			Date gameDate = element.getGameDate();
			int month = gameDate.getMonth();
			int year = gameDate.getYear() + 1900;
			String dateString = month + " " + year;
			
			if(blogaBet && historical){
				if(!rowMap.keySet().contains(dateString)){
					rowMap.put(dateString, rowIndex);
					StatsRow row = new StatsRow();
					row.groupBy = dateString;
					rows.add(row);
					rowIndex++;		
				}
				mapping[mapIndex] = rowMap.get(dateString);
			}
			mapIndex++;
		}

		// Bet Advisor real results
		for (int i = 0; i < betAdvisorBets.size(); i++) {
			Bet bet = betAdvisorBets.get(i);
			BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);	
			Date gameDate = tip.date;
			int month = gameDate.getMonth();
			int year = gameDate.getYear() + 1900;
			String dateString = month + " " + year;
			
			if(betAdvisor && real){
				if(!rowMap.keySet().contains(dateString)){
					rowMap.put(dateString, rowIndex);
					StatsRow row = new StatsRow();
					row.groupBy = dateString;
					rows.add(row);
					rowIndex++;		
				}
				mapping[mapIndex] = rowMap.get(dateString);
			}
			mapIndex++;
		}
		// BlogaBet real results
		for (int i = 0; i < blogaBetBets.size(); i++) {
			Bet bet = blogaBetBets.get(i);		
			BlogaBetTip tip = (BlogaBetTip)gson.fromJson(bet.getTipJsonString(), BlogaBetTip.class);	
			Date gameDate = tip.startDate;
			int month = gameDate.getMonth();
			int year = gameDate.getYear() + 1900;
			String dateString = month + " " + year;
			
			if(blogaBet && real){
				if(!rowMap.keySet().contains(dateString)){
					rowMap.put(dateString, rowIndex);
					StatsRow row = new StatsRow();
					row.groupBy = dateString;
					rows.add(row);
					rowIndex++;		
				}
				mapping[mapIndex] = rowMap.get(dateString);
			}
			mapIndex++;
		}	
		calculateStats(rows, mapping);
		
		int[] tipMapping = new int[betAdvisorList.size() + blogaBetList.size() + betAdvisorTips.size() + blogaBetTips.size()];
		int tipMapIndex = 0;
		// BA Historical
		for(int i = 0; i < betAdvisorList.size(); i++){
			BetAdvisorElement element = betAdvisorList.get(i);
			int index = -1;
			Date gameDate = element.getGameDate();
			int month = gameDate.getMonth();
			int year = gameDate.getYear() + 1900;
			String dateString = month + " " + year;
			
			if(rowMap.keySet().contains(dateString)){
				index = rowMap.get(dateString);
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BB Historical
		for(int i = 0; i < blogaBetList.size(); i++){
			
			BlogaBetElement element = blogaBetList.get(i);
			int index = -1;
			Date gameDate = element.getGameDate();
			int month = gameDate.getMonth();
			int year = gameDate.getYear() + 1900;
			String dateString = month + " " + year;
			
			if(rowMap.keySet().contains(dateString)){
				index = rowMap.get(dateString);
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BA Real
		for(int i = 0; i < betAdvisorTips.size(); i++){
			BetAdvisorTip tip = betAdvisorTips.get(i);
			Date gameDate = tip.date;
			int month = gameDate.getMonth();
			int year = gameDate.getYear() + 1900;
			String dateString = month + " " + year;
			
			int index = -1;
			if(rowMap.keySet().contains(dateString)){
				index = rowMap.get(dateString);
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BB real
		for(int i = 0; i < blogaBetTips.size(); i++){
			BlogaBetTip tip = blogaBetTips.get(i);
			Date gameDate = tip.startDate;
			int month = gameDate.getMonth();
			int year = gameDate.getYear() + 1900;
			String dateString = month + " " + year;
			
			int index = -1;
			if(rowMap.keySet().contains(dateString)){
				index = rowMap.get(dateString);
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}	
		calculateTipStats(rows, tipMapping);
		
		Comparator<StatsRow> c = new Comparator<StatsRow>() {

			@Override
			public int compare(StatsRow o1, StatsRow o2) {
				try{				
					String s0 = o1.groupBy;
					String s1 = o2.groupBy;
					
					if(s0.equals("Average")){
						return 1;
					}
					if(s1.equals("Average")){
						return -1;
					}
					
					String[] ss0 = s0.split(" ");
					int d0 = Integer.parseInt(ss0[1]) * 13 + Integer.parseInt(ss0[0]);
					String[] ss1 = s1.split(" ");
					int d1 = Integer.parseInt(ss1[1]) * 13 + Integer.parseInt(ss1[0]);
					
					if(d0 > d1)
						return 1;
					else if(d1 > d0){
						return -1;
					}
				}catch(Exception e){
					return 0;
				}
				return 0;
			}
		};
		
		Collections.sort(rows, c);
		return rows;			
	}
	
	public List<StatsRow> getTipsterStats(){
		int rowIndex = 0;
		Map<String, Integer> rowMap = new HashMap<String, Integer>();
		List<StatsRow> rows = new ArrayList<StatsRow>();
		
		int[] mapping = new int[betAdvisorBacktestBets.size() + blogaBetBacktestBets.size() + betAdvisorBets.size() + blogaBetBets.size()];
		int mapIndex = 0;
		for (int i = 0; i < betAdvisorBacktestBets.size(); i++) {
			BetAdvisorElement element = betAdvisorBacktestBets.get(i);
			String siteTipster = element.getTipster() + " (BA)";
			boolean tipsterContained = false;
			if(activeTipsters != null){
				if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
					tipsterContained = true;
				}
				for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
					if(aliasList.get(a).isSelected()){
						for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
							String aliasTipster = aliasList.get(a).tipsters.get(al);
							if(aliasTipster.equals(siteTipster)){
								tipsterContained = true;
								break;
							}
						}
					}
				}
			}
			if(!tipsterContained)
				continue;
			if(betAdvisor && historical){
				if(!rowMap.keySet().contains(siteTipster)){
					rowMap.put(siteTipster, rowIndex);
					StatsRow row = new StatsRow();
					row.groupBy = siteTipster;
					rows.add(row);
					rowIndex++;
				}	
				mapping[mapIndex] = rowMap.get(siteTipster);	
			}
			mapIndex++;
		}
		// BlogaBet Backtest
		for (int i = 0; i < blogaBetBacktestBets.size(); i++) {
			BlogaBetElement element = blogaBetBacktestBets.get(i);
			String siteTipster = element.getTipster() + " (BB)";
			boolean tipsterContained = false;
			if(activeTipsters != null){
				if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
					tipsterContained = true;
				}
				for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
					if(aliasList.get(a).isSelected()){
						for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
							String aliasTipster = aliasList.get(a).tipsters.get(al);
							if(aliasTipster.equals(siteTipster)){
								tipsterContained = true;
								break;
							}
						}
					}
				}
			}
			if(!tipsterContained)
				continue;
			if(blogaBet && historical){
				if(!rowMap.keySet().contains(siteTipster)){
					rowMap.put(siteTipster, rowIndex);
					StatsRow row = new StatsRow();
					row.groupBy = siteTipster;
					rows.add(row);
					rowIndex++;		
				}
				mapping[mapIndex] = rowMap.get(siteTipster);
			}
			mapIndex++;
		}

		// Bet Advisor real results
		for (int i = 0; i < betAdvisorBets.size(); i++) {
			Bet bet = betAdvisorBets.get(i);
			BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);	
			String siteTipster = tip.tipster + " (BA)";
			boolean tipsterContained = false;
			if(activeTipsters != null){
				if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
					tipsterContained = true;
				}
				for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
					if(aliasList.get(a).isSelected()){
						for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
							String aliasTipster = aliasList.get(a).tipsters.get(al);
							if(aliasTipster.equals(siteTipster)){
								tipsterContained = true;
								break;
							}
						}
					}
				}
			}
			if(!tipsterContained)
				continue;
			if(betAdvisor && real){
				if(!rowMap.keySet().contains(siteTipster)){
					rowMap.put(siteTipster, rowIndex);
					StatsRow row = new StatsRow();
					row.groupBy = siteTipster;
					rows.add(row);
					rowIndex++;		
				}
				mapping[mapIndex] = rowMap.get(siteTipster);
			}
			mapIndex++;
		}
		// BlogaBet real results
		for (int i = 0; i < blogaBetBets.size(); i++) {
			Bet bet = blogaBetBets.get(i);		
			BlogaBetTip tip = (BlogaBetTip)gson.fromJson(bet.getTipJsonString(), BlogaBetTip.class);	
			String siteTipster = tip.tipster + " (BB)";
			boolean tipsterContained = false;
			if(activeTipsters != null){
				if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
					tipsterContained = true;
				}
				for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
					if(aliasList.get(a).isSelected()){
						for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
							String aliasTipster = aliasList.get(a).tipsters.get(al);
							if(aliasTipster.equals(siteTipster)){
								tipsterContained = true;
								break;
							}
						}
					}
				}
			}
			if(!tipsterContained)
				continue;
			if(blogaBet && real){
				if(!rowMap.keySet().contains(siteTipster)){
					rowMap.put(siteTipster, rowIndex);
					StatsRow row = new StatsRow();
					row.groupBy = siteTipster;
					rows.add(row);
					rowIndex++;		
				}
				mapping[mapIndex] = rowMap.get(siteTipster);
			}
			mapIndex++;
		}	
		calculateStats(rows, mapping);
		
		int[] tipMapping = new int[betAdvisorList.size() + blogaBetList.size() + betAdvisorTips.size() + blogaBetTips.size()];
		int tipMapIndex = 0;
		// BA Historical
		for(int i = 0; i < betAdvisorList.size(); i++){
			BetAdvisorElement element = betAdvisorList.get(i);
			String siteTipster = element.getTipster() + " (BB)";
			int index = -1;
			if(rowMap.containsKey(siteTipster)){
				index = rowMap.get(siteTipster);
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BB Historical
		for(int i = 0; i < blogaBetList.size(); i++){
			
			BlogaBetElement element = blogaBetList.get(i);
			String siteTipster = element.getTipster() + " (BB)";
			int index = -1;
			if(rowMap.containsKey(siteTipster)){
				index = rowMap.get(siteTipster);
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BA Real
		for(int i = 0; i < betAdvisorTips.size(); i++){
			BetAdvisorTip tip = betAdvisorTips.get(i);
			String siteTipster = tip.tipster + " (BA)";
			int index = -1;
			if(rowMap.containsKey(siteTipster)){
				index = rowMap.get(siteTipster);
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BB real
		for(int i = 0; i < blogaBetTips.size(); i++){
			BlogaBetTip tip = blogaBetTips.get(i);
			String siteTipster = tip.tipster + " (BB)";
			int index = -1;
			if(rowMap.containsKey(siteTipster)){
				index = rowMap.get(siteTipster);
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}	
		calculateTipStats(rows, tipMapping);
		
		return rows;		
	}
	
	public List<StatsRow> getLiquidityStats(){
		List<StatsRow> rows = new ArrayList<StatsRow>();
		for(int i = 0; i < 10; i++){
			StatsRow row = new StatsRow();
			row.groupBy = "" + i;
			rows.add(row);
		}
		double minLiquiditySeen = Double.MAX_VALUE;
		double maxLiquiditySeen = Double.MIN_VALUE;
		
		// BetAdvisor Backtest
		if (betAdvisor && historical) {
			for (int i = 0; i < betAdvisorBacktestBets.size(); i++) {
				BetAdvisorElement element = betAdvisorBacktestBets.get(i);
				double liquidity = betAdvisorBacktestLiquidity.get(i);
				double bestOdds = betAdvisorBacktestBestOddsList.get(i);

				String siteTipster = element.getTipster() + " (BA)";
				if (activeTipsters != null) {
					if (!activeTipsters.containsKey(siteTipster) || !activeTipsters.get(siteTipster)) {
						continue;
					}
				}

				Date gameDate = element.getGameDate();
				if (gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && bestOdds >= minOdds && bestOdds <= maxOdds) {
					if(liquidity < minLiquiditySeen){
						minLiquiditySeen = liquidity;
					}
					if(liquidity > maxLiquiditySeen){
						maxLiquiditySeen = liquidity;
					}
				}
			}
		}

		// BlogaBet Backtest
		if (blogaBet && historical) {
			for (int i = 0; i < blogaBetBacktestBets.size(); i++) {
				
				BlogaBetElement element = blogaBetBacktestBets.get(i);
				double liquidity = blogaBetBacktestLiquidity.get(i);
				double bestOdds = blogaBetBacktestBestOddsList.get(i);

				String siteTipster = element.getTipster() + " (BB)";
				if (activeTipsters != null) {
					if (!activeTipsters.containsKey(siteTipster) || !activeTipsters.get(siteTipster)) {
						continue;
					}
				}

				Date gameDate = element.getGameDate();
				if (gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && bestOdds >= minOdds && bestOdds <= maxOdds) {
					if(liquidity < minLiquiditySeen){
						minLiquiditySeen = liquidity;
					}
					if(liquidity > maxLiquiditySeen){
						maxLiquiditySeen = liquidity;
					}
				}
			}
		}

		// Bet Advisor real results
		if (betAdvisor && real) {
			for (int i = 0; i < betAdvisorBets.size(); i++) {
				Bet bet = betAdvisorBets.get(i);

				BetAdvisorTip tip = (BetAdvisorTip) gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);
				BetTicket betTicket = (BetTicket) gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);

				String siteTipster = tip.tipster + " (BA)";
				if (activeTipsters != null) {
					if (!activeTipsters.containsKey(siteTipster) || !activeTipsters.get(siteTipster)) {
						continue;
					}
				}

				if (tip.betOn == null)
					continue;
				Date gameDate = tip.date;
				double liquidity = betTicket.getMaxStake();
				double tipOdds = tip.bestOdds;
				if (gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && tipOdds >= minOdds && tipOdds <= maxOdds) {
					if(liquidity < minLiquiditySeen){
						minLiquiditySeen = liquidity;
					}
					if(liquidity > maxLiquiditySeen){
						maxLiquiditySeen = liquidity;
					}
				}
			}
		}

		// BlogaBet real results
		if (blogaBet && real) {
			for (int i = 0; i < blogaBetBets.size(); i++) {
				Bet bet = blogaBetBets.get(i);

				BlogaBetTip tip = (BlogaBetTip) gson.fromJson(bet.getTipJsonString(),BlogaBetTip.class);

				BetTicket betTicket = (BetTicket) gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);

				String siteTipster = tip.tipster + " (BB)";
				if (activeTipsters != null) {
					if (!activeTipsters.containsKey(siteTipster) || !activeTipsters.get(siteTipster)) {
						continue;
					}
				}

				Date gameDate = tip.startDate;
				double liquidity = betTicket.getMaxStake();
				double tipOdds = tip.odds;
				if (gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && tipOdds >= minOdds && tipOdds <= maxOdds) {
					if(liquidity < minLiquiditySeen){
						minLiquiditySeen = liquidity;
					}
					if(liquidity > maxLiquiditySeen){
						maxLiquiditySeen = liquidity;
					}
				}
			}
		}
		double stakeLevel = (maxLiquiditySeen - minLiquiditySeen) / rows.size();	
		int[] mapping = new int[betAdvisorBacktestBets.size() + blogaBetBacktestBets.size() + betAdvisorBets.size() + blogaBetBets.size()];
		int mapIndex = 0;
		for (int i = 0; i < betAdvisorBacktestBets.size(); i++) {
			double liquidity = betAdvisorBacktestLiquidity.get(i);
			int index = -1;
			for(int j = 0; j < rows.size(); j++){
				if(liquidity > minLiquiditySeen + (j + 1) * stakeLevel + 0.01)
					continue;
				index = j;
				break;
			}
			mapping[mapIndex] = index;
			mapIndex++;
		}
		// BlogaBet Backtest
		for (int i = 0; i < blogaBetBacktestBets.size(); i++) {
			double liquidity = blogaBetBacktestLiquidity.get(i);
			int index = -1;
			for(int j = 0; j < rows.size(); j++){
				if(liquidity > minLiquiditySeen + (j + 1) * stakeLevel + 0.01)
					continue;
				index = j;
				break;
			}
			mapping[mapIndex] = index;
			mapIndex++;
		}

		// Bet Advisor real results
		for (int i = 0; i < betAdvisorBets.size(); i++) {
			
			Bet bet = betAdvisorBets.get(i);
			BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);	
			double liquidity = betTicket.getMaxStake();
			int index = -1;
			for(int j = 0; j < rows.size(); j++){
				if(liquidity > minLiquiditySeen + (j + 1) * stakeLevel + 0.01)
					continue;
				index = j;
				break;
			}
			mapping[mapIndex] = index;
			mapIndex++;
		}
		// BlogaBet real results
		for (int i = 0; i < blogaBetBets.size(); i++) {
			Bet bet = blogaBetBets.get(i);
			BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);	
			double liquidity = betTicket.getMaxStake();
			int index = -1;
			for(int j = 0; j < rows.size(); j++){
				if(liquidity > minLiquiditySeen + (j + 1) * stakeLevel + 0.01)
					continue;
				index = j;
				break;
			}
			mapping[mapIndex] = index;
			mapIndex++;
		}	
		calculateStats(rows, mapping);	
		return rows;
	}
	
	public List<StatsRow> getKoBStats(){
		List<StatsRow> rows = new ArrayList<StatsRow>();
		StatsRow r0 = new StatsRow();
		r0.groupBy = "One Two Result";
		rows.add(r0);
		StatsRow r1 = new StatsRow();
		r1.groupBy = "X result";
		rows.add(r1);
		StatsRow r2 = new StatsRow();
		r2.groupBy = "Asian Handicap";
		rows.add(r2);
		StatsRow r3 = new StatsRow();
		r3.groupBy = "Over Under";
		rows.add(r3);
		
		int[] mapping = new int[betAdvisorBacktestBets.size() + blogaBetBacktestBets.size() + betAdvisorBets.size() + blogaBetBets.size()];
		int mapIndex = 0;
		for (int i = 0; i < betAdvisorBacktestBets.size(); i++) {
			BetAdvisorElement element = betAdvisorBacktestBets.get(i);
			
			String typeOfBet = element.getTypeOfBet();
			typeOfBet = typeOfBet.replace(" 1st Half", "");
			typeOfBet = typeOfBet.replace(" Team", "");
			
			int index = -1;
			if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
				if(element.getSelection().equalsIgnoreCase("DRAW")){
					index = 1;
				}
				else{
					index = 0;
				}
			}
			else if(typeOfBet.equalsIgnoreCase("Over / Under")){
				index = 3;
			}
			else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
				index = 2;
			}
			mapping[mapIndex] = index;
			mapIndex++;
		}
		// BlogaBet Backtest
		for (int i = 0; i < blogaBetBacktestBets.size(); i++) {
			BlogaBetElement element = blogaBetBacktestBets.get(i);
			String typeOfBet = element.getTypeOfBet();
			typeOfBet = typeOfBet.replace(" Team", "");
			typeOfBet = typeOfBet.replace(" 1st Half", "");
			typeOfBet = typeOfBet.replace(" Corners", "");
			typeOfBet = typeOfBet.replace(" Alternative", "");
			typeOfBet = typeOfBet.replace(" Half Time", "");

			int index = -1;
			if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
				if(element.getSelection().equalsIgnoreCase("DRAW")){
					index = 1;
				}
				else{
					index = 0;
				}
			}
			else if(typeOfBet.equalsIgnoreCase("Over / Under")){
				index = 3;
			}
			else if(typeOfBet.equalsIgnoreCase("Over Under")){
				index = 3;
			}
			else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
				index = 2;
			}
			mapping[mapIndex] = index;
			mapIndex++;
		}

		// Bet Advisor real results
		for (int i = 0; i < betAdvisorBets.size(); i++) {
			Bet bet = betAdvisorBets.get(i);
			BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);	
			String typeOfBet = tip.typeOfBet;
			typeOfBet = typeOfBet.replace(" Team", "");
			typeOfBet = typeOfBet.replace(" 1st Half", "");
			
			int index = -1;
			if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
				if(tip.betOn.equalsIgnoreCase("DRAW")){
					index = 1;
				}
				else{
					index = 0;
				}
			}
			else if(typeOfBet.equalsIgnoreCase("Over / Under")){
				index = 3;
			}
			else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
				index = 2;
			}
			mapping[mapIndex] = index;
			mapIndex++;
		}
		// BlogaBet real results
		for (int i = 0; i < blogaBetBets.size(); i++) {
			Bet bet = blogaBetBets.get(i);		
			BlogaBetTip tip = (BlogaBetTip)gson.fromJson(bet.getTipJsonString(), BlogaBetTip.class);	
			String typeOfBet = tip.pivotType;
			typeOfBet = typeOfBet.replace(" Team", "");
			typeOfBet = typeOfBet.replace(" 1st Half", "");
			typeOfBet = typeOfBet.replace(" Corners", "");
			typeOfBet = typeOfBet.replace(" Alternative", "");

			int index = -1;
			if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
				if(tip.selection.equalsIgnoreCase("DRAW")){
					index = 1;
				}
				else{
					index = 0;
				}
			}
			else if(typeOfBet.equalsIgnoreCase("Over / Under")){
				index = 3;
			}
			else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
				index = 2;
			}
			mapping[mapIndex] = index;
			mapIndex++;
		}		
		calculateStats(rows, mapping);
		
		int[] tipMapping = new int[betAdvisorList.size() + blogaBetList.size() + betAdvisorTips.size() + blogaBetTips.size()];
		int tipMapIndex = 0;
		// BA Historical
		for(int i = 0; i < betAdvisorList.size(); i++){
			BetAdvisorElement element = betAdvisorList.get(i);
			int index = -1;

			String typeOfBet = element.getTypeOfBet();
			typeOfBet = typeOfBet.replace(" Team", "");
			typeOfBet = typeOfBet.replace(" 1st Half", "");
			if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
				if(element.getSelection().equalsIgnoreCase("DRAW")){
					index = 1;
				}
				else{
					index = 0;
				}
			}
			else if(typeOfBet.equalsIgnoreCase("Over / Under")){
				index = 3;
			}
			else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
				index = 2;
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BB Historical
		for(int i = 0; i < blogaBetList.size(); i++){
			
			BlogaBetElement element = blogaBetList.get(i);
			String typeOfBet = element.getTypeOfBet();
			typeOfBet = typeOfBet.replace(" Team", "");
			typeOfBet = typeOfBet.replace(" 1st Half", "");
			typeOfBet = typeOfBet.replace(" Corners", "");
			typeOfBet = typeOfBet.replace(" Alternative", "");

			int index = -1;
			if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
				if(element.getSelection().equalsIgnoreCase("DRAW")){
					index = 1;
				}
				else{
					index = 0;
				}
			}
			else if(typeOfBet.equalsIgnoreCase("Over / Under")){
				index = 3;
			}
			else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
				index = 2;
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BA Real
		for(int i = 0; i < betAdvisorTips.size(); i++){
			BetAdvisorTip tip = betAdvisorTips.get(i);
			String typeOfBet = tip.typeOfBet;
			typeOfBet = typeOfBet.replace(" Team", "");
			typeOfBet = typeOfBet.replace(" 1st Half", "");
			
			int index = -1;
			if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
				if(tip.betOn.equalsIgnoreCase("DRAW")){
					index = 1;
				}
				else{
					index = 0;
				}
			}
			else if(typeOfBet.equalsIgnoreCase("Over / Under")){
				index = 3;
			}
			else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
				index = 2;
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BB real
		for(int i = 0; i < blogaBetTips.size(); i++){
			BlogaBetTip tip = blogaBetTips.get(i);
			String typeOfBet = tip.pivotType;
			typeOfBet = typeOfBet.replace(" Team", "");
			typeOfBet = typeOfBet.replace(" 1st Half", "");
			typeOfBet = typeOfBet.replace(" Corners", "");
			typeOfBet = typeOfBet.replace(" Alternative", "");

			int index = -1;
			if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
				if(tip.selection.equalsIgnoreCase("DRAW")){
					index = 1;
				}
				else{
					index = 0;
				}
			}
			else if(typeOfBet.equalsIgnoreCase("Over / Under")){
				index = 3;
			}
			else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
				index = 2;
			}
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}	
		calculateTipStats(rows, tipMapping);
		
		return rows;
	}

	public List<StatsRow> getWeekStats(){
		
		List<StatsRow> rows = new ArrayList<StatsRow>();
		StatsRow r0 = new StatsRow();
		r0.groupBy = "Monday";
		rows.add(r0);
		StatsRow r1 = new StatsRow();
		r1.groupBy = "Tuesday";
		rows.add(r1);
		StatsRow r2 = new StatsRow();
		r2.groupBy = "Wednesday";
		rows.add(r2);
		StatsRow r3 = new StatsRow();
		r3.groupBy = "Thursday";
		rows.add(r3);
		StatsRow r4 = new StatsRow();
		r4.groupBy = "Friday";
		rows.add(r4);
		StatsRow r5 = new StatsRow();
		r5.groupBy = "Saturday";
		rows.add(r5);
		StatsRow r6 = new StatsRow();
		r6.groupBy = "Sunday";
		rows.add(r6);
		
		int[] mapping = new int[betAdvisorBacktestBets.size() + blogaBetBacktestBets.size() + betAdvisorBets.size() + blogaBetBets.size()];
		int mapIndex = 0;
		for (int i = 0; i < betAdvisorBacktestBets.size(); i++) {
			mapping[mapIndex] = (betAdvisorBacktestBets.get(i).getGameDate().getDay() + 6) % 7;
			mapIndex++;
		}
		// BlogaBet Backtest
		for (int i = 0; i < blogaBetBacktestBets.size(); i++) {
			mapping[mapIndex] = (blogaBetBacktestBets.get(i).getGameDate().getDay() + 6) % 7;
			mapIndex++;
		}

		// Bet Advisor real results
		for (int i = 0; i < betAdvisorBets.size(); i++) {
			Bet bet = betAdvisorBets.get(i);
			BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);		
			Date date = tip.date;
			mapping[mapIndex] = (date.getDay() + 6) % 7;
			mapIndex++;
		}
		// BlogaBet real results
		for (int i = 0; i < blogaBetBets.size(); i++) {
			Bet bet = blogaBetBets.get(i);		
			BlogaBetTip tip = (BlogaBetTip)gson.fromJson(bet.getTipJsonString(), BlogaBetTip.class);	
			Date date = tip.startDate;
			mapping[mapIndex] = (date.getDay() + 6) % 7;
			mapIndex++;
		}	
		calculateStats(rows, mapping);
		
		int[] tipMapping = new int[betAdvisorList.size() + blogaBetList.size() + betAdvisorTips.size() + blogaBetTips.size()];
		int tipMapIndex = 0;
		// BA Historical
		for(int i = 0; i < betAdvisorList.size(); i++){
			BetAdvisorElement element = betAdvisorList.get(i);
			int index = (element.getGameDate().getDay() + 6) % 7;
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BB Historical
		for(int i = 0; i < blogaBetList.size(); i++){
			
			BlogaBetElement element = blogaBetList.get(i);
			int index = (element.getGameDate().getDay() + 6) % 7;
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BA Real
		for(int i = 0; i < betAdvisorTips.size(); i++){
			BetAdvisorTip tip = betAdvisorTips.get(i);
			int index = (tip.date.getDay() + 6) % 7;
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}
		
		// BB real
		for(int i = 0; i < blogaBetTips.size(); i++){
			BlogaBetTip tip = blogaBetTips.get(i);
			int index = (tip.startDate.getDay() + 6) % 7;
			tipMapping[tipMapIndex] = index;
			tipMapIndex++;
		}	
		calculateTipStats(rows, tipMapping);
		
		return rows;
	}
	
	public void calculateTipStats(List<StatsRow> rows, int[] mapping){
		if(rows.isEmpty())
			return;
		
		int mappingBaseIndex = 0;
		
		// BA Historical
		if(betAdvisor && historical){
			for(int i = 0; i < betAdvisorList.size(); i++){
				BetAdvisorElement element = betAdvisorList.get(mappingBaseIndex + i);
				String siteTipster = element.getTipster() + " (BA)";
				if(activeTipsters != null){
					if(!activeTipsters.containsKey(siteTipster) || !activeTipsters.get(siteTipster)){
						continue;
					}
				}
				Date gameDate = element.getGameDate();
				double bestOdds = element.getOdds();
				if(gameDate.after(startdate) && gameDate.before(endDate) && bestOdds >= minOdds && bestOdds <= maxOdds && betAdvisor){
					int mapIndex = mapping[mappingBaseIndex + i];
					if(mapIndex == -1)
						continue;

					String typeOfBet = element.getTypeOfBet();
					typeOfBet = typeOfBet.replace(" Team", "");
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(element.getSelection().equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
						}
						else{
							if(!oneTwoResult)
								continue;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
					}
					StatsRow row = rows.get(mapIndex);
					row.numberOfTips++;
				}
			}		
		}
		mappingBaseIndex += betAdvisorList.size();
		
		// BB Historical
		if(blogaBet && historical){
			for(int i = 0; i < blogaBetList.size(); i++){
				
				// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
				BlogaBetElement element = blogaBetList.get(i);
				
				String siteTipster = element.getTipster() + " (BB)";
				if(activeTipsters != null){
					if(!activeTipsters.containsKey(siteTipster) || !activeTipsters.get(siteTipster)){
						continue;
					}
				}		
				Date gameDate = element.getGameDate();
				double bestOdds = element.getBestOdds();
				if(gameDate.after(startdate) && gameDate.before(endDate) && bestOdds >= minOdds && bestOdds <= maxOdds){
					int mapIndex = mapping[mappingBaseIndex + i];
					if(mapIndex == -1)
						continue;

					String typeOfBet = element.getTypeOfBet();
					typeOfBet = typeOfBet.replace(" Half Time", "");
					
					
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(element.getSelection().equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
						}
						else{
							if(!oneTwoResult)
								continue;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over Under")){
						if(!overUnder)
							continue;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
					}
					StatsRow row = rows.get(mapIndex);
					row.numberOfTips++;
				}
			}		
		}
		mappingBaseIndex += blogaBetList.size();
		
		if(betAdvisor && real){
			for(int i = 0; i < betAdvisorTips.size(); i++){
				BetAdvisorTip tip = betAdvisorTips.get(i);
				String tipster = tip.tipster;
				String siteTipster = tipster + " (BA)";
				if(activeTipsters != null){
					if(!activeTipsters.containsKey(siteTipster) || !activeTipsters.get(siteTipster)){
						continue;
					}
				}
				Date gameDate = tip.date;
				double bestOdds = tip.bestOdds;
				if(gameDate.after(startdate) && gameDate.before(endDate) && bestOdds >= minOdds && bestOdds <= maxOdds && betAdvisor){
					int mapIndex = mapping[mappingBaseIndex + i];
					if(mapIndex == -1)
						continue;
					
					String typeOfBet = tip.typeOfBet;
					typeOfBet = typeOfBet.replace(" Team", "");
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(tip.betOn.equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
						}
						else{
							if(!oneTwoResult)
								continue;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
					}
					StatsRow row = rows.get(mapIndex);
					row.numberOfTips++;
				}
			}		
		}
		mappingBaseIndex += betAdvisorTips.size();
		
		if(blogaBet && real){
			for(int i = 0; i < blogaBetTips.size(); i++){
				BlogaBetTip tip = blogaBetTips.get(i);
				String tipster = tip.tipster;
				String siteTipster = tipster + " (BB)";
				if(activeTipsters != null){
					if(!activeTipsters.containsKey(siteTipster) || !activeTipsters.get(siteTipster)){
						continue;
					}
				}
				Date gameDate = tip.startDate;
				double bestOdds = tip.odds;
				if(gameDate.after(startdate) && gameDate.before(endDate) && bestOdds >= minOdds && bestOdds <= maxOdds && blogaBet){
					int mapIndex = mapping[mappingBaseIndex + i];
					if(mapIndex == -1)
						continue;
					
					String typeOfBet = tip.pivotType;
					typeOfBet = typeOfBet.replace(" Team", "");
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					typeOfBet = typeOfBet.replace(" Corners", "");
					typeOfBet = typeOfBet.replace(" Alternative", "");
					
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(tip.selection.equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
						}
						else{
							if(!oneTwoResult)
								continue;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
					}
					StatsRow row = rows.get(mapIndex);
					row.numberOfTips++;
				}
			}			
		}
		
		for(int i = 0; i < rows.size() -1; i++){
			rows.get(rows.size() -1).numberOfTips += rows.get(i).numberOfTips;
			rows.get(i).percentOfTipsFound = 100.0 * rows.get(i).numberOfBets / rows.get(i).numberOfTips;
		}
		rows.get(rows.size() -1).percentOfTipsFound = 100.0 * rows.get(rows.size() -1).numberOfBets * 1.0 / rows.get(rows.size() -1).numberOfTips;
	}
	
	public void calculateStats(List<StatsRow> rows, int[] mapping){
		
		if(rows.isEmpty()){
			return;		
		}
		
		int mappingBaseIndex = 0;
		// BetAdvisor Backtest
		if(betAdvisor && historical){
			for(int i = 0; i < betAdvisorBacktestBets.size(); i++){
				
				// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
				BetAdvisorElement element = betAdvisorBacktestBets.get(i);
				double liquidity = betAdvisorBacktestLiquidity.get(i);
				double bestOdds = betAdvisorBacktestBestOddsList.get(i);
				
				String siteTipster = element.getTipster() + " (BA)";
				boolean tipsterContained = false;
				if(activeTipsters != null){
					if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
						tipsterContained = true;
					}
					for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
						if(aliasList.get(a).isSelected()){
							for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
								String aliasTipster = aliasList.get(a).tipsters.get(al);
								if(aliasTipster.equals(siteTipster)){
									tipsterContained = true;
									break;
								}
							}
						}
					}
				}
				if(!tipsterContained)
					continue;
								
				Date gameDate = element.getGameDate();
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && bestOdds >= minOdds && bestOdds <= maxOdds){
					int mapIndex = mapping[mappingBaseIndex + i];
					
					StatsRow row = rows.get(mapIndex);
					String typeOfBet = element.getTypeOfBet();
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					
					BettingManagerBet bet = new BettingManagerBet();
					bet.tipster = siteTipster;
					bet.betDate = element.getPublicationDate();
					bet.gameDate = element.getGameDate();
					
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(element.getSelection().equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
							bet.koB = "X";
						}
						else{
							if(!oneTwoResult)
								continue;
							bet.koB = "1/2";
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
						bet.koB = "O/U";
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
						bet.koB = "AH";
					}
					row.numberOfBets++;
					row.averageLiquidity += liquidity;
					row.averageOdds += element.getOdds();
					row.invested += element.getTake();
					
					bet.event = element.getEvent();
					bet.selection = element.getSelection();
					bet.odds = bestOdds;
					
					if(element.getProfit() > 0){
						row.averageYield += bestOdds * element.getTake() - element.getTake();
						row.flatStakeYield += bestOdds - 1;
						
						bet.netWon = "" + (bestOdds * element.getTake() - element.getTake());
					}
					else if(element.getProfit() < 0){
						row.averageYield -= element.getTake();
						row.flatStakeYield -= 1;	
						
						bet.netWon = "" + (- element.getTake());
					}
					else{
						bet.netWon = "0";
					}
					if(bestOdds / element.getOdds() > 0.95){
						row.percentOver95++;
					}
					row.percentWeGet += bestOdds / element.getOdds();
					List<BettingManagerBet> bets = row.bets;
					bets.add(bet);
				}				
			}
		}
		mappingBaseIndex += betAdvisorBacktestBets.size();
		
		// BlogaBet Backtest
		if(blogaBet && historical){
			for(int i = 0; i < blogaBetBacktestBets.size(); i++){
				
				// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
				BlogaBetElement element = blogaBetBacktestBets.get(i);
				double liquidity = blogaBetBacktestLiquidity.get(i);
				double bestOdds = blogaBetBacktestBestOddsList.get(i);
				
				String siteTipster = element.getTipster() + " (BB)";
				boolean tipsterContained = false;
				if(activeTipsters != null){
					if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
						tipsterContained = true;
					}
					for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
						if(aliasList.get(a).isSelected()){
							for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
								String aliasTipster = aliasList.get(a).tipsters.get(al);
								if(aliasTipster.equals(siteTipster)){
									tipsterContained = true;
									break;
								}
							}
						}
					}
				}
				if(!tipsterContained)
					continue;
				
				Date gameDate = element.getGameDate();
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && bestOdds >= minOdds && bestOdds <= maxOdds){
					int mapIndex = mapping[mappingBaseIndex + i];
					
					if(mapIndex == -1)
						System.out.println();

					StatsRow row = rows.get(mapIndex);
					String typeOfBet = element.getTypeOfBet();
					typeOfBet = typeOfBet.replace(" Half Time", "");
					
					BettingManagerBet bet = new BettingManagerBet();
					bet.tipster = siteTipster;
					bet.betDate = element.getPublicationDate();
					bet.gameDate = element.getGameDate();
					
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(element.getSelection().equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
							bet.koB = "X";
						}
						else{
							if(!oneTwoResult)
								continue;
							bet.koB = "1/2";
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over Under")){
						if(!overUnder)
							continue;
						bet.koB = "O/U";
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
						bet.koB = "AH";
					}
					row.numberOfBets++;
					row.averageLiquidity += liquidity;
					row.averageOdds += element.getBestOdds();
					row.invested += element.getStake() * 100;	
					
					bet.event = element.getEvent();
					bet.selection = element.getSelection();
					bet.odds = bestOdds;
					
					if(element.getResult().equalsIgnoreCase("WIN")){
						row.averageYield += bestOdds * StakeCalculation.blogaBetPercent(element.getStake())  * 50 - StakeCalculation.blogaBetPercent(element.getStake())  * 50;
						row.flatStakeYield += bestOdds - 1;
						
						bet.netWon = "" + (bestOdds * StakeCalculation.blogaBetPercent(element.getStake())  * 50 - StakeCalculation.blogaBetPercent(element.getStake())  * 50);
					}
					else if(element.getResult().equalsIgnoreCase("LOST")){
						row.averageYield -= element.getStake() * 100;
						row.flatStakeYield -= 1;	
						
						bet.netWon = "" + (-element.getStake() * 100);
					}
					else{
						bet.netWon = "0";
					}
					
					if(bestOdds / element.getBestOdds() > 0.95){
						row.percentOver95++;
					}
					row.percentWeGet += bestOdds / element.getBestOdds();
					
					List<BettingManagerBet> bets = row.bets;
					bets.add(bet);
				}				
			}		
		}
		mappingBaseIndex += blogaBetBacktestBets.size();
		
		// Bet Advisor real results
		if(betAdvisor && real){		
			for(int i = 0; i < betAdvisorBets.size(); i++){
				Bet bet = betAdvisorBets.get(i);
				
				// The tip, its a different class than a Blogabet tip
				// Some variables also have difefrent names and possible values
				BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);				
				BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);
				
				String siteTipster = tip.tipster + " (BA)";
				boolean tipsterContained = false;
				if(activeTipsters != null){
					if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
						tipsterContained = true;
					}
					for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
						if(aliasList.get(a).isSelected()){
							for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
								String aliasTipster = aliasList.get(a).tipsters.get(al);
								if(aliasTipster.equals(siteTipster)){
									tipsterContained = true;
									break;
								}
							}
						}
					}
				}
				if(!tipsterContained)
					continue;
				
				if(tip.betOn == null){
					continue;
				}
				Date gameDate = tip.date;
				double liquidity = betTicket.getMaxStake();
				double tipOdds = tip.bestOdds;
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && tipOdds >= minOdds && tipOdds <= maxOdds){
					int mapIndex = mapping[mappingBaseIndex + i];

					StatsRow row = rows.get(mapIndex);
					String typeOfBet = tip.typeOfBet;
					typeOfBet = typeOfBet.replace(" Team", "");
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					
					BettingManagerBet bBet = new BettingManagerBet();
					bBet.tipster = siteTipster;
					bBet.betDate = tip.receivedDate;
					bBet.gameDate = tip.date;
					bBet.event = tip.event.replaceAll("121921  ", "");
					bBet.selection = tip.betOn;
					bBet.odds = betTicket.getCurrentOdd();
					
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(tip.betOn.equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
							bBet.koB = "X";
						}
						else{
							if(!oneTwoResult)
								continue;
							bBet.koB = "1/2";
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
						bBet.koB = "O/U";
						bBet.odds++;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
						bBet.koB = "AH";
						bBet.odds++;
					}
					row.numberOfBets++;
					row.averageLiquidity += liquidity;
					row.averageOdds += tip.bestOdds;
					row.invested += bet.getBetAmount();
					
					double realOdds = betTicket.getCurrentOdd();
					if(typeOfBet.equalsIgnoreCase("Over / Under") || typeOfBet.equalsIgnoreCase("Asian Handicap")){
						realOdds++;
					}
					if(bet.getBetStatus() == 4){
						row.averageYield += realOdds * bet.getBetAmount() - bet.getBetAmount();
						row.flatStakeYield += realOdds - 1;
						
						bBet.netWon = "" + (realOdds * bet.getBetAmount() - bet.getBetAmount());
						
					}
					else if(bet.getBetStatus() == 5){
						row.averageYield -= bet.getBetAmount();
						row.flatStakeYield -= 1;		
						
						bBet.netWon = "" + (-bet.getBetAmount());
					}
					else{
						bBet.netWon = "0";
					}
					if(tipOdds / betTicket.getCurrentOdd() > 0.95){
						row.percentOver95++;
					}
					row.percentWeGet += tipOdds / realOdds;
					
					List<BettingManagerBet> bets = row.bets;
					bets.add(bBet);
				}	
			}
		}
		mappingBaseIndex += betAdvisorBets.size();
		
		// BlogaBet real results
		if(blogaBet && real){
			for(int i = 0; i < blogaBetBets.size(); i++){
				Bet bet = blogaBetBets.get(i);
						
				// The tip, its a different class than a betAdvisor tip
				// Some variables also have difefrent names and possible values
				// Conversion from String to double
				String tipJsonString = bet.getTipJsonString();
				int startStake = tipJsonString.indexOf("\"stake\"") + 9;
				int stakeEnd = tipJsonString.indexOf("\"", startStake);
				String stakeString = tipJsonString.substring(startStake, stakeEnd);
				int splitPoint = stakeString.indexOf("/");
				if(splitPoint != -1){
					String a = stakeString.substring(0, splitPoint);
					String b = stakeString.substring(splitPoint + 1);
					double stake = Double.parseDouble(a) / Double.parseDouble(b);
					tipJsonString = tipJsonString.replace(stakeString, stake + "");
				}
				
				BlogaBetTip tip = (BlogaBetTip)gson.fromJson(tipJsonString, BlogaBetTip.class);
				
				BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);
				
				String siteTipster = tip.tipster + " (BB)";
				boolean tipsterContained = false;
				if(activeTipsters != null){
					if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
						tipsterContained = true;
					}
					for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
						if(aliasList.get(a).isSelected()){
							for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
								String aliasTipster = aliasList.get(a).tipsters.get(al);
								if(aliasTipster.equals(siteTipster)){
									tipsterContained = true;
									break;
								}
							}
						}
					}
				}
				if(!tipsterContained)
					continue;
				
				Date gameDate = tip.startDate;
				double liquidity = betTicket.getMaxStake();
				double tipOdds = tip.odds;
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && tipOdds >= minOdds && tipOdds <= maxOdds){
					int mapIndex = mapping[mappingBaseIndex + i];

					StatsRow row = rows.get(mapIndex);
					String typeOfBet = tip.pivotType;
					typeOfBet = typeOfBet.replace(" Team", "");
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					typeOfBet = typeOfBet.replace(" Corners", "");
					typeOfBet = typeOfBet.replace(" Alternative", "");
					
					BettingManagerBet bBet = new BettingManagerBet();
					bBet.tipster = siteTipster;
					bBet.betDate = tip.receivedDate;
					bBet.gameDate = tip.startDate;
					bBet.event = tip.event.replaceAll("121921  ", "");
					bBet.selection = tip.selection;
					bBet.odds = betTicket.getCurrentOdd();
					
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(tip.selection.equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
							bBet.koB = "X";
						}
						else{
							if(!oneTwoResult)
								continue;
							bBet.koB = "1/2";
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
						bBet.koB = "O/U";
						bBet.odds++;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
						bBet.koB = "AH";
						bBet.odds++;
					}
					row.numberOfBets++;
					row.averageLiquidity += liquidity;
					row.averageOdds += tip.odds;
					row.invested += bet.getBetAmount();
					
					double realOdds = betTicket.getCurrentOdd();
					if(typeOfBet.equalsIgnoreCase("Over / Under") || typeOfBet.equalsIgnoreCase("Asian Handicap")){
						realOdds++;
					}
					if(bet.getBetStatus() == 4){
						row.averageYield += realOdds * bet.getBetAmount() - bet.getBetAmount();
						row.flatStakeYield += realOdds - 1;
						
						bBet.netWon = "" + (realOdds * bet.getBetAmount() - bet.getBetAmount());
					}
					else if(bet.getBetStatus() == 5){
						row.averageYield -= bet.getBetAmount();
						row.flatStakeYield -= 1;	
						
						bBet.netWon = "" + (-bet.getBetAmount());
					}
					else{
						bBet.netWon = "0";
					}
					if(tipOdds / betTicket.getCurrentOdd() > 0.95){
						row.percentOver95++;
					}
					row.percentWeGet += tipOdds / realOdds;
					
					List<BettingManagerBet> bets = row.bets;
					bets.add(bBet);
				}
			}	
		}
		
		StatsRow averageRow = new StatsRow();
		averageRow.groupBy = "Average";
		
		BettingManagerBetComparator c = new BettingManagerBetComparator();
		
		// Compute averages and sort Bets
		for(int i = 0; i < rows.size(); i++){
			StatsRow row = rows.get(i);
			averageRow.averageLiquidity += row.averageLiquidity;
			averageRow.averageOdds += row.averageOdds;
			averageRow.averageYield += row.averageYield;
			averageRow.flatStakeYield += row.flatStakeYield;
			averageRow.percentOver95 += row.percentOver95;
			averageRow.percentWeGet += row.percentWeGet;
			averageRow.numberOfBets += row.numberOfBets;
			averageRow.invested += row.invested;
			averageRow.bets.addAll(row.getBets());
			
			row.averageLiquidity /= Math.max(row.numberOfBets, 1);
			row.averageOdds /= Math.max(row.numberOfBets, 1);
			row.averageYield = row.averageYield * 100.0 / Math.max(row.invested, 1);
			row.flatStakeYield = row.flatStakeYield * 100.0 / Math.max(row.numberOfBets, 1);
			row.percentOver95 = row.percentOver95 * 100.0 / Math.max(row.numberOfBets, 1);
			row.percentWeGet = row.percentWeGet * 100.0 / Math.max(row.numberOfBets, 1);
			Collections.sort(row.bets, c);
			if(rows.get(i).bets.size() > 100)
				rows.get(i).bets = row.bets.subList(row.bets.size() - 101, row.bets.size() - 1);
		}
		averageRow.averageLiquidity /= Math.max(averageRow.numberOfBets, 1);
		averageRow.averageOdds /= Math.max(averageRow.numberOfBets, 1);
		averageRow.averageYield /= Math.max(averageRow.invested, 1);
		averageRow.flatStakeYield /= Math.max(averageRow.numberOfBets, 1);
		averageRow.percentOver95 /= Math.max(averageRow.numberOfBets, 1);
		averageRow.percentWeGet /= Math.max(averageRow.numberOfBets, 1);
		
		averageRow.percentOver95 *= 100;
		averageRow.percentWeGet *= 100; 
		averageRow.averageYield *= 100;
		averageRow.flatStakeYield *= 100;	
		rows.add(averageRow);
	}
	
	// BA List
	// BB List
	// Total list
	public List<List<Double>> getGraphs(){
		List<List<Double>> result = new ArrayList<List<Double>>();
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		
		List<Pair<Date, Double>> baList = new ArrayList<Pair<Date, Double>>();
		List<Pair<Date, Double>> bbList = new ArrayList<Pair<Date, Double>>();
		List<Pair<Date, Double>> totalList = new ArrayList<Pair<Date, Double>>();
		
		// BetAdvisor Backtest
		if(betAdvisor && historical){
			for(int i = 0; i < betAdvisorBacktestBets.size(); i++){
				
				// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
				BetAdvisorElement element = betAdvisorBacktestBets.get(i);
				HistoricalDataElement historicalElement = betAdvisorHistorical.get(i);
				double liquidity = betAdvisorBacktestLiquidity.get(i);
				double bestOdds = betAdvisorBacktestBestOddsList.get(i);
				
				String siteTipster = element.getTipster() + " (BA)";
				boolean tipsterContained = false;
				if(activeTipsters != null){
					if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
						tipsterContained = true;
					}
					for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
						if(aliasList.get(a).isSelected()){
							for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
								String aliasTipster = aliasList.get(a).tipsters.get(al);
								if(aliasTipster.equals(siteTipster)){
									tipsterContained = true;
									break;
								}
							}
						}
					}
				}
				if(!tipsterContained)
					continue;
								
				Date gameDate = element.getGameDate();
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && bestOdds >= minOdds && bestOdds <= maxOdds){
					String typeOfBet = element.getTypeOfBet();
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(element.getSelection().equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
						}
						else{
							if(!oneTwoResult)
								continue;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
					}
					
					if(element.getProfit() > 0){
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, bestOdds * element.getTake() -element.getTake());
						baList.add(p);
						totalList.add(p);
						
					}
					if(element.getProfit() < 0){
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, -element.getTake());
						baList.add(p);
						totalList.add(p);			
					}
					else{
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, 0.0);
						baList.add(p);
						totalList.add(p);						
					}

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
				
				String siteTipster = element.getTipster() + " (BB)";
				boolean tipsterContained = false;
				if(activeTipsters != null){
					if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
						tipsterContained = true;
					}
					for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
						if(aliasList.get(a).isSelected()){
							for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
								String aliasTipster = aliasList.get(a).tipsters.get(al);
								if(aliasTipster.equals(siteTipster)){
									tipsterContained = true;
									break;
								}
							}
						}
					}
				}
				if(!tipsterContained)
					continue;
				
				Date gameDate = element.getGameDate();
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && bestOdds >= minOdds && bestOdds <= maxOdds){
					String typeOfBet = element.getTypeOfBet();
					typeOfBet = typeOfBet.replace(" Half Time", "");
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(element.getSelection().equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
						}
						else{
							if(!oneTwoResult)
								continue;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over Under")){
						if(!overUnder)
							continue;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
					}
					
					if(element.getResult().equalsIgnoreCase("WIN")){
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, StakeCalculation.blogaBetPercent(element.getStake()) * 50 * bestOdds - StakeCalculation.blogaBetPercent(element.getStake())  * 50);
						bbList.add(p);
						totalList.add(p);		
					}
					else if(element.getResult().equalsIgnoreCase("LOST")){
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, - StakeCalculation.blogaBetPercent(element.getStake())  * 50);
						bbList.add(p);
						totalList.add(p);		
					}
					else{
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, 0.0);
						bbList.add(p);
						totalList.add(p);		
					}
				}				
			}		
		}
		
		// Bet Advisor real results
		if(betAdvisor && real){		
			for(int i = 0; i < betAdvisorBets.size(); i++){
				Bet bet = betAdvisorBets.get(i);
				
				// The tip, its a different class than a Blogabet tip
				// Some variables also have difefrent names and possible values
				BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);
				
				BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);
				
				String siteTipster = tip.tipster + " (BA)";
				boolean tipsterContained = false;
				if(activeTipsters != null){
					if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
						tipsterContained = true;
					}
					for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
						if(aliasList.get(a).isSelected()){
							for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
								String aliasTipster = aliasList.get(a).tipsters.get(al);
								if(aliasTipster.equals(siteTipster)){
									tipsterContained = true;
									break;
								}
							}
						}
					}
				}
				if(!tipsterContained)
					continue;
				
				if(tip.betOn == null)
					continue;
				Date gameDate = tip.date;
				double liquidity = betTicket.getMaxStake();
				double tipOdds = tip.bestOdds;
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && tipOdds >= minOdds && tipOdds <= maxOdds){
					String typeOfBet = tip.typeOfBet;
					typeOfBet = typeOfBet.replace(" Team", "");
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(tip.betOn.equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
						}
						else{
							if(!oneTwoResult)
								continue;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
					}
			
					double realOdds = betTicket.getCurrentOdd();
					if(typeOfBet.equalsIgnoreCase("Over / Under") || typeOfBet.equalsIgnoreCase("Asian Handicap")){
						realOdds++;
					}
					if(bet.getBetStatus() == 4){
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, bet.getBetAmount() * realOdds - bet.getBetAmount());
						baList.add(p);
						totalList.add(p);	
						
					}
					else if(bet.getBetStatus() == 5){
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, -bet.getBetAmount());
						baList.add(p);
						totalList.add(p);				
					}
				}	
			}
		}
		
		// BlogaBet real results
		if(blogaBet && real){
			for(int i = 0; i < blogaBetBets.size(); i++){
				Bet bet = blogaBetBets.get(i);
				
				// The tip, its a different class than a betAdvisor tip
				// Some variables also have difefrent names and possible values
				// Conversion from String to double
				String tipJsonString = bet.getTipJsonString();
				int startStake = tipJsonString.indexOf("\"stake\"") + 9;
				int stakeEnd = tipJsonString.indexOf("\"", startStake);
				String stakeString = tipJsonString.substring(startStake, stakeEnd);
				int splitPoint = stakeString.indexOf("/");
				if(splitPoint != -1){
					String a = stakeString.substring(0, splitPoint);
					String b = stakeString.substring(splitPoint + 1);
					double stake = Double.parseDouble(a) / Double.parseDouble(b);
					tipJsonString = tipJsonString.replace(stakeString, stake + "");
				}
				
				BlogaBetTip tip = (BlogaBetTip)gson.fromJson(tipJsonString, BlogaBetTip.class);
				
				BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);
				
				String siteTipster = tip.tipster + " (BB)";
				boolean tipsterContained = false;
				if(activeTipsters != null){
					if(activeTipsters.containsKey(siteTipster) && activeTipsters.get(siteTipster)){
						tipsterContained = true;
					}
					for(int a = 0; a < aliasList.size() && !tipsterContained; a++){
						if(aliasList.get(a).isSelected()){
							for(int al = 0; al < aliasList.get(a).tipsters.size(); al++){
								String aliasTipster = aliasList.get(a).tipsters.get(al);
								if(aliasTipster.equals(siteTipster)){
									tipsterContained = true;
									break;
								}
							}
						}
					}
				}
				if(!tipsterContained)
					continue;
				
				Date gameDate = tip.startDate;
				double liquidity = betTicket.getMaxStake();
				double tipOdds = tip.odds;
				if(gameDate.after(startdate) && gameDate.before(endDate) && liquidity >= minLiquidity && liquidity <= maxLiquidity && tipOdds >= minOdds && tipOdds <= maxOdds){
					String typeOfBet = tip.pivotType;
					typeOfBet = typeOfBet.replace(" Team", "");
					typeOfBet = typeOfBet.replace(" 1st Half", "");
					typeOfBet = typeOfBet.replace(" Corners", "");
					typeOfBet = typeOfBet.replace(" Alternative", "");
					if(typeOfBet.equalsIgnoreCase("MATCH ODDS")){
						if(tip.selection.equalsIgnoreCase("DRAW")){
							if(!xResult)
								continue;
						}
						else{
							if(!oneTwoResult)
								continue;
						}
					}
					else if(typeOfBet.equalsIgnoreCase("Over / Under")){
						if(!overUnder)
							continue;
					}
					else if(typeOfBet.equalsIgnoreCase("Asian Handicap")){
						if(!asianHandicap)
							continue;
					}
					
					double realOdds = betTicket.getCurrentOdd();
					if(typeOfBet.equalsIgnoreCase("Over / Under") || typeOfBet.equalsIgnoreCase("Asian Handicap")){
						realOdds++;
					}
					if(bet.getBetStatus() == 4){
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, bet.getBetAmount() * realOdds - bet.getBetAmount());
						bbList.add(p);
						totalList.add(p);	
						
					}
					else if(bet.getBetStatus() == 5){
						Pair<Date, Double> p = new Pair<Date, Double>(gameDate, -bet.getBetAmount());
						bbList.add(p);
						totalList.add(p);				
					}
				}
			}	
		}
		
		Comparator<Pair<Date, Double>> c = new Comparator<Pair<Date,Double>>() {

			@Override
			public int compare(Pair<Date, Double> o1, Pair<Date, Double> o2) {
				long m0 = o1.getKey().getTime();
				
				long m1 = o2.getKey().getTime();
				
				if(m0 < m1)
					return -1;
				if(m1 < m0)
					return 1;
				return 0;
			}
		};
		
		Collections.sort(baList, c);
		Collections.sort(bbList, c);
		Collections.sort(totalList, c);
		
		double baTotal = 0;
		for(int i = 0; i < baList.size(); i++){
			result.get(0).add(baTotal);
			baTotal += baList.get(i).getValue();
		}
		result.get(0).add(baTotal);
		
		double bbTotal = 0;
		for(int i = 0; i < bbList.size(); i++){
			result.get(1).add(bbTotal);
			bbTotal += bbList.get(i).getValue();
		}
		result.get(1).add(bbTotal);
		
		double totalTotal = 0;
		for(int i = 0; i < totalList.size(); i++){
			result.get(2).add(totalTotal);
			totalTotal += totalList.get(i).getValue();
		}
		result.get(2).add(totalTotal);		
			
		return result;
	}
	
	public static void main(String[] args) {
		StatsCalculator calculator = new StatsCalculator();
		List<StatsRow> row = calculator.getKoBStats();
		System.out.println();
	}
}
