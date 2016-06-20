package bettingManager.gui;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class OptionsDateRangeController extends Observable{

	private MainController mainC;
	public static int OPTIONS_DATERANGE_ID = 7;
	
	private OptionsController optionsController;
	
	public static String[] monthNames = {"January", 
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
	
	private RadioButton[] radioButtons;
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
	 * Month
	 */
	@FXML private ComboBox<String> monthMonth;
	@FXML private ComboBox<String> monthYear;
	
	/**
	 * Day, Before, After, Between
	 */
	@FXML private DatePicker datePickerDay; 
	@FXML private DatePicker datePickerBefore; 
	@FXML private DatePicker datePickerAfter; 
	@FXML private DatePicker datePickerBetween1; 
	@FXML private DatePicker datePickerBetween2; 
	
	/**
	 * Last "Tips"
	 */
	@FXML private TextField lastTextField;
	
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
		
		radioButtons = new RadioButton[6];
		radioButtons[0] = dateRangeMonth;
		radioButtons[1] = dateRangeDay;
		radioButtons[2] = dateRangeBefore;
		radioButtons[3] = dateRangeAfter;
		radioButtons[4] = dateRangeBetween;
		radioButtons[5] = dateRangeLast;
		
		hboxes = new HBox[6];
		hboxes[0] = hboxMonth;
		hboxes[1] = hboxDay;
		hboxes[2] = hboxBefore; 
		hboxes[3] = hboxAfter; 
		hboxes[4] = hboxBetween; 
		hboxes[5] = hboxLast;
		
		String standardHour = "15";
		populateChoicebox(beforeHourChoiceBox, 0, 23, 1);
		beforeHourChoiceBox.setValue(standardHour);
		populateChoicebox(afterHourChoiceBox, 0, 23, 1);
		afterHourChoiceBox.setValue(standardHour);
		populateChoicebox(betweenHourChoiceBox1, 0, 23, 1);
		betweenHourChoiceBox1.setValue(standardHour);
		populateChoicebox(betweenHourChoiceBox2, 0, 23, 1);
		betweenHourChoiceBox2.setValue(standardHour);

		populateChoicebox(beforeMinuteChoiceBox, 0, 59, 10);
		beforeMinuteChoiceBox.setValue("00");
		populateChoicebox(afterMinuteChoiceBox, 0, 59, 10);
		afterMinuteChoiceBox.setValue("00");
		populateChoicebox(betweenMinuteChoiceBox1, 0, 59, 10);
		betweenMinuteChoiceBox1.setValue("00");
		populateChoicebox(betweenMinuteChoiceBox2, 0, 59, 10);
		betweenMinuteChoiceBox2.setValue("00");
		
		
		/**
		 * Populate month, year ComboBox
		 * Set month, year to current date
		 */
		populateMonthYear();
		Date date = new Date();
		Calendar now = Calendar.getInstance();
		monthMonth.setValue(monthNames[now.get(Calendar.MONTH)]);
		monthYear.setValue(now.get(Calendar.YEAR)+"");
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
		optionsController.hideDateRangeWindow();
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
			
			Date date = get2CalendarValuesDate(Calendar.MONTH, Calendar.YEAR, theNumberOfMonth(monthMonth.getValue()), Integer.parseInt(monthYear.getValue()));
			System.out.println(date);
			msg.setD1(date);
		} else if (hbox.equals(hboxDay)) {
			System.out.println("HboxDay");
			msg.setState(DateRangeMessage.DAY);
			
			//Get date from DatePicker
			Date date = convertLocalDateToDate(datePickerDay.getValue());

			msg.setD1(date);
			System.out.println(date);
		} else if (hbox.equals(hboxBefore)) {
			System.out.println("HboxBefore");
			msg.setState(DateRangeMessage.BEFORE);
			
			//Get date from DatePicker
			Date date = convertLocalDateToDate(datePickerBefore.getValue());

			//Set hour and minute
			date = get2CalendarValuesDate(Calendar.HOUR, Calendar.MINUTE, Integer.parseInt(beforeHourChoiceBox.getValue()), Integer.parseInt(beforeMinuteChoiceBox.getValue()), date);
			
			System.out.println(date);
			msg.setD1(date);
		} else if (hbox.equals(hboxAfter)) {
			System.out.println("HboxAfter");
			msg.setState(DateRangeMessage.AFTER);
			
			//Get date from DatePicker
			Date date = convertLocalDateToDate(datePickerAfter.getValue());
			
			//Set hour and minute
			date = get2CalendarValuesDate(Calendar.HOUR, Calendar.MINUTE, Integer.parseInt(afterHourChoiceBox.getValue()), Integer.parseInt(afterMinuteChoiceBox.getValue()), date);
			
			System.out.println(date);
			msg.setD1(date);
		} else if (hbox.equals(hboxBetween)) {
			System.out.println("HboxBetween");
			msg.setState(DateRangeMessage.BETWEEN);

			//Get date from DatePicker Between
			Date date = convertLocalDateToDate(datePickerBetween1.getValue());
			
			//Set hour and minute
			date = get2CalendarValuesDate(Calendar.HOUR, Calendar.MINUTE, Integer.parseInt(betweenHourChoiceBox1.getValue()), Integer.parseInt(betweenMinuteChoiceBox1.getValue()), date);
			msg.setD1(date);
			
			//Get date from DatePicker And
			date = convertLocalDateToDate(datePickerBetween2.getValue());
			
			//Set hour and minute
			date = get2CalendarValuesDate(Calendar.HOUR, Calendar.MINUTE, Integer.parseInt(betweenHourChoiceBox2.getValue()), Integer.parseInt(betweenMinuteChoiceBox2.getValue()), date);
			msg.setD2(date);
			
			System.out.println(msg.getD1());
			System.out.println(msg.getD2());
		} else if (hbox.equals(hboxLast)) {
			System.out.println("HboxLast");
			msg.setState(DateRangeMessage.LAST);
			
			msg.setLast_state(DateRangeMessage.LAST_STATE_TIPS);
			msg.setLast_state_value(Integer.parseInt(lastTextField.getText()));
			System.out.println(msg.getLast_state_value());
		}
		notifyMainController();
		optionsController.hideDateRangeWindow();
	}
	
	/**
	 * Change 2 values, e.g. month and year, hour and minute, etc.
	 * @return
	 */
	private Date get2CalendarValuesDate(int date1, int date2, int value1, int value2) {
		Calendar now = Calendar.getInstance();
		Date date = new Date();
		now.setTime(date);
		now.set(date1, value1);
		now.set(date2, value2);
		date = now.getTime();
		return date;
	}

	private Date get2CalendarValuesDate(int date1, int date2, int value1, int value2, Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(date1, value1);
		now.set(date2, value2);
		date = now.getTime();
		return date;
	}
	
	
	private Date convertLocalDateToDate(LocalDate ld) {
		Instant instant = Instant.from(ld.atStartOfDay(ZoneId.systemDefault()));
		Date date = Date.from(instant);
		return date;
	}
	
	public static String theMonth(int month){
	    return monthNames[month];
	}
	
	public static int theNumberOfMonth(String month) {
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
	
	/**
	 * Populate the Years Dropdown field
	 */
	private void populateMonthYear() {
		ObservableList<String> list = FXCollections.observableArrayList(monthNames);
		monthMonth.setItems(list);
		
		int start = 2006; 							//STARTYEAR
		Calendar now = Calendar.getInstance();
		int end = now.get(Calendar.YEAR);			//ENDYEAR (current year
		list = FXCollections.observableArrayList();
		for(int i=end; i>=start; i-=1) {
			list.add(""+i);
		}
		monthYear.setItems(list);
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_DATERANGE_ID, msg)); 
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
		this.msg = filters.getDateRangeMessage();
		if (this.msg == null) return;
		
		disableNonSelectedRows(hboxes[this.msg.getState()]);
		radioButtons[this.msg.getState()].setSelected(true);
	}
}
