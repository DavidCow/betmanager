package captcha;

import java.util.ArrayList;
import java.util.List;

import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;

public class MailFetching {
	
	private static GMailReader reader = new GMailReader();
		
	public static List<ParsedTextMail> getBlogaBetTips(int numberOfMailsToCheck){
		List<ParsedTextMail> mailList = reader.read("blogabet", numberOfMailsToCheck);
		List<ParsedTextMail> result = new ArrayList<ParsedTextMail>();
		for(int i = 0; i < mailList.size(); i++){
			if(mailList.get(i).subject.contains("New pick from")){
				result.add(mailList.get(i));
			}
		}
		return result;
	}
	
	public static String parseTipLinkFromMail(ParsedTextMail mail){
		int start = mail.content.indexOf("URL in a new browser window: https") + 29;
		int end = mail.content.indexOf("</p>", start + 31);
		String html = mail.content.substring(start, end);
		return html;	
	}
}
