package bettingManager.gui;

import java.io.IOException;
import java.util.Observable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 2nd item of filters
 * @author David
 *
 */
public class OptionsController extends Observable{
	private MainController mainC;
	public static int OPTIONS_ID = 1;
	
	public static String DATE_RANGE_TITLE = "Select Date Range";
	public static String DATE_RANGE_RESOURCE = "/bettingManager/gui/layout/Options_DateRange.fxml";
	public static String TIPSTERS_TITLE = "Select Tipsters";
	public static String TIPSTERS_RESOURCE = "/bettingManager/gui/layout/Options_Tipsters.fxml";
	public static String STYLESHEET = "/bettingManager/gui/layout/style.css";
	
	/**
	 * Buttons
	 */
	@FXML private Button dateRangeButton;
	@FXML private Button tipstersButton;
	@FXML private Button moreFiltersButton;
	@FXML private Button clearAllButton;
	@FXML private Button refreshButton;
	
	
	OptionsDateRangeController optionsDateRangeController;
	OptionsTipstersController optionsTipstersController;
	
	private Stage stageDateRange;
	private Stage stageTipsters;
	
	public void init(MainController mainC) throws IOException {
		this.mainC = mainC;
		this.addObserver(mainC);
		createDateRangeStage();
		createTipstersStage();
		/*
		 * Subcontroller
		 */
		optionsDateRangeController.init(mainC, this);
		optionsTipstersController.init(mainC, this);
	}
	
	
	/**
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void handleDateRangeButton(ActionEvent event) {
		System.out.println("date range button clicked");
		/**
		 * Set up Date Range button
		 */
//		Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/bettingManager/gui/layout/Options_DateRange.fxml")));
//		scene.getStylesheets().add(getClass().getResource("/bettingManager/gui/layout/style.css").toExternalForm());
		stageDateRange.showAndWait();
	}
	
	private void createDateRangeStage() throws IOException {
		stageDateRange = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(DATE_RANGE_RESOURCE));
		Parent root = loader.load();
		optionsDateRangeController = (OptionsDateRangeController) loader.getController();
		root.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
		stageDateRange.setScene(new Scene(root));
		stageDateRange.setTitle(DATE_RANGE_TITLE);
		stageDateRange.initModality(Modality.APPLICATION_MODAL);
	}

	private void createTipstersStage() throws IOException {
		stageTipsters = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(TIPSTERS_RESOURCE));
		Parent root = loader.load();
		optionsTipstersController = (OptionsTipstersController) loader.getController();
		root.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
		stageTipsters.setScene(new Scene(root));
		stageTipsters.setTitle(TIPSTERS_TITLE);
		stageTipsters.initModality(Modality.APPLICATION_MODAL);
	}

	public void hideDateRangeWindow() {
		stageDateRange.hide();
	}
	public void hideTipstersWindow() {
		stageTipsters.hide();
	}
	
	public void handleTipstersButton(ActionEvent event) {
		System.out.println("tipsters button clicked");
		stageTipsters.showAndWait();
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

	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_ID, null)); //TODO: NOT YET DECIDED, notifyMainController has to be called still
	}


	public void updateSettings(FilterSettingsContainer allFilters) {
		optionsDateRangeController.updateSettings(allFilters);
		optionsTipstersController.updateSettings(allFilters);
	}
	
	public OptionsDateRangeController getOptionsDateRangeController() {
		return optionsDateRangeController;
	}
	
}
