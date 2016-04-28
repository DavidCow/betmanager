package bettingBot.database;

import java.util.ArrayList;
import java.util.List;

import mailParsing.BetAdvisorEmailParser;
import mailParsing.BetAdvisorTip;
import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;
import bettingBot.entities.Bet;

import com.google.gson.Gson;

public class DatabaseCleaning {

	public static void main(String[] args) throws Exception{
		Gson gson = new Gson();
		GMailReader reader = new GMailReader("vicentbet90@gmail.com", "bmw735tdi2");
		List<ParsedTextMail> mails = reader.read("noreply@betadvisor.com");
		List<BetAdvisorTip> tips = new ArrayList<BetAdvisorTip>();
		for(ParsedTextMail mail : mails){
			if(mail.subject.indexOf("Tip subscription") != -1){
				tips.add(BetAdvisorEmailParser.parseTip(mail));
			}
		}
		BettingBotDatabase dataBase = new BettingBotDatabase();
		
		List<Bet> bets = dataBase.getAllBets();
		for(int j = 0; j < bets.size(); j++){
			Bet bet = bets.get(j);
//			for(int i = 0; i < tips.size(); i++){
//				BetAdvisorTip newTip = tips.get(i);
//				Bet bet = bets.get(j);
//				BetAdvisorTip tip = gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);
//				String tipsterSubString = newTip.tipster.substring(0, newTip.tipster.length() - 1);
//				if(tipsterSubString.equals(tip.tipster) && newTip.event.equals(tip.event)){
//					String id = bet.getId();
//					dataBase.deleteBet(id);
//					String newJsonString = gson.toJson(newTip);
//					bet.setTipJsonString(newJsonString);
//					dataBase.addBet(bet);
//					System.out.println("Bet added");
//				}
//			}
			BetAdvisorTip tip = gson.fromJson(bet.getTipJsonString(), BetAdvisorTip.class);
			System.out.println(tip.tipster);
			System.out.println(tip.take);
		}		
	}
}
