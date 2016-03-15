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
import mailParsing.BetInformations;
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
	    
	    // Tipps Table
	    String createBetAdvisor = "CREATE TABLE IF NOT EXISTS bet_advisor " +
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
	            " PRIMARY KEY ( id ))"; 
	    sql.executeUpdate(createBets);
	}
	
	public void addBet(Bet bet) throws SQLException{
		addBet(bet.getId(), bet.getReqId(), bet.getBetAmount(), bet.getBetOdd(), bet.getBetStatus());
	}
	
	private void addBet(String id, String reqId, double betAmount, double betOdd, int betStatus) throws SQLException{
		Statement sT = null;
		try {
			sT = db.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String addBet = "INSERT INTO bets (id, reqId, betAmount, betOdd, betStatus)";
		addBet += "VALUES ('" + id + "','" + reqId + "'," + betAmount + "," + betOdd + "," + betStatus + ")";
		sT.executeUpdate(addBet);		
	}
	
	public void addBetInformations(BetInformations betInformations) throws SQLException{
		addBetInformations(betInformations.event, betInformations.tipster, betInformations.date.getTime(), 
		                   betInformations.host, betInformations.guest, betInformations.typeOfBet, betInformations.betOn, 
		                   betInformations.bestOdds, betInformations.noBetUnder, betInformations.pivotValue, betInformations.pivotBias);
	}
	
	private void addBetInformations(String event, String tipster, long date, String host, String guest, String typeofBet, 
			                        String betOn, double bestOdds, double noBetUnder, double pivotValue, String pivotBias) throws SQLException
	{
		Statement sT = null;
		try {
			sT = db.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String addBetInformations = "INSERT INTO bet_advisor (event, tipster, date, host, guest, typeOfBet, betOn, bestOdds, noBetUnder, pivotValue, pivotBias)";
		addBetInformations += "VALUES ('" + event + "','" + tipster + "'," + date + ",'" + host + "','" + guest + "','" + typeofBet +
				              "','" + betOn + "'," + bestOdds + "," + noBetUnder + "," + pivotValue + ",'" + pivotBias + "')";
		sT.executeUpdate(addBetInformations);
	}
	
	public boolean isTippInDatabase(String event, String tipster, long date) {
		try {
			Statement stmt = db.createStatement();
			ResultSet result = null;
			result = stmt.executeQuery("SELECT date FROM bet_advisor WHERE event='" + event + "' AND tipster='" + tipster + "' AND date=" + date);
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
		List<BetInformations> betInformations = new ArrayList<BetInformations>();
		for(ParsedTextMail mail : mails){
			if(mail.subject.indexOf("Tip subscription") != -1){
				System.out.println(mail.subject);
				betInformations.add(BetAdvisorEmailParser.parseMail(mail.content));
				System.out.println();
			}
		}
		for(int i = 0; i < betInformations.size(); i++){
			dataBase.addBetInformations(betInformations.get(i));
		}
	}
}
