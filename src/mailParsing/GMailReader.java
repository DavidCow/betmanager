package mailParsing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import com.sun.mail.imap.IMAPFolder.FetchProfileItem;


public class GMailReader {
	
	private void parseContent(Object content, StringBuilder builder) throws Exception{
		if(content instanceof String){
			builder.append(content);
		}
		else if(content instanceof Part){
			Part part = (Part)content;
			parseContent(part.getContent(), builder);
		}
		else if(content instanceof MimeMultipart){
			MimeMultipart mmp = (MimeMultipart)content;
			for (int m = 0; m < mmp.getCount(); m++) {
				Object mbp = mmp.getBodyPart(m);
				parseContent(mbp, builder);
			}
		}
	}
	
	public List<ParsedTextMail> read(String from, Date sinceDate) {
		
		List<ParsedTextMail> result = new ArrayList<ParsedTextMail>();
		try {
			Properties props = new Properties();
			props.put("mail.imaps.ssl.trust", "*");
			Session session = Session.getDefaultInstance(props);
			Store store = session.getStore("imaps");
			store.connect("smtp.gmail.com", "vicentbet90@gmail.com", "bmw735tdi");
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			
			
			SearchTerm sT = new ReceivedDateTerm(ComparisonTerm.GT, sinceDate);
			Message[] messages = inbox.search(sT);
			
		    FetchProfile fp = new FetchProfile();
		    fp.add(FetchProfile.Item.ENVELOPE);
		    fp.add(FetchProfileItem.FLAGS);
		    fp.add(FetchProfileItem.CONTENT_INFO);
		    
		    inbox.fetch(messages, fp);
			
			for (int i = 0; i < messages.length; i++) {
				boolean fromMatches = false;
				String matchingFrom = "";
				Address[] addresses = messages[i].getFrom();
				for(int j = 0; j < addresses.length; j++){
					String s = addresses[j].toString();
					if(s.contains(from)){
						fromMatches = true;
						matchingFrom = addresses[j].toString();
						break;
					}
				}
				if(fromMatches){
					ParsedTextMail parsedMail = new ParsedTextMail();
					parsedMail.from = matchingFrom;
					parsedMail.subject = messages[i].getSubject();
					parsedMail.receivedDate = messages[i].getReceivedDate();

					StringBuilder builder = new StringBuilder();
					Object content = messages[i].getContent();
					parseContent(content, builder);
					
					parsedMail.content = builder.toString();
					result.add(parsedMail);
//					System.out.println("From: " + parsedMail.from);
//					System.out.println("Subject: " + parsedMail.subject);
//					System.out.println("Content: " + parsedMail.content);
				}
			}
			inbox.close(true);
			store.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<ParsedTextMail> read(String from) {
				
		List<ParsedTextMail> result = new ArrayList<ParsedTextMail>();
		try {
			Properties props = new Properties();
			props.put("mail.imaps.ssl.trust", "*");
			Session session = Session.getDefaultInstance(props);
			Store store = session.getStore("imaps");
			store.connect("smtp.gmail.com", "vicentbet90@gmail.com", "bmw735tdi");
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
		
			Message[] messages = inbox.getMessages();
			
		    FetchProfile fp = new FetchProfile();
		    fp.add(FetchProfile.Item.ENVELOPE);
		    fp.add(FetchProfileItem.FLAGS);
		    fp.add(FetchProfileItem.CONTENT_INFO);
		    
		    inbox.fetch(messages, fp);
			
			for (int i = 0; i < messages.length; i++) {
				boolean fromMatches = false;
				String matchingFrom = "";
				Address[] addresses = messages[i].getFrom();
				for(int j = 0; j < addresses.length; j++){
					String s = addresses[j].toString();
					if(s.contains(from)){
						fromMatches = true;
						matchingFrom = addresses[j].toString();
						break;
					}
				}
				if(fromMatches){
					ParsedTextMail parsedMail = new ParsedTextMail();
					parsedMail.from = matchingFrom;
					parsedMail.subject = messages[i].getSubject();
					parsedMail.receivedDate = messages[i].getReceivedDate();

					StringBuilder builder = new StringBuilder();
					Object content = messages[i].getContent();
					parseContent(content, builder);
					
					parsedMail.content = builder.toString();
					result.add(parsedMail);
//					System.out.println("From: " + parsedMail.from);
//					System.out.println("Subject: " + parsedMail.subject);
//					System.out.println("Content: " + parsedMail.content);
				}
			}
			inbox.close(true);
			store.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public List<ParsedTextMail> read() {
		
		List<ParsedTextMail> result = new ArrayList<ParsedTextMail>();
		try {
			Properties props = new Properties();
			Session session = Session.getInstance(props, null);		
			Store store = session.getStore("imaps");
			store.connect("smtp.gmail.com", "vicentbet90@gmail.com", "bmw735tdi");
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			Message[] messages = inbox.getMessages();
			
		    FetchProfile fp = new FetchProfile();
		    fp.add(FetchProfile.Item.ENVELOPE);
		    fp.add(FetchProfileItem.FLAGS);
		    fp.add(FetchProfileItem.CONTENT_INFO);
		    
		    inbox.fetch(messages, fp);
			
			for (int i = 0; i < messages.length; i++) {
				Address[] addresses = messages[i].getFrom();
				ParsedTextMail parsedMail = new ParsedTextMail();
				parsedMail.from = addresses[0].toString();
				parsedMail.subject = messages[i].getSubject();
				parsedMail.receivedDate = messages[i].getReceivedDate();

				StringBuilder builder = new StringBuilder();
				Object content = messages[i].getContent();
				parseContent(content, builder);
				
				parsedMail.content = builder.toString();
				result.add(parsedMail);
			}
			inbox.close(true);
			store.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		GMailReader reader = new GMailReader();
		List<ParsedTextMail> mails = reader.read("noreply@betadvisor.com");
		for(ParsedTextMail s : mails)
			System.out.println(s.content);
	}
}
