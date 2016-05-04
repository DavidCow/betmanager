package betadvisor;

import java.util.Date;

/**
 * 
 * @author Patryk Hopner
 *
 * Class containing the data of one Row of the betadvisor historical tipster data
 */
public class BetAdvisorElement implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date gameDate;
	private String category;
	private String league;
	private String event;
	private String eventResult;
	private String typeOfBet;
	private String selection;
	private double odds;
	private String bookMaker;
	private double take;
	private double profit;
	private String result;
	private String tipster;
	private String sport;
	private Date publicationDate;
	
	public BetAdvisorElement(Date gameDate, String category, String league, String event, String eventResult, String typeOfBet, String selection,
			                 double odds, String bookMaker, double take, double profit, String result, String tipster, String sport, Date publicationDate){
		
		this.gameDate = gameDate;
		this.category = category;
		this.league = league;
		this.event = event;
		this.eventResult = eventResult;
		this.typeOfBet = typeOfBet;
		this.selection = selection;
		this.odds = odds;
		this.bookMaker = bookMaker;
		this.take = take;
		this.profit = profit;
		this.result = result;
		this.tipster = tipster;
		this.sport = sport;
		this.publicationDate = publicationDate;
	}
	
	@Override
	public String toString(){
		String s = "GameDate: " + gameDate.toString() + ", ";
		s += "Category: " + category + ", ";
		s += "League: " + league + ", ";
		s += "Event: " + event + ", ";
		s += "Event Result: " + eventResult + ", ";
		s += "Type of Bet: " + typeOfBet + ", ";
		s += "Selection: " + selection + ", ";
		s += "Odds: " + odds + ", ";
		s += "Bookmaker: " + bookMaker + ", ";
		s += "Take: " + take + ", ";
		s += "Profit: " + profit + ", ";
		s += "Result: " + result + ", ";
		s += "Tipster: " + tipster + ", ";
		s += "Sport: " + sport + ", ";
		s += "Publication Date: " + publicationDate.toString() + ", ";
		return s;
	}

	/**
	 * @return the selection
	 */
	public String getSelection() {
		return selection;
	}

	/**
	 * @return the gameDate
	 */
	public Date getGameDate() {
		return gameDate;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the league
	 */
	public String getLeague() {
		return league;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @return the eventResult
	 */
	public String getEventResult() {
		return eventResult;
	}

	/**
	 * @return the typeOfBet
	 */
	public String getTypeOfBet() {
		return typeOfBet;
	}

	/**
	 * @return the odds
	 */
	public double getOdds() {
		return odds;
	}

	/**
	 * @return the bookMaker
	 */
	public String getBookMaker() {
		return bookMaker;
	}

	/**
	 * @return the take
	 */
	public double getTake() {
		return take;
	}

	/**
	 * @return the profit
	 */
	public double getProfit() {
		return profit;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @return the tipster
	 */
	public String getTipster() {
		return tipster;
	}

	/**
	 * @return the sport
	 */
	public String getSport() {
		return sport;
	}

	/**
	 * @return the publicationDate
	 */
	public Date getPublicationDate() {
		return publicationDate;
	}
}
