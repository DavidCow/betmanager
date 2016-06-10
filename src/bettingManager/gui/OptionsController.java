package bettingManager.gui;

import java.io.IOException;
import java.util.Observable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;

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
	@FXML private MenuButton tipstersButton;
	@FXML private Button moreFiltersButton;
	@FXML private Button clearAllButton;
	@FXML private Button refreshButton;
	
	@FXML private CustomMenuItem customMenuItemDateRange;  //DateRange
	@FXML private CustomMenuItem customMenuItemDateRange2; //Tipsters
	
	@FXML OptionsDateRangeController optionsDateRangeController;
	@FXML OptionsTipstersController optionsTipstersController;
	
	
	public void init(MainController mainC) {
		this.mainC = mainC;
		this.addObserver(mainC);
		
		/*
		 * Subcontroller
		 */
		optionsDateRangeController.init(mainC, this);
		optionsTipstersController.init(mainC, this);
		
		customMenuItemDateRange.setHideOnClick(false);
		customMenuItemDateRange2.setHideOnClick(false);
	}
	
	
	public OptionsDateRangeController getOptionsDateRangeController() {
		return optionsDateRangeController;
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
		mainC.clearFilters();
		
	}
	
	public void handleRefreshButton(ActionEvent event) {
		System.out.println("refresh button clicked");
		mainC.setStatsCalculator(mainC.getAllFilters());
		
		int selectedTab = mainC.getSelectedTab();
		if (selectedTab == 0) {
			mainC.getTableKindOfBetController().init(mainC);
		} else if (selectedTab == 1) {
			mainC.getTableAverageLiquidityController().init(mainC);
		} else if (selectedTab == 2) {
			mainC.getTableTipsterNameController().init(mainC);
		} else if (selectedTab == 3) {
			mainC.getTableDayWeekController().init(mainC);
		} else if (selectedTab == 4) {
			mainC.getTableMonthlyController().init(mainC);
		} else if (selectedTab == 5) {
			mainC.getTableGraphController().init(mainC);
		}
	}

	public void hideWindow() {
//		customMenuItemDateRange.setHideOnClick(true);
	}

	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_ID, null)); //TODO: NOT YET DECIDED, notifyMainController has to be called still
	}


	public void updateSettings(FilterSettingsContainer allFilters) {
		optionsDateRangeController.updateSettings(allFilters);
		optionsTipstersController.updateSettings(allFilters);
	}
	
}
