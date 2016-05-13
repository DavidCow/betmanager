package bettingManager.gui;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

public class KoBController {
	private MainController mainC;
	private ArrayList<String> checkedKoB;
	
	@FXML GridPane koBGrid;
	
	public void init(MainController mainC) {
		this.mainC = mainC;
	}
	
	@FXML
	public void handleKoBCheckbox(ActionEvent event) {
		System.out.println("CHECKBOX SITE");
		checkedKoB = new ArrayList<String>();
		for (Node cb : koBGrid.getChildren()) {
			if (((CheckBox)cb).isSelected()) {
				checkedKoB.add( ((CheckBox)cb).getText());
			}
		}
		System.out.println(checkedKoB);
	}
	
	public ArrayList<String> getCheckedSites() {
		return checkedKoB;
	}
	
}
