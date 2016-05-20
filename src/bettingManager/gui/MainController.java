package bettingManager.gui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
	
	/**
	 * Table controller
	 */
	@FXML TableKindOfBetController tableKindOfBetController;
	
	/**
	 * Filter String
	 */
	@FXML Label activeFiltersLabel;
	
	
	/**
	 * All filter options
	 */
	private FilterSettingsContainer allFilters;
	private Preferences prefs;
	private Gson gson; 
	public final String PREFS_ALLFILTERS = "PREFS_ALLFILTERS";
	
	public MainController() {
		this.prefs = Preferences.userNodeForPackage(bettingManager.gui.MainController.class);
		this.gson = new Gson();
		String json = prefs.get(PREFS_ALLFILTERS, null);
		
		if (json == null) {
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
		
		updateSettingsControllers(MainController.UPDATE_MODE_ALL);
		updateFilterLabel();
	}

	public void updateSettingsControllers(int updateMode) {
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
		}
		//ADD Tipsters Controller
		System.out.println(allFilters);
		saveFilters();
		
		updateFilterLabel();
	}
	
	private void updateFilterLabel() {
		//Update Filter Label
		activeFiltersLabel.setText(allFilters.getActiveFiltersString());
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
		updateSettingsControllers(MainController.UPDATE_MODE_NOCHECKBOX1_NO_SITES);
		saveFilters();
		updateFilterLabel();
	}
	
	
}
