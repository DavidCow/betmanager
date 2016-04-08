package backtest;

import historicalData.HistoricalDataElement;
import historicalData.HistoricalDataParser;

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

import betadvisor.BetAdvisorElement;
import bettingBot.TeamMapping;
import blogaBetHistoricalDataParsing.BlogaBetComparator;
import blogaBetHistoricalDataParsing.BlogaBetElement;
import blogaBetHistoricalDataParsing.BlogaBetParser;

public class BlogaBetBacktest {

	public void runBacktest() throws IOException{
		
		BlogaBetParser parser = new BlogaBetParser();
		List<BlogaBetElement> blogaBetList = parser.parseSheets("blogaBetTipsterData/csv");
		Collections.sort(blogaBetList, new BlogaBetComparator());
		
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
		
		int numberOfTimeMatches = 0;
		int numberOfTeamMatches = 0;
		
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
			
			int matches = 0;
			int hostMatches = 0;
			String blogaBetHost = tipp.getHost();
			String blogaBetGuest = tipp.getGuest();
			
			String historicalDataHost = "";
			String historicalDataGuest = "";
			
			Date blogaBetGameDate = tipp.getGameDate();
			String s0 = blogaBetGameDate.toGMTString();
			
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
						numberOfTeamMatches++;
						System.out.println(numberOfTeamMatches);
						break;
					}
				}	
			}			
		}
		System.out.println("Time Matches: " + numberOfTimeMatches);
		System.out.println("Team Matches: " + numberOfTeamMatches);
	}
	
	public static void main(String[] args) throws IOException {
		BlogaBetBacktest backtest = new BlogaBetBacktest();
		backtest.runBacktest();
	}
}
