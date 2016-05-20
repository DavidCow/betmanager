package bettingManager.gui;

import java.util.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 4 TextFields
 * @author David
 *
 */
public class AverageOddsController extends Observable{
	private MainController mainC;
	public static int AVERAGEODDS_ID = 3;
	
	@FXML private TextField greaterThanTextField;
	@FXML private TextField lessThanTextField;
	@FXML private TextField betweenTextField;
	@FXML private TextField andTextField;
	
	private OddsData oddsData;
	private static String regex = "[^0-9\\.]+";
	
	public AverageOddsController() {
		oddsData = new OddsData();
	}
	
	@FXML
	public void initialize() {
		greaterThanTextField.focusedProperty().addListener(new OnOutFocusListener<Boolean>());
		lessThanTextField.focusedProperty().addListener(new OnOutFocusListener<Boolean>());
		betweenTextField.focusedProperty().addListener(new OnOutFocusListener<Boolean>());
		andTextField.focusedProperty().addListener(new OnOutFocusListener<Boolean>());
	}
	
	public void init(MainController mainC) {
		this.mainC = mainC;
		this.addObserver(mainC);
	}
	
	public void handleGreaterThan(KeyEvent event) {
		System.out.println("handle greater than");
		checkTFInput(event, greaterThanTextField);
	}

	public void handleLessThan(KeyEvent event) {
		System.out.println("handle less than");
		checkTFInput(event, lessThanTextField);
	}
	
	public void handleBetween(KeyEvent event) {
		System.out.println("handle between");
		checkTFInput(event, betweenTextField);
	}

	public void handleAnd(KeyEvent event) {
		System.out.println("handle and");
		checkTFInput(event, andTextField);
	}

	/**
	 * Check after each keyinput if text is correct
	 * @param event
	 * @param actTF
	 */
	public static void checkTFInput(KeyEvent event, TextField actTF) {
		if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) return;
		int caretPos = actTF.getCaretPosition();
		actTF.setText(actTF.getText().replaceAll(regex, ""));
		actTF.positionCaret(caretPos);
	}
	
	
	/**
	 * Save odds values
	 * If no value or crap value, save float value "-1"
	 */
	public void saveOddsData() {
		try {
			oddsData.setGreaterThan(Float.parseFloat(greaterThanTextField.getText()));
		}
		catch (NumberFormatException ex) {
			System.out.println("Parse error 1, no correct float number in one of the text fields.");
			oddsData.setGreaterThan(-1);
		}
		
		try {
			oddsData.setLessThan(Float.parseFloat(lessThanTextField.getText()));
		}
		catch (NumberFormatException ex) {
			System.out.println("Parse error 2, no correct float number in one of the text fields.");
			oddsData.setLessThan(-1);
		}
		
		try {
			oddsData.setBetween(Float.parseFloat(betweenTextField.getText()));
		}
		catch (NumberFormatException ex) {
			System.out.println("Parse error 3, no correct float number in one of the text fields.");
			oddsData.setBetween(-1);
		}

		try {
			oddsData.setAnd(Float.parseFloat(andTextField.getText()));
		}
		catch (NumberFormatException ex) {
			System.out.println("Parse error 4, no correct float number in one of the text fields.");
			oddsData.setAnd(-1);
		}
	}
	
	
	/**
	 * Out of focus listener
	 * Saves the current values from the four TextFields and saves it in local
	 * instance of OddsData
	 * @author David
	 *
	 * @param <Boolean>
	 */
	private class OnOutFocusListener<Boolean> implements ChangeListener<Boolean> {
		@Override
	    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
	    {
	        if ((boolean) newPropertyValue)
	        {
	            System.out.println("Textfield on focus");
	        }
	        else
	        {
	            System.out.println("Textfield out focus");
	            saveOddsData();
	            notifyMainController();
//	            System.out.println("1::: "+oddsData.getGreaterThan());
//	            System.out.println("2::: "+oddsData.getLessThan());
//	            System.out.println("3::: "+oddsData.getBetween());
//	            System.out.println("4::: "+oddsData.getAnd());
	        }
	    }
	}
	
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(AVERAGEODDS_ID, oddsData));
	}
	
	public void updateSettings(FilterSettingsContainer filters) {
		if (filters.getOddsDataAverageOdds() == null) return;
		
		this.oddsData = filters.getOddsDataAverageOdds();
		greaterThanTextField.setText(getValueString(oddsData.getGreaterThan()));
		lessThanTextField.setText(getValueString(oddsData.getLessThan()));
		betweenTextField.setText(getValueString(oddsData.getBetween()));
		andTextField.setText(getValueString(oddsData.getAnd()));
	}
	
	/**
	 * A value that is not set is internally set to -1. The textfield should just display an empty string
	 * @param val
	 * @return
	 */
	private String getValueString(float val) {
		if (val == -1) return "";
		return ""+val;
	}
	
}
