package bettingManager.gui;

 
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
 
public class StageLoader extends Application {
	
	
	public static String WINDOW_TITLE = "Betting Manager 1.0";
	public static String MAIN_FXML = "/bettingManager/gui/layout/Main.fxml";
	public static String MAIN_CSS = "/bettingManager/gui/layout/style.css";
	
	
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(MAIN_FXML));
        Scene scene = new Scene(root);
        scene.setRoot(root);
        scene.getStylesheets().add(getClass().getResource(MAIN_CSS).toExternalForm());
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}