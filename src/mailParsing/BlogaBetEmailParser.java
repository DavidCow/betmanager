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
	
	public static void parseEmail(String s){
		BlogaBetTip tip = new BlogaBetTip();
		String[] lines = s.split("\n");
		tip.tipster = lines[1];
		tip.host = lines[4].replaceAll("-.*", "").trim();
		tip.guest = lines[4].replaceAll(".*-\\s?", "").trim();
		
		/*Parse pivot type, value and bias
		 * Check type first, then parse value and bias
		 * 
		 */
		if(lines[5].contains("(AH)")){
			tip.pivotType = "";
			tip.pivotValue = Double.parseDouble(lines[5].replaceAll("(.*)(\\d+\\.\\d*)(.*@.*)", "$2"));
			String bias = lines[5].replaceAll("(.*)(.{1})(\\d+\\.\\d*)(.*@.*)", "$2");
			tip.selection = lines[5].replaceAll("(.*)(\\s.*\\s)(.{1})(\\d+\\.\\d*)(.*@.*)", "$2").trim().toUpperCase();
			if(bias.equalsIgnoreCase("-"))
				tip.pivotBias = tip.selection;
			else
				tip.pivotBias = oppositeString(tip.selection);
			System.out.println(tip.pivotValue);
			System.out.println(tip.pivotBias);
			System.out.println(tip.selection);
		}
		else if(lines[5].contains("(1X2)")){
			tip.selection = lines[5].replaceAll("(.*)(\\s.+\\s+)(\\(.*)", "$2").trim().toUpperCase();
			System.out.println(tip.selection);
		}
		//TODO Add more pivot types

		tip.odds = Double.parseDouble(lines[5].replaceAll(".*@", "").trim());
		tip.stake = lines[6].replaceAll("(\\d+/\\d+)(.*)", "$1");
		tip.source = lines[6].replaceAll("(\\d+/\\d+)(\\s?\\S*\\s?)(.*)?", "$2").trim();
		System.out.println(tip.stake);
		System.out.println(tip.source);
		System.out.println(tip.odds);
		
		
		tip.sport = lines[7].replaceAll("\\s?/.*", "").trim();
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
	}
	

	public static void main(String[] args) {
		try {
			String data = (String) Toolkit.getDefaultToolkit()
					.getSystemClipboard().getData(DataFlavor.stringFlavor);
			System.out.println(data);
			parseEmail(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

}
