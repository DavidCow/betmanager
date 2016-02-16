package backtest;

import historicalData.HistoricalDataElement;
import historicalData.HistoricalDataParser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import betadvisor.BetAdvisorComparator;
import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;

public class ElementMatcher {

	void matchElements() throws IOException, ParseException{
		double ev = 0;
		
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
			if(y == 2014){
				if(date.getMonth() == 1){
					endI = i;
					break;
				}
			}
		}
		
		HistoricalDataParser historicalDataParser = new HistoricalDataParser();
		List<HistoricalDataElement> historicalDataList = historicalDataParser.parseFile("C:\\Users\\Patryk\\Desktop\\HistoricalData\\ChangesOnly\\data\\pendingFull-results_20140101_20140201.xml");
		
		int tipps = 0;
		for(int i = startI; i < endI; i++){
			int matches = 0;
			int hostMatches = 0;
			String betAdvisorHost = "";
			String betAdvisorGuest = "";
			
			String historicalDataHost = "";
			String historicalDataGuest = "";
			for(int j = 0; j < historicalDataList.size(); j++){
				DateFormat gmtFormat = new SimpleDateFormat();
				TimeZone gmtTime = TimeZone.getTimeZone("GMT");
				gmtFormat.setTimeZone(gmtTime);
				
				Date betAdvisorDate = betAdvisorList.get(i).getGameDate();
				BetAdvisorElement e0 = betAdvisorList.get(i);
				String s0 = betAdvisorDate.toGMTString();
				
				Date historicalDataDate = historicalDataList.get(j).getStartDate();
				HistoricalDataElement e1 = historicalDataList.get(j);
				String s1 = historicalDataDate.toGMTString();
				
				if(s0.equals(s1)){
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
						double win = e0.getProfit() / e0.getTake() * 100;
						ev += win;
						//break;
						if(hostMatches == 2){
							int kek = 12;
							int b = kek;
						}
					}
					else if(betAdvisorGuest.equalsIgnoreCase(historicalDataGuest)){
						hostMatches++;
						double win = e0.getProfit() / e0.getTake() * 100;
						ev += win;
						break;
					}
					else if(betAdvisorLeague.equals("International Friendly Games")){
						if(betAdvisorHost.equalsIgnoreCase(historicalDataGuest)){
							hostMatches++;
							double win = e0.getProfit() / e0.getTake() * 100;
							ev += win;
							break;
						}
					}
					
					matches++;
					int kek = 12;
					int b = kek;
				}
			}
			if(matches != 0){
				if(hostMatches == 0){
					int kek = 12;
					int b = kek;
				}
				else
					tipps++;
				System.out.println(hostMatches);
			}
		}
		System.out.println("Tipps: " + tipps);
		System.out.println("EV: " + ev);
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		ElementMatcher matcher = new ElementMatcher();
		matcher.matchElements();
	}
}
