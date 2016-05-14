package bettingManager.gui;


import java.util.ArrayList;
import java.util.Observable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

public class SiteController extends Observable{
	private MainController mainC;
	public static int SITE_ID = 2;
	
	
	private ArrayList<String> checkedSites;
	
	@FXML
	GridPane siteGrid;

	public void init(MainController mainC) {
		this.mainC = mainC;
		this.addObserver(mainC);
	}

	@FXML
	public void handleSiteCheckbox(ActionEvent event) {
		System.out.println("CHECKBOX SITE");
		checkedSites = new ArrayList<String>();
		for (Node cb : siteGrid.getChildren()) {
			if (((CheckBox)cb).isSelected()) {
				checkedSites.add( ((CheckBox)cb).getText());
			}
		}
		System.out.println(checkedSites);
		notifyMainController();
	}
	
	public ArrayList<String> getCheckedSites() {
		return checkedSites;
	}
	
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(SITE_ID, checkedSites));
	}
	
	public void updateSettings(FilterSettingsContainer filters) {
		if (filters.getSitesList() == null) return;
		this.checkedSites = filters.getSitesList();
		
		for (Node cb : siteGrid.getChildren()) {
			if (filters.getSitesList().contains( ((CheckBox)cb).getText() )){
				((CheckBox)cb).setSelected(true);
			}
		}
	}
	
}
