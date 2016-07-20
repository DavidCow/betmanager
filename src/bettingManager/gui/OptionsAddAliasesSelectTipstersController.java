package bettingManager.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import bettingManager.statsCalculation.Alias;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class OptionsAddAliasesSelectTipstersController extends Observable{
	private MainController mainC;
	public static int OPTIONS_ADDALIASESSELECTTIPSTERS_ID = 89384;
	
	private OptionsController optionsController;
	
	@FXML OptionsAddAliasesController optionsAddAliasesController;
	


	@FXML TableView<TipsterRow> tipsterTable;
	@FXML TextField tipsterSearchTextField;
	@FXML Button tipsterSearchButton;
	
	@FXML Button applyButton;
	
	private ArrayList<TipsterRow> tipsterAllForTable;
	
	@FXML Button buttonSelectAll;
	@FXML Button buttonDeselectAll;
	
	private String currentAliasName;
	
	public String getCurrentAliasName() {
		return currentAliasName;
	}

	public void setCurrentAliasName(String currentAliasName) {
		this.currentAliasName = currentAliasName;
	}

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
	public void init(MainController mainC, OptionsController opt, OptionsAddAliasesController optAddAliasesContr) {
		this.mainC = mainC;
		this.addObserver(mainC);
		this.optionsController = opt;
		this.optionsAddAliasesController = optAddAliasesContr;
		
		tipsterTable.getSelectionModel().setSelectionMode(
			    SelectionMode.MULTIPLE
		);
		inflateTable(tipsterTitles);
		
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
		System.out.println("Reading Select Tipsters..");
//		Set<String> all = mainC.getStatsCalc().getAllTipsters();
		
		for(String key:mainC.getAllFilters().getTipstersMessage().keySet()) {
			tipsterList.add(stringToTipster(key));
		}
		System.out.println("Reading Select Tipsters Done!");
		
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
	private void notifyMainController(ArrayList<Alias> lis) {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_ADDALIASESSELECTTIPSTERS_ID, lis)); 
	}
	
	
//	ADD TIPSTER TO ALIAS
	@FXML
	public void handleApply(ActionEvent event) {
		System.out.println("Apply");
	    ObservableList<TipsterRow> data = tipsterTable.getItems();

	    Map<String, Boolean> tipstersSaved = new HashMap<String, Boolean>();
	    ArrayList<Alias> newList = mainC.getAllFilters().getAliases();
	    for(Alias a:newList) {
			if(a.getAliasName().equals(currentAliasName)) {
				a.getTipsters().removeAll(a.getTipsters());
			}
		}
	    
	    for (TipsterRow tr: data){
	    	if (tr.getInclude().isSelected()) {
	    		for(Alias a:newList) {
	    			if(a.getAliasName().equals(currentAliasName)) {
	    				a.getTipsters().add(tipsterToString(tr));
	    			}
	    		}
	    	}
//	    	tipstersSaved.put(tipsterToString(tr), tr.getInclude().isSelected());
	    }
	    notifyMainController(newList);
	    optionsAddAliasesController.updateAliasAfterSelectTipster();
	    optionsAddAliasesController.hideSelectTipstersWindow();
	}

	/**
	 * Filter by text from TextField
	 * @param event
	 */
	public void handleTipsterSearch(ActionEvent event) {
	}
	
	public void handleSelectAll(ActionEvent event){
	    setSelectedAll(true);
	}

	public void handleDeselectAll(ActionEvent event){
	   setSelectedAll(false);
	}
	
	private void setSelectedAll(boolean bool) {
		 for (TipsterRow tr: tipsterTable.getItems()){
		    	tr.getInclude().setSelected(bool);
		 }
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
	    optionsAddAliasesController.updateSettings(filters);
	}
	
	public OptionsAddAliasesController getOptionsAddAliasesController() {
		return optionsAddAliasesController;
	}
	public OptionsAddAliasesSelectTipstersController getOptionsAddAliasesSelectTipstersController() {
		return this;
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
