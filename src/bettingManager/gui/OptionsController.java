package bettingManager.gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
	
	public void handleDateRangeButton(ActionEvent event) throws IOException {
		System.out.println("date range button clicked");
		
		//TODO: Maybe use MenuItem instead of entire new window for better visuals
		Stage dateRangePopup = new Stage();
		Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/bettingManager/gui/layout/Options_DateRange.fxml")));
		scene.getStylesheets().add(getClass().getResource("/bettingManager/gui/layout/style.css").toExternalForm());
		dateRangePopup.setScene(scene);
		dateRangePopup.show();
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
