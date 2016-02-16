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
			
			while(line != null){
				if(line.trim().indexOf("<Match") == 0){
					oneTwoList = new ArrayList<OneTwoElement>();
					int leagueStart = line.indexOf("League=") + 8;
					int leagueEnd = line.indexOf("\"", leagueStart);
					String league = line.substring(leagueStart, leagueEnd);
					
					int startTimeStart = line.indexOf("StartTime") + 11;
					int startTimeEnd = line.indexOf("\"", startTimeStart);
					String startTime = line.substring(startTimeStart, startTimeEnd);
					DateFormat dF = new SimpleDateFormat("yyyy-MM-dd HH:mm XXX");
					Date startDate = dF.parse(startTime);
					
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
				else if(line.trim().indexOf("</Match") == 0){
					if(map.containsKey(element)){
						HistoricalDataElement e = map.get(element);
						for(int i = 0; i < oneTwoList.size(); i++){
							e.getOneTwoList().add(oneTwoList.get(i));
						}
					}
					else{
						element.setOneTwoList(oneTwoList);
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
		// Sort odds
		for(int i = 0; i < res.size(); i++){
			HistoricalDataElement e = res.get(i);
			List<OneTwoElement> l = e.getOneTwoList();
			Collections.sort(l, new OddsElementComparator());
			for(int j = 0; j < l.size() - 1; j++){
				long t0 = l.get(j).getTime();
				long t1 = l.get(j + 1).getTime();
				if(t0 == t1){
					System.out.println("Duplicate");
				}
			}
		}
		// Sort the list
		Collections.sort(res, new HistoricalDataComparator());
		
		return res;
	}
	
	public static void main(String[] args) {
		HistoricalDataParser parser = new HistoricalDataParser();
		List<HistoricalDataElement> l = parser.parseFilesInFolder("C:\\Users\\Patryk\\Desktop\\HistoricalData\\ChangesOnly\\data", "pendingFull");
		int kek = 21;
		int b = kek;
	}
}
