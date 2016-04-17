package resultAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mailParsing.BetAdvisorTip;
import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;
import bettingBot.entities.BetComparator;
import bettingBot.entities.BetTicket;

import com.google.gson.Gson;

public class OddsLiquidityAnalysis {
	
	public static void analyseBets() throws ClassNotFoundException, SQLException{
		Gson gson = new Gson();
		
		/* Initialize Classes for JSon deserialisation */
		boolean initialiseGson = true;
		Class recordClass = null;
		Class eventClass = null;
		File f0 = new File("event.dat");
		File f1 = new File("record.dat");
		if(f0.isFile() && f0.canRead() && f1.isFile() && f1.canRead()){
			initialiseGson = false;
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

		// Init database
		BettingBotDatabase database = new BettingBotDatabase();
		List<Bet> allBets = database.getAllBets();
		
		Map<String, List<Bet>> betMap = new HashMap<String, List<Bet>>();
		for(int i = 0; i < allBets.size(); i++){
			Bet bet = allBets.get(i);
			String tipJsonString = bet.getTipJsonString();
			BetAdvisorTip tip = gson.fromJson(tipJsonString, BetAdvisorTip.class);
			String key = tip.event + tip.tipster + tip.date;
			if(!betMap.containsKey(key)){
				List<Bet> innerBets = new ArrayList<Bet>();
				innerBets.add(bet);
				betMap.put(key, innerBets);
			}
			else{
				List<Bet> innerBets = betMap.get(key);
				innerBets.add(bet);
			}
		}
		for(String key : betMap.keySet()){
			List<Bet> innerBets = betMap.get(key);
			Collections.sort(innerBets, new BetComparator());
			if(innerBets.size() > 1){
				System.out.println(key);
				for(int i = 0; i < innerBets.size(); i++){
					Bet bet = innerBets.get(i);
					BetTicket betTicket = gson.fromJson(bet.getBetTicketJsonString(), BetTicket.class);
					double liquidity = betTicket.getMaxStake();
					double odds = betTicket.getCurrentOdd();
					System.out.println("Liquidity: " + liquidity);
					System.out.println("Odds: " + odds);
				}
			}
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		analyseBets();
	}
}
