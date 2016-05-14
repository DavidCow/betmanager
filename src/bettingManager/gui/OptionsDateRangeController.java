package bettingManager.gui;

import java.util.Observable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class OptionsDateRangeController extends Observable{

	private MainController mainC;
	public static int OPTIONS_DATERANGE_ID = 7;
	
	private OptionsController optionsController;
	
	/**
	 * Radio buttons
	 */
	final ToggleGroup toggleGroup = new ToggleGroup();
	@FXML private RadioButton dateRangeMonth;
	@FXML private RadioButton dateRangeDay;
	@FXML private RadioButton dateRangeBefore;
	@FXML private RadioButton dateRangeAfter;
	@FXML private RadioButton dateRangeBetween;
	@FXML private RadioButton dateRangeLast;
	
	/**
	 * Hboxes
	 */
	@FXML private HBox hboxMonth;
	@FXML private HBox hboxDay;
	@FXML private HBox hboxBefore;
	@FXML private HBox hboxAfter;
	@FXML private HBox hboxBetween;
	@FXML private HBox hboxLast;
	
	private HBox[] hboxes;
	
	/**
	 * Date Range items
	 */

	@FXML private Button dateRangeOkButton; 
	@FXML private Button dateRangeCancelButton;
	
	
	public void init(MainController mainC, OptionsController opt) {
		this.mainC = mainC;
		this.addObserver(mainC);
		this.optionsController = opt;
		
	}
	
	@FXML public void initialize() {
		dateRangeMonth.setToggleGroup(toggleGroup);
		dateRangeDay.setToggleGroup(toggleGroup);
		dateRangeBefore.setToggleGroup(toggleGroup);
		dateRangeAfter.setToggleGroup(toggleGroup);
		dateRangeBetween.setToggleGroup(toggleGroup);
		dateRangeLast.setToggleGroup(toggleGroup);
		
		hboxes = new HBox[6];
		hboxes[0] = hboxMonth;
		hboxes[1] = hboxDay;
		hboxes[2] = hboxBefore; 
		hboxes[3] = hboxAfter; 
		hboxes[4] = hboxBetween; 
		hboxes[5] = hboxLast;
	}
	
	public void handleMonth(ActionEvent event) {
		System.out.println("Handle month");
		disableNonSelectedRows(hboxMonth);
	}
	public void handleDay(ActionEvent event) {
		System.out.println("Handle day");
		disableNonSelectedRows(hboxDay);
	}
	public void handleBefore(ActionEvent event) {
		System.out.println("Handle before");
		disableNonSelectedRows(hboxBefore);
	}
	public void handleAfter(ActionEvent event) {
		System.out.println("Handle after");
		disableNonSelectedRows(hboxAfter);
	}
	public void handleBetween(ActionEvent event) {
		System.out.println("Handle between");
		disableNonSelectedRows(hboxBetween);
	}
	public void handleLast(ActionEvent event) {
		System.out.println("Handle last");
		disableNonSelectedRows(hboxLast);
	}
	
	public void disableNonSelectedRows(HBox visibleBox) {
		for(int i=0; i<hboxes.length; i+=1) {
			if (visibleBox.equals(hboxes[i])) {
				hboxes[i].setDisable(false);
			} else {
				hboxes[i].setDisable(true);
			}
		}
	}
	
	@FXML
	public void handleDateRangeCancelButton(ActionEvent event){
		System.out.println("Date Range Cancel");
	}

	@FXML
	public void handleDateRangeOkButton(ActionEvent event){
		System.out.println("Date Range OK");
//		optionsController.hideWindow();
	}
	
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_DATERANGE_ID, null)); 
	}
}
