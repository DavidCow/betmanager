package bettingManager.statsCalculation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

import com.google.gson.Gson;

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
	 * This method creates a list of Tips, which were not found in the API
	 */
	private static void createMissedTips(){
		Gson gson = new Gson();
		Class recordClass;
		Class eventClass;
		
		File f0 = new File("event.dat");
		File f1 = new File("record.dat");
		if(f0.isFile() && f0.canRead() && f1.isFile() && f1.canRead()){
			try{
				FileInputStream in0 = new FileInputStream(f0);
				ObjectInputStream inO0 = new ObjectInputStream(in0);
				eventClass = (Class)inO0.readObject();
				
				FileInputStream in1 = new FileInputStream(f1);
				ObjectInputStream inO1 = new ObjectInputStream(in1);
				recordClass = (Class)inO1.readObject();
				inO0.close();
				inO1.close();
			}catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
		}	
		
		// Received Tips
		List<BetAdvisorTip> betAdvisorTips = null;
		List<BlogaBetTip> blogaBetTips = null;
		
		// Real Bets
		List<Bet> betAdvisorBets = null;
		List<Bet> blogaBetBets = null;
		
		// Read received Tips
		try {
			FileInputStream fileInput = new FileInputStream(DataCreation.BETADVISOR_TIP_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			betAdvisorTips = (List<BetAdvisorTip>)objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			FileInputStream fileInput = new FileInputStream(DataCreation.BLOGABET_TIP_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			blogaBetTips = (List<BlogaBetTip>)objectInputStream.readObject();	
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}	
		
		// Read real bets
		try {
			FileInputStream fileInput = new FileInputStream(DataCreation.BETADVISOR_RESULT_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			betAdvisorBets = (List<Bet>)objectInputStream.readObject();
			for(int i = 0; i < betAdvisorBets.size(); i++){
				Bet bet = betAdvisorBets.get(i);
				BetAdvisorTip tip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);		
				Date date = tip.date;
				if(date == null){
					betAdvisorBets.remove(i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		try {
			FileInputStream fileInput = new FileInputStream(DataCreation.BLOGABET_RESULT_PATH);
			BufferedInputStream br = new BufferedInputStream(fileInput);
			ObjectInputStream objectInputStream = new ObjectInputStream(br);        
			blogaBetBets = (List<Bet>)objectInputStream.readObject();	
			for(int i = 0; i < blogaBetBets.size(); i++){
				Bet bet = blogaBetBets.get(i);
				// Conversion from String to double
				String tipJsonString = bet.getTipJsonString();
				int startStake = tipJsonString.indexOf("\"stake\"") + 9;
				int stakeEnd = tipJsonString.indexOf("\"", startStake);
				String stakeString = tipJsonString.substring(startStake, stakeEnd);
				int splitPoint = stakeString.indexOf("/");
				if(splitPoint != -1){
					String a = stakeString.substring(0, splitPoint);
					String b = stakeString.substring(splitPoint + 1);
					double stake = Double.parseDouble(a) / Double.parseDouble(b);
					tipJsonString = tipJsonString.replace(stakeString, stake + "");
					bet.setTipJsonString(tipJsonString);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}	
		
		FileWriter fw = null;
		FileWriter fw2 = null;
		try {
			fw = new FileWriter("Missed_Bets_BA.txt", true);
			fw2 = new FileWriter("Missed_Bets_BB.txt", true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //the true will append the new data
		
		for(int i = 0; i < betAdvisorTips.size(); i++){
			BetAdvisorTip tip = betAdvisorTips.get(i);	
			boolean found = false;
			for(int j = 0; j < betAdvisorBets.size(); j++){
				Bet bet = betAdvisorBets.get(j);
				BetAdvisorTip betTip = (BetAdvisorTip)gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);	
				if(Math.abs(tip.date.getTime() - betTip.date.getTime()) < 60 * 60 * 2000 && betTip.tipster.equals(tip.tipster) && betTip.betOn.equals(tip.betOn) && betTip.host.equals(tip.host) && betTip.guest.equals(tip.guest)){
					found = true;
					break;
				}
			}
			if(!found){
				System.out.println(tip.toString());
				try {
					fw.write(tip.toString() + "\r\n\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for(int i = 0; i < blogaBetTips.size(); i++){
			BlogaBetTip tip = blogaBetTips.get(i);	
			boolean found = false;
			for(int j = 0; j < blogaBetBets.size(); j++){
				Bet bet = blogaBetBets.get(j);
				BlogaBetTip betTip = (BlogaBetTip)gson.fromJson(bet.getTipJsonString(), BlogaBetTip.class);	
				if(Math.abs(tip.startDate.getTime() - betTip.startDate.getTime()) < 60 * 60 * 2000 && betTip.tipster.equals(tip.tipster) && betTip.selection.equals(tip.selection) && betTip.host.equals(tip.host) && betTip.guest.equals(tip.guest)){
					found = true;
					break;
				}
			}
			if(!found){
				System.out.println(tip.toString());
				try {
					fw2.write(tip.toString() + "\r\n\r\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			fw.close();
			fw2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		}
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
		createMissedTips();
	}
}
