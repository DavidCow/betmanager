package bettingManager.gui;

import java.util.List;
import java.util.Observable;


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;

public class TableGraphController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TABLEGRAPH_ID = 1137;
	
	public final String TITLE = "Graphs";

	public final String BLOGABET = "Blogabet";
	public final String BETADVISOR = "BetAdvisor";
	public final String TOTAL = "Total";
	public final String X_AXIS = "Time";
	public final String Y_AXIS = "Bets";
	
	public final String[] graphsTitleArray = {BLOGABET, BETADVISOR, TOTAL};
	
	LineChart<Number, Number> lineChart;
	@FXML AnchorPane graphAnchorPane;
	
	/**
	 * Initialize
	 */
	public void init(MainController mainC) {
		this.mainC = mainC;
//		setUpLineChart();
		new Thread(task).start();
//		inflateGraph();
	}
	
	Task<Void> task = new Task<Void>() {
	    @Override public Void call() {
	        setUpLineChart();
//	            updateProgress(i, max);
	        return null;
	    }
	};
	
	private void setUpLineChart() {
		System.out.println("Setting up Graph");
		/**
		 * Create Axis
		 */
		final NumberAxis xAxis = new NumberAxis();
	    final NumberAxis yAxis = new NumberAxis();
	    xAxis.setLabel(X_AXIS);
	    yAxis.setLabel(Y_AXIS);
	    
	    /**
	     * Create chart
	     */
	    lineChart = new LineChart<Number, Number>(xAxis, yAxis);
	    lineChart.setCreateSymbols(false);
	    lineChart.setTitle(TITLE);
	    lineChart.getStyleClass().add("thick-chart");
	    graphAnchorPane.getChildren().add(lineChart);
	    
		List<List<Double>> graphs = mainC.getStatsCalc().getGraphs();
		int i = 0;
		for (List<Double> graph:graphs) {
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		    series.setName(graphsTitleArray[i]);
		    int j = 0;
			for(Double value:graph) {
				j += 1;
				if (j % 5 == 0) continue;
				series.getData().add(new XYChart.Data<Number, Number>(j, value));
				
			}
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					lineChart.getData().add(series);
				}
			});
			i += 1;
			System.out.println("Graph " + i + " done!");
		}
	    
	    //Simple data test
//		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
//	    series.setName("Schmosby");
//	    //populating the series with data
//	    series.getData().add(new XYChart.Data<Number, Number>(1, 23));
//	    series.getData().add(new XYChart.Data<Number, Number>(2, 80));
//	    series.getData().add(new XYChart.Data<Number, Number>(3, 75));
//	    series.getData().add(new XYChart.Data<Number, Number>(4, 54));
//	    series.getData().add(new XYChart.Data<Number, Number>(5, 94));
//	    series.getData().add(new XYChart.Data<Number, Number>(6, 106));
//	    series.getData().add(new XYChart.Data<Number, Number>(7, 122));
//	    series.getData().add(new XYChart.Data<Number, Number>(8, 145));
//	    series.getData().add(new XYChart.Data<Number, Number>(9, 143));
//	    series.getData().add(new XYChart.Data<Number, Number>(10, 1000));
//	    series.getData().add(new XYChart.Data<Number, Number>(11, 129));
//	    series.getData().add(new XYChart.Data<Number, Number>(12, 125));
//	    lineChart.getData().add(series);
	    

	    System.out.println("Setting up Graph done!");
	}


	public void inflateGraph() {
		List<List<Double>> graphs = mainC.getStatsCalc().getGraphs();
		for (List<Double> d:graphs) {
			System.out.println("New List: ");
			for(Double d2:d) {
				System.out.println(d2);
			}
			System.out.println("List end.");
		}
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_TABLEGRAPH_ID, null)); 
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
//		this.msg = filters.getDateRangeMessage();
//		if (this.msg == null) return;
	}
}
