package bettingManager.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;

import com.google.gson.Gson;

import bettingManager.statsCalculation.StatsCalculator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;

public class MainController implements Observer{

	public static final int UPDATE_MODE_ALL = 0;
	public static final int UPDATE_MODE_NOCHECKBOX1_NO_SITES = 1;
	
	
	/**
	 * Filter controllers
	 */
	@FXML Checkbox1Controller checkbox1Controller;
	@FXML OptionsController optionsController;
	@FXML SiteController siteController;
	@FXML AverageOddsController averageOddsController;
	@FXML KoBController koBController;
	@FXML LiquidityController liquidityController;
	
	
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
	 * Filter String
	 */
	@FXML Label activeFiltersLabel;
	@FXML Tooltip allFiltersToolTip;
	
	
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
		System.out.println("Loading StatsCalculator..");
		this.statsCalc = new StatsCalculator();
		System.out.println("Loading StatsCalculator Done!");
//		prefs.put(PREFS_ALLFILTERS, "");
		String json = prefs.get(PREFS_ALLFILTERS, null);
//		json = null;
		if (json == null || json.isEmpty()) {
			/*
			 * If no previous filter settings 
			 */
			this.allFilters = new FilterSettingsContainer();
		} else {
			/*
			 * Load last filter settings
			 */
			this.allFilters = gson.fromJson(json, FilterSettingsContainer.class);
		}
		setStatsCalculator(allFilters);
	}
	
	/**
	 * Initialize MainController references
	 * and add Observer to each controller
	 */
	@FXML public void initialize() {
		System.out.println("MainController initialize");
		
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
		
		tableKindOfBetController.init(this);
		tableAverageLiquidityController.init(this);
		tableTipsterNameController.init(this);
		tableDayWeekController.init(this);
		tableMonthlyController.init(this);
		tableGraphController.init(this);
		
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
			siteController.updateSettings(allFilters);
		}
		optionsController.updateSettings(allFilters);		//Updates DateRange as well
		//siteController
		averageOddsController.updateSettings(allFilters);
		koBController.updateSettings(allFilters);
		liquidityController.updateSettings(allFilters);
	}
	
	/**
	 * Controllers notifie MainController
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
		} else if (o instanceof OptionsDateRangeController) {
			System.out.println("OptionsDateRangeContr has sent something");
			allFilters.setDateRangeMessage((DateRangeMessage)argMsg.getMsg());
		} else if (o instanceof OptionsTipstersController) {
			System.out.println("OptionsTipstersContr has sent something");
			allFilters.setTipstersMessage((Map<String, Boolean>)argMsg.getMsg());
		}
		//ADD Tipsters Controller
		System.out.println(allFilters);
		saveFilters();
		
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
		//Don't clear Data and Site (Requested by stakeholder)
		this.allFilters.setDataState(old.getDataState());
		this.allFilters.setSitesList(old.getSitesList());
		this.allFilters.setTipstersMessage(old.getTipstersMessage());
		updateSettingsControllers(MainController.UPDATE_MODE_NOCHECKBOX1_NO_SITES);
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
			statsCalc.startdate = new Date(0);
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
		} else if (c.getOddsDataAverageOdds().getBetween() != -1 || c.getOddsDataAverageOdds().getAnd() != Float.MAX_VALUE) {
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
		} else if (c.getOddsDataLiquidity().getBetween() != -1 || c.getOddsDataLiquidity().getAnd() != Float.MAX_VALUE) {
			statsCalc.minLiquidity = c.getOddsDataLiquidity().getBetween(); 
			statsCalc.maxLiquidity = c.getOddsDataLiquidity().getAnd(); 
		}
		//
		statsCalc.activeTipsters = c.getTipstersMessage();
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
	
}
