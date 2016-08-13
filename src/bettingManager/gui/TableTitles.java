package bettingManager.gui;

public class TableTitles {
	
	public final static int TAB_KOB = 0;
	public final static int TAB_AVG_LIQ = 1;
	public final static int TAB_TIPSTER = 2;
	public final static int TAB_DAYWEEK = 3;
	public final static int TAB_MONTHLY = 4;
	public final static int TAB_GRAPH = 5;
	
	/**
	 * Column name definitions
	 */
	public final static String KINDOFBET = "Kind of Bet";
	public final static String AVERAGEYIELD = "Average yield";
	public final static String AVERAGEODDS = "Average odds";
	public final static String NUMBEROFBETS = "Number of Bets";
	public final static String PERCENTWEGET = "% we get";
	public final static String PERCENTOVER95 = "% over 95%";
	public final static String AVERAGELIQUIDITY = "Average Liquidity";
	public final static String PERCENTOFTIPSFOUND = "% of tips found";
	public final static String FLATSTAKEYIELD = "Flat stake yield";
	public final static String TIPSTERNAME = "Tipster";
	public final static String DAY = "Day";
	public final static String MONTH = "Month";
	public final static String LIQUIDITY = "Liquidity";
	
	
	/**
	 * Variable names for Table injection
	 */
	public final static String[] kindOfBetTableValueNames = {"groupBy","averageYield","averageOdds","numberOfBets","percentWeGet","percentOver95",
			"averageLiquidity","percentOfTipsFound","flatStakeYield"};
	public final static String[] averageLiquidityTableValueNames = {"groupBy", "averageLiquidity", 
			"averageYield","averageOdds","numberOfBets","percentWeGet",
			"percentOver95","percentOfTipsFound","flatStakeYield"};
	public final static String[] tipsterNameTableValueNames = {"groupBy", 
			"averageLiquidity","averageYield","averageOdds","numberOfBets",
			"percentWeGet", "percentOver95","percentOfTipsFound","flatStakeYield"};
	public final static String[] dayWeekTableValueNames = {"groupBy", 
			"averageLiquidity","averageYield","averageOdds","numberOfBets",
			"percentWeGet", "percentOver95","percentOfTipsFound","flatStakeYield"};
	
	public final static String[] monthlyTableValueNames = {"groupBy", 
			"averageLiquidity","averageYield","averageOdds","numberOfBets",
			"percentWeGet", "percentOver95","percentOfTipsFound","flatStakeYield"};
	
	
	/**
	 * Title of column (string)
	 */
	public final static String[] TABLE_TITLES_KINDOFBET = {KINDOFBET, AVERAGEYIELD, AVERAGEODDS, NUMBEROFBETS, PERCENTWEGET, PERCENTOVER95, AVERAGELIQUIDITY, PERCENTOFTIPSFOUND, FLATSTAKEYIELD};
	public final static String[] TABLE_TITLES_AVERAGELIQUIDITY = {
			LIQUIDITY,
			AVERAGELIQUIDITY,
			AVERAGEYIELD, 
			AVERAGEODDS, 
			NUMBEROFBETS, 
			PERCENTWEGET, 
			PERCENTOVER95, 
			PERCENTOFTIPSFOUND, FLATSTAKEYIELD
			};
	public final static String[] TABLE_TITLES_TIPSTERNAME = {
			TIPSTERNAME,
			AVERAGELIQUIDITY,
			AVERAGEYIELD, 
			AVERAGEODDS, 
			NUMBEROFBETS, 
			PERCENTWEGET, 
			PERCENTOVER95, 
			PERCENTOFTIPSFOUND, FLATSTAKEYIELD
	};
	public final static String[] TABLE_TITLES_DAYWEEK = {
			DAY,
			AVERAGELIQUIDITY,
			AVERAGEYIELD, 
			AVERAGEODDS, 
			NUMBEROFBETS, 
			PERCENTWEGET, 
			PERCENTOVER95, 
			PERCENTOFTIPSFOUND, FLATSTAKEYIELD
	};
	public final static String[] TABLE_TITLES_MONTHLY = {
			MONTH,
			AVERAGELIQUIDITY,
			AVERAGEYIELD, 
			AVERAGEODDS, 
			NUMBEROFBETS, 
			PERCENTWEGET, 
			PERCENTOVER95, 
			PERCENTOFTIPSFOUND, FLATSTAKEYIELD
	};
}
