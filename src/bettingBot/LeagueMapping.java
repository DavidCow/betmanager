package bettingBot;

public class LeagueMapping {
	
	private static final double similarityThreshold = 0.75;
	
	public static boolean leaguesMatch(String league0, String league1){
		if(league0.length() == 0 || league1.length() == 0)
			return false;
		
		double similarity = LetterPairSimilarity.compareStrings(league0, league1);		
		if(similarity > similarityThreshold){
			return true;
		}
		
		String italy_lega_pro = "Italy Lega Pro";
		if(league0.contains(italy_lega_pro) && league1.contains(italy_lega_pro))
			return true;
		
		if((league0.contains("Eredivisie") || league0.contains("Eerste Divisie")) && 
				(league1.contains("Eredivisie") || league1.contains("Eerste Divisie")))
			return true;
		
		
		return false;
	}

	public static void main(String[] args) {
		String a = "Italy Serie A";
		String b = "Italian Serie A";
		System.out.println(LetterPairSimilarity.compareStrings(a, b));

	}

}
