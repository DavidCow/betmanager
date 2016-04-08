package eastbridgeLiquidityMining;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import bettingBot.LetterPairSimilarity;
import bettingBot.entities.BetTicket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import eastbridgeLiquidityMining.database.EastbridgeLiquidityDatabase;
import eastbridgeLiquidityMining.regression.ArffCreator;

public class LiquidityAnalyser {

	private EastbridgeLiquidityDatabase database;
	private TreeSet<String> leagues;
	
	public LiquidityAnalyser(){
		try {
			database = new EastbridgeLiquidityDatabase();
			leagues = new TreeSet<String>();
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		} 
	}
	
	public void iterateAllEvents() throws JsonSyntaxException, SQLException{
		Gson gson = new Gson();
		Class recordClass = null;
		Class eventClass = null;
		File f0 = new File("event.dat");
		File f1 = new File("record.dat");
		int num_events = 0;
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
			System.out.println("Objects loaded from Inputstream");
		}	
		else{
			System.out.println("FILES NOT FOUND");
			return;
		}
		ResultSet rS = database.getAllEvents();
		while(rS.next()){
			int eventId = rS.getInt("id");
			String eventJsonString = rS.getString("eventJsonString");
			SoccerEvent event = (SoccerEvent)gson.fromJson(eventJsonString, eventClass);
//			String league = event.getLeague();
//			String[] cut_out_specials = league.split(" - ");
//			String[] cut_out_brackets = cut_out_specials[0].split("\\(");
//			leagues.add(cut_out_brackets[0].trim());
//			leagues.add(league);
			num_events++;
			if(num_events%100==0)
				System.out.println(num_events);
			ResultSet rS2 = database.getRecordsForEvent(eventId);
			List<Record> records = new ArrayList<Record>();
			List<BetTicket> betTickets = new ArrayList<BetTicket>();
			while(rS2.next()){
				String betTicketJsonString = rS2.getString("betTicketJsonString");
				String recordJsonString = rS2.getString("recordJsonString");
				BetTicket betTicket = (BetTicket)gson.fromJson(betTicketJsonString, BetTicket.class);
				Record record = (Record)gson.fromJson(recordJsonString, recordClass);
				betTickets.add(betTicket);
				records.add(record);
				if(betTicket.getMaxStake() < 100){
					System.out.println(event.getHost());
					System.out.println(event.getGuest());
					System.out.println(event.getLeague());
					System.out.println(betTicket.getMaxStake());
					System.out.println("****************************************************************");
				}
//				System.out.println(rS2.getLong("time"));
			}
		}
	}
	
	public void getTeamNames() throws JsonSyntaxException, SQLException{
		Gson gson = new Gson();
		Class recordClass = null;
		Class eventClass = null;
		File f0 = new File("event.dat");
		File f1 = new File("record.dat");
		int num_events = 0;
		HashSet<String> teams = new HashSet<String>();
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
			System.out.println("Objects loaded from Inputstream");
		}	
		else{
			System.out.println("FILES NOT FOUND");
			return;
		}
		ResultSet rS = database.getAllEvents();
		while(rS.next()){
			int eventId = rS.getInt("id");
			String eventJsonString = rS.getString("eventJsonString");
			SoccerEvent event = (SoccerEvent)gson.fromJson(eventJsonString, eventClass);
			String host = event.getHost();
			String guest = event.getGuest();
			String league = event.getLeague();
			if(!(host.contains("No.of Corners") || host.contains("Total Bookings"))){
				host = ArffCreator.getCleanedNames(host);
				String s  = ArffCreator.getCleanedNames(league);
				if(s.contains("Turkey Super"))
					teams.add(host);
			}
			if(!(guest.contains("No.of Corners") || guest.contains("Total Bookings"))){
				guest = ArffCreator.getCleanedNames(guest);
				String s  = ArffCreator.getCleanedNames(league);
				if(s.contains("Turkey Super"))
					teams.add(guest);
			}
//			if(!(host.contains("No.of Corners") || host.contains("Total Bookings"))){
//				host = host.replaceAll(" - .*", "");
//				teams.add(host);
//			}
//			if(!(guest.contains("No.of Corners") || guest.contains("Total Bookings"))){
//				guest = guest.replaceAll(" - .*", "");
//				teams.add(guest);
//			}
//			if(!(league.contains("No.of Corners") || league.contains("Total Bookings"))){
//				league = league.replaceAll(" - .*", "");
//				leagues.add(league);
//			}
			num_events++;
			if(num_events%100==0)
				System.out.println(num_events);
		}
		for(String s : teams){
//			if(s.contains("English Premier League") || s.contains("Spain Primera Laliga") ||
//					s.contains("Italy Serie A") || s.contains("Germany-bundesliga I"))
				System.out.println(s);
		}
			
		System.out.println(teams.size());

	}
	
	public static void main(String[] args) throws JsonSyntaxException, SQLException {
		LiquidityAnalyser analyser = new LiquidityAnalyser();
		analyser.iterateAllEvents();
//		for(String s : analyser.leagues){
//			System.out.println(s);
//		}
//		String s = "Crystal Palace			125.75";
//		String st = s.replaceAll("(.+)(\\t+)(.+)", "$3");
//		System.out.println(st);
//		String s2 = "J-league";
//		s = s.replaceAll(" Total Bookings", "");
//		s2 = s2.replaceAll(" - .*", "");
//
//		System.out.println(s);
//		System.out.println(s2);

	}
}
