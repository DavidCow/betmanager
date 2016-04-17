package blogaBetHistoricalDataParsing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.Normalizer;
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

import bettingBot.TeamMapping;


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
		            		String selection = Normalizer.normalize(tokens[7], Normalizer.Form.NFD);
		            		// bestOdds
		            		double bestOdds = Double.parseDouble(tokens[10]);
		            		// result
		            		String result = tokens[6];
							// pick	            		
		            		// Create element
		            		String host = parseHostFromEvent(event);
		            		String guest = parseGuestFromEvent(event);
		            		// type of bet
		            		String typeOfBet = parsetypeOfBetFromPick(selection, host, guest);
		            		// pivot value
		            		double pivotvalue = parsePivotValue(selection, typeOfBet, host, guest);
		            		// tip
		            		String tipTeam = parseTipTeam(selection, typeOfBet, host, guest);
		            		// pivot Bias
		            		String pivotBias = parsePivotBias(selection, typeOfBet, host, guest, pivotvalue);
		            		
		            		BlogaBetElement element = new BlogaBetElement(gameDate, publicationDate, sport, competition, event, tipster, selection, typeOfBet, bestOdds, result, host, guest, pivotvalue, tipTeam, pivotBias);
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
	
	public static String parsetypeOfBetFromPick(String pick, String host, String guest){
		String res = "UNKNOWN";
		String upperPick = pick.toUpperCase();
		String upperHost = host.toUpperCase();
		String upperGuest = guest.toUpperCase();
		
		if(upperPick.contains("(AH)")){
			res = "Asian Handicap";
		}
		else if(upperPick.contains("ASIAN HANDICAP")){
			res = "Asian Handicap";
		}
		else if(upperPick.contains(" AH)")){
			res = "Asian Handicap";
		}
		else if(upperPick.contains("HANDICAP")){
			res = "Asian Handicap";
		}
		else if(upperPick.contains("HOME +") || upperPick.contains("HOME -") || upperPick.contains("HOME 0")){
			res = "Asian Handicap";
		}
		else if(upperPick.contains("AWAY +") || upperPick.contains("AWAY -") || upperPick.contains("AWAY 0")){
			res = "Asian Handicap";
		}
		else if(upperPick.contains("O/U")){
			if(upperPick.contains("CORNER")){
				res = "Over Under Corner";		
			}
			else if(upperPick.contains("BOOKINGS")){
				res = "Over Under Bookings";	
			}
			else if(upperPick.contains("HOME") || upperPick.contains("AWAY") || upperPick.contains(upperHost) || upperPick.contains(upperGuest)){
				res = "Over Under Team";		
			}
			else{
				res = "Over Under";			
			}
		}
		else if(upperPick.contains("OVER ")){
			if(upperPick.contains("CORNER")){
				res = "Over Under Corner";		
			}
			else if(upperPick.contains("BOOKINGS")){
				res = "Over Under Bookings";	
			}
			else if(upperPick.contains("HOME") || upperPick.contains("AWAY") || upperPick.contains(upperHost) || upperPick.contains(upperGuest)){
				res = "Over Under Team";		
			}
			else{
				res = "Over Under";			
			}	
		}
		else if(upperPick.contains("OVER(?!TIME)")){
			if(upperPick.contains("CORNER")){
				res = "Over Under Corner";		
			}
			else if(upperPick.contains("BOOKINGS")){
				res = "Over Under Bookings";	
			}
			else if(upperPick.contains("HOME") || upperPick.contains("AWAY") || upperPick.contains(upperHost) || upperPick.contains(upperGuest)){
				res = "Over Under Team";		
			}
			else{
				res = "Over Under";			
			}
		}
		else if(upperPick.contains("UNDER")){
			if(upperPick.contains("CORNER")){
				res = "Over Under Corner";		
			}
			else if(upperPick.contains("BOOKINGS")){
				res = "Over Under Bookings";	
			}
			else if(upperPick.contains("HOME") || upperPick.contains("AWAY") || upperPick.contains(upperHost) || upperPick.contains(upperGuest)){
				res = "Over Under Team";		
			}
			else{
				res = "Over Under";			
			}
		}
		else if(upperPick.contains("1X2")){
			res = "Match Odds";
		}
		else if(upperPick.equalsIgnoreCase("DRAW")){
			res = "Match Odds";
		}
		else if(upperPick.equalsIgnoreCase("HOME") || upperPick.equalsIgnoreCase("AWAY")){
			res = "Match Odds";
		}
		else if(upperPick.equalsIgnoreCase("HOME (MATCH)") || upperPick.equalsIgnoreCase("AWAY (MATCH)") || upperPick.equalsIgnoreCase("DRAW (MATCH)")){
			res = "Match Odds";
		}
		else if(upperPick.indexOf("FT HOME") == 0 || upperPick.indexOf("FT AWAY") == 0 || upperPick.indexOf("FT DRAW") == 0){
			res = "Match Odds";
		}
		else if(TeamMapping.teamsMatch(upperPick, upperHost) || TeamMapping.teamsMatch(upperPick, upperGuest)){
			if(upperPick.contains("(-") || upperPick.contains("(+")){
				res = "Asian Handicap";
			}
			else{
				res = "Match Odds";		
			}
		}
		else{
			res = "INVALID";
		}
		
		// halftime
		if(upperPick.indexOf("HT ") == 0){
			res += " Half Time";
		}
		if(upperPick.contains("HALF TIME")){
			res += " Half Time";
		}
		if(upperPick.contains("HALF)")){
			res += " Half Time";
		}
		if(upperPick.contains("FIRST HALF")){
			res += " Half Time";
		}
		if(upperPick.contains("HALFTIME")){
			res += " Half Time";
		}
	
		return res;
	}
	
	public static String parsePivotBias(String selection, String typeOfBet, String host, String guest, double pivotValue){
		String res = "NEUTRAL";
		if(typeOfBet.equals("Asian Handicap") && pivotValue != 0){
			res = "?";
			String upperSelection = selection.toUpperCase();
			String upperHost = host.toUpperCase();
			String upperGuest = guest.toUpperCase();
			if(upperSelection.indexOf("HOME") != -1){
				if(upperSelection.contains("+")){
					res = "GUEST";
				}
				else if(upperSelection.contains("-")){
					res = "HOST";
				}
			}
			else if(upperSelection.indexOf("GUEST") != -1){
				if(upperSelection.contains("+")){
					res = "HOST";
				}
				else if(upperSelection.contains("-")){
					res = "GUEST";
				}
			}
			else if(upperSelection.indexOf("AWAY") != -1){
				if(upperSelection.contains("+")){
					res = "HOST";
				}
				else if(upperSelection.contains("-")){
					res = "GUEST";
				}
			}
			else if(upperSelection.indexOf(" 1") != -1){
				if(upperSelection.contains("+")){
					res = "GUEST";
				}
				else if(upperSelection.contains("-")){
					res = "HOST";
				}
			}
			else if(upperSelection.indexOf(" 2") != -1){
				if(upperSelection.contains("+")){
					res = "HOST";
				}
				else if(upperSelection.contains("-")){
					res = "GUEST";
				}
			}
			else if(upperSelection.indexOf(upperHost) != -1){
				if(upperSelection.contains("+")){
					res = "GUEST";
				}
				else if(upperSelection.contains("-")){
					res = "HOST";
				}
			}
			else if(upperSelection.indexOf(upperGuest) != -1){
				if(upperSelection.contains("+")){
					res = "HOST";
				}
				else if(upperSelection.contains("-")){
					res = "GUEST";
				}
			}
			else if(TeamMapping.teamsMatch(upperSelection, upperHost)){
				if(upperSelection.contains("+")){
					res = "GUEST";
				}
				else if(upperSelection.contains("-")){
					res = "HOST";
				}
			}
			else if(TeamMapping.teamsMatch(upperSelection, upperGuest)){
				if(upperSelection.contains("+")){
					res = "host";
				}
				else if(upperSelection.contains("-")){
					res = "guest";
				}
			}			
		}
		return res;
	}
	
	public static double parsePivotValue(String selection, String typeOfBet, String host, String guest){
		String upperSelection = selection.toUpperCase();
		String upperHost = host.toUpperCase();
		String upperGuest = guest.toUpperCase();
		double pivotValue = -10;
		
		if(typeOfBet.indexOf("Match Odds") == 0){
			return 0;
		}
		if(typeOfBet.indexOf("Over Under") == 0){
			try{
				int startIndex = upperSelection.indexOf("OVER ");
				int overUnderIndex = upperSelection.indexOf("OVER UNDER");
				if(startIndex != -1 && startIndex != overUnderIndex){
					startIndex += 5;
					int endIndex = upperSelection.indexOf(" ", startIndex);
					if(endIndex != -1){
						String pivotValueString = upperSelection.substring(startIndex, endIndex);
						pivotValue = Double.parseDouble(pivotValueString);
					}
					else{
						String pivotValueString = upperSelection.substring(startIndex);
						pivotValue = Double.parseDouble(pivotValueString);	
					}
				}
				else{
					startIndex = upperSelection.indexOf("UNDER ");
					if(startIndex != -1){
						startIndex += 6;
						int endIndex = upperSelection.indexOf(" ", startIndex);
						if(endIndex != -1){
							String pivotValueString = upperSelection.substring(startIndex, endIndex);
							pivotValue = Double.parseDouble(pivotValueString);
						}
						else{
							String pivotValueString = upperSelection.substring(startIndex);
							pivotValue = Double.parseDouble(pivotValueString);	
						}
					}	
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		if(typeOfBet.indexOf("Asian Handicap") == 0){
			if(upperSelection.contains(" 0 ")){
				pivotValue = 0;
			}
			else{
				int startIndex = upperSelection.indexOf("+");
				if(startIndex != -1){
					startIndex += 1;
					int endIndex = upperSelection.indexOf(" ", startIndex);
					if(endIndex != -1){
						String pivotValueString = upperSelection.substring(startIndex, endIndex);
						pivotValue = Double.parseDouble(pivotValueString);
						return pivotValue;
					}
					else{
						endIndex = upperSelection.indexOf(")", startIndex);
						if(endIndex != -1){
							String pivotValueString = upperSelection.substring(startIndex, endIndex);
							pivotValue = Double.parseDouble(pivotValueString);
							return pivotValue;
						}	
					}
				}
				else{
					startIndex = upperSelection.indexOf("-");
					if(startIndex != -1){
						startIndex += 1;
						int endIndex = upperSelection.indexOf(" ", startIndex);
						if(endIndex != -1){
							String pivotValueString = upperSelection.substring(startIndex, endIndex);
							pivotValue = Double.parseDouble(pivotValueString);
							return pivotValue;
						}
						else{
							endIndex = upperSelection.indexOf(")", startIndex);
							if(endIndex != -1){
								String pivotValueString = upperSelection.substring(startIndex, endIndex);
								pivotValue = Double.parseDouble(pivotValueString);
								return pivotValue;
							}	
						}
					}					
				}
			}			
		}
	
		if(pivotValue > 10){
			pivotValue = -10;
		}
		return pivotValue;
	}
	
	public static String parseTipTeam(String selection, String typeOfBet, String host, String guest){
		String res = "INVALID";
		String upperSelection = selection.toUpperCase();
		String upperHost = host.toUpperCase();
		String upperGuest = guest.toUpperCase();
		
		if(typeOfBet.indexOf("Match Odds") == 0){
			if(upperSelection.indexOf("DRAW") != -1){
				res = "Draw";
			}
			if(upperSelection.indexOf(" X ") != -1){
				res = "Draw";
			}
			else if(upperSelection.indexOf("HOME") != -1){
				res = host;
			}
			else if(upperSelection.indexOf("GUEST") != -1){
				res = guest;
			}
			else if(upperSelection.indexOf("AWAY") != -1){
				res = guest;
			}
			else if(upperSelection.indexOf(upperHost) != -1){
				res = host;
			}
			else if(upperSelection.indexOf(upperGuest) != -1){
				res = guest;
			}
			else if(TeamMapping.teamsMatch(upperSelection, upperHost)){
				res = host;
			}
			else if(TeamMapping.teamsMatch(upperSelection, upperGuest)){
				res = guest;
			}
		}
		if(typeOfBet.indexOf("Over Under") == 0){
			upperSelection = upperSelection.replaceAll("OVERTIME", "");
			upperSelection = upperSelection.replaceAll("OVER/UNDER", "");
			upperSelection = upperSelection.replaceAll("OVER UNDER", "");
			int overIndex = upperSelection.indexOf("OVER");
			int underIndex = upperSelection.indexOf("UNDER");
			
			if(overIndex != -1 && underIndex == -1)
				res = "Over";
			else if(overIndex == -1 && underIndex != -1)
				res = "Under";		
		}
		if(typeOfBet.indexOf("Asian Handicap") == 0){
			if(upperSelection.indexOf("HOME") != -1){
				res = host;
			}
			else if(upperSelection.indexOf("GUEST") != -1){
				res = guest;
			}
			else if(upperSelection.indexOf("AWAY") != -1){
				res = guest;
			}
			else if(upperSelection.indexOf(" 1") != -1){
				res = host;
			}
			else if(upperSelection.indexOf(" 2") != -1){
				res = guest;
			}
			else if(upperSelection.indexOf(upperHost) != -1){
				res = host;
			}
			else if(upperSelection.indexOf(upperGuest) != -1){
				res = guest;
			}
			else if(TeamMapping.teamsMatch(upperSelection, upperHost)){
				res = host;
			}
			else if(TeamMapping.teamsMatch(upperSelection, upperGuest)){
				res = guest;
			}
		}
		if(typeOfBet.indexOf("Asian Handicap") == 0){
			if(res.equals("INVALID")){
				System.out.println();
			}
		}
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
		int numberOfValid = 0;
		int numberOfInvalid = 0;
		int numberOfAsianHandicap = 0;
		int numberOfMatchOdds = 0;
		int numberOfOverUnder = 0;
		int numberOfOverUnderTeam = 0;
		int numberOfOverUnderCorner = 0;
		int numberOfOverUnderBookings = 0;
		int numberOfIncorrectPivotValues = 0;
		for(int i = 0; i < l.size(); i++){
			BlogaBetElement e = l.get(i);
			if(e.getTypeOfBet().equals("INVALID")){
				numberOfInvalid++;
			}
			else{
				numberOfValid++;
			}
			if(e.getTypeOfBet().equals("Over Under")){
				numberOfAsianHandicap++;
				if(e.getPivotValue() == -10){
					numberOfIncorrectPivotValues++;
				}
			}
			if(e.getTypeOfBet().equals("Match Odds")){
				numberOfMatchOdds++;
			}
			if(e.getTypeOfBet().equals("Over Under")){
				numberOfOverUnder++;
			}
			if(e.getTypeOfBet().equals("Over Under Team")){
				numberOfOverUnderTeam++;
			}
			if(e.getTypeOfBet().equals("Over Under Corner")){
				numberOfOverUnderCorner++;
			}
			if(e.getTypeOfBet().equals("Over Under Bookings")){
				numberOfOverUnderBookings++;
			}
		}
		System.out.println("Valid: " + numberOfValid);
		System.out.println("INVALID: " + numberOfInvalid);
		System.out.println("Asian Handicap: " + numberOfAsianHandicap);
		System.out.println("Match Odds: " + numberOfMatchOdds);
		System.out.println("Over Under: " + numberOfOverUnder);
		System.out.println("Over Under Team: " + numberOfOverUnderTeam);
		System.out.println("Over Under Corner: " + numberOfOverUnderCorner);
		System.out.println("Over Under Bookings: " + numberOfOverUnderBookings);
		System.out.println("numberOfIncorrectPivotValues: " + numberOfIncorrectPivotValues);
		
		File f = new File("blogaBetParsing.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		for(int i = 0; i < l.size(); i++){
			BlogaBetElement e = l.get(i);
			if(!e.getTypeOfBet().equals("INVALID")){
				writer.write(e.getSelection() + "\n");
			}
		}
		int b = 12;
	}
}
