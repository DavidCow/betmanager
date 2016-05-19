package bettingManager.gui;

import java.io.IOException;
import java.util.List;
import java.util.Observable;

import betadvisor.BetAdvisorElement;
import betadvisor.BetAdvisorParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableKindOfBetController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TABLEKINDOFBET_ID = 10;
	
	  private final ObservableList<String> data =
		        FXCollections.observableArrayList(
		        		"T", "T2", "T3","T", "T2", "T3","T", "T2", "T3"
		        		);   
	
	@FXML TableView tableKindOfBet;
	
	/**
	 * Initialize
	 * @param mainC
	 * @param opt
	 */
	public void init(MainController mainC) {
		this.mainC = mainC;
		inflateTable(TableTitles.TABLE_TITLES_KINDOFBET);
	}
	
	private void inflateTable(String [] tableTitles) {
		for(int i=0; i<tableTitles.length; i+=1) {
			TableColumn newTC = new TableColumn(tableTitles[i]);
			tableKindOfBet.getColumns().add(newTC);
		}
		
		
		/**
		 * TEST TO FILL IN TABLES WITH DATA
		 */
//		BetAdvisorParser betAdvisorParser = new BetAdvisorParser();
//		List<BetAdvisorElement> betAdvisorList = null;
//		try {
//			betAdvisorList = betAdvisorParser.parseSheets("TipsterData/csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

//		tableKindOfBet.setItems(betAdvisorList);
		
		/**
		 * TEST TO FILL IN TABLES WITH DATA
		 */
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_TABLEKINDOFBET_ID, null)); 
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
