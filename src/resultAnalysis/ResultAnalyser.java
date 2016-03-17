package resultAnalysis;

import java.util.List;

import bettingBot.database.BettingBotDatabase;
import bettingBot.entities.Bet;

public class ResultAnalyser {

	private BettingBotDatabase dataBase = null;
	
	public ResultAnalyser(){
		try {
			dataBase = new BettingBotDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
			
	public void analyseBets(){
		int numberOfRunninngBets = 0;
		int numberOfWonBets = 0;
		int numberOfLostBets = 0;
		List<Bet> bets = dataBase.getAllBets();
		for(int i = 0; i < bets.size(); i++){
			Bet bet = bets.get(i);
			if(bet.getBetStatus() == 1){
				numberOfRunninngBets++;
			}
			if(bet.getBetStatus() == 4){
				numberOfWonBets++;
			}
			if(bet.getBetStatus() == 5){
				numberOfLostBets++;
			}
		}
		System.out.println("numberOfRunninngBets: " + numberOfRunninngBets);
		System.out.println("numberOfWonBets: " + numberOfWonBets);
		System.out.println("numberOfLostBets: " + numberOfLostBets);
	}
	
	public static void main(String[] args) {
		ResultAnalyser analyser = new ResultAnalyser();
		analyser.analyseBets();
	}
}
