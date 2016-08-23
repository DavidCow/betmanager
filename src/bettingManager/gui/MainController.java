package bettingManager.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.prefs.Preferences;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import bettingManager.statsCalculation.Alias;
import bettingManager.statsCalculation.StatsCalculator;

import com.google.gson.Gson;

public class MainController implements Observer{

	public static final int UPDATE_MODE_ALL = 0;
	public static final int UPDATE_MODE_NOCHECKBOX1 = 1;
	
	
	/**
	 * Filter controllers
	 */
	@FXML Checkbox1Controller checkbox1Controller;
	@FXML OptionsController optionsController;
	@FXML SiteController siteController;
	@FXML AverageOddsController averageOddsController;
	@FXML KoBController koBController;
	@FXML LiquidityController liquidityController;
	@FXML OddsOfTheTipController oddsOfTheTipController;
	
	@FXML TabPane tabPane;
	
	/**
	 * Table controller
	 */
	@FXML TableKindOfBetController tableKindOfBetController;
	@FXML TableAverageLiquidityController tableAverageLiquidityController;
	@FXML TableTipsterNameController tableTipsterNameController;
	@FXML TableDayWeekController tableDayWeekController;
	@FXML TableMonthlyController tableMonthlyController;
	@FXML TableGraphController tableGraphController;
	
	/**
	 * Last Bets
	 */
	@FXML TableLastBetsController tableLastBetsController;
	


	/**
	 * Filter String
	 */
	@FXML Label activeFiltersLabel;
	@FXML Tooltip allFiltersToolTip;
	
	
	/**
	 * Progress Indicator
	 */
	@FXML ProgressIndicator progressIndicatorMain;
	/**
	 * All filter options
	 */
	private FilterSettingsContainer allFilters;
	public FilterSettingsContainer getAllFilters() {
		return allFilters;
	}

	private StatsCalculator statsCalc;

	public StatsCalculator getStatsCalc() {
		return statsCalc;
	}

	private Preferences prefs;
	public Preferences getPrefs() {
		return prefs;
	}

	private Gson gson; 

	public final String PREFS_ALLFILTERS = "PREFS_ALLFILTERS";
	
	public MainController() {
		this.prefs = Preferences.userNodeForPackage(bettingManager.gui.MainController.class);
		this.gson = new Gson();
		//RESET SAVED FILTERSETTINGS
//		String json = gson.toJson(new FilterSettingsContainer()); //RESET FILTER
//		prefs.put(PREFS_ALLFILTERS, json);							//RESET FILTER
		
		System.out.println("Loading StatsCalculator..");
		this.statsCalc = new StatsCalculator();
		System.out.println("Loading StatsCalculator Done!");
		String json = prefs.get(PREFS_ALLFILTERS, null);		//COMMENT OUT WHEN RESET FILTER
//		json = null;											//RESET FILTER
		if (json == null || json.isEmpty()) {
			/*
			 * If no previous filter settings 
			 */
			this.allFilters = new FilterSettingsContainer();
			Map<String, Boolean> tipstersSaved = new HashMap<String, Boolean>();
			for (String t:statsCalc.getAllTipsters()) {
				tipstersSaved.put(t, true);
			}
			this.allFilters.setTipstersMessage(tipstersSaved);
		} else {
			/*
			 * Load last filter settings
			 */
			this.allFilters = gson.fromJson(json, FilterSettingsContainer.class);
			
			// PATRYK: Added code to load new tipster names, even if the tipster map already exists
			Map<String, Boolean> tipstersSaved = this.allFilters.getTipstersMessage();
			Map<String, Boolean> newTipstersSaved = new HashMap<String, Boolean>();
			Set<String> tipstersInStatsCalculator = statsCalc.getAllTipsters();
			for (String t:tipstersInStatsCalculator) {
				if(!tipstersSaved.containsKey(t)){
					tipstersSaved.put(t, true);
				}
			}
			// PATRYK: Remove tipsters, which are no longer used
			for(String t:tipstersSaved.keySet()){
				if(tipstersInStatsCalculator.contains(t)){
					newTipstersSaved.put(t, tipstersSaved.get(t));
				}
			}
			this.allFilters.setTipstersMessage(newTipstersSaved);
			
			//Reset Date Range to minimum and maximum dates
			this.allFilters.getDateRangeMessage().setState(DateRangeMessage.ALL);
			this.allFilters.getDateRangeMessage().setD1(new Date(Long.MIN_VALUE));
			this.allFilters.getDateRangeMessage().setD2(new Date(Long.MAX_VALUE));
		}
		setStatsCalculator(allFilters);
	}
	
