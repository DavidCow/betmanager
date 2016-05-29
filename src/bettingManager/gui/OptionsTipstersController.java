package bettingManager.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import bettingManager.statsCalculation.StatsCalculator;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class OptionsTipstersController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TIPSTERS_ID = 8;
	
	public static final String PREFS_TIPSTERS = "PREFS_TIPSTERS";
	
	private OptionsController optionsController;
	
	private StatsCalculator statsCalculator;
	
	@FXML OptionsAddAliasesController optionsAddAliasesController;
	
	@FXML CustomMenuItem customMenuItemAddAliases;
	
	@FXML TableView<TipsterRow> tipsterTable;
	
	@FXML Button applyButton;
	
	private final String[] tipsterTitles = {
				"+",
				"Tipster",
				"Site",
				"Tips",
				"Average Yield"
	};
	
	public final static String[] tipstersTableValueNames = 
		{
				"include",
				"tipster",
				"site",
				"tips",
				"averageYield"
		};
	
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
		
		this.statsCalculator = new StatsCalculator();
		
		customMenuItemAddAliases.setHideOnClick(false);
		
		tipsterTable.getSelectionModel().setSelectionMode(
			    SelectionMode.MULTIPLE
			);
		inflateTable(tipsterTitles);
		
//		this.msg = new DateRangeMessage();
	}
	
	/**
	 * Populate Table with Column Headers
	 * @param tableTitles
	 */
	private void inflateTable(String [] tableTitles) {
		for(int i = 0; i<tableTitles.length; i+=1) {
			TableColumn<TipsterRow, String> newTC = new TableColumn<TipsterRow, String>(tableTitles[i]);
			newTC.setCellValueFactory(new PropertyValueFactory<TipsterRow, String>(tipstersTableValueNames[i]));
			tipsterTable.getColumns().add(newTC);
		}
		
		List<TipsterRow> tipsterList = new ArrayList<TipsterRow>();
		//ADD TIPSTER STRINGS
		Set<String> all = statsCalculator.getAllTipsters();
		
		for(String t:all) {
			tipsterList.add(stringToTipster(t));
		}
		
		ObservableList<TipsterRow> data = FXCollections.observableList(tipsterList);
		
		tipsterTable.getItems().setAll(data);
		tipsterTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TipsterRow>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends TipsterRow> c) {
			     for( TipsterRow t : c.getList()) {
//		                System.out.println(t);
			     }
			     System.out.println("ListChangeListener");
			}
		});

	}
	
	/**
	 * Create TipsterRow from Set element 
	 */
	private TipsterRow stringToTipster(String t) {
		TipsterRow tr = new TipsterRow();
		CheckBox box = new CheckBox();
		box.setSelected(true);
		tr.setInclude(box);
		tr.setTipster(t.substring(0, t.indexOf("(")));
		tr.setSite(t.substring(t.indexOf("(")+1, t.length()-1));
		tr.setTips("-");
		tr.setAverageYield("-");
		return tr;
	}
	
	private String TipsterToString(TipsterRow tr) {
		return tr.getTipster()+"("+tr.getSite()+")";
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_TIPSTERS_ID, tipsterTable.getItems())); 
	}
	
	
	@FXML
	public void handleApply(ActionEvent event) {
		System.out.println("Apply");
	    ObservableList<TipsterRow> data = tipsterTable.getItems();

	    ArrayList<String> tipstersSaved = new ArrayList<String>();
	    for (TipsterRow tr: data){
	    	System.out.println(tr.getInclude().selectedProperty());
	    }
//	    notifyMainController();
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
//		this.msg = filters.getDateRangeMessage();
//		if (this.msg == null) return;
	}
	
	/**
	 * Table row
	 *
	 */
	public class TipsterRow {
		private CheckBox include;
		private String tipster;
		private String site;
		private String tips;
		private String averageYield;

		public CheckBox getInclude() {
			return include;
		}
		public void setInclude(CheckBox include) {
			this.include = include;
		}
		public String getTipster() {
			return tipster;
		}
		public void setTipster(String tipster) {
			this.tipster = tipster;
		}
		public String getSite() {
			return site;
		}
		public void setSite(String site) {
			this.site = site;
		}
		public String getTips() {
			return tips;
		}
		public void setTips(String tips) {
			this.tips = tips;
		}
		public String getAverageYield() {
			return averageYield;
		}
		public void setAverageYield(String averageYield) {
			this.averageYield = averageYield;
		}
		
	}
	
}
