package betadvisor;

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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * 
 * @author Patryk Hopner
 *
 * This class parses all BetAdvicor csv sheets in a folder
 */
public class BetAdvisorParser {

	
	/**
	 * 
	 * @param folderPath the path to the folder where the csv files are stored
	 * @return a List with all the betadvisor tipster data
	 * @throws IOException
	 * 
	 * This method reads all the tipster data in the folder, each row in the csv file is a BetAdvisorElement in the List
	 * If a line contains invalid data, it will be skipped
	 */
	public List<BetAdvisorElement> parseSheets(String folderPath) throws IOException{
		List<BetAdvisorElement> res = new ArrayList<BetAdvisorElement>();
		//Create the CSVFormat object with the header mapping
		String[] fileHeaderMapping = {"gameDade", "category", "league", "event", "eventResult", "typeOfBet", "selection", "odds", "bookMaker", "take", "profit", "result", "tipster", "sport", "publicationDate"};
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
		            		// gameDate
		            		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		            		Date gameDate = format.parse(tokens[0]);
		            		// category
		            		String category = tokens[1];
		            		// league
		            		String league = tokens[2];
		            		// event
		            		String event = tokens[3];
		            		// eventResult
		            		String eventResult = tokens[4];
		            		// typeOfBet
		            		String typeOfBet = tokens[5];
		            		// selection
		            		String selection = tokens[6];
		            		// odds
		            		double odds = Double.parseDouble(tokens[7]);
		            		// bookMaker
		            		String bookMaker = tokens[8];
		            		// take
		            		double take = Double.parseDouble(tokens[9]);
		            		// profit
		            		double profit = Double.parseDouble(tokens[10]);
		            		// result
		            		String result = tokens[11];
		            		// tipster
		            		String tipster = tokens[12];
		            		// sport
		            		String sport = tokens[13];
		            		// publicationDate
		            		if(tokens[14].length() == 0){
		            			int kek = 12;
		            			int b = kek;
		            		}
		            		Date publicationDate = format.parse(tokens[14]);
		            		
		            		// Create element
		            		BetAdvisorElement element = new BetAdvisorElement(gameDate, category, league, event, eventResult, 
		            				                                          typeOfBet, selection, odds, bookMaker, take, profit, result, tipster, sport, publicationDate);
		            		
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
		return res;
	}
	
	public static void main(String[] args) throws IOException {
		BetAdvisorParser parser = new BetAdvisorParser();
		List<BetAdvisorElement> l = parser.parseSheets("TipsterData/csv");
		Collections.sort(l, new BetAdvisorComparator());
		int b = 12;
	}
}
