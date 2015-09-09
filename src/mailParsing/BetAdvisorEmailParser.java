package mailParsing;

import java.util.List;


public class BetAdvisorEmailParser {

	static BetInformations parseMail(String mail){
		/* Create result bject */
		BetInformations result = new BetInformations();
		
		/* Parse Tipster */
		int tipsterStart = mail.indexOf("Tipster:") + 9;
		int tipsterEnd = mail.indexOf("\n", tipsterStart) - 1;
		String tipster = mail.substring(tipsterStart, tipsterEnd);
		result.tipster = tipster;
		System.out.println("Tipster: " + tipster);
		
		/* Parse date */
		int dateStart = mail.indexOf("Date:") + 6;
		int dateEnd = mail.indexOf("at", dateStart) - 1;
		String date = mail.substring(dateStart, dateEnd);
		result.date = date;
		System.out.println("Date: " + date);
		
		/* Parse event */
		int eventStart = mail.indexOf("Event:") + 8;
		int eventEnd = mail.indexOf("*", eventStart);
		String event = mail.substring(eventStart, eventEnd);
		result.event = event;
		System.out.println("Event: " + event);
		
		return result;
	}
	
	public static void main(String[] args) {
		GMailReader reader = new GMailReader();
		List<ParsedTextMail> mails = reader.read("pedrogtocrack@gmail.com");
		for(ParsedTextMail mail : mails){
			System.out.println(mail.subject);
			BetAdvisorEmailParser.parseMail(mail.content);
			System.out.println();
		}
	}
}
