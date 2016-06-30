package bettingManager.gui;

import java.util.List;
import java.util.Observable;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class TableGraphController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TABLEGRAPH_ID = 1137;
	
	public final String TITLE = "Graphs";

	public final String BLOGABET = "Blogabet";
	public final String BETADVISOR = "BetAdvisor";
	public final String TOTAL = "Total";
	public final String X_AXIS = "Bets";
	public final String Y_AXIS = "Profit";
	
	public final String[] graphsTitleArray = {BETADVISOR, BLOGABET, TOTAL};
	
	LineChart<Number, Number> lineChart;
	@FXML AnchorPane graphAnchorPane;
	@FXML ProgressIndicator progress;
	
	private Task<Void> task;
	/**
	 * Initialize
	 */
	public void init(MainController mainC) {
		this.mainC = mainC;
//		setUpLineChart();
		createTask();
		new Thread(task).start();
//		inflateGraph();
	}
	
	private void createTask() {
		task = new Task<Void>() {
		    @Override public Void call() {
		        setUpLineChart();
//		            updateProgress(i, max);
		        return null;
		    }
		};
	}
	
	 
	
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
	    System.out.println("Creating line chart");
//	    graphAnchorPane.getChildren().clear();
	    if (graphAnchorPane.getChildren().size() == 1) {
	        lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		    lineChart.setCreateSymbols(false);
		    lineChart.setTitle(TITLE);
		    lineChart.getStyleClass().add("thick-chart");
	    	graphAnchorPane.getChildren().add(lineChart);
	    } else {
	    		Platform.runLater(new Runnable() {
				@Override
				public void run() {
					lineChart.getData().clear();
				}
			});
	    	System.out.println("Clear");
	    }
	    System.out.println("Creating line chart done!");
		List<List<Double>> graphs = mainC.getStatsCalc().getGraphs();
		int i = 0;
		System.out.println("Inflating Graph data..");
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				progress.setProgress(-1);
			}
		});
		for (List<Double> graph:graphs) {
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		    series.setName(graphsTitleArray[i]);
		    int j = 0;
			for(Double value:graph) {
				j += 1;
				if (j % 5 == 0) continue;
				final XYChart.Data<Number, Number> data = new XYChart.Data<Number, Number>(j, value);
//				data.setNode(new HoveredThresholdNode(value));
				series.getData().add(data);
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
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				progress.setProgress(1);
			}
		});
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

	/** a node which displays a value on hover, but is otherwise empty */
	  class HoveredThresholdNode extends StackPane {
	    HoveredThresholdNode(double value) {
	      setPrefSize(1, 1);

	      final Label label = createDataThresholdLabel(value);

	      setOnMouseEntered(new EventHandler<MouseEvent>() {
	        @Override public void handle(MouseEvent mouseEvent) {
	          getChildren().setAll(label);
	          setCursor(Cursor.NONE);
	          toFront();
	        }
	      });
	      setOnMouseExited(new EventHandler<MouseEvent>() {
	        @Override public void handle(MouseEvent mouseEvent) {
	          getChildren().clear();
	          setCursor(Cursor.CROSSHAIR);
	        }
	      });
	    }

	    private Label createDataThresholdLabel(double value) {
	      final Label label = new Label(String.format("%.2f", value));
	      label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
	      label.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

	       label.setTextFill(Color.FORESTGREEN);

	      label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
	      return label;
	    }
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
