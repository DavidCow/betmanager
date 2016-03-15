package bettingBot;

import java.text.Normalizer;

public class TeamMapping {

	private static final double similarityThreshold = 0.75;
	
	private static String replaceAbrevations(String team){
		String res = team.replaceAll("UTD", "UNITED");
		return res;
	}
	
	private static String postProcess(String team){
		String res = team.toUpperCase();
		res = Normalizer.normalize(res, Normalizer.Form.NFD);
		res = replaceAbrevations(res);
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
}
