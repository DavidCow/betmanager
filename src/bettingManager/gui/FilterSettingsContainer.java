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
	private int dataState = -1;
	
	/**
	 * More
	 */
	
	
	/**
	 * Site
	 */
	private ArrayList<String> sitesList;
	
	/**
	 * Average odds
	 */
	private OddsData oddsDataAverageOdds;
	
	/**
	 * KoB
	 */
	private ArrayList<String> koBList;
	
	
	/**
	 * Liquidity
	 */
	private OddsData oddsDataLiquidity;


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
	
}
