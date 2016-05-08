package bettingManager.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class KoBController {
	private MainController mainC;
	
	
	@FXML GridPane koBGrid;
	
	public void init(MainController mainC) {
		this.mainC = mainC;
	}
}
