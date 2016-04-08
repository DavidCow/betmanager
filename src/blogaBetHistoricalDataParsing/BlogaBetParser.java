package blogaBetHistoricalDataParsing;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


public class BlogaBetParser {
	/**
	 * 
	 * @param folderPath the path to the folder where the csv files are stored
	 * @return a List with all the betadvisor tipster data
	 * @throws IOException
	 * 
	 * This method reads all the tipster data in the folder, each row in the csv file is a BetAdvisorElement in the List
	 * If a line contains invalid data, it will be skipped
	 */
	public List<BlogaBetElement> parseSheets(String folderPath) throws IOException{
		List<BlogaBetElement> res = new ArrayList<BlogaBetElement>();
		//Create the CSVFormat object with the header mapping
		String[] fileHeaderMapping = {"count", "Game date", "Sport", "Competition", "Bet", "Return", "Result", "Pick", "Score", "Bookmaker", "Odd", "Stake", "Profit", "Tipster", "CST"};
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(fileHeaderMapping);
	
		
		Files.walk(Paths.get(folderPath)).forEach(filePath -> {
		    if (Files.isRegularFile(filePath)) {
		        System.out.println(filePath);
	        
		        try(CSVParser csvFileParser  = new CSVParser(new FileReader(filePath.toString()), csvFileFormat)){
		        	List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
		            for (int i = 1; i < csvRecords.size(); i++) {
		            	try{
		            		CSVRecord record = csvRecords.get(i);
			            	String[] tokens = new String[15];
			            	for(int j = 0; j < 15; j++){
			            		tokens[j] = record.get(j);
			            	}
		            		// date format
			            	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		            		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		            		// game date
		            		String gameDateString = tokens[1];
		            		Date gameDate = format.parse(gameDateString);		
		            		// publication date
		            		String publicationDateString = tokens[14];
		            		Date publicationDate = format.parse(publicationDateString);		
		            		// sport
		            		String sport = tokens[2];
		            		// competition
		            		String competition = tokens[3];
		            		// event
		            		String event = tokens[4];
		            		// tipster
		            		String tipster = tokens[13];
		            		// selection
		            		String selection = tokens[7];
		            		// type of bet
		            		String typeOfBet = "TYPE OF BET";
		            		// bestOdds
		            		double bestOdds = Double.parseDouble(tokens[10]);
		            		// result
		            		String result = tokens[6];
							// pick	            		
		            		// Create element
		            		String host = parseHostFromEvent(event);
		            		String guest = parseGuestFromEvent(event);
		            		
		            		BlogaBetElement element = new BlogaBetElement(gameDate, publicationDate, sport, competition, event, tipster, selection, typeOfBet, bestOdds, result, host, guest);
		            		res.add(element);
           		
		            	}catch(Exception e){
		            		
		            	}		            
		            }
		        }
		        catch(Exception e){
		        	e.printStackTrace();
		        }
		    }
		});		
		Collections.sort(res, new BlogaBetComparator());
		return res;
	}
	
	public static String parseHostFromEvent(String eventString){
		int endIndex = eventString.indexOf(" - ");
		String hostString = eventString.substring(0, endIndex);
		return hostString;
	}
	
	public static String parseGuestFromEvent(String eventString){
		int startIndex = eventString.indexOf(" - ") + 3;
		String guestString = eventString.substring(startIndex);
		return guestString;
	}
	
	public static void main(String[] args) throws IOException {
		BlogaBetParser parser = new BlogaBetParser();
		List<BlogaBetElement> l = parser.parseSheets("blogaBetTipsterData/csv");
		Collections.sort(l, new BlogaBetComparator());
		int b = 12;
	}
}