	/**
	 * Initialize MainController references
	 * and add Observer to each controller
	 * @throws IOException 
	 */
	@FXML public void initialize() throws IOException {
		System.out.println("MainController initialize");
		
		tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
		    @Override
		    public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
		    	switch (newValue.intValue()) {
			    	case TableTitles.TAB_KOB:
//			    		tableLastBetsController.setDataList(tableKindOfBetController.getData());
			    		break;
			    	case TableTitles.TAB_AVG_LIQ:
//			    		tableLastBetsController.setDataList(tableAverageLiquidityController.getData());
			    		break;
			    	case TableTitles.TAB_TIPSTER:
//			    		tableLastBetsController.setDataList(tableTipsterNameController.getData());
			    		break;
			    	case TableTitles.TAB_DAYWEEK:
//			    		tableLastBetsController.setDataList(tableDayWeekController.getData());
			    		break;
			    	case TableTitles.TAB_MONTHLY:
//			    		tableLastBetsController.setDataList(tableMonthlyController.getData());
			    		break;
			    	case TableTitles.TAB_GRAPH:
			    		break;
			    	default:
			    		break;
		    	}
		    	tableLastBetsController.table.getColumns().clear();
		    	tableLastBetsController.labelLastBets.setText("0 Last Bet(s)");
		    }
		});
		
		/*
		 * Pass instance of MainController to filter controllers
		 * AND add MainController as Observer for each controller
		 * in init(..)
		 */
		checkbox1Controller.init(this);
		optionsController.init(this);
		siteController.init(this);
		averageOddsController.init(this);
		koBController.init(this);
		liquidityController.init(this);
		oddsOfTheTipController.init(this);
//		
//		Platform.runLater(new Runnable() {
//			
//			@Override
//			public void run() {
				tableKindOfBetController.init(MainController.this);
				tableAverageLiquidityController.init(MainController.this);
				tableTipsterNameController.init(MainController.this);
				tableDayWeekController.init(MainController.this);
				tableMonthlyController.init(MainController.this);
				
				tableLastBetsController.init(MainController.this);
				System.out.println("DONE loading tables.");
//			}
//		});
		tableGraphController.init(MainController.this);
		updateSettingsControllers(MainController.UPDATE_MODE_ALL);
		updateFilterLabel();
	}

	public void updateSettingsControllers(int updateMode) {
		if (allFilters == null) {
			System.out.println("AllFilters is null");
			return;
		}
		/*
		 * Update the views according to last FilterSettingsContainer
		 */
		if (updateMode == MainController.UPDATE_MODE_ALL) {
			checkbox1Controller.updateSettings(allFilters);
		}
		siteController.updateSettings(allFilters);
		optionsController.updateSettings(allFilters);		//Updates DateRange as well
		//siteController
		averageOddsController.updateSettings(allFilters);
		koBController.updateSettings(allFilters);
		liquidityController.updateSettings(allFilters);
		oddsOfTheTipController.updateSettings(allFilters);
	}
	
	/**
	 * Controllers notify MainController
	 */
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("NOTIFIED");
		ObservableMessage argMsg = (ObservableMessage) arg;
		if (o instanceof Checkbox1Controller) {
			System.out.println("Checkbox1Contr has sent something");
			allFilters.setDataState((int)argMsg.getMsg());
		} else if (o instanceof OptionsController) {
			System.out.println("OptionsContr has sent something");
		} else if (o instanceof SiteController) {
			System.out.println("SiteContr has sent something");
			allFilters.setSitesList((ArrayList<String>)argMsg.getMsg());
		} else if (o instanceof AverageOddsController) {
			System.out.println("AverageOddsContr has sent something");
			allFilters.setOddsDataAverageOdds((OddsData)argMsg.getMsg());
		} else if (o instanceof KoBController) {
			System.out.println("KoBContr has sent something");
			allFilters.setKoBList((ArrayList<String>)argMsg.getMsg());
		} else if (o instanceof LiquidityController) {
			System.out.println("LiquidityContr has sent something");
			allFilters.setOddsDataLiquidity((OddsData)argMsg.getMsg());
		}  else if (o instanceof OddsOfTheTipController) {
			System.out.println("OddsOfTheTipContr has sent something");
			allFilters.setOddsDataOddsOfTheTip((OddsData)argMsg.getMsg());
		} else if (o instanceof OptionsDateRangeController) {
			System.out.println("OptionsDateRangeContr has sent something");
			allFilters.setDateRangeMessage((DateRangeMessage)argMsg.getMsg());
		} else if (o instanceof OptionsTipstersController) {
			System.out.println("OptionsTipstersContr has sent something");
			allFilters.setTipstersMessage((Map<String, Boolean>)argMsg.getMsg());
		} else if (o instanceof OptionsAddAliasesController) {
			System.out.println("OptionsAddAliasesContr has sent something");
			allFilters.setAliases((ArrayList<Alias>)argMsg.getMsg());
		} else if (o instanceof OptionsAddAliasesSelectTipstersController) {
			System.out.println("OptionsAddAliasesSelectTisptersContr has sent something");
			allFilters.setAliases((ArrayList<Alias>)argMsg.getMsg());
		}
		//ADD Tipsters Controller
		System.out.println(allFilters);
		saveFilters();
