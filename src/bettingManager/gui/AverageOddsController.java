package bettingManager.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class AverageOddsController {
	private MainController mainC;
	
	
	@FXML private TextField greaterThanTextField;
	@FXML private TextField lessThanTextField;
	@FXML private TextField betweenTextField;
	@FXML private TextField andTextField;
	
	public void init(MainController mainC) {
		this.mainC = mainC;
	}
	
	public void handleGreaterThan(KeyEvent event) {
		System.out.println("handle greater than");
	}

	public void handleLessThan(KeyEvent event) {
		System.out.println("handle less than");
	}
	
	public void handleBetween(KeyEvent event) {
		System.out.println("handle between");
	}

	public void handleAnd(KeyEvent event) {
		System.out.println("handle and");
	}
	
	
}
