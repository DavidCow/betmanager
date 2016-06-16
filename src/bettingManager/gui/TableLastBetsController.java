package bettingManager.gui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

import bettingManager.statsCalculation.BettingManagerBet;
import bettingManager.statsCalculation.BettingManagerBetComparator;
import bettingManager.statsCalculation.StatsRow;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class TableLastBetsController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TABLELASTBETS_ID = 1199;
	
	/**
	 * Column name definitions
	 */
	public final static String BETDATE = "Date";
	public final static String GAMEDATE = "Game Date";
	public final static String KOB = "Kind of Bet";
	public final static String TIPSTER = "Tipster";
	public final static String EVENT = "Event";
	public final static String SELECTION = "Selection";
	public final static String NETWON = "Net won";
	public final static String ODDS = "Odds";
	
	/**
	 * Variable names for Table injection
	 */
	public final static String[] lastBetsTableValueNames = {
			"betDate", 
			"gameDate",
			"koB",
			"tipster",
			"event",
			"selection",
			"netWon",
			"odds" };
	
	/**
	 * Title of column (string)
	 */
	public final static String[] TABLE_TITLES_LASTBETS = {
			BETDATE,
			GAMEDATE,
			KOB,
			TIPSTER,
			EVENT,
			SELECTION,
			NETWON,
			ODDS };
	
	
	@FXML TableView<BettingManagerBet> table;
	ObservableList<BettingManagerBet> data;
	
	/**
	 * Initialize
	 */
	public void init(MainController mainC) {
		this.mainC = mainC;
		data = FXCollections.observableList(new ArrayList<BettingManagerBet>());
		inflateTable(TABLE_TITLES_LASTBETS);

		table.getSelectionModel().setSelectionMode(
		    SelectionMode.MULTIPLE
		);
	}
	
	/**
	 * Populate Table with Column Headers
	 * @param tableTitles
	 */
	private void inflateTable(String [] tableTitles) {
		System.out.println("Inflating Last Bets table...");
		
		for(int i = 0; i<tableTitles.length; i+=1) {
			TableColumn<BettingManagerBet, Object> newTC = new TableColumn<BettingManagerBet, Object>(tableTitles[i]);
//			 newTC.setCellFactory(EditingDoubleCell.<BettingManagerBet, Object>forTableColumn(new StringConverter<Object>() {
//			        private final NumberFormat nf = NumberFormat.getNumberInstance();
//
//			        {
//			             nf.setMaximumFractionDigits(2);
//			             nf.setMinimumFractionDigits(2);
//			        }
//
//			        
//			        
//			        @Override public String fromString(final String s) {
//			            // Don't need this, unless table is editable, see DoubleStringConverter if needed
//			            return null; 
//			        }
//
//					@Override
//					public String toString(Object value) {
//						if (value instanceof String) {
//							return (String) value;
//						} else if (value instanceof Date) {
//								return ((Date) value).toString();
//			        	} else if (value instanceof Double) {
//			        		return ((Double) value).toString();
////			        		return nf.format(Double.parseDouble((String) value));
//			        	}
//						return "NOT WORKING";
//					}
//			    }));
			
//			 newTC.setCellFactory(new Callback<TableColumn<BettingManagerBet, Object>, TableCell<BettingManagerBet, Object>>() {
//			        @Override
//			        public TableCell call(TableColumn p) {
//			            return new TableCell<BettingManagerBet, Object>() {
//			                @Override
//			                public void updateItem(Object item,boolean empty) {
//			                    super.updateItem(item, empty);
//			                    if (item != null) {
//			                        if (item instanceof String) {
//			                        	setText((String) item);
//			                        } else if (item instanceof Date) {
//										setText(((Date) item).toString());
//						        	} else if (item instanceof Double) {
//						        		setText(((Double) item).toString());
////						        		return nf.format(Double.parseDouble((String) value));
//						        	}
//			                        setAlignment(Pos.CENTER);
//			                        this.setTextFill(Color.BLUE);
//			                        System.out.println("updateItem()");
//
//			                    } 
//			                }
//			            };
//
//
//			        }
//			    });
			 
			newTC.setCellFactory(col -> new CustomTableCell());
			
			newTC.setCellValueFactory(new PropertyValueFactory<BettingManagerBet, Object>(lastBetsTableValueNames[i]));
			table.getColumns().add(newTC);
		}
		
		
		/**
		 * TEST TO FILL IN TABLES WITH DATA
		 */
//		List<BettingManagerBet> rows = this.mainC.getStatsCalc().getMonthlyStats();
			
//		data = FXCollections.observableList(rows);
		data.sort(new BettingManagerBetComparator());
		table.getItems().setAll(data);
//		table.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<BettingManagerBet>() {
//
//			@Override
//			public void onChanged(ListChangeListener.Change<? extends BettingManagerBet> c) {
//			     for(BettingManagerBet t : c.getList())
//		                System.out.println(t);
//			}
//		});
		/**
		 * TEST TO FILL IN TABLES WITH DATA
		 */
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_TABLELASTBETS_ID, null)); 
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
//		this.msg = filters.getDateRangeMessage();
//		if (this.msg == null) return;
	}
	
	public void setDataList(ObservableList<? extends StatsRow> list) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				List<BettingManagerBet> bets = new ArrayList<BettingManagerBet>();
				for(StatsRow statsRow:list) {
					for (BettingManagerBet bet:statsRow.getBets()) {
						bets.add(bet);
					}
				}
				table.getColumns().clear();
				data = FXCollections.observableList(bets);
				inflateTable(TABLE_TITLES_LASTBETS);
			}
		});
		
	}
	
	public static class CustomTableCell extends TableCell<BettingManagerBet, Object>{

	    private TextField textField;

	    private DecimalFormat df ;
	    private final NumberFormat nf = NumberFormat.getNumberInstance();

        {
             nf.setMaximumFractionDigits(2);
             nf.setMinimumFractionDigits(2);
        }
        
	    public CustomTableCell() {
	        Locale locale  = new Locale("en", "UK");
	        String pattern = "###,###.##";
	        df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
	        df.applyPattern(pattern);
	        createTextField();
	    }

	    @Override
	    public void updateItem(Object item, boolean empty) {
	        super.updateItem(item, empty);

	        int columnNo = getTableView().getColumns().indexOf(getTableColumn());
	        if (empty) {
	            setText("null 1");
	        } else {
	        	if (item instanceof String) {
                	setText((String) item);
                	if (((String) item).isEmpty()) {
                		setText("Empty String");
                	}
                } else if (item instanceof Date) {
					setText(((Date) item).toString());
	        	} else if (item instanceof Double) {
	        		setText(((Double) item).toString());
	        	}
                if (columnNo == 6) {
//                	setTextFill(Color.WHITE);
                	double netWon = 0;
                	try {
                		netWon = Double.parseDouble(getText());
                		if (netWon < 0) {
                			setStyle("-fx-background-color:#F64D54");		//RED
                		} else if (netWon > 0){ 
                			setStyle("-fx-background-color:#8CDD81");		//GREEN
                		}
                	} catch (NumberFormatException e) {
                		
                	}
                	setText(nf.format(netWon));
                }
                setAlignment(Pos.CENTER);
                System.out.println("updateItem() ");
	        }
	    }

	    private String getString() {
	        return getItem() == null ? "null 2" : (String) getItem();
	    }

	    private void createTextField(){
	        textField = new TextField();
	        textField.setText( getString() );
	        StringConverter<Object> converter = new StringConverter<Object>() {
		        private final NumberFormat nf = NumberFormat.getNumberInstance();

		        {
		             nf.setMaximumFractionDigits(2);
		             nf.setMinimumFractionDigits(2);
		        }

		        
		        
		        @Override public String fromString(final String s) {
		            // Don't need this, unless table is editable, see DoubleStringConverter if needed
		            return null; 
		        }

				@Override
				public String toString(Object value) {
					if (value instanceof String) {
						return (String) value;
					} else if (value instanceof Date) {
							return ((Date) value).toString();
		        	} else if (value instanceof Double) {
//		        		return ((Double) value).toString();
		        		return nf.format(((Double) value));
		        	}
					return "NOT WORKING";
				}
	        };
	        
	        TextFormatter textFormatter = new TextFormatter<>(converter,  0.0, c ->
	        {
	            if (c.getControlNewText().isEmpty()) {
	                return c;
	            }
	            ParsePosition parsePosition = new ParsePosition( 0 );
	            Object object = df.parse( c.getControlNewText(), parsePosition );

	            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
	            {
	                return null;
	            }
	            else
	            {
	                return c;
	            }
	        } ) ;

	        // add filter to allow for typing only integer
	        textField.setTextFormatter( textFormatter);
//	        textField.setMinWidth( this.getWidth() - this.getGraphicTextGap() * 2 );

	        // commit on Enter
//	        textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
//	            commitEdit(newValue);
//	        });
	    }
	}
	
}
