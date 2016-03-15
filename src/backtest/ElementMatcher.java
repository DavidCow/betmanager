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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import betadvisor.BetAdvisorComparator;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import bettingBot.TeamMapping;

public class ElementMatcher {

	void matchElements() throws IOException, ParseException{
		
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		Collections.sort(betAdvisorList, new BetAdvisorComparator());
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
			if(y == 2015 && date.getMonth() == 0){
				endI = i;
				break;
			}
		}
		
		//Try to load the historical data from an object stream or load it flom csv files otherwise
		List<HistoricalDataElement> historicalDataList = null;
		File historicalDataFile = new File("changesOnly.dat");
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
			System.out.println("Historical data loaded from CSV");
            FileOutputStream fileOutput = new FileOutputStream(historicalDataFile);
            BufferedOutputStream br = new BufferedOutputStream(fileOutput);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
            objectOutputStream.writeObject(historicalDataList);
            objectOutputStream.close();
		}
		
		int tipps = 0;
		int dateMatches = 0;
		// We dont have to loop over all historical data for every tipp, sonce some historical data will be
		// From games before the tipp
		int startJ = 0;
		boolean startJSet = false;
		for(int i = startI; i < endI; i++){
			startJSet = false;
			int matches = 0;
			int hostMatches = 0;
			String betAdvisorHost = "";
			String betAdvisorGuest = "";
			
			String historicalDataHost = "";
			String historicalDataGuest = "";
			
			List<HistoricalDataElement> availableBets = new ArrayList<HistoricalDataElement>();
			for(int j = startJ; j < historicalDataList.size(); j++){
				DateFormat gmtFormat = new SimpleDateFormat();
				TimeZone gmtTime = TimeZone.getTimeZone("GMT");
				gmtFormat.setTimeZone(gmtTime);
				
				Date betAdvisorDate = betAdvisorList.get(i).getGameDate();
				BetAdvisorElement e0 = betAdvisorList.get(i);
				String s0 = betAdvisorDate.toGMTString();
				long t0 = betAdvisorDate.getTime();
				
				Date historicalDataDate = historicalDataList.get(j).getStartDate();
				HistoricalDataElement e1 = historicalDataList.get(j);
				String s1 = historicalDataDate.toGMTString();
				long t1 = historicalDataDate.getTime();
				
				if(t0 == t1){
					if(!startJSet){
						startJSet = true;
						startJ = j;
					}					
					String betAdvisorLeague = e0.getLeague();
					String historicalDataLeague = e1.getLeague();
					//System.out.println(betAdvisorLeague + " , " + historicalDataLeague);
					
					betAdvisorHost = BetAdvisorParser.parseHostFromEvent(e0.getEvent());
					betAdvisorGuest = BetAdvisorParser.parseGuestFromEvent(e0.getEvent());
					
					historicalDataHost = e1.getHost();
					historicalDataGuest = e1.getGuest();
					matches++;
					
					//System.out.println(betAdvisorHost + " , " + historicalDataHost);	
					if(betAdvisorHost.equalsIgnoreCase(historicalDataHost)){
						hostMatches++;
					}
					else if(betAdvisorGuest.equalsIgnoreCase(historicalDataGuest)){
						hostMatches++;
					}
					else if(betAdvisorLeague.equals("International Friendly Games")){
						if(betAdvisorHost.equalsIgnoreCase(historicalDataGuest)){
							hostMatches++;
						}
					}	
					else if(TeamMapping.teamsMatch(betAdvisorHost, historicalDataHost)){
						hostMatches++;
					}
					else if(TeamMapping.teamsMatch(betAdvisorGuest, historicalDataGuest)){
						hostMatches++;
					}
					matches++;
				}
				if(t0 < t1){
					break;
				}
			}
			if(matches != 0){
				dateMatches++;
				if(hostMatches != 0){
					tipps++;
				}
			}
		}
		System.out.println("dateMatches: " + dateMatches);
		System.out.println("Tipps: " + tipps);
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		ElementMatcher matcher = new ElementMatcher();
		matcher.matchElements();
	}
}
