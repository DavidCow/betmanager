package bettingManager.statsCalculation;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mailParsing.BetAdvisorEmailParser;
import mailParsing.BetAdvisorTip;
import mailParsing.BlogaBetEmailParser;
import mailParsing.BlogaBetTip;
import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;
import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;
import bettingBot.entities.BetComparator;

/** 
 * This Class provides methods for saving the results of the betting bot to a java Object, from the SQL Database on the server
 * and for the tips received by mail
 * 
 */
public class DataCreation {
	public static final String BLOGABET_RESULT_PATH = "blogaBetRealBets.dat";
	public static final String BETADVISOR_RESULT_PATH = "betAdvisorRealBets.dat";
	public static final String BLOGABET_TIP_PATH = "blogaBetTips.dat";
	public static final String BETADVISOR_TIP_PATH = "betAdvisorTips.dat";
	
	/*
	 * This method saves the bets done by our bot as a Java object, using an ObjectOutputStream
	 * 
	 * It will never be called from the GUI, it will only be called on the server where the bot is running
	 * The saved objects will then have to be sent to the user wanting to analyse the results
	 */
	private static void saveResultLists() throws Exception{
		BettingBotDatabase database = new BettingBotDatabase();
		List<Bet> betAdvisorBets = database.getAllBets();
		Collections.sort(betAdvisorBets, new BetComparator());
		
        FileOutputStream fileOutput = new FileOutputStream(BETADVISOR_RESULT_PATH);
        BufferedOutputStream br = new BufferedOutputStream(fileOutput);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(betAdvisorBets);
        objectOutputStream.close();
        
		List<Bet> blogaBetBets = database.getAllBetsBlogaBet();
		Collections.sort(blogaBetBets, new BetComparator());
		
        fileOutput = new FileOutputStream(BLOGABET_RESULT_PATH);
        br = new BufferedOutputStream(fileOutput);
        objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(blogaBetBets);
        objectOutputStream.close();
	}
	
	/*
	 * This method saves the received Tips as a Java object, using an ObjectOutputStream
	 * 
	 * It will never be called from the GUI
	 * 
	 */
	private static void saveTipLists() throws Exception{
		GMailReader reader = new GMailReader("vicentbet90@gmail.com", "bmw735tdi2");
		GMailReader readerBlogaBet = new GMailReader("blogabetcaptcha@gmail.com", "bmw735tdi");
		
		// Get parsed mails
		List<ParsedTextMail> mails = new ArrayList<ParsedTextMail>();
		try{
			mails = reader.read("noreply@betadvisor.com");
		} catch(Exception e){
			e.printStackTrace();
		}
		List<BetAdvisorTip> tips = new ArrayList<BetAdvisorTip>();
		for(ParsedTextMail mail : mails){
			if(mail.subject.indexOf("Tip subscription") != -1){
				try{
					tips.add(BetAdvisorEmailParser.parseTip(mail));
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
        FileOutputStream fileOutput = new FileOutputStream(BETADVISOR_TIP_PATH);
        BufferedOutputStream br = new BufferedOutputStream(fileOutput);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(tips);
        objectOutputStream.close();	
        
		// BlogaBet
		// Get parsed mails
		List<ParsedTextMail> mailsBlogaBet = new ArrayList<ParsedTextMail>();
		try{
			mailsBlogaBet = readerBlogaBet.read("vicentbet90@gmail.com");
		} catch(Exception e){
			e.printStackTrace();
		}
		List<BlogaBetTip> tipsBlogaBet = new ArrayList<BlogaBetTip>();
		for(ParsedTextMail mail : mailsBlogaBet){
			try{
				tipsBlogaBet.add(BlogaBetEmailParser.parseEmail(mail));
			} catch(RuntimeException e){
				e.printStackTrace();
			}
		}
		
		fileOutput = new FileOutputStream(BLOGABET_TIP_PATH);
        br = new BufferedOutputStream(fileOutput);
        objectOutputStream = new ObjectOutputStream(br);	
        objectOutputStream.writeObject(tipsBlogaBet);
        objectOutputStream.close();	
	}
	
	public static void main(String[] args) throws Exception {
		saveTipLists();
	}
}
