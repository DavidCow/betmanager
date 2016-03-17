package bettingBot.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mailParsing.BetAdvisorEmailParser;
import mailParsing.BetAdvisorTip;
import mailParsing.GMailReader;
import mailParsing.ParsedTextMail;
import bettingBot.entities.Bet;

public class BettingBotDatabase {

	private String dbName = "BettingBot";
	private static String userName = "postgres";
	private static String password = "postgrespass";
	private static String port = "5433";

	private Connection       db;        // A connection to the database
	private DatabaseMetaData dbmd;      // This is basically info the driver delivers about the DB it just connected to.
	
	public BettingBotDatabase() throws ClassNotFoundException, SQLException{
		
		/* initialize connection */
		Class.forName("org.postgresql.Driver");
		db = DriverManager.getConnection("jdbc:postgresql://localhost:" + port + "/" + dbName, userName, password); //connect to the db
		dbmd = db.getMetaData(); //get MetaData to confirm connection
		System.out.println("Connection to " + dbmd.getDatabaseProductName() + " " + dbmd.getDatabaseProductVersion() + " URL: " + dbmd.getURL() + " successful.\n");
		Statement sql = db.createStatement(); //create a statement that we can use later	
		
		/* Create necessary tables */
	    
	    // Tips Table
		// Contains all the processed tips, not all the tips we received
	    String createBetAdvisor = "CREATE TABLE IF NOT EXISTS processed_tips " +
	            "(event VARCHAR(255) NOT NULL, " +
	            " tipster VARCHAR(255) NOT NULL, " +
	            " date BIGINT NOT NULL, " +
	            " host VARCHAR(255), " + 
	            " guest VARCHAR(255), " + 
	            " typeOfBet VARCHAR(255), " + 
	            " betOn VARCHAR(255), " + 
	            " bestOdds DOUBLE PRECISION, " + 
	            " noBetUnder DOUBLE PRECISION, " + 
	            " pivotValue DOUBLE PRECISION, " + 
	            " pivotBias VARCHAR(255), " + 
	            " PRIMARY KEY ( event, tipster, date ))"; 
	    sql.executeUpdate(createBetAdvisor);
	    
	    // Bets Table
	    String createBets = "CREATE TABLE IF NOT EXISTS bets " +
	            "(id VARCHAR(255) NOT NULL, " +
	            " reqId VARCHAR(255), " +
	            " betAmount DOUBLE PRECISION, " + 
	            " betOdd DOUBLE PRECISION, " + 
	            " betStatus INTEGER, " + 
	            " tipJsonString VARCHAR, " +
	            " eventJsonString TEXT, " +
	            " recordJsonString VARCHAR, " +
	            " selection VARCHAR(255), " +
	            " PRIMARY KEY ( id ))"; 
	    sql.executeUpdate(createBets);
	}
	
	public void addBet(Bet bet) throws SQLException{
		addBet(bet.getId(), bet.getReqId(), bet.getBetAmount(), bet.getBetOdd(), bet.getBetStatus(), bet.getTipJsonString(), bet.getEventJsonString(), bet.getRecordJsonString(), bet.getSelection());
	}
	
	private void addBet(String id, String reqId, double betAmount, double betOdd, int betStatus, String tipJSsonString, String eventJsonString, String recordJsonString, String selection) throws SQLException{
		Statement sT = null;
		try {
			sT = db.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String addBet = "INSERT INTO bets (id, reqId, betAmount, betOdd, betStatus, tipJsonString, eventJsonString, recordJsonString, selection)";
		addBet += "VALUES ('" + id + "','" + reqId + "'," + betAmount + "," + betOdd + "," + betStatus + ",'" +  tipJSsonString + "','" + eventJsonString + "','" + recordJsonString + "','" +  selection + "')";
		sT.executeUpdate(addBet);		
	}
	
	public List<Bet> getAllBets(){
		Statement sT = null;
		try {
			sT = db.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ResultSet rs = null;
		try {
			rs = sT.executeQuery("SELECT * FROM bets");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	
		List<Bet> bets = new ArrayList<Bet>();
		if(rs != null){
			try {
				while(rs.next()){
					Bet b = new Bet();
					String id = rs.getString("id");
					String reqId = rs.getString("reqId");
					double betAmount = rs.getDouble("betAmount");
					double betOdd = rs.getDouble("betOdd");
					int betStatus = rs.getInt("betStatus");
					String tipJsonString = rs.getString("tipJsonString");
					String eventJsonString = rs.getString("eventJsonString");
					String recordJsonString = rs.getString("recordJsonString");
					String selection = rs.getString("selection");
					b.setBetAmount(betAmount);
					b.setBetOdd(betOdd);
					b.setBetStatus(betStatus);
					b.setId(id);
					b.setReqId(reqId);
					b.setTipJsonString(tipJsonString);
					b.setEventJsonString(eventJsonString);
					b.setRecordJsonString(recordJsonString);
					b.setSelection(selection);
					bets.add(b);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return bets;
	}
	
	public void updateBet(String id, int betStatus){
		Statement sT = null;
		try {
			sT = db.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String updateBet = "UPDATE bets set betStatus=" + betStatus + " WHERE id='" + id + "'";
		try {
			sT.executeUpdate(updateBet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addProcessedTip(BetAdvisorTip tip) throws SQLException{
		addProcessedTip(tip.event, tip.tipster, tip.date.getTime(), 
		                   tip.host, tip.guest, tip.typeOfBet, tip.betOn, 
		                   tip.bestOdds, tip.noBetUnder, tip.pivotValue, tip.pivotBias);
	}
	
	private void addProcessedTip(String event, String tipster, long date, String host, String guest, String typeofBet, 
			                        String betOn, double bestOdds, double noBetUnder, double pivotValue, String pivotBias) throws SQLException
	{
		Statement sT = null;
		try {
			sT = db.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String addBetInformations = "INSERT INTO processed_tips (event, tipster, date, host, guest, typeOfBet, betOn, bestOdds, noBetUnder, pivotValue, pivotBias)";
		addBetInformations += "VALUES ('" + event + "','" + tipster + "'," + date + ",'" + host + "','" + guest + "','" + typeofBet +
				              "','" + betOn + "'," + bestOdds + "," + noBetUnder + "," + pivotValue + ",'" + pivotBias + "')";
		sT.executeUpdate(addBetInformations);
	}
	
	public boolean isTipInDatabase(String event, String tipster, long date) {
		try {
			Statement stmt = db.createStatement();
			ResultSet result = null;
			result = stmt.executeQuery("SELECT date FROM processed_tips WHERE event='" + event + "' AND tipster='" + tipster + "' AND date=" + date);
			if (!result.isBeforeFirst()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return false;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		BettingBotDatabase dataBase = new BettingBotDatabase();
		GMailReader reader = new GMailReader();
		List<ParsedTextMail> mails = reader.read("noreply@betadvisor.com");
		List<BetAdvisorTip> betInformations = new ArrayList<BetAdvisorTip>();
		for(ParsedTextMail mail : mails){
			if(mail.subject.indexOf("Tip subscription") != -1){
				System.out.println(mail.subject);
				betInformations.add(BetAdvisorEmailParser.parseTip(mail));
				System.out.println();
			}
		}
		for(int i = 0; i < betInformations.size(); i++){
			dataBase.addProcessedTip(betInformations.get(i));
		}
	}
}
