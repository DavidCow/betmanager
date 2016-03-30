package eastbridgeLiquidityMining.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EastbridgeLiquidityDatabase {
	
	private String dbName = "EastbridgeLiquidity";
	private static String userName = "postgres";
	private static String password = "postgrespass";
	private static String port = "5433";

	private Connection       db;        // A connection to the database
	private DatabaseMetaData dbmd;      // This is basically info the driver delivers about the DB it just connected to.
	
	public EastbridgeLiquidityDatabase() throws ClassNotFoundException, SQLException{
		
		/* initialize connection */
		Class.forName("org.postgresql.Driver");
		db = DriverManager.getConnection("jdbc:postgresql://localhost:" + port + "/" + dbName, userName, password); //connect to the db
		dbmd = db.getMetaData(); //get MetaData to confirm connection
		System.out.println("Connection to " + dbmd.getDatabaseProductName() + " " + dbmd.getDatabaseProductVersion() + " URL: " + dbmd.getURL() + " successful.\n");
		Statement sql = db.createStatement(); //create a statement that we can use later	
		
	    // Events Table
		// Contains the events
	    String createEvents = "CREATE TABLE IF NOT EXISTS events " +
	            "(id SERIAL NOT NULL, " +
	            " time BIGINT NOT NULL, " +
	            " eventJsonString TEXT NOT NULL, " +
	            " PRIMARY KEY ( id ))"; 
	    sql.executeUpdate(createEvents);
	    
	    // Records + BetTicket Table
	    String createRecords = "CREATE TABLE IF NOT EXISTS records " +
	            "(id SERIAL NOT NULL, " +
	            " time BIGINT NOT NULL, " +
	            " recordJsonString TEXT NOT NULL, " +
	            " betTicketJsonString TEXT NOT NULL, " +
	            " eventId INTEGER, " + 
	            " FOREIGN KEY ( eventId ) REFERENCES events (id) ON DELETE CASCADE, " + 
	            " PRIMARY KEY ( id ))"; 
	    sql.executeUpdate(createRecords);	    
	}
	
	public int addEvent(long time, String eventJsonString){
		int id = -1;
		String sql = "INSERT INTO events(id, time, eventJsonString) VALUES (DEFAULT, " + time + ",'" + eventJsonString + "')";
		
		PreparedStatement sT = null;
		try {
			sT = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			sT.execute();
			ResultSet rS = sT.getGeneratedKeys();
			rS.next();
			id = rS.getInt(1);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}				
		return id;
	}
	
	public void addRecord(long time, String recordJsonString, String betTicketJsonString, int eventId){
		Statement sT = null;
		try {
			sT = db.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String inserString = "INSERT INTO records(id, time, recordJsonString, betTicketJsonString, eventId) VALUES(DEFAULT, " + time
				+ ",'" + recordJsonString + "','" + betTicketJsonString + "'," + eventId + ")";
		try {
			sT.execute(inserString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Throwable, SQLException {
		EastbridgeLiquidityDatabase dataBase = new EastbridgeLiquidityDatabase();
		System.out.println();
	}
}
