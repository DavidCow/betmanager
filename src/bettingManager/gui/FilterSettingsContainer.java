package bettingManager.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bettingManager.gui.OptionsTipstersController.TipsterRow;
import bettingManager.statsCalculation.Alias;
import javafx.collections.ObservableList;

/**
 * Container, that keeps track of all current active filters
 * @author David
 *
 */
public class FilterSettingsContainer {
	/**
	 *  HISTORIC / REAL, BOTH
	 *  dateState is an int, which can be 0,1,2
	 *  
	 * 	public static int CHECKBOX_GROUP1_HISTORIC = 0;
	 *  public static int CHECKBOX_GROUP1_REAL = 1;
	 *  public static int CHECKBOX_GROUP1_BOTH = 2;
	 *  From the class Checkbox1Controller
	 *  
	 *  Represents what the user has chosen
	 * 
	 */
	private int dataState = Checkbox1Controller.CHECKBOX_GROUP1_HISTORIC;
	
	
	/**
	 * SITE
	 * This is a List with Strings "BlogaBet" and/or "BetAdvisor"
	 * The ones that are in the list, are currently selected by
	 * the user.
	 */
	private ArrayList<String> sitesList = new ArrayList<String>();
	
	
	/**
	 * AVERAGE ODDS
	 * Contains the GreaterThan, LessThan, Between-And
	 * values "Average Odds".
	 * IMPORTANT: Notice that a value is -1 if the user has left the TextField empty. 
	 */
	private OddsData oddsDataAverageOdds = new OddsData();
	
	/**
	 * KoB
	 * A list of the selected KoB's the user has chosen.
	 * "Asian Handicap", "Over - Under", "X Result", "1 2 Result", "Final score"
	 * The ones that are in the list, are the ones that are selected currently.
	 */
	private ArrayList<String> koBList = new ArrayList<String>();
	
	
	/**
	 * LIQUIDITY
	 * Contains the GreaterThan, LessThan, Between-And
	 * values "Liquidity".
	 * IMPORTANT: Notice that a value is -1 if the user has left the TextField empty. 
	 */
	private OddsData oddsDataLiquidity = new OddsData();

	/**
	 * DATE RANGE
	 * Contains information about what the user has chosen in the "Date Range" filter
	 * What Month, Day, Before, After, Between and Last "Tips"
	 * Please check the class Documentation (DateRangeMessage) for more detailed information.
	 */
	private DateRangeMessage dateRangeMessage = new DateRangeMessage();
	
	/**
	 * TIPSTERS
	 * Saving the tipsters that are selected
	 */
	private Map<String, Boolean> tipstersMessage = new HashMap<String, Boolean>();
	
	
	/**
	 * ODDSOFTHETIP
	 * Contains the GreaterThan, LessThan, Between-And
	 * values "".
	 * IMPORTANT: Notice that a value is -1 if the user has left the TextField empty. 
	 */
	private OddsData oddsDataOddsOfTheTip = new OddsData();
	
	
	public Map<String, Boolean> getTipstersMessage() {
		return tipstersMessage;
	}

	private ArrayList<Alias> aliases = new ArrayList<Alias>();


	public ArrayList<Alias> getAliases() {
		return aliases;
	}


	public void setAliases(ArrayList<Alias> aliases) {
		this.aliases = aliases;
	}


	public void setTipstersMessage(Map<String, Boolean> tipstersMessage) {
		for(String s:tipstersMessage.keySet()) {
			this.tipstersMessage.put(s, tipstersMessage.get(s));
		}
//		this.tipstersMessage = tipstersMessage;
	}

	private String limiter = " | ";
	
	
	public DateRangeMessage getDateRangeMessage() {
		return dateRangeMessage;
	}


	public void setDateRangeMessage(DateRangeMessage dateRangeMessage) {
		this.dateRangeMessage = dateRangeMessage;
	}


	public int getDataState() {
		return dataState;
	}


	public void setDataState(int dataState) {
		this.dataState = dataState;
	}


	public ArrayList<String> getSitesList() {
		return sitesList;
	}


	public void setSitesList(ArrayList<String> sitesList) {
		this.sitesList = sitesList;
	}


	public OddsData getOddsDataAverageOdds() {
		return oddsDataAverageOdds;
	}


	public void setOddsDataAverageOdds(OddsData oddsDataAverageOdds) {
		this.oddsDataAverageOdds = oddsDataAverageOdds;
	}


	public ArrayList<String> getKoBList() {
		return koBList;
	}


