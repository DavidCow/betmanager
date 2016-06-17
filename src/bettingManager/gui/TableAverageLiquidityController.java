package bettingManager.gui;

import java.text.NumberFormat;
import java.util.List;
import java.util.Observable;

import bettingManager.statsCalculation.StatsRow;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class TableAverageLiquidityController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TABLEAVERAGELIQUIDITY_ID = 11;
	
	
	@FXML TableView<StatsRow> table;
	private ObservableList<StatsRow> data;
	
	/**
	 * Initialize
	 */
	public void init(MainController mainC) {
		this.mainC = mainC;
		inflateTable(TableTitles.TABLE_TITLES_AVERAGELIQUIDITY);

		
		table.getSelectionModel().setSelectionMode(
		    SelectionMode.MULTIPLE
		);
	}
	
	/**
	 * Populate Table with Column Headers
	 * @param tableTitles
	 */
	private void inflateTable(String [] tableTitles) {
		System.out.println("Inflating table...");
		table.getColumns().clear();
		for(int i = 0; i<tableTitles.length; i+=1) {
			TableColumn<StatsRow, Object> newTC = new TableColumn<StatsRow, Object>(tableTitles[i]);
			 newTC.setCellFactory(TextFieldTableCell.<StatsRow, Object>forTableColumn(new StringConverter<Object>() {
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
						try {
							if (value instanceof String) {
								return (String)value;
//								return nf.format(Double.parseDouble((String) value));
							} else {
								return nf.format((Double) value);
							}
			        	} catch (NumberFormatException n) {
			        		return (String)value;
			        	}
					}
			    }));
			newTC.setCellValueFactory(new PropertyValueFactory<StatsRow, Object>(TableTitles.averageLiquidityTableValueNames[i]));
			table.getColumns().add(newTC);
		}
		
		
		/**
		 * TEST TO FILL IN TABLES WITH DATA
		 */
		System.out.println("Reading Average Liquidity Data...");
		List<StatsRow> rows = this.mainC.getStatsCalc().getLiquidityStats();
		System.out.println("Reading Average Liquidity Data Done!");
			
		data = FXCollections.observableList(rows);
		
		table.getItems().setAll(data);
		table.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<StatsRow>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends StatsRow> c) {
				mainC.getTableLastBetsController().setDataList(c.getList());
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
		notifyObservers(new ObservableMessage(OPTIONS_TABLEAVERAGELIQUIDITY_ID, null)); 
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
//		this.msg = filters.getDateRangeMessage();
//		if (this.msg == null) return;
	}
	
	public ObservableList<StatsRow> getData() {
		return data;
	}
}
