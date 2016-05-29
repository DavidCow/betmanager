package bettingManager.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import bettingManager.statsCalculation.StatsCalculator;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
	@FXML TextField tipsterSearchTextField;
	@FXML Button tipsterSearchButton;
	
	@FXML Label tipstersSelectedLabel;
	@FXML Button applyButton;
	
	private ArrayList<TipsterRow> tipsterAllForTable;
	
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
		
		setTipstersSelectedLabel();
		
		tipsterAllForTable = putObservableToArrayListTipsters(tipsterTable.getItems());
		
//		this.msg = new DateRangeMessage();
	}
	
	private ArrayList<TipsterRow> putObservableToArrayListTipsters(ObservableList<TipsterRow> items) {
		ArrayList<TipsterRow> trow = new ArrayList<TipsterRow>(); 
//		for (TipsterRow tr: items){
			trow.addAll(items);
//		}
		return trow;
	}

	private void setTipstersSelectedLabel() {
		if (mainC.getAllFilters() != null && mainC.getAllFilters().getTipstersMessage() != null) {
			tipstersSelectedLabel.setText(countSelectedTipsters(mainC.getAllFilters().getTipstersMessage())+" Tipster selected.");
		}
	}
	
	private int countSelectedTipsters(Map<String, Boolean> tipsters) {
		int num = 0;
		for(String s:tipsters.keySet()) {
			if (tipsters.get(s)) {
				num += 1;
			}
		}
		return num;
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
		FilteredList<TipsterRow> filteredData = new FilteredList<>(data, p -> true);
		
		  tipsterSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
	            filteredData.setPredicate(tipsterRow -> {
	                // If filter text is empty, display all persons.
	                if (newValue == null || newValue.isEmpty()) {
	                    return true;
	                }

	                // Compare first name and last name of every person with filter text.
	                String lowerCaseFilter = newValue.toLowerCase();

	                if (tipsterRow.getTipster().toLowerCase().contains(lowerCaseFilter)) {
	                    return true; // Filter matches first name.
	                } 
	                return false; // Does not match.
	            });
	        });
		  SortedList<TipsterRow> sortedData = new SortedList<>(filteredData);

	        // 4. Bind the SortedList comparator to the TableView comparator.
	        sortedData.comparatorProperty().bind(tipsterTable.comparatorProperty());

	        // 5. Add sorted (and filtered) data to the table.
	        tipsterTable.setItems(sortedData);
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
	
	private String tipsterToString(TipsterRow tr) {
		return tr.getTipster()+"("+tr.getSite()+")";
	}
	
	public static final String SELECTION_LIMITER = " -- ";
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController(Map<String, Boolean> lis) {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_TIPSTERS_ID, lis)); 
	}
	
	
	@FXML
	public void handleApply(ActionEvent event) {
		System.out.println("Apply");
	    ObservableList<TipsterRow> data = tipsterTable.getItems();

	    Map<String, Boolean> tipstersSaved = new HashMap<String, Boolean>();
	    for (TipsterRow tr: data){
	    	tipstersSaved.put(tipsterToString(tr), tr.getInclude().isSelected());
	    }
	    notifyMainController(tipstersSaved);
	    setTipstersSelectedLabel();
	}

	/**
	 * Filter by text from TextField
	 * @param event
	 */
	public void handleTipsterSearch(ActionEvent event) {
//		System.out.println("SEARCH..");
//		ObservableList<TipsterRow> data = tipsterTable.getItems();
//		System.out.println("0");
//		//Clear and put all initial tipster back into table
//		//then filter through textfield text again
//		data.clear();
//		System.out.println("1");
//		data.addAll(tipsterAllForTable);
//		System.out.println("2");
//		for(int i=0; i<data.size(); i+=1) {
//			if (data.get(i).getTipster().contains(tipsterSearchTextField.getText())) {
//				System.out.println(data.get(i).getTipster() + " contains:  " + tipsterSearchTextField.getText());
//			} else {
////	    		data.remove(data.get(i));
//			}
//		}
//		System.out.println("3");
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
		Map<String, Boolean> tipsSaved = filters.getTipstersMessage();
		if (tipsSaved.size() <= 0 || tipsSaved == null) return;
		ObservableList<TipsterRow> data = tipsterTable.getItems();

	    for (TipsterRow tr: data){
	    	try {
	    		boolean selected = tipsSaved.get(tipsterToString(tr));
	    		tr.getInclude().setSelected(selected);
	    	} catch (NullPointerException e) {
	    		System.out.println(e);
	    		tr.getInclude().setSelected(true);
	    	}
	    }
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
