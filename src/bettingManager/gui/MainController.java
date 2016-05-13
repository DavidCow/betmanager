package bettingManager.gui;

import javafx.fxml.FXML;

public class MainController {

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
	 * Initialize
	 */
	@FXML public void initialize() {
		System.out.println("MainController initialize");
		
		/**
		 * Pass instance of MainController to filter controllers
		 */
		checkbox1Controller.init(this);
		optionsController.init(this);
		siteController.init(this);
		averageOddsController.init(this);
		koBController.init(this);
		liquidityController.init(this);
	}
	
}
