package bettingManager.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import bettingManager.statsCalculation.Alias;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class OptionsAddAliasesController extends Observable{
	private MainController mainC;
	public static int OPTIONS_ADDALIASES_ID = 9;
	
	private OptionsController optionsController;
	private OptionsTipstersController optionsTipstersController;
	
	//NEW
	public static String ALIASSELECTTIPSTERS_TITLE = "Select Tipsters";
	public static String ALIASSELECTTIPSTERS_RESOURCE = "/bettingManager/gui/layout/Options_AddAliasesSelectTipsters.fxml";
	public static String STYLESHEET = "/bettingManager/gui/layout/style.css";
	
	@FXML OptionsAddAliasesSelectTipstersController optionsAddAliasesSelectTipstersController;
	
	private Stage stageAliasSelectTipsters;
	//NEW
	
	private ArrayList<Alias> aliases = new ArrayList<Alias>();
	
	@FXML private ListView<String> lvAlias;
	@FXML private ListView<String> lvTipster;
	
	ObservableList<String> itemsAlias;
	ObservableList<String> itemsTipster;
	
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
		try {
			createSelectTipstersStage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		optionsAddAliasesSelectTipstersController.init(mainC, opt, this);
//		for (int i=0;i<10;i+=1) {
//			Alias a = new Alias();
//			a.setAliasName("Hello" + i);
//			a.getTipsters().add("Wurst" + i);
//			a.getTipsters().add("Pommes" + i);
//			aliases.add(a);
//		}
//		notifyMainController();
		setUpAliasTable();
	
	}
	
	private void createSelectTipstersStage() throws IOException {
		stageAliasSelectTipsters = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ALIASSELECTTIPSTERS_RESOURCE));
		Parent root = loader.load();
		optionsAddAliasesSelectTipstersController = (OptionsAddAliasesSelectTipstersController) loader.getController();
		root.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
		stageAliasSelectTipsters.setScene(new Scene(root));
		stageAliasSelectTipsters.setTitle(ALIASSELECTTIPSTERS_TITLE);
		stageAliasSelectTipsters.initModality(Modality.APPLICATION_MODAL);
	}
	
	public void updateAliasAfterSelectTipster() {
    	for(Alias s:aliases) {
			if (optionsAddAliasesSelectTipstersController.getCurrentAliasName().equals(s.getAliasName())) {
				lvTipster.getItems().clear();
//				itemsTipster.setAll(s);
				itemsTipster = FXCollections.observableArrayList(s.getTipsters());
				lvTipster.setItems(itemsTipster);
				break;
			}
		}
	}
	
	private void setUpAliasTable() {
		aliases = mainC.getAllFilters().getAliases();
		
		 itemsAlias = FXCollections.observableArrayList (getAliasNamesAsList(aliases));
			lvAlias.setItems(itemsAlias);
	        lvAlias.getSelectionModel().selectedItemProperty().addListener(
	                new ChangeListener<String>() {
	                    public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
	                            System.out.println(new_val);
	                        	for(Alias s:aliases) {
	                    			if (new_val.equals(s.getAliasName())) {
	                    				optionsAddAliasesSelectTipstersController.setCurrentAliasName(s.getAliasName());
	                    				lvTipster.getItems().clear();
//	                    				itemsTipster.setAll(s);
	                    				itemsTipster = FXCollections.observableArrayList(s.getTipsters());
	                    				lvTipster.setItems(itemsTipster);
	                    				break;
	                    			}
	                    		}
	                    }
	            });
	        lvAlias.setEditable(true);
	        lvAlias.setCellFactory(new Callback<ListView<String>, 
            ListCell<String>>() {
                @Override 
                public ListCell<String> call(ListView<String> list) {
                    return new TFListCell();
                }
            }
	        );
	}
	
	private List<String> getAliasNamesAsList(List<Alias> alis) {
		List<String> lis = new ArrayList<String>();
		for(Alias s:alis) {
			lis.add(s.getAliasName());
		}
		return lis;
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_ADDALIASES_ID, aliases)); 
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
		
	}
	
	
	/**
	 * Button handling
	 * @param action
	 */
	public void handleButtonNewAlias(ActionEvent action) {
		System.out.println("Button New Alias");
		String newAlias = "New Alias";
		lvAlias.getItems().add(newAlias);
		
		Alias a = new Alias();
		a.setAliasName(newAlias);
		
		aliases.add(a);
		notifyMainController();
	}
	public void handleButtonDeleteAlias(ActionEvent action) {
		System.out.println("Button Delete Alias");
//		lvAlias.getItems().remove(lvAlias.getSelectionModel().getSelectedIndex());
		
		//REMOVE FROM aliases
		Iterator<Alias> iter = aliases.iterator();

		while (iter.hasNext()) {
		    Alias a = iter.next();

		    if (a.getAliasName().equals(lvAlias.getSelectionModel().getSelectedItem())) {
//				aliases.remove(a);
		    	iter.remove();
			}
		}
		
		//REMOVE FROM listview
		try {
			lvAlias.getItems().remove(lvAlias.getSelectionModel().getSelectedItem());
		} catch (NullPointerException e) {
			System.out.println("Caught: "+e);
		}
		notifyMainController();
		
	}
	public void handleButtonAddTipster(ActionEvent action) {
		System.out.println("Button Add Tipster");
		
		
		//DEBUG CHECK FILTER
		System.out.println("no: " + mainC.getAllFilters().getAliases().size());
		for(Alias a:mainC.getAllFilters().getAliases()) {
			System.out.println(a.getAliasName());
			for (String s:a.getTipsters()) {
				System.out.println(s);
			}
			System.out.println("-");
		}
		
		
		stageAliasSelectTipsters.showAndWait();

	}
	
	public void hideSelectTipstersWindow() {
		stageAliasSelectTipsters.hide();
	}
	
	public void handleButtonDeleteTipster(ActionEvent action) {
		System.out.println("Button Delete Tipster");
		for (Alias a:aliases) {
			if (a.getAliasName().equals(lvAlias.getSelectionModel().getSelectedItem())) {
				a.getTipsters().remove(lvTipster.getSelectionModel().getSelectedItem());
			}
		}
		lvTipster.getItems().remove(lvTipster.getSelectionModel().getSelectedItem());
		notifyMainController();
	}
	
	
	/**
	 * Edit test
	 * @author David
	 *
	 */
	class TFListCell extends ListCell<String> {

	    private TextField textField;
	    private String oldText = "OLDTEXT";
	    
	    @Override
	    public void startEdit() {
	        if (!isEditable() || !getListView().isEditable()) {
	            return;
	        }
	        super.startEdit();

	        if (isEditing()) {
	            if (textField == null) {
//	                textField = new TextField(getItem());
	                textField = new TextField("NEW TF");
	                textField.setOnAction(new EventHandler<ActionEvent>() {
	                    @Override
	                    public void handle(ActionEvent event) {
	                        commitEdit(textField.getText());
	                        
	                        for(Alias a:aliases) {
	                        	if (a.getAliasName().equals(oldText)) {
	                        		a.setAliasName(textField.getText());
	                        	}
	                        }
	                        notifyMainController();
//	                        commitEdit("commitEdit");
	                        System.out.println("COMMITEDIT()");
	                    }
	                });
	                textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	                    @Override
	                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	                    	if(!newValue) {
//	                    		commitEdit(textField.getText());
	                        }
	                    }
	                });
	            }
	        }
	        System.out.println("OUTSIDE");
//	        textField.setText(getItem());
	        textField.setText(getText());
	        setText(null);

	        setGraphic(textField);
	        textField.selectAll();
	    }

	    @Override
	    public void updateItem(String item, boolean empty) {
	        super.updateItem(item, empty);
	        
	        if (isEmpty()) {
	            setText(null);
	            setGraphic(null);
	        } else {
	            if (!isEditing()) {
	                if (textField != null) {
	                    setText(textField.getText());
//	                    setText("wurst");
	                } else {
	                    setText(item);
	                    oldText = getText();
//	                    setText("wurst2");	//textField == null
	                }
	                setGraphic(null);
	            }
	        }
	    }
	}
	
}
