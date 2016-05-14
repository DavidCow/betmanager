package bettingManager.gui;

import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
	
	/**
	 * ChoiceBoxes population
	 */
	@FXML private ChoiceBox<String> beforeHourChoiceBox;
	@FXML private ChoiceBox<String> afterHourChoiceBox;
	@FXML private ChoiceBox<String> betweenHourChoiceBox1;
	@FXML private ChoiceBox<String> betweenHourChoiceBox2;

	@FXML private ChoiceBox<String> beforeMinuteChoiceBox;
	@FXML private ChoiceBox<String> afterMinuteChoiceBox;
	@FXML private ChoiceBox<String> betweenMinuteChoiceBox1;
	@FXML private ChoiceBox<String> betweenMinuteChoiceBox2;
	
	
	/**
	 * Initialize
	 * @param mainC
	 * @param opt
	 */
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
		
		populateChoicebox(beforeHourChoiceBox, 0, 23, 1);
		populateChoicebox(afterHourChoiceBox, 0, 23, 1);
		populateChoicebox(betweenHourChoiceBox1, 0, 23, 1);
		populateChoicebox(betweenHourChoiceBox2, 0, 23, 1);

		populateChoicebox(beforeMinuteChoiceBox, 0, 59, 10);
		populateChoicebox(afterMinuteChoiceBox, 0, 59, 10);
		populateChoicebox(betweenMinuteChoiceBox1, 0, 59, 10);
		populateChoicebox(betweenMinuteChoiceBox2, 0, 59, 10);
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
	
	/**
	 * Populate dropdown fields with hours and minutes
	 */
	private void populateChoicebox(ChoiceBox<String> choiceBox, int start, int end, int modulo) {
		ObservableList<String> list = FXCollections.observableArrayList();
		for(int i=start; i<=end; i+=1) {
			if (i%modulo == 0) {
				if (i < 10) {
					list.add("0"+i);
				} else {
					list.add(""+i);
				}
			}
		}
		choiceBox.setItems(list);
	}
	
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_DATERANGE_ID, null)); 
	}
}
