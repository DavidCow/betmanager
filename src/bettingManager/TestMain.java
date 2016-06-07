package bettingManager;

import historicalData.HistoricalDataElement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import mailParsing.BetAdvisorEmailParser;
import mailParsing.BetAdvisorTip;
import mailParsing.BlogaBetEmailParser;
import mailParsing.BlogaBetTip;
import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;
import backtest.BetAdvisorBacktest;
import backtest.BlogaBetBacktest;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;
import bettingBot.entities.BetComparator;
import bettingBot.entities.BetTicket;
import blogaBetHistoricalDataParsing.BlogaBetElement;
import blogaBetHistoricalDataParsing.BlogaBetParser;

import com.google.gson.Gson;

public class TestMain {
	
	private static final String blogaBetResultPath = "blogaBetRealBets.dat";
	private static final String betAdvisorResultPath = "betAdvisorRealBets.dat";
	
	private static void saveResultLists() throws Exception{
		BettingBotDatabase database = new BettingBotDatabase();
		List<Bet> betAdvisorBets = database.getAllBets();
		Collections.sort(betAdvisorBets, new BetComparator());
		
        FileOutputStream fileOutput = new FileOutputStream(betAdvisorResultPath);
        BufferedOutputStream br = new BufferedOutputStream(fileOutput);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(betAdvisorBets);
        objectOutputStream.close();
        
		List<Bet> blogaBetBets = database.getAllBetsBlogaBet();
		Collections.sort(blogaBetBets, new BetComparator());
		
        fileOutput = new FileOutputStream(blogaBetResultPath);
        br = new BufferedOutputStream(fileOutput);
        objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(blogaBetBets);
        objectOutputStream.close();
	}
	
