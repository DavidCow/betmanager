package bettingManager.gui;

import java.io.IOException;
import java.util.Observable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

/**
 * 2nd item of filters
 * @author David
 *
 */
public class OptionsController extends Observable{
	private MainController mainC;
	public static int OPTIONS_ID = 1;
	
	/**
	 * Buttons
	 */
	@FXML private MenuButton dateRangeButton;
	@FXML private Button tipstersButton;
	@FXML private Button moreFiltersButton;
	@FXML private Button clearAllButton;
	@FXML private Button refreshButton;
	
	
	public void init(MainController mainC) {
		this.mainC = mainC;
		this.addObserver(mainC);
	}
	
	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void handleDateRangeButton(ActionEvent event) throws IOException {
		System.out.println("date range button clicked");
//		/**
//		 * Set up Date Range button
//		 */
//		Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/bettingManager/gui/layout/Options_DateRange.fxml")));
//		scene.getStylesheets().add(getClass().getResource("/bettingManager/gui/layout/style.css").toExternalForm());
//		
//		final MenuItem dateRangeWindow = new MenuItem();
//		dateRangeWindow.setGraphic(scene.getRoot());
//		((MenuButton) event.getSource()).getItems().setAll(dateRangeWindow);
		
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
	
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_ID, null)); //TODO: NOT YET DECIDED, notifyMainController has to be called still
	}
	
}
