package bettingManager.gui;

import java.util.Observable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

/**
 * 1st item of filters
 * @author David
 *
 */
public class Checkbox1Controller extends Observable{

	private MainController mainC;
	public static int CHECKBOX1_ID = 0;
	
	/**
	 * Group 1 Checkboxes Historic / Real
	 */
	@FXML private CheckBox checkboxGroup1historic;
	@FXML private CheckBox checkboxGroup1real;

	// States
	private int checkboxGroup1state = 0;
	public static int CHECKBOX_GROUP1_HISTORIC = 0;
	public static int CHECKBOX_GROUP1_REAL = 1;
	public static int CHECKBOX_GROUP1_BOTH = 2;

	/**
	 * Handle Checkbox group 1 (Historic / Real)
	 * 
	 * @param event
	 */
	public void handleCheckboxGroup1(ActionEvent event) {
		// Historic and Real
		if (checkboxGroup1historic.isSelected() && checkboxGroup1real.isSelected()) {
			checkboxGroup1state = CHECKBOX_GROUP1_BOTH;
		}

		// Only Historic
		if (checkboxGroup1historic.isSelected() && !checkboxGroup1real.isSelected()) {
			checkboxGroup1state = CHECKBOX_GROUP1_HISTORIC;
		}

		// Only Real
		if (!checkboxGroup1historic.isSelected() && checkboxGroup1real.isSelected()) {
			checkboxGroup1state = CHECKBOX_GROUP1_REAL;
		}

		// If none selected (not possible),
		// then leave the last selection
		if (!checkboxGroup1historic.isSelected() && !checkboxGroup1real.isSelected()) {
			CheckBox triggerCheckbox = ((CheckBox) event.getSource());
			triggerCheckbox.setSelected(true);

			if (triggerCheckbox.equals(checkboxGroup1historic)) {
				checkboxGroup1state = CHECKBOX_GROUP1_HISTORIC;
			}
			if (triggerCheckbox.equals(checkboxGroup1real)) {
				checkboxGroup1state = CHECKBOX_GROUP1_REAL;
			}
		}
		
		System.out.println("State changed: " + getCheckboxState());
		notifyMainController();
	}
	
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(CHECKBOX1_ID, getCheckboxState()));
	}
	
	public int getCheckboxState() {
		return checkboxGroup1state;
	}
	
	public void init(MainController mainC) {
		this.mainC = mainC;
		this.addObserver(mainC);
	}

	public void updateSettings(FilterSettingsContainer filters) {
		checkboxGroup1state = filters.getDataState();
		if (filters.getDataState() == CHECKBOX_GROUP1_BOTH) {
			checkboxGroup1historic.setSelected(true);
			checkboxGroup1real.setSelected(true);
		}
		if (filters.getDataState() == CHECKBOX_GROUP1_HISTORIC) {
			checkboxGroup1historic.setSelected(true);
			checkboxGroup1real.setSelected(false);
		}
		if (filters.getDataState() == CHECKBOX_GROUP1_REAL) {
			checkboxGroup1historic.setSelected(false);
			checkboxGroup1real.setSelected(true);
		}
		
	}
	
}
