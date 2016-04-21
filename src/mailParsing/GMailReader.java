package mailParsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailRawSearchTerm;
import com.sun.mail.gimap.GmailStore;
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
	
	private String userName;
	private String passWord;
	public GMailReader(String userName, String passWord){
		this.userName = userName;
		this.passWord = passWord;
	}
	
	public List<ParsedTextMail> read(String from, int numberOfLastMessages) {
		List<ParsedTextMail> result = new ArrayList<ParsedTextMail>();
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "gimap");

        try {
            Session session = Session.getDefaultInstance(props, null);
            GmailStore store = (GmailStore) session.getStore("gimap");
            store.connect("imap.gmail.com", userName, passWord);
            GmailFolder inbox = (GmailFolder) store.getFolder("[Gmail]/All Mail");
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.search(new GmailRawSearchTerm("from:(" + from + ")"));
            Message[] messages2 = new MimeMessage[Math.min(messages.length, numberOfLastMessages)];
            for(int i = 0; i < messages2.length; i++){
            	messages2[messages2.length - 1 - i] = messages[messages.length - 1 - i];
            }
        
		    FetchProfile fp = new FetchProfile();
		    fp.add(FetchProfile.Item.ENVELOPE);
		    fp.add(FetchProfileItem.FLAGS);
		    fp.add(FetchProfileItem.CONTENT_INFO);
		    fp.add(FetchProfileItem.MESSAGE);
		    
		    inbox.fetch(messages2, fp);
			
			for (int i = 0; i < messages2.length; i++) {

				ParsedTextMail parsedMail = new ParsedTextMail();
				parsedMail.from = messages2[i].getFrom()[0].toString();
				parsedMail.subject = messages2[i].getSubject();
				parsedMail.receivedDate = messages2[i].getReceivedDate();

				StringBuilder builder = new StringBuilder();
				Object content;
				try {
					content = messages2[i].getContent();
					parseContent(content, builder);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				parsedMail.content = builder.toString();
				result.add(parsedMail);
			}
			
            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        }
        return result;
	}
	
	public List<ParsedTextMail> read(String from) {
		List<ParsedTextMail> result = new ArrayList<ParsedTextMail>();
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "gimap");

        try {
            Session session = Session.getDefaultInstance(props, null);
            GmailStore store = (GmailStore) session.getStore("gimap");
            store.connect("imap.gmail.com", userName, passWord);
            GmailFolder inbox = (GmailFolder) store.getFolder("[Gmail]/All Mail");
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.search(new GmailRawSearchTerm("from:(" + from + ")"));
        
		    FetchProfile fp = new FetchProfile();
		    fp.add(FetchProfile.Item.ENVELOPE);
		    fp.add(FetchProfileItem.FLAGS);
		    fp.add(FetchProfileItem.CONTENT_INFO);
		    fp.add(FetchProfileItem.MESSAGE);
		    
		    inbox.fetch(messages, fp);
			
			for (int i = 0; i < messages.length; i++) {

				ParsedTextMail parsedMail = new ParsedTextMail();
				parsedMail.from = messages[i].getFrom()[0].toString();
				parsedMail.subject = messages[i].getSubject();
				parsedMail.receivedDate = messages[i].getReceivedDate();

				StringBuilder builder = new StringBuilder();
				Object content;
				try {
					content = messages[i].getContent();
					parseContent(content, builder);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				parsedMail.content = builder.toString();
				result.add(parsedMail);
			}
			
            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        }
        return result;
	}
	
	public List<ParsedTextMail> read() {
		List<ParsedTextMail> result = new ArrayList<ParsedTextMail>();
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "gimap");

        try {
            Session session = Session.getDefaultInstance(props, null);
            GmailStore store = (GmailStore) session.getStore("gimap");
            store.connect("imap.gmail.com", userName, passWord);
            GmailFolder inbox = (GmailFolder) store.getFolder("[Gmail]/All Mail");
            inbox.open(Folder.READ_ONLY);
            Message[] messages = inbox.getMessages();
        
		    FetchProfile fp = new FetchProfile();
		    fp.add(FetchProfile.Item.ENVELOPE);
		    fp.add(FetchProfileItem.FLAGS);
		    fp.add(FetchProfileItem.CONTENT_INFO);
		    fp.add(FetchProfileItem.MESSAGE);
		    
		    inbox.fetch(messages, fp);
			
			for (int i = 0; i < messages.length; i++) {

				ParsedTextMail parsedMail = new ParsedTextMail();
				parsedMail.from = messages[i].getFrom()[0].toString();
				parsedMail.subject = messages[i].getSubject();
				parsedMail.receivedDate = messages[i].getReceivedDate();

				StringBuilder builder = new StringBuilder();
				Object content;
				try {
					content = messages[i].getContent();
					parseContent(content, builder);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				parsedMail.content = builder.toString();
				result.add(parsedMail);
			}
			
            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.exit(2);
        }
        return result;
	}
	
	public void sendMail(String to, String subject, String content) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, passWord);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(userName));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(content);

			Transport.send(message);
			
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}	
	}
	
	public static void main(String[] args){
		GMailReader reader = new GMailReader("vicentbet90@gmail.com", "bmw735tdi2");
		long startTime = System.currentTimeMillis();
		List<ParsedTextMail> mails = reader.read("noreply@betadvisor.com", 10);
		System.out.println("time: " + (System.currentTimeMillis() - startTime));
	}
}
