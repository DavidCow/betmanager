package bettingManager.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import bettingManager.statsCalculation.Alias;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class OptionsTipstersController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TIPSTERS_ID = 8;
	
	public static final String PREFS_TIPSTERS = "PREFS_TIPSTERS";
	
	public static String ALIAS_TITLE = "Manage Aliases";
	public static String ALIAS_RESOURCE = "/bettingManager/gui/layout/Options_AddAliases.fxml";
	public static String STYLESHEET = "/bettingManager/gui/layout/style.css";
	
	private OptionsController optionsController;
	
	@FXML OptionsAddAliasesController optionsAddAliasesController;
	


	@FXML TableView<TipsterRow> tipsterTable;
	@FXML TextField tipsterSearchTextField;
	@FXML Button tipsterSearchButton;
	
	@FXML Label tipstersSelectedLabel;
	@FXML Button applyButton;
	
	private ArrayList<TipsterRow> tipsterAllForTable;
	private Stage stageAlias;
	
	@FXML Button buttonSelectAll;
	@FXML Button buttonDeselectAll;
	
	public static final String[] tipsterTitles = {
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
	
	private void createTipstersStage() throws IOException {
		stageAlias = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ALIAS_RESOURCE));
		Parent root = loader.load();
		optionsAddAliasesController = (OptionsAddAliasesController) loader.getController();
		root.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
		stageAlias.setScene(new Scene(root));
		stageAlias.setTitle(ALIAS_TITLE);
		stageAlias.initModality(Modality.APPLICATION_MODAL);
		
		stageAlias.setOnCloseRequest(new EventHandler<WindowEvent>() {

	            @Override
	            public void handle(WindowEvent event) {
	                Platform.runLater(new Runnable() {

	                    @Override
	                    public void run() {
	                        System.out.println("Application Closed by click to Close Button(X)");
	                        inflateTable(tipsterTitles);
	                    }
	                });
	            }
	        });
	}
	
	public void handleAliasButton(ActionEvent event) {
		stageAlias.showAndWait();
	}
	
	public void hideAliasWindow() {
		stageAlias.hide();
	}
	
	
	/**
	 * Initialize
	 * @param mainC
	 * @param opt
	 */
	public void init(MainController mainC, OptionsController opt) {
		this.mainC = mainC;
		this.addObserver(mainC);
		this.optionsController = opt;
		try {
			createTipstersStage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		optionsAddAliasesController.init(mainC, opt, this);
		
		
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
	boolean firstTime = true;
	/**
	 * Populate Table with Column Headers
	 * @param tableTitles
	 */
	public void inflateTable(String [] tableTitles) {
	
//		tipsterTable.getItems().clear();
		if (firstTime) {
			
		for(int i = 0; i<tableTitles.length; i+=1) {
			TableColumn<TipsterRow, String> newTC = new TableColumn<TipsterRow, String>(tableTitles[i]);
			newTC.setCellValueFactory(new PropertyValueFactory<TipsterRow, String>(tipstersTableValueNames[i]));
			tipsterTable.getColumns().add(newTC);
		}
		}
		
		List<TipsterRow> tipsterList = new ArrayList<TipsterRow>();
		//ADD TIPSTER STRINGS
		System.out.println("Reading Tipsters..");
//		Set<String> all = mainC.getStatsCalc().getAllTipsters();
		
		for(String key:mainC.getAllFilters().getTipstersMessage().keySet()) {
			tipsterList.add(stringToTipster(key));
		}
		System.out.println("Reading Tipsters Done!");
		
		System.out.println("Reading Aliases..");
		// ADD ALIASES TO Tipster List
		for(Alias al:mainC.getAllFilters().getAliases()) {
			tipsterList.add(aliasToTipster(al));
		}
		////
		System.out.println("Reading Aliases Done!");
		
		ObservableList<TipsterRow> data = FXCollections.observableList(tipsterList);
		
		if (firstTime) {
		tipsterTable.getItems().setAll(data);
//		tipsterTable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TipsterRow>() {
//
//			@Override
//			public void onChanged(ListChangeListener.Change<? extends TipsterRow> c) {
//			     for( TipsterRow t : c.getList()) {
////		                System.out.println(t);
//			     }
//			     System.out.println("ListChangeListener");
//			}
//		});
		}
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
	        
	        setTipstersSelectedLabel();
	    	if (firstTime) {
				firstTime = false;
			}
	}
	
	
	public static String ALIAS_SITE = "ALIAS";
	
	/**
	 * Create TipsterRow from Set element 
	 */
	private TipsterRow aliasToTipster(Alias a) {
		TipsterRow tr = new TipsterRow();
		CheckBox box = new CheckBox();
		box.setSelected(a.isSelected());
		tr.setInclude(box);
		tr.setTipster(a.getAliasName());
		tr.setSite(ALIAS_SITE);
		tr.setTips("-");
		tr.setAverageYield("-");
		return tr;
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
	    	if (tr.getSite().equals(ALIAS_SITE)) {
	    		for (Alias a:mainC.getAllFilters().getAliases()) {
	    			if (a.getAliasName().equalsIgnoreCase(tr.getTipster())) {
	    				a.setSelected(tr.getInclude().isSelected());
	    			}
	    		}
	    	} else {
	    		tipstersSaved.put(tipsterToString(tr), tr.getInclude().isSelected());
	    	}
	    }
//	    for (Alias a:mainC.getAllFilters().getAliases()) {
//	    	System.out.println(a.getAliasName() + ":" + a.isSelected());
//	    }
	    notifyMainController(tipstersSaved);
	    setTipstersSelectedLabel();
	    optionsController.hideTipstersWindow();
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
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
		Map<String, Boolean> tipsSaved = filters.getTipstersMessage();
		if (tipsSaved.size() <= 0 || tipsSaved == null) return;
		ObservableList<TipsterRow> data = tipsterTable.getItems();

	    for (TipsterRow tr: data){
	    	if (tr.getSite().equalsIgnoreCase(ALIAS_SITE)) {
	    		for (Alias a:mainC.getAllFilters().getAliases()) {
	    			if (a.getAliasName().equalsIgnoreCase(tr.getSite())) {
	    				tr.getInclude().setSelected(a.isSelected());
	    			}
	    		}
	    	} else {
	    		try {
	    			boolean selected = tipsSaved.get(tipsterToString(tr));
	    			tr.getInclude().setSelected(selected);
	    		} catch (NullPointerException e) {
	    			System.out.println(e);
	    			tr.getInclude().setSelected(true);
	    		}
	    	}
	    }
	    optionsAddAliasesController.updateSettings(filters);
	}
	
	public OptionsAddAliasesController getOptionsAddAliasesController() {
		return optionsAddAliasesController;
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
