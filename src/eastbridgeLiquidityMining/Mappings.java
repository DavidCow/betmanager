package eastbridgeLiquidityMining;

import java.util.HashMap;
import java.util.Map;

import bettingBot.LetterPairSimilarity;

public class Mappings {
	
	public static final Map<String,String> league_to_country;
	public static final Map<String,String> league_ranks;
	
	static{
		league_to_country = new HashMap<String, String>();
		league_to_country.put("France Ligue 2", "France");
		league_to_country.put("France Ligue 1", "France");
		league_to_country.put("Romania Liga I (play Off)", "Romania");
		league_to_country.put("Sweden Superettan", "Sweden");
		league_to_country.put("Germany Bundesliga 2", "Germany");
		league_to_country.put("Finland Cup", "Finland");
		league_to_country.put("Germany 3rd Liga", "Germany");
		league_to_country.put("Sweden Allsvenskan", "Sweden");
		league_to_country.put("English National League", "England");
		league_to_country.put("Uefa Europa League - Specials To Qualify", "Europa League");
		league_to_country.put("Austria Erste Liga", "Austria");
		league_to_country.put("Korea K League Classic", "Korea");
		league_to_country.put("Usa Major League Soccer", "USA");
		league_to_country.put("Italy Serie A", "Italy");
		league_to_country.put("English League Championship", "England");
		league_to_country.put("Russia Premier League", "Russia");
		league_to_country.put("Poland Ekstraklasa", "Poland");
		league_to_country.put("Switzerland Raiffeisen Super League", "Switzerland");
		league_to_country.put("Greece Super League", "Greece");
		league_to_country.put("Germany Cup", "Germany");
		league_to_country.put("English Premier League", "England");
		league_to_country.put("Japan J-league Division 1", "Japan");
		league_to_country.put("Norway Tippeligaen", "Norway");
	}
	
	static{
		league_ranks = new HashMap<String, String>();
		league_ranks.put("France Ligue 2", "2");
		league_ranks.put("France Ligue 1", "1");
		league_ranks.put("Romania Liga I (play Off)", "1");
		league_ranks.put("Sweden Superettan", "2");
		league_ranks.put("Germany Bundesliga 2", "2");
		league_ranks.put("Finland Cup", "National Cup");
		league_ranks.put("Germany 3rd Liga", "3");
		league_ranks.put("Sweden Allsvenskan", "1");
		league_ranks.put("English National League", "3");
		league_ranks.put("Uefa Europa League - Specials To Qualify", "Europa League");
		league_ranks.put("Austria Erste Liga", "1");
		league_ranks.put("Korea K League Classic", "1");
		league_ranks.put("Usa Major League Soccer", "1");
		league_ranks.put("Italy Serie A", "1");
		league_ranks.put("English League Championship", "2");
		league_ranks.put("Russia Premier League", "1");
		league_ranks.put("Poland Ekstraklasa", "Poland");
		league_ranks.put("Switzerland Raiffeisen Super League", "1");
		league_ranks.put("Greece Super League", "1");
		league_ranks.put("Germany Cup", "National Cup");
		league_ranks.put("English Premier League", "1");
		league_ranks.put("Japan J-league Division 1", "1");
		league_ranks.put("Norway Tippeligaen", "1");
		
	}
	
	public static String extractCountryInfo(String s, double threshold){
		String[] splits = s.split(" ");
		if(LetterPairSimilarity.compareStrings(splits[0], "Albania") > threshold)
			return "Albania";
		if(LetterPairSimilarity.compareStrings(splits[0], "Algeria") > threshold)
			return "Algeria";
		if(LetterPairSimilarity.compareStrings(splits[0], "Argentina") > threshold)
			return "Argentina";
		if(LetterPairSimilarity.compareStrings(splits[0], "Austria") > threshold)
			return "Austria";
		if(LetterPairSimilarity.compareStrings(splits[0], "Australia") > threshold)
			return "Australia";
		if(LetterPairSimilarity.compareStrings(splits[0], "Belgium") > threshold)
			return "Belgium";
		if(LetterPairSimilarity.compareStrings(splits[0], "Brazil") > threshold)
			return "Brazil";
		if(LetterPairSimilarity.compareStrings(splits[0], "Bulgaria") > threshold)
			return "Bulgaria";
		if(LetterPairSimilarity.compareStrings(splits[0], "Canada") > threshold)
			return "Canada";
		if(LetterPairSimilarity.compareStrings(splits[0], "China") > threshold)
			return "China";
		if(LetterPairSimilarity.compareStrings(splits[0], "Croatia") > threshold)
			return "Croatia";
		if(LetterPairSimilarity.compareStrings(splits[0], "Czech") > threshold)
			return "Czech Republic";
		if(LetterPairSimilarity.compareStrings(splits[0], "Denmark") > threshold)
			return "Denmark";
		if(LetterPairSimilarity.compareStrings(splits[0], "Finland") > threshold)
			return "Finland";
		if(LetterPairSimilarity.compareStrings(splits[0], "France") > threshold)
			return "France";
		if(LetterPairSimilarity.compareStrings(splits[0], "Germany") > threshold)
			return "Germany";
		if(LetterPairSimilarity.compareStrings(splits[0], "Greece") > threshold)
			return "Greece";
		if(LetterPairSimilarity.compareStrings(splits[0], "Hungary") > threshold)
			return "Hungary";
		if(LetterPairSimilarity.compareStrings(splits[0], "Israel") > threshold)
			return "Israel";
		if(LetterPairSimilarity.compareStrings(splits[0], "Iran") > threshold)
			return "Iran";
		if(LetterPairSimilarity.compareStrings(splits[0], "Italy") > threshold)
			return "Italy";
		if(LetterPairSimilarity.compareStrings(splits[0], "Japan") > threshold)
			return "Japan";
		if(LetterPairSimilarity.compareStrings(splits[0], "Korea") > threshold)
			return "Korea";
		if(LetterPairSimilarity.compareStrings(splits[0], "Finland") > threshold)
			return "Finland";
		if(LetterPairSimilarity.compareStrings(splits[0], "Finland") > threshold)
			return "Finland";
		if(LetterPairSimilarity.compareStrings(splits[0], "Finland") > threshold)
			return "Finland";
		return "";
		
		
	}

}
