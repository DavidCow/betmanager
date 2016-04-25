package moneyManagement;

import java.io.IOException;
import java.util.List;

import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import weka.core.Instance;
import yieldPrediction.betAdvisor.PredictiveModel;


public class Kelly {
	
	private static PredictiveModel model = new PredictiveModel("Yield.arff", "yieldCluster.model");
	// Liquidity Model
	private static eastbridgeLiquidityMining.regression.PredictiveModel liquidityModel = new eastbridgeLiquidityMining.regression.PredictiveModel("EastBridge6BackTest.arff", "bagging.model");

	public static double brPercent(double odds, double winPercentage){
		double r = (winPercentage * (odds) - 1) / odds;
		return r;
	}
	
	public static double brPercent(BetAdvisorElement element) throws Exception{
		String tipster = element.getTipster();
		String typeOfBet = element.getTypeOfBet();
		typeOfBet = typeOfBet.toUpperCase();
		typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
		double odds = element.getOdds();
		if(!typeOfBet.equalsIgnoreCase("MATCH ODDS"))
			odds++;
		
		Instance record2 = null;
		try{
			record2 = liquidityModel.createWekaInstance(element);
		}catch(Exception e){
			
		}
		double liquidity = -1;
		try {
			if(record2 != null)
				liquidity = liquidityModel.classifyInstance(record2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Instance record = model.createWekaInstance(tipster, typeOfBet, odds, liquidity);
		double winPercent = model.predictWinPercent(record);
		double brP = brPercent(element.getOdds(), winPercent);
		return brP;
	}
	
	static void testBrPercent() throws IOException{
		// Load historical Data
		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
		List<BetAdvisorElement> betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
		
		double br = 10000;
		
		for(int i = 0; i < betAdvisorList.size(); i++){
			try{
				BetAdvisorElement element = betAdvisorList.get(i);
				String tipster = element.getTipster();
				String typeOfBet = element.getTypeOfBet();
				typeOfBet = typeOfBet.toUpperCase();
				typeOfBet = typeOfBet.replaceAll(" 1ST HALF", "");
				double odds = element.getOdds();
				if(!typeOfBet.equalsIgnoreCase("MATCH ODDS"))
					odds++;
				
				Instance record2 = liquidityModel.createWekaInstance(element);
				if(record2 == null)
					continue;
				double liquidity = -1;
				try {
					liquidity = liquidityModel.classifyInstance(record2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				double brP = brPercent(element);
				if(brP <= 0)
					continue;
				double take = Math.min(liquidity, br * brP);
				if(element.getProfit() < 0)
					br -= take;
				else if(element.getProfit() > 0)
					br += take * 0.99 * element.getOdds() - take;			
				System.out.println(br);
			}catch(Exception e){
				
			}
		}
		System.out.println();
	}
	
	public static void main(String[] args) throws IOException {
		testBrPercent();
	}
}
