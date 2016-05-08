package bettingManager.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * 2nd item of filters
 * @author David
 *
 */
public class OptionsController {
	private MainController mainC;
	
	/**
	 * Buttons
	 */
	@FXML private Button dateRangeButton;
	@FXML private Button tipstersButton;
	@FXML private Button moreFiltersButton;
	@FXML private Button clearAllButton;
	@FXML private Button refreshButton;
	
	public void init(MainController mainC) {
		this.mainC = mainC;
	}
	
	public void handleDateRangeButton(ActionEvent event) {
		System.out.println("date range button clicked");
	}

	public void handleTipstersButton(ActionEvent event) {
		System.out.println("tipsters button clicked");
		
	}
	
	public void handleMoreFiltersButton(ActionEvent event) {
		System.out.println("more filters button clicked");
		
	}
	
	public void handleClearAllButton(ActionEvent event) {
		System.out.println("clear all button clicked");
		
	}
	
	public void handleRefreshButton(ActionEvent event) {
		System.out.println("refresh button clicked");
		
	}
	
}
