package bettingManager.gui;

import java.util.Observable;
import java.util.Observer;

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
	
	public MainController() {
		this.allFilters = new FilterSettingsContainer();
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
	}

	/**
	 * Controllers notifie MainController
	 */
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("NOTIFIED");
		if (o instanceof Checkbox1Controller) {
			System.out.println("Checkbox1Contr has sent something");
			
		} else if (o instanceof OptionsController) {
			System.out.println("OptionsContr has sent something");
			
		} else if (o instanceof SiteController) {
			System.out.println("SiteContr has sent something");
			
		} else if (o instanceof AverageOddsController) {
			System.out.println("AverageOddsContr has sent something");
			
		} else if (o instanceof KoBController) {
			System.out.println("KoBContr has sent something");
			
		} else if (o instanceof LiquidityController) {
			System.out.println("LiquidityContr has sent something");
			
		}
	}

	
}
