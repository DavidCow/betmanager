package bettingManager.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class SiteController {
	private MainController mainC;
	
	@FXML GridPane siteGrid;
	
	public void init(MainController mainC) {
		this.mainC = mainC;
	}
}
