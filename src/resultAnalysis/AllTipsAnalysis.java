package resultAnalysis;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mailParsing.BetAdvisorEmailParser;
import mailParsing.BetAdvisorResult;
import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;

public class AllTipsAnalysis {

	public static void main(String[] args) throws ParseException {
		double profit = 0;
		double yield = 0;
		double numberOfWins = 0;
		int numberOfBets = 0;
		
		GMailReader reader = new GMailReader("vicentbet90@gmail.com", "bmw735tdi2");
		List<ParsedTextMail> mails = reader.read("noreply@betadvisor.com");
		List<BetAdvisorResult> results = new ArrayList<BetAdvisorResult>();
		for(ParsedTextMail mail : mails){
			if(mail.subject.indexOf("Tip result") != -1){
				results.add(BetAdvisorEmailParser.parseResult(mail));
			}
		}
		for(int i = 0; i < results.size(); i++){
			BetAdvisorResult result = results.get(i);
			String string = "March 25, 2016";
			DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
			Date date = format.parse(string);
			
			if(result.receivedDate.after(date)){
				numberOfBets++;
				if(result.result.equalsIgnoreCase("LOST")){
					profit -= 100;
				}
				if(result.result.equalsIgnoreCase("WIN")){
					profit += result.bestOdds * 100 - 100;
					numberOfWins++;
				}
			}
		}
		System.out.println("Bets: " + numberOfBets);
		System.out.println("Profit: " + profit);
		System.out.println("Yield: " + (profit / numberOfBets));
		System.out.println("Winning %: " + (numberOfWins / numberOfBets));
	}
}
