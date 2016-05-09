package bettingBot;

import java.text.Normalizer;

public class TeamMapping {

	private static final double similarityThreshold = 0.75;
	private static final double similarityThresholdBothChecked = 0.5;
	
	private static String replaceAbrevations(String team){
		String res = team.replaceAll("UTD", "UNITED");
		return res;
	}
	
	private static String deleteUnessential(String team){
		String res = team.replaceAll("\\(.*\\)", "");
		return res;
	}
	
	private static String postProcess(String team){
		String res = team.toUpperCase();
		res = Normalizer.normalize(res, Normalizer.Form.NFD);
		res = deleteUnessential(res); 
		res = replaceAbrevations(res);
		res = replaceBetInfos(res);
		return res;
	}
	
	private static String replaceBetInfos(String team){
		String res = team;
		res = res.replaceAll("-", "");
		res = res.replaceAll("OVER", "");
		res = res.replace("UNDER", "");
		return res;
	}
	
	public static boolean teamsMatch(String team0, String team1){
		if(team0.length() == 0 || team1.length() == 0)
			return false;
		
		String t0 = postProcess(team0);
		String t1 = postProcess(team1);
		if(t0.equalsIgnoreCase(t1))
			return true;
		
		double similarity = LetterPairSimilarity.compareStrings(t0, t1);		
		if(similarity > similarityThreshold){
			return true;
		}
		
		return false;
	}
	
	public static boolean teamsMatch(String home0, String home1, String guest0, String guest1){
		if(home0.length() == 0 || home1.length() == 0 || guest0.length() == 0 || guest1.length() == 0)
			return false;
		
		String t0 = postProcess(home0);
		String t1 = postProcess(home1);
		String t2 = postProcess(guest0);
		String t3 = postProcess(guest1);
		if(t0.equalsIgnoreCase(t1))
			return true;
		
		double similarity0 = LetterPairSimilarity.compareStrings(t0, t1);		
		double similarity1 = LetterPairSimilarity.compareStrings(t2, t3);	
		if(similarity0 > similarityThresholdBothChecked && similarity1 > similarityThresholdBothChecked){
			return true;
		}
		
		return false;
	}
}
