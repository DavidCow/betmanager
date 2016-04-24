package mailParsing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BlogaBetEmailParser {
	
	private static String oppositeString(String s){
		if(s.equalsIgnoreCase("HOME"))
			return "AWAY";
		else
			return "HOME";
	}
	
//	private static List<BlogaBetTip> parseComboBetTip(ParsedTextMail mail){
//		String content = mail.content;
//		
//	}
	
	public static BlogaBetTip parseEmail(ParsedTextMail mail){
		String s = mail.content.replaceAll("\r", "");
		BlogaBetTip tip = new BlogaBetTip();
		String[] lines = s.split("\n");
		
		//parse tipster
		tip.tipster = lines[1];
		
		//parse host and guest
		if(lines[4].contains(" - ")){
			tip.host = lines[4].replaceAll("-.*", "").trim();
			tip.guest = lines[4].replaceAll(".*-\\s?", "").trim();
		}
		else {
			tip.host = lines[4].replaceAll("\\sv\\s.*", "").trim();
			tip.guest = lines[4].replaceAll(".*\\sv\\s", "").trim();
		}

		
		//replace selected team name with home or guest
		if(lines[5].contains(tip.host))
			lines[5] = lines[5].replace(tip.host, "Home");
		if(lines[5].contains(tip.guest))
			lines[5] = lines[5].replace(tip.guest, "Away");
		
		/*Parse pivot type, value and bias
		 * Check type first, then parse value and bias
		 * 
		 */
		if(lines[5].contains("(AH)") || lines[5].contains("Asian") || lines[5].contains("1X2 HC")){
			//check handicap variant
//			if(lines[5].contains("1st Half"))
//				tip.pivotType = "Asian handicap 1st Half";
			if(lines[5].contains("Alternative"))
				tip.pivotType = "Asian handicap Alternative";
			else if(lines[5].contains("1X2 HC"))
				tip.pivotType = "1X2 HC";
			else
				tip.pivotType = "Asian handicap";
			
			tip.pivotValue = Double.parseDouble(lines[5].replaceAll("(.*\\s\\D?)(\\d+\\.?\\d*\\s)(.*@.*)", "$2").trim());
			
			if(lines[5].contains("Home") || lines[5].contains("HOME") || lines[5].contains("home"))
				tip.selection = "HOME";
			else if(lines[5].contains("Away") || lines[5].contains("AWAY") || lines[5].contains("away")) 
				tip.selection = "AWAY";
			
			if(tip.pivotValue>0){
				String bias = lines[5].replaceAll("(.*)(\\s.{1})(\\d+\\.\\d*)(.*@.*)", "$2").trim();
				if(bias.equalsIgnoreCase("-"))
					tip.pivotBias = tip.selection;
				else
					tip.pivotBias = oppositeString(tip.selection);
			}
		}
		else if(lines[5].contains("1X2") || lines[5].contains("(Win)")){
			tip.pivotType = "Match Odds";
			if(lines[5].contains("Home") || lines[5].contains("HOME") || lines[5].contains("home"))
				tip.selection = "HOME";
			else if(lines[5].contains("Away") || lines[5].contains("AWAY") || lines[5].contains("away")) 
				tip.selection = "AWAY";
			else if(lines[5].contains("Draw") || lines[5].contains("DRAW") || lines[5].contains("draw") || lines[5].contains("X"))
				tip.selection = "DRAW";
		}
		else if(lines[5].contains("O/U")){
			//check over under variants
			if(lines[5].contains("Team")){
				tip.pivotType = "Over / Under Team";
				if(lines[5].contains("Home") || lines[5].contains("HOME") || lines[5].contains("home"))
					tip.pivotBias = "HOME";
				else if(lines[5].contains("Away") || lines[5].contains("AWAY") || lines[5].contains("away")) 
					tip.pivotBias = "AWAY";
			}
			else if(lines[5].contains("Corners")){
				tip.pivotType = "Over / Under Corners";
			}
			else
				tip.pivotType = "Over / Under";
			
			tip.pivotValue = Double.parseDouble(lines[5].replaceAll("(.*\\s\\D?)(\\d+\\.?\\d*\\s)(.*@.*)", "$2").trim());
			if(lines[5].contains("Over") || lines[5].contains("OVER") || lines[5].contains("over"))
				tip.selection = "OVER";
			else if(lines[5].contains("Under") || lines[5].contains("UNDER") || lines[5].contains("under")) 
				tip.selection = "UNDER";
		}
		else if(lines[5].contains("Double Chance")){
			tip.pivotType = "Double Chance";
			if(lines[5].contains("X1") || lines[5].contains("1X"))
				tip.selection = "X1";
			else if(lines[5].contains("X2") || lines[5].contains("2X")) 
				tip.selection = "X2";
		}
		else if(lines[5].contains("(DNB)")){
			tip.pivotType = "DNB";
			if(lines[5].contains("Home") || lines[5].contains("HOME") || lines[5].contains("home"))
				tip.selection = "HOME";
			else if(lines[5].contains("Away") || lines[5].contains("AWAY") || lines[5].contains("away")) 
				tip.selection = "AWAY";
		}
		else if(lines[5].contains("Match Goals")){
			tip.pivotType = "Match Goals";
			tip.pivotValue = Double.parseDouble(lines[5].replaceAll("(.*\\s\\D?)(\\d+\\.?\\d*\\s)(.*@.*)", "$2").trim());
			if(lines[5].contains("Over") || lines[5].contains("OVER") || lines[5].contains("over"))
				tip.selection = "OVER";
			else if(lines[5].contains("Under") || lines[5].contains("UNDER") || lines[5].contains("under")) 
				tip.selection = "UNDER";
		}
		else if(lines[5].contains("Goal Line")){
			tip.pivotType = "Goal Line";
			tip.pivotValue = Double.parseDouble(lines[5].replaceAll("(.*\\s\\D?)(\\d+\\.?\\d*\\s)(.*@.*)", "$2").trim());
			if(lines[5].contains("Over") || lines[5].contains("OVER") || lines[5].contains("over"))
				tip.selection = "OVER";
			else if(lines[5].contains("Under") || lines[5].contains("UNDER") || lines[5].contains("under")) 
				tip.selection = "UNDER";
		}
		
		//parse odds
		tip.odds = Double.parseDouble(lines[5].replaceAll(".*@", "").trim());
		
		//parse units
		tip.stake = lines[6].replaceAll("(\\d+/\\d+)(.*)", "$1");
		
		//parse source
		String[] lineSixSplits = lines[6].split(" ");
		if(lineSixSplits[1].equalsIgnoreCase("Live"))
			tip.source = lineSixSplits[2];
		else 
			tip.source = lineSixSplits[1];
		
		//parse sport
		tip.sport = lines[7].replaceAll("\\s?/.*", "").trim();
		
		//parse country
		tip.country = lines[7].replaceAll("(.*/)(.*)(/.*)", "$2").trim();

		/* Parse date 
		 * 
		 * The timezone is CET
		 * Format is like: 01 Mar 2016, 20:45
		 */
		DateFormat format = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.UK);
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		String dateString = lines[7].replaceAll(".*off:", "").trim();
		Date date = null;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {

		}
		tip.startDate = date;
		
		//parse received date
		tip.receivedDate = mail.receivedDate;
		
		//parse publish date
		DateFormat publish_date_format = new SimpleDateFormat("EEE, MMM dd, yyyy, HH:mm", Locale.UK);
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		String publishdateString = lines[3].replaceFirst(".*:\\s", "").trim();
		
		if(publishdateString.contains("min ago") || publishdateString.contains("mins ago")){
			int minsEnd = publishdateString.indexOf(" min");
			String minsString = publishdateString.substring(0, minsEnd);
			int minsAgo = Integer.parseInt(minsString);
			long publishedTime = mail.receivedDate.getTime() - minsAgo * 60 * 1000;
			Date publishDate = new Date(publishedTime);
			tip.publishDate = publishDate;
		}
		else if(publishdateString.contains("sec ago") || publishdateString.contains("secs ago")){
			int secsEnd = publishdateString.indexOf(" sec");
			String secsString = publishdateString.substring(0, secsEnd);
			int secsAgo = Integer.parseInt(secsString);
			long publishedTime = mail.receivedDate.getTime() - secsAgo * 1000;
			Date publishDate = new Date(publishedTime);	
			tip.publishDate = publishDate;
		}
		else{
			// Remove the suffix of the day 
			// There is nothing in the java standard library to do this more elegantly
			publishdateString = publishdateString.replaceAll("st", ""); // as in 1st
			publishdateString = publishdateString.replaceAll("nd", ""); // as in 2nd
			publishdateString = publishdateString.replaceAll("rd", ""); // as in 3rd
			publishdateString = publishdateString.replaceAll("th", ""); // as in 4th
			Date publishDate = null;
			try {
				publishDate = publish_date_format.parse(publishdateString);
				tip.publishDate = publishDate;
			} catch (ParseException e) {
				
			}		
		}
		
		// Parse half time
		String halfTimeLine = lines[5].toUpperCase();
		if(halfTimeLine.contains("HALF")){
			tip.pivotType += " 1st Half";
		}
		
		// full
		tip.fullContent = s;
		
		// event
		tip.event = tip.host + " - " + tip.guest;
		
		//throw parsing error exception
		if(tip.startDate == null)
			throw new RuntimeException("no start date");
		if(tip.publishDate == null)
			throw new RuntimeException("no publish date");
		if(tip.selection == null)
			throw new RuntimeException("no selection");
		if(tip.pivotType == null)
			throw new RuntimeException("no pivottype");
		if(tip.odds <= 0)
			throw new RuntimeException("odd < 0");
		
		return tip;
	}
	

	public static void main(String[] args) {
//		String a = "Cacereno 0 (1st Half Asian Handicap) (1-1) @ 3.7";
//		System.out.println(a.replaceAll("(.*\\s\\D?)(\\d+\\.?\\d*\\s)(.*@.*)", "$2").trim());
//		try {
//			String data = (String) Toolkit.getDefaultToolkit()
//					.getSystemClipboard().getData(DataFlavor.stringFlavor);
//			ParsedTextMail mail = new ParsedTextMail();
//			mail.content = data;
//			parseEmail(mail);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}
		GMailReader reader = new GMailReader("blogabetcaptcha@gmail.com", "bmw735tdi");
		List<ParsedTextMail> mails = reader.read("vicentbet90@gmail.com", 100);
		List<BlogaBetTip> tips = new ArrayList<BlogaBetTip>();
		for(int i = 25; i < mails.size(); i++){
			ParsedTextMail mail = mails.get(i);
			try{
				BlogaBetTip tip = parseEmail(mail);
				System.out.println("Mail " + i);
				System.out.println("Host: " + tip.host);
				System.out.println("Guest: " + tip.guest);
				System.out.println("Type: " + tip.pivotType);
				System.out.println("Pvalue: " + tip.pivotValue);
				if(tip.pivotType.equalsIgnoreCase("Asian handicap"))
					System.out.println("Pbias: " + tip.pivotBias);
				System.out.println("Odd: " + tip.odds);
				if(tip.selection == null)
					throw new RuntimeException("selection is null");
				System.out.println("Selection: " + tip.selection);
				System.out.println("Source: " + tip.source);
				System.out.println("*****************************************************");
				tips.add(tip);
			}
			catch (Exception e){
				e.printStackTrace();
				System.out.println(mail.content);
			}
		}
		System.out.println();
	}
}