//			for(Alias a:allFilters.getAliases()) {
//				System.out.println(a.getAliasName());
//				for(String s:a) {
//					System.out.println(s);
//				}
//				System.out.println("AFTER---");
//			}
		
		updateFilterLabel();
	}
	
	private void updateFilterLabel() {
		if (allFilters == null) {
			System.out.println("AllFilters is null at updateFilterLabel()");
			return;
		}
		//Update Filter Label
		String actFilterText = allFilters.getActiveFiltersString();
		activeFiltersLabel.setText(actFilterText);
		allFiltersToolTip.setText(actFilterText);
	}
	
	/**
	 * Save filters in preferences as Json string
	 */
	public void saveFilters() {
		/**
		 * Save in preferences
		 */
		String json = gson.toJson(allFilters);
		prefs.put(PREFS_ALLFILTERS, json);
	}
	
	public void clearFilters() {
		FilterSettingsContainer old = this.allFilters;
		this.allFilters = new FilterSettingsContainer();
		//Keep Historic/Real preference
		this.allFilters.setDataState(old.getDataState());
		
		//enable BB, BA
		ArrayList<String> sitesList = new ArrayList<String>();
		sitesList.add("BetAdvisor");
		sitesList.add("Blogabet");
		this.allFilters.setSitesList(sitesList);
		
		//enable kob's
		ArrayList<String> koBList = new ArrayList<String>();
		koBList.add("Asian Handicap");
		koBList.add("Over - Under");
		koBList.add("X Result");
		koBList.add("1 2 Result");
		this.allFilters.setKoBList(koBList);
		
		
		//Keep aliases
		this.allFilters.setAliases(old.getAliases());
		
		//Reset Date Range to minimum and maximum dates
		this.allFilters.getDateRangeMessage().setState(DateRangeMessage.ALL);
		this.allFilters.getDateRangeMessage().setD1(new Date(Long.MIN_VALUE));
		this.allFilters.getDateRangeMessage().setD2(new Date(Long.MAX_VALUE));
		
		//Keep Tipster selections
		this.allFilters.setTipstersMessage(old.getTipstersMessage());
		
		updateSettingsControllers(MainController.UPDATE_MODE_NOCHECKBOX1);
		saveFilters();
		updateFilterLabel();
	}
	
	public void setStatsCalculator(FilterSettingsContainer c) {
		if (c.getDataState() == Checkbox1Controller.CHECKBOX_GROUP1_HISTORIC  || c.getDataState() == Checkbox1Controller.CHECKBOX_GROUP1_BOTH) {
			statsCalc.historical = true;
		} else {
			statsCalc.historical = false;
		}
		if (c.getDataState() == Checkbox1Controller.CHECKBOX_GROUP1_REAL || c.getDataState() == Checkbox1Controller.CHECKBOX_GROUP1_BOTH) {
			statsCalc.real = true;
		} else {
			statsCalc.real = false;
		}
		//
		if (c.getDateRangeMessage().getD1() != null) {
			statsCalc.startdate = c.getDateRangeMessage().getD1();
		} else {
			statsCalc.startdate = new Date(Long.MIN_VALUE);
		}
		if(c.getDateRangeMessage().getD2() != null) {
			statsCalc.endDate = c.getDateRangeMessage().getD2();
		} else {
			statsCalc.endDate = new Date(Long.MAX_VALUE);
		}
		//
		if (c.getSitesList().contains("Blogabet")) {
			statsCalc.blogaBet = true;
		} else {
			statsCalc.blogaBet = false;
		}
		if (c.getSitesList().contains("BetAdvisor")) {
			statsCalc.betAdvisor = true;
		} else {
			statsCalc.betAdvisor = false;
		}
		//
		if (c.getOddsDataAverageOdds().getGreaterThan() != -1 || c.getOddsDataAverageOdds().getLessThan() != Float.MAX_VALUE) {
			statsCalc.minOdds = c.getOddsDataAverageOdds().getGreaterThan();
			statsCalc.maxOdds = c.getOddsDataAverageOdds().getLessThan();
		} else {
			statsCalc.minOdds = c.getOddsDataAverageOdds().getBetween(); 
			statsCalc.maxOdds = c.getOddsDataAverageOdds().getAnd(); 
		}
		//
		if (c.getKoBList().contains("Asian Handicap")) {
			statsCalc.asianHandicap = true;
		} else {
			statsCalc.asianHandicap = false;
		}
		if (c.getKoBList().contains("Over - Under")) {
			statsCalc.overUnder = true;
		} else {
			statsCalc.overUnder = false;
		}
		if (c.getKoBList().contains("1 2 Result")) {
			statsCalc.oneTwoResult = true;
		} else {
			statsCalc.oneTwoResult = false;
		}
		if (c.getKoBList().contains("X Result")) {
			statsCalc.xResult = true;
		} else {
			statsCalc.xResult = false;
		}
		//
		if (c.getOddsDataLiquidity().getGreaterThan() != -1 || c.getOddsDataLiquidity().getLessThan() != Float.MAX_VALUE) {
			statsCalc.minLiquidity = c.getOddsDataLiquidity().getGreaterThan();
			statsCalc.maxLiquidity = c.getOddsDataLiquidity().getLessThan();
		} else {
			statsCalc.minLiquidity = c.getOddsDataLiquidity().getBetween(); 
			statsCalc.maxLiquidity = c.getOddsDataLiquidity().getAnd(); 
		}
		//
		//
		if (c.getOddsDataOddsOfTheTip().getGreaterThan() != -1 || c.getOddsDataOddsOfTheTip().getLessThan() != Float.MAX_VALUE) {
			statsCalc.minOddsOfTheTip = c.getOddsDataOddsOfTheTip().getGreaterThan();
			statsCalc.maxOddsOfTheTip = c.getOddsDataOddsOfTheTip().getLessThan();
		} else {
			statsCalc.minOddsOfTheTip = c.getOddsDataOddsOfTheTip().getBetween(); 
			statsCalc.maxOddsOfTheTip = c.getOddsDataOddsOfTheTip().getAnd(); 
		}
		//
		statsCalc.activeTipsters = c.getTipstersMessage();
		statsCalc.aliasList = c.getAliases();
	}
	
	public int getSelectedTab() {
		return tabPane.getSelectionModel().getSelectedIndex();
	}
	
	public TableGraphController getTableGraphController() {
		return tableGraphController;
	}

	public TableMonthlyController getTableMonthlyController() {
		return tableMonthlyController;
	}

	public TableDayWeekController getTableDayWeekController() {
		return tableDayWeekController;
	}

	public TableTipsterNameController getTableTipsterNameController() {
		return tableTipsterNameController;
	}

	public TableAverageLiquidityController getTableAverageLiquidityController() {
		return tableAverageLiquidityController;
	}

	public TableKindOfBetController getTableKindOfBetController() {
		return tableKindOfBetController;
	}

	public TableLastBetsController getTableLastBetsController() {
		return tableLastBetsController;
	}
	
}
