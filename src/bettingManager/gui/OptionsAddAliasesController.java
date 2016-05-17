package bettingManager.gui;

import java.util.Observable;

public class OptionsAddAliasesController extends Observable{
	private MainController mainC;
	public static int OPTIONS_ADDALIASES_ID = 9;
	
	private OptionsController optionsController;
	private OptionsTipstersController optionsTipstersController;
	
	/**
	 * Initialize
	 * @param mainC
	 * @param opt
	 */
	public void init(MainController mainC, OptionsController opt, OptionsTipstersController optT) {
		this.mainC = mainC;
		this.addObserver(mainC);
		this.optionsController = opt;
		this.optionsTipstersController = optT;
//		this.msg = new DateRangeMessage();
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_ADDALIASES_ID, null)); 
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
