package mailParsing;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
	
	public List<ParsedTextMail> read(String from, int numberOfLastMessages) {
		
		List<ParsedTextMail> result = new ArrayList<ParsedTextMail>();
		try {
			Properties props = new Properties();
			props.put("mail.imaps.ssl.trust", "*");
			Session session = Session.getDefaultInstance(props);
			Store store = session.getStore("imaps");
			store.connect("smtp.gmail.com", "vicentbet90@gmail.com", "bmw735tdi2");
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			
			int end = inbox.getMessageCount();
			int start = end - numberOfLastMessages + 1;
			Message[] messages = inbox.getMessages(start, end);
			
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
			store.connect("smtp.gmail.com", "vicentbet90@gmail.com", "bmw735tdi2");
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
			store.connect("smtp.gmail.com", "vicentbet90@gmail.com", "bmw735tdi2");
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
	
	private class SMTPAuthenticator extends Authenticator
	  {
	        private PasswordAuthentication authentication;

	        public SMTPAuthenticator(String login, String password)
	        {
	             authentication = new PasswordAuthentication(login, password);
	        }

	        @Override
	        protected PasswordAuthentication getPasswordAuthentication()
	        {
	             return authentication;
	        }
	  }
	
	public void sendMail(String to, String subject, String content) {
		final String username = "patrykhopner@gmail.com";
		final String password = "Finasteride321";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("patrykhopner@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(content);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}	
	}
	
	public static void main(String[] args){
		GMailReader reader = new GMailReader();
		List<ParsedTextMail> mails = reader.read("noreply@betadvisor.com", 10);
		for(ParsedTextMail s : mails)
			System.out.println(s.content);
		reader.sendMail("patrykhopner@gmail.com", "Infos", "content");
	}
}
