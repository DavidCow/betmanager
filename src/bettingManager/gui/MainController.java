package bettingManager.gui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;

import com.google.gson.Gson;

import javafx.fxml.FXML;

public class MainController implements Observer{

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
		
		
		
		updateSettingsControllers();
	}

	public void updateSettingsControllers() {
		/*
		 * Update the views according to last FilterSettingsContainer
		 */
		checkbox1Controller.updateSettings(allFilters);
		//optionsController.updateSettings(allFilters);
		siteController.updateSettings(allFilters);
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
		}
		System.out.println(allFilters);
		
		/**
		 * Save in preferences
		 */
		String json = gson.toJson(allFilters);
		prefs.put(PREFS_ALLFILTERS, json);
	}
	
	public void clearFilters() {
		this.allFilters = new FilterSettingsContainer();
		updateSettingsControllers();
	}
	
	
}
