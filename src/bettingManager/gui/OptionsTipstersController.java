package bettingManager.gui;

import java.util.Observable;

import javafx.fxml.FXML;
import javafx.scene.control.CustomMenuItem;

public class OptionsTipstersController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TIPSTERS_ID = 8;
	
	private OptionsController optionsController;
	
	@FXML OptionsAddAliasesController optionsAddAliasesController;
	
	@FXML CustomMenuItem customMenuItemAddAliases;
	
	/**
	 * Initialize
	 * @param mainC
	 * @param opt
	 */
	public void init(MainController mainC, OptionsController opt) {
		this.mainC = mainC;
		this.addObserver(mainC);
		this.optionsController = opt;
		optionsAddAliasesController.init(mainC, opt, this);
		
		customMenuItemAddAliases.setHideOnClick(false);
		
//		this.msg = new DateRangeMessage();
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_TIPSTERS_ID, null)); 
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
//		this.msg = filters.getDateRangeMessage();
//		if (this.msg == null) return;
	}
}