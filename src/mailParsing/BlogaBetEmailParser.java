package mailParsing;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BlogaBetEmailParser {
	
	private static String oppositeString(String s){
		if(s.equalsIgnoreCase("HOME"))
			return "GUEST";
		else
			return "HOME";
	}
	
	public static BlogaBetTip parseEmail(ParsedTextMail mail){
		String s = mail.content;
		BlogaBetTip tip = new BlogaBetTip();
		String[] lines = s.split("\n");
		
		//parse tipster
		tip.tipster = lines[1];
		
		//parse host
		tip.host = lines[4].replaceAll("-.*", "").trim();
		
		//parse guest
		tip.guest = lines[4].replaceAll(".*-\\s?", "").trim();
		
		/*Parse pivot type, value and bias
		 * Check type first, then parse value and bias
		 * 
		 */
		if(lines[5].contains("(AH)")){
			tip.pivotType = "Asian handicap";
			tip.pivotValue = Double.parseDouble(lines[5].replaceAll("(.*\\s.{1})(\\d+\\.\\d*)(.*@.*)", "$2"));
			String bias = lines[5].replaceAll("(.*)(\\s.{1})(\\d+\\.\\d*)(.*@.*)", "$2").trim();
			tip.selection = lines[5].replaceAll("(.*)(\\s.*\\s)(.{1})(\\d+\\.\\d*)(.*@.*)", "$2").trim().toUpperCase();
			if(bias.equalsIgnoreCase("-"))
				tip.pivotBias = tip.selection;
			else
				tip.pivotBias = oppositeString(tip.selection);
//			System.out.println(tip.pivotValue);
//			System.out.println(tip.pivotBias);
//			System.out.println(tip.selection);
		}
		else if(lines[5].contains("(1X2)")){
			tip.pivotType = "Match Odds";
			tip.selection = lines[5].replaceAll("(.*)(\\s.+\\s+)(\\(.*)", "$2").trim().toUpperCase();
			System.out.println(tip.selection);
		}
		else if(lines[5].contains("(O/U)")){
			tip.pivotType = "Over / Under";
			tip.pivotValue = Double.parseDouble(lines[5].replaceAll("(.*)(\\s\\d+\\.\\d*)(.*@.*)", "$2"));
			tip.selection = lines[5].replaceAll("(.*)(\\s.*\\s)(\\d+\\.\\d*)(.*@.*)", "$2").trim().toUpperCase();
			System.out.println(tip.pivotValue);
			System.out.println(tip.selection);
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
		System.out.println(tip.stake);
		System.out.println(tip.source);
		System.out.println(tip.odds);
		
		//parse sport
		tip.sport = lines[7].replaceAll("\\s?/.*", "").trim();
		
		//parse country
		tip.country = lines[7].replaceAll("(.*/)(.*)(/.*)", "$2").trim();
		System.out.println(tip.sport);
		System.out.println(tip.country);
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
			e.printStackTrace();
			System.exit(-1); // Exit and debug it, rather than working with wrong values
		}
		tip.startDate = date;
		System.out.println(tip.startDate.toString());
		return tip;
	}
	

	public static void main(String[] args) {
		try {
			String data = (String) Toolkit.getDefaultToolkit()
					.getSystemClipboard().getData(DataFlavor.stringFlavor);
			ParsedTextMail mail = new ParsedTextMail();
			mail.content = data;
			parseEmail(mail);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

}
