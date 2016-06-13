package bettingManager.gui;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import bettingManager.statsCalculation.BettingManagerBet;
import bettingManager.statsCalculation.StatsRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class TableLastBetsController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TABLELASTBETS_ID = 1199;
	
	/**
	 * Column name definitions
	 */
	public final static String BETDATE = "Date";
	public final static String GAMEDATE = "Game Date";
	public final static String KOB = "Kind of Bet";
	public final static String TIPSTER = "Tipster";
	public final static String EVENT = "Event";
	public final static String SELECTION = "Selection";
	public final static String NETWON = "Net won";
	public final static String ODDS = "Odds";
	
	/**
	 * Variable names for Table injection
	 */
	public final static String[] lastBetsTableValueNames = {
			"betDate", 
			"gameDate",
			"koB",
			"tipster",
			"event",
			"selection",
			"netWon",
			"odds" };
	
	/**
	 * Title of column (string)
	 */
	public final static String[] TABLE_TITLES_LASTBETS = {
			BETDATE,
			GAMEDATE,
			KOB,
			TIPSTER,
			EVENT,
			SELECTION,
			NETWON,
			ODDS };
	
	
	@FXML TableView<BettingManagerBet> table;
	ObservableList<BettingManagerBet> data;
	
	/**
	 * Initialize
	 */
	public void init(MainController mainC) {
		this.mainC = mainC;
		data = FXCollections.observableList(new ArrayList<BettingManagerBet>());
		inflateTable(TABLE_TITLES_LASTBETS);

		table.getSelectionModel().setSelectionMode(
		    SelectionMode.MULTIPLE
		);
	}
	
	/**
	 * Populate Table with Column Headers
	 * @param tableTitles
	 */
	private void inflateTable(String [] tableTitles) {
		System.out.println("Inflating Last Bets table...");
		
		for(int i = 0; i<tableTitles.length; i+=1) {
			TableColumn<BettingManagerBet, Object> newTC = new TableColumn<BettingManagerBet, Object>(tableTitles[i]);
			 newTC.setCellFactory(TextFieldTableCell.<BettingManagerBet, Object>forTableColumn(new StringConverter<Object>() {
			        private final NumberFormat nf = NumberFormat.getNumberInstance();

			        {
			             nf.setMaximumFractionDigits(2);
			             nf.setMinimumFractionDigits(2);
			        }


			        @Override public String fromString(final String s) {
			            // Don't need this, unless table is editable, see DoubleStringConverter if needed
			            return null; 
			        }

					@Override
					public String toString(Object value) {
						if (value instanceof String) {
							return (String) value;
						} else if (value instanceof Date) {
								return ((Date) value).toString();
			        	} else if (value instanceof Double) {
			        		return ((Double) value).toString();
//			        		return nf.format(Double.parseDouble((String) value));
			        	}
						return "NOT WORKING";
					}
			    }));
			newTC.setCellValueFactory(new PropertyValueFactory<BettingManagerBet, Object>(lastBetsTableValueNames[i]));
			table.getColumns().add(newTC);
		}
		
		
		/**
		 * TEST TO FILL IN TABLES WITH DATA
		 */
//		List<BettingManagerBet> rows = this.mainC.getStatsCalc().getMonthlyStats();
			
//		data = FXCollections.observableList(rows);
		
		table.getItems().setAll(data);
//		table.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<BettingManagerBet>() {
//
//			@Override
//			public void onChanged(ListChangeListener.Change<? extends BettingManagerBet> c) {
//			     for(BettingManagerBet t : c.getList())
//		                System.out.println(t);
//			}
//		});
		/**
		 * TEST TO FILL IN TABLES WITH DATA
		 */
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_TABLELASTBETS_ID, null)); 
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
//		this.msg = filters.getDateRangeMessage();
//		if (this.msg == null) return;
	}

	public void setDataList(ObservableList<? extends StatsRow> list) {
		List<BettingManagerBet> bets = new ArrayList<BettingManagerBet>();
		for(StatsRow statsRow:list) {
			for (BettingManagerBet bet:statsRow.getBets()) {
				bets.add(bet);
			}
		}
		table.getColumns().clear();
		data = FXCollections.observableList(bets);
		inflateTable(TABLE_TITLES_LASTBETS);
	}
}
