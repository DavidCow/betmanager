package bettingManager.gui;

import java.util.Calendar;
import java.util.Date;
import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class OptionsDateRangeController extends Observable{

	private MainController mainC;
	public static int OPTIONS_DATERANGE_ID = 7;
	
	private OptionsController optionsController;
	
	String[] monthNames = {"January", 
			"February", 
			"March", 
			"April", 
			"May", 
			"June", 
			"July", 
			"August",
			"September", 
			"October", "November", "December"};
	
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
	 * Month month, Month year population
	 */
	@FXML private ComboBox<String> monthMonth;
	@FXML private ComboBox<String> monthYear;
	
	
	private DateRangeMessage msg;
	
	/**
	 * Initialize
	 * @param mainC
	 * @param opt
	 */
	public void init(MainController mainC, OptionsController opt) {
		this.mainC = mainC;
		this.addObserver(mainC);
		this.optionsController = opt;
		
		this.msg = new DateRangeMessage();
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
		
		populateMonthYear();
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
		/**
		 * Check which RadioButton is selected 
		 */
		HBox hbox = null;
		for(int i=0; i<hboxes.length; i+=1) {
			if (!hboxes[i].isDisabled()) {
				hbox = hboxes[i];
			}
		}
		
		if (hbox == null) {
			System.out.println("Nothing has been selected, return.");
			return;
		}

		if (hbox.equals(hboxMonth)) {
			System.out.println("HboxMonth");
			msg.setState(DateRangeMessage.MONTH);
			
			Calendar now = Calendar.getInstance();
			Date date = new Date();
			now.setTime(date);
			now.set(Calendar.MONTH, theNumberOfMonth(monthMonth.getValue()));
			now.set(Calendar.YEAR, Integer.parseInt(monthYear.getValue()));
			date = now.getTime();
			System.out.println(date);
			msg.setD1(date);
		} else if (hbox.equals(hboxDay)) {
			System.out.println("HboxDay");
		} else if (hbox.equals(hboxBefore)) {
			System.out.println("HboxBefore");
		} else if (hbox.equals(hboxAfter)) {
			System.out.println("HboxAfter");
		} else if (hbox.equals(hboxBetween)) {
			System.out.println("HboxBetween");
		} else if (hbox.equals(hboxLast)) {
			System.out.println("HboxLast");
		}
		notifyMainController();
	}
	
	public String theMonth(int month){
	    return monthNames[month];
	}
	
	public int theNumberOfMonth(String month) {
		for(int i=0; i<monthNames.length; i+=1) {
			if (monthNames[i].contentEquals(month)) {
				return i;
			}
		}
		return -1;
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
	
	private void populateMonthYear() {
		ObservableList<String> list = FXCollections.observableArrayList(monthNames);
		monthMonth.setItems(list);
		
		int start = 2000;
		int end = 2016;
		list = FXCollections.observableArrayList();
		for(int i=start; i<=end; i+=1) {
			list.add(""+i);
		}
		monthYear.setItems(list);
	}
	
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_DATERANGE_ID, null)); 
	}
}
