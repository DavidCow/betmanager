package bettingManager.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableKindOfBetController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TABLEKINDOFBET_ID = 10;
	
	@FXML TableView<TableValue> tableKindOfBet;
	
	/**
	 * Initialize
	 */
	public void init(MainController mainC) {
		this.mainC = mainC;
		inflateTable(TableTitles.TABLE_TITLES_KINDOFBET);

		tableKindOfBet.getSelectionModel().setSelectionMode(
		    SelectionMode.MULTIPLE
		);
	}
	
	/**
	 * Populate Table with Column Headers
	 * @param tableTitles
	 */
	private void inflateTable(String [] tableTitles) {
		for(int i = 0; i<tableTitles.length; i+=1) {
			TableColumn<TableValue, String> newTC = new TableColumn<TableValue, String>(tableTitles[i]);
			newTC.setCellValueFactory(new PropertyValueFactory<TableValue, String>(TableTitles.kindOfBetTableValueNames[i]));
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
		TableValue tv = new TableValue();
		tv.setKindOfBet(new String("works"));
		tv.setAverageYield(new String("works"));
		tv.setAverageOdds(new String("works"));
		tv.setNumberOfBets(new String("works"));
		tv.setPercentWeGet(new String("works"));
		tv.setPercentOver95(new String("works"));
		tv.setAverageLiquidity(new String("works"));
		tv.setPercentOfTipsFound(new String("works"));
		tv.setFlatStakeYield(new String("works"));
		
		TableValue tv2 = new TableValue();
		tv2.setKindOfBet(new String("works2"));
		tv2.setAverageYield(new String("works2"));
		tv2.setAverageOdds(new String("works2"));
		tv2.setNumberOfBets(new String("works2"));
		tv2.setPercentWeGet(new String("works2"));
		tv2.setPercentOver95(new String("works2"));
		tv2.setAverageLiquidity(new String("works2"));
		tv2.setPercentOfTipsFound(new String("works2"));
		tv2.setFlatStakeYield(new String("works2"));
		
		List<TableValue> arr = new ArrayList<TableValue>();
		arr.add(tv);
		arr.add(tv2);
		ObservableList<TableValue> data = FXCollections.observableList(arr);
		
		tableKindOfBet.getItems().setAll(data);
		tableKindOfBet.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TableValue>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends TableValue> c) {
			     for( TableValue t : c.getList())
		                System.out.println(t);
			}
		});
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