	private static void saveTipLists() throws Exception{
		GMailReader reader = new GMailReader("vicentbet90@gmail.com", "bmw735tdi2");
		GMailReader readerBlogaBet = new GMailReader("blogabetcaptcha@gmail.com", "bmw735tdi");
		
		// Get parsed mails
		List<ParsedTextMail> mails = new ArrayList<ParsedTextMail>();
		try{
			mails = reader.read("noreply@betadvisor.com");
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
		
        FileOutputStream fileOutput = new FileOutputStream("betAdvisorTips.dat");
        BufferedOutputStream br = new BufferedOutputStream(fileOutput);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(tips);
        objectOutputStream.close();	
        
		// BlogaBet
		// Get parsed mails
		List<ParsedTextMail> mailsBlogaBet = new ArrayList<ParsedTextMail>();
		try{
			mailsBlogaBet = readerBlogaBet.read("vicentbet90@gmail.com");
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
		
		fileOutput = new FileOutputStream("blogaBetTips.dat");
        br = new BufferedOutputStream(fileOutput);
        objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(tips);
        objectOutputStream.close();	
	}
	
//	public static void test() throws Exception{
//		
//		// This part loads data and unitialises some stuff
//		// It can probably stay mostly untouched
//		// All the necessary files should be in the repository
//		Gson gson = new Gson();
//		File f0 = new File("event.dat");
//		File f1 = new File("record.dat");
//		Class recordClass = null;
//		Class eventClass = null;
//		if(f0.isFile() && f0.canRead() && f1.isFile() && f1.canRead()){
//			try{
//				FileInputStream in0 = new FileInputStream(f0);
//				ObjectInputStream inO0 = new ObjectInputStream(in0);
//				eventClass = (Class)inO0.readObject();
//				
//				FileInputStream in1 = new FileInputStream(f1);
//				ObjectInputStream inO1 = new ObjectInputStream(in1);
//				recordClass = (Class)inO1.readObject();
//				inO0.close();
//				inO1.close();
//			}catch(Exception e){
//				e.printStackTrace();
//				System.exit(-1);
//			}
//			System.out.println("Objects loaded from Inputstream");
//		}
//		
//		// Read historical Tipster Data
//		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
//		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
//		BlogaBetParser parser = new BlogaBetParser();
//		List<BlogaBetElement> blogaBetList = parser.parseSheets("blogaBetTipsterData/csv");
//
//		// Read backtest results
//        FileInputStream fileInput = new FileInputStream(BetAdvisorBacktest.betAdvisorBackTestRecordPath);
//        BufferedInputStream br = new BufferedInputStream(fileInput);
//        ObjectInputStream objectInputStream = new ObjectInputStream(br);	
//		List<HistoricalDataElement> betAdvisorHistorical = (List<HistoricalDataElement>)objectInputStream.readObject();
//		objectInputStream.close();
//		
//		fileInput = new FileInputStream(BetAdvisorBacktest.betAdvisorBackTestLiquidityPath);
//        br = new BufferedInputStream(fileInput);
//        objectInputStream = new ObjectInputStream(br);        
//		List<Double> betAdvisorBacktestLiquidity= (List<Double>)objectInputStream.readObject();
//		objectInputStream.close();
//		
//		FileInputStream fileInput2 = new FileInputStream(BetAdvisorBacktest.betAdvisorBackTestPath);
//		BufferedInputStream br2 = new BufferedInputStream(fileInput2);
//		ObjectInputStream objectInputStream2 = new ObjectInputStream(br2);	
//		List<BetAdvisorElement> betAdvisorBacktestBets = (List<BetAdvisorElement>)objectInputStream2.readObject();
//		objectInputStream.close();
//		
//        fileInput = new FileInputStream(BlogaBetBacktest.blogaBetBackTestRecordPath);
//        br = new BufferedInputStream(fileInput);
//        objectInputStream = new ObjectInputStream(br);	
//		List<HistoricalDataElement> blogaBetHistorical = (List<HistoricalDataElement>)objectInputStream.readObject();
//		objectInputStream.close();
//		fileInput = new FileInputStream(BlogaBetBacktest.blogaBetBackTestPath);
//        br = new BufferedInputStream(fileInput);
//        objectInputStream = new ObjectInputStream(br);	
//		List<BlogaBetElement> blogaBetBacktestBets = (List<BlogaBetElement>)objectInputStream.readObject();
//		objectInputStream.close();
//		fileInput = new FileInputStream(BlogaBetBacktest.blogaBetBackTestLiquidityPath);
//        br = new BufferedInputStream(fileInput);
//        objectInputStream = new ObjectInputStream(br);        
//		List<Double> blogaBetBacktestLiquidity= (List<Double>)objectInputStream.readObject();
//		objectInputStream.close();
//		
//		// Read real bets
//		fileInput = new FileInputStream(betAdvisorResultPath);
//        br = new BufferedInputStream(fileInput);
//        objectInputStream = new ObjectInputStream(br);        
//		List<Bet> betAdvisorBets = (List<Bet>)objectInputStream.readObject();
//		fileInput = new FileInputStream(blogaBetResultPath);
//        br = new BufferedInputStream(fileInput);
//        objectInputStream = new ObjectInputStream(br);        
//		List<Bet> blogaBetBets = (List<Bet>)objectInputStream.readObject();
//			
//		
//		// This Part demostrates how to use the data
//		
//		// BetAdvisor Backtest
//		for(int i = 0; i < betAdvisorBacktestBets.size(); i++){
//			
//			// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
//			BetAdvisorElement element = betAdvisorBacktestBets.get(i);
//			HistoricalDataElement historicalElement = betAdvisorHistorical.get(i);
//			double liquidity = betAdvisorBacktestLiquidity.get(i);
//		}
//		
//		// BlogaBet Backtest
//		for(int i = 0; i < blogaBetBacktestBets.size(); i++){
//			
//			// Those 3 elements combined hold all the relevant informations about a bet in the bet advisor backtest
//			BlogaBetElement element = blogaBetBacktestBets.get(i);
//			HistoricalDataElement historicalElement = betAdvisorHistorical.get(i);
//			double liquidity = betAdvisorBacktestLiquidity.get(i);
//		}
//		
//		// Bet Advisor real results
//		for(int i = 0; i < betAdvisorBets.size(); i++){
//			Bet bet = betAdvisorBets.get(i);
//			
//			// Some objects were saved to our SQL database as JSON string
//			// We have to convert them to objects again
//			Record record = (Record)gson.fromJson(bet.getRecordJsonString(), recordClass);
//			SoccerEvent event = (SoccerEvent)gson.fromJson(bet.getEventJsonString(), eventClass);
//			
//			// The tip, its a different class than a Blogabet tip
//			// Some variables also have difefrent names and possible values
//			BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);
//			
//			BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);
//		}
//		
//		// BlogaBet real results
//		for(int i = 0; i < blogaBetBets.size(); i++){
//			Bet bet = blogaBetBets.get(i);
//			
//			// Some objects were saved to our SQL database as JSON string
//			// We have to convert them to objects again
//			Record record = (Record)gson.fromJson(bet.getRecordJsonString(), recordClass);
//			SoccerEvent event = (SoccerEvent)gson.fromJson(bet.getEventJsonString(), eventClass);
//			
//			// The tip, its a different class than a betAdvisor tip
//			// Some variables also have difefrent names and possible values
//			// Conversion from String to double
//			String tipJsonString = bet.getTipJsonString();
//			int startStake = tipJsonString.indexOf("\"stake\"") + 9;
//			int stakeEnd = tipJsonString.indexOf("\"", startStake);
//			String stakeString = tipJsonString.substring(startStake, stakeEnd);
//			int splitPoint = stakeString.indexOf("/");
//			String a = stakeString.substring(0, splitPoint);
//			String b = stakeString.substring(splitPoint + 1);
//			double stake = Double.parseDouble(a) / Double.parseDouble(b);
//			tipJsonString = tipJsonString.replace(stakeString, stake + "");
//			
//			BlogaBetTip tip = (BlogaBetTip)gson.fromJson(tipJsonString, BlogaBetTip.class);
//			
//			BetTicket betTicket = (BetTicket)gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);
//		}
//	}

	public static void main(String[] args) throws Exception {
//		test();
	}
}
