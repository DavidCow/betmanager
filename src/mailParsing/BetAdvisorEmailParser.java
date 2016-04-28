package mailParsing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;


public class BetAdvisorEmailParser {
	
	private static final boolean debug = false;
	
	private static String parseHostFromEvent(String event){
		String res = "";
		int startIndex = event.indexOf(" ");
		if(startIndex != -1){
			startIndex += 2;
			int endIndex = event.indexOf(" vs ", startIndex);
			if(endIndex != -1){
				res = event.substring(startIndex, endIndex);
			}
		}
		return res;
	}
	
	private static String parseGuestFromEvent(String event){
		String res = "";
		int startIndex = event.indexOf(" vs ");
		if(startIndex != -1){
			startIndex += 4;
			int endIndex = event.indexOf(",", startIndex);
			if(endIndex != -1){
				res = event.substring(startIndex, endIndex);
			}
		}
		return res;
	}
	
	public static BetAdvisorResult parseResult(ParsedTextMail mail){
		// Clean HTML Tags etc.
		String cleanedMail = cleanMail(mail.content);
		
		/* Create result object */
		BetAdvisorResult result = new BetAdvisorResult();
		
		/* Get receiveDate and full Content */
		result.receivedDate = mail.receivedDate;
		result.fullContent = cleanedMail;
		
		/* Parse Tipster */
		int tipsterStart = cleanedMail.indexOf("Tipster:") + 9;
		int tipsterEnd = cleanedMail.indexOf("\n", tipsterStart) - 1;
		String tipster = cleanedMail.substring(tipsterStart, tipsterEnd);
		result.tipster = tipster;
		if(debug)
			System.out.println("Tipster: " + tipster);
		
		/* Parse date 
		 * 
		 * The timezone is CET
		 * Format is like: 3rd March 2016 at 20:45
		 */
		DateFormat format = new SimpleDateFormat("dd MMMM yyyy 'at' HH:mm", Locale.UK);
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		int dateStart = cleanedMail.indexOf("DATE:") + 6;
		int dateEnd = cleanedMail.indexOf("\n", dateStart);
		String dateString = cleanedMail.substring(dateStart, dateEnd);
		
		// Remove the suffix of the day 
		// There is nothing in the java standard library to do this more elegantly
		dateString = dateString.replaceAll("st", ""); // as in 1st
		dateString = dateString.replaceAll("nd", ""); // as in 2nd
		dateString = dateString.replaceAll("rd", ""); // as in 3rd
		dateString = dateString.replaceAll("th", ""); // as in 4th
		Date date = null;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1); // Exit and debug it, rather than working with wrong values
		}
		result.date = date;
		if(debug)
			System.out.println("Date: " + date.toString());
		
		/* Parse event */
		int eventStart = cleanedMail.indexOf("EVENT:") + 7;
		int eventEnd = cleanedMail.indexOf("DATE", eventStart);
		String event = cleanedMail.substring(eventStart, eventEnd);
		event = event.replaceAll("\n", "");
		result.event = event;
		if(debug)
			System.out.println("Event: " + event);
		
		/* Parse host */
		String host = parseHostFromEvent(event);
		result.host = host;
		
		/* Parse guest */
		String guest = parseGuestFromEvent(event);
		result.guest = guest;
		
		/* Parse type of bet*/
		int typeOfBetStart = cleanedMail.indexOf("Type Of Bet: ") + 13;
		int typeOfBetEnd = cleanedMail.indexOf("\n", typeOfBetStart) - 1;
		String typeOfBet = cleanedMail.substring(typeOfBetStart, typeOfBetEnd);
		result.typeOfBet = typeOfBet;
		
		/* Parse bet on */
		int betOnStart = cleanedMail.indexOf("BET ON: ") + 8;
		String betOnLine = cleanedMail.substring(betOnStart, cleanedMail.indexOf("\n", betOnStart));
		/* Extra case for Asian handicap */
		if(typeOfBet.indexOf("Asian handicap") == 0){
			int betOnEnd = betOnLine.lastIndexOf("+", betOnStart);
			if(betOnEnd == -1){
				betOnEnd = betOnLine.lastIndexOf("-", betOnStart);
			}
			betOnEnd--;
			String betOn = betOnLine.substring(0, betOnEnd);		
			result.betOn = betOn;			
		}
		else{
			int betOnEnd = cleanedMail.indexOf("\n", betOnStart) - 1;
			String betOn = cleanedMail.substring(betOnStart, betOnEnd);		
			result.betOn = betOn;		
		}
		
		/* Parse pivot value */
		if(typeOfBet.indexOf("Over / Under") == 0){
			int pivotValueStart = cleanedMail.indexOf("+", betOnStart) + 1;
			int pivotValueEnd = cleanedMail.indexOf(" ", pivotValueStart);
			String pivotValueString = cleanedMail.substring(pivotValueStart, pivotValueEnd);
			double pivotValue = Double.parseDouble(pivotValueString);
			result.pivotValue = pivotValue;
		}
		else if(typeOfBet.indexOf("Asian handicap") == 0){
			int pivotValueStart = betOnLine.indexOf("+");
			if(pivotValueStart == -1){
				pivotValueStart = betOnLine.lastIndexOf("-");
				pivotValueStart++;
				int pivotValueEnd = betOnLine.indexOf(" ", pivotValueStart);
				String pivotValueString = betOnLine.substring(pivotValueStart, pivotValueEnd);
				double pivotValue = Double.parseDouble(pivotValueString);
				result.pivotValue = pivotValue;	
				if(result.betOn.equals(result.host)){
					result.pivotBias = "HOST";
				}
				else{
					result.pivotBias = "GUEST";
				}
			}
			else{
				pivotValueStart++;
				int pivotValueEnd = betOnLine.indexOf(" ", pivotValueStart);
				String pivotValueString = betOnLine.substring(pivotValueStart, pivotValueEnd);
				double pivotValue = Double.parseDouble(pivotValueString);
				result.pivotValue = pivotValue;	
				if(result.betOn.equals(result.host)){
					result.pivotBias = "GUEST";
				}
				else{
					result.pivotBias = "HOST";
				}
			}
		}
		
		/* Parse bestOdds */
		int bestOddsStart = cleanedMail.indexOf("Best odds: ") + 11;
		int bestOddsEnd = cleanedMail.indexOf(" ", bestOddsStart);
		String bestOddsString = cleanedMail.substring(bestOddsStart, bestOddsEnd);
		double bestOdds = Double.parseDouble(bestOddsString);
		result.bestOdds = bestOdds;
		
		/* Parse no bet under */
		int noBetUnderStart = cleanedMail.toUpperCase().indexOf("NO BET UNDER: ") + 14;
		int noBetUnderEnd = cleanedMail.indexOf("\n", noBetUnderStart) - 1;
		String noBetUnderString = cleanedMail.substring(noBetUnderStart, noBetUnderEnd);
		double noBetUnder = Double.parseDouble(noBetUnderString);
		result.noBetUnder = noBetUnder;
		
		/* Parse score */
		int scoreStart = cleanedMail.indexOf("Score: ") + 7;
		int scoreEnd = cleanedMail.indexOf("\n", scoreStart);
		String score = cleanedMail.substring(scoreStart, scoreEnd);
		result.score = score;	
		
		/* Parse Result */
		int resultStart = cleanedMail.indexOf("Result of tip: ") + 15;
		int resultEnd = cleanedMail.indexOf("\n", resultStart);
		String resultString = cleanedMail.substring(resultStart, resultEnd);
		result.result = resultString;	
		
		return result;
	}

	public static BetAdvisorTip parseTip(ParsedTextMail mail){
		// Clean HTML Tags etc.
		String cleanedMail = cleanMail(mail.content);
		
		/* Create result object */
		BetAdvisorTip result = new BetAdvisorTip();
		
		/* Get receiveDate and full Content */
		result.receivedDate = mail.receivedDate;
		result.fullContent = cleanedMail;
		
		/* Parse Tipster */
		int tipsterStart = cleanedMail.indexOf("Tipster:") + 9;
		int tipsterEnd = cleanedMail.indexOf("\n", tipsterStart);
		String tipster = cleanedMail.substring(tipsterStart, tipsterEnd);
		result.tipster = tipster;
		if(debug)
			System.out.println("Tipster: " + tipster);
		
		/* Parse date 
		 * 
		 * The timezone is CET
		 * Format is like: 3rd March 2016 at 20:45
		 */
		DateFormat format = new SimpleDateFormat("dd MMMM yyyy 'at' HH:mm", Locale.UK);
		format.setTimeZone(TimeZone.getTimeZone("CET"));
		int dateStart = cleanedMail.indexOf("DATE:") + 6;
		int dateEnd = cleanedMail.indexOf("\n", dateStart);
		String dateString = cleanedMail.substring(dateStart, dateEnd);
		
		// Remove the suffix of the day 
		// There is nothing in the java standard library to do this more elegantly
		dateString = dateString.replaceAll("st", ""); // as in 1st
		dateString = dateString.replaceAll("nd", ""); // as in 2nd
		dateString = dateString.replaceAll("rd", ""); // as in 3rd
		dateString = dateString.replaceAll("th", ""); // as in 4th
		Date date = null;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1); // Exit and debug it, rather than working with wrong values
		}
		result.date = date;
		if(debug)
			System.out.println("Date: " + date.toString());
		
		/* Parse event */
		int eventStart = cleanedMail.indexOf("EVENT:") + 7;
		int eventEnd = cleanedMail.indexOf("DATE", eventStart);
		String event = cleanedMail.substring(eventStart, eventEnd);
		event = event.replaceAll("\n", "");
		result.event = event;
		if(debug)
			System.out.println("Event: " + event);
		
		/* Parse host */
		String host = parseHostFromEvent(event);
		result.host = host;
		
		/* Parse guest */
		String guest = parseGuestFromEvent(event);
		result.guest = guest;
		
		/* Parse type of bet*/
		int typeOfBetStart = cleanedMail.indexOf("Type Of Bet: ") + 13;
		int typeOfBetEnd = cleanedMail.indexOf("\n", typeOfBetStart) - 1;
		String typeOfBet = cleanedMail.substring(typeOfBetStart, typeOfBetEnd);
		result.typeOfBet = typeOfBet;
		
		/* Parse bet on */
		int betOnStart = cleanedMail.indexOf("BET ON: ") + 8;
		String betOnLine = cleanedMail.substring(betOnStart, cleanedMail.indexOf("\n", betOnStart));

		if(typeOfBet.indexOf("Asian handicap") == 0){
			int betOnEnd = betOnLine.lastIndexOf("+", betOnStart);
			if(betOnEnd == -1){
				betOnEnd = betOnLine.lastIndexOf("-", betOnStart);
			}
			betOnEnd--;
			String betOn = betOnLine.substring(0, betOnEnd);		
			result.betOn = betOn;			
		}
		else if(typeOfBet.indexOf("Asian Handicap 1st Half") == 0){
			int betOnEnd = betOnLine.lastIndexOf("+", betOnStart);
			if(betOnEnd == -1){
				betOnEnd = betOnLine.lastIndexOf("-", betOnStart);
			}
			betOnEnd--;
			String betOn = betOnLine.substring(0, betOnEnd);		
			result.betOn = betOn;			
		}
		else if(typeOfBet.indexOf("Match Odds 1st Half") == 0){
			int betOnEnd = cleanedMail.indexOf("\n", betOnStart) - 1;
			String betOn = cleanedMail.substring(betOnStart, betOnEnd);	
			betOn = betOn.replaceAll(" Half Time", "");
			result.betOn = betOn;			
		}
		else if(typeOfBet.indexOf("Over / Under Team") == 0){
			int betOnEnd = cleanedMail.indexOf("\n", betOnStart) - 1;
			String betOn = cleanedMail.substring(betOnStart, betOnEnd);	
			if(betOn.indexOf("Over") != -1){
				betOn = "Over";
			}
			else if(betOn.indexOf("Under") != -1){
				betOn = "Under";
			}
			result.betOn = betOn;			
		}
		else{
			int betOnEnd = cleanedMail.indexOf("\n", betOnStart) - 1;
			String betOn = cleanedMail.substring(betOnStart, betOnEnd);		
			result.betOn = betOn;		
		}
			
		/* Parse pivot value */
		if(typeOfBet.indexOf("Over / Under") == 0){
			int pivotValueStart = cleanedMail.indexOf("+", betOnStart) + 1;
			int pivotValueEnd = cleanedMail.indexOf(" ", pivotValueStart);
			String pivotValueString = cleanedMail.substring(pivotValueStart, pivotValueEnd);
			double pivotValue = Double.parseDouble(pivotValueString);
			result.pivotValue = pivotValue;
			
			// Parse "pivot bias" for Over / Under Team
			if(typeOfBet.indexOf("Team") != -1){
				if(betOnLine.indexOf(host) != -1){
					result.pivotBias = host;
				}
				else if(betOnLine.indexOf(guest) != -1){
					result.pivotBias = guest;
				}
			}
		}
		else if(typeOfBet.indexOf("Asian handicap") == 0){
			int pivotValueStart = betOnLine.indexOf("+");
			if(pivotValueStart == -1){
				pivotValueStart = betOnLine.lastIndexOf("-");
				pivotValueStart++;
				int pivotValueEnd = betOnLine.indexOf(" ", pivotValueStart);
				String pivotValueString = betOnLine.substring(pivotValueStart, pivotValueEnd);
				double pivotValue = Double.parseDouble(pivotValueString);
				result.pivotValue = pivotValue;	
				if(result.betOn.equals(result.host)){
					result.pivotBias = "HOST";
				}
				else{
					result.pivotBias = "GUEST";
				}
			}
			else{
				pivotValueStart++;
				int pivotValueEnd = betOnLine.indexOf(" ", pivotValueStart);
				String pivotValueString = betOnLine.substring(pivotValueStart, pivotValueEnd);
				double pivotValue = Double.parseDouble(pivotValueString);
				result.pivotValue = pivotValue;	
				if(result.betOn.equals(result.host)){
					result.pivotBias = "GUEST";
				}
				else{
					result.pivotBias = "HOST";
				}
			}
		}
		else if(typeOfBet.indexOf("Asian Handicap 1st Half") == 0){
			int pivotValueStart = betOnLine.indexOf("+");
			if(pivotValueStart == -1){
				pivotValueStart = betOnLine.lastIndexOf("-");
				pivotValueStart++;
				int pivotValueEnd = betOnLine.indexOf(" ", pivotValueStart);
				String pivotValueString = betOnLine.substring(pivotValueStart, pivotValueEnd);
				double pivotValue = Double.parseDouble(pivotValueString);
				result.pivotValue = pivotValue;	
				if(result.betOn.equals(result.host)){
					result.pivotBias = "HOST";
				}
				else{
					result.pivotBias = "GUEST";
				}
			}
			else{
				pivotValueStart++;
				int pivotValueEnd = betOnLine.indexOf(" ", pivotValueStart);
				String pivotValueString = betOnLine.substring(pivotValueStart, pivotValueEnd);
				double pivotValue = Double.parseDouble(pivotValueString);
				result.pivotValue = pivotValue;	
				if(result.betOn.equals(result.host)){
					result.pivotBias = "GUEST";
				}
				else{
					result.pivotBias = "HOST";
				}
			}
		}
		
		/* Parse bestOdds */
		int bestOddsStart = cleanedMail.indexOf("Best odds: ") + 11;
		int bestOddsEnd = cleanedMail.indexOf(" ", bestOddsStart);
		String bestOddsString = cleanedMail.substring(bestOddsStart, bestOddsEnd);
		double bestOdds = Double.parseDouble(bestOddsString);
		result.bestOdds = bestOdds;
		
		/* Parse no bet under */
		int noBetUnderStart = cleanedMail.toUpperCase().indexOf("NO BET UNDER: ") + 14;
		int noBetUnderEnd = cleanedMail.indexOf("\n", noBetUnderStart) - 1;
		String noBetUnderString = cleanedMail.substring(noBetUnderStart, noBetUnderEnd);
		double noBetUnder = Double.parseDouble(noBetUnderString);
		result.noBetUnder = noBetUnder;
		
		/* Parse take */
		int stakeLineStart = cleanedMail.indexOf("Stake: ");
		int stakeLineEnd = Math.max(cleanedMail.indexOf("\n", stakeLineStart), cleanedMail.length());
		String stakeLine = cleanedMail.substring(stakeLineStart, stakeLineEnd);
		int stakeStart = stakeLine.indexOf("(") + 1;
		int stakeEnd = stakeLine.indexOf(" units", stakeStart);
		String stakeString = stakeLine.substring(stakeStart, stakeEnd);
		double stake = Double.parseDouble(stakeString);
		result.take = stake;
		
		return result;
	}
	
	// Cleans the mails HTML tags and shortens it to the relevant section
	private static String cleanMail(String s){
		String plain = new HtmlToPlainText().getPlainText(Jsoup.parse(s));
		plain = plain.replaceAll("<.*>", "");
		plain = plain.replaceAll("'", "");
		int startIndex = plain.indexOf("Result of tip:");
		if(startIndex == -1)
			startIndex = plain.indexOf("Details of the tip:");
		plain = plain.substring(startIndex);
		int endIndex = plain.indexOf("units)") + 6;
		plain = plain.substring(0, endIndex);
		return plain;
	}
	
	public static String parseLigueFromEvent(String event){
		int startIndex = event.lastIndexOf(",") + 1;
		String league = event.substring(startIndex);
		return league;
	}
	
	public static void main(String[] args) {
		GMailReader reader = new GMailReader("vicentbet90@gmail.com", "bmw735tdi2");
		List<ParsedTextMail> mails = reader.read("noreply@betadvisor.com");
		List<BetAdvisorTip> tips = new ArrayList<BetAdvisorTip>();
		List<BetAdvisorResult> results = new ArrayList<BetAdvisorResult>();
		for(ParsedTextMail mail : mails){
			if(mail.subject.indexOf("Tip subscription") != -1){
				tips.add(BetAdvisorEmailParser.parseTip(mail));
				String l = parseLigueFromEvent(tips.get(tips.size() - 1).event);
				System.out.println(l);
			}
			if(mail.subject.indexOf("Tip result") != -1){
				results.add(BetAdvisorEmailParser.parseResult(mail));
			}
		}
		double stake = 0;
		for(int i = 0; i < tips.size(); i++){
			stake += tips.get(i).take;
		}
		stake /= tips.size();
		System.out.println(stake);
		int kek = 12;
		int b = kek;
	}
}
