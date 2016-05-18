package resultAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;

import jayeson.lib.datastructure.PivotType;
import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.recordfetcher.DeltaCrawlerSession;
import mailParsing.BetAdvisorTip;
import mailParsing.BlogaBetTip;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;
import bettingBot.entities.BetComparator;
import bettingBot.entities.BetTicket;

import com.google.gson.Gson;

public class ResultAnalyser {

	private BettingBotDatabase dataBase = null;	
	private static Gson gson = new Gson();
	
	private static final int numberOfStakes = 10;
	private List<Double> yieldPerStake;
	private List<Double> betsPerStake;
	private List<Double> finishedBetsPerStake;
	
	
	public ResultAnalyser(){
		try {
			dataBase = new BettingBotDatabase();
			yieldPerStake = new ArrayList<Double>();
			betsPerStake = new ArrayList<Double>();
			finishedBetsPerStake = new ArrayList<Double>();
			for(int i = 0; i < numberOfStakes; i++){
				betsPerStake.add(0.0);
				yieldPerStake.add(0.0);
				finishedBetsPerStake.add(0.0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
			
	public void analyseBets(){
		/* Initialize Classes for JSon deserialisation */
		boolean initialiseGson = true;
		Class recordClass = null;
		Class eventClass = null;
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
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println("Objects loaded from Inputstream");
		}
		else{
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
			
			while(initialiseGson){
				System.out.println("Initialising Gson");
				Collection<SoccerEvent> events = cs.getAllEvents();
				for (SoccerEvent event : events) {	
					
					Collection<Record> records = event.getRecords();	
					/* If there are no records for this event, we can not bet on it */
					if (records.size() == 0) 
						continue;
					
					// Get Objects
					Record record = records.iterator().next(); 
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
						eventClass = event.getClass();
						recordClass = record.getClass();
						System.out.println("Objects saved");
						break;
					}catch(Exception e){
						e.printStackTrace();
						System.exit(-1);
					}
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
			cs.disconnect();
		}

		int numberOfRunninngBets = 0;
		int numberOfWonBets = 0;
		int numberOfLostBets = 0;
		int numberOfCancelledBets = 0;
		int numberOfDrawnBets = 0;
		List<Bet> bets = dataBase.getAllBets();
		Collections.sort(bets, new BetComparator());
		List<Bet> betsBlogaBet = dataBase.getAllBetsBlogaBet();
		Collections.sort(betsBlogaBet, new BetComparator());
		List<Double> profitByBet = new ArrayList<Double>();
		double profit = 0;
		double runningInvestments = 0;
		double oddDifference = 0;
		double averageLiquidity = 0;
		int numberOfLiquidityBets = 0;
		double averageYield = 0;
		double profitForAlwaysMaxStake = 0;
		
		// Needed to check stats for different liquidity levels */
		double maxMaxStakeAllBets = 0;
		double minMaxStakeAllBets = Double.MAX_VALUE;
		
		for(int i = 0; i < bets.size(); i++){
			Bet bet = bets.get(i);
			String betTicketJsonString = bet.getBetTicketJsonString();
			if(betTicketJsonString != null){
				BetTicket betTicket = BetTicket.fromJson(betTicketJsonString);
				double maxStake = betTicket.getMaxStake();
				if(maxStake > maxMaxStakeAllBets){
					maxMaxStakeAllBets = maxStake;
				}
				if(maxStake < minMaxStakeAllBets){
					minMaxStakeAllBets = maxStake;
				}
			}			
		}
		
		for(int i = 0; i < betsBlogaBet.size(); i++){
			Bet bet = bets.get(i);
			String betTicketJsonString = bet.getBetTicketJsonString();
			if(betTicketJsonString != null){
				BetTicket betTicket = BetTicket.fromJson(betTicketJsonString);
				double maxStake = betTicket.getMaxStake();
				if(maxStake > maxMaxStakeAllBets){
					maxMaxStakeAllBets = maxStake;
				}
				if(maxStake < minMaxStakeAllBets){
					minMaxStakeAllBets = maxStake;
				}
			}			
		}
		
		double stakeLevel = (maxMaxStakeAllBets - minMaxStakeAllBets) / numberOfStakes;
		System.out.println("maxStake: " + maxMaxStakeAllBets);
		System.out.println("minStake: " + minMaxStakeAllBets);
		
		double turnOver = 0;
		
		Set<String> processedTipsBetAdvisor = new HashSet<String>();
		for(int i = 0; i < bets.size(); i++){
			Bet bet = bets.get(i);
			Record record = (Record)gson.fromJson(bet.getRecordJsonString(), recordClass);
			SoccerEvent event = (SoccerEvent)gson.fromJson(bet.getEventJsonString(), eventClass);
			
			BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);
			String key = tip.tipster + tip.event + tip.date.toString();
			if(processedTipsBetAdvisor.contains(key))
				continue;
			processedTipsBetAdvisor.add(key);
			
			turnOver += bet.getBetAmount();
			
			
			String betTicketJsonString = bet.getBetTicketJsonString();
			BetTicket betTicket = null;
			if(betTicketJsonString != null){
				betTicket = BetTicket.fromJson(betTicketJsonString);
				numberOfLiquidityBets++;
				averageLiquidity += betTicket.getMaxStake();
			}
			
			double odd = 0;
			if(record.getPivotType() == PivotType.HDP){
				odd = bet.getBetOdd() + 1;
			}
			else if(record.getPivotType() == PivotType.TOTAL){
				odd = bet.getBetOdd() + 1;
			}
			else{
				odd = bet.getBetOdd();
			}
			
			oddDifference += odd / Math.max(1, tip.bestOdds);
			
			int stakeIndex = -1;
			for(int j = 0; j < numberOfStakes; j++){
				if(betTicket != null){
					double maxStake = betTicket.getMaxStake();
					if(maxStake > minMaxStakeAllBets + (j + 1) * stakeLevel + 0.01)
						continue;
					betsPerStake.set(j, betsPerStake.get(j) + 1);
					stakeIndex = j;
					if(bet.getBetStatus() != 1){
						finishedBetsPerStake.set(j, finishedBetsPerStake.get(j) + 1);
					}
					break;
				}
			}
			
			if(bet.getBetStatus() == 1){
				numberOfRunninngBets++;
				runningInvestments += bet.getBetAmount();
			}
			if(bet.getBetStatus() == 4){
				numberOfWonBets++;
				double betProfit = bet.getBetAmount() * odd - bet.getBetAmount();
				profit += betProfit;
				if(betTicket != null)
					profitForAlwaysMaxStake += betTicket.getMaxStake() * odd - betTicket.getMaxStake();
				profitByBet.add(profit);
				averageYield += betProfit / bet.getBetAmount();
				if(stakeIndex != -1)
					yieldPerStake.set(stakeIndex, yieldPerStake.get(stakeIndex) + betProfit / bet.getBetAmount());
			}
			if(bet.getBetStatus() == 5){
				numberOfLostBets++;
				profit -= bet.getBetAmount();
				if(betTicket != null)
					profitForAlwaysMaxStake -= betTicket.getMaxStake();
				profitByBet.add(profit);
				averageYield--;
				if(stakeIndex != -1)
					yieldPerStake.set(stakeIndex, yieldPerStake.get(stakeIndex) - 1);
			}
			if(bet.getBetStatus() == 6){
				numberOfCancelledBets++;
			}
			if(bet.getBetStatus() == 7){
				numberOfDrawnBets++;
			}
		}
		
		for(int i = 0; i < 0; i++){
			Bet bet = betsBlogaBet.get(i);
			System.out.println(bet.getBetStatus());
			Record record = (Record)gson.fromJson(bet.getRecordJsonString(), recordClass);
			SoccerEvent event = (SoccerEvent)gson.fromJson(bet.getEventJsonString(), eventClass);
			turnOver += bet.getBetAmount();
			
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
			String betTicketJsonString = bet.getBetTicketJsonString();
			BetTicket betTicket = null;
			if(betTicketJsonString != null){
				betTicket = BetTicket.fromJson(betTicketJsonString);
				numberOfLiquidityBets++;
				averageLiquidity += betTicket.getMaxStake();
			}
			
			double odd = 0;
			if(record.getPivotType() == PivotType.HDP){
				odd = bet.getBetOdd() + 1;
			}
			else if(record.getPivotType() == PivotType.TOTAL){
				odd = bet.getBetOdd() + 1;
			}
			else{
				odd = bet.getBetOdd();
			}
			
			oddDifference += odd / Math.max(1, tip.odds);
			
			int stakeIndex = -1;
			for(int j = 0; j < numberOfStakes; j++){
				if(betTicket != null){
					double maxStake = betTicket.getMaxStake();
					if(maxStake > minMaxStakeAllBets + (j + 1) * stakeLevel + 0.01)
						continue;
					betsPerStake.set(j, betsPerStake.get(j) + 1);
					stakeIndex = j;
					if(bet.getBetStatus() != 1){
						finishedBetsPerStake.set(j, finishedBetsPerStake.get(j) + 1);
					}
					break;
				}
			}
			
			if(bet.getBetStatus() == 1){
				numberOfRunninngBets++;
				runningInvestments += bet.getBetAmount();
			}
			if(bet.getBetStatus() == 4){
				numberOfWonBets++;
				double betProfit = bet.getBetAmount() * odd - bet.getBetAmount();
				profit += betProfit;
				if(betTicket != null)
					profitForAlwaysMaxStake += betTicket.getMaxStake() * odd - betTicket.getMaxStake();
				profitByBet.add(profit);
				averageYield += betProfit / bet.getBetAmount();
				if(stakeIndex != -1)
					yieldPerStake.set(stakeIndex, yieldPerStake.get(stakeIndex) + betProfit / bet.getBetAmount());
			}
			if(bet.getBetStatus() == 5){
				numberOfLostBets++;
				profit -= bet.getBetAmount();
				if(betTicket != null)
					profitForAlwaysMaxStake -= betTicket.getMaxStake();
				profitByBet.add(profit);
				averageYield--;
				if(stakeIndex != -1)
					yieldPerStake.set(stakeIndex, yieldPerStake.get(stakeIndex) - 1);
			}
			if(bet.getBetStatus() == 6){
				numberOfCancelledBets++;
			}
			if(bet.getBetStatus() == 7){
				if(record.getPivotType() == PivotType.HDP){
					if(record.getPivotValue() == 0.5){
						System.out.println();
					}
					if(record.getPivotValue() == 0.25){
						System.out.println();
					}
					if(record.getPivotValue() == 0.75){
						System.out.println();
					}
				}
				numberOfDrawnBets++;
			}
		}
	
		oddDifference /= bets.size() + betsBlogaBet.size();
		averageYield /= bets.size() + betsBlogaBet.size();
		averageLiquidity /= numberOfLiquidityBets;
		
		System.out.println("turnover: " + turnOver);
		System.out.println("numberOfRunninngBets: " + numberOfRunninngBets);
		System.out.println("running Investments: " + runningInvestments);
		System.out.println("numberOfWonBets: " + numberOfWonBets);
		System.out.println("numberOfLostBets: " + numberOfLostBets);
		System.out.println("numberOfDrawnBets: " + numberOfDrawnBets);
		System.out.println("numberOfCancelledBets: " + numberOfCancelledBets);
		System.out.println("profit: " + profit);
		System.out.println("average Yield: " + averageYield);
		System.out.println("average Odd Ratio: " + oddDifference);
		System.out.println("average Liquidity: " + averageLiquidity + " in " + numberOfLiquidityBets + " bets");
		
		System.out.println("profit for always max stake: " + profitForAlwaysMaxStake);
		System.out.println("Stakes:");
		for(int i = 0; i < numberOfStakes; i++){
			double stakeStart = minMaxStakeAllBets + i * stakeLevel;
			double stakeEnd = stakeStart + stakeLevel;
			System.out.println(i + ": " + stakeStart + " - " + stakeEnd);
		
		}
		System.out.println("bets per Stake: " + betsPerStake.toString());
		for(int i = 0; i < numberOfStakes; i++){
			yieldPerStake.set(i, yieldPerStake.get(i) / Math.max(1, finishedBetsPerStake.get(i)));
		}
		System.out.println("yield per Stake: " + yieldPerStake);
		
		// Chart
		XYSeries series = new XYSeries("Profit");
		XYDataset xyDataset = new XYSeriesCollection(series);
		for(int i = 0; i < profitByBet.size(); i++){
			series.add(i, profitByBet.get(i));
		}
		final JFreeChart chart = ChartFactory.createXYLineChart("Profit", "Bets", "Profit", xyDataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Results");
        frame.setContentPane(chartPanel);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
  
	}
	
	public static void main(String[] args) {
		ResultAnalyser analyser = new ResultAnalyser();
		analyser.analyseBets();
	}
}
