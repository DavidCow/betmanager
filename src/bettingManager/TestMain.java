package bettingManager;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;
import bettingBot.entities.BetComparator;
import blogaBetHistoricalDataParsing.BlogaBetComparator;
import blogaBetHistoricalDataParsing.BlogaBetElement;
import blogaBetHistoricalDataParsing.BlogaBetParser;

public class TestMain {
	
	private static final String blogaBetResultPath = "blogaBetRealBets.dat";
	private static final String betAdvisorResultPath = "betAdvisorRealBets.dat";
	private static final String blogaBetBackTesttPath = "blogaBetBackTestBets.dat";
	private static final String betAdvisorBackTestPath = "betAdvisorBackTestBets.dat";
	
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
	
	public static void test() throws Exception{
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		BlogaBetParser parser = new BlogaBetParser();
		List<BlogaBetElement> blogaBetList = parser.parseSheets("blogaBetTipsterData/csv");
	}

	public static void main(String[] args) {
		
	}
}
