package bettingManager.gui;


import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

public class SiteController {
	private MainController mainC;
	private ArrayList<String> checkedSites;
	
	@FXML
	GridPane siteGrid;

	public void init(MainController mainC) {
		this.mainC = mainC;
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
	}
	
	public ArrayList<String> getCheckedSites() {
		return checkedSites;
	}
}
