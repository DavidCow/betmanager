package bettingManager.gui;

import java.util.ArrayList;
import java.util.Observable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

public class KoBController extends Observable{
	private MainController mainC;
	public static int KOB_ID = 4;
	
	private ArrayList<String> checkedKoB;
	
	@FXML GridPane koBGrid;
	
	public void init(MainController mainC) {
		this.mainC = mainC;
		this.addObserver(mainC);
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
		notifyMainController();
	}
	
	public ArrayList<String> getCheckedSites() {
		return checkedKoB;
	}
	
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(KOB_ID, checkedKoB));
	}
	
	public void updateSettings(FilterSettingsContainer filters) {
		if (filters.getKoBList() == null) return;
		this.checkedKoB = filters.getKoBList();
		for (Node cb : koBGrid.getChildren()) {
			if (filters.getKoBList().contains( ((CheckBox)cb).getText() )){
				((CheckBox)cb).setSelected(true);
			} else {
				((CheckBox)cb).setSelected(false);
			}
		}
	}
}
