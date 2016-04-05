package eastbridgeLiquidityMining;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import bettingBot.entities.BetTicket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import eastbridgeLiquidityMining.database.EastbridgeLiquidityDatabase;

public class LiquidityAnalyser {

	private EastbridgeLiquidityDatabase database;
	private HashSet<String> leagues;
	
	public LiquidityAnalyser(){
		try {
			database = new EastbridgeLiquidityDatabase();
			leagues = new HashSet<String>();
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
			String league = event.getLeague();
			String[] cut_out_specials = league.split(" - ");
			String[] cut_out_brackets = cut_out_specials[0].split("\\(");
			leagues.add(cut_out_brackets[0].trim());
//			leagues.add(league);
			num_events++;
			if(num_events%100==0)
				System.out.println(num_events);
//			ResultSet rS2 = database.getRecordsForEvent(eventId);
//			List<Record> records = new ArrayList<Record>();
//			List<BetTicket> betTickets = new ArrayList<BetTicket>();
//			while(rS2.next()){
//				String betTicketJsonString = rS2.getString("betTicketJsonString");
//				String recordJsonString = rS2.getString("recordJsonString");
//				BetTicket betTicket = (BetTicket)gson.fromJson(betTicketJsonString, BetTicket.class);
//				Record record = (Record)gson.fromJson(recordJsonString, recordClass);
//				betTickets.add(betTicket);
//				records.add(record);
////				System.out.println(rS2.getLong("time"));
//			}
		}
	}
	
	public static void main(String[] args) throws JsonSyntaxException, SQLException {
		LiquidityAnalyser analyser = new LiquidityAnalyser();
		analyser.iterateAllEvents();
		for(String s : analyser.leagues)
			System.out.println(s);
		System.out.println(analyser.leagues.size());
	}
}
