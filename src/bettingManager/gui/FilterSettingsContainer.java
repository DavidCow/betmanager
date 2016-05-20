package bettingManager.gui;

import java.util.ArrayList;

/**
 * Container, that keeps track of all current active filters
 * @author David
 *
 */
public class FilterSettingsContainer {
	/**
	 * Historic / Real, both
	 */
	private int dataState = Checkbox1Controller.CHECKBOX_GROUP1_HISTORIC;
	
	/**
	 * More
	 */
	
	
	/**
	 * Site
	 */
	private ArrayList<String> sitesList = new ArrayList<String>();
	
	/**
	 * Average odds
	 */
	private OddsData oddsDataAverageOdds = new OddsData();
	
	/**
	 * KoB
	 */
	private ArrayList<String> koBList = new ArrayList<String>();
	
	
	/**
	 * Liquidity
	 */
	private OddsData oddsDataLiquidity = new OddsData();

	/**
	 * DateRange
	 * @return
	 */
	private DateRangeMessage dateRangeMessage = new DateRangeMessage();
	
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
		return "--------------------------------------";
	}
	
	private final String title = "Active Filters: ";
	private String limiter = " | ";
	
	public String getActiveFiltersString() {
		String activeFiltersString = "";
		
		activeFiltersString += getDataString();
		activeFiltersString += limiter;
		
		activeFiltersString += dateRangeMessage.getAllFiltersLabel();
		activeFiltersString += limiter;

		activeFiltersString += getSitesString();
		activeFiltersString += limiter;

		
		return activeFiltersString;
	}
	
	private String getSitesString() {
		String siteLabel = "";
		for(String s:getSitesList()) {
			siteLabel += s;
			siteLabel += ", ";
		}
		return siteLabel;
	}
	
	/**
	 * Real/Historic or Both string
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
