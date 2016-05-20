package bettingManager.gui;

import java.util.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class LiquidityController extends Observable{
	private MainController mainC;
	public static int LIQUIDITY_ID = 5;
	
	@FXML private TextField greaterThanTF;
	@FXML private TextField lessThanTF;
	@FXML private TextField betweenTF;
	@FXML private TextField andTF;
	
	private OddsData oddsData;
	private String regex = "[^0-9\\.]+";
	
	public LiquidityController() {
		oddsData = new OddsData();
	}
	
	@FXML
	public void initialize() {
		greaterThanTF.focusedProperty().addListener(new OnOutFocusListener<Boolean>());
		lessThanTF.focusedProperty().addListener(new OnOutFocusListener<Boolean>());
		betweenTF.focusedProperty().addListener(new OnOutFocusListener<Boolean>());
		andTF.focusedProperty().addListener(new OnOutFocusListener<Boolean>());
	}
	
	public void init(MainController mainC) {
		this.mainC = mainC;
		this.addObserver(mainC);
	}
	
	public void handleGreaterThanLiq(KeyEvent event) {
		System.out.println("handle greater than");
		AverageOddsController.checkTFInput(event, greaterThanTF);
	}

	public void handleLessThanLiq(KeyEvent event) {
		System.out.println("handle less than");
		AverageOddsController.checkTFInput(event, lessThanTF);
	}
	
	public void handleBetweenLiq(KeyEvent event) {
		System.out.println("handle between");
		AverageOddsController.checkTFInput(event, betweenTF);
	}

	public void handleAndLiq(KeyEvent event) {
		System.out.println("handle and");
		AverageOddsController.checkTFInput(event, andTF);
	}
	
	
	/**
	 * Save odds values
	 * If no value or crap value, save float value "-1"
	 */
	public void saveOddsData() {
		try {
			oddsData.setGreaterThan(Float.parseFloat(greaterThanTF.getText()));
		}
		catch (NumberFormatException ex) {
			System.out.println("Parse error 1, no correct float number in one of the text fields.");
			oddsData.setGreaterThan(-1);
		}
		
		try {
			oddsData.setLessThan(Float.parseFloat(lessThanTF.getText()));
		}
		catch (NumberFormatException ex) {
			System.out.println("Parse error 2, no correct float number in one of the text fields.");
			oddsData.setLessThan(-1);
		}
		
		try {
			oddsData.setBetween(Float.parseFloat(betweenTF.getText()));
		}
		catch (NumberFormatException ex) {
			System.out.println("Parse error 3, no correct float number in one of the text fields.");
			oddsData.setBetween(-1);
		}

		try {
			oddsData.setAnd(Float.parseFloat(andTF.getText()));
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
		notifyObservers(new ObservableMessage(LIQUIDITY_ID, oddsData));
	}
	
	
	public void updateSettings(FilterSettingsContainer filters) {
		if (filters.getOddsDataAverageOdds() == null) return;
		
		this.oddsData = filters.getOddsDataLiquidity();
		greaterThanTF.setText(getValueString(oddsData.getGreaterThan()));
		lessThanTF.setText(getValueString(oddsData.getLessThan()));
		betweenTF.setText(getValueString(oddsData.getBetween()));
		andTF.setText(getValueString(oddsData.getAnd()));
	}
	
	private String getValueString(float val) {
		if (val == -1) return "";
		return ""+val;
	}
}


