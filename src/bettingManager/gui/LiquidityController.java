package bettingManager.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class LiquidityController {
	private MainController mainC;
	
	@FXML private TextField greaterThanTF;
	@FXML private TextField lessThanTF;
	@FXML private TextField betweenTF;
	@FXML private TextField andTF;
	
	public void init(MainController mainC) {
		this.mainC = mainC;
	}

	public void handleGreaterThanLiq(KeyEvent event) {
		System.out.println("handle greater than liq");
	}

	public void handleLessThanLiq(KeyEvent event) {
		System.out.println("handle less than liq");
	}
	
	public void handleBetweenLiq(KeyEvent event) {
		System.out.println("handle between liq");
	}

	public void handleAndLiq(KeyEvent event) {
		System.out.println("handle and liq");
	}

}


