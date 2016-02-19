package historicalData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricalDataParser {
	
	public List<HistoricalDataElement> parseFilesInFolder(String folderPath, String filterString){
		List<HistoricalDataElement> res = new ArrayList<HistoricalDataElement>();
		try {
			Files.walk(Paths.get(folderPath)).forEach(filePath -> {
			    if (Files.isRegularFile(filePath) && filePath.toString().indexOf(filterString) != -1){
			    	List<HistoricalDataElement> l = parseFile(filePath.toString());
			    	res.addAll(l);
			    }
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Sort the list
		Collections.sort(res, new HistoricalDataComparator());		
		return res;
	}
	
	public List<HistoricalDataElement> parseFile(String path){
		List<HistoricalDataElement> res = new ArrayList<HistoricalDataElement>();
		Map<HistoricalDataElement, HistoricalDataElement> map = new HashMap<HistoricalDataElement, HistoricalDataElement>();
		
		try(BufferedReader reader = new BufferedReader(new FileReader(path))){
			String line = reader.readLine();
			int lineCount = 0;
			int matchCount = 0;
			HistoricalDataElement element = null;
			List<OneTwoElement> oneTwoList = null;
			List<TotalElement> totalList = null;
			
			while(line != null){
				if(line.trim().indexOf("<Match") == 0){
					oneTwoList = new ArrayList<OneTwoElement>();
					totalList = new ArrayList<TotalElement>();
					int leagueStart = line.indexOf("League=") + 8;
					int leagueEnd = line.indexOf("\"", leagueStart);
					String league = line.substring(leagueStart, leagueEnd);
					
					int startTimeStart = line.indexOf("StartTime") + 11;
					int startTimeEnd = line.indexOf("\"", startTimeStart);
					String startTime = line.substring(startTimeStart, startTimeEnd);
					
					DateFormat dF = new SimpleDateFormat("yyyy-MM-dd HH:mm XXX");
					
					Date startDate = null;
					
					try{
						// Postprocess some dates that have a wrong format
						int startTimezone = startTime.lastIndexOf(" ") + 1;
						String timeZoneString = startTime.substring(startTimezone);
						if(timeZoneString.indexOf(":") == -1){
							String newTimezoneString = timeZoneString.substring(0, 1) + "0" + timeZoneString.substring(1, 2) + ":00";
							startTime = startTime.replace(timeZoneString, newTimezoneString);
						}
						
						startDate = dF.parse(startTime);
					}catch(Exception e){
						// If we can not parse the date, we set it to zero and filter those elements out later
						startDate = new Date(0);
					}
					
					int hostStart = line.indexOf("Host") + 6;
					int hostEnd = line.indexOf("\"", hostStart);		
					String host = line.substring(hostStart, hostEnd);
					
					int guestStart = line.indexOf("Guest") + 7;
					int guestEnd = line.indexOf("\"", guestStart);		
					String guest = line.substring(guestStart, guestEnd);	
					
					int sourceStart = line.indexOf("Source") + 8;
					int sourceEnd = line.indexOf("\"", sourceStart);		
					String source = line.substring(sourceStart, sourceEnd);				
					
					//System.out.println(startTime + " , " + startDate.toString());
					element = new HistoricalDataElement(startDate, league, host, guest, source);
				}
				else if(line.trim().indexOf("<Rec one=") == 0){
					int oneStart = line.indexOf("one") + 5;
					int oneEnd = line.indexOf("\"", oneStart);		
					String oneString = line.substring(oneStart, oneEnd);	
					double one = Double.parseDouble(oneString);
					
					int twoStart = line.indexOf("two") + 5;
					int twoEnd = line.indexOf("\"", twoStart);		
					String twoString = line.substring(twoStart, twoEnd);	
					double two = Double.parseDouble(twoString);
					
					int drawStart = line.indexOf("draw") + 6;
					int drawEnd = line.indexOf("\"", drawStart);		
					String drawString = line.substring(drawStart, drawEnd);	
					double draw = Double.parseDouble(drawString);
					
					int timeStart = line.indexOf("time") + 6;
					int timeEnd = line.indexOf("\"", timeStart);		
					String timeString = line.substring(timeStart, timeEnd);	
					long time = Long.parseLong(timeString);
					
					OneTwoElement oneTwoElement = new OneTwoElement(one, two, draw, time);
					oneTwoList.add(oneTwoElement);
				}
				else if(line.trim().indexOf("<Rec over=") == 0){
					int overStart = line.indexOf("over") + 6;
					int overEnd = line.indexOf("\"", overStart);		
					String overString = line.substring(overStart, overEnd);	
					double over = Double.parseDouble(overString);
					
					int underStart = line.indexOf("under") + 7;
					int underEnd = line.indexOf("\"", underStart);		
					String underString = line.substring(underStart, underEnd);	
					double under = Double.parseDouble(underString);
					
					int totalStart = line.indexOf("total") + 7;
					int totalEnd = line.indexOf("\"", totalStart);		
					String totalString = line.substring(totalStart, totalEnd);	
					double total = Double.parseDouble(totalString);
					
					int timeStart = line.indexOf("time") + 6;
					int timeEnd = line.indexOf("\"", timeStart);		
					String timeString = line.substring(timeStart, timeEnd);	
					long time = Long.parseLong(timeString);
					
					TotalElement totalElement = new TotalElement(over, under, total, time);
					totalList.add(totalElement);
				}
				else if(line.trim().indexOf("</Match") == 0){
					if(map.containsKey(element)){
						HistoricalDataElement e = map.get(element);
						for(int i = 0; i < oneTwoList.size(); i++){
							e.getOneTwoList().add(oneTwoList.get(i));
						}
						for(int i = 0; i < totalList.size(); i++){
							e.getTotalList().add(totalList.get(i));
						}
					}
					else{
						element.setOneTwoList(oneTwoList);
						element.setTotalList(totalList);
						res.add(element);
						map.put(element, element);
						matchCount++;
					}
				}
				line = reader.readLine();
				lineCount++;
			}	
			System.out.println("Lines: " + lineCount + " Matches: " + matchCount);	
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// Postprocessing
		for(int i = 0; i < res.size(); i++){
			// Filter out games with wrong date
			long startDate = res.get(i).getStartDate().getTime();
			if(startDate == 0){
				res.remove(i);
				i--;
				continue;
			}
			
			HistoricalDataElement e = res.get(i);
			List<OneTwoElement> l = e.getOneTwoList();
			// Sort odds
			Collections.sort(l, new OddsElementComparator());
			// Check for wrong data and remove it
			for(int j = 0; j < l.size(); j++){
				double one = l.get(j).getOne();
				double two = l.get(j).getTwo();
				double draw = l.get(j).getDraw();
				if(one == 0 || draw == 0 || two == 0){
					l.remove(j);
					j--;
				}
			}
			for(int j = 0; j < l.size() - 1; j++){
				double one = l.get(j).getOne();
				double two = l.get(j).getTwo();
				double draw = l.get(j).getDraw();
				if(one == 0 || draw == 0 || two == 0){
					System.out.println("Error");
				}
			}
			// Check for duplicates
			for(int j = 0; j < l.size() - 1; j++){
				long t0 = l.get(j).getTime();
				long t1 = l.get(j + 1).getTime();
				
				double one0 = l.get(j).getOne();
				double one1 = l.get(j + 1).getOne();
				
				double two0 = l.get(j).getTwo();
				double two1 = l.get(j + 1).getTwo();
				
				double draw0 = l.get(j).getDraw();
				double draw1 = l.get(j + 1).getDraw();
				if(t0 == t1){
//					if(!(one0 == one1 && two0 == two1 && draw0 == draw1)){
//						System.out.println(e.getHost() + " vs " + e.getGuest());	
//						System.out.println(one0 + " " + one1);
//						System.out.println(draw0 + " " + draw1);
//						System.out.println(two0 + " " + two1);
//						System.out.println();
//					}
				}
			}
			List<TotalElement> l2 = e.getTotalList();
			// Sort odds
			Collections.sort(l2, new OddsElementComparator());
			// Check for wrong data and remove it
			for(int j = 0; j < l2.size(); j++){
				double over = l2.get(j).getOver();
				double under = l2.get(j).getUnder();
				double total = l2.get(j).getTotal();
				if(over == 0 || under == 0 || total == 0){
					l2.remove(j);
					j--;
				}
			}
			for(int j = 0; j < l2.size(); j++){
				double over = l2.get(j).getOver();
				double under = l2.get(j).getUnder();
				double total = l2.get(j).getTotal();
				if(over == 0 || under == 0 || total == 0){
					System.out.println("Error");
				}
			}
		}
		// Sort the list
		Collections.sort(res, new HistoricalDataComparator());
		
		return res;
	}
	
	public static void main(String[] args) {
		HistoricalDataParser parser = new HistoricalDataParser();
		List<HistoricalDataElement> l = parser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\pending", "pendingFull");
		for(int i = 0; i < l.size() - 1; i++){
			long t0 = l.get(i).getStartDate().getTime();
			long t1 = l.get(i + 1).getStartDate().getTime();
			if(t0 > t1){
				System.out.println("Error");
			}
		}
	}
}