	public void setKoBList(ArrayList<String> koBList) {
		this.koBList = koBList;
	}


	public OddsData getOddsDataLiquidity() {
		return oddsDataLiquidity;
	}


	public void setOddsDataLiquidity(OddsData oddsDataLiquidity) {
		this.oddsDataLiquidity = oddsDataLiquidity;
	}
	
	

	
	
	/**
	 * Everything below this line is just for creating a String 
	 * for "Active Filters:" in the UI.
	 */

	@Override
	public String toString() {
		System.out.println("--------------------------------------");
		System.out.println("FilterSettingsContainer:");
		System.out.println("DataState:"+getDataState());
//		System.out.println("");
		System.out.println("Sites:"+getSitesList());
		System.out.println("AverageOdds:"+getOddsDataAverageOdds());
		System.out.println("KoB:"+getKoBList());
		System.out.println("Liquidity:"+getOddsDataLiquidity());
		System.out.println("OddsOfTheTip:"+getOddsDataOddsOfTheTip());
		return "--------------------------------------";
	}
	

	

	public OddsData getOddsDataOddsOfTheTip() {
		return oddsDataOddsOfTheTip;
	}


	public void setOddsDataOddsOfTheTip(OddsData oddsDataOddsOfTheTip) {
		this.oddsDataOddsOfTheTip = oddsDataOddsOfTheTip;
	}


	/**
	 * This is the String for the "Active Filters:" in the UI
	 * @return
	 */
	public String getActiveFiltersString() {
		String activeFiltersString = "";
		
		activeFiltersString += getDataString();
		activeFiltersString += limiter;
		
		activeFiltersString += dateRangeMessage.getAllFiltersLabel();
		activeFiltersString += limiter;

		
		String sitesTempString = getSitesString();
		if (!sitesTempString.isEmpty()) {
			activeFiltersString += sitesTempString;
			activeFiltersString += limiter;
		}
		
		String avgOdds = getAverageOddsString();
		if (!avgOdds.isEmpty()) {
			activeFiltersString += avgOdds;
			activeFiltersString += limiter;
		}
		
		String kob = getKoBString();
		if (!kob.isEmpty()) {
			activeFiltersString += kob;
			activeFiltersString += limiter;
		}

		String liq = getLiquidityString();
		if (!liq.isEmpty()) {
			activeFiltersString += liq;
			activeFiltersString += limiter;
		}
		
		String oddsOfTheT = getOddsOfTheTipString();
		if (!oddsOfTheT.isEmpty()) {
			activeFiltersString += oddsOfTheT;
			activeFiltersString += limiter;
		}
		return activeFiltersString;
	}
	
	private String getOddsOfTheTipString() {
		return oddsDataLiquidity.getAllFiltersLabel("Odds of the Tip");
	}

	/**
	 * Used to assemble the "Active Filter:" string
	 * @return
	 */
	private String getLiquidityString() {
		return oddsDataLiquidity.getAllFiltersLabel("Liquidity");
	}
	
	/**
	 * Used to assemble the "Active Filter:" string
	 * @return
	 */
	private String getKoBString() {
		String koBLabel = "";
		for(int i=0; i<getKoBList().size(); i+=1) {
			koBLabel += getKoBList().get(i);
			if(i < getKoBList().size()-1) {
				koBLabel += ", ";
			}
		}
		return koBLabel;
	}


	/**
	 * Used to assemble the "Active Filter:" string
	 * @return
	 */
	private String getAverageOddsString() {
		return oddsDataAverageOdds.getAllFiltersLabel("Average Odds");
	}


	/**
	 * Used to assemble the "Active Filter:" string
	 * @return
	 */
	private String getSitesString() {
		String siteLabel = "";
		for(int i=0; i<getSitesList().size(); i+=1) {
			siteLabel += getSitesList().get(i);
			if(i < getSitesList().size()-1) {
				siteLabel += ", ";
			}
		}
		return siteLabel;
	}
	
	/**
	 * Used to assemble the "Active Filter:" string
	 * @return
	 */
	private String getDataString() {
		if (getDataState() == Checkbox1Controller.CHECKBOX_GROUP1_HISTORIC) {
			return "Historic";
		} else if (getDataState() == Checkbox1Controller.CHECKBOX_GROUP1_REAL) {
			return "Real";
		}  else if (getDataState() == Checkbox1Controller.CHECKBOX_GROUP1_BOTH) {
			return "Historic and Real";
		} 
		
		return "Should never show up";
	}

}
